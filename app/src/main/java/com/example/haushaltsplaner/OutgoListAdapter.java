package com.example.haushaltsplaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

import java.util.ArrayList;
import java.util.List;
//ArrayAdapter<Outgo>,
public class OutgoListAdapter extends ArrayAdapter<Outgo> {

//Zugriff auf intent geht hier nicht!!! bekommt den Wert null
//somit kein zugriff auf Datenbank
    class A extends AppCompatActivity {
        Intent intent = getIntent();

        public ArrayList<Outgo> getArrayList() {
            ArrayList<Outgo> ListeOut = (ArrayList<Outgo>) intent.getSerializableExtra("list");
            return  ListeOut;
        }

    }

    private Context mContext;
    private int mResource;

    private ArrayList<Outgo> a = new A().getArrayList();

    public OutgoListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Outgo> objects) {
        super(context, resource, objects);
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String outgo = a.get(position).getName();
        String value = Double.toString(a.get(position).getValue());
        Integer day = a.get(position).getDay();
        Integer month =a.get(position).getMonth();
        Integer year = a.get(position).getYear();
        String date = day+"."+month+"."+year;

        LayoutInflater inflater =LayoutInflater.from(mContext);
        convertView =inflater.inflate(mResource,parent,false);

        TextView tvAusgabe =(TextView) convertView.findViewById(R.id.textView1);
        TextView tvWert =(TextView) convertView.findViewById(R.id.textView2);
        TextView tvDatum =(TextView) convertView.findViewById(R.id.textView3);

        tvAusgabe.setText(outgo);
        tvWert.setText(value);
        tvDatum.setText(date);

        return convertView;
    }
}
