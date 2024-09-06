package com.codegama.PlanIt.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int taskId;

    @ColumnInfo(name = "taskTitle")
    private String taskTitle;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "taskDescription")
    private String taskDescription;

    @ColumnInfo(name = "lastAlarm")
    private String lastAlarm;


    @ColumnInfo(name = "reminderDuration")
    private int reminderDuration;

    @ColumnInfo(name = "Nature")
    private String Nature;

    @ColumnInfo(name = "priority")
    private String priority;

    public Task() {
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getLastAlarm() {
        return lastAlarm;
    }

    public void setLastAlarm(String lastAlarm) {
        this.lastAlarm = lastAlarm;
    }


    public int getReminderDuration() {
        return reminderDuration;
    }

    public void setReminderDuration(int reminderDuration) {
        this.reminderDuration = reminderDuration;
    }

    public void setNature(String Nature) {
        this.Nature = Nature;
    }

    public String getNature() {
        return Nature;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }
}