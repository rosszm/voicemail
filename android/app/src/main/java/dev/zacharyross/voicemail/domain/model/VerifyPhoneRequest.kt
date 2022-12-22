package dev.zacharyross.voicemail.domain.model

import android.app.Activity

/**
 * A request to verify a phone number.
 *
 * @param phoneNumber the phone number to verify.
 * @param activity the activity in which the verification is occurring.
 * @param resend whether or not this request is a resend request.
 * @param onAutoVerify a function that is called when the SMS code has been automatically verified.
 */
data class VerifyPhoneRequest(
    val phoneNumber: String,
    val activity: Activity,
    val resend: Boolean = false,
    val onAutoVerify: (smsCode: String) -> Unit = {},
    override val onSuccess: (Unit) -> Unit = {},
    override val onFailure: (exception: Exception) -> Unit = {},
): Request<Unit, Exception>()