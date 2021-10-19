package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yeomanda.Retrofit.ResponseDto.MyFavoriteListResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

public class MyFavoriteList extends AppCompatActivity {
    ListView myFavoriteTeamListView;
    RetrofitClient retrofitClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite_list);
        init();
    }

    public void init(){
        Intent intent=getIntent();
        String myToken=intent.getStringExtra("token");
        Log.d("tokenCheck",myToken);

        retrofitClient=new RetrofitClient();

        MyFavoriteListResponseDto myFavoriteListResponseDto=retrofitClient.showMyFavoriteTeamList(myToken);

        while(myFavoriteListResponseDto==null){
            Log.d("error","myFavoriteListResponseDto is null");
        }
        if(myFavoriteListResponseDto.getData()!=null) {
            myFavoriteTeamListView = findViewById(R.id.myFavoriteTeamListView);
            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, myFavoriteListResponseDto.getData());
            myFavoriteTeamListView.setAdapter(adapter);
        }
        myFavoriteTeamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),MyFavoriteTeamProfile.class);
                intent.putExtra("teamName", myFavoriteListResponseDto.getData().get(position));
                intent.putExtra("token",myToken);
                startActivity(intent);
            }
        });


    }
}