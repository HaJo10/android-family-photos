package com.shellmonger.apps.familyphotos.services.mock

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.shellmonger.apps.familyphotos.models.User
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityHandler
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRepository
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRequest

class MockIdentityRepository : IdentityRepository {
    companion object {
        private val TAG: String = this::class.java.simpleName

        private val DO_NOTHING: (Map<String, String>?) -> Unit = { }
    }

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
    override fun initiateSignin(handler: IdentityHandler) {
        val parameters: MutableMap<String, String> = HashMap()
        parameters["username"] = ""
        parameters["password"] = ""

        runOnUiThread {
            Log.d(TAG, "initiateSignin: triggering NEED_CREDENTIALS")
            handler(IdentityRequest.NEED_CREDENTIALS, parameters)
            { response ->
                if (response != null) {
                    for (key in response.keys) parameters[key] = response[key] ?: ""
                    if (!parametersAreValid(parameters)) {
                        Log.d(TAG, "initiateSignin: triggering FAILURE")
                        handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid parameters"), DO_NOTHING)
                    } else {
                        Log.d(TAG, "initiateSignin: triggering NEED_MULTIFACTORCODE")
                        handler(IdentityRequest.NEED_MULTIFACTORCODE, mapOf("deliveryVia" to "SMS", "deliveryTo" to "+17205551212"))
                        { mfaResponse ->
                            if (mfaResponse != null) {
                                for (key in mfaResponse.keys) parameters[key] = mfaResponse[key] ?: ""
                                val mfaCode = parameters["mfaCode"] ?: ""
                                if (mfaCode.length != 6) {
                                    Log.d(TAG, "initiateSignin: triggering FAILURE")
                                    handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid mfa code $mfaCode - length = ${mfaCode.length}"), DO_NOTHING)
                                } else {
                                    val newUser = User()
                                    newUser.username = parameters["username"]!!
                                    mutableCurrentUser.value = newUser
                                    Log.d(TAG, "initiateSignin: triggering SUCCESS")
                                    handler(IdentityRequest.SUCCESS, parameters, DO_NOTHING)
                                }
                            } else {
                                Log.d(TAG, "initiateSignin: triggering FAILURE")
                                handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid mfa request response"), DO_NOTHING)
                            }
                        }
                    }
                } else
                    handler(IdentityRequest.FAILURE, mapOf("message" to "Invalid request response"), DO_NOTHING)
            }
        }
    }

    /**
     * Determines if the parameters are valid
     */
    private fun parametersAreValid(parameters: Map<String, String>): Boolean {
        val username = parameters["username"] ?: ""
        val password = parameters["password"] ?: ""
        return !(username.isEmpty() || password.isEmpty())
    }

    /**
     * Sign out of the system
     */
    override fun initiateSignout(handler: IdentityHandler) {
        runOnUiThread {
            Log.d(TAG, "initiateSignout: triggering SUCCESS")
            mutableCurrentUser.value = null
            handler(IdentityRequest.SUCCESS, HashMap(), DO_NOTHING)
        }
    }

    override fun initiateForgotPassword(handler: IdentityHandler) {
        val parameters: MutableMap<String, String> = HashMap()
        parameters["username"] = ""
        parameters["password"] = ""

        runOnUiThread {
            Log.d(TAG, "initiateForgotPassword: triggering NEED_CREDENTIALS")
            handler(IdentityRequest.NEED_CREDENTIALS, parameters)
            { response ->
                if (response != null) {
                    for (key in response.keys) parameters[key] = response[key] ?: ""
                    if (parametersAreValid(parameters)) {
                        Log.d(TAG, "initiateForgotPassword: triggering NEED_MULTIFACTORCODE")
                        handler(IdentityRequest.NEED_MULTIFACTORCODE, mapOf("deliveryVia" to "SMS", "deliveryTo" to "+17205551212"))
                        { mfaResponse ->
                            if (mfaResponse != null) {
                                for (key in mfaResponse.keys) parameters[key] = mfaResponse[key] ?: ""
                                val mfaCode = parameters["mfaCode"] ?: ""
                                if (mfaCode.length == 6)
                                    handler(IdentityRequest.SUCCESS, parameters, DO_NOTHING)
                                else
                                    errorMessage(handler, "Invalid MFA Code")
                            } else
                                errorMessage(handler, "Invalid MFA request response")
                        }
                    } else
                        errorMessage(handler, "Invalid parameters")
                } else
                    errorMessage(handler, "Invalid request response")
            }
        }
    }

    private fun errorMessage(handler: IdentityHandler, message: String) {
        Log.d(TAG, "triggering Failure")
        handler(IdentityRequest.FAILURE, mapOf("message" to message), DO_NOTHING)
    }
}