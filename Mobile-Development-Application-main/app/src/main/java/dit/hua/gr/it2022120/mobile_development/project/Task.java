package dit.hua.gr.it2022120.mobile_development.project;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "short_name")
    private String shortName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "start_time")
    private String startTime;

    @ColumnInfo(name = "start_date")
    private String startDate;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "location")
    private String location;

    public Task(String shortName, String description, String startTime, String startDate, int duration, String location) {
        this.shortName = shortName;
        this.description = description;
        this.startTime = startTime;
        this.startDate = startDate;
        this.duration = duration;
        this.location = location;

    }

    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getStartTime() {
        return startTime;
    }
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
}