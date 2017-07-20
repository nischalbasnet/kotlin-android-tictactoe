package com.nbasnet.tictactoe.models

data class GameInfo(
        val player1Info: PlayerGameInfo,
        val player2Info: PlayerGameInfo
)

data class PlayerGameInfo(
        val player: Player,
        var win: Int = 0,
        var loss: Int = 0
)

data class PlayAreaInfo(val row: Int, val column: Int) {
    val isDiagonal: Boolean = (row == column || Math.abs(row - column) != 1)
}