package com.unilab.uniting.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.unilab.uniting.GeoUtil.GeoUtil;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.Splash;
import com.unilab.uniting.activities.launch.tutorial.Guide2Activity;
import com.unilab.uniting.activities.launch.tutorial.TutorialActivity;
import com.unilab.uniting.activities.notification.NotificationAlarmActivity;
import com.unilab.uniting.activities.setprofile.SetProfileMainActivity;
import com.unilab.uniting.activities.setprofile.SetProfile_2Edit;
import com.unilab.uniting.activities.store.StoreMainActivity;
import com.unilab.uniting.fragments.chatting.Tab4ChattingFragment;
import com.unilab.uniting.fragments.dialog.DialogBlockFragment;
import com.unilab.uniting.fragments.dialog.DialogNoOkFragment;
import com.unilab.uniting.fragments.evaluation.Tab2EvaluationFragment;
import com.unilab.uniting.fragments.home.DialogCloseUserNoOkFragment;
import com.unilab.uniting.fragments.home.MainPermissionBusProvider;
import com.unilab.uniting.fragments.home.Tab0HomeFragment;
import com.unilab.uniting.fragments.meeting.Tab1MeetingFragment;
import com.unilab.uniting.fragments.tier.Tab3TierFragment;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.MyGeoPoint;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

import java.util.HashMap;
import java.util.Map;

class CustomViewPager extends ViewPager {

    private boolean isPagingEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }


}


public class MainActivity extends BasicActivity implements DialogBlockFragment.BlockListener, DialogCloseUserNoOkFragment.DialogCloseUserListener, DialogNoOkFragment.DialogOkListener{

    //태그
    final static String TAG = "MainTAG";

    private int defaultPage = 0;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    private int oldPage = 0;

    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 23;
    private static final int REQUEST_CODE_APP_LOCATION = 222;
    private static final int REQUEST_CODE_ALARM = 20;

    private static final int REQUEST_CODE_GUIDE = 111;

    private static final int REQUEST_CODE_DEVICE_LOCATION= 221;

    //xml
    private CustomViewPager mViewPager;
    private ImageButton mTab0Button;
    private ImageButton mTab1Button;
    private ImageButton mTab2Button;
    private ImageButton mTab3Button;
    private ImageButton mTab4Button;
    private LinearLayout mProfileLinearLayout;
    private TextView mTitleTextView;
    private ImageView mTitleImageView;
    private LinearLayout mRingBellLinearLayout;
    private LinearLayout mHeartBellLinearLayout;
    private Toolbar mMainToolbar;
    private ImageView mRingBellImageView;
    private TextView mTab0TextView;
    private TextView mTab1TextView;
    private TextView mTab2TextView;
    private TextView mTab3TextView;
    private TextView mTab4TextView;
    private RelativeLayout loaderLayout;

    //리스너
    private ListenerRegistration listenerRegistration;
    private ViewPagerClickListener mCommunityListener;
    private ViewPagerClickListener mEvaluationListener;
    private ViewPagerClickListener mMeetingListener;
    private ViewPagerClickListener mTodayIntroListener;
    private BlockEvaluationListener blockEvaluationListener;


    private View.OnClickListener movePageListener;
    private Button.OnClickListener onClickListener;

    private Dia dia;
    private Location userLocation;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkCurrentUser();
        init();
        setLogData();
        setPermissionOnDB();
        setNotice();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GeoUtil.updateLocationPermission(this);
        setMyLocation();

        setOnClickListener();
        setMovePageListener();
        setNotificationListener();

        //툴바 및 뷰페이저 설정
        setSupportActionBar(mMainToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(pagerAdapter);
        //뷰페이저 스크롤막기
        mViewPager.setOnTouchListener((v, event) -> true);
        mViewPager.setOffscreenPageLimit(5); //캐싱(빠르게 로딩하기위해)
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //디폴트 페이지 설정 (처음 defaultPage 값은 0, 서로 연결되어 채팅 페이지 봐야할 때는 4를 받아서 넘어옴)
        Intent intent = getIntent();
        defaultPage = intent.getIntExtra(Strings.defaultPage, 0);
        updateUI(defaultPage);


    }

    private void setNotice(){
        FirebaseHelper.db.collection("Data").document("MainNotice")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String url = (String) document.get("notice");
                        DialogMainNoticeFragment dialog3 = DialogMainNoticeFragment.Companion.getInstance();
                        dialog3.setPhotoUrl(url);
                        dialog3.show(getSupportFragmentManager(), DialogMainNoticeFragment.TAG_LIKE_DIALOG);
                    }
                }
            }
        });


    }

    private void setPermissionOnDB(){
        boolean isLocationPermitted = false;
        if (!GeoUtil.canGetLocation(this) || (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            isLocationPermitted = false;
        } else {
            isLocationPermitted = true;
        }

        boolean isAlarmPermitted = NotificationManagerCompat.from(this).areNotificationsEnabled();
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.appPushOn, isAlarmPermitted, FirebaseHelper.geoPermitted, isLocationPermitted);
    }

    private void setLogData(){
        LogData.eventLog(LogData.Main, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 9);
        LogData.customLog(LogData.SignUp, bundle, this);
    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            MyGeoPoint currentGeoPoint = new MyGeoPoint(location.getLatitude(), location.getLongitude());
            float distance = GeoUtil.getMeterDistanceFrom(currentGeoPoint, MyProfile.getUser().getGeoPoint());
            if(distance > 300) {
                GeoUtil.synchronizeLocation(location, MainActivity.this);
            } else {
                GeoUtil.isLocationSynchronized = true;
            }


        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    /**
     * 사용자의 위치를 수신
     */
    private void setMyLocation() {
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 500, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 500, gpsLocationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "위치 권한 거부됨", Toast.LENGTH_SHORT).show();
                }
            }
        }

        MainPermissionBusProvider.getInstance().post("");
    }


    private void checkCurrentUser(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            FirebaseHelper.init(auth, this);
            if(!MyProfile.getUser().getUid().equals(FirebaseHelper.mUid)){
                FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);
                                MyProfile.init(user);
                            }
                        }
                    }
                });
            }
        }else {
            Intent intent = new Intent(MainActivity.this, Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private void init(){
        //xml 바인딩
        mViewPager =  findViewById(R.id.main_viewpager);
        mViewPager.setPagingEnabled(false);
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
        mProfileLinearLayout = findViewById(R.id.home_main_toolbar_li_profile);
        mTitleTextView = findViewById(R.id.home_main_toolbar_tv_title);
        mTitleImageView = findViewById(R.id.home_main_toolbar_iv_title);
        mRingBellLinearLayout = findViewById(R.id.home_main_toolbar_linear_ringbell);
        mRingBellImageView = findViewById(R.id.home_main_toolbar_iv_ringbell);
        mHeartBellLinearLayout = findViewById(R.id.home_main_toolbar_iv_heartbell);
        mMainToolbar = findViewById(R.id.home_main_toolbar);

        //로딩중
        loaderLayout = findViewById(R.id.loaderLayout);
        loaderLayout.setClickable(true);



    }

    private void setGuide() {
        if (MyProfile.getUser().getStageGuide2().equals(LogData.guide2_s00) || MyProfile.getUser().getStageGuide2().equals(LogData.guide2_s01_meeting)) {
            Map<String, String> userProps = new HashMap<>();
            userProps.put(LogData.recentMainTime, DateUtil.getUnixTimeLong() + "");
            LogData.setUserProperties(userProps, MainActivity.this);
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                    .update(LogData.recentMainTime, DateUtil.getUnixTimeLong(),
                            LogData.stageGuide, LogData.guide_s01_start);
            LogData.eventLog(LogData.MainFirst, MainActivity.this);

            Intent intent = new Intent(MainActivity.this, Guide2Activity.class);
            startActivityForResult(intent, REQUEST_CODE_GUIDE);
            return;
        }

        if (MyProfile.getUser().getStageGuide2().equals(LogData.guide2_s00_no_meeting)) {
            setTutorial();
        }

        LogData.eventLog(LogData.MainNotice, MainActivity.this);

    }

    private void setTutorial(){
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(LogData.stageGuide2, LogData.guide2_s99_complete);
        Map<String,String> user_properties = new HashMap<>();
        user_properties.put(LogData.stageGuide2, LogData.guide2_s99_complete);
        LogData.setUserProperties(user_properties, MainActivity.this);
        MyProfile.getOurInstance().setStageGuide2(LogData.guide2_s99_complete);

        String title = "가입이 모두 완료되었어요!";
        String content = "";
        if(MyProfile.getUser().getGender().equals("여자")){
            title = "가입이 모두 완료되었어요:)";
            content = "그 전에 ";
        }

        if(MyProfile.getUser().getSelfIntroduction() == null || MyProfile.getUser().getSelfIntroduction().equals("")){
            Bundle bundle = new Bundle();
            bundle.putString(Strings.title, title);
            bundle.putString(Strings.content, content+ "자기소개를 완성하고 시작하는건 어떠세요?");
            bundle.putString(Strings.ok, "자기소개 쓰기");
            bundle.putString(Strings.no, "나중에 할게요");
            DialogNoOkFragment dialog = DialogNoOkFragment.getInstance();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), DialogNoOkFragment.TAG_EVENT_DIALOG);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder
                    .setMessage(content + "튜토리얼을 보고 시작하는건 어떠세요?")
                    .setCancelable(false)
                    .setPositiveButton("튜토리얼 보기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
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

        mViewPager.setCurrentItem(pageNumber, false);

        switch (pageNumber){
            case 0:
                mTitleImageView.setVisibility(View.VISIBLE);
                mTitleTextView.setVisibility(View.GONE);
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab0Button.setImageResource(R.drawable.ic_bar0_home_pink);
                mTab0TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                break;
            case 1:
                mTitleImageView.setVisibility(View.VISIBLE);
                mTitleTextView.setVisibility(View.GONE);
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab1Button.setImageResource(R.drawable.ic_bar1_people_pink);
                mTab1TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(mMeetingListener != null && oldPage != pageNumber){
                    mMeetingListener.refreshData();
                }
                break;
            case 2:
                mMainToolbar.setVisibility(View.GONE);
                mTab2Button.setImageResource(R.drawable.ic_bar2_star_pink);
                mTab2TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(mEvaluationListener != null && oldPage != pageNumber){
                    mEvaluationListener.refreshData();
                }
                break;
            case 3:
                mTitleImageView.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText("호감도 측정");
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab3Button.setImageResource(R.drawable.ic_experiment_pink);
                mTab3TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                if(mCommunityListener != null && oldPage != pageNumber){
                    mCommunityListener.refreshData();
                }
                break;
            case 4:
                mTitleImageView.setVisibility(View.GONE);
                mTitleTextView.setVisibility(View.VISIBLE);
                mTitleTextView.setText(R.string.toolbar_title_message);
                mMainToolbar.setVisibility(View.VISIBLE);
                mTab4Button.setImageResource(R.drawable.ic_bar4_chat_pink);
                mTab4TextView.setTextColor(getResources().getColor(R.color.colorMainPink));
                break;
        }

        oldPage = pageNumber;
    }

    private void setMovePageListener(){
        mTab0Button.setTag(0);
        mTab1Button.setTag(1);
        mTab2Button.setTag(2);
        mTab3Button.setTag(3);
        mTab4Button.setTag(4);
        
        //탭버튼 클릭시 툴바이름 변경하는데 사용
        movePageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = (int) v.getTag();
                updateUI(tag);


            }
        };

        //탭버튼클릭리스너 세팅
        mTab0Button.setOnClickListener(movePageListener);
        mTab1Button.setOnClickListener(movePageListener);
        mTab2Button.setOnClickListener(movePageListener);
        mTab3Button.setOnClickListener(movePageListener);
        mTab4Button.setOnClickListener(movePageListener);
    }



    private class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    mMainToolbar.setVisibility(View.VISIBLE);
                    return new Tab0HomeFragment();
                case 1:
                    mMainToolbar.setVisibility(View.VISIBLE);
                    return new Tab1MeetingFragment();
                case 2:
                    mMainToolbar.setVisibility(View.VISIBLE);
                    return new Tab2EvaluationFragment();
                case 3:
                    mMainToolbar.setVisibility(View.VISIBLE);
                    return new Tab3TierFragment();
                case 4:
                    mMainToolbar.setVisibility(View.VISIBLE);
                    return new Tab4ChattingFragment();
                default:
                    return null;

            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    //뒤로가기 2번 클릭시 종료
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
            finishAffinity();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setOnClickListener() {
        onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.home_main_toolbar_li_profile:
                        Intent profileIntent = new Intent(MainActivity.this, SetProfileMainActivity.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.home_main_toolbar_linear_ringbell:
                        Intent ringBellIntent = new Intent(MainActivity.this, NotificationAlarmActivity.class);
                        startActivity(ringBellIntent);
                        break;
                    case R.id.home_main_toolbar_iv_heartbell:
                        Intent heartBellIntent = new Intent(MainActivity.this, StoreMainActivity.class);
                        startActivity(heartBellIntent);
                        break;
                }
            }
        };

        mProfileLinearLayout.setOnClickListener(onClickListener);
        mRingBellLinearLayout.setOnClickListener(onClickListener);
        mHeartBellLinearLayout.setOnClickListener(onClickListener);
    }


    private void setNotificationListener(){
        setBellUI(false);
        long nowUnixTime = DateUtil.getUnixTimeLong();
        long standardUnixTime = nowUnixTime - 7 * 24 * 60 * 60 * 1000;

        listenerRegistration = FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Notification")
                .whereGreaterThan(FirebaseHelper.unixTime, standardUnixTime).orderBy(FirebaseHelper.unixTime, Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            Notification notification = dc.getDocument().toObject(Notification.class);
                            switch (dc.getType()) {
                                case ADDED:
                                case MODIFIED:
                                    setBellUI(notification.isCheck());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed city: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    private void setBellUI(boolean isNotificationChecked){
        if (!isNotificationChecked){
            mRingBellImageView.setImageResource(R.drawable.ic_bell_red);
        } else {
            mRingBellImageView.setImageResource(R.drawable.ic_bell_none);
        }

        float scale = getResources().getDisplayMetrics().density;
        mRingBellImageView.getLayoutParams().height = (int) (24 * scale);
        mRingBellImageView.requestLayout();
    }



    public interface ViewPagerClickListener {
        void refreshData();
    }

    public void setEvaluationDataListener(ViewPagerClickListener listener) {
        this.mEvaluationListener = listener;
    }

    public void setCommunityDataListener(ViewPagerClickListener listener) {
        this.mCommunityListener = listener;
    }

    public void setMeetingDataListener(ViewPagerClickListener listener){
        this.mMeetingListener = listener;
    }

    public void setTodayIntroDataListener(ViewPagerClickListener listener){
        this.mTodayIntroListener = listener;
    }




    @Override
    public void block() {
        blockEvaluationListener.blockEvaluation();
    }

    public void setBlockEvaluationListener(BlockEvaluationListener listener){
        this.blockEvaluationListener = listener;
    }

    public interface BlockEvaluationListener{
        void blockEvaluation();
    }



    @Override
    public void introduceCloseUser() {
        if(!GeoUtil.canGetLocation(MainActivity.this)){
            deviceLocationPermissionSetting();
        } else {
            checkPermissionAndIntroUser();
        }
    }


    private void checkPermissionAndIntroUser(){
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_APP_LOCATION);
            } else {
                permissionSetting(REQUEST_CODE_APP_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 500, gpsLocationListener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 500, gpsLocationListener);
            GeoUtil.updateLocationPermission(MainActivity.this);
            if(!GeoUtil.isLocationSynchronized){
                Toast.makeText(MainActivity.this, "위치 정보를 받는 중입니다. 잠시 후 시도해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }


            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                //그럴 일은 없지만 하트 기록이 없는 경우
                                if (task.getResult() == null) {
                                    loaderLayout.setVisibility(View.GONE);
                                    return;
                                }

                                //하트 세팅
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    dia = document.toObject(Dia.class);
                                }

                                String today = DateUtil.getDate();
                                String freeDay = DateUtil.getUnixToDate(dia.getRecentFreeCloseUserTime());
                                if(today.equals(freeDay)){
                                    Toast.makeText(MainActivity.this, "이미 오늘의 동네 친구를 소개해드렸어요.", Toast.LENGTH_SHORT).show();
                                    loaderLayout.setVisibility(View.GONE);
                                    return;
                                }

                                introCloseUser()
                                        .addOnCompleteListener(new OnCompleteListener<Boolean>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Boolean> task) {
                                                if (!task.isSuccessful()) {
                                                    Exception e = task.getException();
                                                    if (e instanceof FirebaseFunctionsException) {
                                                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                                        FirebaseFunctionsException.Code code = ffe.getCode();
                                                        Object details = ffe.getDetails();
                                                    }
                                                    Toast.makeText(MainActivity.this, "오류가 발생했어요:(", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    LogData.eventLog(LogData.close_user_s02_intro, MainActivity.this);
                                                    LogData.setStageCloseUser(LogData.close_user_s02_intro, MainActivity.this);
                                                    if(task.getResult() != null){
                                                        Boolean taskResult = task.getResult();
                                                        if(taskResult){
                                                            Toast.makeText(MainActivity.this, "동네 친구가 소개되었어요:).", Toast.LENGTH_SHORT).show();
                                                            if(mTodayIntroListener != null){
                                                                mTodayIntroListener.refreshData();
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "오늘은 소개해드릴 동네 친구가 없어요:(", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                                loaderLayout.setVisibility(View.GONE);
                                            }
                                        });


                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                loaderLayout.setVisibility(View.GONE);

                            }
                        }
                    });

        }
    }


    private Task<Boolean> introCloseUser() {
        mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);

        Map<String, String> data = new HashMap<>();
        data.put(Strings.partnerUid, "");

        return mFunctions
                .getHttpsCallable("introCloseUser")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task

                        Map<String,Boolean> result = (Map<String,Boolean>) task.getResult().getData();
                        boolean isSuccessful = false;
                        if(result.get("isSuccessful") != null){
                            isSuccessful = result.get("isSuccessful");
                        }
                        return isSuccessful;
                    }
                });
    }

    private void permissionSetting(int requestCode) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("위치 권한 요청");
        alertDialogBuilder
                .setMessage("동네 친구를 소개 받기 위해서는 위치 권한이 필요합니다. 설정에서 위치 권한을 켜주세요!")
                .setCancelable(false)
                .setPositiveButton("위치 권한 설정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, requestCode);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "위치 권한 거부됨", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deviceLocationPermissionSetting() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("위치 권한 요청");
        alertDialogBuilder
                .setMessage("동네 친구를 소개 받기 위해서는 위치 권한이 필요합니다. 설정에서 위치 권한을 켜주세요!")
                .setCancelable(false)
                .setPositiveButton("위치 권한 설정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE_DEVICE_LOCATION);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(MainActivity.this, "위치 권한 거부됨", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_DEVICE_LOCATION){
            checkPermissionAndIntroUser();
        }
        if(requestCode == REQUEST_CODE_APP_LOCATION){
            checkPermissionAndIntroUser();
        }

        if(requestCode == REQUEST_CODE_GUIDE){
            setTutorial();
        }

    }


    //자기소개 완성 추천 다이어로그
    @Override
    public void ok() {
        startActivity(new Intent(MainActivity.this, SetProfile_2Edit.class));
    }



}
