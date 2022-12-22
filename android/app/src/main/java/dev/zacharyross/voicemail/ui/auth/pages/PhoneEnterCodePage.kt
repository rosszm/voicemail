package dev.zacharyross.voicemail.ui.auth.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import dev.zacharyross.voicemail.R
import dev.zacharyross.voicemail.ui.auth.AuthEvent
import dev.zacharyross.voicemail.ui.auth.AuthEventType
import dev.zacharyross.voicemail.ui.common.util.AuthUtils
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest


@Composable
fun PhoneEnterCodePage(
    authEventFlow: StateFlow<AuthEvent?>,
    verifyCode: (String) -> Unit,
) {
    val notDigitRegex = remember { Regex("([^0-9])+") }
    var code by remember { mutableStateOf("") }
    var isSigningIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authEventFlow.collectLatest {
            if (it != null) {
                when(it.type) {
                    AuthEventType.VERIFY_COMPLETE -> code = it.value!!
                    AuthEventType.SIGN_IN_REQUESTED -> isSigningIn = true
                    else -> {}
                }
            }
        }
    }

    if (isSigningIn)
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    else
        AuthPage(
            title = stringResource(R.string.verify_number_title),
            illustrationId = R.drawable.illustration_complete,
            buttonText = stringResource(R.string.verify_code),
            buttonOnClick = {
                verifyCode(code)
                isSigningIn = true
            },
            buttonEnabled = code.length == 6
        ) {
            VerificationCodeField(
                value = code,
                onValueChange = { code = it.replace(notDigitRegex, "").take(6) }
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificationCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    OutlinedTextField(
        label = { Text(text = stringResource(R.string.verification_code)) },
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(text = "2 3 4 9 8 7") },
        visualTransformation = { AuthUtils.transformVerificationCodeText(it) },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
    )
}




