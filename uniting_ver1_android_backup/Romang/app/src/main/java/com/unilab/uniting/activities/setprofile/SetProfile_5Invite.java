package com.unilab.uniting.activities.setprofile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.unilab.uniting.R;
import com.unilab.uniting.model.Dia;
import com.unilab.uniting.model.Invite;
import com.unilab.uniting.model.Notification;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

//초대코드 입력하고 인증시 각각 하트30개씩 지급하는 클래스
public class SetProfile_5Invite extends BasicActivity {
    static final String TAG = "SetProfile_5InviteT";

    //xml
    private LinearLayout mBackLinearLayout;
    private TextView mDiaTextView;
    private TextView mMyCodeTextView;
    private Button mCopyButton;
    private EditText mEnterCodeEditText;
    private Button mOkButton;
    private RelativeLayout mLoaderLayout; //로딩레이아웃
    private LinearLayout mCertiLayout;
    private TextView mCertiTextView;

    //value
    View.OnClickListener onClickListener;
    private String mEnterCode = ""; //코드입력값
    private String mEnterCodeDI =""; //입력한 코드의 주인이 되는 uid
    private String mPartnerUid = "";
    private String mMyInviteCode = "";
    private boolean checkValidity1 = false;
    private boolean checkValidity2 = false;
    private boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_5_invite);
        initView();
        setOnClickListener();
    }

    private void initView() {
        mBackLinearLayout = findViewById(R.id.setprofile_invite5_li_back);
        mDiaTextView = findViewById(R.id.setprofile_invite5_tv_heart);
        mMyCodeTextView = findViewById(R.id.setprofile_invite5_tv_mycode);
        mCopyButton = findViewById(R.id.setprofile_invite5_btn_copy);
        mEnterCodeEditText = findViewById(R.id.setprofile_invite5_et_entercode);
        mOkButton = findViewById(R.id.setprofile_invite5_btn_ok);
        mCertiLayout = findViewById(R.id.setprofile_invite5_linear_certificate);
        mCertiTextView = findViewById(R.id.setprofile_invite5_tv_certificate);

        //로딩, 툴바 레이아웃
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
        mLoaderLayout.setVisibility(View.VISIBLE);

        if (MyProfile.getUser().isOfficialMajorPublic()) {
            mCertiLayout.setVisibility(View.GONE);
        }

        if (!MyProfile.getUser().getInviteCode().equals("")) {
            mMyInviteCode = MyProfile.getUser().getInviteCode();
            mMyCodeTextView.setText(mMyInviteCode);
        } else {
            Toast.makeText(SetProfile_5Invite.this, "초대코드가 생성되지 않았습니다. 앱을 완전히 종료후 재시작 해주세요.", Toast.LENGTH_SHORT).show();
        }

        //현재 하트 정보
        FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Dia").orderBy(FirebaseHelper.diaId, Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        mLoaderLayout.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Dia dia = document.toObject(Dia.class);
                                mDiaTextView.setText(dia.getCurrentDia() + "");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.setprofile_invite5_li_back:
                        onBackPressed();
                        break;
                    case R.id.setprofile_invite5_tv_certificate:
                        startActivity(new Intent(SetProfile_5Invite.this, SetProfile_4UniversityCertification.class));
                        break;
                    case R.id.setprofile_invite5_btn_copy:
                        //클립보드 사용 코드
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("CODE", mMyCodeTextView.getText().toString().trim()); //클립보드에 ID라는 이름표로 id 값을 복사하여 저장
                        clipboardManager.setPrimaryClip(clipData);
                        //복사가 되었다면 토스트메시지 노출
                        Toast.makeText(getApplicationContext(), "코드가 복사되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.setprofile_invite5_btn_ok:
                        mEnterCode = mEnterCodeEditText.getText().toString().trim();

                        if(mEnterCode.equals("")){
                            Toast.makeText(SetProfile_5Invite.this, "초대 코드를 입력해 주세요.", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        if (mMyInviteCode.equals(mEnterCode)) {
                            Toast.makeText(SetProfile_5Invite.this, "자신의 초대 코드는 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            break;
                        }

                        //mEnterCode 초대코드 주인 uid 가져오기
                        mLoaderLayout.setVisibility(View.VISIBLE);
                        FirebaseHelper.db.collection("Users").whereEqualTo(FirebaseHelper.inviteCode, mEnterCode).get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            int count = 0;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d(TAG, document.getId() + " => " + document.getData());
                                                mEnterCodeDI = document.getId();
                                                mPartnerUid = (String) document.get(FirebaseHelper.uid);
                                                count++;
                                            }

                                            if (count != 1) {
                                                Toast.makeText(SetProfile_5Invite.this, "유효한 초대코드가 아닙니다.", Toast.LENGTH_SHORT).show();
                                                mLoaderLayout.setVisibility(View.GONE);
                                                return;
                                            }

                                            //entercode 주인이 내 추천코드를 사용했는지 체크 (Invite 콜렉션 내의 document 이름은 모두 guestUid임. 즉 본인의 uid doc을 보면 누가 나를 초대했는지 알 수 있음)
                                            FirebaseHelper.db.collection("UserDI").document(mEnterCodeDI).collection("Invite").document(mEnterCodeDI)
                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document2 = task.getResult();
                                                        if (document2.exists()) { //있다면 상대방이 누구 초대코드 사용했는지 확인
                                                            Invite invite = document2.toObject(Invite.class);
                                                            String hostDI = invite.getHostDI();
                                                            if (hostDI.equals(MyProfile.getUser().getDi())) { //중복(상대방이 내 코드로 하트를 이미 받은 경우)
                                                                Toast.makeText(SetProfile_5Invite.this, "회원님이 이미 상대방을 초대하였습니다.", Toast.LENGTH_SHORT).show();
                                                                mLoaderLayout.setVisibility(View.GONE);
                                                                return;
                                                            }
                                                        }

                                                        checkValidity1 = true;
                                                        if (checkValidity2) {
                                                            checkValidity1 = false; //다시 눌렀을 때를 대비해서 초기화
                                                            checkValidity2 = false;
                                                            updateInviteToDB();
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with2 ", task.getException());
                                                        Toast.makeText(SetProfile_5Invite.this, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                                        mLoaderLayout.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                            //내가 이미 다른 사람의 초대를 받은적이 있는지 확인하는 코드
                                            FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()).collection("Invite").document(MyProfile.getUser().getDi()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) { //이미 초대 받은 경우
                                                            Toast.makeText(SetProfile_5Invite.this, "이미 초대코드를 사용하셨습니다.", Toast.LENGTH_SHORT).show();
                                                            mLoaderLayout.setVisibility(View.GONE);
                                                            return;
                                                        }

                                                        checkValidity2 = true;
                                                        if (checkValidity1) {
                                                            checkValidity1 = false; //다시 눌렀을 때를 대비해서 초기화
                                                            checkValidity2 = false;
                                                            updateInviteToDB();
                                                        }
                                                    } else {
                                                        Log.d(TAG, "get failed with 3", task.getException());
                                                        Toast.makeText(SetProfile_5Invite.this, "네트워크가 불안정합니다", Toast.LENGTH_SHORT).show();
                                                        mLoaderLayout.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                            mLoaderLayout.setVisibility(View.GONE);
                                        }
                                    }
                                });
                        break;
                }
            }
        };

        mCertiTextView.setOnClickListener(onClickListener);
        mBackLinearLayout.setOnClickListener(onClickListener);
        mCopyButton.setOnClickListener(onClickListener);
        mOkButton.setOnClickListener(onClickListener);
    }



    private void updateInviteToDB(){
        //초대 코드 사용 기록 (상대방에게도 내가 상대방꺼를 썼다는걸 기록! 상대방이 내꺼 쓴게 아님!!!)
        String timeStamp = DateUtil.getTimeStampUnix();
        Invite myInvite = new Invite(mEnterCodeDI, MyProfile.getUser().getDi());
        Notification hostNotification = new Notification("초대", myInvite, DateUtil.getDateMin(),DateUtil.getUnixTimeLong(), "운영자", "", mPartnerUid, false);
        Notification myNotification = new Notification("초대", myInvite, DateUtil.getDateMin(), DateUtil.getUnixTimeLong(),"운영자","", FirebaseHelper.mUid, false);

        WriteBatch batch = FirebaseHelper.db.batch();
        batch.set(FirebaseHelper.db.collection("UserDI").document(MyProfile.getUser().getDi()).collection("Invite").document(MyProfile.getUser().getDi()), myInvite);
        batch.set(FirebaseHelper.db.collection("UserDI").document(mEnterCodeDI).collection("Invite").document(MyProfile.getUser().getDi()), myInvite);
        batch.set(FirebaseHelper.db.collection("Users").document(mPartnerUid).collection("Notification").document(timeStamp), hostNotification);
        batch.set(FirebaseHelper.db.collection("Users").document(FirebaseHelper.mUid).collection("Notification").document(timeStamp), myNotification);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SetProfile_5Invite.this, "초대코드 인증이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SetProfile_5Invite.this, "오류 발생", Toast.LENGTH_SHORT).show();
                }
                mLoaderLayout.setVisibility(View.GONE);
                mPartnerUid = "";
                mEnterCodeDI = "";
            }
        });
    }


}
