package com.itsaky.devoptions.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.itsaky.devoptions.DevOption
import com.itsaky.devoptions.net.wadbConnectionAddress
import com.itsaky.devoptions.toggle.readToggle
import com.itsaky.devoptions.toggle.writeToggle

@Composable
fun ToggleScreen() {
    val context = LocalContext.current
    val resolver = context.contentResolver

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Dev Options Shortcuts",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(16.dp))

        for (option in DevOption.entries) {
            if (option.minSdk > Build.VERSION.SDK_INT) continue

            var enabled by remember { mutableStateOf(readToggle(resolver, option)) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label(),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (option == DevOption.WirelessDebugging && enabled) {
                        val address = wadbConnectionAddress(resolver)
                        Text(
                            text = address ?: "No network",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Switch(
                    checked = enabled,
                    onCheckedChange = { newValue ->
                        writeToggle(resolver, option, newValue)
                        enabled = newValue
                    },
                )
            }
        }
    }
}

private fun DevOption.label(): String = when (this) {
    DevOption.DeveloperOptions -> "Developer Options"
    DevOption.UsbDebugging -> "USB Debugging"
    DevOption.WirelessDebugging -> "Wireless Debugging"
}
