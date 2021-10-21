package com.example.yeomanda.Retrofit.RequestDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginDto {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    @SerializedName("fcm_token")
    @Expose
    private String fcm_token;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

}