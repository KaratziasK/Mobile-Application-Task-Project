package dit.hua.gr.it2022120.mobile_development.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<TaskWithStatus> tasksWithStatus;
    private final TaskDao taskDao;
    private final Context context;

    public TaskAdapter(Context context, List<TaskWithStatus> tasksWithStatus) {
        this.context = context;
        this.tasksWithStatus = new ArrayList<>(tasksWithStatus);
        AppDatabase db = AppDatabase.getInstance(context);
        this.taskDao = db.taskDao();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskWithStatus taskWithStatus = tasksWithStatus.get(position);
        Task task = taskWithStatus.getTask();

        holder.taskId.setText("ID: " + task.getId());
        holder.taskName.setText("Name: " + task.getShortName());
        holder.taskStatus.setText("Status: " + taskWithStatus.getStatusName());
        holder.taskDescription.setText("Description: " + task.getDescription());
        holder.taskStartDate.setText("Start Date: " + task.getStartDate());
        holder.taskStartTime.setText("Start Time: " + task.getStartTime());
        holder.taskEndDate.setText("End Date: " + taskWithStatus.getEndDate());
        holder.taskEndTime.setText("End Time: " + taskWithStatus.getEndTime());
        holder.taskDuration.setText("Duration: " + task.getDuration() + " hours");
        holder.taskLocation.setText("Location: " + task.getLocation());

        boolean isCompleted = taskWithStatus.getStatusName().equalsIgnoreCase("COMPLETED");
        holder.taskCompleted.setChecked(isCompleted);
        holder.taskCompleted.setEnabled(!isCompleted);

        holder.taskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
                    TaskDao taskDao = db.taskDao();
                    taskDao.updateTaskStatus(task.getId(), "COMPLETED");
                    taskWithStatus.setStatusName("COMPLETED");
                    holder.itemView.post(() -> holder.taskCompleted.setEnabled(false));
                }).start();
            }
        });

        holder.editTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra("TASK_ID", task.getId());
            try {
                ((ViewAllTasksActivity) context).startActivityForResult(intent, 1);
            } catch (ClassCastException e) {
                context.startActivity(intent);
            }
        });


        holder.deleteTaskButton.setOnClickListener(v -> {
            new Thread(() -> {
                int rowsDeleted = taskDao.deleteTaskById(task.getId());
                if (rowsDeleted > 0) {
                    holder.itemView.post(() -> {
                        if (position >= 0 && position < tasksWithStatus.size()) {
                            tasksWithStatus.remove(position);
                            notifyItemRemoved(position);
                        }
                    });
                }
            }).start();
        });


        holder.viewOnMapButton.setOnClickListener(v -> {
            String location = task.getLocation();
            if (location != null && !location.isEmpty()) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                } else {
                    Toast.makeText(context, "Google Maps is not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "No location specified", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasksWithStatus.size();
    }

    public void updateTasks(List<TaskWithStatus> newTasks) {
        tasksWithStatus.clear();
        tasksWithStatus.addAll(newTasks);
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskId,taskName, taskStatus, taskDescription, taskStartDate, taskStartTime, taskDuration, taskLocation, taskEndDate, taskEndTime;
        CheckBox taskCompleted;
        Button editTaskButton, deleteTaskButton, viewOnMapButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskId=itemView.findViewById(R.id.task_id);
            taskName = itemView.findViewById(R.id.task_name);
            taskStatus = itemView.findViewById(R.id.task_status);
            taskDescription = itemView.findViewById(R.id.task_description);
            taskStartDate = itemView.findViewById(R.id.task_start_date);
            taskStartTime = itemView.findViewById(R.id.task_start_time);
            taskEndDate = itemView.findViewById(R.id.task_end_date);
            taskEndTime = itemView.findViewById(R.id.task_end_time);
            taskDuration = itemView.findViewById(R.id.task_duration);
            taskLocation = itemView.findViewById(R.id.task_location);
            taskCompleted = itemView.findViewById(R.id.task_completed);
            editTaskButton = itemView.findViewById(R.id.edit_task_button);
            deleteTaskButton = itemView.findViewById(R.id.delete_task_button);
            viewOnMapButton = itemView.findViewById(R.id.view_on_map_button);
        }
    }
}
