package com.unilab.uniting.fragments.setprofile;

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

import com.facebook.login.LoginManager;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.launch.Splash;
import com.unilab.uniting.utils.FirebaseHelper;

public class DialogLogoutOkNoFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;


    public DialogLogoutOkNoFragment() {
    }

    public static DialogLogoutOkNoFragment getInstance(){
        DialogLogoutOkNoFragment dialog = new DialogLogoutOkNoFragment();
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

        mTitleTextView.setText("로그아웃 하시겠습니까?");
        mSubTitleTextView.setVisibility(View.GONE);
        mSubTitleTextView.setHeight(0);
        mOkTextView.setText("확인");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorBlack));


        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper.auth.signOut();
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(getActivity(), Splash.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
