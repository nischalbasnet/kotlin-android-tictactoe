package com.nbasnet.extensions.activity

import android.content.Context
import android.content.Intent
import android.hardware.input.InputManager
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import es.dmoral.toasty.Toasty

/**
 * Adds toast extension
 */
fun FragmentActivity.toast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toasty.info(this, message, duration).show()
}

fun FragmentActivity.failToast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toasty.error(this, message, duration).show()
}

fun FragmentActivity.successToast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toasty.success(this, message, duration).show()
}

fun FragmentActivity.startActivity(cls: Class<*>, payLoad: Bundle? = null): Unit {
    val intent = Intent(this, cls)

    if (payLoad != null) intent.putExtra("payload", payLoad)

    startActivity(intent)
}

fun FragmentActivity.getInputMethodManager(): InputMethodManager {
    return getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}

fun FragmentActivity.hideKeyboard(inputMethodManager: InputMethodManager): Unit {
    inputMethodManager.hideSoftInputFromWindow(
            currentFocus.windowToken,
            InputMethodManager.RESULT_UNCHANGED_SHOWN
    )
}

fun FragmentActivity.fullScreenMode(): Unit {
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
    )
}