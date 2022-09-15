package com.unilab.uniting.fragments.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.otto.Subscribe;
import com.unilab.uniting.GeoUtil.GeoUtil;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.adapter.home.HomeTodayIntroAdapter;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeTodayIntroFragment extends Fragment implements MainActivity.ViewPagerClickListener {

    final static String TAG = "HOME_TODAY_TAG";

    ViewGroup rootView;

    private FrameLayout mPermissionLayout;
    private TextView mPermissionTitleTextView;
    private TextView mPermissionGuideTextView;
    private Button mAlarmButton;
    private Button mLocationButton;
    private ImageView mDeleteImgView;

    private boolean isAlarmPermitted = false;
    private boolean isLocationPermitted = false;

    private static final int REQUEST_CODE_APP_LOCATION = 222;
    private static final int REQUEST_CODE_DEVICE_LOCATION= 221;
    private static final int REQUEST_CODE_ALARM = 220;

    private RecyclerView mTodayRecyclerView;
    private ArrayList<QueryDocumentSnapshot> mTodayUserList;
    private HomeTodayIntroAdapter mTodayAdapter;

    private RecyclerView mPastRecyclerView;
    private ArrayList<QueryDocumentSnapshot> mPastUserList;
    private HomeTodayIntroAdapter mPastAdapter;

    public HomeTodayIntroFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainPermissionBusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MainPermissionBusProvider.getInstance().unregister(this);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home_today_intro, container, false);


        init();
        setOnClickListener();
        updatePermissionUI();
        setAdapter();
        getTodayIntro();
        setViewPagerClickListener();

        return rootView;
    }

    private void init(){
        mPermissionLayout = rootView.findViewById(R.id.home_layout_permission);
        mPermissionTitleTextView = rootView.findViewById(R.id.home_tv_permission_title);
        mPermissionGuideTextView = rootView.findViewById(R.id.home_tv_permission_guide);
        mAlarmButton = rootView.findViewById(R.id.home_btn_permission_alarm);
        mLocationButton = rootView.findViewById(R.id.home_btn_permission_location);
        mDeleteImgView = rootView.findViewById(R.id.home_iv_delete);
        mTodayRecyclerView = rootView.findViewById(R.id.home_todayintro_rv_todayintro);
        mPastRecyclerView = rootView.findViewById(R.id.home_todayintro_rv_pastintro);
    }

    private void setViewPagerClickListener(){
        MainActivity activity = (MainActivity) getActivity();
        activity.setTodayIntroDataListener(this);
    }

    private void getTodayIntro(){
        long nowUnixTime = DateUtil.getUnixTimeLong();
        long nowUnixTime15minFuture = DateUtil.getUnixTimeLong() + 15 * 60 * 1000;
        long yesterdayUnixTime = nowUnixTime - DateUtil.dayInMilliSecond;
        long weekUnixTime = nowUnixTime - 7 * DateUtil.dayInMilliSecond;

        mTodayUserList.clear();
        mTodayAdapter.clear();

        //해당 uid의 유저정보 가져오기
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntro")
                .whereGreaterThan(FirebaseHelper.introTime, yesterdayUnixTime)
                .whereLessThan(FirebaseHelper.introTime, nowUnixTime15minFuture)
                .orderBy(FirebaseHelper.introTime, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    mTodayUserList.add(document);
                                }
                                mTodayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        mPastUserList.clear();
        mPastAdapter.clear();

        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("TodayIntro")
                .whereLessThan(FirebaseHelper.introTime, yesterdayUnixTime)
                .whereGreaterThan(FirebaseHelper.introTime, weekUnixTime)
                .orderBy(FirebaseHelper.introTime, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    mPastUserList.add(document);
                                }
                                mPastAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setAdapter(){
        //오늘의 소개 리스트
        mTodayUserList = new ArrayList<>();
        RecyclerView.LayoutManager todayLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        mTodayRecyclerView.setLayoutManager(todayLayoutManager);
        mTodayAdapter = new HomeTodayIntroAdapter(getActivity(), mTodayUserList);
        mTodayRecyclerView.setAdapter(mTodayAdapter);


        //지나간 오늘의 소개 리스트
        mPastUserList = new ArrayList<>();
        RecyclerView.LayoutManager pastLayoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        mPastRecyclerView.setLayoutManager(pastLayoutManager);
        mPastAdapter = new HomeTodayIntroAdapter(getActivity(), mPastUserList);
        mPastRecyclerView.setAdapter(mPastAdapter);

    }

    private void setOnClickListener() {
        mAlarmButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", getActivity().getPackageName());
                    intent.putExtra("app_uid", getActivity().getApplicationInfo().uid);
                } else {
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                }
                startActivityForResult(intent, REQUEST_CODE_ALARM);
            }
        });


        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!GeoUtil.canGetLocation(getActivity())){
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_CODE_DEVICE_LOCATION);
                } else {
                    requestLocationPermission();
                }
            }
        });

        mDeleteImgView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mPermissionLayout.setVisibility(View.GONE);
                saveDeleteShared();

            }
        });
    }

    private void saveDeleteShared(){
        SharedPreferences pref = getActivity().getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String today = DateUtil.getDate();
        editor.putBoolean(Strings.permission_delete + today, true);
        editor.apply();
    }



    private void requestLocationPermission(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_APP_LOCATION);
            } else {
                permissionSetting(REQUEST_CODE_APP_LOCATION);
            }
        } else {
            updatePermissionUI();
        }
    }


    private void permissionSetting(int requestCode) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("위치 권한 요청");
        alertDialogBuilder
                .setMessage("동네 친구를 소개 받기 위해서는 위치 권한이 필요합니다. 설정에서 위치 권한을 켜주세요!")
                .setCancelable(false)
                .setPositiveButton("위치 권한 설정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, requestCode);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "위치 권한 거부됨", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updatePermissionUI() {
        SharedPreferences pref = getActivity().getSharedPreferences(FirebaseHelper.mUid, MODE_PRIVATE);
        boolean isDeleted = pref.getBoolean(Strings.permission_delete + DateUtil.getDate(), false);

        if (isDeleted){
            mPermissionLayout.setVisibility(View.GONE);
            return;
        }


        if (!GeoUtil.canGetLocation(getActivity()) || (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            isLocationPermitted = false;
        } else {
            isLocationPermitted = true;
        }
        isAlarmPermitted = NotificationManagerCompat.from(getActivity()).areNotificationsEnabled();

        if (isAlarmPermitted && isLocationPermitted) {
            mPermissionLayout.setVisibility(View.GONE);
        } else {
            mPermissionLayout.setVisibility(View.VISIBLE);
        }

        if (!isAlarmPermitted && !isLocationPermitted) {
            mPermissionTitleTextView.setText("알림 및 위치 기능을 활성화해 주세요!");
            mPermissionGuideTextView.setText("동네 친구 신청을 놓치지 마세요!");
            mAlarmButton.setVisibility(View.VISIBLE);
            mLocationButton.setVisibility(View.VISIBLE);
        } else if (!isAlarmPermitted && isLocationPermitted) {
            mPermissionTitleTextView.setText("알림을 활성화해주세요.");
            mPermissionTitleTextView.setText("알림을 켜고 친구 신청을 놓치지 마세요!");
            mAlarmButton.setVisibility(View.VISIBLE);
            mLocationButton.setVisibility(View.GONE);
        } else if (isAlarmPermitted && !isLocationPermitted) {
            mPermissionTitleTextView.setText("위치 기능을 활성화해주세요.");
            mPermissionGuideTextView.setText("동네 친구를 소개 받을 수 있어요!");
            mAlarmButton.setVisibility(View.GONE);
            mLocationButton.setVisibility(View.VISIBLE);
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_DEVICE_LOCATION){
            requestLocationPermission();
        }

        updatePermissionUI();

    }

    @Subscribe
    public void getPost(String event) {
        updatePermissionUI();
    }


    @Override
    public void refreshData() {
        getTodayIntro();
    }
}
