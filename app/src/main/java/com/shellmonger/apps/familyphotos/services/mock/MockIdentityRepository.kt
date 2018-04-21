package com.shellmonger.apps.familyphotos.services.mock

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.shellmonger.apps.familyphotos.models.User
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityHandler
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRepository
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRequest
import java.util.*

data class MockUser(val username: String, var password: String, var mfaCode: String, var passwordReset: Boolean) {
    val attributes: MutableMap<String, String> = HashMap()
}

class MockIdentityRepository : IdentityRepository {
    companion object {
        private val TAG: String = this::class.java.simpleName

        private val DO_NOTHING: (Map<String, String>?) -> Unit = { }
    }

    private val mutableCurrentUser: MutableLiveData<User?> = MutableLiveData()
    private val mockUserMap: MutableMap<String, MockUser> = HashMap()

    init {
        mutableCurrentUser.value = null

        // Dummy user table
        val user1 = MockUser("user@user.com", "abcd1234", "123456",false)
        user1.attributes["name"] = "User 1"
        user1.attributes["phone_number"] = "+17205551212"
        mockUserMap[UUID.randomUUID().toString()] = user1

        val user2 = MockUser("reset@password.com", "abcd1234", "000000", true)
        user2.attributes["name"] = "User 2"
        user2.attributes["phone_number"] = "+14085551212"
        mockUserMap[UUID.randomUUID().toString()] = user2
    }

    /**
     * Property for the current user record - null if the user is not signed in
     */
    override val currentUser: LiveData<User?> = mutableCurrentUser

    /**
     * Initiate Flow: Sign in with a username / password
     *
     * @param handler the identity handler within the UI
     */
    override fun initiateSignin(handler: IdentityHandler) = runOnUiThread {
        handler(IdentityRequest.NEED_CREDENTIALS, null) { response -> signInWithCredentials(handler, response) }
    }

    /**
     * Initiate Flow: Sign-out
     *
     * @param handler the identity handler within the UI
     */
    override fun initiateSignout(handler: IdentityHandler) = runOnUiThread {
        mutableCurrentUser.value = null
        handler(IdentityRequest.SUCCESS, HashMap(), DO_NOTHING)
    }

    /**
     * Initiate Flow: Forgot Password
     *
     * @param handler the identity handler within the UI
     */
    override fun initiateForgotPassword(handler: IdentityHandler) = runOnUiThread {
        handler(IdentityRequest.NEED_CREDENTIALS, null) { response -> forgotPasswordWithCredentials(handler, response) }
    }

    /**
     * Initiate Flow: Sign up
     *
     * @param handler the identity handler within the UI
     */
    override fun initiateSignup(handler: IdentityHandler) = runOnUiThread {
        handler(IdentityRequest.NEED_SIGNUP, null) { response -> signUpWithCredentials(handler, response) }
    }

    /**
     * Store the new user profile in the livedata
     */
    private fun storeNewUserProfile(handler: IdentityHandler, mockUser: MockUser, parameters: Map<String, String>) {
        val user = User()
        user.username = parameters["username"] ?: ""
        for (key in parameters.keys) user.userAttributes[key] = parameters[key] ?: ""
        for (entry in mockUser.attributes) user.userAttributes[entry.key] = entry.value
        mutableCurrentUser.value = user
        handler(IdentityRequest.SUCCESS, parameters, DO_NOTHING)
    }

    /**
     * Handle a response for the sign-in with a username / password
     *
     * @param handler the identity handler
     * @param nResponse nullable response from the callback
     */
    private fun signInWithCredentials(handler: IdentityHandler, nResponse: Map<String, String>?) {
        val parameters: MutableMap<String, String> = HashMap()

        try {
            val response = checkNotNull(nResponse) { "Invalid response from NEED_CREDENTIALS" }

            // Copy the response into the parameters
            for (key in response.keys) parameters[key] = response[key] ?: ""
            val username = parameters["username"] ?: ""
            val password = parameters["password"] ?: ""
            check(username.isNotEmpty()) { "Invalid Username" }
            check(password.isNotEmpty()) { "Invalid Password" }

            val mockUser = mockUserMap.entries.find { it.value.username == username }
            when {
                mockUser == null -> handleFailure(handler, "Username does not exist")
                mockUser.value.password != password -> handleFailure(handler, "Password incorrect")
                mockUser.value.passwordReset -> // Test the new password flow
                    handler(IdentityRequest.NEED_NEWPASSWORD, null) {
                        checkNotNull(it) { "Invalid response from NEED_NEWPASSWORD" }
                        val newpassword = parameters["password"] ?: ""
                        check(newpassword.isNotEmpty()) { "Invalid new password" }
                        mockUserMap[mockUser.key]?.passwordReset = false
                        mockUserMap[mockUser.key]?.password = newpassword
                        storeNewUserProfile(handler, mockUser.value, parameters)
                        return@handler
                    }
                else -> // Test the MFA flow
                    handler(IdentityRequest.NEED_MULTIFACTORCODE, null) {
                        val mfaResponse = checkNotNull(it) { "Invalid response from NEED_MULTIFACTORCODE" }
                        val mfaCode = mfaResponse["mfaCode"] ?: ""
                        if (mockUser.value.mfaCode != mfaCode) {
                            handleFailure(handler, "MFA Code Incorrect")
                        } else {
                            storeNewUserProfile(handler, mockUser.value, parameters)
                            return@handler
                        }
                    }
            }
        } catch (exception: Exception) {
            handleFailure(handler, exception.message)
        }
    }

    /**
     * Handle a response for the forgot password flow
     *
     * @param handler the identity handler within the UI
     * @param nResponse nullable list of fields within the response
     */
    private fun forgotPasswordWithCredentials(handler: IdentityHandler, nResponse: Map<String, String>?) {
        val parameters: MutableMap<String, String> = HashMap()

        try {
            val response = checkNotNull(nResponse) { "Invalid response from NEED_CREDENTIALS" }
            for (key in response.keys) parameters[key] = response[key] ?: ""
            val username = parameters["username"] ?: ""
            val password = parameters["password"] ?: ""
            check(username.isNotEmpty()) { "Invalid username" }
            check(password.isNotEmpty()) { "Invalid password" }

            val mockUser = mockUserMap.entries.find { it.value.username == username }
            if (mockUser == null) {
                handleFailure(handler, "Username does not exist")
            } else {
                handler(IdentityRequest.NEED_MULTIFACTORCODE, mapOf("deliveryVia" to "SMS", "deliveryTo" to "+1705551212")) { nMfaResponse ->
                    run {
                        val mfaResponse = checkNotNull(nMfaResponse) { "Invalid response to NEED_MULTIFACTORCODE" }
                        val mfaCode = mfaResponse["mfaCode"] ?: ""
                        if (mfaCode != mockUser.value.mfaCode) {
                            handleFailure(handler, "MFA Code does not match")
                        } else {
                            mockUserMap[mockUser.key]?.password = password
                            handler(IdentityRequest.SUCCESS, null, DO_NOTHING)
                        }
                    }
                }
            }
        } catch (exception: Exception) {
            handleFailure(handler, exception.message)
        }
    }

    /**
     * Handle a response for the sign-up with name, email and password
     *
     * @param handler the identity handler
     * @param nResponse nullable response from the callback
     */
    private fun signUpWithCredentials(handler: IdentityHandler, nResponse: Map<String, String>?) {
        try {
            val response = checkNotNull(nResponse) { "Invalid response from NEED_SIGNUP" }

            val emailaddr = response["username"] ?: ""
            val password = response["password"] ?: ""
            val phone = response["phone"] ?: ""
            val name = response["name"] ?: ""
            check(emailaddr.isNotEmpty()) { "Email Address is empty" }
            check(password.isNotEmpty()) { "Password is empty" }
            check(phone.isNotEmpty()) { "Phone is empty" }
            check(name.isNotEmpty()) { "Name is empty" }

            handler(IdentityRequest.NEED_MULTIFACTORCODE, mapOf("deliveryVia" to "SMS", "deliveryTo" to "144255")) { nMfaResponse ->
                run {
                    try {
                        val mfaResponse = checkNotNull(nMfaResponse) { "Invalid response from NEED_MULTIFACTORCODE" }
                        val mfaCode = mfaResponse["mfaCode"] ?: ""
                        check(mfaCode.length == 6) { "Invalid MFA Code len=${mfaCode.length}" }
                        check(mfaCode == "144255") { "Invalid Code Entered" }

                        val mockUser = MockUser(emailaddr, password, mfaCode, false)
                        mockUser.attributes["name"] = name
                        mockUser.attributes["phone_number"] = phone
                        mockUserMap[UUID.randomUUID().toString()] = mockUser
                        handler(IdentityRequest.SUCCESS, null, DO_NOTHING)
                    } catch (exception: Exception) {
                        handleFailure(handler, exception.message)
                    }
                }
            }
        } catch (exception: Exception) {
            handleFailure(handler, exception.message)
        }
    }

    private fun handleFailure(handler: IdentityHandler, message: String?) {
        handler(IdentityRequest.FAILURE, mapOf("message" to (message ?: "Unknown error")), DO_NOTHING)
    }
}