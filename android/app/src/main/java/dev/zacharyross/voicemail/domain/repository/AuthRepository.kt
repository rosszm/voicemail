package dev.zacharyross.voicemail.domain.repository

import com.google.firebase.auth.FirebaseUser
import dev.zacharyross.voicemail.domain.model.SignInRequest
import dev.zacharyross.voicemail.domain.model.VerifyPhoneRequest
import kotlinx.coroutines.flow.Flow


interface AuthRepository {
    /**
     * Returns the currently signed in user. If no user is signed in, returns `null`
     */
    fun getUser(): FirebaseUser?

    /**
     * Starts the phone verification process with a given request.
     */
    fun verifyPhoneNumber(request: VerifyPhoneRequest)

    /**
     * Attempts to sign in a user from a given request.
     */
    fun signInWithCode(request: SignInRequest)

    /**
     * Attempts to sign out a user.
     */
    fun signOut()

    /**
     * Returns a flow of the current authentication state.
     */
    fun getAuthStateFlow(): Flow<FirebaseUser?>
}