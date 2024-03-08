package dev.amanraj.ListUp.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.amanraj.ListUp.AddNewTask;
import dev.amanraj.ListUp.MainActivity;
import dev.amanraj.ListUp.Model.ToDoModel;
import dev.amanraj.ListUp.R;
import dev.amanraj.ListUp.Utils.DatabaseHandler;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;
    public ToDoAdapter(DatabaseHandler db, MainActivity activity){
        this.db = db;
        this.activity=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout,parent,false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder,int position){
        db.openDatabase();

        final ToDoModel item = todoList.get(position);
        //set task text
        holder.task.setText(item.getTask());
        //set date text
        holder.dateTextView.setText(item.getDate());

        //set checked status and update listener
        holder.task.setChecked(toBoolean(item.getStatus()));
        //applying strikethrough if task is completed
        if(item.getStatus() == 1){
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }


        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    db.updateStatus(item.getId(),1);
                    //applying strikethrough when task is checked
                    holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else {
                    db.updateStatus(item.getId(),0);
                    //removing strikethrough when task is unchecked
                    holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                //notifying the activity about task status change
                if(mListener != null){
                    mListener.onTaskStatusChanged();
                }
            }
        });
    }

    public int getItemCount(){
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n!=0;
    }


    public Context getContext(){
        return activity;
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id",item.getId());
        bundle.putString("task",item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        TextView dateTextView;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            dateTextView = view.findViewById(R.id.dateTextView);
        }
    }

    //adding interface to communicate the task status change to the activity
    public interface OnTaskStatusChangedListener{
        void onTaskStatusChanged();
    }

    //method to set the listener

    private OnTaskStatusChangedListener mListener;
    public void setOnTaskStatusChangedListener(OnTaskStatusChangedListener listener){
        mListener = listener;
    }



}
