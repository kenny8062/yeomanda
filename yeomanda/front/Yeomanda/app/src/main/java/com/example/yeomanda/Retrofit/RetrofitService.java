package com.example.yeomanda.Retrofit;

import com.example.yeomanda.Retrofit.RequestDto.CreateBoardDto;
import com.example.yeomanda.Retrofit.RequestDto.EmailDto;
import com.example.yeomanda.Retrofit.RequestDto.LocationDto;
import com.example.yeomanda.Retrofit.RequestDto.LoginDto;
import com.example.yeomanda.Retrofit.ResponseDto.ChatListResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ChatRoomResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.CreateBoardResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.WithoutDataResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.JoinResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.LocationResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.LoginResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.MyFavoriteListResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.MyFavoriteTeamProfileResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ProfileResponseDto;

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

    @GET("menuBar/finishTravel")
    Call<WithoutDataResponseDto> deleteBoard(
            @Header("Authorization") String userToken
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
    Call<WithoutDataResponseDto> postFavoriteTeam(@Header("Authorization") String userToken,
                                                  @Path("team_no") Integer team_no
    );

    @GET("menuBar/deleteFavorite/{team_no}")
    Call<WithoutDataResponseDto> deleteFavoriteTeam(@Header("Authorization") String userToken,
                                                    @Path("team_no") Integer team_no
    );

    @GET("menuBar/showFavoriteTeamName")
    Call<MyFavoriteListResponseDto> showMyFavoriteTeam(@Header("Authorization") String userToken

    );

    @GET("menuBar/showFavoritesDetail/{teamName}")
    Call<MyFavoriteTeamProfileResponseDto> showMyFavriteTeamProfile(@Header("Authorization") String userToken,
                                                                    @Path("teamName") String teamName);

    @GET("chatting/InToChatRoom/{otherTeamNum}")
    Call<ChatRoomResponseDto> markerToChat(@Header("Authorization") String userToken,
                                           @Path("otherTeamNum") String otherTeamNum);
    @GET("chatting/getAllMyChatList")
    Call<ChatListResponseDto> getChatList(@Header("Authorization") String userToken
    );
}
