package com.unilab.uniting.activities.setprofile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.unilab.uniting.R;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.MyProfile;

public class SetProfileMainActivity extends BasicActivity {

    final static String TAG ="SetProfileMainTAG";
    ImageView profileImage;

    private LinearLayout mBack;
    private ImageView mMyProfile;
    private TextView mEdit, mNickName, mAge;
    private LinearLayout mBlockFriend;
    private LinearLayout mUniversityCertification;
    private LinearLayout mInvite;
    private LinearLayout mAccount;
    private LinearLayout mNotificationSetting;
    private LinearLayout mSuggestion;
    private LinearLayout mCustomerService;

    Button.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_main);

        init();
        setOnClickListener();
    }

    private void init(){
        //xml 바인딩
        profileImage = findViewById(R.id.setprofile_main_iv_myProfile);
        mBack = findViewById(R.id.toolbar_back);
        mMyProfile = findViewById(R.id.setprofile_main_iv_myProfile);
        mEdit = findViewById(R.id.setprofile_main_tv_edit);
        mBlockFriend = findViewById(R.id.setprofile_main_linear_blockFriend);
        mUniversityCertification = findViewById(R.id.setprofile_main_linear_universityCertification);
        mInvite = findViewById(R.id.setprofile_main_linear_invite);
        mAccount = findViewById(R.id.setprofile_main_linear_account);
        mNotificationSetting = findViewById(R.id.setprofile_main_linear_notificationSetting);
        mSuggestion = findViewById(R.id.setprofile_main_linear_suggestion);
        mCustomerService = findViewById(R.id.setprofile_main_linear_customerService);
        mNickName = findViewById(R.id.setprofile_main_tv_nickname);
        mAge = findViewById(R.id.setprofile_main_tv_age);
    }

    private void setOnClickListener(){
        onClickListener = new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.setprofile_main_linear_notificationSetting :
                        Intent notificationSettingIntent = new Intent(SetProfileMainActivity.this, SetProfile_7NotificationSetting.class);
                        startActivity(notificationSettingIntent);
                        break;
                    case R.id.setprofile_main_iv_myProfile:
                        Intent myProfileIntent = new Intent(SetProfileMainActivity.this, SetProfile_1MyProfile.class);
                        startActivity(myProfileIntent);
                        break;
                    case R.id.setprofile_main_tv_edit :
                        Intent editIntent = new Intent(SetProfileMainActivity.this, SetProfile_2Edit.class);
                        startActivity(editIntent);
                        break;
                    case R.id.setprofile_main_linear_blockFriend :
                        Intent blockFriendIntent = new Intent(SetProfileMainActivity.this, SetProfile_3BlockFriend.class);
                        startActivity(blockFriendIntent);
                        break;
                    case R.id.setprofile_main_linear_universityCertification :
                        Intent universityCertificationIntent = new Intent(SetProfileMainActivity.this, SetProfile_4UniversityCertification.class);
                        startActivity(universityCertificationIntent);
                        break;
                    case R.id.setprofile_main_linear_invite :
                        Intent inviteIntent = new Intent(SetProfileMainActivity.this, SetProfile_5Invite.class);
                        startActivity(inviteIntent);
                        break;
                    case R.id.setprofile_main_linear_account :
                        Intent accountIntent = new Intent(SetProfileMainActivity.this, SetProfile_6Account.class);
                        startActivity(accountIntent);
                        break;
                    case R.id.setprofile_main_linear_suggestion :
                        Intent suggestionIntent = new Intent(SetProfileMainActivity.this, SetProfile_8Suggestion.class);
                        startActivity(suggestionIntent);
                        break;
                    case R.id.setprofile_main_linear_customerService :
                        Intent customerServiceIntent = new Intent(SetProfileMainActivity.this, SetProfile_9CustomerService.class);
                        startActivity(customerServiceIntent);
                        break;
                    case R.id.toolbar_back:
                        onBackPressed();
                        break;

                }
            }
        };

        mMyProfile.setOnClickListener(onClickListener);
        mEdit.setOnClickListener(onClickListener);
        mBlockFriend.setOnClickListener(onClickListener);
        mUniversityCertification.setOnClickListener(onClickListener);
        mInvite.setOnClickListener(onClickListener);
        mAccount.setOnClickListener(onClickListener);
        mNotificationSetting.setOnClickListener(onClickListener);
        mSuggestion.setOnClickListener(onClickListener);
        mCustomerService.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
        refreshMyProfile();

    }

    private void updateUI(){
        User user = MyProfile.getUser();
        RequestOptions circleCrop = new RequestOptions().circleCrop();
        if (!this.isDestroyed()) {
            if(user.getPhotoUrl()!= null && user.getPhotoUrl().size() > 0 && user.getPhotoUrl().get(0) != null){
                Glide.with(SetProfileMainActivity.this).load(user.getPhotoUrl().get(0)) .apply(circleCrop).into(profileImage);
            }
        }
        mNickName.setText(user.getNickname());
        mAge.setText(user.getAge());
    }

    private void refreshMyProfile(){
        LaunchUtil.checkAuth(this);
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User user = document.toObject(User.class);
                        MyProfile.init(user);
                        updateUI();
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}
