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

import com.unilab.uniting.R;

public class DialogBlockFacebookFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    final static String TAG = "BLOCK_FACEBOOK_TAG";
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;



    public DialogBlockFacebookFragment() {
    }

    public static DialogBlockFacebookFragment getInstance(){
        DialogBlockFacebookFragment dialog = new DialogBlockFacebookFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_no_ok,container,false);

        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);

        mTitleTextView.setText("페이스북 친구 목록 권한 요청");
        mSubTitleTextView.setText("페이스북 친구를 차단하기 위해서는 '유니팅'을 사용하고 있는 페이스북 친구 목록을 받아와야합니다. 동의하십니까?");
        mOkTextView.setText("동의");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorBlack));


        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelListener activity = (CancelListener) getActivity();
                activity.fbBlockOk();
                dismiss();
//                LoginButton loginButton = new LoginButton(getActivity());
//                loginButton.setReadPermissions("email", "public_profile");
//                loginButton.performClick();
                dismiss();
            }
        });

        mNoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelListener activity = (CancelListener) getActivity();
                activity.fbBlockCancelled();
                dismiss();
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {
        CancelListener activity = (CancelListener) getActivity();
        activity.fbBlockCancelled();
        dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        CancelListener activity = (CancelListener) getActivity();
        activity.fbBlockCancelled();
    }

    public interface CancelListener {
        void fbBlockOk();
        void fbBlockCancelled();
    }


}
