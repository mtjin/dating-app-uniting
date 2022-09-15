package com.unilab.uniting.fragments.meeting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.utils.Numbers;
import com.unilab.uniting.utils.OnSingleClickListener;

public class MeetingDialogStep1ApplyFragment extends DialogFragment implements View.OnClickListener {
    final static String TAG = "MeetingDialogFragmentT";
    //상수
    public static final String TAG_MEETING_DIALOG = "TAG_MEETING_DIALOG";
    public static final String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";
    //xml
    private TextView mOkTextView;
    private TextView mCancelTextView;

    public MeetingDialogStep1ApplyFragment() {
    }

    public static MeetingDialogStep1ApplyFragment getInstance() {
        MeetingDialogStep1ApplyFragment dialog = new MeetingDialogStep1ApplyFragment();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_okno_meeting, container, false);
        mOkTextView = view.findViewById(R.id.meeting_dialog_tv_ok);
        mCancelTextView = view.findViewById(R.id.meeting_dialog_tv_cancel);
        TextView subTitleTextView = view.findViewById(R.id.meeting_dialog_tv_subtitle);

        subTitleTextView.setText("1. 다이아 "+ Numbers.MEETING_STEP1_COST + "개가 사용됩니다. \n 2. 주최자가 거절할 경우 다이아 " + Numbers.MEETING_STEP1_REFUND + "개가 환급됩니다.");
        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    MeetingCardListener activity = (MeetingCardListener) getActivity();
                    activity.applyStep1();
                    dismiss();
                }

            }
        });

        mCancelTextView.setOnClickListener(new View.OnClickListener() {
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

    public interface MeetingCardListener {
        void applyStep1();
    }

}
