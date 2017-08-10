package com.nbasnet.helpers

import android.content.res.AssetManager
import android.graphics.Typeface

/**
 * Helper class to handle the font
 */
object FontManager {
    private val pathRoot = "fonts"
    val samuraiFontPath = "$pathRoot/samurai.ttf"
    val kalamFontPath = "$pathRoot/kalam_regular.ttf"

    private var currentCustomFont: Typeface? = null

    fun getCustomFontForLanguage(assets: AssetManager, language: String, forceLoad: Boolean = false): Typeface {
//        if (currentCustomFont == null || forceLoad) {
        if (currentCustomFont == null) {
            currentCustomFont = when (language) {
                "nepali" -> getFont(assets, FontManager.kalamFontPath)
                else -> getFont(assets, FontManager.kalamFontPath)
            }
        }

        return currentCustomFont!!
    }

    fun getFont(assets: AssetManager, fontPath: String): Typeface {
        return Typeface.createFromAsset(assets, fontPath)
    }
}