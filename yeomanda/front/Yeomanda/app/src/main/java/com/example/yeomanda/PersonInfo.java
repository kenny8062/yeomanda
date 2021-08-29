package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PersonInfo extends AppCompatActivity {
    ImageView selfImage1,selfImage2,selfImage3;
    TextView personEmail,personSex,personName,personBirth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        init();
    }

    public void init(){
        Intent intent=getIntent();
        selfImage1=findViewById(R.id.selfimage1);
        selfImage2=findViewById(R.id.selfimage2);
        selfImage3=findViewById(R.id.selfimage3);
        personEmail=findViewById(R.id.personEmail);
        personSex=findViewById(R.id.personSex);
        personName=findViewById(R.id.personName);
        personBirth=findViewById(R.id.personBirth);
        personEmail.setText(intent.getStringExtra("이메일"));
        System.out.println(intent.getStringExtra("이메일"));
    }
}