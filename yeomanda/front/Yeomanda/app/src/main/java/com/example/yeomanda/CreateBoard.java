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
import android.widget.Toast;

import com.example.yeomanda.Retrofit.CreateBoardDto;
import com.example.yeomanda.Retrofit.CreateBoardResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import java.util.ArrayList;

public class CreateBoard extends AppCompatActivity {
    Double lat,lon;
    EditText edt1,edt2 , edt = null;
    ArrayList<EditText> edts;
    Button createBtn ,plusBtn,cancleBtn;
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
        cancleBtn=findViewById(R.id.cancelBtn);
        ll = findViewById(R.id.ll);
        edts =new ArrayList<>();
        edts.add(edt2);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt = new EditText(getApplicationContext());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                edt.setLayoutParams(p);
                edt.setText("");
                edt.setId(count++);
                edts.add(edt);
                ll.addView(edt);
            }
        });

        cancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edts.size()!=0)
                    ll.removeView(edts.get(edts.size()-1));
                    edts.remove(edts.size()-1);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CreateBoardDto> createBoardDtos=new ArrayList<>();
                for(int i=0;i<count;i++){
                    createBoardDto=new CreateBoardDto();
                    createBoardDto.setLatitude(Double.toString(lat));
                    createBoardDto.setLongitude(Double.toString(lon));
                    createBoardDto.setTravelDate(edt1.getText().toString());
                    createBoardDto.setTravelMate(edts.get(i).getText().toString());
                    createBoardDtos.add(createBoardDto);
                }
                RetrofitClient retrofitClient=new RetrofitClient();
                CreateBoardResponseDto createBoardResponseDto= retrofitClient.createboard(createBoardDtos);
                if(createBoardResponseDto.getSuccess()) {
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"이메일을 다시 확인해주세요",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}