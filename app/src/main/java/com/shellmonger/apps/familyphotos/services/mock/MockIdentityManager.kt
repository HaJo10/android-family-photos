package com.shellmonger.apps.familyphotos.services.mock

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.shellmonger.apps.familyphotos.models.User
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityManager
import java.util.*

class MockIdentityManager : IdentityManager {
    private val mutableCurrentUser: MutableLiveData<User?> = MutableLiveData()

    init {
        mutableCurrentUser.value = null
    }

    /**
     * Property for the current user record - null if the user is not signed in
     */
    override val currentUser: LiveData<User?> = mutableCurrentUser

    /**
     * Sign in with a username / password
     */
    override fun signin(username: String, password: String) {
        mutableCurrentUser.value = User(UUID.randomUUID().toString(), username)
    }

    /**
     * Sign out of the system
     */
    override fun signout() {
        mutableCurrentUser.value = null
    }
}