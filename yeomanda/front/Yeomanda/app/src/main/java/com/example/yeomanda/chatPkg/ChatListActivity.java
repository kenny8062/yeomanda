package com.example.yeomanda.chatPkg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.yeomanda.ListView.ChatListViewAdapter;
import com.example.yeomanda.R;
import com.example.yeomanda.Retrofit.ResponseDto.ChatListResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import java.util.Date;

public class ChatListActivity extends AppCompatActivity {
    String myToken,myEmail;
    ListView listview;
    ChatListViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        Intent intent=getIntent();
        myToken=intent.getStringExtra("token");
        myEmail=intent.getStringExtra("myEmail");
        init();
    }
    public void init(){
        adapter=new ChatListViewAdapter();
        listview=findViewById(R.id.chatListView);
        listview.setAdapter(adapter);
        RetrofitClient retrofitClient=new RetrofitClient();
        ChatListResponseDto chatListResponseDto=retrofitClient.showMyChatList(myToken);
        while(chatListResponseDto==null){
            Log.e("error","chatListResponseDto is Null");
        }
        for (int i=0;i<chatListResponseDto.getData().size();i++){
            Log.d("test",chatListResponseDto.getData().get(i).getRoomId());
            Log.d("test",chatListResponseDto.getData().get(i).getOtherTeamName());
            Log.d("test",chatListResponseDto.getData().get(i).getChatMessages().getCreatedAt());

            adapter.addItem(chatListResponseDto.getData().get(i).getOtherTeamName(),chatListResponseDto.getData().get(i).getChatMessages().getContent(),chatListResponseDto.getData().get(i).getChatMessages().getCreatedAt().split(" ")[4]);

        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("roomId",chatListResponseDto.getData().get(position).getRoomId());
                intent.putExtra("token",myToken);
                intent.putExtra("myEmail",myEmail);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("RestartTag", "onRestart()");
        init();
    }

}