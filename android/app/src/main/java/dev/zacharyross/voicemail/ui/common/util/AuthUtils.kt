package dev.zacharyross.voicemail.ui.common.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import kotlin.math.max
import kotlin.math.min


object AuthUtils {
    /**
     * Provides a visual transformation for a verification code. For example, the code, `"123456"`
     * is transformed to `"1 2 3 4 5 6"`
     *
     * @param code A verification code on any length, i.e., `"123456"`
     */
    fun transformVerificationCodeText(code: AnnotatedString): TransformedText {
        val formatted = AnnotatedString.Builder().run {
            val string = code.toList().joinToString(" ")
            append(string)
            toAnnotatedString()
        }
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return if (offset < 2) offset else (offset - 1) * 2 + 1
            }
            override fun transformedToOriginal(offset: Int): Int {
                return if (offset < 2) offset else (offset - 1) / 2 + 1
            }
        }
        return TransformedText(formatted, offsetMapping)
    }
}