package com.unilab.uniting.activities.launch.signup;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unilab.uniting.BuildConfig;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.launch.LaunchCertificationAdapter;
import com.unilab.uniting.fragments.dialog.DialogEditPhoto3Fragment;
import com.unilab.uniting.fragments.dialog.DialogOk2Fragment;
import com.unilab.uniting.fragments.dialog.DialogOkNoFragment;
import com.unilab.uniting.fragments.launch.DialogFailOkFragment;
import com.unilab.uniting.fragments.launch.DialogLaterFragment;
import com.unilab.uniting.model.Certification;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.RemoteConfig;
import com.unilab.uniting.utils.Strings;
import com.unilab.uniting.utils.University;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LaunchSignup_16UniversityCertification extends BasicActivity implements DialogEditPhoto3Fragment.DialogEdit3Listener, DialogOkNoFragment.DialogSignUpBackListener {
    final static String TAG = "16UniversityTAG";

    //xml
    private EditText mUniversityEditText;
    private EditText mMajorEditText;
    private RecyclerView mUniversityRecyclerView;
    private LinearLayout mPhotoMenuLinearLayout;
    private ImageView mPhotoMenuArrowImageView;
    private LinearLayout mPhotoLinearLayout;
    private TextView mGuideTextView;
    private ImageView mSetImageVIew; //이미지가 올라갈 뷰
    private ImageView mUploadImageVIew; //이미지올려주세요라는 그림이있는 뷰(?)
    private Button mPhotoDoneBtn;

    private LinearLayout mEmailMenuLinearLayout;
    private ImageView mEmailMenuArrowImageView;
    private LinearLayout mEmailLinearLayout;
    private TextView mEmailGuideTextView;
    private EditText mEmailUniversityEditText;
    private RecyclerView mEmailUniversityRecyclerView;
    private EditText mEmailEditText;
    private EditText mCodeEditText;
    private Button mSendCodeBtn;
    private Button mEmailDoneBtn;

    private TextView mLaterTextView;
    private TextView mEmailTextView;

    private RelativeLayout loaderLayout;
    private LinearLayout mBack;


    View.OnClickListener onClickListener;
    OnSingleClickListener onSingleClickListener;
    private LaunchCertificationAdapter mUniversityAdapter;
    private LaunchCertificationAdapter mEmailUniversityAdapter;

    //value
    private long mLastClickTime = 0;
    final static int CAMERA_PERMISSION = 0;
    final static int PICK_IMAGE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int RECYCLER_VIEW_HEIGHT = 200;
    static final int PUSH_REQUEST_CODE = 333;


    final static String TAKE_PHOTO = "TAKE_PHOTO";
    final static String GALLERY = "GALLERY";
    final static String REMOVE_PHOTO = "REMOVE_PHOTO";


    private boolean isCertificationForced = false;
    private boolean isLoading = false;
    private Uri mFinalUri = null;
    private String mUniversityPhotoUrl = null;
    private String mOfficialUniversity = "";
    private String mOfficialMajor = "";
    private String mEmailOfficialUniversity = "";
    private String currentPhotoPath;
    private ArrayList<String> universityList = new ArrayList<>();
    private ArrayList<String> filteredUnivList =   new ArrayList<>();
    private ArrayList<String> selectionList =  new ArrayList<>();
    private Map<String,String> universityDict = new HashMap<String, String>();
    private String certiComment = "인증 정보가 일치하지 않습니다.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_signup_16_university_certification);

        setLogData();
        init();
        updateUI();
        setOnClickListener();
        setPushNoti();
        setFailComment();
        setUniversityAdapter(universityList);
        setEmailUniversityAdapter(universityList);
        universityEditTextChangeListener();
        emailUniversityEditTextChangeListener();

    }

    private void setPushNoti(){
        boolean isAlarmPermitted = NotificationManagerCompat.from(this).areNotificationsEnabled();
        if(!isAlarmPermitted){
            Log.d(TAG, "알림 꺼짐");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("가입 심사가 시작되었습니다.");
            alertDialogBuilder
                    .setMessage("심사 완료 후 알려드리니 알림을 켜놔주세요!")
                    .setCancelable(false)
                    .setPositiveButton("알림 켜기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(Settings.EXTRA_APP_PACKAGE, LaunchSignup_16UniversityCertification.this.getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", LaunchSignup_16UniversityCertification.this.getPackageName());
                                intent.putExtra("app_uid", LaunchSignup_16UniversityCertification.this.getApplicationInfo().uid);
                            } else {
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + LaunchSignup_16UniversityCertification.this.getPackageName()));
                            }
                            startActivityForResult(intent, PUSH_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alertDialogBuilder.show();

        }
    }

    private void setLogData(){
        LogData.eventLog(LogData.SignUp6_Univ, this);
        Bundle bundle = new Bundle();
        bundle.putInt(LogData.sign_up_progress, 6);
        LogData.customLog(LogData.SignUp, bundle, this);
    }

    private void init(){
        //바인딩
        mBack = findViewById(R.id.toolbar_back);


        mPhotoMenuLinearLayout= findViewById(R.id.signup_university16_linear_menu_photo);
        mPhotoLinearLayout= findViewById(R.id.signup_university16_linear_container_photo);
        mPhotoMenuArrowImageView = findViewById(R.id.signup_university16_imgView_arrow1);
        mUniversityEditText = findViewById(R.id.signup_university16_et_university);
        mMajorEditText = findViewById(R.id.signup_university16_et_major);
        mUniversityRecyclerView = findViewById(R.id.signup_university16_rv_university);
        mSetImageVIew = findViewById(R.id.signup_university16_iv_setimage);
        mUploadImageVIew = findViewById(R.id.signup_university16_iv_uploadimage);
        mPhotoDoneBtn = findViewById(R.id.signup_university16_btn_ok);
        mGuideTextView = findViewById(R.id.signup_university16_tv_guide);

        mEmailMenuLinearLayout = findViewById(R.id.signup_university16_linear_menu_email);
        mEmailMenuArrowImageView = findViewById(R.id.signup_university16_imgView_arrow2);
        mEmailLinearLayout = findViewById(R.id.signup_university16_linear_container_email);
        mEmailGuideTextView = findViewById(R.id.signup_university16_tv_guide_email);
        mEmailUniversityEditText = findViewById(R.id.signup_university16_et_university2);
        mEmailUniversityRecyclerView = findViewById(R.id.signup_university16_rv_university2);
        mEmailEditText = findViewById(R.id.signup_university16_et_sendCode);
        mCodeEditText = findViewById(R.id.signup_university16_et_done);
        mSendCodeBtn = findViewById(R.id.signup_university16_btn_sendCode);
        mEmailDoneBtn = findViewById(R.id.signup_university16_btn_done_email);

        mLaterTextView = findViewById(R.id.signup_university16_tv_later);
        mEmailTextView = findViewById(R.id.signup_university16_tv_inquiry);

        //로딩중
        loaderLayout = findViewById(R.id.loaderLayout);
        loaderLayout.setClickable(true);

        University.init();
        universityList = University.universityOfficialList;
        filteredUnivList =  University.universityOfficialList;
        selectionList = University.universityOfficialList;
        universityDict = University.universityDict;

        if((RemoteConfig.getSharedManager().getAbTest() != null && (( RemoteConfig.getSharedManager().getAbTest().get(RemoteConfig.ABCertification.variant_name)) != null))){
            if(RemoteConfig.getSharedManager().getAbTest().get(RemoteConfig.ABCertification.variant_name).equals(RemoteConfig.ABCertification.variant_force)){
                isCertificationForced = true;
            }
        }
    }

    private void updateUI(){
        mUniversityRecyclerView.setVisibility(View.GONE);
        mPhotoMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_down));
        mEmailLinearLayout.setVisibility(View.GONE);
        mEmailUniversityRecyclerView.setVisibility(View.GONE);
        mGuideTextView.setText(R.string.certification_guide_email);
        mEmailGuideTextView.setText(R.string.certification_guide_email);

        if(MyProfile.getUser().getMembership().equals(LaunchUtil.Screening)){
            mLaterTextView.setVisibility(View.VISIBLE);
            mBack.setVisibility(View.GONE);
        } else {
            mLaterTextView.setVisibility(View.GONE);
            mBack.setVisibility(View.VISIBLE);
        }

        if(MyProfile.getUser().getGender().equals("여자")){
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "가입 심사가 시작됐어요. 심사를 기다리시는 동안 대학을 인증해보는건 어떠세요?", Toast.LENGTH_SHORT).show();
        }

    }


    private void setFailComment(){
        if (MyProfile.getUser().getMembership().equals(LaunchUtil.Fail) && isCertificationForced) {
            FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").orderBy(FirebaseHelper.timeStamp, Query.Direction.DESCENDING).limit(1)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    try {
                                        certiComment = (String) document.get(FirebaseHelper.adminComment);
                                    } catch (Exception ex){
                                        certiComment = "사진을 가이드라인에 맞게 2장 이상 올려주세요.";
                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                            Bundle bundle = new Bundle();
                            bundle.putString(Strings.EXTRA_MESSAGE, certiComment);
                            DialogFailOkFragment dialog2 = DialogFailOkFragment.getInstance();
                            dialog2.setArguments(bundle);
                            dialog2.show(getSupportFragmentManager(), DialogFailOkFragment.TAG_MEETING_DIALOG2);
                        }
                    });
        }
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.toolbar_back:  // back(<) 클릭시 뒤로가기 효과
                        onBackPressed();
                        break;
                    case R.id.signup_university16_linear_menu_photo:
                        if (mPhotoLinearLayout.getVisibility() == View.VISIBLE) {
                            mPhotoLinearLayout.setVisibility(View.GONE);
                            mPhotoMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
                        } else {
                            mPhotoLinearLayout.setVisibility(View.VISIBLE);
                            mPhotoMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_down));
                            mEmailLinearLayout.setVisibility(View.GONE);
                            mEmailMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
                        }
                        break;
                    case R.id.signup_university16_linear_menu_email:
                        if (mEmailLinearLayout.getVisibility() == View.VISIBLE) {
                            mEmailLinearLayout.setVisibility(View.GONE);
                            mEmailMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
                        } else {
                            mEmailLinearLayout.setVisibility(View.VISIBLE);
                            mEmailMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_down));
                            mPhotoLinearLayout.setVisibility(View.GONE);
                            mPhotoMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
                        }
                        break;
                    case R.id.signup_university16_iv_setimage:
                        photoDialogRadio();
                        break;
                    case R.id.signup_university16_tv_later:
                        DialogLaterFragment dialog = DialogLaterFragment.getInstance();
                        dialog.show(getSupportFragmentManager(), DialogLaterFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.signup_university16_tv_inquiry:
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        String[] helpEmail = {getString(R.string.help_mail)};
                        emailIntent.setType("plain/Text");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, helpEmail);
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[" + getString(R.string.app_name) + "]  고객센터에 문의합니다.");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "답장 받을 이메일: " + MyProfile.getUser().getEmail() + "\n앱 버전 (AppVersion):" + BuildConfig.VERSION_NAME + "\n기기명 (Device):" + Build.MODEL + "\n안드로이드 OS (Android OS):" + Build.VERSION.RELEASE + "\n안드로이드 SDK(API):" + Build.VERSION.SDK_INT +"\n내용 (Content):\n");
                        emailIntent.setType("message/rfc822");
                        startActivity(emailIntent);
                        break;
                }
            }
        };


        onSingleClickListener = new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (v.getId()){
                    case R.id.signup_university16_btn_ok:
                        if (mFinalUri == null) {
                            Toast.makeText(LaunchSignup_16UniversityCertification.this, "사진을 올려주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        //증명사진저장
                        saveUnivCertificationToStorage();
                        break;
                    case R.id.signup_university16_btn_sendCode:
                        sendCode();
                        break;
                    case R.id.signup_university16_btn_done_email:
                        emailCertificationDone();
                        break;
                }
            }
        };

        mBack.setOnClickListener(onClickListener);
        mPhotoMenuLinearLayout.setOnClickListener(onClickListener);
        mEmailMenuLinearLayout.setOnClickListener(onClickListener);
        mSetImageVIew.setOnClickListener(onClickListener);
        mLaterTextView.setOnClickListener(onClickListener);
        mEmailTextView.setOnClickListener(onClickListener);

        mPhotoDoneBtn.setOnClickListener(onSingleClickListener);
        mSendCodeBtn.setOnClickListener(onSingleClickListener);
        mEmailDoneBtn.setOnClickListener(onSingleClickListener);

    }

    private void sendCode(){
        //2초 내 재클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){ return; }
        mLastClickTime = SystemClock.elapsedRealtime();

        String email = mEmailEditText.getText().toString().trim();

        if (mEmailOfficialUniversity.equals("")){
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "재학중인 대학을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = DateUtil.getDateSec();
        String timeStamp = DateUtil.getTimeStampUnix();
        String university = "";
        if (University.universityDict.get(mEmailOfficialUniversity) != null ){
            university = University.universityDict.get(mEmailOfficialUniversity);
        } else {
            university = mEmailOfficialUniversity;
        }
        Certification certification = new Certification(FirebaseHelper.mUid,"","", Strings.SCREENING, "", date, timeStamp, university, "", mEmailOfficialUniversity, "", false, false, "4", email,FirebaseHelper.signUp);

        loaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collectionGroup("UniversityEmail").whereEqualTo(FirebaseHelper.universityEmail, email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            loaderLayout.setVisibility(View.GONE);
                            Toast.makeText(LaunchSignup_16UniversityCertification.this, "동일한 이메일로 가입한 이력이 있습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            WriteBatch batch = FirebaseHelper.db.batch();
                            batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("EmailCertification").document(timeStamp), certification);
                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loaderLayout.setVisibility(View.GONE);
                                    Toast.makeText(LaunchSignup_16UniversityCertification.this, email + "로 인증코드가 발송되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loaderLayout.setVisibility(View.GONE);
                                    Log.d(TAG, e.toString());
                                    Toast.makeText(LaunchSignup_16UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loaderLayout.setVisibility(View.GONE);
                Log.d(TAG, e.toString());
                Toast.makeText(LaunchSignup_16UniversityCertification.this, "오류 발생2. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void emailCertificationDone(){
        String code = mCodeEditText.getText().toString().trim();

        if (mEmailOfficialUniversity.equals("")){
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "재학중인 대학을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.isEmpty()) {
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> codeList = new ArrayList<>();
        Map<String, String> codeEmailList = new HashMap<>();

        loaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("EmailCertification").orderBy(FirebaseHelper.timeStamp, Query.Direction.DESCENDING).limit(2)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get(FirebaseHelper.code) != null && document.get(FirebaseHelper.universityEmail) != null ){
                                    String certiCode = (String) document.get(FirebaseHelper.code);
                                    String email = (String) document.get(FirebaseHelper.universityEmail);
                                    codeList.add(certiCode);
                                    codeEmailList.put(certiCode, email);
                                }
                            }

                            if (codeList.contains(code)){
                                String timeStamp = DateUtil.getTimeStampUnix();
                                String certifiedEmail = codeEmailList.get(code);
                                String date = DateUtil.getDateSec();
                                String university;
                                if (University.universityDict.get(mEmailOfficialUniversity) != null ){
                                    university = University.universityDict.get(mEmailOfficialUniversity);
                                } else {
                                    university = mEmailOfficialUniversity;
                                }
                                Map<String, Object> emailData = new HashMap<>();
                                emailData.put(FirebaseHelper.universityEmail, certifiedEmail);

                                Map<String,Object> userData = new HashMap<>();
                                Map<String,Object> adminUserData = new HashMap<>();
                                userData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Screening);
                                userData.put(FirebaseHelper.certificationType, "4");
                                adminUserData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Screening);

                                if(!MyProfile.getUser().getMembership().equals(LaunchUtil.Screening) && !MyProfile.getUser().getMembership().equals(LaunchUtil.Scoring)){
                                    userData.put(FirebaseHelper.membership, LaunchUtil.Screening);
                                    adminUserData.put(FirebaseHelper.membership, LaunchUtil.Screening);
                                }

                                Certification certification = new Certification(FirebaseHelper.mUid,"","", Strings.SCREENING, "", date, timeStamp, university, "", mEmailOfficialUniversity, "", false,false, "4", certifiedEmail,FirebaseHelper.signUp);
                                WriteBatch batch = FirebaseHelper.db.batch();
                                batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid),userData);
                                batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid),adminUserData);
                                if(!MyProfile.getUser().getDi().equals("")){
                                    batch.update(FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()), FirebaseHelper.membership, LaunchUtil.Screening);
                                }
                                batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").document(timeStamp), certification);
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        loaderLayout.setVisibility(View.GONE);
                                        startActivity(new Intent(LaunchSignup_16UniversityCertification.this, LaunchSignup_17Screening.class));
                                        MyProfile.getOurInstance().setSignUpProgress(Strings.SignUpPrgoress.Screening);
                                        MyProfile.getOurInstance().setMembership(LaunchUtil.Screening);

                                        Map<String,String> user_properties = new HashMap<>();
                                        user_properties.put(LogData.membership, LaunchUtil.Screening);
                                        user_properties.put(LogData.sign_up_progress, Strings.SignUpPrgoress.Screening);
                                        LogData.setUserProperties(user_properties, LaunchSignup_16UniversityCertification.this);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loaderLayout.setVisibility(View.GONE);
                                        Toast.makeText(LaunchSignup_16UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                loaderLayout.setVisibility(View.GONE);
                                Toast.makeText(LaunchSignup_16UniversityCertification.this, "인증 코드를 잘못 입력하여습니다.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            loaderLayout.setVisibility(View.GONE);
                            Toast.makeText(LaunchSignup_16UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUniversityAdapter(ArrayList<String> universityList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mUniversityRecyclerView.setLayoutManager(layoutManager);
        mUniversityAdapter = new LaunchCertificationAdapter(this, universityList, universityClickListener);
        mUniversityRecyclerView.setAdapter(mUniversityAdapter);
        mUniversityAdapter.notifyDataSetChanged();

        if (universityList.size() > 8)  {
            float scale = getResources().getDisplayMetrics().density;
            mUniversityRecyclerView.getLayoutParams().height = (int) (RECYCLER_VIEW_HEIGHT * scale);
        } else {
            mUniversityRecyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private void universityEditTextChangeListener(){
        mUniversityEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUniversityEditText.setFocusableInTouchMode(true);
                mUniversityEditText.setFocusable(true);
                mUniversityEditText.requestFocus();

                mUniversityEditText.setText("");
                mOfficialUniversity = "";
                mUniversityRecyclerView.setVisibility(View.GONE);
            }
        });

        mUniversityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                String university = s.toString();
                filteredUnivList = filter(universityList, university);
                if (filteredUnivList.size() > 0 || university.length() == 0) {
                    selectionList = filteredUnivList;
                    setUniversityAdapter(selectionList);
                }

                Log.d(TAG, "test123");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                Log.d(TAG, "test124");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
                if (s.toString().isEmpty()){
                    mUniversityRecyclerView.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "test125");

            }

        });

    }

    private LaunchCertificationAdapter.UniversityClickListener universityClickListener = new LaunchCertificationAdapter.UniversityClickListener(){
        @Override
        public void click(String univ) {
            mUniversityRecyclerView.setVisibility(View.GONE);
            mUniversityEditText.setText(univ);
            mUniversityEditText.setFocusableInTouchMode(false);
            mUniversityEditText.setFocusable(false);
            mUniversityEditText.clearFocus();
            mOfficialUniversity = univ;
        }
    };


    private void setEmailUniversityAdapter(ArrayList<String> universityList){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mEmailUniversityRecyclerView.setLayoutManager(layoutManager);
        mEmailUniversityAdapter = new LaunchCertificationAdapter(this, universityList, emailUniversityClickListener);
        mEmailUniversityRecyclerView.setAdapter(mEmailUniversityAdapter);
        mEmailUniversityAdapter.notifyDataSetChanged();

        if (universityList.size() > 8)  {
            float scale = getResources().getDisplayMetrics().density;
            mUniversityRecyclerView.getLayoutParams().height = (int) (RECYCLER_VIEW_HEIGHT * scale);
        } else {
            mUniversityRecyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
    }

    private void emailUniversityEditTextChangeListener(){
        mEmailUniversityEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailUniversityEditText.setFocusableInTouchMode(true);
                mEmailUniversityEditText.setFocusable(true);
                mEmailUniversityEditText.requestFocus();

                mEmailUniversityEditText.setText("");
                mEmailOfficialUniversity = "";
                mEmailUniversityRecyclerView.setVisibility(View.GONE);
            }
        });

        mEmailUniversityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때
                String university = s.toString();
                filteredUnivList = filter(universityList, university);
                if (filteredUnivList.size() > 0 || university.length() == 0) {
                    selectionList = filteredUnivList;
                    setEmailUniversityAdapter(selectionList);
                }
                Log.d(TAG, "t123");
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                Log.d(TAG, "t124");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
                if (s.toString().isEmpty()){
                    mEmailUniversityRecyclerView.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "t125");

            }

        });

    }

    private LaunchCertificationAdapter.UniversityClickListener emailUniversityClickListener = new LaunchCertificationAdapter.UniversityClickListener(){
        @Override
        public void click(String univ) {
            mEmailUniversityRecyclerView.setVisibility(View.GONE);
            mEmailUniversityEditText.setText(univ);
            mEmailUniversityEditText.setFocusableInTouchMode(false);
            mEmailUniversityEditText.setFocusable(false);
            mEmailUniversityEditText.clearFocus();
            mEmailOfficialUniversity = univ;
        }
    };

    public ArrayList<String>  filter(ArrayList<String> universityList,String searchValue){
        ArrayList<String>  newUniversityList = new ArrayList<>();
        for(String university :universityList){
            if(university.contains(searchValue)){
                newUniversityList.add(university);
            }
        }
        return newUniversityList;

    }


    //사진찍기 or 앨범에서 가져오기 선택 다이얼로그
    private void photoDialogRadio() {
        DialogEditPhoto3Fragment dialog = DialogEditPhoto3Fragment.getInstance();
        dialog.show(getSupportFragmentManager(), DialogEditPhoto3Fragment.TAG_EVENT_DIALOG);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.unilab.uniting.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = DateUtil.getTimeStampUnix();
        String imageFileName = "Certification_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void selectMenu(String menu) {
        switch (menu){
            case TAKE_PHOTO:
                if (ActivityCompat.checkSelfPermission(LaunchSignup_16UniversityCertification.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(LaunchSignup_16UniversityCertification.this, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(LaunchSignup_16UniversityCertification.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
                    } else {
                        cameraPermissionSetting(CAMERA_PERMISSION);
                    }
                } else {
                    dispatchTakePictureIntent();
                }
                break;
            case GALLERY:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);
                break;
            case REMOVE_PHOTO:
                mSetImageVIew.setImageDrawable(null);
                mFinalUri = null;
                break;
        }
    }

    private void cameraPermissionSetting(int requestCode){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("카메라 요청");
        alertDialogBuilder
                .setMessage("카메라를 사용하기 위해선 권한 허용이 필요합니다.")
                .setCancelable(false)
                .setPositiveButton("권한 설정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, requestCode);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(LaunchSignup_16UniversityCertification.this, "권한 거부됨", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    //갤러리 사진가져온거 결과(비트맵으로) 저장
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                if (contentUri != null) {
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    openCropActivity(contentUri, destinationUri);
                } else {
                    Toast.makeText(this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == PICK_IMAGE) {
                Uri sourceUri = data.getData();
                if (sourceUri != null) {
                    Log.d(TAG, "test127" + sourceUri);
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
                    openCropActivity(sourceUri, destinationUri);
                } else {
                    Toast.makeText(this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                Uri resultUri = UCrop.getOutput(data);
                if (resultUri != null) {
                    //초기화
                    mFinalUri = null;
                    mSetImageVIew.setImageDrawable(null);
                    //이미지뷰에 세팅
                    mFinalUri = resultUri;
                    mSetImageVIew.setImageURI(resultUri);
                } else {
                    Toast.makeText(this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "이미지를 받지 못했습니다.", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == PUSH_REQUEST_CODE){
            Toast.makeText(this, "심사를 기다리는 동안 학교를 인증해보시는건 어떠세요?", Toast.LENGTH_SHORT).show();
        }
    }


    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .start(this);
    }


    private void saveUnivCertificationToStorage() {
        if (mOfficialUniversity.equals("")){
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "대학을 골라주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mFinalUri == null) {
            Toast.makeText(LaunchSignup_16UniversityCertification.this, "사진을 올려주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        mOfficialMajor = mMajorEditText.getText().toString().trim();

        if (!isLoading) {
            loaderLayout.setVisibility(View.VISIBLE);
            isLoading = true;

            String date = DateUtil.getDateSec();
            String timeStamp = DateUtil.getTimeStampUnix();
            String fileName = "users/" + FirebaseHelper.mUid + "/certification/" + timeStamp;
            final StorageReference universityCertifiStorageRef = FirebaseHelper.storageRef.child(fileName);
            UploadTask uploadTask = universityCertifiStorageRef.putFile(mFinalUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(LaunchSignup_16UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                    loaderLayout.setVisibility(View.GONE);
                    isLoading = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                loaderLayout.setVisibility(View.GONE);
                                isLoading = false;
                                throw task.getException();
                            }
                            return universityCertifiStorageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "대학교증명 사진 스토리지에 업로드 성공, saveProfileToDB()호출");
                                mUniversityPhotoUrl = String.valueOf(task.getResult());
                                saveProfileToDB(mUniversityPhotoUrl, fileName, date, timeStamp);
                            } else {
                                Toast.makeText(LaunchSignup_16UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                loaderLayout.setVisibility(View.GONE);
                                isLoading = false;
                            }
                        }
                    });
                }
            });
        }
    }

    private void saveProfileToDB(String photoUrl, String fileName, String date, String timeStamp) {
        String university;
        if (University.universityDict.containsKey(mOfficialUniversity)){
            university = University.universityDict.get(mOfficialUniversity);
        } else {
            university = mOfficialUniversity;
        }

        Map<String,Object> userData = new HashMap<>();
        Map<String,Object> adminUserData = new HashMap<>();
        userData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Screening);
        userData.put(FirebaseHelper.certificationType, "1");
        adminUserData.put(FirebaseHelper.signUpProgress, Strings.SignUpPrgoress.Screening);

        if(!MyProfile.getUser().getMembership().equals(LaunchUtil.Screening) && !MyProfile.getUser().getMembership().equals(LaunchUtil.Scoring)){
            userData.put(FirebaseHelper.membership, LaunchUtil.Screening);
            adminUserData.put(FirebaseHelper.membership, LaunchUtil.Screening);
        }

        Certification certification = new Certification(FirebaseHelper.mUid,photoUrl, fileName, Strings.SCREENING, "", date, timeStamp,university, mOfficialMajor,  mOfficialUniversity, mOfficialMajor, false, false, "1", "",FirebaseHelper.signUp);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), userData);
        batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid),adminUserData);
        batch.update(FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()), FirebaseHelper.membership, LaunchUtil.Screening);
        batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").document(timeStamp), certification);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                MyProfile.getOurInstance().setSignUpProgress(Strings.SignUpPrgoress.Screening);
                MyProfile.getOurInstance().setMembership(LaunchUtil.Screening);

                Map<String,String> user_properties = new HashMap<>();
                user_properties.put(LogData.membership, LaunchUtil.Screening);
                user_properties.put(LogData.sign_up_progress, Strings.SignUpPrgoress.Screening);
                LogData.setUserProperties(user_properties, LaunchSignup_16UniversityCertification.this);


                Intent intent = new Intent(LaunchSignup_16UniversityCertification.this, LaunchSignup_17Screening.class);
                Toast.makeText(LaunchSignup_16UniversityCertification.this, "사진이 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
                loaderLayout.setVisibility(View.GONE);
                isLoading = false;
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LaunchSignup_16UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                isLoading = false;
                loaderLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                if (grantResults[0] == 0) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(LaunchSignup_16UniversityCertification.this, "카메라 권한이 거절됨", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

    @Override
    public void onBackPressed() {
        if(MyProfile.getUser().getMembership().equals(LaunchUtil.Screening)){
            Bundle bundle = new Bundle();
            bundle.putString(Strings.EXTRA_CONTENT, "이미 심사가 시작되어 프로필 수정이 불가합니다. 심사를 기다리시는 동안 학교를 인증해주세요!");
            DialogOk2Fragment dialog = DialogOk2Fragment.getInstance();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(Strings.title, "이전 단계로 돌아가시겠어요?");
            bundle.putString(Strings.content, "마지막 단계에요!");
            bundle.putString(Strings.ok, "이전 단계로");
            bundle.putString(Strings.no, "가입 계속하기");
            DialogOkNoFragment dialog = DialogOkNoFragment.getInstance();
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), DialogOkNoFragment.TAG_EVENT_DIALOG);
        }

    }

    @Override
    public void back() {
        super.onBackPressed();
    }
}
