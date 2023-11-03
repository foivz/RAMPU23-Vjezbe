package hr.foi.rampu.memento.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = TaskCategory::class,
        parentColumns = ["id"],
        childColumns = ["category_id"],
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Task(
    @PrimaryKey val id: Int,
    val name: String,
    @ColumnInfo(name = "due_date") val dueDate: Date,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    val completed: Boolean
) {
    @Ignore
    lateinit var category: TaskCategory
}
