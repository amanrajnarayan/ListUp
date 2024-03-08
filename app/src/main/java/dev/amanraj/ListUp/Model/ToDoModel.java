package dev.amanraj.ListUp.Model;

import java.text.SimpleDateFormat;
import java.util.Date;
public class ToDoModel {
    private int id,status;
    private String task,date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.date = dateFormat.format(new Date());
    }
}
