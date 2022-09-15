package com.unilab.uniting.fragments.launch;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.unilab.uniting.utils.OnSingleClickListener;

public class DialogVersionFragment extends DialogFragment implements View.OnClickListener{
    //상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";

    //xml
    private TextView mOkTextView;
    private TextView mTitleTextView;
    private TextView mContentTextView;

    //put Extra key
    final static String isMeetingAppliedExtra = "EXTRA_IS_MEETING_APPLIED";
    final static String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public DialogVersionFragment() {
    }

    public static DialogVersionFragment getInstance(){
        DialogVersionFragment dialog = new DialogVersionFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_ok, container,false);

        mOkTextView = view.findViewById(R.id.dialog_ok_tv_ok);
        mTitleTextView = view.findViewById(R.id.dialog_ok_tv_title);
        mContentTextView = view.findViewById(R.id.dialog_ok_tv_content);

        mTitleTextView.setText("최신 버전 업데이트가 필요합니다.");

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                openPlayStore();
            }
        });

        return view;
    }


    @Override
    public void onClick(View v) {
        openPlayStore();

    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        openPlayStore();
    }

    private void openPlayStore(){
        final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
        if (getDialog() != null && getDialog().isShowing()) {
            dismiss();
        }
    }
}
