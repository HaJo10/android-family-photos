package com.shellmonger.apps.familyphotos.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.view.menu.MenuBuilder
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.shellmonger.apps.familyphotos.R
import com.shellmonger.apps.familyphotos.extensions.getShowAsAction
import com.shellmonger.apps.familyphotos.extensions.setIconColor
import com.shellmonger.apps.familyphotos.extensions.toast
import com.shellmonger.apps.familyphotos.lifecycle.Logger
import com.shellmonger.apps.familyphotos.lifecycle.RequestCodes
import kotlinx.android.synthetic.main.activity_main.*

/**
 * The main "list" or "master" portion of the UI.  Called immediately after the SplashActivity
 * or when returning to the list.
 */
class MainActivity : AppCompatActivity() {
    private val logger = Logger("MainActivity")

    /**
     * True if the device we are on has a camera we can use.
     */
    private var hasCamera: Boolean = false

    /**
     * Activity lifecycle event.  Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *      down then this Bundle contains the data it most recently supplied in
     *      onSaveInstanceState(Bundle).  Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)

        // Determine if we have a camera
        hasCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    /**
     * Activity lifecycle event.  Initialize the contents of the Activity's standard options menu.
     * You should place your menu items in to menu.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed; if you return false it will not
     *  be shown.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * Activity lifecycle event.  Makes the menu icons available on the collapsed options menu
     */
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            if (menu is MenuBuilder) {
                logger.debug("menu is a menubuilder - making icons visible")
                try {
                    val field = menu.javaClass.getDeclaredField("mOptionalIconsVisible")
                    field.isAccessible = true
                    field.setBoolean(menu, true)
                } catch (ignored: Exception) {
                    logger.debug("ignored exception: ${ignored.javaClass.simpleName}")
                }
            }

            // Update the icon color.  If the item is in the options subMenu, then make the
            // icon black.  If it is in the toolbar, make it white.
            for (item in 0 until menu.size()) {
                val menuItem = menu.getItem(item)
                menuItem.icon.setIconColor(
                    if (menuItem.getShowAsAction() == 0) Color.BLACK else Color.WHITE
                )
            }

            // Disable the camera functionality if we don't have one.
            if (!hasCamera) {
                menu.findItem(R.id.mainActionCamera)?.let {
                    it.isVisible = false
                    it.isEnabled = false
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    /**
     * Activity lifecycle event.  This hook is called whenever an item in your options menu
     * is selected.
     *
     * @param item The menu item that was selected.
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.mainActionCamera -> {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePhotoIntent.resolveActivity(packageManager) != null)
                startActivityForResult(intent, RequestCodes.TAKE_PICTURE)
            else
                toast("No camera available")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Activity lifecycle event.  This hook is called when we initiate an activity using
     * startActivityForResult() and expect a result back.
     *
     * @param requestCode the matching request code when startActivityForResult() was called
     * @param resultCode the result code
     * @param data any data returned through an intent
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RequestCodes.TAKE_PICTURE -> onPictureTaken(resultCode, data)
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Called when there is a new picture taken and the picture data is retrieved
     */
    private fun onPictureTaken(resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK || data == null) {
            toast("No picture data - cancelling")
            return
        }

        val pictureData = data?.extras.get("data") as Bitmap
        // Do something with the picture data
    }
}
