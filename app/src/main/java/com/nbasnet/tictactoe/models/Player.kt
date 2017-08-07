package com.nbasnet.tictactoe.models

import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import com.nbasnet.tictactoe.ai.IPlayGameAI
import com.nbasnet.tictactoe.controllers.TicTacToeGameController

data class Player(
        val name: String,
        val symbolResource: Int,
        val color: Int,
        val aIController: IPlayGameAI? = null
) {
    val maxRow: Int = 3
    val isAI: Boolean = aIController != null
    private val _selectedAreas = mutableListOf<PlayAreaInfo>()
    var isWinner = false
        private set

    private val _scoreMap = hashMapOf<String, Int>()

    private val LOG_TAG = "Player"

    /**
     * Get the draw symbol for the player
     * Always returns a new drawable
     */
    fun getDrawableSymbol(resources: Resources, theme: Resources.Theme): Drawable? {
        val drawable: Drawable? = ResourcesCompat.getDrawable(resources, symbolResource, theme)
        drawable?.setColorFilter(
                resources.getColor(color),
                PorterDuff.Mode.SRC_IN
        )
        return drawable
    }

    /**
     * Check and return if the area is selected by user
     */
    fun isSelected(areaInfo: PlayAreaInfo): Boolean = _selectedAreas.contains(areaInfo)

    /**
     * Set the selected area for user and perform calculation for win
     */
    fun setSelectedArea(selectArea: PlayAreaInfo, gameController: TicTacToeGameController): Player {
        _selectedAreas.add(selectArea)

        _scoreMap["rowx${selectArea.row}"] = (_scoreMap["rowx${selectArea.row}"] ?: 0) + 1
        isWinner = checkForWinner(_scoreMap["rowx${selectArea.row}"] ?: 0)

        _scoreMap["colx${selectArea.column}"] = (_scoreMap["colx${selectArea.column}"] ?: 0) + 1
        isWinner = checkForWinner(_scoreMap["colx${selectArea.column}"] ?: 0)

        if (!isWinner && selectArea.isDiagonal) {
            if (selectArea.row == selectArea.column) {
                //is a back diagonal
                _scoreMap["backD"] = (_scoreMap["backD"] ?: 0) + 1
                isWinner = checkForWinner(_scoreMap["backD"] ?: 0)
            }
            //check for front diagonal
            if (gameController.frontDiagonalRows.contains(PlayAreaInfo(selectArea.row, selectArea.column))) {
                //is a front diagonal point
                _scoreMap["forwardD"] = (_scoreMap["forwardD"] ?: 0) + 1
                isWinner = checkForWinner(_scoreMap["forwardD"] ?: 0)
            }
        }

        if (isWinner) Log.v(LOG_TAG, "$name won the game ${_selectedAreas.toString()}")

        return this
    }

    /**
     * Return the winning row or column or diagonal
     */
    fun winningRow(): FullRegionInfo {
        var returnRegionInfo = FullRegionInfo(0, RegionType.NONE)
        for ((regionInfo, score) in _scoreMap) {
            if (score > maxRow - 1) {
                returnRegionInfo = FullRegionInfo.getFromPlayerScoreMap(regionInfo)
            }
        }

        return returnRegionInfo
    }

    /**
     * Check if the player has won
     */
    private fun checkForWinner(value: Int): Boolean {
        return if (isWinner) isWinner else value > maxRow - 1
    }

    /**
     * Get the winning areas for player
     */
    fun getWinningAreas(gameController: TicTacToeGameController): List<PlayAreaInfo> {
        val winningRegion = mutableListOf<PlayAreaInfo>()
        val rowColSeq = 1..maxRow
        for ((regionInfo, score) in _scoreMap) {
            if (score == maxRow - 1) {
                //this region has winning area get the winning point now
                val fullRegionInfo = FullRegionInfo.getFromPlayerScoreMap(regionInfo)
                if (fullRegionInfo.regionType == RegionType.COL) {
                    val winCol = fullRegionInfo.rowCol
                    val selectedRows: List<Int> = _selectedAreas
                            .filter { it.column == winCol }
                            .map { it.row }

                    val winRow: Int? = rowColSeq.subtract(selectedRows).firstOrNull()
                    //add if winning row is present
                    if (winRow != null) {
                        val playArea = PlayAreaInfo(winRow, winCol)
                        if (gameController.isAreaPlayable(playArea)) winningRegion.add(playArea)
                    }
                } else if (fullRegionInfo.regionType == RegionType.ROW) {
                    val winRow = fullRegionInfo.rowCol
                    val selectedCols: List<Int> = _selectedAreas
                            .filter { it.row == winRow }
                            .map { it.column }

                    val winCol: Int? = rowColSeq.subtract(selectedCols).firstOrNull()
                    //add if winning col is present
                    if (winCol != null) {
                        val playArea = PlayAreaInfo(winRow, winCol)
                        if (gameController.isAreaPlayable(playArea)) winningRegion.add(playArea)
                    }
                } else if (fullRegionInfo.regionType == RegionType.BACK_DIAGONAL) {
                    val selectedRows = _selectedAreas
                            .filter { it.row == it.column }
                            .map { it.row }

                    val winRowCol: Int? = rowColSeq.subtract(selectedRows).firstOrNull()
                    //add if winning col is present
                    if (winRowCol != null) {
                        val playArea = PlayAreaInfo(winRowCol, winRowCol)
                        if (gameController.isAreaPlayable(playArea)) winningRegion.add(playArea)
                    }
                } else if (fullRegionInfo.regionType == RegionType.FORWARD_DIAGONAL) {
                    //TODO implement this
                    val selectRegions = gameController.frontDiagonalRows.subtract(_selectedAreas)
                            .filter { gameController.isAreaPlayable(it) }
                    winningRegion.addAll(selectRegions)
                }
            }
        }
        return winningRegion
    }
}

data class FullRegionInfo(val rowCol: Int, val regionType: RegionType) {
    companion object {
        fun getFromPlayerScoreMap(scoreKey: String): FullRegionInfo {
            val region: RegionType
            val rowCol: Int

            if (scoreKey == "forwardD") {
                region = RegionType.FORWARD_DIAGONAL
                rowCol = 0
            } else if (scoreKey == "backD") {
                region = RegionType.BACK_DIAGONAL
                rowCol = 0
            } else {
                val regionContents = scoreKey.split("x")
                region = if (regionContents.first() == "row") RegionType.ROW else RegionType.COL
                rowCol = regionContents.last().toInt()
            }

            return FullRegionInfo(rowCol, region)
        }
    }
}

enum class RegionType {
    COL, ROW, FORWARD_DIAGONAL, BACK_DIAGONAL, NONE
}