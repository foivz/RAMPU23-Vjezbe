package hr.foi.rampu.memento.services

import android.app.Service
import android.content.Intent
import hr.foi.rampu.memento.database.TasksDatabase

class TaskDeletionService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        TasksDatabase.buildInstance(applicationContext)
        val tasksDao = TasksDatabase.getInstance().getTasksDao()

        tasksDao.getAllTasks(true).forEach {
            if (it.isOverdue()) {
                tasksDao.removeTask(it)
            }
        }

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent) = null
}
