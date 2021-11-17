package com.example.yeomanda.joinActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.example.yeomanda.Retrofit.RequestDto.JoinDto;
import com.example.yeomanda.EmailAuthentication.SendMail;

public class JoinActivity1 extends AppCompatActivity {
    JoinDto joinDto;
    Button nextBtn,certificationBtn,okBtn,mBtn,wBtn;;
    EditText emailEdt,passwordEdt,certificationNumEdt,birthEdt,nameEdt;
    private String emailAuth,user_email;
    LinearLayout linearLayout;
    Boolean isAuth,isMan, isWoman=false;
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
        linearLayout=findViewById(R.id.signUpLinearLayout);
        mBtn=findViewById(R.id.mBtn);
        wBtn=findViewById(R.id.wBtn);
        birthEdt=findViewById(R.id.birthEdt);
        nameEdt=findViewById(R.id.nameEdt);

        joinDto=new JoinDto();

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailAuth.equals(certificationNumEdt.getText().toString())){
                    certificationNumEdt.setVisibility(View.GONE);
                    okBtn.setVisibility(View.GONE);
                    certificationBtn.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    isAuth=true;
                    emailEdt.setEnabled(false);
                    Toast.makeText(getApplicationContext(),"인증이 완료되었습니다.",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"인증번호를 확인해주십시오.",Toast.LENGTH_LONG).show();
                }
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isWoman){
                    wBtn.setBackgroundResource(R.drawable.ic_yellow_button);
                    isWoman =false;
                }
                mBtn.setBackgroundResource(R.drawable.ic_dark_yellow_button);
                isMan=true;
            }
        });


        wBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isMan){
                    mBtn.setBackgroundResource(R.drawable.ic_yellow_button);
                    isMan=false;
                }
                wBtn.setBackgroundResource(R.drawable.ic_dark_yellow_button);
                isWoman=true;
            }
        });
        //다음페이지
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordEdt.getText().toString().length() >=8) {
                    if (isWoman) joinDto.setSex("W");
                    else joinDto.setSex("M");
                    joinDto.setName(nameEdt.getText().toString());
                    joinDto.setBirth(birthEdt.getText().toString());
                    joinDto.setEmail(emailEdt.getText().toString());
                    joinDto.setPassword(passwordEdt.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), JoinActivity2.class);
                    intent.putExtra("joinDto", joinDto);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"비밀번호를 8자리 이상 입력하시오",Toast.LENGTH_LONG).show();
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
        passwordEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable password) {
                String text1 =password.toString();
                String text2 =birthEdt.getText().toString();
                String text3 =nameEdt.getText().toString();
                if (text1.length() > 0 && text2.length() > 0 && text3.length() > 0) {
                    onNextBtn();
                }
                else {
                    offNextBtn();
                }
            }
        });
        birthEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable birth) {
                String text1 =birth.toString();
                String text2 =passwordEdt.getText().toString();
                String text3 =nameEdt.getText().toString();
                if (text1.length() > 0 && text2.length() > 0 && text3.length() > 0) {
                    onNextBtn();
                }
                else {
                    offNextBtn();
                }
            }
        });
        nameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable name) {
                String text1 =name.toString();
                String text2 =birthEdt.getText().toString();
                String text3 =passwordEdt.getText().toString();
                if (text1.length() > 0 && text2.length() > 0 && text3.length() > 0) {
                    onNextBtn();
                }
                else {
                    offNextBtn();
                }
            }
        });
    }
    // 다음 버튼 활성화
    private void onNextBtn() {
        nextBtn.setBackgroundResource(R.drawable.ic_pale_sky_blue_rounded_rectangle);
        nextBtn.setTextColor(getResources().getColor(R.color.black));
        nextBtn.setEnabled(true);
    }

    // 다음 버튼 비활성화
    private void offNextBtn() {
        nextBtn.setBackgroundResource(R.drawable.ic_disabled_button);
        nextBtn.setTextColor(getResources().getColor(R.color.sub_gray));
        nextBtn.setEnabled(false);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}