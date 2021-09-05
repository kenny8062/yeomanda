package com.example.yeomanda.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ProfileDto {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("birth")
    @Expose
    private String birth;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("files")
    @Expose
    private ArrayList<String> files = null;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<String> files) {
        this.files = files;
    }
}
