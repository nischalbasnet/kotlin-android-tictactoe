package com.nbasnet.tictactoe.controllers

import com.nbasnet.tictactoe.models.PlayAreaInfo
import com.nbasnet.tictactoe.models.Player

class TicTacToeGameController(val player1: Player, val player2: Player) {
    private var _currentPlayer: Player = player1
    private var _gameFinished: Boolean = false

    fun getCurrentPlayer(): Player = _currentPlayer

    fun isGameFinished(): Boolean = _gameFinished

    private fun selectArea(areaInfo: PlayAreaInfo): GamePlayResponse {
        //check if the area is already selected
        if (player1.isSelected(areaInfo) || player2.isSelected(areaInfo) || _gameFinished) {
            val error = if (_gameFinished) "Game over winner is: ${_currentPlayer.name}"
            else "Area already selected"
            //return error
            return GamePlayResponse(0, error)
        } else {
            _currentPlayer.setSelectedArea(areaInfo)
            _gameFinished = _currentPlayer.isWinner()

            val message = if (_gameFinished) "Game over winner = ${_currentPlayer.name}"
            else "${_currentPlayer.name}'s turn over: selected ${areaInfo.row}x${areaInfo.column}"

            return GamePlayResponse(1, message)
        }
    }

    fun playNextRound(
            selectedAreaInfo: PlayAreaInfo,
            additionalProcess: (response: GamePlayResponse) -> Unit
    ): Unit {
        val selectResponse = selectArea(selectedAreaInfo)

        additionalProcess(selectResponse)
        if (selectResponse.success == 1) {
            goToNextPlayer()
        }
    }

    private fun goToNextPlayer(): Unit {
        //now set next player as the current player
        if (!_gameFinished) _currentPlayer = getNextPlayer()
    }

    fun getNextPlayer(): Player {
        return if (_currentPlayer == player1) player2
        else player1
    }

    fun isCurrentPlayer1(): Boolean {
        return _currentPlayer == player1
    }
}

data class GamePlayResponse(val success: Int, val message: String)