package com.example.yeomanda.chatPkg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yeomanda.R;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    Button sendBtn;
    EditText chatEdt;
    Socket mSocket;
    String myToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    public void init(){
        sendBtn=findViewById(R.id.sendChatBtn);
        chatEdt=findViewById(R.id.chatEdt);
        Intent intent=getIntent();
        myToken=intent.getStringExtra("token");
        try {
            mSocket= IO.socket("http://172.30.1.48:3000");
            //서버의 connect 이벤트 발생
            mSocket.connect();
            Log.d("connect","ok");
        } catch (URISyntaxException e) {
            Log.e("ChatSocketError",e.getReason());
            e.printStackTrace();
        }
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        //mSocket.on("newUser1",onMessageRecieved);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat= chatEdt.getText().toString();
                //mSocket.emit("newUser1",chat);
            }
        });
    }

    Emitter.Listener onConnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            mSocket.emit("chatRoom",myToken);
            //mSocket.emit("connection","유저이름");
        }
    };


    //서버에서 메시지를 받았을때의 이벤트
    Emitter.Listener onMessageRecieved=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject receivedata = (JSONObject) args[0];
            Log.d("a",receivedata.toString());
            // mSocket.emit("chat1", receivedata.toString());
            //mSocket.emit("connection","유저이름");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.close();
    }
}