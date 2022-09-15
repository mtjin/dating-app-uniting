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

public class DialogPublicInfoFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    final static String TAG = "BLOCK_FACEBOOK_TAG";
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubTitleTextView;



    public DialogPublicInfoFragment() {
    }

    public static DialogPublicInfoFragment getInstance(){
        DialogPublicInfoFragment dialog = new DialogPublicInfoFragment();
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

        mTitleTextView.setText("대학 정보를 공개하시겠습니까?");
        mSubTitleTextView.setText("다이아 30개가 제공됩니다.");

        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkListener activity = (OkListener) getActivity();
                activity.setPublic(true);
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

    public interface OkListener {
        void setPublic(boolean isDone);
    }


}
