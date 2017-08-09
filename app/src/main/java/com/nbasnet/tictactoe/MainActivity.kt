package com.nbasnet.tictactoe

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.nbasnet.extensions.activity.*
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
    var _storedAge: Int = 0
    val _minAge: Int = 5
    val DATE_PICKER_DIALOG_ID = 0
    val formatter: DateTimeFormatter = DateTimeFormat.forPattern(APP_DATE_FORMAT)

    lateinit var _sharePreference: AppPreferences
    lateinit var samuraiFont: Typeface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _sharePreference = AppPreferences(this)

        //set the language and local
        val selectedLanguagePos = _sharePreference.selectedLanguagePosition
        changeResourceLocal(selectedLanguagePos)
        setContentView(R.layout.activity_main)

        title = resources.getString(R.string.app_name)
        //set the custom fonts
        samuraiFont = Typeface.createFromAsset(assets, "fonts/samurai.ttf")
        setCustomFont()

        //fill select language list
        val languageList = ArrayAdapter.createFromResource(
                this,
                R.array.languages_list,
                R.layout.support_simple_spinner_dropdown_item
        )
        selectLanguage.prompt = resources.getString(R.string.label_languages)
        selectLanguage.adapter = languageList

        loadFromPreviousState(_sharePreference)
//        selectLanguage.setSelection(selectedLanguagePos)
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
        if (_storedAge < _minAge) {
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

            if (_storedAge < _minAge && inputDOB.text.isEmpty()) {
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
                val age: Int = if (_storedAge >= _minAge) {
                    _storedAge
                } else {
                    val userEnteredDOB = DateTime.parse(inputDOB.text.toString(), formatter)
                    Years.yearsBetween(userEnteredDOB, currentDate).years
                }

                if (age >= _minAge) {
                    //save the users age
                    _sharePreference.userAge = age
                    _sharePreference.player1Name = inputPlayer1Name.text.toString()
                    _sharePreference.player2Name = inputPlayer2Name.text.toString()
                    _sharePreference.isPlayer1AI = ckboxPlayer1AI.isChecked
                    _sharePreference.isPlayer2AI = ckboxPlayer2AI.isChecked
                    _sharePreference.startPlayer = 1
                    _sharePreference.player1WinsCount = 0
                    _sharePreference.player2WinsCount = 0

                    val gamePayload = Bundle()
                    gamePayload.putString(PLAYER1, inputPlayer1Name.text.toString())
                    gamePayload.putString(PLAYER2, inputPlayer2Name.text.toString())
                    gamePayload.putBoolean(PLAYER1_AI, ckboxPlayer1AI.isChecked)
                    gamePayload.putBoolean(PLAYER2_AI, ckboxPlayer2AI.isChecked)
                    gamePayload.putInt(GRID_ROW, 3)

                    YoYo.with(Techniques.Hinge)
                            .duration(1000)
                            .onEnd {
                                startActivity(TicTacToeActivity::class.java, gamePayload)
                            }
                            .playOn(buttonPlayGame)
                } else {
                    failToast(resources.getString(R.string.error_over5))
                }
            }
        }
    }

    /**
     * Load from the earlier state store in shared preference
     */
    private fun loadFromPreviousState(appPreference: AppPreferences) {
        //fill the input fields from previous state
        inputPlayer1Name.setText(appPreference.player1Name)
        inputPlayer2Name.setText(appPreference.player2Name)
        ckboxPlayer1AI.isChecked = appPreference.isPlayer1AI
        ckboxPlayer2AI.isChecked = appPreference.isPlayer2AI

        selectLanguage.setSelection(appPreference.selectedLanguagePosition)
        _storedAge = appPreference.userAge
    }

    /**
     * Set custom font
     */
    private fun setCustomFont(): Unit {
        buttonPlayGame.typeface = samuraiFont
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
        _sharePreference.selectedLanguagePosition = position
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