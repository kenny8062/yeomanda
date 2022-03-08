package com.example.yeomanda.ListView;

public class FavoriteTeamListViewItem {

    private String favoriteTeamName;
    private Integer favoriteTeamCount;

    public void setFavoriteTeamName(String favoriteTeamName) {
        this.favoriteTeamName = favoriteTeamName;
    }
    public void setFavoriteTeamCount(Integer favoriteTeamCount) {
        this.favoriteTeamCount = favoriteTeamCount;
    }

    public String getFavoriteTeamName() {
        return this.favoriteTeamName;
    }
    public Integer getFavoriteTeamCount() {
        return this.favoriteTeamCount;
    }

}
