package com.example.yeomanda.Retrofit.RequestDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmailDto {

    @SerializedName("email")
    @Expose
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
