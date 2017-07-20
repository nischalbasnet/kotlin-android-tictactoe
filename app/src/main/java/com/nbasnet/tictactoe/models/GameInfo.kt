package com.nbasnet.tictactoe.models

/**
 * Created by nbasnet on 7/19/17.
 */
data class GameInfo(
        val player1Info: PlayerGameInfo,
        val player2Info: PlayerGameInfo
)

data class PlayerGameInfo(
        val player: Player,
        var win: Int = 0,
        var loss: Int = 0
)