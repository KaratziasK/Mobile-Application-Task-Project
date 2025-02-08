package dit.hua.gr.it2022120.mobile_development.project;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class GenerateFileHTML {
    private final Context context;

    public GenerateFileHTML(Context context) {
        this.context = context;
    }

    public String generateFileContent(List<TaskWithStatus> tasks) {
        StringBuilder content = new StringBuilder();

        content.append("<html><body>");
        content.append("<h1>Not Completed Tasks</h1>");
        content.append("<table border='1'>");
        content.append("<tr><th>Name</th><th>Description</th><th>Start Date</th><th>Duration</th><th>Location</th></tr>");

        for (TaskWithStatus taskWithStatus : tasks) {
            Task task = taskWithStatus.getTask();
            content.append("<tr>")
                    .append("<td>").append(task.getShortName()).append("</td>")
                    .append("<td>").append(task.getDescription()).append("</td>")
                    .append("<td>").append(task.getStartDate()).append("</td>")
                    .append("<td>").append(task.getDuration()).append("</td>")
                    .append("<td>").append(task.getLocation()).append("</td>")
                    .append("</tr>");
        }

        content.append("</table>");
        content.append("</body></html>");

        return content.toString();
    }


    public void exportToFile(String fileName, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/html");
            values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream os = resolver.openOutputStream(uri)) {
                    os.write(content.getBytes());
                    runOnMainThread(() -> Toast.makeText(context, "File saved to Downloads", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    e.printStackTrace();
                    runOnMainThread(() -> Toast.makeText(context, "Error saving file.", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnMainThread(() -> Toast.makeText(context, "Failed to create file.", Toast.LENGTH_SHORT).show());
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(content.getBytes());
                runOnMainThread(() -> Toast.makeText(context, "File saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
                runOnMainThread(() -> Toast.makeText(context, "Error saving file.", Toast.LENGTH_SHORT).show());
            }
        }
    }

    private void runOnMainThread(Runnable action) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(action);
    }



    public void generateAndExportTasks(String fileName) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<TaskWithStatus> notCompletedTasks = db.taskDao().getNotCompletedTasks();

            if (notCompletedTasks.isEmpty()) {
                Toast.makeText(context, "No tasks to export.", Toast.LENGTH_SHORT).show();
                return;
            }
            String content = generateFileContent(notCompletedTasks);
            exportToFile(fileName, content);
        }).start();
    }
}
