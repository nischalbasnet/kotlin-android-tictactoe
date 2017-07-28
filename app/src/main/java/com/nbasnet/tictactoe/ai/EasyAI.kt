package com.nbasnet.tictactoe.ai

import android.util.Log
import com.nbasnet.tictactoe.controllers.TicTacToeGameController
import com.nbasnet.tictactoe.models.PlayAreaInfo
import java.util.*

class EasyAI : IPlayGameAI {
    private val selections = listOf<List<PlayAreaInfo>>(
            listOf(
                    PlayAreaInfo(1, 1),
                    PlayAreaInfo(2, 2),
                    PlayAreaInfo(3, 3),
                    PlayAreaInfo(1, 3),
                    PlayAreaInfo(3, 1),
                    PlayAreaInfo(2, 3),
                    PlayAreaInfo(3, 2),
                    PlayAreaInfo(1, 2),
                    PlayAreaInfo(2, 1)
            ),
            listOf(
                    PlayAreaInfo(2, 2),
                    PlayAreaInfo(3, 3),
                    PlayAreaInfo(2, 1),
                    PlayAreaInfo(1, 1),
                    PlayAreaInfo(3, 1),
                    PlayAreaInfo(1, 2),
                    PlayAreaInfo(3, 2),
                    PlayAreaInfo(1, 3),
                    PlayAreaInfo(2, 3)

            )
    )

    private var currentSelection: List<PlayAreaInfo> = selectCurrentSelection()
    private var currentSelectionIndex: Int = 0

    override fun playRound(gameController: TicTacToeGameController): Unit {
        if (!gameController.isGameFinished) {
            val playArea: PlayAreaInfo
            //check if next player has winner region
            val nextUsersWinningRegion = gameController.getNextPlayersWiningAreas()
                    .firstOrNull()

            Log.d("AI", "next player winning region is $nextUsersWinningRegion")
            if (nextUsersWinningRegion != null) {
                playArea = nextUsersWinningRegion
            } else {
                //check my winning region or get the next play area to play
                playArea = gameController.getCurrentPlayersWinningAreas()
                        .firstOrNull() ?: getNextPlayAreaInfo(gameController)
            }

            gameController.playNextRound(playArea)
        }
    }

    override fun getNextPlayAreaInfo(gameController: TicTacToeGameController): PlayAreaInfo {
        val playArea: PlayAreaInfo
        //check if player has winner region take
        val currentPlayerWinningRegion = gameController.getCurrentPlayersWinningAreas()
                .firstOrNull()

        Log.d("AI", "next player winning region is $currentPlayerWinningRegion")
        if (currentPlayerWinningRegion != null) {
            playArea = currentPlayerWinningRegion
        } else {
            //check next players winning region or get the next play area to play
            playArea = gameController.getNextPlayersWiningAreas()
                    .firstOrNull() ?: getPlayAreaFromSelection(gameController)
        }

        return playArea
    }

    private fun getPlayAreaFromSelection(gameController: TicTacToeGameController): PlayAreaInfo {
        val selection = currentSelection[currentSelectionIndex]

        if (currentSelectionIndex >= gameController.totalRounds || gameController.isAreaPlayable(selection)) {
            return selection
        } else {
            if (currentSelectionIndex <= gameController.totalRounds) currentSelectionIndex++
            return getPlayAreaFromSelection(gameController)
        }
    }

    private fun selectCurrentSelection(): List<PlayAreaInfo> {
        val randomKey = Random().nextInt(selections.count() - 0) + 0
        return selections[randomKey]
    }
}