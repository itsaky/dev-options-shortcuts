package com.itsaky.devtoggles.grant

import android.Manifest
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RootGranter(
    private val exec: (Array<String>) -> Process = { Runtime.getRuntime().exec(it) },
) : PermissionGranter {

    override suspend fun grant(context: Context): Result<Unit> = runCatching {
        val command = arrayOf(
            "su", "-c",
            "pm grant ${context.packageName} ${Manifest.permission.WRITE_SECURE_SETTINGS}",
        )
        val process = withContext(Dispatchers.IO) { exec(command) }
        val exitCode = withContext(Dispatchers.IO) { process.waitFor() }
        check(exitCode == 0) { "su exited with code $exitCode" }
    }
}
