package com.shellmonger.apps.familyphotos.services.aws

import android.content.Context
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.shellmonger.apps.familyphotos.models.TokenType
import com.shellmonger.apps.familyphotos.models.User
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityHandler
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityManager
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRequest
import java.lang.Exception
import kotlin.concurrent.thread

class AWSIdentityManager(context: Context) : IdentityManager {
    /**
     * The stored "current user" object
     */
    private val mutableCurrentUser: MutableLiveData<User> = MutableLiveData()

    /**
     * Reference to cognito user pools - needed in the AWSAuthenticatorActivity
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
    override fun initiateSignin(handler: IdentityHandler) {
        val user = User()

        thread(start = true) {
            userPool.currentUser.getSession(object : AuthenticationHandler {
                override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                    runOnUiThread {
                        userSession?.let {
                            user.username = it.username
                            user.tokens[TokenType.ACCESS_TOKEN] = it.accessToken.jwtToken
                            user.tokens[TokenType.ID_TOKEN] = it.idToken.jwtToken
                            user.tokens[TokenType.REFRESH_TOKEN] = it.refreshToken.token
                        }
                        mutableCurrentUser.value = user
                        handler(IdentityRequest.SUCCESS, null) { /* Do Nothing */ }
                    }
                }

                override fun onFailure(exception: Exception?) {
                    runOnUiThread {
                        handler(IdentityRequest.FAILURE, mapOf("message" to exception!!.message!!)) { /* Do Nothing */ }
                    }
                }

                override fun getAuthenticationDetails(continuation: AuthenticationContinuation?, userId: String?) {
                    runOnUiThread {
                        val request = HashMap<String,String>()
                        userId?.let { request["username"] = it }
                        handler(IdentityRequest.NEED_CREDENTIALS, request) { response ->
                            if (response != null) {
                                thread(start = true) {
                                    val authDetails = AuthenticationDetails(
                                        response["username"] ?: "",
                                        response["password"] ?: "",
                                        null
                                    )
                                    with (continuation!!) {
                                        setAuthenticationDetails(authDetails)
                                        continueTask()
                                    }
                                }
                            } else {
                                handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid response for credentials")) { /* Do Nothing */ }
                            }
                        }
                    }
                }

               override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                    if (continuation != null) {
                        when (continuation.challengeName) {
                            "NEW_PASSWORD_REQUIRED" -> {
                                runOnUiThread {
                                    handler(IdentityRequest.NEED_NEWPASSWORD, null) { response ->
                                        if (response != null) {
                                            thread(start = true) {
                                                with(continuation) {
                                                    parameters["NEW_PASSWORD"] = response["password"] ?: ""
                                                    continueTask()
                                                }
                                            }
                                        } else {
                                            handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid response for new password")) { /* Do Nothing */ }
                                        }
                                    }
                                }

                            }

                            else -> {
                                runOnUiThread {
                                    handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid authentication challenge")) { /* Do Nothing */ }
                                }
                            }
                        }
                    }
                }

                override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
                    runOnUiThread {
                        handler(IdentityRequest.NEED_MULTIFACTORCODE, null) { response ->
                            if (response != null) {
                                thread(start = true) {
                                    with (continuation!!) {
                                        setMfaCode(response["mfaCode"] ?: "")
                                        continueTask()
                                    }
                                }
                            } else {
                                handler(IdentityRequest.FAILURE, mapOf("messagfe" to "Invalid MFA response")) { /* Do Nothing */ }
                            }
                        }
                    }
                }
            })
        }
    }

    /**
     * Sign out of the system
     */
    override fun initiateSignout(handler: IdentityHandler) {
        userPool.currentUser.signOut()
        mutableCurrentUser.value = null
        handler(IdentityRequest.SUCCESS, null) { /* Do Nothing */ }
    }

}