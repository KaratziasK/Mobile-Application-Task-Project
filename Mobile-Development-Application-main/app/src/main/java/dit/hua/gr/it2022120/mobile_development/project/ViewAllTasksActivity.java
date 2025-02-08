package dit.hua.gr.it2022120.mobile_development.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViewAllTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_tasks);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTasks();
    }

    private void loadTasks() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            TaskDao dao = db.taskDao();

            List<TaskWithStatus> tasksWithStatus = dao.getTasksWithStatus();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadTasks();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }
}
