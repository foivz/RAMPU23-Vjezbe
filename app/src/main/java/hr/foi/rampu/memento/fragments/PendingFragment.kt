package hr.foi.rampu.memento.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hr.foi.rampu.memento.R
import hr.foi.rampu.memento.adapters.TasksAdapter
import hr.foi.rampu.memento.database.TasksDatabase
import hr.foi.rampu.memento.helpers.NewTaskDialogHelper

class PendingFragment : Fragment() {
    private val tasksDao = TasksDatabase.getInstance().getTasksDao()
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreateTask: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_pending_tasks)
        loadTaskList()
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        btnCreateTask = view.findViewById(R.id.fab_pending_fragment_create_task)
        btnCreateTask.setOnClickListener {
            showDialog()
        }
    }

    private fun loadTaskList() {
        val tasks = tasksDao.getAllTasks(false)
        recyclerView.adapter = TasksAdapter(tasks.toMutableList())
    }

    private fun showDialog() {
        val newTaskDialogView = LayoutInflater
            .from(context)
            .inflate(R.layout.new_task_dialog, null)

        val dialogHelper = NewTaskDialogHelper(newTaskDialogView)

        AlertDialog.Builder(context)
            .setView(newTaskDialogView)
            .setTitle(getString(R.string.create_a_new_task))
            .setPositiveButton(getString(R.string.create_a_new_task)) { _, _ ->
                var newTask = dialogHelper.buildTask()
                val newTaskId = tasksDao.insertTask(newTask)[0]
                newTask = tasksDao.getTask(newTaskId.toInt())

                val tasksAdapter = recyclerView.adapter as TasksAdapter
                tasksAdapter.addTask(newTask)
            }
            .show()

        val categories = TasksDatabase
            .getInstance()
            .getTaskCategoriesDao()
            .getAllCategories()

        dialogHelper.populateSpinner(categories)
        dialogHelper.activateDateTimeListeners()
    }
}
