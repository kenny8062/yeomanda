package com.example.yeomanda.joinActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yeomanda.R;
import com.example.yeomanda.SendMail;

public class JoinActivity1 extends AppCompatActivity {
    Button nextBtn,certificationBtn;
    EditText emailEdt,passwordEdt;
    private String emailAuth,user_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join1);

    }
    public void init(){
        nextBtn=findViewById(R.id.nextBtn);
        certificationBtn=findViewById(R.id.certificationBtn);
        emailEdt=findViewById(R.id.emailEdt);
        passwordEdt=findViewById(R.id.passwordEdt);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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