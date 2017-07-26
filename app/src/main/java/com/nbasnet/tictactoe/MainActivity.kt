package com.nbasnet.tictactoe

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.nbasnet.extensions.activity.failToast
import com.nbasnet.extensions.activity.getInputMethodManager
import com.nbasnet.extensions.activity.hideKeyboard
import com.nbasnet.extensions.activity.startActivity
import com.nbasnet.helpers.AppPreferences
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.DateTime
import org.joda.time.Years
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {
    var _year: Int = 0
    var _month: Int = 0
    var _day: Int = 0
    val DATE_PICKER_DIALOG_ID = 0
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy")

    lateinit var _sharePreference: AppPreferences

    /**
     * App strings constants
     */
    val APP_LANGUAGE_POS = "APP_LANGUAGE_POS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _sharePreference = AppPreferences(this)
        //set the language and local resource
        val selectedLanguagePos = _sharePreference.preference.getInt(APP_LANGUAGE_POS, 0)
        val savedAge = _sharePreference.preference.getInt("age", 0)
        changeResourceLocal(selectedLanguagePos)
        setContentView(R.layout.activity_main)

        title = resources.getString(R.string.app_name)

        //fill select language list
        val languageList = ArrayAdapter.createFromResource(
                this,
                R.array.languages_list,
                R.layout.support_simple_spinner_dropdown_item
        )
        selectLanguage.prompt = resources.getString(R.string.label_languages)
        selectLanguage.adapter = languageList
        selectLanguage.setSelection(selectedLanguagePos)
        //change the language
        changeLanguage.setOnClickListener {
            changeResourceLocal(selectLanguage.selectedItemPosition)
            recreate()
        }

        //fill current date
        val currentDate = DateTime.now()
        _year = currentDate.year
        _month = currentDate.monthOfYear
        _day = currentDate.dayOfMonth

        //set up date of birth field
        if (savedAge < 5) {
            inputDOB.isFocusable = false
            inputDOB.isCursorVisible = false

            inputDOB.setOnClickListener {
                showDialog(DATE_PICKER_DIALOG_ID)
            }
        } else {
            inputDOB.visibility = View.INVISIBLE
        }

        //set play game on click listener
        buttonPlayGame.setOnClickListener {
            // hide virtual keyboard
            hideKeyboard(getInputMethodManager())

            if (savedAge < 5 && inputDOB.text.isEmpty()) {
                YoYo.with(Techniques.Shake)
                        .duration(700)
                        .playOn(inputDOB)

                failToast(resources.getString(R.string.error_dob))
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
                failToast(resources.getString(R.string.error_name))
            } else {
                //calculate the age
                val age: Int = if (savedAge > 4) {
                    savedAge
                } else {
                    val userEnteredDOB = DateTime.parse(inputDOB.text.toString(), formatter)
                    Years.yearsBetween(userEnteredDOB, currentDate).years
                }

                if (age > 4) {
                    //save the users age
                    _sharePreference.put("age", age)

                    val gameUsers = Bundle()
                    gameUsers.putString("player1", inputPlayer1Name.text.toString())
                    gameUsers.putString("player2", inputPlayer2Name.text.toString())
                    gameUsers.putBoolean("player1AI", ckboxPlayer1AI.isChecked)
                    gameUsers.putBoolean("player2AI", ckboxPlayer2AI.isChecked)

                    YoYo.with(Techniques.Hinge)
                            .duration(1000)
                            .onEnd {
                                startActivity(TicTacToeActivity::class.java, gameUsers)
                            }
                            .playOn(buttonPlayGame)
                } else {
                    failToast(resources.getString(R.string.error_over5))
                }
            }
        }
    }

    /**
     * change resource local
     */
    private fun changeResourceLocal(position: Int) {
        val config = Configuration()
        when (position) {
            0 -> config.locale = Locale.ENGLISH
            1 -> config.locale = Locale("ne", "Nepal")
        }

        resources.updateConfiguration(config, resources.displayMetrics)
        //save the language in shared preference
        _sharePreference.put(APP_LANGUAGE_POS, position)
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

    /**
     * Add the date picker listener
     */
    private val datePickerListener: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener {
        _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
        run {
            _year = year
            _month = monthOfYear
            _day = dayOfMonth

            val selectedDate = DateTime(_year, _month, _day, 0, 0, 0)
            inputDOB.setText(selectedDate.toString(formatter))
        }
    }

    /**
     * On create dialog listener
     */
    override fun onCreateDialog(dialogId: Int): Dialog? {
        if (dialogId == DATE_PICKER_DIALOG_ID) {
            return DatePickerDialog(this, datePickerListener, _year, _month, _day)
        }

        return null
    }
}