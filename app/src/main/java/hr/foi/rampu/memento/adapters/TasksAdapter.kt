package hr.foi.rampu.memento.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.memento.R
import hr.foi.rampu.memento.database.TasksDatabase
import hr.foi.rampu.memento.entities.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TasksAdapter(
    private val tasksList: MutableList<Task>,
    private val onTaskCompleted: ((taskId: Int) -> Unit)? = null
) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val sdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.ENGLISH)
        private val taskName: TextView
        private val taskDueDate: TextView
        private val taskCategoryColor: SurfaceView

        init {
            taskName = view.findViewById(R.id.tv_task_name)
            taskDueDate = view.findViewById(R.id.tv_task_due_date)
            taskCategoryColor = view.findViewById(R.id.sv_task_category_color)

            view.setOnLongClickListener {
                val currentTask = tasksList[adapterPosition]

                val alertDialogBuilder = AlertDialog.Builder(view.context)
                    .setTitle(taskName.text)
                    .setNeutralButton("Delete task") { _, _ ->
                        val deletedTask = currentTask
                        TasksDatabase.getInstance().getTasksDao().removeTask(deletedTask)
                        removeTaskFromList()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }

                if (!currentTask.completed) {
                    alertDialogBuilder.setPositiveButton("Mark as completed") { _, _ ->
                        currentTask.completed = true
                        TasksDatabase.getInstance().getTasksDao().insertTask(currentTask)
                        removeTaskFromList()
                        onTaskCompleted?.invoke(currentTask.id)
                    }
                }

                alertDialogBuilder.show()

                return@setOnLongClickListener true
            }
        }

        private fun removeTaskFromList() {
            tasksList.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
        }

        fun bind(task: Task) {
            taskName.text = task.name
            taskDueDate.text = sdf.format(task.dueDate)
            taskCategoryColor.setBackgroundColor(task.category.color.toColorInt())
        }
    }

    fun addTask(newTask: Task) {
        var newIndexInList = tasksList.indexOfFirst { task ->
            task.dueDate > newTask.dueDate
        }
        if (newIndexInList == -1) {
            newIndexInList = tasksList.size
        }
        tasksList.add(newIndexInList, newTask)
        notifyItemInserted(newIndexInList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val taskView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(taskView)
    }

    override fun getItemCount() = tasksList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasksList[position])
    }
}
