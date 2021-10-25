package com.example.yeomanda.Retrofit;

import com.example.yeomanda.Retrofit.RequestDto.CreateBoardDto;
import com.example.yeomanda.Retrofit.RequestDto.EmailDto;
import com.example.yeomanda.Retrofit.RequestDto.LocationDto;
import com.example.yeomanda.Retrofit.RequestDto.LoginDto;
import com.example.yeomanda.Retrofit.ResponseDto.AllMyChatsResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ChatListResponseDto;
import com.example.yeomanda.Retrofit.ResponseDto.ChatMessages;
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
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface RetrofitService {

    //회원가입
    @Multipart
    @POST("/user/signup")
    Call<JoinResponseDto> uploadJoin(
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("name") RequestBody name,
            @Part("sex") RequestBody sex,
            @Part("birth") RequestBody birth,
            @Part MultipartBody.Part[] totalSelfImage);

    //내 프로필 가져오기
    @GET("/menuBar/getProfile")
    Call<ProfileResponseDto> getMyProfile(@Header("Authorization") String userToken
    );

    //내 프로필 수정
    @Multipart
    @POST("menuBar/updateProfile")
    Call<WithoutDataResponseDto> updateMyProfile(@Header("Authorization") String userToken,
                                                 @Part("email") RequestBody email,
                                                 @Part ArrayList<MultipartBody.Part> uri,
                                                 @Part ArrayList<MultipartBody.Part> totalSelfImage);
    //로그인
    @POST("/user/login")
    Call<LoginResponseDto> login(
            @Body LoginDto loginDto
            );

    //여행 계획 추가
    @POST("travelers/registerPlan")
    Call<CreateBoardResponseDto> createBoard(
      @Body ArrayList<CreateBoardDto> createBoardDto
    );

    //여행 취소
    @GET("menuBar/finishTravel")
    Call<WithoutDataResponseDto> deleteBoard(
            @Header("Authorization") String userToken
    );

    //내 위치 서버로 전송
    @POST("travelers/showTravelers")
    Call<LocationResponseDto> sendLocation(
            @Body LocationDto locationDto
    );

    //다른 사람 프로필 보기
    @POST("markup/userDetail")
    Call<ProfileResponseDto> showProfile(@Header("Authorization") String userToken,
                                         @Body EmailDto emailDto
    );

    //즐겨찾기 추가
    @GET("markup/favorite/{team_no}")
    Call<WithoutDataResponseDto> postFavoriteTeam(@Header("Authorization") String userToken,
                                                  @Path("team_no") Integer team_no
    );

    //즐겨찾기 삭제
    @GET("menuBar/deleteFavorite/{team_no}")
    Call<WithoutDataResponseDto> deleteFavoriteTeam(@Header("Authorization") String userToken,
                                                    @Path("team_no") Integer team_no
    );

    //즐겨찾기 팀 리스트 보기
    @GET("menuBar/showFavoriteTeamName")
    Call<MyFavoriteListResponseDto> showMyFavoriteTeam(@Header("Authorization") String userToken

    );

    //즐겨찾기 팀 상세 정보
    @GET("menuBar/showFavoritesDetail/{teamName}")
    Call<MyFavoriteTeamProfileResponseDto> showMyFavriteTeamProfile(@Header("Authorization") String userToken,
                                                                    @Path("teamName") String teamName);

    //채팅방 생성성
    @GET("chatting/InToChatRoom/{otherTeamNum}")
    Call<ChatRoomResponseDto> markerToChat(@Header("Authorization") String userToken,
                                           @Path("otherTeamNum") String otherTeamNum);
    //채팅 리스트 가져오기
    @GET("chatting/getAllMyChatList")
    Call<ChatListResponseDto> getChatList(@Header("Authorization") String userToken
    );

    //기존에 있던 채팅기록 가져오기
    @FormUrlEncoded
    @POST("chatting/getAllMyChats")
    Call<AllMyChatsResponseDto> getAllMyChats(@Header("Authorization") String userToken,
                                              @Field("chatRoomId") String chatRoomId);

    /*@FormUrlEncoded
    @POST("socket/caching")
    Call<WithoutDataResponseDto> closeSocket(@Header("Authorization") String userToken,
                                            @Field("room_id") String chatRoomId);
*/
}
