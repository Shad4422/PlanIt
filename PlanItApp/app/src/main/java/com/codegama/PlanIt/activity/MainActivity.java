package com.codegama.PlanIt.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codegama.PlanIt.R;
import com.codegama.PlanIt.adapter.TaskAdapter;
import com.codegama.PlanIt.TasksAndCalendar.CreateTaskActivity;
import com.codegama.PlanIt.TasksAndCalendar.ShowCalendar;
//import com.codegama.PlanIt.broadcastReceiver.AlarmBroadcastReceiver;
import com.codegama.PlanIt.database.DatabaseClient;
import com.codegama.PlanIt.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements CreateTaskActivity.setRefreshListener {

    @BindView(R.id.taskRecycler)
    RecyclerView taskRecycler;
    @BindView(R.id.addTask)
    TextView addTask;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();

    @BindView(R.id.calendar)
    ImageView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUpAdapter();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        //Click Add Task Button -->> The Fragment Will Appear
        addTask.setOnClickListener(view -> {
            CreateTaskActivity CT = new CreateTaskActivity();
            CT.setTaskId(0, false, this, MainActivity.this);
            CT.show( getSupportFragmentManager(), CT.getTag() );
        });

        getSavedTasks();

        // by lama (fragment >> activity )
        calendar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ShowCalendar.class);
            startActivity(intent);
        });

        //by lama (Click tasks icon >> new activity that contain all tasks )
        ImageView allTasksIcon = findViewById(R.id.alltasks);
        allTasksIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllTasksActivity.class);
                startActivity(intent);
            }
        });

    }//end on create

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(this, tasks, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    private void getSavedTasks() {

        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                      //    by lama
                        .getClosestTasks();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                setUpAdapter();
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    @Override
    public void refresh() {
        getSavedTasks();
    }
}