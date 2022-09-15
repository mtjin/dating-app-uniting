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

public class DialogOk2Fragment extends DialogFragment implements View.OnClickListener{

    //변수 및 상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";

    //xml
    private TextView mTitleTextView;
    private TextView mOkTextView;

    public DialogOk2Fragment() {
    }

    public static DialogOk2Fragment getInstance(){
        DialogOk2Fragment dialog = new DialogOk2Fragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_ok2, container,false);

        final Bundle bundle = getArguments();
        String content = bundle.getString(Strings.EXTRA_CONTENT);

        mTitleTextView = view.findViewById(R.id.dialog_ok_tv_title);
        mOkTextView = view.findViewById(R.id.dialog_ok_tv_ok);

        mTitleTextView.setText(content);
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
