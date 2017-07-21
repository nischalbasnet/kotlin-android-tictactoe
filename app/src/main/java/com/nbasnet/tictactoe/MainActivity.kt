package com.nbasnet.tictactoe

import com.nbasnet.extensions.activity.*
import android.app.DatePickerDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.Years
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    var _year: Int = 0
    var _month: Int = 0
    var _day: Int = 0
    val DATE_PICKER_DIALOG_ID = 0

    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = resources.getString(R.string.app_name)

        //fill current date
        val currentDate = DateTime.now()
        _year = currentDate.year
        _month = currentDate.monthOfYear
        _day = currentDate.dayOfMonth

        inputDOB.isFocusable = false
        inputDOB.isCursorVisible = false

        inputDOB.setOnClickListener {
            showDialog(DATE_PICKER_DIALOG_ID)
        }

        buttonPlayGame.setOnClickListener {
            if (inputDOB.text.isEmpty()) {
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(inputDOB)

                failToast("Please enter DOB.")
            } else if (inputPlayer1Name.text.toString().isBlank() || inputPlayer2Name.text.toString().isBlank()) {
                if (inputPlayer1Name.text.toString().isBlank()) {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(inputPlayer1Name)
                } else {
                    YoYo.with(Techniques.Shake)
                            .duration(700)
                            .playOn(inputPlayer2Name)
                }
                failToast("Please enter player names")
            } else {
                val userEnteredDOB = DateTime.parse(inputDOB.text.toString(), formatter)
                //calculate the age
                val age = Years.yearsBetween(userEnteredDOB, currentDate)

                if (age != null) {
                    labelAgeResult.text = age.years.toString()

                    val gameUsers = Bundle()
                    gameUsers.putString("player1", inputPlayer1Name.text.toString())
                    gameUsers.putString("player2", inputPlayer2Name.text.toString())

                    YoYo.with(Techniques.Hinge)
                            .duration(1000)
                            .onEnd {
                                startActivity(TicTacToeActivity::class.java, gameUsers)
                            }
                            .playOn(buttonPlayGame)
                } else {
                    failToast("You need to be over 18 to play this game.")
                }
            }
        }
    }

    /**
     * Perform action when the view resumes
     */
    override fun onPostResume() {
        super.onPostResume()
        YoYo.with(Techniques.SlideInUp)
                .duration(1000)
                .playOn(buttonPlayGame)
    }

    private val datePickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener {
        datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
        run {
            _year = year
            _month = monthOfYear
            _day = dayOfMonth

            val selectedDate = DateTime(_year, _month, _day, 0, 0, 0)
            inputDOB.setText(selectedDate.toString(formatter))
        }
    }

    override fun onCreateDialog(dialogId: Int): Dialog? {
        if (dialogId == DATE_PICKER_DIALOG_ID) {
            return DatePickerDialog(this, datePickerListener, _year, _month, _day)
        }

        return null
    }
}
