package com.unilab.uniting.fragments.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;

public class DialogBoardOkFragment extends DialogFragment implements View.OnClickListener{

    //변수 및 상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";
    private boolean isLoading = false;

    //xml
    private TextView mTitleTextView;
    private TextView mOkTextView;

    public DialogBoardOkFragment() {
    }

    public static DialogBoardOkFragment getInstance(){
        DialogBoardOkFragment dialog = new DialogBoardOkFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_ok2, container,false);

        mTitleTextView = view.findViewById(R.id.dialog_ok_tv_title);
        mOkTextView = view.findViewById(R.id.dialog_ok_tv_ok);
        mTitleTextView.setText("대학교를 인증하신 분만 학교별 게시판을 사용하실 수 있습니다.");

        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
