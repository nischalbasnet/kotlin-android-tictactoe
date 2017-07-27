package com.nbasnet.tictactoe.controllers

import com.nbasnet.tictactoe.models.PlayAreaInfo
import com.nbasnet.tictactoe.models.Player

class TicTacToeGameController(val player1: Player, val player2: Player, val gridRow: Int) {
    var currentPlayer: Player = player1
        private set

    var isGameFinished: Boolean = false
        private set

    var currentRound: Int = 0
        private set

    val totalRounds = gridRow * gridRow

    lateinit var frontDiagonalRows: List<PlayAreaInfo>
        private set

    init {
        loadInitialValues()
    }

    private fun loadInitialValues(): Unit {
        val tmpDiagonalRows = mutableListOf<PlayAreaInfo>()
        for ((subDiagonal, i) in (1..gridRow).withIndex()) {
            tmpDiagonalRows.add(PlayAreaInfo(i, gridRow - subDiagonal))
        }

        frontDiagonalRows = tmpDiagonalRows.toList()
    }

    private fun selectArea(areaInfo: PlayAreaInfo): GamePlayResponse {
        //check if the area is already selected
        if (player1.isSelected(areaInfo) || player2.isSelected(areaInfo) || isGameFinished) {
            val error = if (isGameFinished) "Game over winner is: ${currentPlayer.name}"
            else "Area already selected"
            //return error
            return GamePlayResponse(false, error)
        } else {
            currentPlayer.setSelectedArea(areaInfo, this)
            currentRound++
            isGameFinished = currentPlayer.isWinner || currentRound >= totalRounds

            val message = if (isGameFinished) "Game over winner = ${currentPlayer.name}"
            else "${currentPlayer.name}'s turn over: selected ${areaInfo.row}x${areaInfo.column}"

            return GamePlayResponse(true, message)
        }
    }

    /**
     * Play the next round
     */
    fun playNextRound(
            selectedAreaInfo: PlayAreaInfo,
            onSuccess: (response: GamePlayResponse) -> Unit = {},
            onFail: (response: GamePlayResponse) -> Unit = {},
            onPlayerChange: (nextPlayer: Player) -> Unit = {},
            onGameFinished: () -> Unit = {}
    ): Unit {
        val selectResponse = selectArea(selectedAreaInfo)

        if (selectResponse.success) {
            onSuccess(selectResponse)
            goToNextPlayer()
            onPlayerChange(currentPlayer)
            if (isGameFinished) {
                onGameFinished()
            }
        } else {
            onFail(selectResponse)
        }
    }

    /**
     * Go to next player
     */
    private fun goToNextPlayer(): Unit {
        //now set next player as the current player
        if (!isGameFinished) currentPlayer = getNextPlayer()
    }

    /**
     * Get the next player
     */
    fun getNextPlayer(): Player {
        return if (currentPlayer == player1) player2
        else player1
    }

    /**
     * check and return if current player playing is player1
     */
    fun isCurrentPlayer1(): Boolean {
        return currentPlayer == player1
    }

    /**
     * Check and return if a game is draw
     */
    fun isDrawGame(): Boolean = if (isGameFinished) !currentPlayer.isWinner else false

    /**
     * Check if a area is playable
     */
    fun isAreaPlayable(selection: PlayAreaInfo): Boolean {
        return !(currentPlayer.isSelected(selection) || getNextPlayer().isSelected(selection))
    }

    /**
     * Checks and returns a single winning playareainfo for the next player
     */
    fun getNextPlayersWiningAreas(): List<PlayAreaInfo> {
        return if (isGameFinished) mutableListOf()
        else getNextPlayer().getWinningAreas(this)
    }

    fun getCurrentPlayersWinningAreas(): List<PlayAreaInfo> {
        return if (isGameFinished) mutableListOf()
        else currentPlayer.getWinningAreas(this)
    }

    var canHumanPlay: Boolean = true
}

data class GamePlayResponse(val success: Boolean, val message: String)