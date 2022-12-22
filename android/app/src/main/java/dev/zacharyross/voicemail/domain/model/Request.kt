package dev.zacharyross.voicemail.domain.model


/**
 * Represents a request.
 *
 * @param onSuccess a function that is called when request is successful.
 * @param onFailure a function that is called when request fails.
 */
open class Request<R, E>(
    open val onSuccess: (user: R) -> Unit = {},
    open val onFailure: (exception: E) -> Unit = {},
)
