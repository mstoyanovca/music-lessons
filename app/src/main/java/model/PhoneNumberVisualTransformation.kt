package model

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(10)
        var out = ""
        for (i in trimmed.indices) {
            if (i == 0) out += "("
            if (i == 3) out += ") "
            if (i == 6) out += "-"
            out += trimmed[i]
        }

        return TransformedText(androidx.compose.ui.text.AnnotatedString(out), OffsetMapping.Companion.Identity)
    }
}