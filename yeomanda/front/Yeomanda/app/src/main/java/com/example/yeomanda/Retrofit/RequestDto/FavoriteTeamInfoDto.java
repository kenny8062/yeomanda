package com.example.yeomanda.Retrofit.RequestDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FavoriteTeamInfoDto {
    @SerializedName("email")
    @Expose
    private List<String> email = null;
    @SerializedName("name")
    @Expose
    private List<String> name = null;
    @SerializedName("birth")
    @Expose
    private List<String> birth = null;
    @SerializedName("url")
    @Expose
    private List<String> url = null;
    @SerializedName("teamName")
    @Expose
    private String teamName;

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getBirth() {
        return birth;
    }

    public void setBirth(List<String> birth) {
        this.birth = birth;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

}
