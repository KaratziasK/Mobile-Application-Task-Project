package dit.hua.gr.it2022120.mobile_development.project;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "status",
        indices = {@Index("task_id")},
        foreignKeys = @ForeignKey(
                entity = Task.class,
                parentColumns = "id",
                childColumns = "task_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class Status{

    @PrimaryKey
    @ColumnInfo(name = "task_id")
    private int taskId;

    @ColumnInfo(name = "status_name")
    private String statusName;

    public Status(int taskId, String statusName) {
        this.taskId = taskId;
        this.statusName = statusName;
    }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

}
