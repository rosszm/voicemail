package dev.zacharyross.voicemail.domain.model

import com.google.firebase.auth.FirebaseUser


/**
 * A request to sign in a user with a verification code.
 *
 * @param code the verification code to sign in with.
 */
data class SignInRequest(
    val code: String,
    override val onSuccess: (user: FirebaseUser) -> Unit = {},
    override val onFailure: (exception: Exception) -> Unit = {},
): Request<FirebaseUser, Exception>()
