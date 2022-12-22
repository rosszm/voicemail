package dev.zacharyross.voicemail.ui.common.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import java.util.*


object PhoneUtils {
    /**
     * Provides a visual transformation for a raw phone number based on the default locale. For example,
     * the raw phone number, `"2345678901"` may be transformed to `"(234) 567-8901"`
     *
     * @param rawPhoneNumber A phone number in raw format, i.e., `"12345678901"`
     */
    fun transformPhoneNumberText(rawPhoneNumber: AnnotatedString): TransformedText {
        val formatter = PhoneNumberUtil.getInstance().getAsYouTypeFormatter(Locale.getDefault().country)
        val formatted = AnnotatedString.Builder().run {
            var string = ""
            rawPhoneNumber.forEach { string = formatter.inputDigit(it) }
            append(string)
            toAnnotatedString()
        }
        val phoneNumberOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var formattedIndex = offset
                var digitIndex = -1
                formatted.forEach {
                    if (digitIndex >= offset)
                        return@forEach
                    if (it.isDigit())
                        digitIndex += 1
                    else
                        formattedIndex += 1
                }
                return formattedIndex
            }
            override fun transformedToOriginal(offset: Int): Int {
                var digitIndex = 0
                formatted.subSequence(0 until offset)
                    .forEach {
                        if (it.isDigit())
                            digitIndex += 1
                    }
                return digitIndex
            }
        }
        return TransformedText(formatted, phoneNumberOffsetMapping)
    }

    fun getExampleNumber(): String {
        val phoneUtil = PhoneNumberUtil.getInstance()
        val region = Locale.getDefault().country
        val example = phoneUtil.getExampleNumber(region)
        return phoneUtil.formatInOriginalFormat(example, region)
    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val phoneUtil = PhoneNumberUtil.getInstance()
        return try {
            val number = phoneUtil.parse(phoneNumber, Locale.getDefault().country)
            phoneUtil.isValidNumber(number)
        }
        catch (e: NumberParseException) {
            false
        }
    }
}

