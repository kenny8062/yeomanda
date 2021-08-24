package com.example.yeomanda.Retrofit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TeamInfoDto {
    @SerializedName("email")
    @Expose
    private ArrayList<String> email;
    @SerializedName("location_gps")
    @Expose
    private String locationGps;
    @SerializedName("team_no")
    @Expose
    private Integer teamNo;
    @SerializedName("travelDate")
    @Expose
    private String travelDate;
    @SerializedName("isfinished")
    @Expose
    private Integer isfinished;
    @SerializedName("region_info")
    @Expose
    private String regionInfo;
    @SerializedName("name")
    @Expose
    private ArrayList<String> nameList;


    public ArrayList<String> getEmail() {
        return email;
    }

    public void setEmail(ArrayList<String> email) {
        this.email = email;
    }

    public String getLocationGps() {
        return locationGps;
    }

    public void setLocationGps(String locationGps) {
        this.locationGps = locationGps;
    }

    public Integer getTeamNo() {
        return teamNo;
    }

    public void setTeamNo(Integer teamNo) {
        this.teamNo = teamNo;
    }

    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(String travelDate) {
        this.travelDate = travelDate;
    }

    public Integer getIsfinished() {
        return isfinished;
    }

    public void setIsfinished(Integer isfinished) {
        this.isfinished = isfinished;
    }

    public String getRegionInfo() {
        return regionInfo;
    }

    public void setRegionInfo(String regionInfo) {
        this.regionInfo = regionInfo;
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }

    public void setNameList(ArrayList<String> nameList) {
        this.nameList = nameList;
    }

}