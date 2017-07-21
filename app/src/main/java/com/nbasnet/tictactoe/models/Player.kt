package com.nbasnet.tictactoe.models

import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.util.Log

data class Player(val name: String, val symbolResource: Int, val color: Int) {
    var isActive: Boolean = false
    private val _selectedAreas = mutableListOf<PlayAreaInfo>()
    var isWinner = false
        private set

    private val _scoreMap = hashMapOf<String, Int>()
    private var _drawableSymbol: Drawable? = null
    private var _attemptedForDrawableSymbol = false

    private val LOG_TAG = "Player"

    /**
     * Get the draw symbol for the player
     */
    fun getDrawableSymbol(resources: Resources, theme: Resources.Theme): Drawable? {
        if (!_attemptedForDrawableSymbol) {
            val drawable: Drawable? = ResourcesCompat.getDrawable(resources, symbolResource, theme)
            drawable?.setColorFilter(
                    resources.getColor(color),
                    PorterDuff.Mode.SRC_IN
            )
            _drawableSymbol = drawable
            _attemptedForDrawableSymbol = true
        }

        return _drawableSymbol
    }

    /**
     * Check and return if the area is selected by user
     */
    fun isSelected(areaInfo: PlayAreaInfo): Boolean = _selectedAreas.contains(areaInfo)

    /**
     * Set the selected area for user and perform calculation for win
     */
    fun setSelectedArea(selectArea: PlayAreaInfo): Player {
        _selectedAreas.add(selectArea)

        _scoreMap["row${selectArea.row}"] = (_scoreMap["row${selectArea.row}"] ?: 0) + 1
        isWinner = checkForWinner(_scoreMap["row${selectArea.row}"] ?: 0)

        _scoreMap["col${selectArea.column}"] = (_scoreMap["col${selectArea.column}"] ?: 0) + 1
        isWinner = checkForWinner(_scoreMap["col${selectArea.column}"] ?: 0)

        if (!isWinner && selectArea.isDiagonal) {
            if (selectArea.row == 2) {
                _scoreMap["backD"] = (_scoreMap["backD"] ?: 0) + 1
                _scoreMap["forwardD"] = (_scoreMap["forwardD"] ?: 0) + 1
            } else if (selectArea.row == selectArea.column) {
                _scoreMap["backD"] = (_scoreMap["backD"] ?: 0) + 1
            } else {
                _scoreMap["forwardD"] = (_scoreMap["forwardD"] ?: 0) + 1
            }
            isWinner = checkForWinner(_scoreMap["forwardD"] ?: 0)
            isWinner = checkForWinner(_scoreMap["backD"] ?: 0)
        }

        if (isWinner) Log.v(LOG_TAG, "$name won the game ${_selectedAreas.toString()}")

        return this
    }

    /**
     * Check if the player has won
     */
    private fun checkForWinner(value: Int): Boolean {
        return if (isWinner) isWinner else value > 2
    }
}