package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.yeomanda.Retrofit.ResponseDto.ProfileResponseDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import java.util.ArrayList;

import okhttp3.MultipartBody;

public class MyProfile extends AppCompatActivity {
    public static Context context;
    ImageView mySubImage1, myMainImage, mySubImage2;
    TextView myEmail, mySex, myName, myBirth;
    ArrayList<MultipartBody.Part> selfImage=new ArrayList<>();
    ArrayList<MultipartBody.Part> changeUri=new ArrayList<>();
    Button editMyProfileBtn;
    RetrofitClient retrofitClient;
    ProfileResponseDto profileResponseDto;;
    final int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE =1;
    private final int GET_IMAGE_FOR_PICTURE1 = 300;
    private final int GET_IMAGE_FOR_PICTURE2 = 301;
    private final int GET_IMAGE_FOR_PICTURE3 = 302;
    Uri uri[]=new Uri[3];
    boolean isUri[]={false,false,false};
    String myToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},1);
        context=this;
        init();
    }
    public void init(){
        Intent intent=getIntent();
        myToken=intent.getStringExtra("token");
        mySubImage1 =findViewById(R.id.myImage1);
        myMainImage =findViewById(R.id.myMainImage);
        mySubImage2 =findViewById(R.id.myImage2);
        myEmail =findViewById(R.id.myEmail);
        mySex =findViewById(R.id.mySex);
        myName =findViewById(R.id.myName);
        myBirth =findViewById(R.id.myBirth);
        editMyProfileBtn=findViewById(R.id.editMyProfileBtn);

        retrofitClient=new RetrofitClient();
        profileResponseDto=retrofitClient.updateProfile(myToken);
        while(profileResponseDto==null){
            System.out.println("ProfileResponseDto is null");
        }
        Log.d("mySex",profileResponseDto.getData().getSex());
        myEmail.setText(profileResponseDto.getData().getEmail());
        mySex.setText(profileResponseDto.getData().getSex());
        myBirth.setText(profileResponseDto.getData().getBirth());
        myName.setText(profileResponseDto.getData().getName());

        Glide.with(this)
                .load(profileResponseDto.getData().getFiles().get(0))
                .into(myMainImage);

        Glide.with(this)
                .load(profileResponseDto.getData().getFiles().get(1))
                .into(mySubImage1);

        Glide.with(this)
                .load(profileResponseDto.getData().getFiles().get(2))
                .into(mySubImage2);


        editMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<3;i++){
                    if(isUri[i]){
                        selfImage.add(retrofitClient.prepareFilePart("files", uri[i], context));
                        //changeUri.add(retrofitClient.createPartFromString(profileResponseDto.getData().getFiles().get(i)));
                        changeUri.add(MultipartBody.Part.createFormData("updatedURI",profileResponseDto.getData().getFiles().get(i)));
                    }
                }/*
                while(selfImage.size()<3){
                    selfImage.add(null);
                    changeUri.add(null);
                }*/
                retrofitClient.updateMyProfile(myToken,profileResponseDto.getData().getEmail(),selfImage,changeUri);
                finish();
            }


        });

        myMainImage.setOnClickListener(new View.OnClickListener() {      @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_IMAGE_FOR_PICTURE1);
            }
        });
        mySubImage1.setOnClickListener(new View.OnClickListener() {       @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_IMAGE_FOR_PICTURE2);
            }
        });
        mySubImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_IMAGE_FOR_PICTURE3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri;
        MultiTransformation multiOption = new MultiTransformation(new CenterCrop(), new RoundedCorners(8));

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            switch (requestCode) {
                case GET_IMAGE_FOR_PICTURE1:
                    selectedImageUri = data.getData();
                    uri[0] = selectedImageUri;
                    isUri[0]=true;
                    Glide.with(getApplicationContext()).asBitmap().load(selectedImageUri).apply(RequestOptions.bitmapTransform(multiOption)).into(myMainImage);     break;
                case GET_IMAGE_FOR_PICTURE2:
                    selectedImageUri = data.getData();
                    uri[1] = selectedImageUri;
                    isUri[1]=true;
                    Glide.with(getApplicationContext()).asBitmap().load(selectedImageUri).apply(RequestOptions.bitmapTransform(multiOption)).into(mySubImage1);   break;
                case GET_IMAGE_FOR_PICTURE3:
                    selectedImageUri = data.getData();
                    uri[2] = selectedImageUri;
                    isUri[2] = true;
                    Glide.with(getApplicationContext()).asBitmap().load(selectedImageUri).apply(RequestOptions.bitmapTransform(multiOption)).into(mySubImage2);break;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


}