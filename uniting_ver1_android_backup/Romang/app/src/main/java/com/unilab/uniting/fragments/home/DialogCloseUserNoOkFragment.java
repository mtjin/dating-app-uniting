package com.unilab.uniting.fragments.home;

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

public class DialogCloseUserNoOkFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private TextView mContentTextView;


    public DialogCloseUserNoOkFragment() {
    }

    public static DialogCloseUserNoOkFragment getInstance(){
        DialogCloseUserNoOkFragment dialog = new DialogCloseUserNoOkFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_no_ok,container,false);

        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mContentTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);
        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);


        //어느 다이어로그에서 왔는지
        Bundle bundle = getArguments();
        String title = bundle.getString(Strings.title);
        String content = bundle.getString(Strings.content);
        String ok = bundle.getString(Strings.ok);
        String no = bundle.getString(Strings.no);
        mTitleTextView.setText(title);

        if(!content.equals("")){
            mContentTextView.setVisibility(View.VISIBLE);
            mContentTextView.setText(content);
        }else{
            mContentTextView.setVisibility(View.GONE);
        }

        mOkTextView.setText(ok);
        mNoTextView.setText(no);

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    DialogCloseUserListener activity = (DialogCloseUserListener) getActivity();
                    activity.introduceCloseUser();
                    dismiss();
                }
            }
        });

        mNoTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
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

    public interface DialogCloseUserListener {
        void introduceCloseUser();

    }

}
