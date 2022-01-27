package com.example.haushaltsapp.ChartPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.database.Outgo;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private ArrayList<Outgo> outgoList;
    private RecyclerViewClickListener listener;

    public RecyclerAdapter(ArrayList<Outgo> outgoList, RecyclerViewClickListener listener) {
        this.outgoList = outgoList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView outname;
        private TextView outValue;
        private TextView outDate;
        private TextView outCategorie;

        public MyViewHolder(final View view) {
            super(view);
            outname = view.findViewById(R.id.Chartname);
            outValue = view.findViewById(R.id.ChartValue);
            outDate = view.findViewById(R.id.ChartDate);
            outCategorie = view.findViewById(R.id.ChartCategorie);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item,parent,false);
        return new MyViewHolder(itemView);
    }

    //runden auf zwei Nachkommazahlen
    public double round(double number, int positions) {
        return (double) ((int)number + (Math.round(Math.pow(10,positions)*(number-(int)number)))/(Math.pow(10,positions)));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.MyViewHolder holder, int position) {

        //Setzen der Textview
        String name = outgoList.get(position).getName();
        holder.outname.setText(name);

        double valuedouble = outgoList.get(position).getValue();
        String value = Double.toString(round(valuedouble,2));
        holder.outValue.setText(value+" €");

        String day = Integer.toString(outgoList.get(position).getDay());
        String month = Integer.toString(outgoList.get(position).getMonth());
        String year = Integer.toString(outgoList.get(position).getYear());
        String date = day+"."+month+"."+year;
        holder.outDate.setText(date);

        String categorie = outgoList.get(position).getCategory();
        holder.outCategorie.setText(categorie);
    }

    @Override
    public int getItemCount() {
        return outgoList.size();
    }

    public  interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

}
