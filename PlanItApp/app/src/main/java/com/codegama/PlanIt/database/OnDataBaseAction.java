package com.codegama.PlanIt.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.codegama.PlanIt.model.Task;

import java.util.List;

@Dao
public interface OnDataBaseAction {


    //updated by lama 29may to make list in ascending order based on both the lastAlarm column (date and time)
   // @Query("SELECT * FROM Task")
   // List<Task> getAllTasksList();
    @Query("SELECT * FROM Task ORDER BY date ASC, lastAlarm ASC")
    List<Task> getAllTasksList();



    @Query("DELETE FROM Task")
    void truncateTheList();

    @Insert
    void insertDataIntoTaskList(Task task);

    @Query("DELETE FROM Task WHERE taskId = :taskId")
    void deleteTaskFromId(int taskId);

    @Query("SELECT * FROM Task WHERE taskId = :taskId")
    Task selectDataFromAnId(int taskId);

    @Query("UPDATE Task SET taskTitle = :taskTitle, taskDescription = :taskDescription, date = :taskDate, " +
            "lastAlarm = :taskTime, Nature = :Nature, reminderDuration = :reminder, priority = :priority " +
            "WHERE taskId = :taskId")


    void updateAnExistingRow(int taskId, String taskTitle, String taskDescription , String taskDate, String taskTime,
                             String Nature, int reminder, String priority);

//by lama 2:00AM am
    @Query("SELECT * FROM Task ORDER BY date ASC, lastAlarm ASC LIMIT 5")

    List<Task> getClosestTasks();




}



