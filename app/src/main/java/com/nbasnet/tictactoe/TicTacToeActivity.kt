package com.nbasnet.tictactoe

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.nbasnet.extensions.activity.failToast
import com.nbasnet.extensions.activity.successToast
import com.nbasnet.extensions.activity.toast
import com.nbasnet.tictactoe.controllers.TicTacToeGameController
import com.nbasnet.tictactoe.databinding.TictactoeGameBinding
import com.nbasnet.tictactoe.models.*
import kotlinx.android.synthetic.main.tictactoe_game.*

class TicTacToeActivity : AppCompatActivity() {
    private lateinit var _fullGameInfo: GameInfo
    private lateinit var _gameController: TicTacToeGameController

    private lateinit var buttonList: List<BtnAreaInfo>
    private var _gridRow: Int = 3
    private lateinit var _frontDiagonalRows: Map<Int, Int>

    /**
     * View oncreate function
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tictactoe_game)

        _gridRow = 3

        //fill diagonal row
        val tmpDiagonalRows = mutableMapOf<Int, Int>()
        for ((subDiagonal, i) in (1.._gridRow).withIndex()) {
            tmpDiagonalRows[i] = _gridRow - subDiagonal
        }
        _frontDiagonalRows = tmpDiagonalRows.toMap()

        title = resources.getString(R.string.title_game_room)
        //retrieve the users info from the payload passed to the view
        val playersInfo = intent.getBundleExtra("payload")
        val inP1Name = playersInfo.getString("player1")
        val inP2Name = playersInfo.getString("player2")

        //generate player classes and create the game info and controller classes
        val _player1 = getPlayer(1, inP1Name)
        val _player2 = getPlayer(2, inP2Name)
        _fullGameInfo = GameInfo(
                PlayerGameInfo(_player1),
                PlayerGameInfo(_player2)
        )
        _gameController = TicTacToeGameController(_player1, _player2)

        //Bind the game info with the tictactoe_game view
        val binding: TictactoeGameBinding = DataBindingUtil.setContentView(this, R.layout.tictactoe_game)
        binding.gameInfo = _fullGameInfo
        //set label for the indication of current player
        setWinnerCurrentPlayerLabels()

        //List to hold the info about all the playable region
//        buttonList = listOf<BtnAreaInfo>(
//                BtnAreaInfo(1, 1, btnArea11),
//                BtnAreaInfo(1, 2, btnArea12),
//                BtnAreaInfo(1, 3, btnArea13),
//                BtnAreaInfo(2, 1, btnArea21),
//                BtnAreaInfo(2, 2, btnArea22),
//                BtnAreaInfo(2, 3, btnArea23),
//                BtnAreaInfo(3, 1, btnArea31),
//                BtnAreaInfo(3, 2, btnArea32),
//                BtnAreaInfo(3, 3, btnArea33)
//        )

        buttonList = getAllPlayAreas(gameArea)
        /**
         * Add event handlers for all the button
         */
        buttonList.forEach {
            val btnInfo = it
            it.button.setOnClickListener {
                val btnAreaInfo = PlayAreaInfo(btnInfo.row, btnInfo.col)
                selectArea(it, btnAreaInfo)
            }
        }
    }

    /**
     * Get all playable button info
     */
    private fun getAllPlayAreas(viewGroup: ViewGroup): List<BtnAreaInfo> {
        val playAreas = arrayListOf<BtnAreaInfo>()
        findButtons(viewGroup, playAreas, 0)
        return playAreas.toList()
    }

    /**
     * Get all the buttons from the view group and fill it up in fillButtonList variable
     */
    private fun findButtons(viewGroup: ViewGroup, fillButtonList: ArrayList<BtnAreaInfo>, viewRow: Int) {
        var row = viewRow
        for (col in 0..viewGroup.childCount) {
            val child = viewGroup.getChildAt(col)

            if (child is ViewGroup) {
                row++
                findButtons(child, fillButtonList, row)
            } else if (child is Button) {
                val actualCol = col + 1
                val btnInfo = BtnAreaInfo(viewRow, actualCol, child)
                fillButtonList.add(btnInfo)
//                child.text = "row=$row col=$actualCol"
            }
        }
    }

    /**
     * Stub method to generated and return player
     */
    private fun getPlayer(playerNo: Int, name: String? = null): Player {
        return if (playerNo == 1)
            Player(name ?: "Nischal", R.drawable.tictactoe_cross_55dp, R.color.green_200)
        else
            Player(name ?: "Niluja", R.drawable.tictactoe_circle_55dp, R.color.blue_300)

    }

    /**
     * Perform the task for region selected with given areaInfo
     */
    private fun selectArea(area: View, areaInfo: PlayAreaInfo) {
        var playSuccess = false
        //play the next round and update the view with the proper values
        _gameController.playNextRound(areaInfo, { (success, message) ->
            run {
                playSuccess = success
                if (success) {
                    val customUserSource = _gameController.currentPlayer.getDrawableSymbol(resources, theme)
                    if (customUserSource == null) {
                        area.setBackgroundResource(_gameController.currentPlayer.symbolResource)
                    } else {
                        area.background = customUserSource
                    }

                    YoYo.with(Techniques.Pulse)
                            .duration(700)
                            .playOn(area)

                    toast(message, Toast.LENGTH_SHORT)
                    if (_gameController.isGameFinished) {
                        successToast("Game over winner: ${_gameController.currentPlayer.name}", Toast.LENGTH_SHORT)
                    }

                } else {
                    failToast(message, Toast.LENGTH_SHORT)
                }
            }
        })

        if (playSuccess) setWinnerCurrentPlayerLabels()
    }

    /**
     * Define animations
     */
    val slideOutLeftAnim: YoYo.AnimationComposer = YoYo.with(Techniques.SlideOutLeft).duration(700)
    val slideInLeftAnim: YoYo.AnimationComposer = YoYo.with(Techniques.SlideInLeft).duration(700)
    val slideOutRightAnim: YoYo.AnimationComposer = YoYo.with(Techniques.SlideOutRight).duration(700)
    val slideInRightAnim: YoYo.AnimationComposer = YoYo.with(Techniques.SlideInRight).duration(700)
    /**
     * Method to update the current player indicating icon and winner name
     */
    private fun setWinnerCurrentPlayerLabels(): Unit {

        if (_gameController.isGameFinished) {
            labelWinnerBanner.text = resources.getString(R.string.label_winner)
            labelWinner.text = _gameController.currentPlayer.name
            YoYo.with(Techniques.Tada)
                    .duration(1000)
                    .repeat(3)
                    .playOn(labelWinner)

            YoYo.with(Techniques.Wobble)
                    .duration(1000)
                    .repeat(3)
                    .playOn(if (_gameController.isCurrentPlayer1()) player1Active else player2Active)

            winningRegionAnimations(_gameController.currentPlayer.winningRow())
        } else {
            if (_gameController.isCurrentPlayer1()) {
                slideOutLeftAnim.playOn(player2Active)
                slideInRightAnim.playOn(player1Active)
            } else {
                slideOutRightAnim.playOn(player1Active)
                slideInLeftAnim.playOn(player2Active)
            }
        }
    }

    /**
     * Handles winning region animation
     */
    private fun winningRegionAnimations(winningRegionInfo: WinningRegionInfo): Unit {
        val waveAnimation = YoYo.with(Techniques.RubberBand).duration(1000).repeat(3)

        if (winningRegionInfo.regionType == RegionType.FORWARD_DIAGONAL) {
            buttonList.forEach {
                if (_frontDiagonalRows[it.row] == it.col) {
                    waveAnimation.playOn(it.button)
                }
            }
        } else if (winningRegionInfo.regionType == RegionType.BACK_DIAGONAL) {
            buttonList.forEach {
                if (it.col == it.row) {
                    waveAnimation.playOn(it.button)
                }
            }
        } else if (winningRegionInfo.regionType == RegionType.COL) {
            buttonList.forEach {
                if (it.col == winningRegionInfo.rowCol) {
                    waveAnimation.playOn(it.button)
                }
            }
        } else if (winningRegionInfo.regionType == RegionType.ROW) {
            buttonList.forEach {
                if (it.row == winningRegionInfo.rowCol) {
                    waveAnimation.playOn(it.button)
                }
            }
        }
    }
}

data class BtnAreaInfo(val row: Int, val col: Int, val button: Button)