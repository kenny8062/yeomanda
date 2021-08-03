package com.example.yeomanda.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitService {

    @Multipart
    @POST("/user/signup")
    Call<JoinResponseDto> uploadJoin(
//            @Part("email") String email,
//            @Part("password") String password,
//            @Part("name") String name,
//            @Part("sex") String sex,
//            @Part("birth") String birth,
//            @Part MultipartBody.Part[] totalselfimage);
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("name") RequestBody name,
            @Part("sex") RequestBody sex,
            @Part("birth") RequestBody birth,
            @Part MultipartBody.Part[] totalselfimage);

    @POST("/user/login")
    Call<LoginResponseDto> login(
            @Body LoginDto loginDto
            );

    @POST("travelers/registerPlan")
    Call<CreateBoardResponseDto> createBoard(
      @Body CreateBoardDto[] createBoardDto
    );
}
