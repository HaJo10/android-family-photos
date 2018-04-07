package com.shellmonger.apps.familyphotos.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.menu.MenuBuilder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.shellmonger.apps.familyphotos.R
import com.shellmonger.apps.familyphotos.extensions.getContent
import com.shellmonger.apps.familyphotos.extensions.getShowAsAction
import com.shellmonger.apps.familyphotos.extensions.setIconColor
import com.shellmonger.apps.familyphotos.extensions.setIconsVisible
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.koin.android.architecture.ext.viewModel

/**
 * The main activity for the app - creates a "list" of notes that the user can
 * interact with.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        val TAG: String = this::class.java.simpleName
    }

    /**
     * View model for this activity
     */
    private val model by viewModel<MainActivityViewModel>()

    /**
     * Called when the activity is starting. This is where most initialization should go: calling
     * setContentView(int) to inflate the activity's UI, initializing any view models, etc.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate()")
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        // Get a reference to the view-model for this activity
        model.let {
            // Monitor for changes in the currentUser object.  If there are changes, then
            // call invalidateOptionsMenu() so that the options menu gets redrawn
            it.currentUser.observe(this, Observer { invalidateOptionsMenu() })
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu. You should place your menu
     * items in to menu.  This is only called once, the first time the options menu is displayed.
     * To update the menu every time it is displayed, see onPrepareOptionsMenu(Menu).
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    /**
     * Prepare the Screen's standard options menu to be displayed. This is called right before the
     * menu is shown, every time it is shown. You can use this method to efficiently enable/disable
     * items or otherwise dynamically modify the contents.
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            (menu as? MenuBuilder)?.setIconsVisible(true)

            for (item in 0 until menu.size()) {
                val menuItem = menu.getItem(item)

                // Set the appropriate color for the icon within the menu
                menuItem.icon.setIconColor(if (menuItem.getShowAsAction() == 0) Color.BLACK else Color.WHITE)

                // If the item is sign-in or sign-out, then make the right one visible
                when (menuItem.itemId) {
                    R.id.action_login  -> menuItem.isVisible = model.currentUser.value == null
                    R.id.action_logout -> menuItem.isVisible = model.currentUser.value != null
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * This hook is called whenever an item in your options menu is selected. The default
     * implementation simply returns false to have the normal processing happen (calling the
     * item's Runnable or sending a message to its Handler as appropriate). You can use this
     * method for any items for which you would like to do processing without those other
     * facilities.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_login ->  onLoginMenuItemSelected()
            R.id.action_logout -> onLogoutMenuItemSelected()
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Hook called when the user requests to sign-in to the application
     */
    private fun onLoginMenuItemSelected(): Boolean {
        // Close the options menu
        closeOptionsMenu()

        // Produce the Alert dialog with the login form
        val formView = layoutInflater.inflate(R.layout.login_form, null, false)
        val username = formView.find<EditText>(R.id.login_form_username)
        val password = formView.find<EditText>(R.id.login_form_password)

        AlertDialog.Builder(this)
            .setView(formView)
            .setNegativeButton("Cancel") { _, _ -> toast("Cancelled") }
            .setPositiveButton("Sign In") { _, _ -> model.signin(username.getContent(), password.getContent()) }
            .show()

        // We handled the request
        return true
    }

    /**
     * Hook called when the user requests to sign-out of the application
     */
    private fun onLogoutMenuItemSelected(): Boolean {
        // Close the options menu
        closeOptionsMenu()

        // Produce an Are You Sure? dialog
        alert("Are you sure you want to sign out?") {
            yesButton { model.signout() }
            noButton { }
        }.show()

        // We handled the request
        return true
    }
}
