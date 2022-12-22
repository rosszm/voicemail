package dev.zacharyross.voicemail.ui.auth


data class AuthEvent(
    val type: AuthEventType,
    val value: String? = null,
)

enum class AuthEventType {
    VERIFY_REQUESTED,
    VERIFY_COMPLETE,
    SIGN_IN_REQUESTED,
    SIGN_IN_COMPLETE,
    ERROR,
}
