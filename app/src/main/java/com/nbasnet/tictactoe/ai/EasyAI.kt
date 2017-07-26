package com.nbasnet.tictactoe.ai

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

    override fun playRound(gameController: TicTacToeGameController) {
        if (!gameController.isGameFinished) {
            val playArea = getNextPlayAreaInfo(gameController)
            gameController.playNextRound(playArea)
        }
    }

    override fun getNextPlayAreaInfo(gameController: TicTacToeGameController): PlayAreaInfo {
        val selection = currentSelection[currentSelectionIndex]

        if (currentSelectionIndex >= gameController.totalRounds || gameController.isAreaPlayable(selection)) {
            return selection
        } else {
            currentSelectionIndex++
            return getNextPlayAreaInfo(gameController)
        }
    }

    override fun play(selectedAreaInfo: PlayAreaInfo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun selectCurrentSelection(): List<PlayAreaInfo> {
        val randomKey = Random().nextInt(selections.count() - 0) + 0
        return selections[randomKey]
    }
}