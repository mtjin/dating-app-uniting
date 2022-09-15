package com.unilab.uniting.fragments.setprofile;

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

public class DialogWithdrawOkNoFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";
    final static String EXTRA_LOCATION = "EXTRA_LOCATION";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private TextView mContentTextView;

    //value
    private String from;

    public DialogWithdrawOkNoFragment() {
    }

    public static DialogWithdrawOkNoFragment getInstance(){
        DialogWithdrawOkNoFragment dialog = new DialogWithdrawOkNoFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_okno_block,container,false);

        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubtitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);
        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mContentTextView = view.findViewById(R.id.profilecard_dialog_tv_content);

        //어느 다이어로그에서 왔는지
        Bundle bundle = getArguments();
        from = bundle.getString("from");

        if(from.equals("withdraw")){
            mContentTextView.setVisibility(View.VISIBLE);
            mTitleTextView.setText("정말 탈퇴하시겠어요?");
            mSubtitleTextView.setText("휴면하시면 회원님의 계정이 노출되지 않도록 비활성화할 수도 있습니다.");
        }else{
            mTitleTextView.setText("휴면하시겠어요?");
            mSubtitleTextView.setText("회원님의 계정이 비공개로 전환되며, 다시 로그인하시면 즉시 휴면이 해제되어 서비스를 이용하실 수 있습니다.");
        }

        mOkTextView.setText("휴면하기");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorMainPink));

        mContentTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                DialogWithdraw2OkNoFragment dialog = DialogWithdraw2OkNoFragment.getInstance();
                dialog.show(getFragmentManager(), DialogWithdraw2OkNoFragment.TAG_EVENT_DIALOG);
                dismiss();

            }
        });

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                DialogDormantListener activity = (DialogDormantListener) getActivity();
                activity.dormant();
                if (getDialog() != null && getDialog().isShowing()) {
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

    public interface DialogDormantListener {
        void dormant();

    }

}
