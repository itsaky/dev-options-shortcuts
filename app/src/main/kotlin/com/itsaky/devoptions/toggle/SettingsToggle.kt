package com.itsaky.devoptions.toggle

import android.content.ContentResolver
import android.provider.Settings
import com.itsaky.devoptions.DevOption

fun readToggle(resolver: ContentResolver, option: DevOption): Boolean =
    Settings.Global.getInt(resolver, option.key, 0) == 1

fun writeToggle(resolver: ContentResolver, option: DevOption, enabled: Boolean): Boolean =
    Settings.Global.putInt(resolver, option.key, if (enabled) 1 else 0)
