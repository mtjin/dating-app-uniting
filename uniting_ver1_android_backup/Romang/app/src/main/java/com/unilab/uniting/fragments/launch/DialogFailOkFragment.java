package com.unilab.uniting.fragments.launch;

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
import com.unilab.uniting.utils.Strings;

public class DialogFailOkFragment extends DialogFragment implements View.OnClickListener{
    //상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";

    //xml
    private TextView mOkTextView;
    private TextView mTitleTextView;
    private TextView mContentTextView;


    public DialogFailOkFragment() {
    }

    public static DialogFailOkFragment getInstance(){
        DialogFailOkFragment dialog = new DialogFailOkFragment();
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

        final Bundle bundle = getArguments();
        String message = bundle.getString(Strings.EXTRA_MESSAGE);

        mTitleTextView.setText("심사 결과, 프로필 보완이 필요합니다.");
        mContentTextView.setText(message);

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
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
