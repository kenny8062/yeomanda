package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yeomanda.Retrofit.ResponseDto.ProfileResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

public class Profile extends AppCompatActivity {
    ImageView selfImage1,selfImage2,selfImage3;
    TextView personEmail,personSex,personName,personBirth;
    RetrofitClient retrofitClient;
    ProfileResponseDto profileResponseDto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        init();
    }

    public void init(){
        Intent intent=getIntent();
        selfImage1=findViewById(R.id.personImage1);
        selfImage2=findViewById(R.id.personImage2);
        selfImage3=findViewById(R.id.personImage3);
        personEmail=findViewById(R.id.personEmail);
        personSex=findViewById(R.id.personSex);
        personName=findViewById(R.id.personName);
        personBirth=findViewById(R.id.personBirth);
        retrofitClient=new RetrofitClient();
        profileResponseDto=retrofitClient.showProfile(intent.getStringExtra("token"),intent.getStringExtra("email"));
        System.out.println(intent.getStringExtra("email"));
        while(profileResponseDto==null){
            System.out.println("ProfileResponseDto is null");
        }
        personEmail.setText(profileResponseDto.getData().getEmail());
        personSex.setText(profileResponseDto.getData().getSex());
        personBirth.setText(profileResponseDto.getData().getBirth());
        personName.setText(profileResponseDto.getData().getName());

        Glide.with(this)
                .load(profileResponseDto.getData().getFiles().get(0))
                .into(selfImage1);

        Glide.with(this)
                .load(profileResponseDto.getData().getFiles().get(1))
                .into(selfImage2);

        Glide.with(this)
                .load(profileResponseDto.getData().getFiles().get(2))
                .into(selfImage3);
    }
}