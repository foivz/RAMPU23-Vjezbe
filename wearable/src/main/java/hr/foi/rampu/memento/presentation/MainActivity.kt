/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package hr.foi.rampu.memento.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import hr.foi.rampu.memento.presentation.models.Task

val mockTasks = listOf(
    Task(0, "Task1", "Category1"),
    Task(0, "Task2", "Category2"),
    Task(0, "Task3", "Category3"),
    Task(0, "Task4", "Category4"),
    Task(0, "Task5", "Category5")
)

class MainActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val tasks = mutableStateListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MementoApp(tasks)
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        dataClient.addListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { dataEvent ->
            if (dataEvent.type == DataEvent.TYPE_CHANGED) {
                DataMapItem.fromDataItem(dataEvent.dataItem).apply {
                    val tasksCount = dataMap.getInt("tasks_count")

                    for (itemIndex in 0 until tasksCount) {
                        val receivedTask = getTaskFromDataMap(dataMap, itemIndex)
                        tasks.add(receivedTask)
                    }

                    dataClient.deleteDataItems(uri)
                }
            }
        }
    }

    private fun getTaskFromDataMap(dataMap: DataMap, itemIndex: Int) = Task(
        dataMap.getInt("task_id_$itemIndex"),
        dataMap.getString("task_name_$itemIndex")!!,
        dataMap.getString("task_category_name_$itemIndex")!!
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MementoApp(mockTasks)
}
