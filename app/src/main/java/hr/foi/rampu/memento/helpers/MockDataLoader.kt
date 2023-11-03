package hr.foi.rampu.memento.helpers

import hr.foi.rampu.memento.entities.Task
import hr.foi.rampu.memento.entities.TaskCategory
import java.util.Date

object MockDataLoader {
    fun getDemoData(): MutableList<Task> {
        return mutableListOf(
            Task(1, "Submit seminar paper", Date(), 1, false),
            Task(2, "Prepare for exercises", Date(), 2, false),
            Task(3, "Rally a project team", Date(), 1, false),
            Task(4, "Connect to server (SSH)", Date(), 3, false)
        )
    }

    fun getDemoCategories(): List<TaskCategory> = listOf(
        TaskCategory(1, "RAMPU", "#000080"),
        TaskCategory(2, "RPP", "#FF0000"),
        TaskCategory(3, "RWA", "#CCCCCC"),
    )
}
