package com.example.yeomanda.joinActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.yeomanda.LoginActivity;
import com.example.yeomanda.R;
import com.example.yeomanda.Retrofit.RequestDto.JoinDto;
import com.example.yeomanda.Retrofit.RetrofitClient;

import okhttp3.MultipartBody;

public class JoinActivity2 extends AppCompatActivity {
    public static Context context;
    JoinDto joinDto;
    Button nextBtn;
    ImageView selfimage1,selfimage2,selfimage3;
    MultipartBody.Part[] selfimage=new MultipartBody.Part[3];
    Uri uri[]=new Uri[3];
    boolean isComplete[]={false,false,false};
    final int MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE =1;
    private final int GET_IMAGE_FOR_PICTURE1 = 300;
    private final int GET_IMAGE_FOR_PICTURE2 = 301;
    private final int GET_IMAGE_FOR_PICTURE3 = 302;

    RetrofitClient retrofitClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join2);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},1);
        retrofitClient=new RetrofitClient();
        context=this;
        init();
    }

    public void init(){
        Intent intent=getIntent();
        joinDto= (JoinDto) intent.getSerializableExtra("joinDto");

        nextBtn=findViewById(R.id.nextBtn3);
        selfimage1=findViewById(R.id.selfimage1);
        selfimage2=findViewById(R.id.selfimage2);
        selfimage3=findViewById(R.id.selfimage3);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<3;i++){
                    selfimage[i]=retrofitClient.prepareFilePart("files", uri[i], context);
                }
                retrofitClient.uploadSign_up(joinDto,selfimage);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        selfimage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_IMAGE_FOR_PICTURE1);
            }
        });
        selfimage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_IMAGE_FOR_PICTURE2);
            }
        });
        selfimage3.setOnClickListener(new View.OnClickListener() {
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
                    Glide.with(getApplicationContext()).asBitmap().load(selectedImageUri).apply(RequestOptions.bitmapTransform(multiOption)).into(selfimage1);
                    isComplete[0]=true;
                    if(isComplete[0]&&isComplete[1]&&isComplete[2]) onNextBtn();
                    break;
                case GET_IMAGE_FOR_PICTURE2:
                    selectedImageUri = data.getData();
                    uri[1] = selectedImageUri;
                    Glide.with(getApplicationContext()).asBitmap().load(selectedImageUri).apply(RequestOptions.bitmapTransform(multiOption)).into(selfimage2);
                    isComplete[1]=true;
                    if(isComplete[0]&&isComplete[1]&&isComplete[2]) onNextBtn();
                    break;
                case GET_IMAGE_FOR_PICTURE3:
                    selectedImageUri = data.getData();
                    uri[2] = selectedImageUri;
                    Glide.with(getApplicationContext()).asBitmap().load(selectedImageUri).apply(RequestOptions.bitmapTransform(multiOption)).into(selfimage3);
                    isComplete[2]=true;
                    if(isComplete[0]&&isComplete[1]&&isComplete[2]) onNextBtn();
                    break;
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
    // 다음 버튼 활성화
    private void onNextBtn() {
        nextBtn.setBackgroundResource(R.drawable.ic_pale_sky_blue_rounded_rectangle);
        nextBtn.setTextColor(getResources().getColor(R.color.black));
        nextBtn.setEnabled(true);
    }


}