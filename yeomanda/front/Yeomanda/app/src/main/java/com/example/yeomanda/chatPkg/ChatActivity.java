package com.example.yeomanda.chatPkg;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yeomanda.ListView.ChatMessageAdapter;
import com.example.yeomanda.ListView.ChatMessageItem;
import com.example.yeomanda.R;
import com.example.yeomanda.Retrofit.ResponseDto.AllMyChatsResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ChatRoomResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.WithoutDataResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    Button sendBtn;
    EditText chatEdt;
    Socket mSocket;
    String myToken,myEmail, roomId;
    JSONObject roomInfo;
    ListView listView;
    ChatMessageAdapter adapter;
    Boolean isMyChat;
    //WithoutDataResponseDto withoutDataResponseDto=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent=getIntent();
        myToken=intent.getStringExtra("token");
        roomId=intent.getStringExtra("roomId");
        myEmail=intent.getStringExtra("myEmail");

        init();
    }

    public void init(){
        sendBtn=findViewById(R.id.sendChatBtn);
        chatEdt=findViewById(R.id.chatEdt);
        listView=findViewById(R.id.chatMsgListView);
        adapter=new ChatMessageAdapter();
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        RetrofitClient retrofitClient=new RetrofitClient();
        AllMyChatsResponseDto allMyChatsResponseDto=retrofitClient.getAllMyChats(myToken,roomId);
        while (allMyChatsResponseDto==null){
            Log.e("error", "allMyChatsResponseDto is null");
        }
        for (int i = 0; i < allMyChatsResponseDto.getData().size(); i++) {
            Log.d("Tag",allMyChatsResponseDto.getData().get(i).getSenderEmail());
            if (allMyChatsResponseDto.getData().get(i).getSenderEmail().equals(myEmail)) {
                isMyChat = true;
            } else {
                isMyChat = false;
            }
            adapter.addItem(allMyChatsResponseDto.getData().get(i).getSenderName(), allMyChatsResponseDto.getData().get(i).getContent(), allMyChatsResponseDto.getData().get(i).getCreatedAt(), isMyChat);
        }

        adapter.notifyDataSetChanged();
        Log.d("teamNum", roomId);
        roomInfo =new JSONObject();

        try {
            roomInfo.put("token",myToken);
            roomInfo.put("room_id", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            mSocket= IO.socket("http://ec2-54-180-202-228.ap-northeast-2.compute.amazonaws.com:3000/");
            //mSocket=IO.socket("http://192.168.0.29:3000/");
            //서버의 connect 이벤트 발생
            mSocket.connect();
            Log.d("connect","ok");
        } catch (URISyntaxException e) {
            Log.e("ChatSocketError",e.getReason());
            e.printStackTrace();
        }
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on("message",onMessageRecieved);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject chat= new JSONObject();
                try {
                    chat = convertMessageToJsonObject(myToken,roomId,chatEdt.getText().toString());
                    mSocket.emit("message",chat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("chatRoom", roomInfo);
            //mSocket.emit("connection","유저이름");
        }
    };


    //서버에서 메시지를 받았을때의 이벤트
    Emitter.Listener onMessageRecieved =new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject receiveData = (JSONObject) args[0];
            Log.d("MessageReceived",receiveData.toString());
            ChatMessageItem chatMessageItem=new ChatMessageItem();
            try {
                //chatMessageItem.setMessage(receiveData.getString("message"));
                //chatMessageItem.setUserName(receiveData.getString("sender"));
                //chatMessageItem.setMsgTime(receiveData.getString("time"));
                if(receiveData.getString("senderEmail").equals(myEmail)){
                    isMyChat=true;
                }else{
                    isMyChat=false;
                }
                adapter.addItem(receiveData.getString("senderName"),receiveData.getString("message"),receiveData.getString("time").split(" ")[4],isMyChat);
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
                Log.d("addMessage",receiveData.getString("time"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // mSocket.emit("chat1", receivedata.toString());
            //mSocket.emit("connection","유저이름");
        }
    };

    //메시지폼을 JSONObject로 변경
    public JSONObject convertMessageToJsonObject(String token,String roomId,String content) throws JSONException {
        JSONObject sendObject=new JSONObject();

        sendObject.put("token",token);
        sendObject.put("room_id",roomId);
        sendObject.put("content",content);

        return sendObject;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.close();
        Log.d("end","chatroom");
    }/*
    @Override
    public void onPause(){
        super.onPause();
        Log.d("end","chatroom111");

       *//* if(withoutDataResponseDto==null) {
            RetrofitClient retrofitClient = new RetrofitClient();
            retrofitClient.closeSocket(myToken, roomId);
        }*//*
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제

        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        RetrofitClient retrofitClient=new RetrofitClient();
        withoutDataResponseDto=retrofitClient.closeSocket(myToken,roomId);
        while(withoutDataResponseDto==null){
            Log.e("error","withOutResponseDto is null");
        };
        finish();

    }*/

    //메인쓰레드가 아닌 다른쓰레드에서 UI 변경이 불가능하므로 Handler 이용
    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 원래 하려던 동작 (UI변경 작업 등)
            adapter.notifyDataSetChanged();
        }
    };
}