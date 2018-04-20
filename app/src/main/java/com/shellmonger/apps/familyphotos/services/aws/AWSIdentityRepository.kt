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

class AWSIdentityRepository(context: Context) : IdentityRepository {
    companion object {
        private val TAG = this::class.java.simpleName

        /**
         * A lambda that does nothing - used for callbacks from an identity handler
         */
        private val DO_NOTHING: (Map<String, String>?) -> Unit = { /* Do Nothing */ }
    }

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

    private fun storeUserSession(userSession: CognitoUserSession) {
        val user = User()
        user.username = userSession.username
        user.tokens[TokenType.ACCESS_TOKEN] = userSession.accessToken.jwtToken
        user.tokens[TokenType.ID_TOKEN] = userSession.idToken.jwtToken
        user.tokens[TokenType.REFRESH_TOKEN] = userSession.refreshToken.token
        runOnUiThread { mutableCurrentUser.value = user }
    }

    /**
     * Sign in with a username / password
     */
    override fun initiateSignin(handler: IdentityHandler) {
        try {
            userPool.currentUser.getSessionInBackground(object : AuthenticationHandler {
                /**
                 * This method is called to deliver valid tokens, when valid tokens were locally
                 * available (cached) or after successful completion of the authentication process.
                 * The `newDevice` will is an instance of [CognitoDevice] for this device, and this
                 * parameter will be not null during these cases:
                 * 1- If the user pool allows devices to be remembered and this is is a new device, that is
                 * first time authentication on this device.
                 * 2- When the cached device key is lost and, hence, the service identifies this as a new device.
                 *
                 * @param nullableUserSession [CognitoUserSession?]  Contains valid user tokens.
                 * @param newDevice           [CognitoDevice], will be null if this is not a new device.
                 */
                override fun onSuccess(nullableUserSession: CognitoUserSession?, newDevice: CognitoDevice?) {
                    val userSession = checkNotNull(nullableUserSession) { "user session is null" }
                    storeUserSession(userSession)
                    runOnUiThread { handler(IdentityRequest.SUCCESS, null, DO_NOTHING) }
                }

                /**
                 * This method is called when a fatal exception was encountered during
                 * authentication. The current authentication process continue because of the error
                 * , hence a continuation is not available. Probe `exception` for details.
                 *
                 * @param exception is this Exception leading to authentication failure.
                 */
                override fun onFailure(exception: Exception?) {
                    handleFailure(handler, exception?.message)
                }

                /**
                 * Call out to the dev to get the credentials for a user.
                 *
                 * @param authenticationContinuation is a [AuthenticationContinuation] object that should
                 * be used to continue with the authentication process when
                 * the users' authentication details are available.
                 * @param userId                     Is the user-ID (username  or alias) used in authentication.
                 * This will be null if the user ID is not available.
                 */
                override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation?, userId: String?) {
                    val continuation = checkNotNull(authenticationContinuation) { "Invalid authentication continuation" }

                    runOnUiThread {
                        handler(IdentityRequest.NEED_CREDENTIALS, null) { nResponse ->
                            run {
                                val response = checkNotNull(nResponse) { "Invalid identity response" }
                                val username = response["username"] ?: ""
                                val password = response["password"] ?: ""
                                check(username.isNotEmpty()) { "Username is empty" }
                                check(password.isNotEmpty()) { "Password is empty" }

                                continuation.setAuthenticationDetails(AuthenticationDetails(username, password, null))
                                continuation.continueTask()
                            }
                        }
                    }
                }

                /**
                 * Call out to the dev to respond to a challenge.
                 * The authentication process as presented the user with the a challenge, to successfully authenticate.
                 * This a generic challenge, that is not MFA or user password verification.
                 *
                 * @param nContinuation contains details about the challenge and allows dev to respond to the
                 * challenge.
                 */
                override fun authenticationChallenge(nContinuation: ChallengeContinuation?) {
                    val continuation = checkNotNull(nContinuation) { "Invalid challenge authentication" }
                    when (continuation.challengeName) {
                        "NEW_PASSWORD_REQUIRED" -> {
                            runOnUiThread {
                                handler(IdentityRequest.NEED_NEWPASSWORD, null) { nResponse ->
                                    run {
                                        val response = checkNotNull(nResponse) { "Invalid new password response" }
                                        continuation.parameters["NEW_PASSWORD"] = response["password"] ?: ""
                                        continuation.continueTask()
                                    }
                                }
                            }
                        }

                        else -> { handleFailure(handler, "Unknown authentication challenge") }
                    }
                }

                /**
                 * Call out to the dev to send MFA code.
                 * MFA code would have been sent via the deliveryMethod before this is invoked.
                 * This callback can be invoked in two scenarios -
                 * 1)  MFA verification is required and only one possible MFA delivery medium is
                 * available.
                 * 2)  MFA verification is required and a MFA delivery medium was successfully set.
                 * 3)  An MFA code sent earlier was incorrect and at-least one more attempt to send
                 * MFA code is available.
                 *
                 * @param nContinuation medium through which the MFA will be delivered
                 */
                override fun getMFACode(nContinuation: MultiFactorAuthenticationContinuation?) {
                    val continuation = checkNotNull(nContinuation) { "Invalid continuation token" }
                    runOnUiThread {
                        handler(IdentityRequest.NEED_MULTIFACTORCODE, null) { nResponse ->
                            run {
                                val response = checkNotNull(nResponse) { "Invalid MFA response" }
                                continuation.setMfaCode(response["mfaCode"] ?: "")
                                continuation.continueTask()
                            }
                        }
                    }
                }
            })
        } catch (exception: Exception) {
            handleFailure(handler, "Validation error")
        }
    }

    /**
     * Sign out of the system
     */
    override fun initiateSignout(handler: IdentityHandler) {
        userPool.currentUser.signOut()
        mutableCurrentUser.value = null
        handler(IdentityRequest.SUCCESS, null, DO_NOTHING)
    }

    /**
     * Initiate the forgot password flow
     */
    override fun initiateForgotPassword(handler: IdentityHandler) {
        runOnUiThread {
            handler(IdentityRequest.NEED_CREDENTIALS, null) { response -> fpHasCredentials(handler, response) }
        }
    }

    /**
     * Handle the response from the forgot password flow when we have credentials
     */
    private fun fpHasCredentials(handler: IdentityHandler, nResponse: Map<String,String>?) {
        try {
            // response validation
            val response: Map<String,String> = checkNotNull(nResponse) { "Invalid response when requesting new credentials" }
            check(response["username"]?.isNotEmpty() ?: false) { "Username must be specified" }
            check(response["password"]?.isNotEmpty() ?: false) { "New password must be specified" }

            // Call the forgotPassword flow on a background thread
            userPool.getUser(response["username"]).forgotPasswordInBackground(object : ForgotPasswordHandler {
                /**
                 * This is called after successfully setting new password for a user.
                 * The new password can new be used to authenticate this user.
                 */
                override fun onSuccess() {
                    runOnUiThread { handler(IdentityRequest.SUCCESS, null, DO_NOTHING) }
                }

                /**
                 * This is called for all fatal errors encountered during the password reset process
                 * Probe {@exception} for cause of this failure.
                 * @param exception REQUIRED: Contains failure details.
                 */
                override fun onFailure(exception: Exception?) {
                    handleFailure(handler, exception?.message ?: "Unknown error")
                }

                /**
                 * A code may be required to confirm and complete the password reset process
                 * Supply the new password and the confirmation code - which was sent through email/sms
                 * to the continuation
                 * @param continuation REQUIRED: Continuation to the next step.
                 */
                override fun getResetCode(continuation: ForgotPasswordContinuation?) {
                    runOnUiThread {
                        val delivery = checkNotNull(continuation?.parameters) { "Invalid continuation token" }
                        handler(IdentityRequest.NEED_MULTIFACTORCODE, mapOf("deliveryVia" to delivery.deliveryMedium, "deliveryTo" to delivery.destination)) {
                            nCodeResponse -> run {
                                val mfaResponse = checkNotNull(nCodeResponse) { "Invalid response when requesting MFA code" }
                                with (continuation!!) {
                                    setPassword(response["password"]!!)
                                    setVerificationCode(mfaResponse["mfaCode"]!!)
                                    continueTask()
                                }
                            }
                        }
                    }
                }
            })
        } catch (exception: Exception) {
            handleFailure(handler, exception.message ?: "Unknown validation error")
        }
    }

    /**
     * Handles failure cases
     */
    private fun handleFailure(handler: IdentityHandler, message: String?) {
        runOnUiThread { handler(IdentityRequest.FAILURE, mapOf("message" to (message ?: "Unknown error")), DO_NOTHING) }
    }

}