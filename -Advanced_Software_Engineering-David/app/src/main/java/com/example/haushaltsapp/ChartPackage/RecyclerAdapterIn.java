package com.example.haushaltsapp.ChartPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haushaltsapp.R;
import com.example.haushaltsapp.database.Intake;

import java.util.ArrayList;

public class RecyclerAdapterIn extends RecyclerView.Adapter<RecyclerAdapterIn.MyViewHolderIn> {
    private ArrayList<Intake> IntakeList;
    private RecyclerViewClickListenerIn listener;

    public RecyclerAdapterIn(ArrayList<Intake> IntakeList, RecyclerViewClickListenerIn listener)
    {
        this.IntakeList = IntakeList;
        this.listener = listener;

    }
    public class MyViewHolderIn extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView Inname;
        private TextView InValue;
        private TextView InDate;
        private TextView Categorie;

        public MyViewHolderIn(final View view) {
            super(view);
            Inname =view.findViewById(R.id.Chartname);
            InValue = view.findViewById(R.id.ChartValue);
            InDate =view.findViewById(R.id.ChartDate);
            Categorie =view.findViewById(R.id.ChartCategorie);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int po = getAdapterPosition(); //fängt bie po 0 an
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolderIn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item,parent,false);
        return new MyViewHolderIn(itemView);
    }
    //runden auf zwei Nachkommazahlen
    public double round(double zahl, int stellen) {
        return (double) ((int)zahl + (Math.round(Math.pow(10,stellen)*(zahl-(int)zahl)))/(Math.pow(10,stellen)));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderIn holder, int position) {
        String name = IntakeList.get(position).getName();
        holder.Inname.setText(name);

        double valuedouble = IntakeList.get(position).getValue();
        String value = Double.toString(round(valuedouble,2));
        holder.InValue.setText(value+" €");

        String day = Integer.toString(IntakeList.get(position).getDay());
        String month = Integer.toString(IntakeList.get(position).getMonth());
        String year = Integer.toString(IntakeList.get(position).getYear());
        String date = day+"."+month+"."+year;
        holder.InDate.setText(date);

        //bei EInnahme keine Kategorie vorhanden
        String categorie = "Einnahmen";//IntakeList.get(position).getCategory();
        holder.Categorie.setText(categorie);
    }

    @Override
    public int getItemCount() {
        return IntakeList.size();
    }


    public  interface RecyclerViewClickListenerIn{
        void onClick(View v, int position);
    }
}
