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
import com.unilab.uniting.utils.OnSingleClickListener;

public class DialogDormantFragment extends DialogFragment implements View.OnClickListener {

    //TAG
    public static final String TAG_EVENT_DIALOG = "dialog_event";

    //putExtra Key
    final int HOME_PAGE = 0;

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    private boolean isLoading = false;

    public DialogDormantFragment() {
    }

    public static DialogDormantFragment getInstance() {
        DialogDormantFragment dialog = new DialogDormantFragment();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_no_ok, container, false);

        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubtitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);

        //상대방 유저 uid
        mTitleTextView.setText("휴면 상태의 계정입니다.");
        mSubtitleTextView.setText("휴면 해제하시겠습니까?");
        mOkTextView.setText("휴면 해제");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorBlack));



        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                DormantCheckListener activity = (DormantCheckListener) getActivity();
                activity.checkDormant(true); //액티비티로 전송

            }
        });

        mNoTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    DormantCheckListener activity = (DormantCheckListener) getActivity();
                    activity.checkDormant(false); //액티비티로 전송
                    dismiss();
                }
            }
        });

        return view;
    }

    public interface DormantCheckListener {
        void checkDormant(boolean isActivated);
    }


    @Override
    public void onClick(View v) {
        if (getDialog() != null && getDialog().isShowing()) {
            dismiss();
        }

    }

}
