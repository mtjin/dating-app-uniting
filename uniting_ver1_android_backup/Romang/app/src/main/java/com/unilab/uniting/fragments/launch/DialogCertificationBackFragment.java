package com.unilab.uniting.fragments.launch;

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
import com.unilab.uniting.utils.Strings;

public class DialogCertificationBackFragment extends DialogFragment implements View.OnClickListener{

    //변수 및 상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";

    //xml
    private TextView mTitleTextView;
    private TextView mOkTextView;

    String from;

    public DialogCertificationBackFragment() {
    }

    public static DialogCertificationBackFragment getInstance(){
        DialogCertificationBackFragment dialog = new DialogCertificationBackFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_ok2, container,false);

        final Bundle bundle = getArguments();
        String content = bundle.getString(Strings.EXTRA_CONTENT);
        from = bundle.getString(Strings.from);

        mTitleTextView = view.findViewById(R.id.dialog_ok_tv_title);
        mOkTextView = view.findViewById(R.id.dialog_ok_tv_ok);

        mTitleTextView.setText(content);

        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CertificationListener activity = (CertificationListener) getActivity();
                activity.stopSignUp(from); //액티비티로 전송
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        CertificationListener activity = (CertificationListener) getActivity();
        activity.stopSignUp(from); //액티비티로 전송
        dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        CertificationListener activity = (CertificationListener) getActivity();
        activity.stopSignUp(from); //액티비티로 전송
        dismiss();
    }

    public interface CertificationListener {
        void stopSignUp(String from);
    }

}
