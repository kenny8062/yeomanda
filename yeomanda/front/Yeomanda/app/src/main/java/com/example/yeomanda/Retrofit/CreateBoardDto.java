package com.example.yeomanda.Retrofit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateBoardDto {

    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("travelMate")
    @Expose
    private String travelMate;
    @SerializedName("travelDate")
    @Expose
    private String travelDate;
    @SerializedName("teamName")
    @Expose
    private String teamName;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTravelMate() {
        return travelMate;
    }

    public void setTravelMate(String travelMate) {
        this.travelMate = travelMate;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }


    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

}
