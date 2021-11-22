package com.example.haushaltsplaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class ShowOutgosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_outgos);

        TextView tv = (TextView) findViewById(R.id.textViewOutgoes);

        Intent intent = getIntent();
        List<Outgo> list = (List<Outgo>) intent.getSerializableExtra("list");
        String text = "Alle Ausgaben des aktuellen Monats:";

        for(int i = 0; i < list.size(); i++){
            text = text + " '\n' "+list.get(i).toString();
        }
        tv.setText(text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showoutgoes_menu, menu);
        return true;
    }

    public void changeEntry(View view){
        EditText id = (EditText) findViewById(R.id.textViewEditText);

        int valueId = -1;
        if(!TextUtils.isEmpty(id.getText())) {
            valueId = Integer.parseInt(id.getText().toString());
        }
        Intent intent = new Intent();
        intent.putExtra("entry","outgo");
        intent.putExtra("id",valueId);
        setResult(RESULT_OK, intent);
        super.finish();
    }
}