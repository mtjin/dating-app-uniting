package com.unilab.uniting.fragments.launch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.signup.LaunchSignup_17Screening;

public class DialogLaterFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;


    public DialogLaterFragment() {
    }

    public static DialogLaterFragment getInstance(){
        DialogLaterFragment dialog = new DialogLaterFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_okno_block,container,false);

        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);

        mTitleTextView.setText("나중에 인증하시겠습니까?");
        mSubTitleTextView.setText("대학 미인증시 추후 일부 서비스 이용이 제한될 수 있어요! 현재 운영진의 심사가 진행중이니 그동안 대학을 인증해 보시는건 어떠세요?");
        mOkTextView.setText("나중에 인증하기");
        mNoTextView.setText("지금 인증하기");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorBlack));


        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LaunchSignup_17Screening.class );
                startActivity(intent);
                if (getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
            }
        });

        mNoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
