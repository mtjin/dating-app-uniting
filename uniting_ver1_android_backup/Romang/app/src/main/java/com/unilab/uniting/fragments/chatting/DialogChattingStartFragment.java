package com.unilab.uniting.fragments.chatting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.utils.Numbers;

public class DialogChattingStartFragment extends DialogFragment implements View.OnClickListener {

    final static String TAG = "ChattingDialogFragmentT";

    //상수
    public static final String TAG_MEETING_DIALOG = "TAG_MEETING_DIALOG";
    public static final String TAG_MEETING_DIALOG3 = "TAG_MEETING_DIALOG3";

    private boolean isLoading = false;


    //xml
    private TextView mOkTextView;
    private TextView mCancelTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;


    public DialogChattingStartFragment() {
    }

    public static DialogChattingStartFragment getInstance() {
        DialogChattingStartFragment dialog = new DialogChattingStartFragment();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_okno_meeting, container, false);
        mOkTextView = view.findViewById(R.id.meeting_dialog_tv_ok);
        mCancelTextView = view.findViewById(R.id.meeting_dialog_tv_cancel);
        mTitleTextView = view.findViewById(R.id.meeting_dialog_tv_title);
        mSubtitleTextView = view.findViewById(R.id.meeting_dialog_tv_subtitle);


        mTitleTextView.setText("채팅을 시작하시겠습니까?");
        mSubtitleTextView.setText("다이아 " + Numbers.OPEN_CHATTING_COST + "개가 사용되며, 바로 채팅창이 열립니다.");

        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    OpenChatListener activity = (OpenChatListener) getActivity();
                    activity.openChatting();
                    dismiss();
                }
            }
        });

        mCancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
            }
        });
        return view;
    }


    //액티비티로 데이터 전송
    public interface OpenChatListener {
        void openChatting();
    }

    @Override
    public void onClick(View v) {
        if (!isLoading) {
            if (getDialog() != null && getDialog().isShowing()) {
                dismiss();
            }
        }
    }


}
