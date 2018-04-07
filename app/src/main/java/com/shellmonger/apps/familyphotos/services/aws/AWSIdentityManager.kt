package com.shellmonger.apps.familyphotos.services.aws

import android.content.Context
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.shellmonger.apps.familyphotos.models.User
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityManager
import java.lang.Exception

class AWSIdentityManager(context: Context) : IdentityManager {
    /**
     * The stored "current user" object
     */
    private val mutableCurrentUser: MutableLiveData<User> = MutableLiveData()

    /**
     * Reference to cognito user pools
     */
    private val userPool: CognitoUserPool

    init {
        val awsConfig = AWSConfiguration(context)
        userPool = CognitoUserPool(context, awsConfig)
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
        val cognitoUser = userPool.currentUser

        val authHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                userSession?.let {
                    val internalUser = User(cognitoUser.userId, userSession.username)
                    mutableCurrentUser.value = internalUser
                }
            }

            override fun onFailure(exception: Exception?) {
                // Do something with the failure here - this probably means setting an
                // error property and then setting state to FAILED, which is picked up
                // via LiveData<> observers
            }

            override fun getAuthenticationDetails(continuation: AuthenticationContinuation?, userId: String?) {
                val authDetails = AuthenticationDetails(username, password, null)
                continuation?.let {
                    it.setAuthenticationDetails(authDetails)
                    it.continueTask()
                }
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                // Custom challenge (e.g. TOTP) - handle the same way as MFA codes
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
                // If you need to deal with MFA, do it here - generally speaking, add a state
                // to the repository that is mutated according to the requirements.  The activity
                // (via the view model and observers) puts up an MFA request and submits
                continuation?.continueTask()
            }

        }
        cognitoUser.getSession(authHandler)
    }

    /**
     * Sign out of the system
     */
    override fun signout() {
        val cognitoUser = userPool.currentUser
        cognitoUser.signOut()
        mutableCurrentUser.value = null
    }
}