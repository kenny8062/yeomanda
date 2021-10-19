package com.example.yeomanda.Retrofit.ResponseDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponseDataDto {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("hasPlanned")
    @Expose
    private Boolean hasPlanned;

    public Boolean getHasPlanned() {
        return hasPlanned;
    }

    public void setHasPlanned(Boolean hasPlanned) {
        this.hasPlanned = hasPlanned;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}