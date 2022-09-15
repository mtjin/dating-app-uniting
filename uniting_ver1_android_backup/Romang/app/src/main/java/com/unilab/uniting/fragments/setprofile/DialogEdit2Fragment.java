package com.unilab.uniting.fragments.setprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogEditListener;

public class DialogEdit2Fragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mTitleTextView;
    private EditText mContentEditText;
    private Button mSubmitButton;


    private String from = "";
    private String itemWritten = "";

    public DialogEdit2Fragment() {
    }

    public static DialogEdit2Fragment getInstance(){
        DialogEdit2Fragment dialog = new DialogEdit2Fragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_setprofile_edit2,container,false);

        mTitleTextView = view.findViewById(R.id.dialog_setprofile_tv_title);
        mContentEditText = view.findViewById(R.id.dialog_setprofile_et_content);
        mSubmitButton = view.findViewById(R.id.dialog_setprofile_btn_submit);

        final Bundle bundle = getArguments();
        from = bundle.getString("title");
        itemWritten = bundle.getString("item");
        mTitleTextView.setText(from);
        mContentEditText.setText(itemWritten);

        switch (from) {
            case "직업":
                mContentEditText.setText(itemWritten);
                break;
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mContentEditText.getText().toString().trim();
                if (getDialog() != null && getDialog().isShowing()) {
                    DialogEditListener activity = (DialogEditListener) getActivity();
                    activity.updateProfile(from, content);
                    dismiss();
                }
            }
        });


        return view;
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }


}
