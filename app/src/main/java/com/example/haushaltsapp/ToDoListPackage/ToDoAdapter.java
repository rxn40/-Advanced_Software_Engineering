package com.example.haushaltsapp.ToDoListPackage;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haushaltsapp.database.MySQLite;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.ToDoListActivity;

import java.util.List;

//Adapter und ViewHolder definieren, wie Daten im RecyclerView dargestellt werden

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<TaskModel> todoList;
    private MySQLite db;
    private ToDoListActivity activity;


    public ToDoAdapter(MySQLite db, ToDoListActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    //Anzeigen der Tasks, Aufruf durch RecyclerView wenn neuer ViewHolder kreiert wird
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    //Aufruf durch RecyclerView, um Verbindug zwischen ViewHolder und zugehörigen Daten zu generieren
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        db.openDatabase();
        final TaskModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    //Anzeige aller Elemente bis zum Ende der toDoListe
    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public Context getContext() {
        return activity;
    }

    public void setTasks(List<TaskModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        TaskModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        TaskModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            task.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                  int pos = getAdapterPosition();
                  Toast.makeText(v.getContext(), "You clicked this" + pos, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

}
