package dev.amanraj.ListUp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.List;

import dev.amanraj.ListUp.Model.ToDoModel;
import dev.amanraj.ListUp.Utils.DatabaseHandler;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG,"Alarm received");
        //getting the remaining tasks
        DatabaseHandler db = new DatabaseHandler(context);
        db.openDatabase();
        List<ToDoModel> tasks = db.getAllTasks();
        int remainingTasks = 0;
        for (ToDoModel task : tasks){
            if (task.getStatus() == 0){
                //assuming 0 represents unchecked tasks
                remainingTasks++;
            }
        }
        db.close();

        //sending Notification
        sendNotification(context ,remainingTasks);
    }

    private void sendNotification(Context context, int remainingTasks){
        Log.d(TAG,"Sending notification");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //creating a notification channel if the device is running Android 8 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Log.d(TAG,"creating notification channel");
            NotificationChannel channel = new NotificationChannel("task_notification_channel", "Task Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        //creating the notification
        Notification.Builder builder = new Notification.Builder(context,"task_notification_channel")
                .setContentTitle("Remaining tasks")
                .setContentText("You have " + remainingTasks + " tasks remaining.")
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true);

        //showing the notification
        notificationManager.notify(1,builder.build());
    }


}
