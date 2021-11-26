package com.example.haushaltsplaner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;


public class AusgabeListAdapter extends ArrayAdapter<class_Ausgabe> {

    private Context mContext;
    int mResource;

    public AusgabeListAdapter(Context context, int resource, ArrayList<class_Ausgabe> objects){
        super(context,resource,objects);
        mContext =context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Ausgabe Infos
        String ausgabe = getItem(position).getAusgabe();
        String wert = getItem(position).getWert();
        String datum = getItem(position).getDatum();

        //Erzeugen von Ausgabe Object mit Infos
        class_Ausgabe ausgabeObject =new class_Ausgabe(ausgabe,wert,datum);

        LayoutInflater inflater =LayoutInflater.from(mContext);
        convertView =inflater.inflate(mResource,parent,false);

        TextView tvAusgabe =(TextView) convertView.findViewById(R.id.textView1);
        TextView tvWert =(TextView) convertView.findViewById(R.id.textView2);
        TextView tvDatum =(TextView) convertView.findViewById(R.id.textView3);

        tvAusgabe.setText(ausgabe);
        tvWert.setText(wert);
        tvDatum.setText(datum);

        return convertView;
    }
}
