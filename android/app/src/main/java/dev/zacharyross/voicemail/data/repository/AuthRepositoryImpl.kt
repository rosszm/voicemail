package dev.zacharyross.voicemail.data.repository

import android.telephony.PhoneNumberUtils
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.zacharyross.voicemail.domain.model.SignInRequest
import dev.zacharyross.voicemail.domain.model.VerifyPhoneRequest
import dev.zacharyross.voicemail.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val authSource: FirebaseAuth
): AuthRepository {
    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    override fun getUser() = authSource.currentUser

    override fun verifyPhoneNumber(request: VerifyPhoneRequest) {
        val callbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                request.onAutoVerify(credential.smsCode!!)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                request.onFailure(e)
            }
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                verificationId = id
                resendToken = token
                request.onSuccess(Unit)
            }
        }
        val numberE164 = PhoneNumberUtils
            .formatNumberToE164(request.phoneNumber, Locale.getDefault().country)

        val options = PhoneAuthOptions.newBuilder(authSource)
            .setPhoneNumber(numberE164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(request.activity)
            .setCallbacks(callbacks)

        if (request.resend)
            options.setForceResendingToken(resendToken!!)

        PhoneAuthProvider.verifyPhoneNumber(options.build())
    }

    override fun signInWithCode(request: SignInRequest) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, request.code)
        authSource.signInWithCredential(credential)
            .addOnSuccessListener { request.onSuccess(it.user!!) }
            .addOnFailureListener { request.onFailure(it) }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }

    override fun getAuthStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser)
        }
        authSource.addAuthStateListener(listener)

        awaitClose { authSource.removeAuthStateListener(listener) }
    }
}

