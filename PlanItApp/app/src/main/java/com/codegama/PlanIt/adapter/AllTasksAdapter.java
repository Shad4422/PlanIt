package com.codegama.PlanIt.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codegama.PlanIt.R;
import com.codegama.PlanIt.activity.AllTasksActivity;
import com.codegama.PlanIt.TasksAndCalendar.CreateTaskActivity;
import com.codegama.PlanIt.activity.TaskDetails;
import com.codegama.PlanIt.database.DatabaseClient;
import com.codegama.PlanIt.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllTasksAdapter extends RecyclerView.Adapter<AllTasksAdapter.TaskViewHolder> {

    private AllTasksActivity context;
    private LayoutInflater inflater;
    private List<Task> taskList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EE dd MMM yyyy", Locale.US);
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", Locale.US);
    private Date date = null;
    private String outputDateString = null;
    private CreateTaskActivity.setRefreshListener setRefreshListener;

    public AllTasksAdapter(AllTasksActivity context, List<Task> taskList, CreateTaskActivity.setRefreshListener setRefreshListener) {
        this.context = context;
        this.taskList = taskList;
        this.setRefreshListener = setRefreshListener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.item_task, viewGroup, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTaskTitle());
        holder.time.setText(task.getLastAlarm());
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));


        //ADDED (DETAILS)
        // Set click listener for the task rectangle
        holder.itemView.setOnClickListener(v -> {
            // Retrieve the selected task
            Task selectedTask = taskList.get(holder.getAdapterPosition());

            // Pass the task details to the next activity
            Intent intent = new Intent(context, TaskDetails.class);
            intent.putExtra("taskId", selectedTask.getTaskId());
            intent.putExtra("taskTitle", selectedTask.getTaskTitle());
            intent.putExtra("taskDescription", selectedTask.getTaskDescription());
            intent.putExtra("taskTime", selectedTask.getLastAlarm());
            intent.putExtra("taskDate", selectedTask.getDate());
            intent.putExtra("taskReminder", selectedTask.getReminderDuration());
            intent.putExtra("taskPriority", selectedTask.getPriority());
            intent.putExtra("taskNature", selectedTask.getNature());
            context.startActivity(intent);
        });




        try {
            date = inputDateFormat.parse(task.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopUpMenu(View view, int position) {
        final Task task = taskList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menuDelete:
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {
                                deleteTaskFromId(task.getTaskId(), position);
                            })
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuUpdate:
                    CreateTaskActivity CT = new CreateTaskActivity();
                    CT.setTaskId(task.getTaskId(), true, setRefreshListener, context);
                    CT.show(context.getSupportFragmentManager(), CT.getTag());
                    break;
                case R.id.menuComplete:
                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context, R.style.AppTheme_Dialog);
                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                            setPositiveButton(R.string.yes, (dialog, which) -> showCompleteDialog(task.getTaskId(), position))
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void showCompleteDialog(int taskId, int position) {
        Dialog dialog = new Dialog(context, R.style.AppTheme);
        dialog.setContentView(R.layout.dialog_completed_theme);
        Button close = dialog.findViewById(R.id.closeButton);
        close.setOnClickListener(view -> {
            deleteTaskFromId(taskId, position);
            dialog.dismiss();
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void deleteTaskFromId(int taskId, int position) {
        class GetSavedTasks extends AsyncTask<Void, Void, List<Task>> {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                DatabaseClient.getInstance(context)
                        .getAppDatabase()
                        .dataBaseAction()
                        .deleteTaskFromId(taskId);

                return taskList;
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                super.onPostExecute(tasks);
                removeAtPosition(position);
                setRefreshListener.refresh();
            }
        }
        GetSavedTasks savedTasks = new GetSavedTasks();
        savedTasks.execute();
    }

    private void removeAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.time)
        TextView time;

        TaskViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
