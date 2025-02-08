package dit.hua.gr.it2022120.mobile_development.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        EditText nameEditText = findViewById(R.id.edit_name);
        EditText descriptionEditText = findViewById(R.id.edit_description);
        DatePicker datePicker = findViewById(R.id.edit_date);
        TimePicker timePicker = findViewById(R.id.edit_time);
        EditText durationEditText = findViewById(R.id.edit_duration);
        EditText locationEditText = findViewById(R.id.edit_location_input);
        Button saveButton = findViewById(R.id.save_changes_button);

        int taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId == -1) {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        TaskDao taskDao = db.taskDao();

        new Thread(() -> {
            Task task = taskDao.getTaskById(taskId);
            if (task != null) {
                runOnUiThread(() -> {
                    nameEditText.setText(task.getShortName());
                    descriptionEditText.setText(task.getDescription());

                    String[] dateParts = task.getStartDate().split("/");
                    datePicker.updateDate(
                            Integer.parseInt(dateParts[2]),
                            Integer.parseInt(dateParts[1]) - 1,
                            Integer.parseInt(dateParts[0])
                    );

                    String[] timeParts = task.getStartTime().split(":");
                    timePicker.setHour(Integer.parseInt(timeParts[0]));
                    timePicker.setMinute(Integer.parseInt(timeParts[1]));

                    durationEditText.setText(String.valueOf(task.getDuration()));
                    locationEditText.setText(task.getLocation());
                });
            }
        }).start();

        saveButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String date = String.format("%02d/%02d/%d", day, month, year);
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();
            String time = String.format("%02d:%02d", hour, minute);
            String durationStr = durationEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();

            if (name.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(this, "Name and Duration are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration = Integer.parseInt(durationStr);

            new Thread(() -> {
                Task taskToUpdate = taskDao.getTaskById(taskId);
                if (taskToUpdate != null) {
                    taskToUpdate.setShortName(name);
                    taskToUpdate.setDescription(description);
                    taskToUpdate.setStartDate(date);
                    taskToUpdate.setStartTime(time);
                    taskToUpdate.setDuration(duration);
                    taskToUpdate.setLocation(location);

                    taskDao.updateTask(taskToUpdate);

                    runOnUiThread(() -> {
                        setResult(RESULT_OK);
                        Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }).start();
        });
    }
}
