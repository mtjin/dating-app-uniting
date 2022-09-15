package com.unilab.uniting.activities;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.BasicActivity;

public class PhotoViewActivity extends BasicActivity {
    public static final String EXTRA_PHOTOVIEW = "EXTRA_PHOTOVIEW";
    private String mPhotoUrl = "";
    //xml
    private PhotoView mPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        mPhotoView = findViewById(R.id.photoview);
        processIntent();
    }

    private void processIntent() {
        Intent resultIntent = getIntent();
        mPhotoUrl =  resultIntent.getStringExtra(EXTRA_PHOTOVIEW);
        Glide.with(this).load(mPhotoUrl).thumbnail(0.1f).into(mPhotoView);
    }
}
