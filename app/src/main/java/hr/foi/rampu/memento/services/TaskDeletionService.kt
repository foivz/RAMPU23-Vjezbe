package hr.foi.rampu.memento.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import hr.foi.rampu.memento.database.TasksDatabase

class TaskDeletionService : Service() {

    fun deleteOldTasks(onTaskDeletion: (Int) -> Unit) {
        TasksDatabase.buildInstance(applicationContext)
        val tasksDao = TasksDatabase.getInstance().getTasksDao()
        tasksDao.getAllTasks(true).forEach {
            if (it.isOverdue()) {
                tasksDao.removeTask(it)
                onTaskDeletion(it.id)
            }
        }
    }

    override fun onBind(intent: Intent) = TaskDeletionBinder()

    inner class TaskDeletionBinder : Binder() {
        fun getService(): TaskDeletionService = this@TaskDeletionService
    }
}
