package com.unilab.uniting.fragments.setprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.utils.OnSingleClickListener;

public class DialogWithdraw2OkNoFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";
    final static String EXTRA_LOCATION = "EXTRA_LOCATION";

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private EditText mEditText;

    //value
    private String from;

    public DialogWithdraw2OkNoFragment() {
    }

    public static DialogWithdraw2OkNoFragment getInstance(){
        DialogWithdraw2OkNoFragment dialog = new DialogWithdraw2OkNoFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_okno_withdraw,container,false);

        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubtitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);
        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mEditText = view.findViewById(R.id.dialog_withdraw_ev_agree);


        mTitleTextView.setText("탈퇴하시겠어요?");
        mSubtitleTextView.setText(" 탈퇴를 진행하실 경우 계정의 모든 데이터와 구매하신 다이아 내역이 삭제되어 복구할 수 없습니다. 또한, 탈퇴 후 3일간 재가입이 불가능합니다. \n 계속 탈퇴 진행을 원하실 경우 아래의 빈칸에 \"동의합니다\"라고 적은 후 탈퇴 버튼을 눌러주세요.");
        mOkTextView.setText("탈퇴하기");
        mOkTextView.setTextColor(getResources().getColor(R.color.colorMainPink));


        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                String agreement = mEditText.getText().toString().trim();
                if(!agreement.equals("동의합니다")){
                    Toast.makeText(getActivity(), "문구를 바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DialogWithdrawListener activity = (DialogWithdrawListener) getActivity();
                activity.withdraw();
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

    public interface DialogWithdrawListener{
        void withdraw();
    }
}
