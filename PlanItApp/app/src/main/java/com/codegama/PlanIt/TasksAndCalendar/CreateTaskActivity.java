package com.codegama.PlanIt.TasksAndCalendar;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.codegama.PlanIt.R;
import com.codegama.PlanIt.activity.AllTasksActivity;
import com.codegama.PlanIt.activity.MainActivity;
import com.codegama.PlanIt.activity.TaskDetails;
import com.codegama.PlanIt.database.DatabaseClient;
import com.codegama.PlanIt.model.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;


import static android.content.Context.ALARM_SERVICE;

public class CreateTaskActivity extends BottomSheetDialogFragment {
    private View contentView;

    Unbinder unbinder;
    @BindView(R.id.addTaskTitle)
    EditText addTaskTitle;
    @BindView(R.id.addTaskDescription)
    EditText addTaskDescription;
    @BindView(R.id.taskDate)
    EditText taskDate;
    @BindView(R.id.taskTime)
    EditText taskTime;

    @BindView(R.id.reminderRadioGroup)
    RadioGroup addReminder;

    @BindView(R.id.priorityRadioGroup)
    RadioGroup addPriority;

    @BindView(R.id.natureRadioGroup)
    RadioGroup addNature;

    String nature;
    String priority;
    int reminder;

    @BindView(R.id.addTask)
    Button addTask;

    int taskId;
    boolean isEdit;
    Task task;
    int mYear, mMonth, mDay;
    int mHour, mMinute;

    setRefreshListener setRefreshListener;
    AlarmManager alarmManager;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    MainActivity activity;
    AllTasksActivity activity2;

    public static int count = 0;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public void setTaskId(int taskId, boolean isEdit, setRefreshListener setRefreshListener, MainActivity activity) {
        this.taskId = taskId;
        this.isEdit = isEdit;
        this.activity = activity;
        this.setRefreshListener = setRefreshListener;
    }
//added sunday
    public void setTaskId(int taskId, boolean isEdit, setRefreshListener setRefreshListener, AllTasksActivity activity) {
        this.taskId = taskId;
        this.isEdit = isEdit;
        this.activity2 = activity;
        this.setRefreshListener = setRefreshListener;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        contentView = View.inflate(getContext(), R.layout.fragment_create_task, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        //back image
        ImageView backImageView = contentView.findViewById(R.id.back);
        backImageView.setOnClickListener(view -> {
            dismiss();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });


        addTask.setOnClickListener(view -> {
            if (validateFields())
                createTask();
        });
        if (isEdit) {
            showTaskFromId();
        }


        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        mBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        mBehavior.setPeekHeight(3900);

        taskDate.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(),
                        (view1, year, monthOfYear, dayOfMonth) -> {
                            taskDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            datePickerDialog.dismiss();
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
            return true;
        });

        taskTime.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(getActivity(),
                        (view12, hourOfDay, minute) -> {
                            taskTime.setText(hourOfDay + ":" + minute);
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
            return true;
        });
        //end date and time


        addReminder.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radio5Min:
                    reminder = 5;
                    break;
                case R.id.radio10Min:
                    reminder = 10;
                    break;
                case R.id.radio15Min:
                    reminder = 15;
                    break;
            }
        });

        addPriority.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioHigh:
                    priority = "High";
                    break;
                case R.id.radioMedium:
                    priority = "Medium";
                    break;
                case R.id.radioLow:
                    priority = "Low";
                    break;
            }
        });

        addNature.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioWork:
                    nature = "Work";
                    break;
                case R.id.radioEntertainment:
                    nature = "Entertainment";
                    break;
                case R.id.radioOther:
                    nature = "Other";
                    break;
            }
        });

    }//set up dialog

    public boolean validateFields() {
        String title = addTaskTitle.getText().toString().trim();
        String date = taskDate.getText().toString().trim();
        String time = taskTime.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Enter a task title", Toast.LENGTH_SHORT).show();
            return false;
        } else if (date.isEmpty()) {
            Toast.makeText(getContext(), "Select a task date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (time.isEmpty()) {
            Toast.makeText(getContext(), "Select a task time", Toast.LENGTH_SHORT).show();
            return false;
        } else if (nature == null) {
            Toast.makeText(getContext(), "Select a task nature", Toast.LENGTH_SHORT).show();
            return false;
        } else if (priority == null) {
            Toast.makeText(getContext(), "Select a task priority", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }//end validate


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void createTask() {
        class saveTaskInBackend extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                Task createTask = new Task();
                createTask.setTaskTitle(addTaskTitle.getText().toString());
                createTask.setTaskDescription(addTaskDescription.getText().toString());
                createTask.setDate(taskDate.getText().toString());
                createTask.setLastAlarm(taskTime.getText().toString());
                createTask.setNature(nature);
                createTask.setPriority(priority);
                createTask.setReminderDuration(reminder);


                if (!isEdit)
                    DatabaseClient.getInstance(getActivity()).getAppDatabase()
                            .dataBaseAction()
                            .insertDataIntoTaskList(createTask);
                else
                    DatabaseClient.getInstance(getActivity()).getAppDatabase()
                            .dataBaseAction()
                            .updateAnExistingRow(taskId,
                                    addTaskTitle.getText().toString(),
                                    addTaskDescription.getText().toString(),
                                    taskDate.getText().toString(),
                                    taskTime.getText().toString(),
                                    nature,
                                    reminder,
                                    priority
                                    );

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    createAnAlarm(); //for now
                }
                setRefreshListener.refresh();
                Toast.makeText(getActivity(), "Done successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }
        saveTaskInBackend st = new saveTaskInBackend();
        st.execute();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm() {
        try {
            String[] items1 = taskDate.getText().toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = taskTime.getText().toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

            int temp = reminder;
            int before = 0;
            if (temp == 5) {
                before = 5;
            } else if (temp == 15) {
                before = 15;
            } else if (temp == 10) {
                before = 10;
            }


            Calendar cur_cal = new GregorianCalendar();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.YEAR, Integer.parseInt(year));
            cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            cal.set(Calendar.MINUTE, Integer.parseInt(min));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long notificationTimeMillis = cal.getTimeInMillis() - (before * 60 * 1000);

            if (before != 0) {
                // Schedule a notification for the task
                scheduleNotification(task, notificationTimeMillis);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    private void showTaskFromId() {
        class showTaskFromId extends AsyncTask<Void, Void, Void> {
            @SuppressLint("WrongThread")
            @Override
            protected Void doInBackground(Void... voids) {
                task = DatabaseClient.getInstance(getActivity()).getAppDatabase()
                        .dataBaseAction().selectDataFromAnId(taskId);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                setDataInUI();
            }
        }
        showTaskFromId st = new showTaskFromId();
        st.execute();
    }

    private void setDataInUI() {
        addTaskTitle.setText(task.getTaskTitle());
        taskDate.setText(task.getDate());
        taskTime.setText(task.getLastAlarm());
        addTaskDescription.setText(task.getTaskDescription());

        // Set the checked state of the radio buttons based on the task's nature
        if (task.getNature().equals("Work")) {
            addNature.check(R.id.radioWork);
        } else if (task.getNature().equals("Entertainment")) {
            addNature.check(R.id.radioEntertainment);
        } else if (task.getNature().equals("Other")) {
            addNature.check(R.id.radioOther);
        }

        // Set the checked state of the radio buttons based on the task's priority
        if (task.getPriority().equals("High")) {
            addPriority.check(R.id.radioHigh);
        } else if (task.getPriority().equals("Medium")) {
            addPriority.check(R.id.radioMedium);
        } else if (task.getPriority().equals("Low")) {
            addPriority.check(R.id.radioLow);
        }

        // Set the checked state of the radio buttons based on the task's reminder duration
        if (task.getReminderDuration() == 5) {
            addReminder.check(R.id.radio5Min);
        } else if (task.getReminderDuration() == 10) {
            addReminder.check(R.id.radio10Min);
        } else if (task.getReminderDuration() == 15) {
            addReminder.check(R.id.radio15Min);
        }

    }

    public interface setRefreshListener {
        void refresh();
    }



    private void scheduleNotification(Task task, long notificationTimeMillis) {
        // Create a unique notification ID for the task
        int notificationId = (int) System.currentTimeMillis();

        // Create an intent to launch the app when the notification is clicked
       /* Intent intent = new Intent(getActivity(), AllTasksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      TaskDetails taskDetails = new TaskDetails();
        Intent intent = new Intent(getActivity(), taskDetails.getClass());
        intent.putExtra("task_id", task.getTaskId());
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/
        TaskDetails taskDetails = new TaskDetails();
        Intent intent = new Intent(   getActivity(), taskDetails.getClass()  );
        intent.putExtra("taskId", task.getTaskId());
        intent.putExtra("taskTitle", task.getTaskTitle());
        intent.putExtra("taskDescription", task.getTaskDescription());
        intent.putExtra("taskTime", task.getLastAlarm());
        intent.putExtra("taskDate", task.getDate());
        intent.putExtra("taskPriority", task.getPriority());
        intent.putExtra("taskNature", task.getNature());
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Create a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "my_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Reminder")
                .setContentText(task.getTaskTitle() +"Is Coming soon!")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if the device is running Android Oreo or higher, and if so, create a notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("my_channel_id", "My Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Schedule the notification to be displayed at the specified time
        long currentTimeMillis = System.currentTimeMillis();
        long delayMillis = notificationTimeMillis - currentTimeMillis;

        if (delayMillis > 0) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notificationManager.notify(notificationId, builder.build());
                }
            }, delayMillis);
        }
    }

}








