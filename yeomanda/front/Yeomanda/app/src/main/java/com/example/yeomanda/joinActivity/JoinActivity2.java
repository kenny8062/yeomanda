package com.example.yeomanda.joinActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yeomanda.R;
import com.example.yeomanda.Retrofit.JoinDto;

public class JoinActivity2 extends AppCompatActivity {
    JoinDto joinDto;
    Button mBtn,wBtn;
    boolean isMan, isWoman =false;
    EditText birthEdt,nameEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);
        init();
    }
    public void init(){
        Intent intent=getIntent();
        joinDto= (JoinDto) intent.getSerializableExtra("joinDto");
        mBtn=findViewById(R.id.mBtn);
        wBtn=findViewById(R.id.wBtn);
        birthEdt=findViewById(R.id.birthEdt);
        nameEdt=findViewById(R.id.nameEdt);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWoman){
                    wBtn.setBackgroundResource(R.drawable.ic_disabled_button);
                    isWoman =false;
                }
            }
        });

    }
}