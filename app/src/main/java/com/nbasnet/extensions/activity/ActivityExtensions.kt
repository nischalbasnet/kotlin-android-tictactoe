package com.nbasnet.extensions.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import es.dmoral.toasty.Toasty

/**
 * Adds toast extension
 */
fun AppCompatActivity.toast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toasty.info(this, message, duration).show()
}

fun AppCompatActivity.failToast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toasty.error(this, message, duration).show()
}

fun AppCompatActivity.successToast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toasty.success(this, message, duration).show()
}

fun AppCompatActivity.startActivity(cls: Class<*>, payLoad: Bundle? = null): Unit {
    val intent = Intent(this, cls)

    if (payLoad != null) intent.putExtra("payload", payLoad)

    startActivity(intent)
}