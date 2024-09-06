package com.codegama.PlanIt.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codegama.PlanIt.R;
import com.codegama.PlanIt.TasksAndCalendar.CreateTaskActivity;
import com.codegama.PlanIt.adapter.AllTasksAdapter;
import com.codegama.PlanIt.adapter.TaskAdapter;
import com.codegama.PlanIt.database.DatabaseClient;
import com.codegama.PlanIt.model.Task;

import java.util.ArrayList;
import java.util.List;

public class AllTasksActivity extends AppCompatActivity  implements CreateTaskActivity.setRefreshListener  {
    private RecyclerView taskRecycler;
    private AllTasksAdapter allTasksAdapter;
    private List<Task> tasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tasks);

        ImageView backIcon = findViewById(R.id.back);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                Intent intent = new Intent(AllTasksActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        taskRecycler = findViewById(R.id.taskRecycler);
        setUpAdapter();

        getSavedTasks();
    }

    private void setUpAdapter() {
        allTasksAdapter = new AllTasksAdapter(this, tasks, this);
        taskRecycler.setLayoutManager(new LinearLayoutManager(this));
        taskRecycler.setAdapter(allTasksAdapter);
    }

    private void getSavedTasks() {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                if (tasks != null) {
                    AllTasksActivity.this.tasks.clear();
                    AllTasksActivity.this.tasks.addAll(tasks);
                    allTasksAdapter.notifyDataSetChanged(); // Fix: Call notifyDataSetChanged() on allTasksAdapter
                }
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
