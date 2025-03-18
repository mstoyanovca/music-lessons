package com.mstoyanov.musiclessons.global

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import com.google.i18n.phonenumbers.AsYouTypeFormatter
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.io.Serializable
import java.time.format.DateTimeFormatter

object Functions {
    // do not change to "HH:mm", it fails on 9:00 etc.:
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("H:mm")!!
    private val asYouTypeFormatter: AsYouTypeFormatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter("US")

    fun formatPhoneNumber(s: Editable): String {
        val builder: StringBuilder = StringBuilder()
        for (c in s.toString()) if (c.isDigit()) builder.append(c)
        val digits = builder.toString()
        val trimmed = if (digits.length >= 10) digits.substring(0..9) else digits
        var formattedPhoneNumber = ""
        asYouTypeFormatter.clear()
        for (c in trimmed.toCharArray()) {
            formattedPhoneNumber = asYouTypeFormatter.inputDigit(c)
        }
        return formattedPhoneNumber
    }

    inline fun <reified T : Serializable> Bundle.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializable(key) as? T
    }

    inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
}
