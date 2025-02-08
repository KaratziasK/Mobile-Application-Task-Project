package dit.hua.gr.it2022120.mobile_development.project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

public class DeleteTaskActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_task);

        EditText taskIdInput = findViewById(R.id.task_id_input);
        Button deleteTaskButton = findViewById(R.id.delete_task_button);

        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tasks_db").build();
        TaskDao dao = db.taskDao();

        deleteTaskButton.setOnClickListener(v -> {
            String taskIdStr = taskIdInput.getText().toString();

            if (taskIdStr.isEmpty()) {
                Toast.makeText(this, "Please enter a Task ID", Toast.LENGTH_SHORT).show();
                return;
            }

            int taskId = Integer.parseInt(taskIdStr);

            new Thread(() -> {
                int rowsAffected = dao.deleteTaskById(taskId);

                runOnUiThread(() -> {
                    if (rowsAffected > 0) {
                        Toast.makeText(this, rowsAffected + " task(s) deleted successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "No task found with ID: " + taskId, Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }
}
