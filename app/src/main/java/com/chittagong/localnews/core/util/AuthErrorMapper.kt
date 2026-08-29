package com.chittagong.localnews.core.util

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException

/**
 * Translates raw Firebase exceptions into copy we're happy to show a user.
 * Living in the data layer's blast radius means the UI never sees an SDK type.
 */
object AuthErrorMapper {

    fun toMessage(throwable: Throwable): String = when (throwable) {
        is FirebaseNetworkException ->
            "No internet connection. Check your network and try again."

        is FirebaseTooManyRequestsException ->
            "Too many attempts. Please wait a moment before trying again."

        is FirebaseAuthWeakPasswordException ->
            throwable.reason ?: "That password is too weak. Try a longer one."

        is FirebaseAuthUserCollisionException ->
            "An account with this email already exists. Try logging in."

        is FirebaseAuthInvalidUserException -> when (throwable.errorCode) {
            "ERROR_USER_DISABLED" -> "This account has been disabled. Contact support."
            else -> "No account found with this email."
        }

        is FirebaseAuthInvalidCredentialsException -> when (throwable.errorCode) {
            "ERROR_INVALID_EMAIL" -> "That email address looks malformed."
            else -> "Incorrect email or password."
        }

        is FirebaseAuthEmailException ->
            "We couldn't send that email. Please try again."

        is FirebaseAuthException -> when (throwable.errorCode) {
            "ERROR_INVALID_CREDENTIAL" -> "Incorrect email or password."
            "ERROR_OPERATION_NOT_ALLOWED" ->
                "Email/password sign-in is disabled for this project."

            else -> throwable.localizedMessage ?: "Authentication failed. Please try again."
        }

        is FirebaseFirestoreException -> when (throwable.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "You don't have permission to do that."

            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "Can't reach the server right now. Check your connection."

            else -> "Something went wrong talking to the database."
        }

        else -> throwable.localizedMessage ?: "Something went wrong. Please try again."
    }
}
