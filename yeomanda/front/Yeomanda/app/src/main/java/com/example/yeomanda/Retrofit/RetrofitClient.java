package com.example.yeomanda.Retrofit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.loader.content.CursorLoader;

import com.example.yeomanda.joinActivity.JoinActivity3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitService retrofitService;
    private static JoinResponseDto joinResponseDto =null;
    private static LoginResponseDto loginResponseDto=null;
    private static CreateBoardResponseDto createBoardResponseDto=null;
    private static LocationResponseDto locationResponseDto=null;
    private static ProfileResponseDto profileResponseDto= null;
    public RetrofitClient() {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.20.10.14:3000/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        retrofitService =retrofit.create(RetrofitService.class);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(JoinActivity3.context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public void uploadSign_up(JoinDto joinDto, MultipartBody.Part[] selfimage){
        Thread thread = new Thread() {
            @Override
            public void run() {
//                try {
//                    Log.d("Tag",joinDto.getEmail());
//                    Log.d("Tag",joinDto.getPassword());
//                    Log.d("Tag",joinDto.getName());
//                    Log.d("Tag",joinDto.getSex());
//                    Log.d("Tag",joinDto.getBirth());
//                    joinResponseDto = retrofitService.uploadJoin(joinDto.getEmail(),joinDto.getPassword(),joinDto.getName(),joinDto.getSex(),joinDto.getBirth(),selfimage).execute().body();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                try {
                    joinResponseDto = retrofitService.uploadJoin(createPartFromString(joinDto.getEmail()),createPartFromString(joinDto.getPassword()),createPartFromString(joinDto.getName()),createPartFromString(joinDto.getSex()),createPartFromString(joinDto.getBirth()),selfimage).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //sign_up_responseDTO = studentcard_upload_service.uploadFile(createPartFromString(sign_upDTO.getSchoolname()),createPartFromString(sign_upDTO.getSchoolnum()),createPartFromString(sign_upDTO.getEmail()),createPartFromString(sign_upDTO.getPassword()),createPartFromString(sign_upDTO.getName()),createPartFromString(sign_upDTO.getSex()),
                //        createPartFromString(sign_upDTO.getAge()),createPartFromString(sign_upDTO.getRegion()),createPartFromString(sign_upDTO.getHobby()),studentcard,selfimage).execute().body();
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
    private RequestBody createPartFromString(String descriptionString) {
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
            return locationResponseDto;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ProfileResponseDto showProfile(String email){
        Thread thread = new Thread(){
          @Override
          public void run(){
              try {
                  Log.d("EmailDto",email);
                  EmailDto emailDto1 =new EmailDto();
                  emailDto1.setEmail(email);
                  profileResponseDto=retrofitService.showProfile(emailDto1).execute().body();
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
}
