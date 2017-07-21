package com.nbasnet.tictactoe.controllers

import com.nbasnet.tictactoe.models.PlayAreaInfo
import com.nbasnet.tictactoe.models.Player

class TicTacToeGameController(val player1: Player, val player2: Player) {
    var currentPlayer: Player = player1
        private set

    var isGameFinished: Boolean = false
        private set

    private fun selectArea(areaInfo: PlayAreaInfo): GamePlayResponse {
        //check if the area is already selected
        if (player1.isSelected(areaInfo) || player2.isSelected(areaInfo) || isGameFinished) {
            val error = if (isGameFinished) "Game over winner is: ${currentPlayer.name}"
            else "Area already selected"
            //return error
            return GamePlayResponse(false, error)
        } else {
            currentPlayer.setSelectedArea(areaInfo)
            isGameFinished = currentPlayer.isWinner

            val message = if (isGameFinished) "Game over winner = ${currentPlayer.name}"
            else "${currentPlayer.name}'s turn over: selected ${areaInfo.row}x${areaInfo.column}"

            return GamePlayResponse(true, message)
        }
    }

    fun playNextRound(
            selectedAreaInfo: PlayAreaInfo,
            additionalProcess: (response: GamePlayResponse) -> Unit
    ): Unit {
        val selectResponse = selectArea(selectedAreaInfo)

        additionalProcess(selectResponse)
        if (selectResponse.success) {
            goToNextPlayer()
        }
    }

    private fun goToNextPlayer(): Unit {
        //now set next player as the current player
        if (!isGameFinished) currentPlayer = getNextPlayer()
    }

    fun getNextPlayer(): Player {
        return if (currentPlayer == player1) player2
        else player1
    }

    fun isCurrentPlayer1(): Boolean {
        return currentPlayer == player1
    }
}

data class GamePlayResponse(val success: Boolean, val message: String)