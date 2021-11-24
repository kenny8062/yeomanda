package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yeomanda.Retrofit.RequestDto.CreateBoardDto;
import com.example.yeomanda.Retrofit.ResponseDto.CreateBoardResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import java.util.ArrayList;

public class CreateBoard extends AppCompatActivity {
    Double lat,lon;
    EditText startYearEdt,startMonthEdt,startDayEdt,endYearEdt,endMonthEdt,endDayEdt, teamEmailEdt, addEdt, teamNameEdt = null;
    ArrayList<EditText> edts;
    Button createBtn ,plusBtn,cancleBtn;
    LinearLayout ll;
    CreateBoardDto createBoardDto;
    public static final int TYPE_TEXT_VARIATION_EMAIL_ADDRESS = 32;
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
        startYearEdt =findViewById(R.id.MyPlanYearStart);
        startMonthEdt=findViewById(R.id.MyPlanMonthStart);
        startDayEdt = findViewById(R.id.MyPlanDayStart);
        endYearEdt = findViewById(R.id.MyPlanYearEnd);
        endMonthEdt = findViewById(R.id.MyPlanMonthEnd);
        endDayEdt = findViewById(R.id.MyPlanDayEnd);
        teamNameEdt=findViewById(R.id.MyTeamName);
        teamEmailEdt =findViewById(R.id.MyTeamEmail);
        plusBtn=findViewById(R.id.plusBtn);
        cancleBtn=findViewById(R.id.cancelBtn);
        ll = findViewById(R.id.ll);
        edts =new ArrayList<>();
        edts.add(teamEmailEdt);

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEdt = new EditText(getApplicationContext());
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, teamEmailEdt.getLayoutParams().height);
                p.setMargins(0,10,0,0);
                addEdt.setLayoutParams(p);
                addEdt.setInputType(TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                addEdt.setId(count++);
                //addEdt.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_yellow_rounded_rectangle));
                edts.add(addEdt);
                ll.addView(addEdt);
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
                String myPlanDate=startYearEdt.getText().toString()+startMonthEdt.getText().toString()+startDayEdt.getText().toString()+"~"+endYearEdt.getText().toString()+endMonthEdt.getText().toString()+endDayEdt.getText().toString();
                for(int i=0;i<count;i++){
                    createBoardDto=new CreateBoardDto();
                    createBoardDto.setLatitude(Double.toString(lat));
                    createBoardDto.setLongitude(Double.toString(lon));
                    createBoardDto.setTravelDate(myPlanDate);
                    createBoardDto.setTravelMate(edts.get(i).getText().toString());
                    createBoardDto.setTeamName(teamNameEdt.getText().toString());
                    createBoardDtos.add(createBoardDto);
                }
                RetrofitClient retrofitClient=new RetrofitClient();
                CreateBoardResponseDto createBoardResponseDto= retrofitClient.createboard(createBoardDtos);
                if(createBoardResponseDto.getSuccess()) {
                    Toast.makeText(getApplicationContext(),createBoardResponseDto.getMessage(),Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),createBoardResponseDto.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}