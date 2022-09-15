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

public class MeetingDialogStep1NoFragment extends DialogFragment implements View.OnClickListener {
    final static String TAG = "MeetingDialogFragmentT";

    //put Extra
    public static final String TAG_MEETING_DIALOG = "TAG_MEETING_DIALOG";

    //xml
    private TextView mOkTextView;
    private TextView mCancelTextView;
    private TextView mTitle;
    private TextView mSubTitle;


    public MeetingDialogStep1NoFragment() {
    }

    public static MeetingDialogStep1NoFragment getInstance() {
        MeetingDialogStep1NoFragment dialog = new MeetingDialogStep1NoFragment();
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
        mTitle.setText("프로필 열람 신청을 거절하시겠습니까??");
        mSubTitle.setText("거절하시면 상대방이 회원님의 프로필을 볼 수 없습니다.");

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    ProfileCardMeetingListener activity = (ProfileCardMeetingListener) getActivity();
                    activity.step1No();
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
