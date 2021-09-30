package com.example.yeomanda.Retrofit.ResponseDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatMessages2 {


    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("senderName")
    @Expose
    private String senderName;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("senderEmail")
    @Expose
    private String senderEmail;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
