package com.itsaky.devtoggles.grant

import android.Manifest
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class ShizukuGranter(
    private val binderAlive: () -> Boolean = { Shizuku.pingBinder() },
    private val grantPermission: (String, String) -> Unit = ::shizukuGrant,
) : PermissionGranter {

    override suspend fun grant(context: Context): Result<Unit> = runCatching {
        check(binderAlive()) { "Shizuku binder is not alive" }
        withContext(Dispatchers.IO) {
            grantPermission(
                context.packageName,
                Manifest.permission.WRITE_SECURE_SETTINGS,
            )
        }
    }
}

@Suppress("DEPRECATION")
private fun shizukuGrant(packageName: String, permission: String) {
    val binder = ShizukuBinderWrapper(
        SystemServiceHelper.getSystemService("package"),
    )
    val data = SystemServiceHelper.obtainParcel(
        "android.content.pm.IPackageManager",
        "grantRuntimePermission",
        "server",
    )
    data.writeString(packageName)
    data.writeString(permission)
    data.writeInt(0)
    val reply = android.os.Parcel.obtain()
    try {
        val code = SystemServiceHelper.getTransactionCode(
            "android.content.pm.IPackageManager",
            "grantRuntimePermission",
        )!!
        binder.transact(code, data, reply, 0)
        reply.readException()
    } finally {
        data.recycle()
        reply.recycle()
    }
}
