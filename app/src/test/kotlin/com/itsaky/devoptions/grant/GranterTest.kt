package com.itsaky.devoptions.grant

import android.os.DeadObjectException
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GranterTest {

    @Test
    fun adbGrantCommandContainsPackageAndPermission() {
        val cmd = adbGrantCommand("com.itsaky.devoptions")
        assertEquals(
            "adb shell pm grant com.itsaky.devoptions android.permission.WRITE_SECURE_SETTINGS",
            cmd,
        )
    }

    @Test
    fun rootGranterBuildsCorrectSuCommand() {
        var captured: Array<String>? = null
        val granter = RootGranter { args ->
            captured = args
            fakeProcess(exitCode = 0)
        }
        runBlocking { granter.grant(RuntimeEnvironment.getApplication()) }
        assertArrayEquals(
            arrayOf("su", "-c", "pm grant com.itsaky.devoptions android.permission.WRITE_SECURE_SETTINGS"),
            captured,
        )
    }

    @Test
    fun rootGranterReturnsSuccessOnExitCodeZero() {
        val granter = RootGranter { fakeProcess(exitCode = 0) }
        val result = runBlocking { granter.grant(RuntimeEnvironment.getApplication()) }
        assertTrue(result.isSuccess)
    }

    @Test
    fun rootGranterReturnsFailureWhenSuNotAvailable() {
        val granter = RootGranter { throw IOException("su not found") }
        val result = runBlocking { granter.grant(RuntimeEnvironment.getApplication()) }
        assertTrue(result.isFailure)
    }

    @Test
    fun shizukuGranterReturnsFailureWhenBinderDies() {
        val granter = ShizukuGranter(
            binderAlive = { true },
            grantPermission = { _, _ -> throw DeadObjectException() },
        )
        val result = runBlocking { granter.grant(RuntimeEnvironment.getApplication()) }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is DeadObjectException)
    }

    @Test
    fun shizukuGranterReturnsFailureWhenBinderNotAlive() {
        val granter = ShizukuGranter(
            binderAlive = { false },
            grantPermission = { _, _ -> },
        )
        val result = runBlocking { granter.grant(RuntimeEnvironment.getApplication()) }
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }
}

private fun fakeProcess(exitCode: Int): Process {
    return object : Process() {
        override fun getOutputStream() = ByteArrayOutputStream()
        override fun getInputStream() = ByteArrayInputStream(ByteArray(0))
        override fun getErrorStream() = ByteArrayInputStream(ByteArray(0))
        override fun waitFor() = exitCode
        override fun exitValue() = exitCode
        override fun destroy() {}
    }
}
