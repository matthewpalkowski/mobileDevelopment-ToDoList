package com.example.hw3_to_do_list

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Activity that allows for the user to edit a Task and save it.
 * @author Matthew Palkowski
 */
class TaskEditActivity : AppCompatActivity() {

    private lateinit var commitAndContinueButton : Button
    private lateinit var commitAndReturnButton : Button
    private lateinit var discardAndReturnButton : Button

    private lateinit var listener : ButtonListener

    private lateinit var userInput : TextView

    private lateinit var intentReturnToMain : Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_edit)
        initializeProperties()
        setOnClickListeners()
    }

    private fun initializeProperties(){
        commitAndContinueButton = findViewById(R.id.btnCommitMultiple)
        commitAndReturnButton = findViewById(R.id.btnCommit)
        discardAndReturnButton = findViewById(R.id.btnReturn)
        listener = ButtonListener()
        userInput = findViewById(R.id.editTxtTaskDesc)
        intentReturnToMain = Intent(this,MainActivity::class.java)
    }

    private fun setOnClickListeners(){
        commitAndContinueButton.setOnClickListener(listener)
        commitAndReturnButton.setOnClickListener(listener)
        discardAndReturnButton.setOnClickListener(listener)
    }

    private fun validInput():Boolean {return userInput.text.toString().isNotEmpty()}

    private fun Activity.hideKeyboard() {hideKeyboard(currentFocus ?: View(this))}

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    inner class ButtonListener: View.OnClickListener{
        override fun onClick(v: View?) {
            hideKeyboard()
            if(v?.id.toString() == discardAndReturnButton.id.toString()){
                setResult(Activity.RESULT_CANCELED)
                Toast.makeText(
                        applicationContext,
                        resources.getString(R.string.taskCancelled),
                        Toast.LENGTH_SHORT)
                        .show()

                startActivity(intentReturnToMain)
                finish()
            }

            if(validInput()){
                intentReturnToMain.putExtra(userInput.text.toString(),userInput.text.toString())
                if(v?.id.toString() == commitAndContinueButton.id.toString()){
                    Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.taskSaved),
                            Toast.LENGTH_SHORT)
                            .show()

                    userInput.text = ""
                }
                else{
                    setResult(Activity.RESULT_OK, intentReturnToMain)
                    Toast.makeText(
                            applicationContext,
                            resources.getString(R.string.submitted),
                            Toast.LENGTH_SHORT)
                            .show()

                    finish()
                }
            }
            else{Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.invalidTask),
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}