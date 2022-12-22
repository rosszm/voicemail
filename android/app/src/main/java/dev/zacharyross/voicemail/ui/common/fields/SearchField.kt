package dev.zacharyross.voicemail.ui.common.fields

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import dev.zacharyross.voicemail.ui.common.SearchIcon


data class SearchFieldDisabledIcons(
    val leadingIcon: @Composable () -> Unit = {},
    val trailingIcon: @Composable () -> Unit = {},
)

/**
 * Search text field.
 *
 * Search fields
 */
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (KeyboardActionScope) -> Unit,
    placeholder: String,
    enabled: Boolean = true,
    actionIcon: @Composable () -> Unit = {},
    disabledIcons: SearchFieldDisabledIcons = SearchFieldDisabledIcons(),
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyLarge
            .copy(color = if (value.isNotBlank())
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurfaceVariant
            ),
        visualTransformation = { transformSearchFieldText(it, placeholder = placeholder) },
        cursorBrush = Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primary)
        ),
        keyboardOptions = KeyboardOptions(
            autoCorrect = true,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = onSearch),
        singleLine = true,
    ) { innerTextField ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (enabled) disabledIcons.leadingIcon() else SearchIcon()
                innerTextField()
            }
            Crossfade(enabled) {
                if (it) actionIcon()
                else disabledIcons.trailingIcon()
            }
        }
    }
}

fun transformSearchFieldText(text: AnnotatedString, placeholder: String): TransformedText {
    val formatted = AnnotatedString.Builder().run {
        append(text.ifBlank { placeholder })
        toAnnotatedString()
    }
    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = offset
        override fun transformedToOriginal(offset: Int): Int = if (text.isBlank()) 0 else offset
    }
    return TransformedText(formatted, offsetMapping)
}