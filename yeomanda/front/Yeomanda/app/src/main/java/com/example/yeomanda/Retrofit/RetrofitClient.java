package com.example.yeomanda.Retrofit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;

import com.example.yeomanda.Retrofit.RequestDto.CreateBoardDto;
import com.example.yeomanda.Retrofit.RequestDto.EmailDto;
import com.example.yeomanda.Retrofit.RequestDto.JoinDto;
import com.example.yeomanda.Retrofit.RequestDto.LocationDto;
import com.example.yeomanda.Retrofit.RequestDto.LoginDto;
import com.example.yeomanda.Retrofit.ResponseDto.AllMyChatsResponseDto;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitService retrofitService;
    private static JoinResponseDto joinResponseDto =null;
    private static LoginResponseDto loginResponseDto=null;
    private static CreateBoardResponseDto createBoardResponseDto=null;
    private static LocationResponseDto locationResponseDto=null;
    private static ProfileResponseDto profileResponseDto= null;
    private static WithoutDataResponseDto withoutDataResponseDto =null;
    private static MyFavoriteListResponseDto myFavoriteListResponseDto=null;
    private static MyFavoriteTeamProfileResponseDto myFavoriteTeamProfileResponseDto=null;
    private static ChatRoomResponseDto chatRoomResponseDto=null;
    private static ChatListResponseDto chatListResponseDto=null;
    private static AllMyChatsResponseDto allMyChatsResponseDto=null;
    public RetrofitClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://172.30.1.7:3000/")
                .baseUrl("http://ec2-54-180-202-228.ap-northeast-2.compute.amazonaws.com:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        retrofitService =retrofit.create(RetrofitService.class);
    }

    public void uploadSign_up(JoinDto joinDto, MultipartBody.Part[] selfImage){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    joinResponseDto = retrofitService.uploadJoin(createPartFromString(joinDto.getEmail()),createPartFromString(joinDto.getPassword()),createPartFromString(joinDto.getName()),createPartFromString(joinDto.getSex()),createPartFromString(joinDto.getBirth()),selfImage).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    public void updateMyProfile(String myToken,String email,ArrayList<MultipartBody.Part> selfImage,ArrayList<MultipartBody.Part> changeUri){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    withoutDataResponseDto = retrofitService.updateMyProfile(myToken,createPartFromString(email),changeUri,selfImage).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private String getRealPathFromURI(Uri contentUri,Context context) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @NonNull
    public RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse("text/plain"), descriptionString);
    }

    @NonNull
    public MultipartBody.Part prepareFilePart(String partName, Uri fileUri, Context context) {
        File file = new File(getRealPathFromURI(fileUri,context));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
    public LoginResponseDto login(LoginDto loginDto){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    loginResponseDto = retrofitService.login(loginDto).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            Log.d("t",loginResponseDto.getMessage());
            return loginResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CreateBoardResponseDto createboard(ArrayList<CreateBoardDto> createBoardDto){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    createBoardResponseDto = retrofitService.createBoard(createBoardDto).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            Log.d("t",createBoardResponseDto.getMessage());
            return createBoardResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public WithoutDataResponseDto deleteBoard(String userToken){
        Thread thread = new Thread(){
            @Override
            public  void run() {
                try {
                    withoutDataResponseDto = retrofitService.deleteBoard(userToken).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            return withoutDataResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public LocationResponseDto sendLocation(LocationDto locationDto){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    locationResponseDto=retrofitService.sendLocation(locationDto).execute().body();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            Log.d("locationResponseDtotest",locationResponseDto.getMessage());

            return locationResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProfileResponseDto showProfile(String token,String email){
        Thread thread = new Thread(){
          @Override
          public void run(){
              try {
                  Log.d("EmailDto",email);
                  EmailDto emailDto1 =new EmailDto();
                  emailDto1.setEmail(email);
                  profileResponseDto=retrofitService.showProfile(token,emailDto1).execute().body();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
        };
        thread.start();

        try {
            thread.join();
            return profileResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProfileResponseDto updateProfile(String token){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    profileResponseDto=retrofitService.getMyProfile(token).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            return profileResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
    public WithoutDataResponseDto postFavoriteTeam(String userToken, Integer teamNum){
        Thread thread = new Thread(){
            @Override
            public  void run() {
                try {
                    withoutDataResponseDto = retrofitService.postFavoriteTeam(userToken, teamNum).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            return withoutDataResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public WithoutDataResponseDto deleteFavoriteTeam(String userToken, Integer teamNum){
        Thread thread = new Thread(){
            @Override
            public  void run() {
                try {
                    withoutDataResponseDto = retrofitService.postFavoriteTeam(userToken, teamNum).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        try {
            thread.join();
            return withoutDataResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MyFavoriteListResponseDto showMyFavoriteTeamList(String userToken){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    myFavoriteListResponseDto=retrofitService.showMyFavoriteTeam(userToken).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            Log.d("tag",myFavoriteListResponseDto.getMessage());
            return myFavoriteListResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public MyFavoriteTeamProfileResponseDto showMyFavoriteTeamProfile(String token,String teamName){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    Log.d("teamName is",teamName);
                    Log.d("teamName is",token);

                    myFavoriteTeamProfileResponseDto=retrofitService.showMyFavriteTeamProfile(token,teamName).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            Log.d("tag",myFavoriteTeamProfileResponseDto.getMessage());
            return myFavoriteTeamProfileResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ChatRoomResponseDto inToChatRoom(String token,String teamNum){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    chatRoomResponseDto=retrofitService.markerToChat(token,teamNum).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            Log.d("tag",chatRoomResponseDto.getMessage());
            return chatRoomResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }




    public ChatListResponseDto showMyChatList(String userToken){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    chatListResponseDto=retrofitService.getChatList(userToken).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            Log.d("chatListResponseDto",chatListResponseDto.getMessage());
            return chatListResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }


    public AllMyChatsResponseDto getAllMyChats(String token,String roomId){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    allMyChatsResponseDto=retrofitService.getAllMyChats(token,roomId).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            Log.d("tag",allMyChatsResponseDto.getMessage());
            return allMyChatsResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*public WithoutDataResponseDto closeSocket(String token,String roomId){
        Thread thread = new Thread(){
            @Override
            public void run(){
                try {
                    withoutDataResponseDto=retrofitService.closeSocket(token,roomId).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
            Log.d("WithOutDataResponseDto",withoutDataResponseDto.getMessage());
            return withoutDataResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }*/
}
