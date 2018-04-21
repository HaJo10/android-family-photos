package com.shellmonger.apps.familyphotos.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.shellmonger.apps.familyphotos.R
import com.shellmonger.apps.familyphotos.extensions.afterTextChanged
import com.shellmonger.apps.familyphotos.extensions.getContent
import com.shellmonger.apps.familyphotos.extensions.isValidEmail
import com.shellmonger.apps.familyphotos.extensions.validate
import com.shellmonger.apps.familyphotos.services.interfaces.IdentityRequest
import kotlinx.android.synthetic.main.activity_authenticator.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.koin.android.architecture.ext.viewModel

/**
 * Deals with authentication - sign-up and sign-in
 */
class AuthenticatorActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = this::class.java.simpleName
    }

    /**
     * View model for this activity
     */
    private val model by viewModel<AuthenticatorActivityViewModel>()

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * setContentView(int) to inflate the activity's UI, initializing any view models, etc.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authenticator)

        // We should be able to close this activity, in which case we go back
        // to the prior activity.
        authenticator_cancel_button.onClick { this@AuthenticatorActivity.finish() }

        // Hook up validator for email address and password.  In this case, we
        // do a minimal validation for the input as it will be checked by the
        // Amazon Cognito system as well.
        loginform_username.validate({ s -> isUsernameValid(s) }, "Valid email address required")

        // Now do the same for password.  We require a minimum length of 6 characters
        loginform_password.validate({ s -> isPasswordValid(s) }, "Minimum 6 characters required")

        // We only enable the login button when both the email address and password are both
        // valid.  To do this, we wire up an additional text listener on both to call the
        // checker
        loginform_username.afterTextChanged { checkLoginEnabled() }
        loginform_password.afterTextChanged { checkLoginEnabled() }

        // Wire up the form buttons
        loginform_signin_button.onClick { handleLogin() }
        loginform_signup_button.onClick { startActivity(Intent(this@AuthenticatorActivity, SignupActivity::class.java)) }
        loginform_forgotpassword_button.onClick {
            val intent = Intent(this@AuthenticatorActivity, ForgotPasswordActivity::class.java)
            intent.putExtra("login_username", loginform_username.getContent())
            startActivity(intent)
        }

        // Check the state of the login button
        checkLoginEnabled()
    }

    /**
     * Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(), for your activity to
     * start interacting with the user. This is a good place to begin animations, open exclusive-
     * access devices (such as the camera), etc.
     */
    override fun onResume() {
        super.onResume()

        // If the username on the page is blank and there is a stored username,
        // then update the username and password
        if (loginform_username.getContent().isBlank() && (model.storedUsername.value ?: "").isNotEmpty()) {
            loginform_username.text.append(model.storedUsername.value)
            loginform_password.text.clear()
            loginform_password.requestFocus()
        }
    }

    /**
     * Checks the loginform_username and loginform_password.  If both of them are
     * valid, then enable the signin button
     */
    private fun checkLoginEnabled() {
        loginform_signin_button.isEnabled = isUsernameValid(loginform_username.getContent())
                && isPasswordValid(loginform_password.getContent())
    }

    /**
     * Checks to see if the username is valid
     */
    private fun isUsernameValid(s: String): Boolean = s.isValidEmail()

    /**
     * Checks to see if the password is valid
     */
    private fun isPasswordValid(s: String): Boolean = s.length >= 6

    /**
     * Handles the login event
     */
    @SuppressLint("InflateParams")
    private fun handleLogin() {
        model.initiateSignin {
            request, params, callback -> when(request) {
                IdentityRequest.NEED_CREDENTIALS -> {
                    Log.d(TAG, "NEED_CREDENTIALS")
                    callback(mapOf("username" to loginform_username.getContent(), "password" to loginform_password.getContent()))
                }

                IdentityRequest.NEED_NEWPASSWORD -> {
                    Log.d(TAG, "NEED_NEWPASSWORD")
                    val newPasswordDialog = layoutInflater.inflate(R.layout.dialog_new_password, null)
                    val passwordInput = newPasswordDialog.find(R.id.newpassworddialog_password) as EditText
                    alert {
                        title = "Enter New Password"
                        customView = newPasswordDialog
                        positiveButton("OK") {
                            callback(mapOf("password" to passwordInput.getContent()))
                        }
                    }.show()
                }

                IdentityRequest.NEED_MULTIFACTORCODE -> {
                    Log.d(TAG, "NEED_MULTIFACTORCODE")
                    val mfaPromptDialog = layoutInflater.inflate(R.layout.dialog_mfa_prompt, null)
                    val mfaCodeInput = mfaPromptDialog.find(R.id.mfapromptdialog_code) as EditText
                    val mfaInstructions = mfaPromptDialog.find(R.id.mfapromptdialog_instructions) as TextView
                    params?.let { mfaInstructions.text = "Enter the code we just sent to ${it["deliveryVia"] ?: "UNK"}:${it["deliveryTo"] ?: "UNKNOWN"}" }
                    alert {
                        title = "Multi-factor Code Required"
                        customView = mfaPromptDialog
                        positiveButton("OK") {
                            callback(mapOf("mfaCode" to mfaCodeInput.getContent()))
                        }
                    }.show()
                }

                // Sucessful signin
                IdentityRequest.SUCCESS -> {
                    Log.d(TAG, "SUCCESS")
                    model.updateStoredUsername(loginform_username.getContent())
                    this@AuthenticatorActivity.finish()
                }

                // Failed signin
                IdentityRequest.FAILURE -> {
                    Log.d(TAG, "FAILURE")
                    alert(params?.get("message") ?: "Error submitting credentials") {
                        title = "Login Denied"
                        positiveButton("Close") { /* Do nothing */ }
                    }.show()
                }

                else -> {
                    Log.d(TAG, "$request")
                    alert("Unknown or unexpected identity request") {
                        positiveButton("Close") { /* Do nothing */ }
                    }
                }
            }
        }
    }
}
