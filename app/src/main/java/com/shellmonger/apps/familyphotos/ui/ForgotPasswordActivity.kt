package com.shellmonger.apps.familyphotos.ui

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.koin.android.architecture.ext.viewModel

class ForgotPasswordActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_forgot_password)

        // Work out if we passed in a username from the login screen - if we did, then
        // use it.
        if (intent.hasExtra("login_username")) {
            forgotpassword_username.text.clear()
            forgotpassword_username.text.append(intent.getStringExtra("login_username"))
            if (forgotpassword_username.getContent().isNotBlank()) forgotpassword_password.requestFocus()
        }

        // We should be able to close this activity, in which case we go back
        // to the prior activity.
        forgotpassword_cancel_button.onClick { this@ForgotPasswordActivity.finish() }

        // Hook up validator for email address and password.  In this case, we
        // do a minimal validation for the input as it will be checked by the
        // Amazon Cognito system as well.
        forgotpassword_username.validate({ s -> isUsernameValid(s) }, "Valid email address required")

        // Now do the same for password.  We require a minimum length of 6 characters
        forgotpassword_password.validate({ s -> isPasswordValid(s) }, "Minimum 6 characters required")

        // We only enable the login button when both the email address and password are both
        // valid.  To do this, we wire up an additional text listener on both to call the
        // checker
        forgotpassword_username.afterTextChanged { checkSubmitEnabled() }
        forgotpassword_password.afterTextChanged { checkSubmitEnabled() }

        // Wire up the form buttons
        forgotpassword_button.onClick { handleForgotPassword() }

        // Call the checkSubmitEnabled to get into the right state
        checkSubmitEnabled()
    }

    /**
     * Checks the loginform_username and loginform_password.  If both of them are
     * valid, then enable the signin button
     */
    private fun checkSubmitEnabled() {
        forgotpassword_button.isEnabled =
                isUsernameValid(forgotpassword_username.getContent())
                && isPasswordValid(forgotpassword_password.getContent())
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
     * Handles the form submission event
     */
    @SuppressLint("InflateParams")
    private fun handleForgotPassword() {
        model.initiateForgotPassword {
            request, params, callback -> when(request) {
                IdentityRequest.NEED_CREDENTIALS -> {
                    Log.d(TAG, "NEED_CREDENTIALS")
                    callback(mapOf("username" to forgotpassword_username.getContent(), "password" to forgotpassword_password.getContent()))
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

                IdentityRequest.SUCCESS -> {
                    Log.d(TAG, "SUCCESS")
                    model.updateStoredUsername(forgotpassword_username.getContent())
                    this@ForgotPasswordActivity.finish()
                }

                IdentityRequest.FAILURE -> {
                    Log.d(TAG, "FAILURE")
                    alert(params?.get("message") ?: "Error submitting new credentials") {
                        title = "Password Reset Failed"
                        positiveButton("Close") { /* Do nothing */ }
                    }.show()
                }

                else -> {
                    Log.d(TAG, "Unexpected IdentityHandler callback")
                    alert("We received an unexpected request from the backend service") {
                        title = "Unexpected request"
                        positiveButton("Close") { this@ForgotPasswordActivity.finish() }
                    }
                }
            }
        }
    }
}
