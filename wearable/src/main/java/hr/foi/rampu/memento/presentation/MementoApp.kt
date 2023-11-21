package hr.foi.rampu.memento.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import hr.foi.rampu.memento.R
import hr.foi.rampu.memento.presentation.models.Task
import hr.foi.rampu.memento.presentation.theme.MementoTheme

@Composable
fun MementoApp(tasks: List<Task>) {
    val listState = rememberScalingLazyListState()

    MementoTheme {
        Scaffold(
            positionIndicator = { PositionIndicator(scalingLazyListState = listState) },
            vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
        ) {
            if (tasks.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxSize(),
                    indicatorColor = MaterialTheme.colors.primaryVariant
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.msg_wait_tasks_syncing),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        ScalingLazyColumn(state = listState, contentPadding = PaddingValues(horizontal = 15.dp, vertical = 20.dp)) {
            items(tasks) { task ->
                TaskCard(task)
            }
        }
    }
}

@Composable
fun TaskCard(task: Task) {
    Card(onClick = { }) {
        Column {
            Text(
                text = task.name,
                style = MaterialTheme.typography.title3, color = MaterialTheme.colors.primary
            )
            Text(
                text = task.categoryName,
                style = MaterialTheme.typography.body2, color = MaterialTheme.colors.primaryVariant
            )
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
