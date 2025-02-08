package dit.hua.gr.it2022120.mobile_development.project;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskUpdateWorker extends Worker {

    public TaskUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("TaskUpdateWorker", "Worker started at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        TaskDao taskDao = db.taskDao();

        try {
            List<TaskWithStatus> tasks = taskDao.getTasksWithStatus();
            LocalDateTime now = LocalDateTime.now();

            for (TaskWithStatus task : tasks) {

                if ("COMPLETED".equalsIgnoreCase(task.getStatusName())) {
                    continue;
                }

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm");
                LocalDateTime start = LocalDateTime.parse(task.getTask().getStartDate() + "T" + task.getTask().getStartTime(), dateTimeFormatter);
                LocalDateTime end = start.plusHours(task.getTask().getDuration());
                String newStatus = task.getStatusName();

                if (now.isAfter(end)) {
                    newStatus = "EXPIRED";
                } else if (now.isAfter(start)) {
                    newStatus = "IN-PROGRESS";
                }

                if (!task.getStatusName().equals(newStatus)) {
                    taskDao.updateTaskStatus(task.getTask().getId(), newStatus);
                    Log.i("TaskUpdateWorker", "Task ID: " + task.getTask().getId() + " - Status updated to: " + newStatus);

                }
            }
            Log.i("TaskUpdateWorker", "Worker completed successfully at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TaskUpdateWorker", "Worker failed at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            return Result.failure();
        }
    }
}
