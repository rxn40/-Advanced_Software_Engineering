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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ShowIntakesActivity extends AppCompatActivity {

    private final int REQUESTCODE_EDIT = 13;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_intakes);

        TextView tv = (TextView) findViewById(R.id.textViewIntakes);

        Intent intent = getIntent();
        List<Intake> list = (List<Intake>) intent.getSerializableExtra("list");
         String text = "Alle Einnahmen des aktuellen Monats:";
        for(int i = 0; i < list.size(); i++){
            text = text + " '\n' "+list.get(i).toString();
        }
        tv.setText(text);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showintakes_menu, menu);
        return true;
    }

    public void changeEntry(View view){
        EditText editTextId = (EditText) findViewById(R.id.textViewEditText);
        int valueId = -1;
        if(!TextUtils.isEmpty(editTextId.getText())) {
            valueId = Integer.parseInt(editTextId.getText().toString());
        }

        Intent intent = new Intent();
        intent.putExtra("entry","Intake");
        intent.putExtra("id",valueId);
        setResult(RESULT_OK, intent);
        super.finish();

    }
}