package com.unilab.uniting.fragments.tier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.community.NoticeActivity;
import com.unilab.uniting.activities.setprofile.SetProfile_1MyProfile;
import com.unilab.uniting.activities.setprofile.SetProfile_2Edit;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Notice;
import com.unilab.uniting.model.User;
import com.unilab.uniting.square.SquareViewPager;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Tab3TierFragment extends Fragment {

    private FirebaseFunctions mFunctions;
    private ViewGroup rootView;

    //상수 및 변수
    final static String TAG = "tierTAG";
    private LinearLayout mNotice;
    private TextView mNoticeTitleTextView;
    private TextView mNoticeDateTextView;
    private TextView mNextExpTextView;
    private ImageView mProfileImgView;
    private TextView mNicknameTextView;
    private TextView mTierTextView;
    private TextView mTierProgressTextView;
    private ProgressBar mTierProgressBar;
    private Button mGetTierBtn;
    private TextView mEditTextView;

    private RelativeLayout mLoaderLayout;

    private String gender = "male";
    private Map<String, Double> receiveScoreList = new HashMap<>();
    private Map<String, Double> tierTable = new HashMap<>();
    private ArrayList<Double> scoreList = new ArrayList<>();
    private ArrayList<Double> tierPercentList = new ArrayList<>();
    private double averageScore = 0;
    private double tierPercent = 0;
    private int scoreCount = 0;

    private int femaleTier = 0;
    private int maleTier = 0;
    private Dia dia;

    SquareViewPager.OnPageChangeListener mOnPageChangeListener;


    public Tab3TierFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //프래그먼트를 인플레이터로 뷰 객체화
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_tab3_tier, container, false);


        init();
        updateUI();
        setOnClickListener();
        setNotice();
        getTierTable();


        return rootView;

    }


    private void init() {
        mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);
        mNotice = rootView.findViewById(R.id.tab3_linear_notice);
        mNoticeTitleTextView = rootView.findViewById(R.id.tab3_tv_notice_title);
        mNoticeDateTextView = rootView.findViewById(R.id.tab3_tv_notice_date);
        mNextExpTextView = rootView.findViewById(R.id.tab3_tv_next_experiment);
        mProfileImgView = rootView.findViewById(R.id.tab3_iv_myProfile);
        mNicknameTextView = rootView.findViewById(R.id.tab3_tv_nickname);
        mTierTextView = rootView.findViewById(R.id.tab3_tv_tier);
        mTierProgressTextView = rootView.findViewById(R.id.tab3_tv_progress);
        mTierProgressBar = rootView.findViewById(R.id.tab3_pb_tier);
        mGetTierBtn = rootView.findViewById(R.id.tab3_btn_get_tier);
        mEditTextView = rootView.findViewById(R.id.tab3_tv_edit);

        mLoaderLayout = rootView.findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
        if (MyProfile.getUser().getGender().equals("여자")) {
            gender = "female";
        }

    }

    private void updateUI(){
        User user = MyProfile.getUser();
        RequestOptions circleCrop = new RequestOptions().circleCrop();
        if (!getActivity().isDestroyed()) {
            if(user.getPhotoUrl()!= null && user.getPhotoUrl().size() > 0 && user.getPhotoUrl().get(0) != null){
                Glide.with(getActivity()).load(user.getPhotoUrl().get(0)) .apply(circleCrop).into(mProfileImgView);
            }
        }
        mNicknameTextView.setText(user.getNickname() + "," + user.getAge() + "세");
        mNextExpTextView.setText("·  예정 기능: " + RemoteConfig.next_experiment);
    }

    private void setOnClickListener() {
        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NoticeActivity.class));
            }
        });

        mProfileImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetProfile_1MyProfile.class));
            }
        });

        mEditTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetProfile_2Edit.class));
            }
        });

        mGetTierBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getTier();
            }
        });

    }

    private void getTier(){
        LogData.eventLog(LogData.tier_s00, getActivity());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("호감도를 측정하시겠어요?");
        alertDialogBuilder
                .setMessage("회원님의 프로필 카드가 순간적으로 많은 친구들에게 노출되요. \n\n 하루 한 번 무료로 가능합니다.")
                .setCancelable(false)
                .setPositiveButton("호감도 측정하기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LogData.eventLog(LogData.tier_s01, getActivity());
                        mLoaderLayout.setVisibility(View.VISIBLE);
                        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            //그럴 일은 없지만 하트 기록이 없는 경우
                                            if (task.getResult() == null) {
                                                mLoaderLayout.setVisibility(View.GONE);
                                                return;
                                            }

                                            //하트 세팅
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                dia = document.toObject(Dia.class);
                                            }

                                            String today = DateUtil.getDate();
                                            String freeDay = DateUtil.getUnixToDate(dia.getRecentFreeTierTime());
                                            if(today.equals(freeDay)){
                                                Toast.makeText(getActivity(), "이미 오늘 호감도를 측정했어요.", Toast.LENGTH_SHORT).show();
                                                mLoaderLayout.setVisibility(View.GONE);
                                                return;
                                            }

                                            Map<String, Double> map = new HashMap<>();
                                            WriteBatch batch = FirebaseHelper.db.batch();
                                            batch.set(FirebaseHelper.db.collection("Tier").document(FirebaseHelper.mUid), MyProfile.getUser(), SetOptions.merge());
                                            batch.update(FirebaseHelper.db.collection("Tier").document(FirebaseHelper.mUid),
                                                    FirebaseHelper.scoreCount, 0,
                                                    FirebaseHelper.receiveScoreList, map);
                                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(), "호감도 측정이 시작되었습니다. 실험실은 테스트 중인 기능으로 경우에 따라 수 시간 이상 소요될 수 있습니다.", Toast.LENGTH_SHORT).show();
                                                    Dia newDia = dia.useFreeTier();
                                                    FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").document(newDia.getDiaId()).set(newDia);
                                                    LogData.eventLog(LogData.tier_s02,getActivity());
                                                    mLoaderLayout.setVisibility(View.GONE);

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "네트워크 오류", Toast.LENGTH_SHORT).show();
                                                    mLoaderLayout.setVisibility(View.GONE);
                                                }
                                            });


                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            mLoaderLayout.setVisibility(View.GONE);

                                        }
                                    }
                                });

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

    private void setNotice() {
        String docId = "";
        if(MyProfile.getUser().getGender().equals("여자")){
            docId = "female";
        } else {
            docId = "male";
        }

        FirebaseHelper.db.collection("Notice").document(docId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Notice notice = document.toObject(Notice.class);
                        mNoticeTitleTextView.setText(notice.getTitle());
                        try{
                            long unixTime = notice.getUnixTime();
                            mNoticeDateTextView.setText(DateUtil.dayForNotice(unixTime));
                        }catch (Exception err){

                        }

                    } else {
                    }
                } else {
                }
            }
        });
    }

    private void getTierTable() {
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Tier").document(gender)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        try {
                            tierTable = (Map<String, Double>) document.get(gender + "Tier");
                            for (String score : tierTable.keySet()) {
                                try {
                                    double rating = Double.parseDouble(score);
                                    scoreList.add(rating);
                                    tierPercentList.add(tierTable.get(score));
                                } catch (Exception err) {

                                }
                            }

                            Collections.sort(scoreList,new Ascending());
                            Collections.sort(tierPercentList,new Descending());
                        } catch (Exception err) {

                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
                mLoaderLayout.setVisibility(View.GONE);
                setTierListener();
            }
        });
    }




    class Descending implements Comparator<Double> {
        @Override
        public int compare(Double o1, Double o2) {
            return Double.compare(o2, o1);
        }

    }

    class Ascending implements Comparator<Double> {
        @Override
        public int compare(Double o1, Double o2) {
            return Double.compare(o1, o2);
        }

    }

    private void setTierListener() {
        FirebaseHelper.db.collection("Tier").document(FirebaseHelper.mUid)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        mTierProgressTextView.setVisibility(View.GONE);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.getData());
                        try{
                            scoreCount = (int)(long) snapshot.get(FirebaseHelper.scoreCount);
                            int receiveScoreCount = 0;
                            double sumOfScore = 0;
                            Map<String, Object> receiveScoreList = (Map<String,Object>) snapshot.get(FirebaseHelper.receiveScoreList);
                            for (String uid : receiveScoreList.keySet()) {
                                String score = receiveScoreList.get(uid) + "";
                                receiveScoreCount++;
                                if(score.equals("1")){
                                    sumOfScore ++;
                                }
                            }
                            if (receiveScoreCount >0){
                                averageScore = sumOfScore / receiveScoreCount;
                            }
                        }catch (Exception err){

                        }
                        setTierProgress();
                    } else {
                        Log.d(TAG, "Current data: null");
                        mTierProgressTextView.setVisibility(View.GONE);
                    }
                });

    }


    private void setTierProgress() {
        Log.d(TAG, "size: " + averageScore + scoreCount +  scoreList.size() + tierPercentList.size());
        if (scoreList.size() == 0 || tierPercentList.size() == 0){
            return;
        }

        int targetIndex = 0;

        for(int i = 0; i < scoreList.size(); i++){
            if(i<scoreList.size() -1){
                if(scoreList.get(i) <= averageScore && averageScore < scoreList.get(i+1)){
                    targetIndex = i;
                    break;
                }
            }
            if(i == scoreList.size() - 1){
                targetIndex = scoreList.size() - 1;
            }
        }

        if(targetIndex == scoreList.size() - 1){
            tierPercent = tierPercentList.get(targetIndex);
        }else{
            tierPercent = (tierPercentList.get(targetIndex) + tierPercentList.get(targetIndex +1))/2;
        }

        if(scoreCount >= RemoteConfig.tier_count){
            if(MyProfile.getUser().getTierRecentCount() <= RemoteConfig.tier_count){
                FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.tierRecentCount, scoreCount);
                MyProfile.getOurInstance().setTierRecentCount(scoreCount);
                MyProfile.getOurInstance().setTierRecent(tierPercent);
            }
            scoreCount = RemoteConfig.tier_count;
        } else {
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(FirebaseHelper.tierRecentCount, scoreCount);
            MyProfile.getOurInstance().setTierRecentCount(scoreCount);
            MyProfile.getOurInstance().setTierRecent(tierPercent);
        }

        int progress = (int) ((scoreCount * 100)/ RemoteConfig.tier_count);
        mTierProgressTextView.setVisibility(View.VISIBLE);
        mTierProgressTextView.setText("진행도  "+ progress + "%");

        if(MyProfile.getUser().getGender().equals("여자")){
            femaleTier = (int) (Math.pow((1-MyProfile.getUser().getTierRecent()), RemoteConfig.female_tier_revision_power) * 100);
            mTierTextView.setText(femaleTier + "점");
            mTierProgressBar.setProgress(progress);
        } else {
            maleTier = (int) (Math.pow((1-MyProfile.getUser().getTierRecent()), RemoteConfig.male_tier_revision_power) * 100);
            mTierTextView.setText(maleTier + "점");
            mTierProgressBar.setProgress(progress);
        }
    }
}