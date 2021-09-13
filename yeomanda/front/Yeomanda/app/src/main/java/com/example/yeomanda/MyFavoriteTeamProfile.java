package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.yeomanda.Retrofit.FavoriteTeamInfoDto;
import com.example.yeomanda.Retrofit.MyFavoriteTeamProfileResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

public class MyFavoriteTeamProfile extends AppCompatActivity {
    MyFavoriteTeamProfileResponseDto myFavoriteTeamProfileResponseDto;
    ListView listView;
    TeamInfoListViewAdapter adapter;
    String myToken,teamName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite_team_profile);
        init();
    }
    public void init(){
        Intent intent=getIntent();
        myToken=intent.getStringExtra("token");
        teamName=intent.getStringExtra("teamName");
        adapter=new TeamInfoListViewAdapter();
        listView=findViewById(R.id.myFavoriteTeamProfileListView);
        listView.setAdapter(adapter);
        RetrofitClient retrofitClient=new RetrofitClient();
        Log.d("teamname is", intent.getStringExtra("teamName"));
        myFavoriteTeamProfileResponseDto =retrofitClient.showMyFavoriteTeamProfile(myToken,teamName);
        while(myFavoriteTeamProfileResponseDto==null){
            Log.d("error","myFavoriteTeamProfileResponseDto is null");
        }
        for (int i=0;i<myFavoriteTeamProfileResponseDto.getData().size();i++){
            adapter.addItem(myFavoriteTeamProfileResponseDto.getData().get(i).getFiles().get(0),myFavoriteTeamProfileResponseDto.getData().get(i).getName(),myFavoriteTeamProfileResponseDto.getData().get(i).getSex(),myFavoriteTeamProfileResponseDto.getData().get(i).getBirth());
            Log.d("File 경로",myFavoriteTeamProfileResponseDto.getData().get(i).getFiles().get(0));
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),Profile.class);
                intent.putExtra("email",myFavoriteTeamProfileResponseDto.getData().get(position).getEmail());
                intent.putExtra("token",myToken);
                startActivity(intent);
            }
        });
    }
}