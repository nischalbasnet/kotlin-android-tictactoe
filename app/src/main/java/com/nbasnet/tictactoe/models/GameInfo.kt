package com.nbasnet.tictactoe.models

import android.databinding.ObservableInt

/**
 * Game info holder class
 */
data class GameInfo(
        val player1Info: PlayerGameInfo,
        val player2Info: PlayerGameInfo
)

/**
 * Player info holder class
 */
data class PlayerGameInfo(
        val player: Player,
        var win: ObservableInt = ObservableInt(0),
        var loss: Int = 0
) {
    /**
     * Functions required for properly binding with the view
     */
    fun winToString(): String = win.toString()

    fun lossToString(): String = loss.toString()
}

/**
 * Info about the single play area
 */
data class PlayAreaInfo(val row: Int, val column: Int) {
    val isDiagonal: Boolean = (row == column || Math.abs(row - column) != 1)
}