package com.nbasnet.tictactoe.ai

import com.nbasnet.tictactoe.controllers.TicTacToeGameController
import com.nbasnet.tictactoe.models.PlayAreaInfo

/**
 * Created by nbasnet on 7/25/17.
 */
interface IPlayGameAI {
    fun playRound(gameController: TicTacToeGameController)
    fun play(selectedAreaInfo: PlayAreaInfo)
    fun getNextPlayAreaInfo(gameController: TicTacToeGameController): PlayAreaInfo
}

object AIFactory {
    fun getAIPlayer(
            aiPlayerTypes: AIPlayerTypes
    ): IPlayGameAI {
        val aiPlayer = when (aiPlayerTypes) {
            AIPlayerTypes.EASY -> EasyAI()
            AIPlayerTypes.HARD -> throw Exception("AI of type $aiPlayerTypes is not created")
        }

        return aiPlayer;
    }
}

enum class AIPlayerTypes {
    EASY, HARD
}