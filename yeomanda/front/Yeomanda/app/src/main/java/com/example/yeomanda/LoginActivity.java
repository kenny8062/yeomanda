package com.example.yeomanda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yeomanda.Retrofit.RequestDto.LoginDto;
import com.example.yeomanda.Retrofit.ResponseDto.LoginResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;
import com.example.yeomanda.joinActivity.JoinActivity1;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    Button joinBtn,loginBtn;
    LoginResponseDto loginResponseDto;
    LoginDto loginDto;
    RetrofitClient retrofitClient;
    EditText emailEdt,passwordEdt;
    String fcmToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE );
        }
        /*Intent fcm = new Intent(getApplicationContext(), MyFirebaseMessagingService.class);
        startService(fcm);*/
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FcmTokenTag", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        fcmToken = task.getResult();
                        Log.d("fcmtoken",fcmToken);
                        // Log and toast

                    }
                });

        loginDto=new LoginDto();
        retrofitClient=new RetrofitClient();
        init();



    }
    public void init(){
        emailEdt=findViewById(R.id.id_edt);
        passwordEdt=findViewById(R.id.password_edt);
        joinBtn=findViewById(R.id.join_btn);
        loginBtn=findViewById(R.id.login_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity1.class);
                startActivity(intent);
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginDto.setEmail(emailEdt.getText().toString());
                loginDto.setPassword(passwordEdt.getText().toString());
                loginDto.setFcm_token(fcmToken);
                loginResponseDto=retrofitClient.login(loginDto);
                while(loginResponseDto==null){}
                if(loginResponseDto.getSuccess()==true) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("token",loginResponseDto.getData().getToken());
                    intent.putExtra("hasPlanned",loginResponseDto.getData().getHasPlanned());
                    intent.putExtra("email",emailEdt.getText().toString());
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"로그인정보가 틀립니다.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}