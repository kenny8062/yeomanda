package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class CreateBoard extends AppCompatActivity {
    Double lat,lon;
    EditText edt1,edt2,edt3;
    Button createBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        Intent intent = getIntent();
        lat=intent.getDoubleExtra("lat",0);
        lon=intent.getDoubleExtra("lon",0);

    }
    public void init(){
        createBtn=findViewById(R.id.createBtn);
        edt1=findViewById(R.id.editTextTextPersonName);
        edt2=findViewById(R.id.editTextTextPersonName2);
        edt3=findViewById(R.id.editTextTextPersonName3);


    }
}