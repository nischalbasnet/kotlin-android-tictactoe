package com.nbasnet.tictactoe

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.nbasnet.extensions.activity.failToast
import com.nbasnet.extensions.activity.successToast
import com.nbasnet.extensions.activity.toast
import com.nbasnet.tictactoe.controllers.TicTacToeGameController
import com.nbasnet.tictactoe.databinding.TictactoeGameBinding
import com.nbasnet.tictactoe.models.GameInfo
import com.nbasnet.tictactoe.models.PlayAreaInfo
import com.nbasnet.tictactoe.models.Player
import com.nbasnet.tictactoe.models.PlayerGameInfo
import kotlinx.android.synthetic.main.tictactoe_game.*

class TicTacToeActivity : AppCompatActivity() {
    private lateinit var _fullGameInfo: GameInfo
    private lateinit var _gameController: TicTacToeGameController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tictactoe_game)

        title = resources.getString(R.string.title_game_room)

        val playersInfo = intent.getBundleExtra("extra")
        val inP1Name = playersInfo.getString("player1")
        val inP2Name = playersInfo.getString("player2")

        val _player1 = getPlayer(1, inP1Name)
        val _player2 = getPlayer(2, inP2Name)
        _fullGameInfo = GameInfo(
                PlayerGameInfo(_player1),
                PlayerGameInfo(_player2)
        )
        _gameController = TicTacToeGameController(_player1, _player2)

        val binding: TictactoeGameBinding = DataBindingUtil.setContentView(this, R.layout.tictactoe_game)
        binding.gameInfo = _fullGameInfo

        setWinnerCurrentPlayerLabels()

        btnArea11.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(1, 1)
            selectArea(it, btnAreaInfo)
        }
        btnArea12.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(1, 2)
            selectArea(it, btnAreaInfo)
        }
        btnArea13.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(1, 3)
            selectArea(it, btnAreaInfo)
        }

        btnArea21.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(2, 1)
            selectArea(it, btnAreaInfo)
        }
        btnArea22.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(2, 2)
            selectArea(it, btnAreaInfo)
        }
        btnArea23.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(2, 3)
            selectArea(it, btnAreaInfo)
        }

        btnArea31.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(3, 1)
            selectArea(it, btnAreaInfo)
        }
        btnArea32.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(3, 2)
            selectArea(it, btnAreaInfo)
        }
        btnArea33.setOnClickListener {
            val btnAreaInfo = PlayAreaInfo(3, 3)
            selectArea(it, btnAreaInfo)
        }
    }

    private fun getPlayer(playerNo: Int, name: String? = null): Player {
        return if (playerNo == 1)
            Player(name ?: "Nischal", R.drawable.tictactoe_cross_55dp, R.color.green_200)
        else
            Player(name ?: "Niluja", R.drawable.tictactoe_circle_55dp, R.color.blue_300)

    }

    private fun selectArea(area: View, areaInfo: PlayAreaInfo) {
//        val selectResponse = _gameController.selectArea(areaInfo)
//
//        if (selectResponse.success == 1) {
//            area.setBackgroundResource(_gameController.getCurrentPlayer().symbolResource)
//            toast(selectResponse.message, Toast.LENGTH_SHORT)
//            if (_gameController.isGameFinished()) {
//                successToast("Game over winner: ${_gameController.getCurrentPlayer().name}", Toast.LENGTH_SHORT)
//            }
//
//            _gameController.goToNextPlayer()
//            setWinnerCurrentPlayerLabels()
//        } else {
//            failToast(selectResponse.message, Toast.LENGTH_SHORT)
//        }

        _gameController.playNextRound(areaInfo, { (success, message) ->
            run {
                if (success == 1) {
                    val customUserSource = _gameController.getCurrentPlayer().getDrawableSymbol(resources, theme)
                    if (customUserSource == null) {
                        area.setBackgroundResource(_gameController.getCurrentPlayer().symbolResource)
                    } else {
                        area.background = customUserSource
                    }

                    toast(message, Toast.LENGTH_SHORT)
                    if (_gameController.isGameFinished()) {
                        successToast("Game over winner: ${_gameController.getCurrentPlayer().name}", Toast.LENGTH_SHORT)
                    }

                    setWinnerCurrentPlayerLabels()
                } else {
                    failToast(message, Toast.LENGTH_SHORT)
                }
            }
        })
    }

    private fun setWinnerCurrentPlayerLabels(): Unit {
        if (_gameController.isCurrentPlayer1()) {
            player1Active.visibility = View.VISIBLE
            player2Active.visibility = View.INVISIBLE
        } else {
            player1Active.visibility = View.INVISIBLE
            player2Active.visibility = View.VISIBLE
        }

        if (_gameController.isGameFinished()) {
            labelWinnerBanner.text = resources.getString(R.string.label_winner)
            labelWinner.text = _gameController.getCurrentPlayer().name
        }
    }
}
