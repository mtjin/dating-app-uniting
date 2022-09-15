package com.unilab.uniting.fragments.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.unilab.uniting.R;
import com.unilab.uniting.activities.MainActivity;
import com.unilab.uniting.activities.launch.login.LaunchLoginActivity;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

public class DialogOkFragment extends DialogFragment implements View.OnClickListener{
    //상수
    public final static  String TAG_MEETING_DIALOG2 = "TAG_MEETING_DIALOG2";

    //xml
    private TextView mOkTextView;
    private TextView mTitleTextView;
    private TextView mContentTextView;

    public DialogOkFragment() {
    }

    public static DialogOkFragment getInstance(){
        DialogOkFragment dialog = new DialogOkFragment();
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
        String from = bundle.getString(Strings.EXTRA_LOCATION);


        switch (from){
            case Strings.isApplied:
                mTitleTextView.setText("이미 신청했습니다. 기다려주세요.");
                break;
            case Strings.applyDone:
                mTitleTextView.setText("프로필 보기를 신청하였습니다.");
                break;
            case Strings.gender:
                mTitleTextView.setText("같은 성별의 미팅·번개는 신청할 수 없습니다. ");
                break;
            case Strings.block:
            case Strings.connected:
                mTitleTextView.setText("이미 연결된 적 있는 회원 혹은 차단 회원입니다.");
                break;
            case Strings.isDenied:
                mTitleTextView.setText("신청이 거절되었습니다.");
                break;
            case Strings.withdraw1:
                mTitleTextView.setText("휴면 설정이 완료되었습니다.");
                break;
            case Strings.withdraw2:
                mTitleTextView.setText("탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.");
                mContentTextView.setVisibility(View.GONE);
                break;
            case Strings.delete:
            case Strings.deleteMeeting:
            case Strings.deletePost:
            case Strings.deleteComment:
                mTitleTextView.setText("삭제되었습니다.");
                break;
            case Strings.openChatting:
                mTitleTextView.setText("채팅이 연결되었습니다.");
                break;
            case Strings.isChattingDeleted:
                mTitleTextView.setText("상대방이 채팅 연결을 해제하였습니다.");
                break;
            case Strings.facebookError:
                mTitleTextView.setText("계정 연동에 실패했습니다.");
                mContentTextView.setText("고객센터로 문의해주세요.");
                mContentTextView.setVisibility(View.VISIBLE);
                break;
        }

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                switch (from){
                    case Strings.withdraw1:
                    case Strings.withdraw2:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getActivity(), LaunchLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;
                    case Strings.delete:
                        Intent intent2 = new Intent(getActivity(), MainActivity.class);
                        intent2.putExtra(Strings.defaultPage, 0);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        break;
                    case Strings.deleteMeeting:
                        Intent intent4 = new Intent(getActivity(), MainActivity.class);
                        intent4.putExtra(Strings.defaultPage, 1);
                        intent4.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent4);
                        break;
                    case Strings.deletePost:
                        Intent intent3 = new Intent(getActivity(), MainActivity.class);
                        intent3.putExtra(Strings.defaultPage, 3);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        break;
                    case Strings.facebookError:
                        LoginManager.getInstance().logOut();
                        break;
                }
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
