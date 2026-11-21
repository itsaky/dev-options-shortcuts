package com.itsaky.devtoggles.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itsaky.devtoggles.R
import com.itsaky.devtoggles.grant.RootGranter
import com.itsaky.devtoggles.grant.ShizukuGranter
import com.itsaky.devtoggles.grant.adbGrantCommand
import kotlinx.coroutines.launch

private data class GrantStatus(val message: String, val success: Boolean)

@Composable
fun GrantScreen(contentPadding: PaddingValues, onGranted: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf<GrantStatus?>(null) }
    val adbCommand = adbGrantCommand(context.packageName)

    val copiedText = stringResource(R.string.copied)
    val grantedShizukuText = stringResource(R.string.granted_via_shizuku)
    val grantedRootText = stringResource(R.string.granted_via_root)
    val shizukuErrorFmt = stringResource(R.string.shizuku_error, "%s")
    val rootErrorFmt = stringResource(R.string.root_error, "%s")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.permission_required_title),
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.permission_required_body),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
        ) {
            Text(
                text = adbCommand,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("adb command", adbCommand))
                Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.copy_adb_command))
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                scope.launch {
                    val result = ShizukuGranter().grant(context)
                    status = if (result.isSuccess) {
                        onGranted()
                        GrantStatus(grantedShizukuText, success = true)
                    } else {
                        GrantStatus(
                            shizukuErrorFmt.format(result.exceptionOrNull()?.message),
                            success = false,
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.grant_via_shizuku))
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                scope.launch {
                    val result = RootGranter().grant(context)
                    status = if (result.isSuccess) {
                        onGranted()
                        GrantStatus(grantedRootText, success = true)
                    } else {
                        GrantStatus(
                            rootErrorFmt.format(result.exceptionOrNull()?.message),
                            success = false,
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.grant_via_root))
        }

        status?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                text = it.message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (it.success)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
            )
        }
    }
}
