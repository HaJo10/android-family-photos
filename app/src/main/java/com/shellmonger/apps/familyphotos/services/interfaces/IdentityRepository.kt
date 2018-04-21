package com.shellmonger.apps.familyphotos.services.interfaces

import android.arch.lifecycle.LiveData
import com.shellmonger.apps.familyphotos.models.User

enum class IdentityRequest {
    NEED_SIGNUP,
    NEED_CREDENTIALS,
    NEED_NEWPASSWORD,
    NEED_MULTIFACTORCODE,
    SUCCESS,
    FAILURE
}

typealias IdentityResponse = (Map<String, String>?) -> Unit
typealias IdentityHandler = (IdentityRequest, Map<String, String>?, IdentityResponse) -> Unit

interface IdentityRepository {
    /**
     * Property for the current user record - null if the user is not signed in
     */
    val currentUser: LiveData<User?>

    /**
     * Stored username
     */
    val storedUsername: LiveData<String?>

    /**
     * Sign in with a username / password
     */
    fun initiateSignin(handler: IdentityHandler)

    /**
     * Sign out of the system
     */
    fun initiateSignout(handler: IdentityHandler)

    /**
     * Forgot Password flow
     */
    fun initiateForgotPassword(handler: IdentityHandler)

    /**
     * Sign up for an account flow
     */
    fun initiateSignup(handler: IdentityHandler)

    /**
     * Update the stored username
     */
    fun updateStoredUsername(username: String?)
}