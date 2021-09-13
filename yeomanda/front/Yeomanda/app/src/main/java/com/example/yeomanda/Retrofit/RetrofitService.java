package com.example.yeomanda.Retrofit;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitService {

    @Multipart
    @POST("/user/signup")
    Call<JoinResponseDto> uploadJoin(
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
      @Body ArrayList<CreateBoardDto> createBoardDto
    );

    @POST("travelers/showTravelers")
    Call<LocationResponseDto> sendLocation(
            @Body LocationDto locationDto
    );

    @POST("markup/userDetail")
    Call<ProfileResponseDto> showProfile(@Header("Authorization") String userToken,
            @Body EmailDto emailDto
    );

    @GET("markup/favorite/{team_no}")
    Call<CreateOrDeleteFavoriteTeamResponseDto> postFavoriteTeam(@Header("Authorization") String userToken,
                                                                 @Path("team_no") Integer team_no
    );

    @GET("menuBar/deleteFavorite/{team_no}")
    Call<CreateOrDeleteFavoriteTeamResponseDto> deleteFavoriteTeam(@Header("Authorization") String userToken,
                                                                   @Path("team_no") Integer team_no
    );

    @GET("menuBar/showFavoriteTeamName")
    Call<MyFavoriteListResponseDto> showMyFavoriteTeam(@Header("Authorization") String userToken

    );

    @GET("menuBar/showFavoritesDetail/{teamName}")
    Call<MyFavoriteTeamProfileResponseDto> showMyFavriteTeamProfile(@Header("Authorization") String userToken,
                                                                    @Path("teamName") String teamName);
}
