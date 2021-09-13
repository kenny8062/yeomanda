package com.example.yeomanda.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostFavoriteTeamDto {


    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("favorite_team_no")
    @Expose
    private Integer favoriteTeamNo;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getFavoriteTeamNo() {
        return favoriteTeamNo;
    }

    public void setFavoriteTeamNo(Integer favoriteTeamNo) {
        this.favoriteTeamNo = favoriteTeamNo;
    }

}
