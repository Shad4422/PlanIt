package com.codegama.PlanIt.TasksAndCalendar;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.codegama.PlanIt.R;
import com.codegama.PlanIt.database.DatabaseClient;
import com.codegama.PlanIt.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ShowCalendar extends AppCompatActivity {

    private ImageView back;
    private CalendarView calendarView;
    private List<Task> tasks = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_calendar_view);

        back = findViewById(R.id.back);
        calendarView = findViewById(R.id.calendarView);

        calendarView.setHeaderColor(R.color.colorPrimaryDark);
        getSavedTasks();


        back.setOnClickListener(view -> onBackPressed());
    }


    private void getSavedTasks() {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                tasks = DatabaseClient
                        .getInstance(ShowCalendar.this)
                        .getAppDatabase()
                        .dataBaseAction()
                        .getAllTasksList();
                return tasks;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                calendarView.setEvents(getHighlightedDays());
            }
        }

        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }


    public List<EventDay> getHighlightedDays() {
        List<EventDay> events = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            String[] items1 = tasks.get(i).getDate().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(year));

            int drawableId;
            String nature = tasks.get(i).getNature();

            if (nature.equals("Work")) {
                drawableId = R.drawable.greendot;
            } else if (nature.equals("Entertainment")) {
                drawableId = R.drawable.pinkdot;
            } else if (nature.equals("Other")) {
                drawableId = R.drawable.yellowdot;
            } else {
                continue; // Skip the task if nature value doesn't match
            }

            events.add(new EventDay(calendar, drawableId));
        }



        return events;
    }



}
