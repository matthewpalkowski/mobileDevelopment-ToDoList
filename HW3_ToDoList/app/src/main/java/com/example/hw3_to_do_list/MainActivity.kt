package com.example.hw3_to_do_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

/**
 * Initial activity of the application. Serves a view for the user to view to initiate the process
 * of generating a new Task and to interact with the list of Tasks that have been previously
 * created.
 * @author Matthew Palkowski
 */
class MainActivity : AppCompatActivity() {

    /*TODO
     * -Implement a list view that holds a list of tasks (title, image such as a check mark)
     *      -(Extra credit) Have list view be invisible until first item is added or when all
     *          items have been removed. Show a text view "no items added" until list has 1+ item(s)
     *      -(Extra credit) Short press sends intent to and opens the TaskEdit activity to edit the
     *              task populating that Activity with current info
     * -(Extra credit) Add a button to go to an activity that directs the user to a set of
     *          instructions about the app.
     * -(Extra credit) Add a button to view list of completed tasks (dates)
     * -Ensure that tasks are persistent when user closes and reopens app
     */

    private lateinit var addTaskButton: Button
    private lateinit var taskListView : ListView
    private lateinit var noTaskText : TextView

    private lateinit var intentTaskEdit : Intent
    private lateinit var longClickListener: ListLongClickListener

    private val REQUEST_CODE : Int = 1

    private val taskList : MutableList<String> = arrayListOf<String>()

    /**
     * Implementation of the onCreate method for the activity.
     * @param savedInstanceState see Kotlin Activity class documentation for details.
     */
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE) {
            var extras: Bundle? = data?.extras
            if (extras != null) {
                noTaskText.visibility = View.INVISIBLE
                var extrasIt: Iterator<String> = extras.keySet().iterator()
                while (extrasIt.hasNext()) {
                    taskList.add(extrasIt.next())
                }
                setAdapter()
            }
        }
    }

    private fun setAdapter(){
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,taskList)
        taskListView.adapter = adapter
        taskListView.onItemLongClickListener = longClickListener
    }

    inner class ButtonListener:View.OnClickListener{
        override fun onClick(v: View?) {
            startActivityForResult(intentTaskEdit, REQUEST_CODE)
        }
    }

    inner class ListLongClickListener : AdapterView.OnItemLongClickListener {
        override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
            taskList.removeAt(position)
            Toast.makeText(
                    applicationContext,
                    resources.getString(R.string.taskCompleted),
                    Toast.LENGTH_SHORT)
                    .show()
            setAdapter()
            if(taskList.size == 0){noTaskText.visibility = View.VISIBLE}
            return true
        }
    }
}