package com.example.yeomanda.Retrofit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateBoardDto {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("travelMate")
    @Expose
    private String travelMate;
    @SerializedName("travelDate")
    @Expose
    private String travelDate;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
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

}
