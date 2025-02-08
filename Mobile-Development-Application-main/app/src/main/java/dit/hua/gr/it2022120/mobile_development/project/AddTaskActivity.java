package dit.hua.gr.it2022120.mobile_development.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class AddTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(TaskUpdateWorker.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks_db").build();
        TaskDao dao = db.taskDao();

        EditText taskNameEditText = findViewById(R.id.name);
        EditText taskDescriptionEditText = findViewById(R.id.description);
        TimePicker timePicker = findViewById(R.id.time);
        DatePicker datePicker = findViewById(R.id.date);
        EditText durationEditText = findViewById(R.id.duration);
        EditText locationEditText = findViewById(R.id.location_input);
        Button saveTaskButton = findViewById(R.id.save_task_button);

        saveTaskButton.setOnClickListener(v -> {
            String taskName = taskNameEditText.getText().toString();
            String taskDescription = taskDescriptionEditText.getText().toString();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String startTime = String.format("%02d:%02d", hour, minute);
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();
            String startDate = String.format("%02d/%02d/%d", day, month + 1, year);
            String taskDuration = durationEditText.getText().toString();
            String taskLocation = locationEditText.getText().toString();

            if (taskName.isEmpty() || taskDuration.isEmpty()) {
                Toast.makeText(this, "Task Name and Duration are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration = Integer.parseInt(taskDuration);

            Task task = new Task(taskName, taskDescription, startTime, startDate, duration, taskLocation);

            new Thread(() -> {
                long taskId = dao.insertTask(task);

                if (taskId > 0) {
                    Status status = new Status((int) taskId, "RECORDED");
                    dao.insertStatus(status);

                    runOnUiThread(() -> {
                        Toast.makeText(this, "Task added successfully with status 'RECORDED'!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to add task. Try again.", Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}
