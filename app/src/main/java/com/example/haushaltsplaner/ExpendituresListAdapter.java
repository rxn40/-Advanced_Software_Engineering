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

//Name geändert, ursprünglich: AusgabeListAdapter

//für die erzeugung der Tabelle mit einer klasse
//kein zugriff auf Datenbank hier realisiert
public class ExpendituresListAdapter extends ArrayAdapter<Expenditures> {

    private Context mContext;
    int mResource;

    public ExpendituresListAdapter(Context context, int resource, ArrayList<Expenditures> objects){
        super(context,
                resource,
                objects);
        mContext =context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String ausgabe = getItem(position).getAusgabe();
        String wert = getItem(position).getWert();
        String datum = getItem(position).getDatum();


        //Erzeugen von Ausgabe Object mit Infos
        Expenditures ausgabeObject =new Expenditures(ausgabe,wert,datum);

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
