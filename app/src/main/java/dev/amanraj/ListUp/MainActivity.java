package dev.amanraj.ListUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import dev.amanraj.ListUp.Adapter.ToDoAdapter;
import dev.amanraj.ListUp.Model.ToDoModel;
import dev.amanraj.ListUp.Utils.DatabaseHandler;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView tasksRecyclerView;
    private ToDoAdapter tasksAdapter;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList;
    private DatabaseHandler db;
    private ProgressBar progressBar;
    private TextView progressText;
    private ImageView hint_tap, hint_right, hint_left;

    private static final String TAG = "Alarm";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hint_tap = findViewById(R.id.tap_hint);
        hint_left = findViewById(R.id.left_hint);
        hint_right = findViewById(R.id.right_hint);
        hint_tap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint_tap.setVisibility(View.GONE);
                hint_right.setVisibility(View.VISIBLE);
            }
        });
        hint_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint_right.setVisibility(View.GONE);
                hint_left.setVisibility(View.VISIBLE);
            }
        });
        hint_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hint_left.setVisibility(View.GONE);
            }
        });



//        getSupportActionBar().hide();

        db = new DatabaseHandler(this);
        db.openDatabase();

//        taskList = new ArrayList<>();

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db,MainActivity.this);
        tasksRecyclerView.setAdapter(tasksAdapter);

        fab=findViewById(R.id.fab);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);

        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(),AddNewTask.TAG);
            }
        });
        updateProgress();
        setAlarm();
    }

    //update progress method
    private void updateProgress(){
        int totalTasks = taskList.size();
        int completedTasks = 0;
        for (ToDoModel task : taskList){
            if (task.getStatus() == 1){
                completedTasks++;
            }
        }
        if (totalTasks > 0){
            int progress = (completedTasks*100)/totalTasks;
            progressBar.setProgress(progress);
            progressText.setText(progress + "%");
        }
    }


    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTasks(taskList);
        tasksAdapter.notifyDataSetChanged();

        //updating progress when task changes
        updateProgress();
    }

    @Override
    public void onTaskStatusChanged(){
        updateProgress();
    }


    private void setAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        //setting the alarm to start at 8pm

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,12);
        calendar.set(Calendar.MINUTE,56);
        Log.d(TAG,"Alarm set");
        //scheduling the alarm to repeat daily
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }

}
//pushing the notif