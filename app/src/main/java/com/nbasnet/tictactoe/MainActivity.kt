package com.nbasnet.tictactoe

import com.nbasnet.extensions.activity.*
import android.app.DatePickerDialog
import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
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
                failToast("Please enter DOB.")
            } else if (inputPlayer1Name.text.toString().isBlank() || inputPlayer2Name.text.toString().isBlank()) {
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
                    startActivity(TicTacToeActivity::class.java, gameUsers)
                } else {
                    failToast("You need to be over 18 to play this game.")
                }
            }
        }
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
