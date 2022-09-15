package com.unilab.uniting.fragments.setprofile;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.facebook.login.LoginManager;
import com.unilab.uniting.R;

import java.util.Arrays;

public class DialogLinkFacebookFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    final static String TAG = "BLOCK_FACEBOOK_TAG";
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;



    public DialogLinkFacebookFragment() {
    }

    public static DialogLinkFacebookFragment getInstance(){
        DialogLinkFacebookFragment dialog = new DialogLinkFacebookFragment();
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

        mTitleTextView.setText("페이스북을 연동하시겠습니까?");
        mSubTitleTextView.setText("페이스북에는 어떤 기록도 남지 않습니다.");
        mOkTextView.setText("확인");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorBlack));


        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email","user_friends")); // 접근해야할 정보 permission
                dismiss();
            }
        });

        mNoTextView.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

    }

}
