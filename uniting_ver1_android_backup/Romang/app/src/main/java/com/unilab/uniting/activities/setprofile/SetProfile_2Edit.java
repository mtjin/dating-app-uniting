package com.unilab.uniting.activities.setprofile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.unilab.uniting.GeoUtil.GeoUtil;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_15Photo_Guide;
import com.unilab.uniting.fragments.dialog.DialogEditListener;
import com.unilab.uniting.fragments.setprofile.DialogEdit2Fragment;
import com.unilab.uniting.fragments.setprofile.DialogEditFragment;
import com.unilab.uniting.fragments.setprofile.DialogEditPersonalityFragment;
import com.unilab.uniting.fragments.setprofile.DialogEditPhotoFragment;
import com.unilab.uniting.model.User;
import com.unilab.uniting.model.UserForEdit;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.BitmapConverter;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetProfile_2Edit extends BasicActivity implements DialogEditListener {

    //requestCode
    final static String TAG = "SetProfile_2EditT";
    public final static int PICK_IMAGE = 1;
    final static String SCREENING = Strings.SCREENING; //?????????
    final static String PASS = Strings.PASS; //?????? ?????? ??????
    final static String FAIL = Strings.FAIL; //??????

    private static final int REQUEST_CODE_APP_LOCATION = 222;
    private static final int REQUEST_CODE_DEVICE_LOCATION = 221;
    private static final int REQUEST_CODE_ALARM = 220;

    //value
    private ArrayList<String> mScreeningResultList = new ArrayList<>(); // ???????????? ?????? 0, ??????1, ??????2
    private ArrayList<String> mScreeningPhotoUrlList = new ArrayList<>(); //??? ?????????????????? ?????????(???????????? ?????????) ?????? ?????????
    private ArrayList<String> mFinalPhotoUrlList = new ArrayList<>(); //???????????? ????????? ?????? Url ?????????
    private ArrayList<String> mScreeningFileNameList = new ArrayList<>(); //???????????? ?????? ????????? ?????? ??????
    private int uriPhotoCount = 0;
    private boolean isLoading = false;
    private int selectNum = 0; //????????? ?????? 0,1,2,3,4,5
    private boolean isInitLoading = false;

    ListenerRegistration registration;

    //xml
    private com.unilab.uniting.square.SquareImageView mPhoto0ImageView, mPhoto1ImageView, mPhoto2ImageView, mPhoto3ImageView, mPhoto4ImageView, mPhoto5ImageView;
    private TextView mPhoto0ScreeningTextView, mPhoto1ScreeningTextView, mPhoto2ScreeningTextView, mPhoto3ScreeningTextView, mPhoto4ScreeningTextView, mPhoto5ScreeningTextView;
    private ArrayList<TextView> mPhotoScreeningTextViewList;
    private ArrayList<ImageView> mPhotoImageViewList;
    private EditText mSelfIntroductionEditText;
    private LinearLayout mUniversityLayout;
    private LinearLayout mMajorLayout;
    private TextView mNicknameTextView;
    private TextView mHeightTextView;
    private TextView mUniversityMenuTextView;
    private TextView mMajorMenuTextView;
    private TextView mUniversityTextView;
    private TextView mMajorTextView;
    private TextView mLocationTextView;
    private ImageView mLocationUpdateImageView;
    private TextView mPersonalityTextView;
    private TextView mBloodTextView;
    private TextView mReligionTextView;
    private TextView mDrinkingTextView;
    private TextView mSmokingTextView;
    private TextView mProfileGuideTextView;
    private EditText mStory0EditText;
    private EditText mStory1EditText;
    private EditText mStory2EditText;
    private RelativeLayout mLoaderLayout; //??????????????????
    private LinearLayout mBack;
    private Button mSubmit;
    private TextView mToolbarTextView;

    View.OnClickListener onClickListener;
    private LocationManager locationManager;

    //profile value
    private String nickName; //?????????
    private String university; //??????
    private String major; //??????
    private String location; //??????
    private String height; //???
    private ArrayList<String> personalityList = new ArrayList<>();
    private String mPersonalityToString = "";
    private String bloodType; //?????????
    private String smoking; //??????
    private String drinking; //??????
    private String religion; //??????
    private String photoCount; //?????? ??????
    private List<String> photoUrl; //???????????? ?????? url
    private String selfIntroduction; //?????? ?????????
    private String story0; //????????? ?????????
    private String story1; //????????? ?????????
    private String story2; //????????? ?????????


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_2_edit);

        init();
        updateUI();
        //??????????????? ??????
        setOnClickListener();
        setMaxLine();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenScreeningResult();
    }

    private void init() {
        mUniversityLayout = findViewById(R.id.setprofile_2edit_linear_university);
        mMajorLayout = findViewById(R.id.setprofile_2edit_linear_major);
        mUniversityMenuTextView = findViewById(R.id.setprofile_2edit_tv_menu_university);
        mMajorMenuTextView = findViewById(R.id.setprofile_2edit_tv_menu_major);
        mSelfIntroductionEditText = findViewById(R.id.signup_profileedit18_et_introduce);
        mNicknameTextView = findViewById(R.id.signup_profileedit18_tv_nickname);
        mHeightTextView = findViewById(R.id.signup_profileedit18_tv_height);
        mUniversityTextView = findViewById(R.id.signup_profileedit_tv_university);
        mMajorTextView = findViewById(R.id.signup_profileedit18_tv_major);
        mLocationTextView = findViewById(R.id.signup_profileedit18_tv_location);
        mLocationUpdateImageView = findViewById(R.id.signup_profileedit18_iv_location_update);
        mPersonalityTextView = findViewById(R.id.signup_profileedit18_tv_personality);
        mBloodTextView = findViewById(R.id.signup_profileedit18_tv_blood);
        mReligionTextView = findViewById(R.id.signup_profileedit18_tv_religion);
        mDrinkingTextView = findViewById(R.id.signup_profileedit18_tv_drinking);
        mSmokingTextView = findViewById(R.id.signup_profileedit18_tv_smoking);
        mStory0EditText = findViewById(R.id.signup_profileedit18_et_story0);
        mStory1EditText = findViewById(R.id.signup_profileedit18_et_story1);
        mStory2EditText = findViewById(R.id.signup_profileedit18_et_story2);
        mProfileGuideTextView = findViewById(R.id.signup_profileedit18_tv_guide);
        View view = findViewById(R.id.toolbar_write_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mSubmit = view.findViewById(R.id.toolbar_submit_btn);

        mPhoto0ImageView = findViewById(R.id.signup_profileedit18_iv_photo0);
        mPhoto1ImageView = findViewById(R.id.signup_profileedit18_iv_photo1);
        mPhoto2ImageView = findViewById(R.id.signup_profileedit18_iv_photo2);
        mPhoto3ImageView = findViewById(R.id.signup_profileedit18_iv_photo3);
        mPhoto4ImageView = findViewById(R.id.signup_profileedit18_iv_photo4);
        mPhoto5ImageView = findViewById(R.id.signup_profileedit18_iv_photo5);
        mPhoto0ScreeningTextView = findViewById(R.id.signup_profileedit18_iv_photo0_text);
        mPhoto1ScreeningTextView = findViewById(R.id.signup_profileedit18_iv_photo1_text);
        mPhoto2ScreeningTextView = findViewById(R.id.signup_profileedit18_iv_photo2_text);
        mPhoto3ScreeningTextView = findViewById(R.id.signup_profileedit18_iv_photo3_text);
        mPhoto4ScreeningTextView = findViewById(R.id.signup_profileedit18_iv_photo4_text);
        mPhoto5ScreeningTextView = findViewById(R.id.signup_profileedit18_iv_photo5_text);

        mPhotoScreeningTextViewList = new ArrayList<>(Arrays.asList(mPhoto0ScreeningTextView, mPhoto1ScreeningTextView, mPhoto2ScreeningTextView, mPhoto3ScreeningTextView, mPhoto4ScreeningTextView, mPhoto5ScreeningTextView));
        mPhotoImageViewList = new ArrayList<>(Arrays.asList(mPhoto0ImageView, mPhoto1ImageView, mPhoto2ImageView, mPhoto3ImageView, mPhoto4ImageView, mPhoto5ImageView));

        //??????, ?????? ????????????
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
        mToolbarTextView = findViewById(R.id.toolbar_title);
        mToolbarTextView.setText("????????? ??????");


    }


    private void updateUI() {
        //??? ????????????
        User userProfile = MyProfile.getUser();
        nickName = userProfile.getNickname();
        height = userProfile.getHeight();
        university = userProfile.getUniversity();
        major = userProfile.getMajor();
        religion = userProfile.getReligion();
        location = userProfile.getLocation();
        personalityList = userProfile.getPersonality();
        mPersonalityToString = personalityList.toString();
        mPersonalityToString = mPersonalityToString.replace("[", "");
        mPersonalityToString = mPersonalityToString.replace("]", "");
        bloodType = userProfile.getBloodType();
        drinking = userProfile.getDrinking();
        smoking = userProfile.getSmoking();
        selfIntroduction = userProfile.getSelfIntroduction();
        story0 = userProfile.getStory0();
        story1 = userProfile.getStory1();
        story2 = userProfile.getStory2();
        photoUrl = userProfile.getPhotoUrl();

        mFinalPhotoUrlList = (ArrayList<String>) userProfile.getPhotoUrl();

        //?????????
        if (userProfile.isOfficialInfoPublic()) {
            if (userProfile.isOfficialUniversityPublic()) {
                mUniversityMenuTextView.setText("??????(??????)");
                mMajorMenuTextView.setText("??????(?????????)");
            } else {
                mUniversityMenuTextView.setText("??????(?????????)");
                mMajorMenuTextView.setText("??????(??????)");
            }
        } else {
            mUniversityMenuTextView.setText("??????(?????????)");
            mMajorMenuTextView.setText("??????(?????????)");
        }

        mSelfIntroductionEditText.setText(selfIntroduction);
        mStory0EditText.setText(story0);
        mStory1EditText.setText(story1);
        mStory2EditText.setText(story2);
        mNicknameTextView.setText(nickName);
        if(university == null || university.equals("")){
            mUniversityTextView.setText("?????????");
        } else{
            mUniversityTextView.setText(university);
        }

        if(major == null ||major.equals("")){
            mMajorTextView.setText("?????????");
        } else{
            mMajorTextView.setText(major);
        }

        mLocationTextView.setText(location);
        mPersonalityTextView.setText(mPersonalityToString);
        mBloodTextView.setText(bloodType);
        mReligionTextView.setText(religion);
        mDrinkingTextView.setText(drinking);
        mSmokingTextView.setText(smoking);
        mHeightTextView.setText(height);
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.signup_profileedit18_iv_photo0: //????????????
                        selectNum = 0;
                        clickPhotoView(selectNum);
                        break;
                    case R.id.signup_profileedit18_iv_photo1:
                        selectNum = 1;
                        clickPhotoView(selectNum);
                        break;
                    case R.id.signup_profileedit18_iv_photo2:
                        selectNum = 2;
                        clickPhotoView(selectNum);
                        break;
                    case R.id.signup_profileedit18_iv_photo3:
                        selectNum = 3;
                        clickPhotoView(selectNum);
                        break;
                    case R.id.signup_profileedit18_iv_photo4:
                        selectNum = 4;
                        clickPhotoView(selectNum);
                        break;
                    case R.id.signup_profileedit18_iv_photo5:
                        selectNum = 5;
                        clickPhotoView(selectNum);
                        break;
                    case R.id.signup_profileedit18_tv_guide:
                        startActivity(new Intent(SetProfile_2Edit.this, LaunchSignup_15Photo_Guide.class));
                        break;
                    case R.id.toolbar_submit_btn:
                        updateProfileToDB();
                        break;
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;
                    case R.id.signup_profileedit18_tv_nickname:
                        profileEditDialog2("?????????", nickName);
                        break;
                    case R.id.signup_profileedit18_tv_blood:
                        profileEditDialog("?????????", bloodType);
                        break;
                    case R.id.signup_profileedit18_tv_drinking:
                        profileEditDialog("??????", drinking);
                        break;
                    case R.id.signup_profileedit18_tv_height:
                        profileEditDialog("???", height);
                        break;
                    case R.id.signup_profileedit18_tv_religion:
                        profileEditDialog("??????", religion);
                        break;
                    case R.id.signup_profileedit18_tv_smoking:
                        profileEditDialog("??????", smoking);
                        break;
                    case R.id.signup_profileedit18_tv_personality:
                        Bundle bundle = new Bundle();
                        bundle.putStringArrayList("item", personalityList);
                        DialogEditPersonalityFragment dialog = DialogEditPersonalityFragment.getInstance();
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(), DialogEditPersonalityFragment.TAG_EVENT_DIALOG);
                        break;
                    case R.id.setprofile_2edit_linear_university:
                    case R.id.setprofile_2edit_linear_major:
                    case R.id.setprofile_2edit_tv_menu_university:
                    case R.id.setprofile_2edit_tv_menu_major:
                    case R.id.signup_profileedit_tv_university:
                    case R.id.signup_profileedit18_tv_major:
                        startActivity(new Intent(SetProfile_2Edit.this, SetProfile_4UniversityCertification.class));
                        break;
                    case R.id.signup_profileedit18_iv_location_update:
                        checkLocationPermission();
                        break;
                }
            }

        };

        mPhoto0ImageView.setOnClickListener(onClickListener);
        mPhoto1ImageView.setOnClickListener(onClickListener);
        mPhoto2ImageView.setOnClickListener(onClickListener);
        mPhoto3ImageView.setOnClickListener(onClickListener);
        mPhoto4ImageView.setOnClickListener(onClickListener);
        mPhoto5ImageView.setOnClickListener(onClickListener);
        mSubmit.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
        mProfileGuideTextView.setOnClickListener(onClickListener);
        mUniversityLayout.setOnClickListener(onClickListener);
        mMajorLayout.setOnClickListener(onClickListener);
        mUniversityMenuTextView.setOnClickListener(onClickListener);
        mMajorMenuTextView.setOnClickListener(onClickListener);
        mUniversityTextView.setOnClickListener(onClickListener);
        mMajorTextView.setOnClickListener(onClickListener);
        mNicknameTextView.setOnClickListener(onClickListener);
        mUniversityTextView.setOnClickListener(onClickListener);
        mMajorTextView.setOnClickListener(onClickListener);
        mPersonalityTextView.setOnClickListener(onClickListener);
        mBloodTextView.setOnClickListener(onClickListener);
        mReligionTextView.setOnClickListener(onClickListener);
        mDrinkingTextView.setOnClickListener(onClickListener);
        mSmokingTextView.setOnClickListener(onClickListener);
        mHeightTextView.setOnClickListener(onClickListener);
        mLocationUpdateImageView.setOnClickListener(onClickListener);
    }

    private void checkLocationPermission() {
        if (!GeoUtil.canGetLocation(this)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_CODE_DEVICE_LOCATION);
        } else {
            requestLocationPermission();
        }
    }


    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_APP_LOCATION);
            } else {
                permissionSetting(REQUEST_CODE_APP_LOCATION);
            }
        } else {
            Location currentLocation = null;
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation == null){
                //gps??? ????????? ???????????? ????????? network??? ?????? ??????
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (currentLocation != null) {
                GeoUtil.synchronizeLocation(currentLocation, this);

                String newLocation = GeoUtil.getAddress(currentLocation, this);
                GeoUtil.init();
                if(GeoUtil.koreaLocation.get(newLocation) != null){
                    newLocation = GeoUtil.koreaLocation.get(newLocation);
                }

                mLocationTextView.setText(newLocation);
                Toast.makeText(SetProfile_2Edit.this, "?????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
            } else {
                Toast.makeText(SetProfile_2Edit.this, "?????? ????????? ????????? ??? ????????????.", Toast.LENGTH_SHORT).show();
            }

            Log.d("test222", "////////////?????? ??? ?????????222 "+ currentLocation);
        }
    }

    private void permissionSetting(int requestCode) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("?????? ?????? ??????");
        alertDialogBuilder
                .setMessage("?????? ????????? ?????? ?????? ???????????? ?????? ????????? ???????????????. ???????????? ?????? ????????? ????????????!")
                .setCancelable(false)
                .setPositiveButton("?????? ?????? ??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, requestCode);
                        }
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(SetProfile_2Edit.this, "?????? ?????? ?????????", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void clickPhotoView(int selectNum){
        if (mScreeningResultList.size() > selectNum && mScreeningResultList.get(selectNum) != null && mScreeningResultList.get(selectNum).equals(SCREENING)) {
            Toast.makeText(SetProfile_2Edit.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
        } else {
            photoDialogRadio(selectNum);
        }
    }


    //????????? ????????? ?????? ???????????????
    private  void profileEditDialog(String title, String item){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("item",item);
        DialogEditFragment dialog = DialogEditFragment.getInstance();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), DialogEditFragment.TAG_EVENT_DIALOG);
    }

    //????????? ????????? ?????? ???????????????
    private  void profileEditDialog2(String title, String item){
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("item",item);
        DialogEdit2Fragment dialog = DialogEdit2Fragment.getInstance();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), DialogEdit2Fragment.TAG_EVENT_DIALOG);
    }

    //???????????? ???????????? ?????? ???????????????
    private void photoDialogRadio(int selectNum) {
        Bundle bundle = new Bundle();
        bundle.putInt("selectNum", selectNum);
        bundle.putStringArrayList("mFinalPhotoUrlList", mFinalPhotoUrlList);
        DialogEditPhotoFragment dialog = DialogEditPhotoFragment.getInstance();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), DialogEditPhotoFragment.TAG_EVENT_DIALOG);
    }


    @Override
    public void removePhoto() {
        if (mScreeningPhotoUrlList.size() > selectNum) {
            mLoaderLayout.setVisibility(View.VISIBLE);

            ArrayList<String> tempFinalPhotoUrlList = mFinalPhotoUrlList;
            ArrayList<String> tempScreeningPhotoUrlList = mScreeningPhotoUrlList;
            ArrayList<String> tempScreeningResultList = mScreeningResultList;
            ArrayList<String> tempScreeningFileNameList = mScreeningFileNameList;

            tempFinalPhotoUrlList.remove(selectNum);
            tempScreeningPhotoUrlList.remove(selectNum);
            tempScreeningResultList.remove(selectNum);
            tempScreeningFileNameList.remove(selectNum);

            Map<String, Object> deletePhoto = new HashMap<>();
            deletePhoto.put(FirebaseHelper.photoUrl, tempFinalPhotoUrlList);
            deletePhoto.put(FirebaseHelper.screeningPhotoUrl, tempScreeningPhotoUrlList);
            deletePhoto.put(FirebaseHelper.screeningResult, tempScreeningResultList);
            deletePhoto.put(FirebaseHelper.screeningFileName, tempScreeningFileNameList);

            WriteBatch batch = FirebaseHelper.db.batch();
            batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), deletePhoto);
            batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("ProfilePhoto").document(FirebaseHelper.mUid), deletePhoto);
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mFinalPhotoUrlList = tempFinalPhotoUrlList;
                    mScreeningPhotoUrlList = tempScreeningPhotoUrlList;
                    mScreeningResultList = tempScreeningResultList;
                    mScreeningFileNameList = tempScreeningFileNameList;
                    mLoaderLayout.setVisibility(View.GONE);
                    removePhoto(selectNum);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetProfile_2Edit.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                    mLoaderLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    private void switchArrayElement(int selectNum, ArrayList<String> list){
        String tempElement0 = list.get(0);
        String tempElementSelectNum = list.get(selectNum);
        list.remove(0);
        list.add(0, tempElementSelectNum);
        list.remove(selectNum);
        list.add(selectNum, tempElement0);
    }

    @Override
    public void changeRepresentPhoto() {
        if (!mScreeningResultList.get(selectNum).equals(SCREENING)) {
            mLoaderLayout.setVisibility(View.VISIBLE);

            ArrayList<String> tempFinalPhotoUrlList = mFinalPhotoUrlList;
            ArrayList<String> tempScreeningPhotoUrlList = mScreeningPhotoUrlList;
            ArrayList<String> tempScreeningResultList = mScreeningResultList;
            ArrayList<String> tempScreeningFileNameList = mScreeningFileNameList;

            switchArrayElement(selectNum, tempFinalPhotoUrlList);
            switchArrayElement(selectNum, tempScreeningPhotoUrlList);
            switchArrayElement(selectNum, tempScreeningResultList);
            switchArrayElement(selectNum, tempScreeningFileNameList);

            //?????? ?????? ?????? ?????? ?????? ?????????
            Map<String, Object> changePhoto = new HashMap<>();
            changePhoto.put(FirebaseHelper.photoUrl, tempFinalPhotoUrlList);
            changePhoto.put(FirebaseHelper.screeningPhotoUrl, tempScreeningPhotoUrlList);
            changePhoto.put(FirebaseHelper.screeningResult, tempScreeningResultList);
            changePhoto.put(FirebaseHelper.screeningFileName, tempScreeningFileNameList);

            WriteBatch batch = FirebaseHelper.db.batch();
            batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), changePhoto);
            batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("ProfilePhoto").document(FirebaseHelper.mUid), changePhoto);
            batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mLoaderLayout.setVisibility(View.GONE);
                    mFinalPhotoUrlList = tempFinalPhotoUrlList;
                    mScreeningPhotoUrlList = tempScreeningPhotoUrlList;
                    mScreeningResultList = tempScreeningResultList;
                    mScreeningFileNameList = tempScreeningFileNameList;
                    changeRepresentPhoto(selectNum);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mLoaderLayout.setVisibility(View.GONE);
                    Toast.makeText(SetProfile_2Edit.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void updatePersonality(ArrayList<String> checkedPersonalityList) {
        personalityList = checkedPersonalityList;
        mPersonalityToString = personalityList.toString();
        mPersonalityToString = mPersonalityToString.replace("[","");
        mPersonalityToString = mPersonalityToString.replace("]","");
        mPersonalityTextView.setText(mPersonalityToString);
    }


    public void updateProfile(String from, String checkedProfile){
        switch (from){
            case "?????????":
                mBloodTextView.setText(checkedProfile);
                bloodType = checkedProfile;
                break;
            case "??????":
                mDrinkingTextView.setText(checkedProfile);
                drinking = checkedProfile;
                break;
            case "???":
                mHeightTextView.setText(checkedProfile);
                height = checkedProfile;
                break;
            case "??????":
                mLocationTextView.setText(checkedProfile);
                location = checkedProfile;
                break;
            case "??????":
                mReligionTextView.setText(checkedProfile);
                religion = checkedProfile;
                break;
            case "??????":
                mSmokingTextView.setText(checkedProfile);
                smoking = checkedProfile;
                break;
        }


    }

    //?????? ?????? ??? ?????? ????????? ?????????
    private void listenScreeningResult() {
        registration = FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .addSnapshotListener(SetProfile_2Edit.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {
                            if (snapshot.get(FirebaseHelper.screeningResult) != null && snapshot.get(FirebaseHelper.screeningPhotoUrl) != null && snapshot.get(FirebaseHelper.screeningFileName) != null && snapshot.get(FirebaseHelper.photoUrl) != null) {
                                ArrayList<String> screeningResultList = (ArrayList<String>) snapshot.get(FirebaseHelper.screeningResult);
                                ArrayList<String> screeningPhotoUrlList = (ArrayList<String>) snapshot.get(FirebaseHelper.screeningPhotoUrl);
                                ArrayList<String> screeningFileNameList = (ArrayList<String>) snapshot.get(FirebaseHelper.screeningFileName);
                                ArrayList<String> finalPhotoUrlList = (ArrayList<String>) snapshot.get(FirebaseHelper.photoUrl);

                                mScreeningResultList = screeningResultList;
                                mScreeningPhotoUrlList = screeningPhotoUrlList;
                                mScreeningFileNameList = screeningFileNameList;
                                mFinalPhotoUrlList = finalPhotoUrlList;

                                int count = Math.min(screeningPhotoUrlList.size(), screeningResultList.size());

                                for(int i = count; i < 6; i++){
                                    mPhotoImageViewList.get(i).setImageDrawable(null);
                                    mPhotoScreeningTextViewList.get(i).setVisibility(View.GONE);
                                }

                                for (int i = 0; i < count ; i++) {
                                    if (screeningResultList.get(i) != null && screeningResultList.get(i).equals(SCREENING)) {
                                        if (!SetProfile_2Edit.this.isDestroyed()) {
                                            Glide.with(SetProfile_2Edit.this).load(screeningPhotoUrlList.get(i)).fitCenter().thumbnail(0.1f).into(mPhotoImageViewList.get(i));
                                            mPhotoScreeningTextViewList.get(i).setVisibility(View.VISIBLE);
                                        }
                                    } else if (screeningResultList.get(i) != null && screeningResultList.get(i).equals(PASS)) {
                                        if (!SetProfile_2Edit.this.isDestroyed()) {
                                            Glide.with(SetProfile_2Edit.this).load(screeningPhotoUrlList.get(i)).fitCenter().thumbnail(0.1f).into(mPhotoImageViewList.get(i));
                                            mPhotoScreeningTextViewList.get(i).setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }

                            User user = snapshot.toObject(User.class);
                            MyProfile.init(user);
                            updateUI();
                        } else {
                            Log.d(TAG, "Current data: null");
                        }
                    }
                });
    }

    //????????? ?????????????????? ?????? imageView??? ????????????, photoUriList??? ??????.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                    Uri sourceUri = data.getData();
                    if (sourceUri != null) {
                        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "ProfilePhoto" + uriPhotoCount));
                        openCropActivity(sourceUri, destinationUri);
                    } else {
                        Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    uriPhotoCount++;
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        //??????????????? ??????
                        updatePhotoToDBandStorage(selectNum, resultUri);
                        Log.d("CropTest", "??????????????????." + resultUri);
                    } else {
                        Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePhotoToDBandStorage(int selectNum, Uri resultUri) {
        if (!isLoading) {
            isLoading = true;
            mLoaderLayout.setVisibility(View.VISIBLE);

            if(registration != null){
                registration.remove();
            }

            ArrayList<String> tempFinalPhotoUrlList = mFinalPhotoUrlList;
            ArrayList<String> tempScreeningPhotoUrlList = mScreeningPhotoUrlList;
            ArrayList<String> tempScreeningResultList = mScreeningResultList;
            ArrayList<String> tempScreeningFileNameList = mScreeningFileNameList;

            String lastPath = "ProfilePhoto" + DateUtil.getDateSec();
            String fileName = "users/" + FirebaseHelper.mUid + "/edit/" + lastPath;

            final StorageReference profileStorageRef = FirebaseHelper.storageRef.child(fileName);
            UploadTask uploadTask = BitmapConverter.getUploadTask(profileStorageRef, resultUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //????????? ????????? ?????? url ????????????
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.d(TAG, "??????" + task.getException());
                                isLoading = false;
                                mLoaderLayout.setVisibility(View.GONE);
                                listenScreeningResult();
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return profileStorageRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                //???????????? url ???????????? ??????
                                Log.d(TAG, "?????? ???????????? url ????????? :" + task.getResult());
                                final int listSize = mScreeningPhotoUrlList.size();
                                if (listSize > selectNum) { //?????? ?????? ????????? ??????
                                    tempScreeningPhotoUrlList.remove(selectNum);
                                    tempScreeningResultList.remove(selectNum);
                                    tempScreeningFileNameList.remove(selectNum);

                                    tempScreeningPhotoUrlList.add(selectNum, String.valueOf(task.getResult()));
                                    tempScreeningResultList.add(selectNum, SCREENING);
                                    tempScreeningFileNameList.add(selectNum, fileName);
                                } else { //?????? ???????????? ??????
                                    tempFinalPhotoUrlList.add(listSize, null);
                                    tempScreeningPhotoUrlList.add(listSize, String.valueOf(task.getResult()));
                                    tempScreeningResultList.add(listSize, SCREENING);
                                    tempScreeningFileNameList.add(listSize, fileName);
                                }
                                //screeningPhotoUrl ????????????
                                Map<String, Object> editPhoto = new HashMap<>();
                                editPhoto.put(FirebaseHelper.photoUrl, tempFinalPhotoUrlList);
                                editPhoto.put(FirebaseHelper.screeningPhotoUrl, tempScreeningPhotoUrlList);
                                editPhoto.put(FirebaseHelper.screeningResult, tempScreeningResultList);
                                editPhoto.put(FirebaseHelper.screeningFileName, tempScreeningFileNameList);

                                WriteBatch batch = FirebaseHelper.db.batch();
                                batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), editPhoto);
                                batch.set(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid).collection("ProfilePhoto").document(FirebaseHelper.mUid), editPhoto);
                                batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (listSize > selectNum) {
                                            setImageResource(selectNum, resultUri);
                                        } else {
                                            setImageResource(listSize, resultUri);
                                        }

                                        mFinalPhotoUrlList = tempFinalPhotoUrlList;
                                        mScreeningPhotoUrlList = tempScreeningPhotoUrlList;
                                        mScreeningResultList = tempScreeningResultList;
                                        mScreeningFileNameList = tempScreeningFileNameList;
                                        isLoading = false;
                                        mLoaderLayout.setVisibility(View.GONE);
                                        listenScreeningResult();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SetProfile_2Edit.this, "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                        isLoading = false;
                                        mLoaderLayout.setVisibility(View.GONE);
                                        listenScreeningResult();
                                    }
                                });
                            } else {
                                Toast.makeText(SetProfile_2Edit.this, "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                isLoading = false;
                                mLoaderLayout.setVisibility(View.GONE);
                                listenScreeningResult();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetProfile_2Edit.this, "?????? ???????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                    mLoaderLayout.setVisibility(View.GONE);
                    listenScreeningResult();
                }
            });
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(5, 5)
                .start(this);
    }

    //?????? num????????? ?????? set
    private void setImageResource(int selectNum, Uri resultUri) {
        mPhotoImageViewList.get(selectNum).setImageURI(null);
        mPhotoImageViewList.get(selectNum).setImageURI(resultUri);
        mPhotoScreeningTextViewList.get(selectNum).setVisibility(View.VISIBLE);
    }


    private void changeRepresentPhoto(int selectNum){
        mPhoto0ImageView.setImageURI(null);
        Glide.with(this).load(mScreeningPhotoUrlList.get(0)).fitCenter().thumbnail(0.1f).into(mPhoto0ImageView);

        mPhotoImageViewList.get(selectNum).setImageURI(null);
        Glide.with(this).load(mScreeningPhotoUrlList.get(selectNum)).fitCenter().thumbnail(0.1f).into(mPhotoImageViewList.get(selectNum));

    }

    //??????????????? ?????????
    private void removePhoto(int selectNum) { //????????? ???????????? ?????? ??????
        for(int i = 0; i < 6; i++ ){
            if (selectNum <= i) {
                if (mScreeningResultList.size() == i) { //?????? ?????????????????? ?????? ???????????? ???
                    mPhotoImageViewList.get(i).setImageDrawable(null);
                }
                if (mScreeningResultList.size() > i) { //?????? ???????????? ????????? ??????
                    Glide.with(this).load(mScreeningPhotoUrlList.get(i)).fitCenter().thumbnail(0.1f).into(mPhotoImageViewList.get(i));
                    if(mScreeningResultList.size() > selectNum && mScreeningResultList.get(i) != null && mScreeningResultList.get(i).equals(SCREENING)){
                        mPhotoScreeningTextViewList.get(i).setVisibility(View.VISIBLE);
                    }
                    if(i < 5){
                        mPhotoImageViewList.get(i+1).setImageDrawable(null);
                        mPhotoScreeningTextViewList.get(i+1).setVisibility(View.GONE);
                    }
                }
            }
        }

//        if (selectNum == 0) {
//            if (mScreeningResultList.size() == 0) {
//                mPhoto0ImageView.setImageDrawable(null);
//            }
//            if (mScreeningResultList.size() > 0) {
//                Glide.with(this).load(mScreeningPhotoUrlList.get(0)).fitCenter().thumbnail(0.1f).into(mPhoto0ImageView);
//                if(mScreeningResultList.size() > selectNum && mScreeningResultList.get(0) != null && mScreeningResultList.get(0).equals(SCREENING)){
//                    mPhoto0ScreeningTextView.setVisibility(View.VISIBLE);
//                }
//                mPhoto1ImageView.setImageDrawable(null);
//                mPhoto1ScreeningTextView.setVisibility(View.GONE);
//            }
//        }
//        if (selectNum <= 1) {
//            if (mScreeningResultList.size() == 1) {
//                mPhoto1ImageView.setImageDrawable(null);
//            }
//            if (mScreeningResultList.size() > 1) {
//                Glide.with(this).load(mScreeningPhotoUrlList.get(1)).fitCenter().thumbnail(0.1f).into(mPhoto1ImageView);
//                if(mScreeningResultList.size() > selectNum && mScreeningResultList.get(1) != null && mScreeningResultList.get(1).equals(SCREENING)){
//                    mPhoto1ScreeningTextView.setVisibility(View.VISIBLE);
//                }
//                mPhoto2ImageView.setImageDrawable(null);
//                mPhoto2ScreeningTextView.setVisibility(View.GONE);
//            }
//        }
//        if (selectNum <= 2) {
//            if (mScreeningResultList.size()  == 2) {
//                mPhoto2ImageView.setImageDrawable(null);
//            }
//            if (mScreeningResultList.size() > 2) {
//                Glide.with(this).load(mScreeningPhotoUrlList.get(2)).fitCenter().thumbnail(0.1f).into(mPhoto2ImageView);
//                if(mScreeningResultList.size() > selectNum && mScreeningResultList.get(2) != null && mScreeningResultList.get(2).equals(SCREENING)){
//                    mPhoto2ScreeningTextView.setVisibility(View.VISIBLE);
//                }
//                mPhoto3ImageView.setImageDrawable(null);
//                mPhoto3ScreeningTextView.setVisibility(View.GONE);
//            }
//        }
//        if (selectNum <= 3) {
//            if (mScreeningResultList.size()  == 3) {
//                mPhoto3ImageView.setImageDrawable(null);
//            }
//            if (mScreeningResultList.size() > 3) {
//                Glide.with(this).load(mScreeningPhotoUrlList.get(3)).fitCenter().thumbnail(0.1f).into(mPhoto3ImageView);
//                if(mScreeningResultList.size() > selectNum && mScreeningResultList.get(3) != null && mScreeningResultList.get(3).equals(SCREENING)){
//                    mPhoto3ScreeningTextView.setVisibility(View.VISIBLE);
//                }
//                mPhoto4ImageView.setImageDrawable(null);
//                mPhoto4ScreeningTextView.setVisibility(View.GONE);
//            }
//        }
//        if (selectNum <= 4) {
//            if (mScreeningResultList.size()  == 4) {
//                mPhoto4ImageView.setImageDrawable(null);
//            }
//            if (mScreeningResultList.size() > 4) {
//                Glide.with(this).load(mScreeningPhotoUrlList.get(4)).fitCenter().thumbnail(0.1f).into(mPhoto4ImageView);
//                if(mScreeningResultList.size() > selectNum && mScreeningResultList.get(4) != null && mScreeningResultList.get(4).equals(SCREENING)){
//                    mPhoto4ScreeningTextView.setVisibility(View.VISIBLE);
//                }
//                mPhoto5ImageView.setImageDrawable(null);
//                mPhoto5ScreeningTextView.setVisibility(View.GONE);
//
//            }
//        }
//        if (selectNum <= 5) {
//            if (mScreeningResultList.size()  == 5) {
//                mPhoto5ImageView.setImageDrawable(null);
//            }
//        }
    }



    //DB??? ????????? ?????? ??????
    private void updateProfileToDB() {
        if(!isLoading) {
            isLoading = true;
            mLoaderLayout.setVisibility(View.VISIBLE);

            //????????? ??? ???????????? ?????????
            story0 = mStory0EditText.getText().toString().trim();
            story1 = mStory1EditText.getText().toString().trim();
            story2 = mStory2EditText.getText().toString().trim();
            selfIntroduction = mSelfIntroductionEditText.getText().toString().trim();

            Map<String, Object> profileData = new HashMap<>();
            profileData.put(FirebaseHelper.nickname, nickName);
            profileData.put(FirebaseHelper.location, location);
            profileData.put(FirebaseHelper.height, height);
            profileData.put(FirebaseHelper.personality, personalityList);
            profileData.put(FirebaseHelper.bloodType, bloodType);
            profileData.put(FirebaseHelper.smoking, smoking);
            profileData.put(FirebaseHelper.drinking, drinking);
            profileData.put(FirebaseHelper.religion, religion);
            profileData.put(FirebaseHelper.selfIntroduction, selfIntroduction);
            profileData.put(FirebaseHelper.story0, story0);
            profileData.put(FirebaseHelper.story1, story1);
            profileData.put(FirebaseHelper.story2, story2);

            UserForEdit userForEdit = new UserForEdit(nickName, location, height, personalityList, bloodType, smoking, drinking, religion, selfIntroduction, story0, story1, story2);


            //?????? ??????????????? ????????? ??????
            FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).update(profileData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "???????????? ??????");
                            MyProfile.edit(userForEdit); //?????? ?????? static ????????? ?????????
                            Toast.makeText(SetProfile_2Edit.this, "?????????????????????.", Toast.LENGTH_SHORT).show();
                            mLoaderLayout.setVisibility(View.GONE);
                            isLoading = false;
                            setResult(RESULT_OK);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetProfile_2Edit.this, "????????? ????????? ?????????????????????. ???????????? ??? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    mLoaderLayout.setVisibility(View.GONE);
                    isLoading = false;
                }
            });

        }
    }



    private void setMaxLine(){

        mSelfIntroductionEditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSelfIntroductionEditText.getLineCount() >= 8) {
                    mSelfIntroductionEditText.setText(previousString);
                    mSelfIntroductionEditText.setSelection(mSelfIntroductionEditText.length());
                }
            }
        });

        mStory0EditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStory0EditText.getLineCount() >= 8) {
                    mStory0EditText.setText(previousString);
                    mStory0EditText.setSelection(mStory0EditText.length());
                }
            }
        });

        mStory1EditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStory1EditText.getLineCount() >= 8) {
                    mStory1EditText.setText(previousString);
                    mStory1EditText.setSelection(mStory1EditText.length());
                }
            }
        });

        mStory2EditText.addTextChangedListener(new TextWatcher() {
            String previousString = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mStory2EditText.getLineCount() >= 8) {
                    mStory2EditText.setText(previousString);
                    mStory2EditText.setSelection(mStory2EditText.length());
                }
            }
        });

    }


}
