package com.example.yeomanda.joinActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yeomanda.R;
import com.example.yeomanda.Retrofit.RequestDto.JoinDto;

public class JoinActivity2 extends AppCompatActivity {
    JoinDto joinDto;
    Button mBtn,wBtn,nextBtn;
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
        nextBtn=findViewById(R.id.nextBtn2);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWoman){
                    wBtn.setBackgroundResource(R.drawable.ic_disabled_button);
                    isWoman =false;
                }
                mBtn.setBackgroundResource(R.drawable.ic_sub_black_lined_button);
                isMan=true;
            }
        });

        wBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMan){
                    mBtn.setBackgroundResource(R.drawable.ic_disabled_button);
                    isMan=false;
                }
                wBtn.setBackgroundResource(R.drawable.ic_sub_black_lined_button);
                isWoman=true;
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWoman) joinDto.setSex("W");
                else joinDto.setSex("M");
                joinDto.setName(nameEdt.getText().toString());
                joinDto.setBirth(birthEdt.getText().toString());
                Intent intent = new Intent(getApplicationContext(),JoinActivity3.class);
                intent.putExtra("joinDto",joinDto);
                startActivity(intent);
            }
        });
    }


}