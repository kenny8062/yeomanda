package com.example.yeomanda.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface JoinService {

    @Multipart
    @POST("/member")
    Call<JoinResponceDto> uploadJoin(
            @Part("email") RequestBody email,
            @Part("Password") RequestBody password,
            @Part("Name") RequestBody name,
            @Part("sex") RequestBody sex,
            @Part("birth") RequestBody birth,
            @Part MultipartBody.Part[] totalselfimage);

}
