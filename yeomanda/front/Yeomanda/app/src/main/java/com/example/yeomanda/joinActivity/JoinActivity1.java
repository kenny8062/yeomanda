package com.example.yeomanda.joinActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yeomanda.R;
import com.example.yeomanda.Retrofit.JoinDto;
import com.example.yeomanda.SendMail;

public class JoinActivity1 extends AppCompatActivity {
    JoinDto joinDto;
    Button nextBtn,certificationBtn,okBtn;
    EditText emailEdt,passwordEdt,certificationNumEdt;
    private String emailAuth,user_email;
    LinearLayout linearLayout;
    Boolean isAuth=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join1);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork()
                .build());
        init();
    }
    public void init(){
        nextBtn=findViewById(R.id.nextBtn);
        certificationBtn=findViewById(R.id.certificationBtn);
        okBtn=findViewById(R.id.okBtn);
        emailEdt=findViewById(R.id.emailEdt);
        passwordEdt=findViewById(R.id.passwordEdt);
        certificationNumEdt=findViewById(R.id.certNum);
        linearLayout=findViewById(R.id.linearlayout);
        joinDto=new JoinDto();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailAuth.equals(certificationNumEdt.getText().toString())){
                    linearLayout.setVisibility(View.GONE);
                    isAuth=true;
                    Toast.makeText(getApplicationContext(),"인증이 완료되었습니다.",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"인증번호를 확인해주십시오.",Toast.LENGTH_LONG).show();
                }
            }
        });
        //다음페이지
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isAuth){
                    System.out.println(emailEdt.getText().toString());
                    joinDto.setEmail(emailEdt.getText().toString());
                    joinDto.setPassword(passwordEdt.getText().toString());
                    Intent intent=new Intent(getApplicationContext(),JoinActivity2.class);
                    intent.putExtra("joinDto", joinDto);
                    startActivity(intent);
                }
            }
        });
        //이메일 인증 버튼
        certificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmail(user_email)) {
                    SendMail sendMail = new SendMail();
                    System.out.println(emailEdt.getText().toString());
                    emailAuth = sendMail.sendSecurityCode(getApplicationContext(), emailEdt.getText().toString());

                }
                else{
                    Toast.makeText(getApplicationContext(), "이메일형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        emailEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                user_email = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}