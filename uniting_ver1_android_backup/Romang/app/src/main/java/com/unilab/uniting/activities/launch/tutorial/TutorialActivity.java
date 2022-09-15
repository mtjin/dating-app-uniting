package com.unilab.uniting.activities.launch.tutorial;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.CircleAnimIndicator;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TutorialActivity extends AppCompatActivity {

    //private Button mCloseForeverBtn;
    private LinearLayout mCloseBtn;

    private ArrayList<String> storagePathList = new ArrayList<>();
    private ArrayList<String> imageList = new ArrayList<>();
    private Map<Integer,String> imageMap = new HashMap<>();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    TutorialPagerAdapter tutorialPagerAdapter;
    ViewPager viewPager;
    CircleAnimIndicator circleAnimIndicator;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);


        init();
        setOnClickListener();
        loadImageUrl();

    }

    private void init(){
        mCloseBtn = findViewById(R.id.toolbar_back);

        LogData.setStageTutorial(0, TutorialActivity.this);
        LogData.eventLog(LogData.tutorial_s0 + 0, TutorialActivity.this);
    }


    private void setOnClickListener(){
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadImageUrl(){
        StorageReference storageRef = storage.getReference();

        for(int i : Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12)){
            storagePathList.add("Tutorial/" + i +".jpg");
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

                            count++;
                            if(count == 12){
                                setImageList();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                    count++;
                    if(count == 12){
                        setImageList();
                    }
                }
            });
        }
    }

    private void setImageList(){
        for(int i : Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11,12)) {
            if (imageMap.get(i) != null) {
                imageList.add(imageMap.get(i));
            }
        }

        setViewPager();

    }

    private void setViewPager(){
        viewPager = findViewById(R.id.tutorial_vp);
        tutorialPagerAdapter = new TutorialPagerAdapter(getSupportFragmentManager(), imageList.size(), imageList, TutorialActivity.this);
        viewPager.setAdapter(tutorialPagerAdapter);
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
        viewPager.setOffscreenPageLimit(10); //캐싱(빠르게 로딩하기위해)

        //인디케이터 설정
        circleAnimIndicator = (CircleAnimIndicator) findViewById(R.id.tutorial_circleAnimIndicator);
        circleAnimIndicator.setItemMargin(15); //원사이의 간격
        circleAnimIndicator.setAnimDuration(300); //애니메이션 속도
        circleAnimIndicator.createDotPanel(imageList.size(), R.drawable.ic_indicator_non , R.drawable.ic_indicator_on); //indicator 생성
    }


    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            LogData.setStageTutorial(position, TutorialActivity.this);
            LogData.eventLog(LogData.tutorial_s0 + position, TutorialActivity.this);
            circleAnimIndicator.selectDot(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setTutorialChecked(){
        SharedPreferences pref = getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(FirebaseHelper.tutorialChecked, true);
        editor.apply();
    }
}
