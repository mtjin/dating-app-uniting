package com.unilab.uniting.activities.launch.signup;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.LaunchBasicActivity;
import com.unilab.uniting.utils.LogData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PreviewActivity extends LaunchBasicActivity {


    private ImageButton mTab0Button;
    private ImageButton mTab1Button;
    private ImageButton mTab2Button;
    private ImageButton mTab3Button;
    private ImageButton mTab4Button;
    private TextView mTitleTextView;
    private ImageView mTitleImageView;
    private Toolbar mMainToolbar;
    private TextView mTab0TextView;
    private TextView mTab1TextView;
    private TextView mTab2TextView;
    private TextView mTab3TextView;
    private TextView mTab4TextView;
    private ImageView mPreviewImgView;
    private LinearLayout mBackLayout;


    private ArrayList<String> storagePathList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private Map<Integer,String> imageMap = new HashMap<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        init();
        updateUI(0);
        loadImageUrl();
        setOnClickListener();
        LogData.eventLog(LogData.Preview, this);
    }

    private void loadImageUrl(){
        StorageReference storageRef = storage.getReference();

        for(int i : Arrays.asList(1,2,3,4,5)){
            storagePathList.add("Preview/" + i +".png");
        }

        count = 0;
        imageList = new ArrayList<>();

        for(int i = 0; i < storagePathList.size(); i++){
            StorageReference spaceRef = storageRef.child(storagePathList.get(i));

            int finalI = i;
            spaceRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageMap.put(finalI, uri.toString());

                            Log.d("test1111", uri.toString());
                            count++;
                            if(count == 5){
                                setImageList();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    count++;
                    if(count == 5){
                        setImageList();
                    }
                }
            });
        }
    }

    private void setImageList(){
        for(int i : Arrays.asList(0,1,2,3,4,5)) {
            if (imageMap.get(i) != null) {
                imageList.add(imageMap.get(i));
            }
        }

        updateUI(0);

    }



    private void init() {
        //xml 바인딩
        mTab0Button = findViewById(R.id.home_main_ibtn_tab0);
        mTab1Button = findViewById(R.id.home_main_ibtn_tab1);
        mTab2Button = findViewById(R.id.home_main_ibtn_tab2);
        mTab3Button = findViewById(R.id.home_main_ibtn_tab3);
        mTab4Button = findViewById(R.id.home_main_ibtn_tab4);
        mTab0TextView = findViewById(R.id.home_main_tv_tab0);
        mTab1TextView = findViewById(R.id.home_main_tv_tab1);
        mTab2TextView = findViewById(R.id.home_main_tv_tab2);
        mTab3TextView = findViewById(R.id.home_main_tv_tab3);
        mTab4TextView = findViewById(R.id.home_main_tv_tab4);

        mTitleTextView = findViewById(R.id.home_main_toolbar_tv_title);
        mTitleImageView = findViewById(R.id.home_main_toolbar_iv_title);
        mMainToolbar = findViewById(R.id.home_main_toolbar);
        mPreviewImgView = findViewById(R.id.preview_iv);
        mBackLayout = findViewById(R.id.home_main_toolbar_li_profile);
    }

    private void updateUI(int pageNumber){
        mTab0Button.setImageResource(R.drawable.ic_bar0_home);
        mTab1Button.setImageResource(R.drawable.ic_bar1_people);
        mTab2Button.setImageResource(R.drawable.ic_bar2_star);
        mTab3Button.setImageResource(R.drawable.ic_experiment);
        mTab4Button.setImageResource(R.drawable.ic_bar4_chat);
        mTab0TextView.setTextColor(getResources().getColor(R.color.colorLightGray160));
        mTab1TextView.setTextColor(getResources().getColor(R.color.colorLightGray160));
        mTab2TextView.setTextColor(getResources().getColor(R.color.colorLightGray160));
        mTab3TextView.setTextColor(getResources().getColor(R.color.colorLightGray160));
        mTab4TextView.setTextColor(getResources().getColor(R.color.colorLightGray160));
        mTitleTextView.setTextSize(17);
        mTitleTextView.setVisibility(View.VISIBLE);
        mTitleImageView.setVisibility(View.VISIBLE);


        switch (pageNumber){
            case 0:
                mTitleImageView.setVisibility(View.VISIBLE);
                mTitleTextView.setVisibility(View.GONE);
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab0Button.setImageResource(R.drawable.ic_bar0_home_pink);
                mTab0TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(imageList.size() > pageNumber){
                    Glide.with(PreviewActivity.this)
                            .load(imageList.get(pageNumber))
                            .into(mPreviewImgView);
                }
                break;
            case 1:
                mTitleImageView.setVisibility(View.VISIBLE);
                mTitleTextView.setVisibility(View.GONE);
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab1Button.setImageResource(R.drawable.ic_bar1_people_pink);
                mTab1TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(imageList.size() > pageNumber){
                    Glide.with(PreviewActivity.this)
                            .load(imageList.get(pageNumber))
                            .into(mPreviewImgView);
                }
                break;
            case 2:
                mMainToolbar.setVisibility(View.GONE);
                mTab2Button.setImageResource(R.drawable.ic_bar2_star_pink);
                mTab2TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(imageList.size() > pageNumber){
                    Glide.with(PreviewActivity.this)
                            .load(imageList.get(pageNumber))
                            .into(mPreviewImgView);
                }
                break;
            case 3:
                mTitleImageView.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText("실험실");
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab3Button.setImageResource(R.drawable.ic_experiment_pink);
                mTab3TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(imageList.size() > pageNumber){
                    Glide.with(PreviewActivity.this)
                            .load(imageList.get(pageNumber))
                            .into(mPreviewImgView);
                }
                break;
            case 4:
                mTitleImageView.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText(R.string.toolbar_title_message);
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab4Button.setImageResource(R.drawable.ic_bar4_chat_pink);
                mTab4TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(imageList.size() > pageNumber){
                    Glide.with(PreviewActivity.this)
                            .load(imageList.get(pageNumber))
                            .into(mPreviewImgView);
                }
                break;
        }
    }

    private void setOnClickListener() {
        View.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.home_main_ibtn_tab0:
                        updateUI(0);
                        break;
                    case R.id.home_main_ibtn_tab1:
                        updateUI(1);
                        break;
                    case R.id.home_main_ibtn_tab2:
                        updateUI(2);
                        break;
                    case R.id.home_main_ibtn_tab3:
                        updateUI(3);
                        break;
                    case R.id.home_main_ibtn_tab4:
                        updateUI(4);
                        break;

                }
            }
        };

        mTab0Button.setOnClickListener(onClickListener);
        mTab1Button.setOnClickListener(onClickListener);
        mTab2Button.setOnClickListener(onClickListener);
        mTab3Button.setOnClickListener(onClickListener);
        mTab4Button.setOnClickListener(onClickListener);

        mPreviewImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PreviewActivity.this, "둘러보기용 데모버전입니다. 회원가입 후 유니팅을 즐겨주세요!", Toast.LENGTH_SHORT).show();
            }
        });

        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
