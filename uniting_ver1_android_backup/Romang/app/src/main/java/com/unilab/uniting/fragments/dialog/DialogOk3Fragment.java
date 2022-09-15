package com.unilab.uniting.fragments.dialog;

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
import com.unilab.uniting.utils.Strings;

public class DialogOk3Fragment extends DialogFragment implements View.OnClickListener{

    //변수 및 상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";

    //xml
    private TextView mTitleTextView;
    private TextView mOkTextView;
    private TextView mContentTextView;

    public DialogOk3Fragment() {
    }

    public static DialogOk3Fragment getInstance(){
        DialogOk3Fragment dialog = new DialogOk3Fragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_ok, container,false);

        final Bundle bundle = getArguments();
        String title = bundle.getString(Strings.title);
        String content = bundle.getString(Strings.content);

        mTitleTextView = view.findViewById(R.id.dialog_ok_tv_title);
        mContentTextView = view.findViewById(R.id.dialog_ok_tv_content);
        mOkTextView = view.findViewById(R.id.dialog_ok_tv_ok);

        mTitleTextView.setText(title);
        mContentTextView.setText(content);

        if(content != null && !content.equals("")){
            mContentTextView.setVisibility(View.VISIBLE);
        }
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
