package com.example.yeomanda;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SelectImageActivity extends AppCompatActivity {

    String uri;
    ImageView selectImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);
        selectImageView=findViewById(R.id.selectPersonImage);
        uri=getIntent().getStringExtra("uri");
        Glide.with(this).load(uri).into(selectImageView);
    }
}