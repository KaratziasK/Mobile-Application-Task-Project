package dit.hua.gr.it2022120.mobile_development.project;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
@Dao
public interface TaskDao {

    @Insert
    long insertTask(Task task);

    @Insert
    void insertStatus(Status status);

    @Query("SELECT tasks.*, status.status_name FROM tasks " +
            "INNER JOIN status ON tasks.id = status.task_id")
    List<TaskWithStatus> getTasksWithStatus();

    @Query("DELETE FROM tasks WHERE id = :taskId")
    int deleteTaskById(int taskId);

    @Query("UPDATE status SET status_name = :statusName WHERE task_id = :taskId")
    void updateTaskStatus(int taskId, String statusName);

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    Task getTaskById(int taskId);

    @Update
    void updateTask(Task task);

    @Query("SELECT tasks.*, status.status_name FROM tasks " +
            "INNER JOIN status ON tasks.id = status.task_id " +
            "WHERE status.status_name != 'COMPLETED' " +
            "ORDER BY " +
            "CASE status.status_name " +
            "   WHEN 'EXPIRED' THEN 1 " +
            "   WHEN 'IN-PROGRESS' THEN 2 " +
            "   WHEN 'RECORDED' THEN 3 " +
            "   ELSE 4 END, " +
            "tasks.start_date ASC, tasks.start_time ASC")
    List<TaskWithStatus> getNotCompletedTasks();

}
