package com.example.haushaltsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import top.defaults.colorpicker.ColorPickerPopup;

import android.os.Bundle;

public class AddCategoryActivity extends AppCompatActivity {

    private Button pickColorButton;

    private View mColorPreview;

    private int mDefaultColor;
    private String name;
    private double border;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);


        //Kasten der später die Farbe anzeigt
        mColorPreview = findViewById(R.id.preview_selected_color);
        mDefaultColor = 0;

        TextView viewText = findViewById(R.id.textView3);
        String text = "";
        Intent intent = getIntent();
        ArrayList<Category> list = (ArrayList<Category>) intent.getSerializableExtra("list");
        for(int i = 0; i < list.size(); i++){
            text = text + list.get(i).toString()+"\n";
        }
        viewText.setText(text);
    }



    public void pickColor(View view){
        new ColorPickerPopup.Builder(AddCategoryActivity.this).initialColor(
                Color.RED).enableBrightness(true)
                .enableAlpha(true)
                .okTitle( "Bestätigen")
                .cancelTitle("Abbrechen")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(view,
                        new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void
                            onColorPicked(int color) {
                                mDefaultColor = color;
                                mColorPreview.setBackgroundColor(mDefaultColor);
                            }
                        });
    }

    public void onClickOk(View view){
        getValues();
        Category category = new Category(name, mDefaultColor, border);

        Intent i = new Intent();
        i.putExtra("category", category);
        i.putExtra("selection","ok");
        setResult(RESULT_OK, i);
        super.finish();
    }

    public void onClickBreak(View view){
        Intent i = new Intent();
        i.putExtra("selection","break");
        setResult(RESULT_OK, i);
        super.finish();
    }

    private void getValues() {
        EditText editTextName = (EditText) findViewById(R.id.Bezeichnung);
        name = editTextName.getText().toString();

        EditText editTextValue = (EditText) findViewById(R.id.editTextLimit);
        String valueString = editTextValue.getText().toString();
        border = Double.parseDouble(valueString);
    }
}