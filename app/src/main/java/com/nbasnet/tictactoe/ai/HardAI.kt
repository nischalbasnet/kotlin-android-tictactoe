package com.nbasnet.tictactoe.ai

import com.nbasnet.tictactoe.controllers.TicTacToeGameController
import com.nbasnet.tictactoe.models.PlayAreaInfo
import com.nbasnet.tictactoe.models.PlayRegionDetail
import com.nbasnet.tictactoe.models.Player

/**
 * Created by nbasnet on 7/29/17.
 * AI Implemented using minimax algorithm
 */
class HardAI(val player: Player) : IPlayGameAI {
    override fun playRound(gameController: TicTacToeGameController) {
        //TODO complete game play
    }

    override fun getNextPlayAreaInfo(gameController: TicTacToeGameController): PlayAreaInfo {
        //get the current sate of the board
        val currentState = gameController.copy()

        //now create tree for each state and get the final score value for each end state of node
        //me win = 10
        //draw = 0
        //opponent win = -10
        val minmaxTree = MinMaxTree(gameController, player)


        //TODO return proper value
        return PlayAreaInfo(-1, -1)
    }
}

class MinMaxTree(
        val controllerState: TicTacToeGameController,
        val roundPlayer: Player
) {
    val choices: RegionChoiceRootNode = RegionChoiceRootNode()

    init {
        //fill the choice tree
        val parentNode = RegionChoiceNode(
                PlayAreaInfo(0, 0),
                null,
                RegionChoiceNode.NO_SCORE
        )
        fillChoiceTree(
                controllerState,
                parentNode
        )

        choices.nodes = parentNode.getChildrens()

        //get all the initial playable regions

        //for each playable region
    }

    private fun fillChoiceTree(currentState: TicTacToeGameController, parentNode: RegionChoiceNode): Boolean {
        val playableRegions = currentState.gameBoard.getOpenRegions()

        playableRegions.forEach {
            val playArea = it.area
            var score = RegionChoiceNode.NO_SCORE

            currentState.playNextRound(
                    playArea,
                    onGameFinished = {
                        if (it.isGameFinished) {
                            score = if (it.isDrawGame())
                                RegionChoiceNode.DRAW_SCORE
                            else if (it.isCurrentPlayer(roundPlayer))
                                RegionChoiceNode.WIN_SCORE
                            else
                                RegionChoiceNode.LOSE_SCORE
                        }
                    })

            val childNode = RegionChoiceNode(
                    playArea,
                    parentNode,
                    score
            )
            parentNode.addChildren(childNode)

            if (score == RegionChoiceNode.NO_SCORE)
            //call for more choices is no score this play
                fillChoiceTree(currentState, childNode)
            else
            //finished
                return true
        }

        return true
    }

    private fun playAndGetOptimalPosition(currentState: TicTacToeGameController, region: PlayRegionDetail): Unit {

    }
}

class RegionChoiceRootNode {
    lateinit var nodes: List<RegionChoiceNode>
}

data class RegionChoiceNode(
        val area: PlayAreaInfo,
        val parent: RegionChoiceNode?,
        val score: Int = RegionChoiceNode.NO_SCORE
) {
    companion object {
        val NO_SCORE = -1
        val WIN_SCORE = 10
        val DRAW_SCORE = 0
        val LOSE_SCORE = -10
    }

    private lateinit var childrens: MutableList<RegionChoiceNode>

    fun getChildrens(): List<RegionChoiceNode> = childrens

    fun getFinalScore(): Int = 0

    fun addChildren(children: RegionChoiceNode): RegionChoiceNode {
        childrens.add(children)
        return this
    }

    fun isHead(): Boolean = parent == null
    fun isTail(): Boolean = childrens.isEmpty()
}