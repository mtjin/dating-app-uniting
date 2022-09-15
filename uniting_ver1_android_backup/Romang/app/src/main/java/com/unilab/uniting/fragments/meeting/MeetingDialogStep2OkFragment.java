package com.unilab.uniting.fragments.meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.utils.OnSingleClickListener;

public class MeetingDialogStep2OkFragment extends DialogFragment implements View.OnClickListener {
    final static String TAG = "MeetingDialogFragmentT";
    //상수
    public static final String TAG_MEETING_DIALOG = "TAG_MEETING_DIALOG";
    public static final String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";


    //xml
    private TextView mOkTextView;
    private TextView mCancelTextView;
    private TextView mTitle;
    private TextView mSubTitle;

    public MeetingDialogStep2OkFragment() {
    }

    public static MeetingDialogStep2OkFragment getInstance() {
        MeetingDialogStep2OkFragment dialog = new MeetingDialogStep2OkFragment();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_okno_meeting, container, false);
        mOkTextView = view.findViewById(R.id.meeting_dialog_tv_ok);
        mCancelTextView = view.findViewById(R.id.meeting_dialog_tv_cancel);
        mTitle = view.findViewById(R.id.meeting_dialog_tv_title);
        mSubTitle = view.findViewById(R.id.meeting_dialog_tv_subtitle);

        //제목,부제목 설정
        mTitle.setText("연락처 교환 신청을 수락하시겠습니까?");
        mSubTitle.setText("연락처 교환은 무료로 가능합니다:) \n 채팅이 아닌 실제 전화번호가 교환됩니다.");

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    ProfileCardMeetingListener activity = (ProfileCardMeetingListener) getActivity();
                    activity.step2Ok();
                    dismiss();
                }
            }
        });



        mCancelTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if (getDialog() != null && getDialog().isShowing()) {
            dismiss();
        }
    }
}
