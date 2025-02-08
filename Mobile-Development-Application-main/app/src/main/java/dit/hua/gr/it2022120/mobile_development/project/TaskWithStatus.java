package dit.hua.gr.it2022120.mobile_development.project;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskWithStatus {

    @Embedded
    public Task task;

    @ColumnInfo(name = "status_name")
    public String statusName;



    private String endDate;

    private String endTime;

    private void calculateEndDateTime() {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate date = LocalDate.parse(this.task.getStartDate(), dateFormatter);
            LocalTime time = LocalTime.parse(this.task.getStartTime(), timeFormatter);

            LocalDateTime startDateTime = LocalDateTime.of(date, time);
            LocalDateTime endDateTime = startDateTime.plusHours(this.task.getDuration());

            this.endDate = endDateTime.toLocalDate().format(dateFormatter);
            this.endTime = endDateTime.toLocalTime().format(timeFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            this.endDate = null;
            this.endTime = null;
        }
    }

    public TaskWithStatus(Task task, String statusName) {
        this.task = task;
        this.statusName = statusName;
        calculateEndDateTime();
    }

    public Task getTask() {
        return task;
    }
    public void setTask(Task task) {
        this.task = task;
    }
    public String getStatusName() {
        return statusName;
    }
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
