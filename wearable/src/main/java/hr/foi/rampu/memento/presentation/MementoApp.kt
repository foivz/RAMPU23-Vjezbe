package hr.foi.rampu.memento.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import hr.foi.rampu.memento.presentation.models.Task

@Composable
fun MementoApp(tasks: List<Task>) {
    ScalingLazyColumn {
        items(tasks) { task ->
            TaskCard(task)
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(onClick = { }) {
        Column {
            Text(task.name)
            Text(task.categoryName)
        }
    }
}

private val previewTask = Task(0, "App preview", "Wear app")

@Preview
@Composable
fun MementoAppPreview() {
    MementoApp(tasks = listOf(previewTask, previewTask, previewTask))
}

@Preview
@Composable
fun TaskCardPreview() {
    MementoApp(tasks = listOf(previewTask))
}
