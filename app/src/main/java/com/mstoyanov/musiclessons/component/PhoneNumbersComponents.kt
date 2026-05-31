package com.mstoyanov.musiclessons.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mstoyanov.musiclessons.entity.NanpVisualTransformation
import com.mstoyanov.musiclessons.entity.PhoneNumber
import com.mstoyanov.musiclessons.entity.PhoneNumberType

@Composable
fun PhoneNumbers(phoneNumbers: List<PhoneNumber>) {
    phoneNumbers.forEach { phoneNumber ->
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(4.dp))
            val formattedPhoneNumber = NanpVisualTransformation()
                .filter(AnnotatedString(phoneNumber.number))
                .text
            Text(
                text = "$formattedPhoneNumber ${phoneNumber.type.displayValue()}",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(24.dp))
            if (phoneNumber.type == PhoneNumberType.CELL) {
                Icon(
                    imageVector = Icons.Default.Sms,
                    contentDescription = null,
                    tint = Color.Blue
                )
            }
        }
    }
}
