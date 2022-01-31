package com.example.haushaltsapp.ChartPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.Database.Intake;
import java.util.ArrayList;

public class RecyclerAdapterIn extends RecyclerView.Adapter<RecyclerAdapterIn.MyViewHolderIn> {
    private ArrayList<Intake> intakeList;
    private RecyclerViewClickListenerIn listener;

    public RecyclerAdapterIn(ArrayList<Intake> intakeList, RecyclerViewClickListenerIn listener) {
        this.intakeList = intakeList;
        this.listener = listener;
    }

    public class MyViewHolderIn extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView inName;
        private TextView inValue;
        private TextView inDate;
        private TextView category;

        public MyViewHolderIn(final View view) {
            super(view);
            inName = view.findViewById(R.id.Chartname);
            inValue = view.findViewById(R.id.ChartValue);
            inDate = view.findViewById(R.id.ChartDate);
            category = view.findViewById(R.id.ChartCategorie);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolderIn onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item,parent,false);
        return new MyViewHolderIn(itemView);
    }

    //Runden auf zwei Nachkommazahlen
    public double round(double number, int digits) {
        return (double) ((int) number + (Math.round(Math.pow(10, digits)*(number -(int) number)))/(Math.pow(10, digits)));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderIn holder, int position) {

        //Setzen der Textview
        String name = intakeList.get(position).getName();
        holder.inName.setText(name);

        double valueDouble = intakeList.get(position).getValue();
        String value = Double.toString(round(valueDouble,2));
        holder.inValue.setText(value+" €");

        String day = Integer.toString(intakeList.get(position).getDay());
        String month = Integer.toString(intakeList.get(position).getMonth());
        String year = Integer.toString(intakeList.get(position).getYear());
        String date = day+"."+month+"."+year;
        holder.inDate.setText(date);

        //Bei Einnahme keine Kategorie vorhanden
        String categoryString = "";
        holder.category.setText(categoryString);
    }

    @Override
    public int getItemCount() {
        return intakeList.size();
    }


    public  interface RecyclerViewClickListenerIn{
        void onClick(View v, int position);
    }
}
