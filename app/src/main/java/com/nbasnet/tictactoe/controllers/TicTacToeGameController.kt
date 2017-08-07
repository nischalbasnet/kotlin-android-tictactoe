package com.nbasnet.tictactoe.controllers

import com.nbasnet.tictactoe.models.GameBoard
import com.nbasnet.tictactoe.models.PlayAreaInfo
import com.nbasnet.tictactoe.models.Player

data class TicTacToeGameController(val player1: Player, val player2: Player, val gridRow: Int, val startPlayer: Int) {
    var currentPlayer: Player = if (startPlayer == 2) player2 else player1
        private set

    var isGameFinished: Boolean = false
        private set

    var currentRound: Int = 0
        private set

    val totalRounds = gridRow * gridRow

    val gameBoard: GameBoard = GameBoard(gridRow)

    lateinit var frontDiagonalRows: List<PlayAreaInfo>
        private set

    init {
        loadInitialValues()
    }

    private fun loadInitialValues(): Unit {
        frontDiagonalRows = gameBoard.fullPlayRegion
                .filter { it.frontDiagonal }
                .map { it.area }
    }

    private fun selectArea(areaInfo: PlayAreaInfo): GamePlayResponse {
        //check if the area is already selected
//        if (player1.isSelected(areaInfo) || player2.isSelected(areaInfo) || isGameFinished) {
        if (gameBoard.isRegionSelected(areaInfo) || isGameFinished) {
            val error = if (isGameFinished) "Game over winner is: ${currentPlayer.name}"
            else "Area already selected"
            //return error
            return GamePlayResponse(false, error)
        } else {
            gameBoard.selectRegion(areaInfo, currentPlayer)
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
            onSuccess: (it: TicTacToeGameController, response: GamePlayResponse) -> Unit = { _, _ -> },
            onFail: (it: TicTacToeGameController, response: GamePlayResponse) -> Unit = { _, _ -> },
            onPlayerChange: (it: TicTacToeGameController, nextPlayer: Player) -> Unit = { _, _ -> },
            onGameFinished: (it: TicTacToeGameController) -> Unit = {}
    ): Unit {
        val selectResponse = selectArea(selectedAreaInfo)

        if (selectResponse.success) {
            onSuccess(this, selectResponse)
            goToNextPlayer()
            onPlayerChange(this, currentPlayer)
            if (isGameFinished) {
                onGameFinished(this)
            }
        } else {
            onFail(this, selectResponse)
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
    fun getNextPlayer(): Player = if (currentPlayer == player1) player2 else player1

    /**
     * check and return if current player playing is player1
     */
    fun isCurrentPlayer1(): Boolean = currentPlayer == player1


    fun isCurrentPlayer(player: Player): Boolean = player == currentPlayer

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