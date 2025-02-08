package dit.hua.gr.it2022120.mobile_development.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleHourlyWorker();

        recyclerView = findViewById(R.id.recycler_view_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadTasks();

        findViewById(R.id.add_task_button).setOnClickListener(view -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent_add_task = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });

        findViewById(R.id.view_tasks_button).setOnClickListener(view -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent_view_tasks = new Intent(MainActivity.this, ViewTaskNotCompletedActivity.class);
            startActivity(new Intent(MainActivity.this, ViewTaskNotCompletedActivity.class));
        });

        findViewById(R.id.delete_task_button).setOnClickListener(v -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            Intent intent_delete_task = new Intent(MainActivity.this, DeleteTaskActivity.class);
            startActivity(new Intent(MainActivity.this, DeleteTaskActivity.class));
        });

        findViewById(R.id.export_button).setOnClickListener(view -> {
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskUpdateWorker.class).build();
            WorkManager.getInstance(this).enqueue(workRequest);
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                List<TaskWithStatus> notCompletedTasks = db.taskDao().getNotCompletedTasks();

                if (notCompletedTasks.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "No tasks to export.", Toast.LENGTH_SHORT).show());
                    return;
                }

                GenerateFileHTML fileGenerator = new GenerateFileHTML(this);
                String content = fileGenerator.generateFileContent(notCompletedTasks);
                fileGenerator.exportToFile("NotCompletedTasks.html", content);
            }).start();
        });
    }

    private void loadTasks() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<TaskWithStatus> tasksWithStatus = db.taskDao().getTasksWithStatus();

            runOnUiThread(() -> {
                if (taskAdapter == null) {
                    taskAdapter = new TaskAdapter(this, tasksWithStatus);
                    recyclerView.setAdapter(taskAdapter);
                } else {
                    taskAdapter.updateTasks(tasksWithStatus);
                }
            });
        }).start();
    }

        private void scheduleHourlyWorker() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextHour = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
            long initialDelay = Duration.between(now, nextHour).toMinutes();

            PeriodicWorkRequest hourlyWorkRequest = new PeriodicWorkRequest.Builder(HourlyTaskUpdateWorker.class, 1, TimeUnit.HOURS)
                    .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                    .build();

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                    "HourlyTaskUpdate",
                    ExistingPeriodicWorkPolicy.KEEP,
                    hourlyWorkRequest
            );
        }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.getInstance(getApplicationContext()).closeDatabase();
    }
}
