package com.example.haushaltsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haushaltsapp.database.Outgo;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

private ArrayList<Outgo> OutgoList;
private RecyclerViewClickListener listener;

public RecyclerAdapter(ArrayList<Outgo> OutgoList, RecyclerViewClickListener listener)
{
    this.OutgoList = OutgoList;
    this.listener = listener;

}

public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView Outname;
    private TextView OutValue;
    private TextView OutDate;
    private TextView OutCategorie;

    public MyViewHolder(final View view)
    {
        super(view);
        Outname =view.findViewById(R.id.Chartname);
        OutValue = view.findViewById(R.id.ChartValue);
        OutDate =view.findViewById(R.id.ChartDate);
        OutCategorie =view.findViewById(R.id.ChartCategorie);

        view.setOnClickListener(this);

       // view.setOnContextClickListener((View.OnContextClickListener) this);

    }

    @Override
    public void onClick(View view) {
        int po = getAdapterPosition(); //fängt bie po 0 an
        listener.onClick(view, getAdapterPosition());

    }
}
    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {

        String name = OutgoList.get(position).getName();
        holder.Outname.setText(name);

        String value = Double.toString(OutgoList.get(position).getValue());
        holder.OutValue.setText(value);

        String day = Integer.toString(OutgoList.get(position).getDay());
        String month = Integer.toString(OutgoList.get(position).getMonth());
        String year = Integer.toString(OutgoList.get(position).getYear());
        String date = day+"."+month+"."+year;
        holder.OutDate.setText(date);

        String categorie = OutgoList.get(position).getCategory();
        holder.OutCategorie.setText(categorie);
    }

    @Override
    public int getItemCount() {
        return OutgoList.size();
    }

    public  interface RecyclerViewClickListener{
    void onClick(View v, int position);
    }
}
