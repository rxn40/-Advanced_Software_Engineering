package com.example.haushaltsapp.ToDoListPackage;

import android.content.DialogInterface;

public interface ToDoInterface {
    public void handleDialogClose(DialogInterface dialog);
    public void onTaskClick(int position);
}
