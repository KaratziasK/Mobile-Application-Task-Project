package dit.hua.gr.it2022120.mobile_development.project;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ViewTaskNotCompletedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks_not_completed);

        TableLayout tableLayout = findViewById(R.id.task_table);
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        TaskDao dao = db.taskDao();

        loadNotCompletedTasks(tableLayout, dao);
    }

    private void loadNotCompletedTasks(TableLayout tableLayout, TaskDao dao) {
        new Thread(() -> {
            List<TaskWithStatus> tasksWithStatus = dao.getNotCompletedTasks();

            runOnUiThread(() -> {
                tableLayout.removeAllViews();

                TableRow headerRow = new TableRow(this);
                headerRow.addView(createTextView("Name"));
                headerRow.addView(createTextView("Status"));
                headerRow.addView(createTextView("Date"));
                headerRow.addView(createTextView("Button"));
                tableLayout.addView(headerRow);

                for (TaskWithStatus taskWithStatus : tasksWithStatus) {
                    if (!taskWithStatus.getStatusName().equalsIgnoreCase("COMPLETED")) {
                        TableRow row = new TableRow(this);
                        row.addView(createTextView(taskWithStatus.getTask().getShortName()));
                        row.addView(createTextView(taskWithStatus.getStatusName()));
                        row.addView(createTextView(taskWithStatus.getTask().getStartDate()));

                        ImageButton viewDetailsButton = new ImageButton(this);
                        viewDetailsButton.setImageResource(R.drawable.eye_icon);
                        viewDetailsButton.setBackgroundResource(android.R.color.transparent);
                        viewDetailsButton.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                        viewDetailsButton.setPadding(8, 8, 8, 8);
                        TableRow.LayoutParams params = new TableRow.LayoutParams(80, 80);
                        viewDetailsButton.setLayoutParams(params);

                        viewDetailsButton.setOnClickListener(v -> showTaskDetails(taskWithStatus));
                        row.addView(viewDetailsButton);

                        tableLayout.addView(row);
                    }
                }
            });
        }).start();
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private void showTaskDetails(TaskWithStatus taskWithStatus) {
        String details = "ID: " + taskWithStatus.getTask().getId() + "\n" +
                "Name: " + taskWithStatus.getTask().getShortName() + "\n" +
                "Status: " + taskWithStatus.getStatusName() + "\n" +
                "Description: " + taskWithStatus.getTask().getDescription() + "\n" +
                "Start Date: " + taskWithStatus.getTask().getStartDate() + "\n" +
                "Start Time: " + taskWithStatus.getTask().getStartTime() + "\n" +
                "End Date: " + taskWithStatus.getEndDate() + "\n" +
                "End Time: " + taskWithStatus.getEndTime() + "\n" +
                "Duration: " + taskWithStatus.getTask().getDuration() + " hours\n" +
                "Location: " + taskWithStatus.getTask().getLocation();

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Task Details");
        builder.setMessage(details);
        builder.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}