package com.nbasnet.extensions.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.nbasnet.tictactoe.R

/**
 * Adds toast extension
 */
fun AppCompatActivity.toast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    Toast.makeText(this, message, duration).show()
}

fun AppCompatActivity.failToast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    val toast = Toast.makeText(this, message, duration)
    toast.view.setBackgroundColor(resources.getColor(R.color.red_400))
    toast.show()
}

fun AppCompatActivity.successToast(message: String, duration: Int = Toast.LENGTH_LONG): Unit {
    val toast = Toast.makeText(this, message, duration)
    toast.view.setBackgroundColor(resources.getColor(R.color.green_400))
    toast.show()
}

fun AppCompatActivity.startActivity(cls: Class<*>, payLoad: Bundle? = null): Unit {
    val intent = Intent(this, cls)

    if (payLoad != null) intent.putExtra("extra", payLoad)

    startActivity(intent)
}