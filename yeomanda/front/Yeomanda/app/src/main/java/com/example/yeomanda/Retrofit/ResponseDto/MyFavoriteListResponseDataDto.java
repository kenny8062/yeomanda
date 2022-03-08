package com.example.yeomanda.Retrofit.ResponseDto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyFavoriteListResponseDataDto {


    @SerializedName("team_name")
    @Expose
    private String teamName;

    @SerializedName("member")
    @Expose
    private Integer member;

    @SerializedName("team_no")
    @Expose
    private Integer teamNum;

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getMember() {
        return member;
    }

    public void setMember(Integer member) {
        this.member = member;
    }

    public Integer getTeamNum() {
        return teamNum;
    }

    public void setTeamNum(Integer teamNum) {
        this.teamNum = teamNum;
    }
}