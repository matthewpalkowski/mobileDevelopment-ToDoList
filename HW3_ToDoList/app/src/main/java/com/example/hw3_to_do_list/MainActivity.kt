package com.example.hw3_to_do_list

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Initial activity of the application. Serves a view for the user to view to initiate the process
 * of generating a new Task and to interact with the list of Tasks that have been previously
 * created.
 * @author Matthew Palkowski
 */
class MainActivity : AppCompatActivity() {

    private lateinit var addTaskButton: Button
    private lateinit var taskListView : ListView
    private lateinit var noTaskText : TextView

    private lateinit var intentTaskEdit : Intent
    private lateinit var longClickListener: ListLongClickListener

    private var adapter:  ArrayAdapter<String>? = null

    private val REQUEST_CODE : Int = 1
    private val TASK_KEY : String = "Tasks"

    private var taskList : MutableList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeProperties()
    }

    private fun initializeProperties(){
        addTaskButton = findViewById(R.id.btnAddTask)
        taskListView = findViewById(R.id.listTasks)
        noTaskText = findViewById(R.id.txtNoTasks)
        longClickListener = ListLongClickListener()
        intentTaskEdit = Intent(this,TaskEditActivity::class.java)
        addTaskButton.setOnClickListener(ButtonListener())
        setVisibility()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE) {
            val extras: Bundle? = data?.extras
            if (extras != null) {
                val extrasIt: Iterator<String> = extras.keySet().iterator()
                while (extrasIt.hasNext()) {
                    taskList.add(extrasIt.next())
                }
                setAdapter()
                setVisibility()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        setPreferences()
    }

    override fun onResume() {
        super.onResume()
        if(taskList.size == 0) {
            val tasks = getPreferences(MODE_PRIVATE).getString(TASK_KEY,"") ?: ""
            if(tasks.isNotEmpty()) {
                val gson = Gson()
                val sType = object : TypeToken<List<String>>() {}.type
                taskList = gson.fromJson<List<String>>(tasks, sType) as MutableList<String>
            }
        }
        setVisibility()
        setAdapter()
        getPreferences(MODE_PRIVATE).edit().clear().apply()
    }

    private fun setAdapter(){
        if(adapter == null){
            adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,taskList)
            taskListView.adapter = adapter
            taskListView.onItemLongClickListener = longClickListener
        }
        else{adapter?.notifyDataSetChanged()}
    }

    private fun setPreferences(){
        val prefs : SharedPreferences = getPreferences(MODE_PRIVATE)
        val editor : SharedPreferences.Editor = prefs.edit()
        editor.clear().apply()
        val gson = Gson()
        val jsonString : String = gson.toJson(taskList)
        editor.putString(TASK_KEY,jsonString)
        editor.apply()
    }

    private fun setVisibility(){
        if(taskList.size == 0){noTaskText.visibility = View.VISIBLE}
        else{noTaskText.visibility = View.INVISIBLE}
    }

    inner class ButtonListener:View.OnClickListener{
        override fun onClick(v: View?) {
            startActivityForResult(intentTaskEdit, REQUEST_CODE)
        }
    }

    inner class ListLongClickListener : AdapterView.OnItemLongClickListener {
        override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
            taskList.removeAt(position)
            var toastText : String
            if(taskList.size == 0){toastText = resources.getString(R.string.allTaskCompleted)}
            else{toastText = resources.getString(R.string.taskCompleted)}
            Toast.makeText(
                    applicationContext,
                    toastText,
                    Toast.LENGTH_SHORT)
                    .show()
            setPreferences()
            setAdapter()
            setVisibility()
            return true
        }
    }
}