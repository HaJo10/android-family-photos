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
import kotlinx.android.synthetic.main.activity_signup.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import org.koin.android.architecture.ext.viewModel

class SignupActivity : AppCompatActivity() {
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

        setContentView(R.layout.activity_signup)

        // We should be able to close this activity, in which case we go back
        // to the prior activity.
        signup_cancel_button.onClick { this@SignupActivity.finish() }

        // Hook up validator for the fields
        signup_username.validate({ s -> isUsernameValid(s) }, "Valid email address required")
        signup_password.validate({ s -> isPasswordValid(s) }, "Minimum 6 characters required")
        signup_phone.validate({ s -> isPhoneValid(s) }, "Valid phone number required")
        signup_name.validate({ s -> isNameValid(s) }, "A name must be entered")

        // We only enable the login button when both the email address and password are both
        // valid.  To do this, we wire up an additional text listener on both to call the
        // checker
        signup_username.afterTextChanged { checkSubmitEnabled() }
        signup_password.afterTextChanged { checkSubmitEnabled() }
        signup_phone.afterTextChanged { checkSubmitEnabled() }
        signup_name.afterTextChanged { checkSubmitEnabled() }

        // Wire up the form buttons
        signup_button.onClick { handleSignup() }

        // Call the checkSubmitEnabled to get into the right state
        checkSubmitEnabled()
    }

    /**
     * Checks the loginform_username and loginform_password.  If both of them are
     * valid, then enable the signin button
     */
    private fun checkSubmitEnabled() {
        signup_button.isEnabled =
            isUsernameValid(signup_username.getContent())
            && isPasswordValid(signup_password.getContent())
            && isPhoneValid(signup_phone.getContent())
            && isNameValid(signup_name.getContent())
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
     * Checks to see if the full name is valid
     */
    private fun isNameValid(s: String): Boolean = s.isNotBlank()

    /**
     * Checks to see if the phone number is valid
     */
    private fun isPhoneValid(s: String): Boolean = s.isNotBlank()

    /**
     * Handles the form submission event
     */
    @SuppressLint("InflateParams")
    private fun handleSignup() {
        model.initiateSignup {
            request, params, callback -> when(request) {
                IdentityRequest.NEED_SIGNUP -> {
                    Log.d(TAG, "NEED_SIGNUP")
                    val attrs: MutableMap<String, String> = HashMap()
                    attrs["username"] = signup_username.getContent()
                    attrs["password"] = signup_password.getContent()
                    attrs["phone"] = signup_phone.getContent()
                    attrs["name"] = signup_name.getContent()
                    callback(attrs)
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
                toast("Signup Successful")
                model.updateStoredUsername(signup_username.getContent())
                this@SignupActivity.finish()
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
                    positiveButton("Close") { this@SignupActivity.finish() }
                }
            }
            }
        }
    }
}
