package dev.zacharyross.voicemail.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zacharyross.voicemail.domain.model.SignInRequest
import dev.zacharyross.voicemail.domain.model.VerifyPhoneRequest
import dev.zacharyross.voicemail.domain.usecase.auth.MonitorAuthUseCase
import dev.zacharyross.voicemail.domain.usecase.auth.SignInWithCodeUseCase
import dev.zacharyross.voicemail.domain.usecase.auth.VerifyPhoneNumberUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val verifyPhoneNumberUseCase: VerifyPhoneNumberUseCase,
    private val signInWithCodeUseCase: SignInWithCodeUseCase,
    private val monitorAuthUseCase: MonitorAuthUseCase
): ViewModel() {
    val phoneNumberStateFlow = MutableStateFlow("")

    private val _authEventFlow: MutableStateFlow<AuthEvent?> = MutableStateFlow(null)
    val authEventFlow = _authEventFlow.asStateFlow()

    var authStateFlow: StateFlow<FirebaseUser?>? = null

    init {
        viewModelScope.launch {
            authStateFlow = monitorAuthUseCase.invoke(Unit).getOrNull()?.stateIn(viewModelScope)
        }
    }

    private val autoSignInDelay = 500L

    /**
     * Starts the phone verification process.
     *
     * @param activity the activity with which app verification will be completed.
     */
    fun verifyPhoneNumber(activity: Activity) = startPhoneVerification(activity)

    /**
     * Restarts the verification process by resending an SMS verification code.
     *
     * @param activity the activity with which app verification will be completed.
     */
    fun resendSmsVerificationCode(activity: Activity) = startPhoneVerification(activity, resend = true)

    /**
     * Authenticates the user with an SMS verification code.
     *
     * @param code an SMS verification code
     */
    fun signInWithSmsVerificationCode(code: String) {
        _authEventFlow.value = AuthEvent(AuthEventType.SIGN_IN_REQUESTED)
        val request = SignInRequest(
            code = code,
            onSuccess = { _authEventFlow.value = AuthEvent(AuthEventType.SIGN_IN_COMPLETE) },
            onFailure = { _authEventFlow.value = AuthEvent(AuthEventType.ERROR, it.message) }
        )
        viewModelScope.launch { signInWithCodeUseCase.invoke(request) }
    }

    /**
     * Starts a phone verification process.
     *
     * @param activity the activity with which app verification will be completed.
     * @param resend if the verification request is a resend request.
     */
    private fun startPhoneVerification(activity: Activity, resend: Boolean = false) {
        _authEventFlow.value = AuthEvent(AuthEventType.VERIFY_REQUESTED)
        val request = VerifyPhoneRequest(
            phoneNumber = phoneNumberStateFlow.value,
            activity = activity,
            resend = resend,
            onAutoVerify = {
                _authEventFlow.value = AuthEvent(AuthEventType.VERIFY_COMPLETE, it)
                viewModelScope.launch {
                    delay(autoSignInDelay)
                    signInWithSmsVerificationCode(it)
                }
            },
            onFailure = { _authEventFlow.value = AuthEvent(AuthEventType.ERROR, it.message) }
        )
        viewModelScope.launch { verifyPhoneNumberUseCase.invoke(request) }
    }
}