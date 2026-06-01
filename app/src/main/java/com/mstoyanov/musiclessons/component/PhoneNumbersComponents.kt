package com.mstoyanov.musiclessons.component

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mstoyanov.musiclessons.entity.NanpVisualTransformation
import com.mstoyanov.musiclessons.entity.PhoneNumber
import com.mstoyanov.musiclessons.entity.PhoneNumberType

@Composable
fun PhoneNumbers(phoneNumbers: List<PhoneNumber>) {
    val context = LocalContext.current

    phoneNumbers.forEach { phoneNumber ->
        val formattedPhoneNumber = NanpVisualTransformation()
            .filter(AnnotatedString(phoneNumber.number))
            .text
        val annotatedString = buildAnnotatedString {
            withLink(
                link = LinkAnnotation.Clickable(
                    tag = "phone number",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = Color.Blue,
                            // textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:${phoneNumber.number}".toUri()
                        }
                        context.startActivity(intent)
                    }
                )
            ) {
                append("$formattedPhoneNumber")
            }
            append(" ${phoneNumber.type.displayValue()}")
        }

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
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(24.dp))
            if (phoneNumber.type == PhoneNumberType.CELL) {
                IconButton(onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "smsto:${phoneNumber.number}".toUri()
                    }
                    context.startActivity(intent)
                }) {
                    Icon(
                        imageVector = Icons.Default.Sms,
                        contentDescription = null,
                        tint = Color.Blue
                    )
                }
            }
        }
    }
}
