package com.example.yeomanda.Retrofit.ResponseDto;

import java.util.ArrayList;

import com.example.yeomanda.Retrofit.RequestDto.TeamInfoDto;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class LocationResponseDto {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private ArrayList<TeamInfoDto> data = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<TeamInfoDto> getData() {
        return data;
    }

    public void setData(ArrayList<TeamInfoDto> data) {
        this.data = data;
    }

}