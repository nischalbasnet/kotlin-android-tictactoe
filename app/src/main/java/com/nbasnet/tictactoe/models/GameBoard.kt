package com.nbasnet.tictactoe.models

/**
 * Class for keeping gameboard info
 */
data class GameBoard(val gridRow: Int) {
    val fullPlayRegion: List<PlayRegionDetail>

    init {
        fullPlayRegion = generateFullPlayRegion(
                gridRow,
                generateFrontDiagonalRegion(gridRow)
        )
    }

    /**
     * Generate front diagonal region map from given max rows
     */
    private fun generateFrontDiagonalRegion(maxRow: Int): Map<Int, Int> {
        val tmpDiagonalRows = mutableMapOf<Int, Int>()
        for ((subDiagonal, i) in (1..maxRow).withIndex()) {
            tmpDiagonalRows[i] = maxRow - subDiagonal
        }
        return tmpDiagonalRows.toMap()
    }

    /**
     * Generate full pay regions for the board from given max rows and diagonal row map info
     */
    private fun generateFullPlayRegion(maxRow: Int, frontDiagonalRows: Map<Int, Int>): List<PlayRegionDetail> {
        val tmpFullPlayRegion = mutableListOf<PlayRegionDetail>()
        for (row in 1..maxRow) {
            for (col in 1..maxRow) {
                val playArea = PlayAreaInfo(row, col)

                var isDiagonal = false
                var frontDiagonal = false
                var backDiagonal = false

                if (row == col) {
                    isDiagonal = true
                    backDiagonal = true
                }
                if (frontDiagonalRows[row] == col) {
                    isDiagonal = true
                    frontDiagonal = true
                }

                tmpFullPlayRegion.add(PlayRegionDetail(
                        area = playArea,
                        isDiagonal = isDiagonal,
                        frontDiagonal = frontDiagonal,
                        backDiagonal = backDiagonal
                ))
            }
        }

        return tmpFullPlayRegion.toList()
    }

    /**
     * Select a region from board for given player
     */
    fun selectRegion(selectedArea: PlayAreaInfo, player: Player): GameBoard {
        val boardRegion = fullPlayRegion.firstOrNull {
            it.area == selectedArea
        } ?: throw Exception("Selected region with row = ${selectedArea.row} and column = ${selectedArea.column} " +
                "is not present in the GameBoard")

        boardRegion.selectedUser = player

        return this
    }

    /**
     * Check if region is already selected
     */
    fun isRegionSelected(region: PlayAreaInfo): Boolean {
        return fullPlayRegion.firstOrNull { it.area == region }?.selectedUser != null
    }

    fun getPlayedRegions(): List<PlayRegionDetail> = fullPlayRegion.filter { it.selectedUser != null }
    fun getOpenRegions(): List<PlayRegionDetail> = fullPlayRegion.filter { it.selectedUser == null }

}

/**
 * Class containing full info about a single play region
 */
data class PlayRegionDetail(
        val area: PlayAreaInfo,
        val isDiagonal: Boolean,
        val frontDiagonal: Boolean,
        val backDiagonal: Boolean
) {
    var selectedUser: Player? = null
            //region can be selected only once
        set(player) {
            if (field == null) field = player
        }
}