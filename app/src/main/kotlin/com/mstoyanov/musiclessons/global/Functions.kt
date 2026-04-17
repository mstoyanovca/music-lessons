package com.mstoyanov.musiclessons.global

import android.text.Editable
import com.google.i18n.phonenumbers.AsYouTypeFormatter
import com.google.i18n.phonenumbers.PhoneNumberUtil
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
}
