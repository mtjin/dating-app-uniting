package com.unilab.uniting.activities.setprofile;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unilab.uniting.BuildConfig;
import com.unilab.uniting.R;
import com.unilab.uniting.adapter.launch.LaunchCertificationAdapter;
import com.unilab.uniting.fragments.dialog.DialogEditPhoto3Fragment;
import com.unilab.uniting.fragments.dialog.DialogOk2Fragment;
import com.unilab.uniting.fragments.setprofile.DialogPublicInfoFragment;
import com.unilab.uniting.model.Certification;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;
import com.unilab.uniting.utils.University;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetProfile_4UniversityCertification extends BasicActivity implements DialogEditPhoto3Fragment.DialogEdit3Listener, DialogPublicInfoFragment.OkListener {

    final static String TAG = "SetProfile_4UniversityT";
    final static String EXTRA_CONTENT = "EXTRA_CONTENT";
    private FirebaseFunctions mFunctions;

    //xml
    private LinearLayout mPublicMenuLayout;
    private LinearLayout mPublicLayout;
    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton;
    private RadioButton mUniversityRadioButton;
    private RadioButton mMajorRadioButton;
    private TextView mPublicNicknameTextView;
    private TextView mPublicHeightTextView;
    private TextView mPublicUniversityTitleTextView;
    private TextView mPublicMajorTitleTextView;
    private ImageView mPublicUniversityBadgeImgView;
    private ImageView mPublicMajorBadgeImgView;
    private Button mPublicDoneBtn;

    private TextView mPhotoMenuTextView;
    private EditText mUniversityEditText;
    private EditText mMajorEditText;
    private RecyclerView mUniversityRecyclerView;
    private LinearLayout mPhotoMenuLinearLayout;
    private ImageView mPhotoMenuArrowImageView;
    private LinearLayout mPhotoLinearLayout;
    private TextView mGuideTitleTextView;
    private TextView mGuideTextView;
    private ImageView mSetImageVIew; //이미지가 올라갈 뷰
    private ImageView mUploadImageVIew; //이미지올려주세요라는 그림이있는 뷰(?)
    private Button mPhotoDoneBtn;
    private TextView mScreeningBlackTextView;

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

    private LinearLayout mMajorMenuLayout;
    private ImageView mMajorMenuArrowImageView;
    private LinearLayout mMajorLayout;
    private EditText mMajorChangeEditText;
    private Button mMajorDoneBtn;
    private TextView mMajorGuideTextView;

    private TextView mEmailTextView;

    private RelativeLayout mLoaderLayout;
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
    final static String SCREENING = Strings.SCREENING; //심사중
    final static String PASS = Strings.PASS; //사진 심사 합격
    final static String FAIL = Strings.FAIL; //불합

    final static String TAKE_PHOTO = "TAKE_PHOTO";
    final static String GALLERY = "GALLERY";
    final static String REMOVE_PHOTO = "REMOVE_PHOTO";


    private boolean isLoading = false;
    private Uri mFinalUri = null;
    private int count = 0;
    private String mUniversityPhotoUrl = null;
    private String mOfficialUniversity = "";
    private String mOfficialMajor = "";
    private String mEmailOfficialUniversity = "";
    private String currentPhotoPath;
    private ArrayList<String> universityList = new ArrayList<>();
    private ArrayList<String> filteredUnivList =   new ArrayList<>();
    private ArrayList<String> selectionList =  new ArrayList<>();
    private Map<String,String> universityDict = new HashMap<String, String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_4_certification);


        init();
        updateUI();
        setOnClickListener();
        setUniversityAdapter(universityList);
        setEmailUniversityAdapter(universityList);
        universityEditTextChangeListener();
        emailUniversityEditTextChangeListener();
        setRadioGroup();

    }

    private void init(){
        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);

        //바인딩
        mBack = findViewById(R.id.toolbar_back);

        mPublicMenuLayout = findViewById(R.id.signup_university16_linear_menu_public);
        mPublicLayout = findViewById(R.id.signup_university16_linear_container_public);
        mRadioGroup = findViewById(R.id.setprofile_certification_radio_group);
        mUniversityRadioButton = findViewById(R.id.setprofile_certification_rbtn_university);
        mMajorRadioButton = findViewById(R.id.setprofile_certification_rbtn_major);
        mPublicNicknameTextView = findViewById(R.id.setprofile_certification_tv_nickname);
        mPublicHeightTextView = findViewById(R.id.setprofile_certification_tv_height);
        mPublicDoneBtn = findViewById(R.id.signup_university16_btn_done_public);
        mPublicUniversityTitleTextView = findViewById(R.id.setprofile_certification_tv_public_university);
        mPublicMajorTitleTextView = findViewById(R.id.setprofile_certification_tv_public_major);
        mPublicUniversityBadgeImgView = findViewById(R.id.setprofile_certification_iv_public_university);
        mPublicMajorBadgeImgView = findViewById(R.id.setprofile_certification_iv_public_major);

        mPhotoMenuTextView = findViewById(R.id.signup_university16_tv_menu_university);
        mPhotoMenuLinearLayout= findViewById(R.id.signup_university16_linear_menu_photo);
        mPhotoLinearLayout= findViewById(R.id.signup_university16_linear_container_photo);
        mPhotoMenuArrowImageView = findViewById(R.id.signup_university16_imgView_arrow1);
        mUniversityEditText = findViewById(R.id.signup_university16_et_university);
        mMajorEditText = findViewById(R.id.signup_university16_et_major);
        mUniversityRecyclerView = findViewById(R.id.signup_university16_rv_university);
        mScreeningBlackTextView = findViewById(R.id.signup_university16_tv_screening);
        mSetImageVIew = findViewById(R.id.signup_university16_iv_setimage);
        mUploadImageVIew = findViewById(R.id.signup_university16_iv_uploadimage);
        mPhotoDoneBtn = findViewById(R.id.signup_university16_btn_ok);
        mGuideTitleTextView = findViewById(R.id.signup_university16_tv_title);
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

        mMajorMenuLayout = findViewById(R.id.signup_university16_linear_menu_major);
        mMajorMenuArrowImageView = findViewById(R.id.signup_university16_imgView_arrow3);
        mMajorLayout = findViewById(R.id.signup_university16_linear_container_major);
        mMajorChangeEditText = findViewById(R.id.signup_university16_et_major2);
        mMajorDoneBtn = findViewById(R.id.signup_university16_btn_done_major);
        mMajorGuideTextView = findViewById(R.id.signup_university16_tv_major2_guide);

        mEmailTextView = findViewById(R.id.signup_university16_tv_inquiry);

        University.init();
        universityList = University.universityOfficialList;
        filteredUnivList =  University.universityOfficialList;
        selectionList = University.universityOfficialList;
        universityDict = University.universityDict;
    }

    private void updateUI(){
        if (MyProfile.getUser().isOfficialUniversityChecked()) {
            mPhotoLinearLayout.setVisibility(View.GONE);
            mPhotoMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
            mEmailMenuLinearLayout.setVisibility(View.GONE);

            mPhotoMenuTextView.setText("사진(학생증, 재학증명서)로 인증하기");
            mUniversityRadioButton.setText(MyProfile.getUser().getUniversity());
            mGuideTitleTextView.setText("대학 및 전공 인증하기");
            mGuideTextView.setText(R.string.certification_guide_photo);
        } else {
            mPublicMenuLayout.setVisibility(View.GONE);
            mPublicLayout.setVisibility(View.GONE);
            mPhotoLinearLayout.setVisibility(View.VISIBLE);
            mPhotoMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_down));
            mEmailMenuLinearLayout.setVisibility(View.VISIBLE);

            mPhotoMenuTextView.setText("사진(학생증, 포털, 재학증명서)로 인증하기");
            mGuideTitleTextView.setText("대학은 왜 인증하나요?");
            mGuideTextView.setText(R.string.certification_guide_email);
            mUniversityRadioButton.setText("대학 미인증");
        }

        mUniversityRecyclerView.setVisibility(View.GONE);
        mEmailUniversityRecyclerView.setVisibility(View.GONE);
        mEmailLinearLayout.setVisibility(View.GONE);
        mMajorMenuLayout.setVisibility(View.GONE);
        mMajorLayout.setVisibility(View.GONE);

        if (MyProfile.getUser().isOfficialUniversityChecked() && MyProfile.getUser().isOfficialMajorChecked()){
            mMajorMenuLayout.setVisibility(View.VISIBLE);
        }
        mPublicNicknameTextView.setText(MyProfile.getUser().getNickname() + ", " + MyProfile.getUser().getAge() + "세");
        mPublicHeightTextView.setText(MyProfile.getUser().getHeight());


        if(MyProfile.getUser().isOfficialMajorChecked()) {
            mMajorRadioButton.setText(MyProfile.getUser().getMajor());
        } else {
            mMajorRadioButton.setText("전공 미인증");
        }

        if(MyProfile.getUser().isOfficialMajorPublic()){
            mMajorRadioButton.setChecked(true);
            mUniversityRadioButton.setTextColor(getResources().getColor(R.color.colorBlackInvisible));
            mPublicUniversityTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130Invisible));
            mMajorRadioButton.setTextColor(getResources().getColor(R.color.colorBlack));
            mPublicMajorTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130));
            mPublicUniversityBadgeImgView.setVisibility(View.GONE);
            mPublicMajorBadgeImgView.setVisibility(View.VISIBLE);
        } else {
            mUniversityRadioButton.setChecked(true);
            mUniversityRadioButton.setTextColor(getResources().getColor(R.color.colorBlack));
            mPublicUniversityTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130));
            mMajorRadioButton.setTextColor(getResources().getColor(R.color.colorBlackInvisible));
            mPublicMajorTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130Invisible));
            mPublicUniversityBadgeImgView.setVisibility(View.VISIBLE);
            mPublicMajorBadgeImgView.setVisibility(View.GONE);
        }
    }

    private void setRadioGroup(){
        mUniversityRadioButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                pickPublicInfo(true);

            }
        });

        mMajorRadioButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (!MyProfile.getUser().isOfficialMajorChecked()){
                    mUniversityRadioButton.setChecked(true);
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_CONTENT, "전공 미인증 상태입니다. 인증 후 공개할 수 있습니다.");
                    DialogOk2Fragment dialog = DialogOk2Fragment.getInstance();
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), DialogOk2Fragment.TAG_MEETING_DIALOG2);
                    return;
                }

                pickPublicInfo(false);
            }
        });

    }

    private void pickPublicInfo(boolean isUniversity){
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .update(FirebaseHelper.officialUniversityPublic, isUniversity,
                        FirebaseHelper.officialMajorPublic, !isUniversity)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        if (isUniversity){
                            mUniversityRadioButton.setTextColor(getResources().getColor(R.color.colorBlack));
                            mPublicUniversityTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130));
                            mMajorRadioButton.setTextColor(getResources().getColor(R.color.colorBlackInvisible));
                            mPublicMajorTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130Invisible));
                            mPublicUniversityBadgeImgView.setVisibility(View.VISIBLE);
                            mPublicMajorBadgeImgView.setVisibility(View.GONE);
                        } else {
                            mUniversityRadioButton.setTextColor(getResources().getColor(R.color.colorBlackInvisible));
                            mPublicUniversityTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130Invisible));
                            mMajorRadioButton.setTextColor(getResources().getColor(R.color.colorBlack));
                            mPublicMajorTitleTextView.setTextColor(getResources().getColor(R.color.colorGray130));
                            mPublicUniversityBadgeImgView.setVisibility(View.GONE);
                            mPublicMajorBadgeImgView.setVisibility(View.VISIBLE);
                        }

                        if(MyProfile.getUser().isOfficialInfoPublic()){
                            if (isUniversity){
                                Toast.makeText(SetProfile_4UniversityCertification.this, "대학 공개로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SetProfile_4UniversityCertification.this, "전공 공개로 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        if(isUniversity){
                            mMajorRadioButton.setChecked(true);
                        }else {
                            mUniversityRadioButton.setChecked(true);
                        }
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        screeningResultListener();
    }

    private void screeningResultListener(){
        FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").orderBy("date", Query.Direction.DESCENDING).limit(1)
                .addSnapshotListener(SetProfile_4UniversityCertification.this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        if(value != null && !value.isEmpty()){
                            for (QueryDocumentSnapshot doc : value) {
                                Certification certification = doc.toObject(Certification.class);
                                if (certification.getResult() != null && certification.getPhotoUrl()!= null) {
                                    String result = certification.getResult();
                                    String photoUrl = certification.getPhotoUrl();
                                    switch (result){
                                        case SCREENING:
                                            if (!SetProfile_4UniversityCertification.this.isDestroyed()) {
                                                Glide.with(SetProfile_4UniversityCertification.this).load(photoUrl).fitCenter().thumbnail(0.1f).into(mSetImageVIew);
                                                mScreeningBlackTextView.setVisibility(View.VISIBLE);
                                            }
                                            mUniversityEditText.setText(certification.getOfficialUniversity() +  "(심사중)");
                                            mEmailUniversityEditText.setText(certification.getOfficialUniversity() +  "(심사중)");
                                            mMajorEditText.setText(certification.getOfficialMajor());
                                            mUniversityRecyclerView.setVisibility(View.GONE);
                                            mEmailUniversityRecyclerView.setVisibility(View.GONE);
                                            break;
                                        case PASS:
                                        case FAIL:
                                            mScreeningBlackTextView.setVisibility(View.GONE);
                                            break;
                                    }
                                }
                            }
                        }else{
                            Log.d(TAG, "인증 자료가 없습니다. ");
                        }
                    }
                });
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
                    case R.id.signup_university16_linear_menu_major:
                        if (mMajorLayout.getVisibility() == View.VISIBLE) {
                            mMajorLayout.setVisibility(View.GONE);
                            mMajorMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_right));
                        } else {
                            mMajorLayout.setVisibility(View.VISIBLE);
                            mMajorMenuArrowImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_chevron_down));
                        }
                        break;
                    case R.id.signup_university16_iv_setimage:
                        if(mScreeningBlackTextView.getVisibility() == View.VISIBLE){
                            Toast.makeText(SetProfile_4UniversityCertification.this, "심사 중입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            photoDialogRadio();
                        }

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
                switch (v.getId()) {
                    case R.id.signup_university16_btn_done_public:
                        setPublicDialog();
                        break;
                    case R.id.signup_university16_btn_ok:
                        //증명사진저장
                        saveUnivCertificationToStorage();
                        break;
                    case R.id.signup_university16_btn_sendCode:
                        sendCode();
                        break;
                    case R.id.signup_university16_btn_done_email:
                        emailCertificationDone();
                        break;
                    case R.id.signup_university16_btn_done_major:
                        changeMajor();
                        break;

                }
            }
        };

        mBack.setOnClickListener(onClickListener);
        mPhotoMenuLinearLayout.setOnClickListener(onClickListener);
        mEmailMenuLinearLayout.setOnClickListener(onClickListener);
        mMajorMenuLayout.setOnClickListener(onClickListener);
        mSetImageVIew.setOnClickListener(onClickListener);
        mEmailTextView.setOnClickListener(onClickListener);


        mPhotoDoneBtn.setOnClickListener(onSingleClickListener);
        mSendCodeBtn.setOnClickListener(onSingleClickListener);
        mEmailDoneBtn.setOnClickListener(onSingleClickListener);
        mMajorDoneBtn.setOnClickListener(onSingleClickListener);
        mPublicDoneBtn.setOnClickListener(onSingleClickListener);
    }

    private void setPublicDialog(){
        DialogPublicInfoFragment dialog = DialogPublicInfoFragment.getInstance();
        dialog.show(getSupportFragmentManager(), DialogPublicInfoFragment.TAG_EVENT_DIALOG);
    }

    @Override
    public void setPublic(boolean isDone) {
        if (MyProfile.getUser().isOfficialInfoPublic()){
            Toast.makeText(SetProfile_4UniversityCertification.this, "이미 공개되어있습니다.", Toast.LENGTH_SHORT).show();
            return;

        }
        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .update(FirebaseHelper.officialInfoPublic, true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        MyProfile.getOurInstance().setOfficialInfoPublic(true);
                        Toast.makeText(SetProfile_4UniversityCertification.this, "공개 전환되었습니다.", Toast.LENGTH_SHORT).show();
                        certificationDia();

                        mLoaderLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(SetProfile_4UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    private Task<Boolean> certificationDia() {
        mFunctions = FirebaseFunctions.getInstance(Strings.region_asia);

        Map<String, String> data = new HashMap<>();
        data.put(FirebaseHelper.location, FirebaseHelper.publicInfo);

        return mFunctions
                .getHttpsCallable("certificationDia")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return true;
                    }
                });
    }



    private void changeMajor(){
        String major = mMajorChangeEditText.getText().toString().trim();
        if(!MyProfile.getUser().isOfficialUniversityChecked() || !MyProfile.getUser().isOfficialMajorChecked()){
            Toast.makeText(SetProfile_4UniversityCertification.this, "인증하기 탭에서 대학과 전공을 인증한 회원님만 전공 변경이 가능합니디", Toast.LENGTH_SHORT).show();
            return;
        }


        if(major.isEmpty()){
            Toast.makeText(SetProfile_4UniversityCertification.this, "전공을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = DateUtil.getDateSec();
        String timeStamp = DateUtil.getTimeStampUnix();


        Certification certification = new Certification(FirebaseHelper.mUid, "", "", SCREENING, "", date, timeStamp, MyProfile.getUser().getUniversity(), major ,  MyProfile.getUser().getOfficialUniversity(), MyProfile.getUser().getOfficialMajor(), true, true, "", "", FirebaseHelper.active);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set( FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").document(timeStamp), certification);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SetProfile_4UniversityCertification.this, "완료되었습니다. 심사를 기다려주세요.", Toast.LENGTH_SHORT).show();
                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetProfile_4UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
            }
        });
    }

    private void sendCode(){
        //2초 내 재클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000){ return; }
        mLastClickTime = SystemClock.elapsedRealtime();

        String email = mEmailEditText.getText().toString().trim();

        if (mEmailOfficialUniversity.equals("")){
            Toast.makeText(SetProfile_4UniversityCertification.this, "재학중인 대학을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(SetProfile_4UniversityCertification.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
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


        Certification certification = new Certification(FirebaseHelper.mUid,"","", SCREENING, "", date, timeStamp, university, "", mEmailOfficialUniversity, "", MyProfile.getUser().isOfficialUniversityChecked(), MyProfile.getUser().isOfficialMajorChecked(), "4", email, FirebaseHelper.active);

        mLoaderLayout.setVisibility(View.VISIBLE);
        FirebaseHelper.db.collectionGroup("UniversityEmail").whereEqualTo(FirebaseHelper.universityEmail, email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            mLoaderLayout.setVisibility(View.GONE);
                            Toast.makeText(SetProfile_4UniversityCertification.this, "동일한 이메일로 가입한 이력이 있습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            WriteBatch batch = FirebaseHelper.db.batch();
                            batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("EmailCertification").document(timeStamp), certification);
                            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mLoaderLayout.setVisibility(View.GONE);
                                    Toast.makeText(SetProfile_4UniversityCertification.this, email + "로 인증코드가 발송되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    mLoaderLayout.setVisibility(View.GONE);
                                    Log.d(TAG, e.toString());
                                    Toast.makeText(SetProfile_4UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                                    mLastClickTime = DateUtil.getUnixTimeLong();

                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Log.d(TAG, e.toString());
                Toast.makeText(SetProfile_4UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void emailCertificationDone(){
        String code = mCodeEditText.getText().toString().trim();

        if(mScreeningBlackTextView.getVisibility() == View.VISIBLE){
            Toast.makeText(SetProfile_4UniversityCertification.this, "심사중", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEmailOfficialUniversity.equals("")){
            Toast.makeText(SetProfile_4UniversityCertification.this, "재학중인 대학을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.isEmpty()) {
            Toast.makeText(SetProfile_4UniversityCertification.this, "코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> codeList = new ArrayList<>();
        Map<String, String> codeEmailList = new HashMap<>();

        mLoaderLayout.setVisibility(View.VISIBLE);
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

                                Certification certification = new Certification(FirebaseHelper.mUid,"","", SCREENING, "", date, timeStamp, university, "", mEmailOfficialUniversity, "", MyProfile.getUser().isOfficialUniversityChecked(), MyProfile.getUser().isOfficialMajorChecked(), "4", certifiedEmail,FirebaseHelper.active);

                                WriteBatch batch = FirebaseHelper.db.batch();
                                batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").document(timeStamp), certification);
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mLoaderLayout.setVisibility(View.GONE);
                                        Toast.makeText(SetProfile_4UniversityCertification.this, "심사가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mLoaderLayout.setVisibility(View.GONE);
                                        Toast.makeText(SetProfile_4UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else {
                                mLoaderLayout.setVisibility(View.GONE);
                                Toast.makeText(SetProfile_4UniversityCertification.this, "인증 코드를 잘못 입력하여습니다.", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            mLoaderLayout.setVisibility(View.GONE);
                            Toast.makeText(SetProfile_4UniversityCertification.this, "오류 발생. 지속시 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
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

        //mUniversityEditText.removeTextChangedListener();

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
            mEmailUniversityRecyclerView.getLayoutParams().height = (int) (RECYCLER_VIEW_HEIGHT * scale);
        } else {
            mEmailUniversityRecyclerView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
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
                if (ActivityCompat.checkSelfPermission(SetProfile_4UniversityCertification.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SetProfile_4UniversityCertification.this, Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(SetProfile_4UniversityCertification.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
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
                        Toast.makeText(SetProfile_4UniversityCertification.this, "권한 거부됨", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //갤러리 사진가져온거 결과 저장
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
    }


    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .start(this);
    }


    private void saveUnivCertificationToStorage() {
        if(mScreeningBlackTextView.getVisibility() == View.VISIBLE){
            Toast.makeText(SetProfile_4UniversityCertification.this, "심사중", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mOfficialUniversity.equals("")){
            Toast.makeText(SetProfile_4UniversityCertification.this, "대학을 골라주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mFinalUri == null) {
            Toast.makeText(SetProfile_4UniversityCertification.this, "사진을 올려주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        mOfficialMajor = mMajorEditText.getText().toString().trim();

        if(!isLoading) {
            mLoaderLayout.setVisibility(View.VISIBLE);
            isLoading = true;

            String date = DateUtil.getDateSec();
            String timeStamp = DateUtil.getTimeStampUnix();
            String fileName = "users/" + FirebaseHelper.mUid + "/certification/" + timeStamp;
            final StorageReference universityCertifiStorageRef = FirebaseHelper.storageRef.child(fileName);
            UploadTask uploadTask = universityCertifiStorageRef.putFile(mFinalUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(SetProfile_4UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                isLoading = false;
                                mLoaderLayout.setVisibility(View.GONE);
                                throw task.getException();
                            }
                            return universityCertifiStorageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "대학교증명 사진 스토리지에 업로드 성공, saveProfileToDB()호출");
                                String universityPhotoUrl = String.valueOf(task.getResult());
                                saveProfileToDB(universityPhotoUrl, fileName, date, timeStamp);
                            }else{
                                Toast.makeText(SetProfile_4UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                isLoading = false;
                                mLoaderLayout.setVisibility(View.GONE);
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

        Certification certification = new Certification(FirebaseHelper.mUid,photoUrl, fileName, SCREENING, "", date, timeStamp,university, mOfficialMajor,  mOfficialUniversity, mOfficialMajor, MyProfile.getUser().isOfficialUniversityChecked(), MyProfile.getUser().isOfficialMajorChecked(), "1", "",FirebaseHelper.active);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set( FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("Certification").document(timeStamp), certification);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(SetProfile_4UniversityCertification.this, "완료되었습니다. 심사를 기다려주세요.", Toast.LENGTH_SHORT).show();
                        mScreeningBlackTextView.setVisibility(View.VISIBLE);
                        isLoading = false;
                        mLoaderLayout.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetProfile_4UniversityCertification.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                isLoading = false;
                mLoaderLayout.setVisibility(View.GONE);
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
                    Toast.makeText(SetProfile_4UniversityCertification.this, "카메라 권한이 거절됨", Toast.LENGTH_SHORT).show();
                }

            }

        }
    }

}
