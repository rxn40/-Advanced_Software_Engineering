package com.example.haushaltsapp.ChartPackage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.haushaltsapp.R;
import com.example.haushaltsapp.Database.Outgo;
import java.util.ArrayList;

public class RecyclerAdapterOut extends RecyclerView.Adapter<RecyclerAdapterOut.MyViewHolderOut> {

    private ArrayList<Outgo> outgoList;
    private RecyclerViewClickListenerOut listener;

    public RecyclerAdapterOut(ArrayList<Outgo> outgoList, RecyclerViewClickListenerOut listener) {
        this.outgoList = outgoList;
        this.listener = listener;
    }

    public class MyViewHolderOut extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView outName;
        private TextView outValue;
        private TextView outDate;
        private TextView outCategory;

        public MyViewHolderOut(final View view) {
            super(view);
            outName = view.findViewById(R.id.Chartname);
            outValue = view.findViewById(R.id.ChartValue);
            outDate = view.findViewById(R.id.ChartDate);
            outCategory = view.findViewById(R.id.ChartCategorie);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
    @NonNull
    @Override
    public MyViewHolderOut onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item,parent,false);
        return new MyViewHolderOut(itemView);
    }

    //Runden auf zwei Nachkommazahlen
    public double round(double number, int digits) {
        return (double) ((int)number + (Math.round(Math.pow(10, digits)*(number-(int)number)))/(Math.pow(10, digits)));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderOut holder, int position) {

        //Setzen der Textview
        String name = outgoList.get(position).getName();
        holder.outName.setText(name);

        double valueDouble = outgoList.get(position).getValue();
        String value = Double.toString(round(valueDouble,2));
        holder.outValue.setText(value+" €");

        String day = Integer.toString(outgoList.get(position).getDay());
        String month = Integer.toString(outgoList.get(position).getMonth());
        String year = Integer.toString(outgoList.get(position).getYear());
        String date = day+"."+month+"."+year;
        holder.outDate.setText(date);

        String category = outgoList.get(position).getCategory();
        holder.outCategory.setText(category);
    }

    @Override
    public int getItemCount() {
        return outgoList.size();
    }

    public  interface RecyclerViewClickListenerOut {
        void onClick(View v, int position);
    }

}
