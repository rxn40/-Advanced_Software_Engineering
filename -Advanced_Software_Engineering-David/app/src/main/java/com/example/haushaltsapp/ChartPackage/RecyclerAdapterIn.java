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
    private ArrayList<Intake> intakeList;
    private RecyclerViewClickListenerIn listener;

    public RecyclerAdapterIn(ArrayList<Intake> intakeList, RecyclerViewClickListenerIn listener) {
        this.intakeList = intakeList;
        this.listener = listener;
    }

    public class MyViewHolderIn extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView inname;
        private TextView inValue;
        private TextView inDate;
        private TextView categorie;

        public MyViewHolderIn(final View view) {
            super(view);
            inname = view.findViewById(R.id.Chartname);
            inValue = view.findViewById(R.id.ChartValue);
            inDate = view.findViewById(R.id.ChartDate);
            categorie = view.findViewById(R.id.ChartCategorie);

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

    //runden auf zwei Nachkommazahlen
    public double round(double zahl, int stellen) {
        return (double) ((int)zahl + (Math.round(Math.pow(10,stellen)*(zahl-(int)zahl)))/(Math.pow(10,stellen)));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderIn holder, int position) {

        //Setzen der Textview
        String name = intakeList.get(position).getName();
        holder.inname.setText(name);

        double valuedouble = intakeList.get(position).getValue();
        String value = Double.toString(round(valuedouble,2));
        holder.inValue.setText(value+" €");

        String day = Integer.toString(intakeList.get(position).getDay());
        String month = Integer.toString(intakeList.get(position).getMonth());
        String year = Integer.toString(intakeList.get(position).getYear());
        String date = day+"."+month+"."+year;
        holder.inDate.setText(date);

        //bei Einnahme keine Kategorie vorhanden
        String categorieString = "Einnahmen";//IntakeList.get(position).getCategory();
        holder.categorie.setText(categorieString);
    }

    @Override
    public int getItemCount() {
        return intakeList.size();
    }


    public  interface RecyclerViewClickListenerIn{
        void onClick(View v, int position);
    }
}
