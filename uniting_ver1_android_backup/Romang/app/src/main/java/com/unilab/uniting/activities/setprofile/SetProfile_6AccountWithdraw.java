package com.unilab.uniting.activities.setprofile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogOkFragment;
import com.unilab.uniting.fragments.setprofile.DialogWithdraw2OkNoFragment;
import com.unilab.uniting.fragments.setprofile.DialogWithdrawOkNoFragment;
import com.unilab.uniting.model.DIModel;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.LaunchUtil;
import com.unilab.uniting.utils.LogData;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

import java.util.HashMap;
import java.util.Map;

public class SetProfile_6AccountWithdraw extends BasicActivity implements DialogWithdrawOkNoFragment.DialogDormantListener, DialogWithdraw2OkNoFragment.DialogWithdrawListener {

    private Button mPauseButton;
    private TextView mWithdrawTextView;
    private LinearLayout mBack;
    private RelativeLayout mLoaderLayout; //로딩레이아웃

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_6_account_withdraw);

        mPauseButton = findViewById(R.id.setprofile_6account_btn_pause);
        mWithdrawTextView = findViewById(R.id.setprofile_6account_tv_withdraw);
        mBack = findViewById(R.id.toolbar_back);
        mLoaderLayout = findViewById(R.id.loaderLayout);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mPauseButton.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Strings.from, Strings.pause);
                DialogWithdrawOkNoFragment dialog = DialogWithdrawOkNoFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogWithdrawOkNoFragment.TAG_EVENT_DIALOG);
            }
        });

        mWithdrawTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Strings.from, Strings.withdraw);
                DialogWithdrawOkNoFragment dialog = DialogWithdrawOkNoFragment.getInstance();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), DialogWithdrawOkNoFragment.TAG_EVENT_DIALOG);
            }
        });

    }

    @Override
    public void dormant() {
        //멤버쉽 4단계가 휴면, 5단계 탈퇴

        mLoaderLayout.setVisibility(View.VISIBLE);
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), FirebaseHelper.membership, LaunchUtil.Dormant);
        batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.membership, LaunchUtil.Dormant);
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);
                Map<String,String> userProps = new HashMap<>();
                userProps.put(LogData.membership, LaunchUtil.Dormant);
                LogData.setUserProperties(userProps, SetProfile_6AccountWithdraw.this);
                LogData.eventLog(LogData.Dormant, SetProfile_6AccountWithdraw.this);

                DialogOkFragment dialog2 = DialogOkFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString(Strings.EXTRA_LOCATION, Strings.withdraw1);
                dialog2.setArguments(bundle);
                dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(SetProfile_6AccountWithdraw.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void withdraw() {
        //멤버쉽 4단계가 휴면, 5단계 탈퇴
        String timeStamp = DateUtil.getTimeStampUnix();
        long unixTime = DateUtil.getUnixTimeLong();
        DIModel diModel = new DIModel(MyProfile.getUser().getDi(), MyProfile.getUser().getUid(), MyProfile.getUser().getFacebookUid(), "", MyProfile.getUser().getEmail(), LaunchUtil.Withdraw, MyProfile.getUser().getInviteCode(), unixTime);

        Map<String,String> userProps = new HashMap<>();
        userProps.put(LogData.membership, LaunchUtil.Withdraw);
        LogData.setUserProperties(userProps, SetProfile_6AccountWithdraw.this);
        LogData.eventLog(LogData.Withdraw, SetProfile_6AccountWithdraw.this);

        mLoaderLayout.setVisibility(View.VISIBLE);
        WriteBatch batch = FirebaseHelper.db.batch();
        batch.update(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid), FirebaseHelper.membership, LaunchUtil.Withdraw);
        batch.update(FirebaseHelper.db.collection("AdminUsers").document(FirebaseHelper.mUid), FirebaseHelper.membership, LaunchUtil.Withdraw);
        batch.set(FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()), diModel, SetOptions.merge());
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mLoaderLayout.setVisibility(View.GONE);
                DialogOkFragment dialog2 = DialogOkFragment.getInstance();
                Bundle bundle = new Bundle();
                bundle.putString(Strings.EXTRA_LOCATION, Strings.withdraw2);
                dialog2.setArguments(bundle);
                dialog2.show(getSupportFragmentManager(), DialogOkFragment.TAG_MEETING_DIALOG2);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mLoaderLayout.setVisibility(View.GONE);
                Toast.makeText(SetProfile_6AccountWithdraw.this, "오류 발생. 고객센터로 문의해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
