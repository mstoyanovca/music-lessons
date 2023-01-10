package com.mstoyanov.musiclessons.global

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable
import java.time.format.DateTimeFormatter

object Functions {
    // do not change to "HH:mm", it fails on 9:00 etc.:
    val formatter = DateTimeFormatter.ofPattern("H:mm")

    inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
}
