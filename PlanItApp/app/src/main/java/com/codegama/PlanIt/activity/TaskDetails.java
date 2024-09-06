package com.codegama.PlanIt.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codegama.PlanIt.R;

public class TaskDetails extends AppCompatActivity {

        private TextView taskTitleTextView;
        private TextView taskDescriptionTextView;
        private TextView taskTimeTextView;
        private TextView taskDateTextView;
        private TextView taskReminderTextView;
        private TextView taskPriorityTextView;
        private TextView taskNatureTextView;

        ImageView backIcon;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.details);

            backIcon = findViewById(R.id.backButton);
            backIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate to MainActivity
                    Intent intent = new Intent(TaskDetails.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            taskTitleTextView = findViewById(R.id.taskTitleTextView);
            taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
            taskTimeTextView = findViewById(R.id.taskTimeTextView);
            taskDateTextView = findViewById(R.id.taskDateTextView);
            taskReminderTextView = findViewById(R.id.taskReminderTextView);
            taskPriorityTextView = findViewById(R.id.taskPriorityTextView);
            taskNatureTextView = findViewById(R.id.taskNatureTextView);

            // Retrieve the task details from the intent
            int taskId = getIntent().getIntExtra("taskId", 0);
            String taskTitle = getIntent().getStringExtra("taskTitle");
            String taskDescription = getIntent().getStringExtra("taskDescription");
            String taskTime = getIntent().getStringExtra("taskTime");
            String taskDate = getIntent().getStringExtra("taskDate");
            int taskReminder = getIntent().getIntExtra("taskReminder", 0);
            String taskPriority = getIntent().getStringExtra("taskPriority");
            String taskNature = getIntent().getStringExtra("taskNature");

            // Display the task details in the respective TextViews
            taskTitleTextView.setText("Task Title:  "+taskTitle);
            if (!taskDescription.isEmpty()) {
                taskDescriptionTextView.setText("Description:  " + taskDescription);
            } else {
                taskDescriptionTextView.setVisibility(View.GONE); // Hide the description TextView
            }
            taskDateTextView.setText("Date: "+taskDate);
            taskTimeTextView.setText("Time:  "+taskTime);

            if (taskReminder != 0) {
                taskReminderTextView.setText("Reminder before : " + String.valueOf(taskReminder) + " Min");
            } else {
                taskReminderTextView.setVisibility(View.GONE); // Hide the reminder TextView

            }
            taskPriorityTextView.setText("Priority:  "+taskPriority);
            taskNatureTextView.setText("Nature:  "+taskNature);
        }
    }