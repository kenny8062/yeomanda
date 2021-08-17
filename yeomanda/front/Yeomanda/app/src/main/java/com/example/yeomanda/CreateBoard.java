package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.yeomanda.Retrofit.CreateBoardDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import java.util.ArrayList;

public class CreateBoard extends AppCompatActivity {
    Double lat,lon;
    EditText edt1,edt2 ,et;
    ArrayList<EditText> edt;
    Button createBtn ,plusBtn;
    LinearLayout ll;
    CreateBoardDto createBoardDto;
    int count=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_board);
        Intent intent = getIntent();
        lat=intent.getDoubleExtra("lat",0);
        lon=intent.getDoubleExtra("lon",0);
        System.out.println(lat);
        Log.d("tag",lat.toString());
        init();
    }
    public void init(){
        createBtn=findViewById(R.id.createBtn);
        edt1=findViewById(R.id.editTextTextPersonName);
        edt2=findViewById(R.id.editTextTextPersonName2);
        plusBtn=findViewById(R.id.plusBtn);
        ll = findViewById(R.id.ll);
        edt=new ArrayList<>();
        edt.add(edt2);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et = new EditText(getApplicationContext());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                et.setLayoutParams(p);
                et.setText("editText" + count + "번");
                et.setId(count++);
                edt.add(et);
                ll.addView(et);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", String.valueOf(count));
                Log.d("TAG", "////////////////////////////////////////////////////////////////////");
                Log.d("TAG", String.valueOf(count));
                ArrayList<CreateBoardDto> createBoardDtos=new ArrayList<>();
                for(int i=0;i<count;i++){
                    createBoardDto=new CreateBoardDto();
                    createBoardDto.setLatitude(Double.toString(lat));
                    createBoardDto.setLongitude(Double.toString(lon));
                    createBoardDto.setTravelDate(edt1.toString());
                    createBoardDto.setTravelMate(edt.get(i).toString());
                    createBoardDtos.add(createBoardDto);
                }
                RetrofitClient retrofitClient=new RetrofitClient();
                retrofitClient.createboard(createBoardDtos);
            }
        });
    }
}