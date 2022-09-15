package com.unilab.uniting.fragments.meeting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.unilab.uniting.R;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

public class DialogOpenMeetingFragment extends DialogFragment implements View.OnClickListener {

    //상수
    public static final String TAG_LIKE_DIALOG = "dialog_like";
    public static final String LIKE_CHECK = "like_check";
    final static String TAG = "DialogTAG";

    //변수 및 뷰 세팅
    private String mPartnerUid;
    private ImageView mPhotoImageView;
    private CardView mLikeCardView;
    private ImageView mHeartImageView;
    private TextView mLikeTextView;
    private EditText mMessageEditText;
    private RelativeLayout loaderLayout;
    private TextView mGuideTextView;

    private User mPartnerUser;
    private String mMessage;
    private View view;


    public DialogOpenMeetingFragment() {
    }

    public static DialogOpenMeetingFragment getInstance() {
        DialogOpenMeetingFragment dialog = new DialogOpenMeetingFragment();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.dialog_like, container, false);

        init();
        updateUI();
        setMaxLine();

        //좋아요 버튼
        mLikeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeTextView.setText("다이아 "+ Numbers.OPEN_MEETING_COST+"개");
                mLikeCardView.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if (getDialog() != null && getDialog().isShowing()) {
                            mMessage = mMessageEditText.getText().toString().trim();
                            OpenMeetingListener activity = (OpenMeetingListener) getActivity();
                            activity.openMeeting(mMessage); //액티비티로 전송
                            dismiss();
                        }
                    }
                });
            }
        });
        return view;

    }

    private void init(){
        //바인딩
        mPhotoImageView = view.findViewById(R.id.dialog_like_iv_photo);
        mLikeCardView = view.findViewById(R.id.dialog_like_cv_like);
        mHeartImageView = view.findViewById(R.id.dialog_like_iv_heart);
        mLikeTextView = view.findViewById(R.id.dialog_like_tv_like);
        mMessageEditText = view.findViewById(R.id.dialog_like_et_message);
        mGuideTextView = view.findViewById(R.id.dialog_like_tv_guide);

        mHeartImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_phone_2));

        //로딩중
        loaderLayout = view.findViewById(R.id.loaderLayout);
        loaderLayout.setClickable(true);

        //액티비티에서 상대방 uid 받아오는 번들
        final Bundle bundle = getArguments();
        mPartnerUid = bundle.getString(Strings.partnerUid);
        mPartnerUser = (User) bundle.getSerializable(Strings.partnerUser);
    }

    private void updateUI(){
        //사진 url 리스트 받아온 후 글라이드를 통해 사진 세팅
        String imageUrl0 =  mPartnerUser.getPhotoUrl().get(0);
        Glide.with(getActivity()).load(imageUrl0).into(mPhotoImageView);
        mGuideTextView.setText("· 다이아 25개가 사용됩니다. \n· 상대방이 수락할 경우 서로의 '전화번호'가 공개됩니다. \n· 상대방이 거절할 경우 사용한 다이아(25개)가 환급됩니다.\n· 개인 연락처 기재시 경고 없이 서비스 이용이 정지될 수 있습니다. ");
        mLikeTextView.setText("연락처 교환 신청");


    }

    //액티비티로 데이터 전송
    public interface OpenMeetingListener {
        void openMeeting(String message);
    }


    private void setMaxLine(){
        mMessageEditText.addTextChangedListener(new TextWatcher() {
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
                if (mMessageEditText.getLineCount() >= 4) {
                    mMessageEditText.setText(previousString);
                    mMessageEditText.setSelection(mMessageEditText.length());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (getDialog() != null && getDialog().isShowing()) {
            dismiss();
        }
    }

}



