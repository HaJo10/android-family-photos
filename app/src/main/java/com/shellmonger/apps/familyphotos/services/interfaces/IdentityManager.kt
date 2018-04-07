package com.shellmonger.apps.familyphotos.services.interfaces

import android.arch.lifecycle.LiveData
import com.shellmonger.apps.familyphotos.models.User

interface IdentityManager {
    /**
     * Property for the current user record - null if the user is not signed in
     */
    val currentUser: LiveData<User?>

    /**
     * Sign in with a username / password
     */
    fun signin(username: String, password: String)

    /**
     * Sign out of the system
     */
    fun signout()
}