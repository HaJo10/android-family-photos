package com.shellmonger.apps.familyphotos.services.aws

import android.content.Context
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.shellmonger.apps.familyphotos.models.TokenType
import com.shellmonger.apps.familyphotos.models.User
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityHandler
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRepository
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRequest
import java.lang.Exception
import kotlin.concurrent.thread

class AWSIdentityRepository(context: Context) : IdentityRepository {
    /**
     * The stored "current user" object
     */
    private val mutableCurrentUser: MutableLiveData<User> = MutableLiveData()

    /**
     * Reference to cognito user pools - needed in the AWSAuthenticatorActivity
     */
    private val userPool: CognitoUserPool

    init {
        // Initially, logged out
        mutableCurrentUser.value = null

        val awsConfig = AWSConfiguration(context)
        userPool = CognitoUserPool(context, awsConfig)

        // Check to see if we have a current session - if so, we are logged in!!!!!
        userPool.currentUser.getSessionInBackground(object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                userSession?.let {
                    val user = User()
                    user.username = it.username
                    user.tokens[TokenType.ACCESS_TOKEN] = it.accessToken.jwtToken
                    user.tokens[TokenType.ID_TOKEN] = it.idToken.jwtToken
                    user.tokens[TokenType.REFRESH_TOKEN] = it.refreshToken.token
                    mutableCurrentUser.value = user
                }
            }

            override fun onFailure(exception: Exception?) { }
            override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) { }
            override fun authenticationChallenge(continuation: ChallengeContinuation?) { }
            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) { }
        })
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

        userPool.currentUser.getSessionInBackground(object : AuthenticationHandler {
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
//                                        thread(start = true) {
                                            with(continuation) {
                                                parameters["NEW_PASSWORD"] = response["password"] ?: ""
                                                continueTask()
                                            }
//                                        }
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
                            with (continuation!!) {
                                setMfaCode(response["mfaCode"] ?: "")
                                continueTask()
                            }
                        } else {
                            handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid MFA response")) { /* Do Nothing */ }
                        }
                    }
                }
            }
        })
    }

    /**
     * Sign out of the system
     */
    override fun initiateSignout(handler: IdentityHandler) {
        userPool.currentUser.signOut()
        mutableCurrentUser.value = null
        handler(IdentityRequest.SUCCESS, null) { /* Do Nothing */ }
    }

    override fun initiateForgotPassword(handler: IdentityHandler) {
        var username = ""
        var password = ""

        runOnUiThread {
            handler(IdentityRequest.NEED_CREDENTIALS, null) {
                response -> if (response != null) {
                    username = response["username"] ?: ""
                    password = response["password"] ?: ""
                    if (username.isEmpty() || password.isEmpty()) {
                        handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid username or password")) {/* Do Nothing */ }
                    } else {
                        userPool.getUser(username).forgotPasswordInBackground(object : ForgotPasswordHandler {
                            /**
                             * This is called after successfully setting new password for a user.
                             * The new password can new be used to authenticate this user.
                             */
                            override fun onSuccess() {
                                runOnUiThread {
                                    handler(IdentityRequest.SUCCESS, null) { /* Do Nothing */ }
                                }
                            }

                            /**
                             * This is called for all fatal errors encountered during the password reset process
                             * Probe {@exception} for cause of this failure.
                             * @param exception REQUIRED: Contains failure details.
                             */
                            override fun onFailure(exception: Exception?) {
                                runOnUiThread {
                                    val message = exception?.message ?: "Unknown error"
                                    handler(IdentityRequest.FAILURE, mapOf("message" to message)) { /* Do Nothing */ }
                                }
                            }

                            /**
                             * A code may be required to confirm and complete the password reset process
                             * Supply the new password and the confirmation code - which was sent through email/sms
                             * to the continuation
                             * @param continuation REQUIRED: Continuation to the next step.
                             */
                            override fun getResetCode(continuation: ForgotPasswordContinuation?) {
                                runOnUiThread {
                                    handler(IdentityRequest.NEED_MULTIFACTORCODE, mapOf("deliveryVia" to continuation!!.parameters.deliveryMedium, "deliveryTo" to continuation!!.parameters.destination)) {
                                    response ->
                                        if (response != null) {
                                            with(continuation!!) {
                                                setPassword(password)
                                                setVerificationCode(response["mfaCode"])
                                                continueTask()
                                            }
                                        } else {
                                            handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid MFA response")) { /* Do Nothing */ }
                                        }
                                    }
                                }
                            }
                        })
                    }
                } else {
                    handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid Response")) { /* Do Nothing */ }
                }
            }
        }
    }

}