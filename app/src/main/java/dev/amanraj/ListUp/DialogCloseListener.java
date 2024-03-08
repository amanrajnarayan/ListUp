package dev.amanraj.ListUp;

import android.content.DialogInterface;

public interface DialogCloseListener {
    public void handleDialogClose(DialogInterface dialog);

    void onTaskStatusChanged();
}
