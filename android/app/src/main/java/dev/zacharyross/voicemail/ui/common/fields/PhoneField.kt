package dev.zacharyross.voicemail.ui.common.fields

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.common.ErrorIcon
import dev.zacharyross.voicemail.ui.common.util.PhoneUtils

/**
 * Phone input field.
 *
 * Phone fields allow users to easily input phone numbers, allowing only numerical input and
 * displaying formatted number. This composable wrap a Material Design [OutlinedTextField].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String?,
    imeAction: ImeAction?,
    keyboardActions: KeyboardActions?,
) {
    OutlinedTextField(
        modifier = modifier.padding(vertical = 16.dp),
        label = { Text(text = stringResource(R.string.phone_number)) },
        value = value,
        onValueChange = onValueChange,
        visualTransformation = { PhoneUtils.transformPhoneNumberText(it) },
        placeholder = { Text(text = PhoneUtils.getExampleNumber()) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = imeAction ?: ImeAction.Default),
        keyboardActions = keyboardActions ?: KeyboardActions.Default,
        isError = isError,
        supportingText = {
            Text(text = if (!isError) supportingText ?: ""
                else stringResource(id = R.string.invalid_phone_number)) },
        trailingIcon = {
            if (isError) ErrorIcon()
        }
    )
}