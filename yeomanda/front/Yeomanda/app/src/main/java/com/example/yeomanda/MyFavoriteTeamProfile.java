package com.example.yeomanda;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yeomanda.ListView.TeamInfoListViewAdapter;
import com.example.yeomanda.Retrofit.ResponseDto.MyFavoriteTeamProfileResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ProfileResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

public class MyFavoriteTeamProfile extends AppCompatActivity {
    MyFavoriteTeamProfileResponseDto myFavoriteTeamProfileResponseDto;
    ListView listView;
    TeamInfoListViewAdapter adapter;
    String myToken,teamName;


    View profileDialogView;
    Context context=this;
    ImageView personSubImage1, personMainImage, personSubImage2;
    TextView personEmail,personSex,personName,personBirth;
    ProfileResponseDto profileResponseDto;
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
                profileDialogView = getLayoutInflater().inflate(R.layout.activity_person_info,null);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(profileDialogView);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                personMainImage =alertDialog.findViewById(R.id.personMainImage);
                personSubImage1 =alertDialog.findViewById(R.id.personSubImage1);
                personSubImage2 =alertDialog.findViewById(R.id.personsubImage2);
                personEmail=alertDialog.findViewById(R.id.personEmail);
                personSex=alertDialog.findViewById(R.id.personSex);
                personName=alertDialog.findViewById(R.id.personName);
                personBirth=alertDialog.findViewById(R.id.personBirth);
                RetrofitClient retrofitClient=new RetrofitClient();
                profileResponseDto=retrofitClient.showProfile(myToken,myFavoriteTeamProfileResponseDto.getData().get(position).getEmail());
                while(profileResponseDto==null){
                    System.out.println("ProfileResponseDto is null");
                }
                personEmail.setText(profileResponseDto.getData().getEmail());
                personSex.setText(profileResponseDto.getData().getSex());
                personBirth.setText(profileResponseDto.getData().getBirth());
                personName.setText(profileResponseDto.getData().getName());

                Glide.with(context)
                        .load(profileResponseDto.getData().getFiles().get(0))
                        .into(personMainImage);

                Glide.with(context)
                        .load(profileResponseDto.getData().getFiles().get(1))
                        .into(personSubImage1);

                Glide.with(context)
                        .load(profileResponseDto.getData().getFiles().get(2))
                        .into(personSubImage2);

                //이미지 크게 보기1
                personSubImage1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SelectImageActivity.class);
                        intent.putExtra("uri",profileResponseDto.getData().getFiles().get(1));
                        startActivity(intent);

                    }
                });
                //이미지 크게 보기2
                personMainImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SelectImageActivity.class);
                        intent.putExtra("uri",profileResponseDto.getData().getFiles().get(0));
                        startActivity(intent);

                    }
                });
                //이미지 크게 보기3
                personSubImage2.setOnClickListener(new View.OnClickListener() {     @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getApplicationContext(),SelectImageActivity.class);
                        intent.putExtra("uri",profileResponseDto.getData().getFiles().get(2));
                        startActivity(intent);

                    }
                });
            }
        });
    }
}