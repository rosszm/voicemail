package dev.zacharyross.voicemail.ui.auth.pages

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.common.fields.PhoneField
import dev.zacharyross.voicemail.ui.common.util.PhoneUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@Composable
fun PhoneSendCodePage(
    phoneNumberStateFlow: MutableStateFlow<String>,
    focusManager: FocusManager,
    navigateNext: () -> Unit,
    sendCode: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val notDigitRegex = remember { Regex("([^0-9])+") }
    var phoneNumber by remember { mutableStateOf("") }
    var isFieldError by remember { mutableStateOf(false) }
    val isValidPhoneNumber by remember { derivedStateOf {
        PhoneUtils.isValidPhoneNumber(phoneNumber)
    } }

    LaunchedEffect(Unit) {
        phoneNumberStateFlow.collectLatest { phoneNumber = it }
    }

    LaunchedEffect(phoneNumber) {
        isFieldError = false
        delay(1000)
        if (phoneNumber.isNotBlank() && !isValidPhoneNumber) isFieldError = true
    }

    AuthPage(
        title = stringResource(R.string.login_with_phone),
        illustrationId = R.drawable.illustration_login,
        buttonText = stringResource(R.string.send_code),
        buttonEnabled = isValidPhoneNumber,
        buttonOnClick = {
            sendCode()
            coroutineScope.launch {
                delay(750)
                navigateNext()
            }
        },
    ) {
        PhoneField(
            value = phoneNumber,
            onValueChange = { value ->
                phoneNumberStateFlow.update {
                    value.replace(notDigitRegex, "")
                }
            },
            isError = isFieldError,
            supportingText = stringResource(R.string.msg_data_usage_warning),
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus(true) })
        )
    }
}

