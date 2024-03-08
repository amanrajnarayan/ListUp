package dev.amanraj.ListUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
}
//testing github