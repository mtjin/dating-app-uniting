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
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.OnSingleClickListener;
import com.unilab.uniting.utils.Strings;

public class DialogBlockFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //putExtra Key
    final int HOME_PAGE = 0;

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    //value
    private String partnerUid;
    private User mUser = MyProfile.getUser();
    private User mPartnerUser; //차단할 유저
    private String mLocation = ""; //위치
    private ChatRoom mChatRoom;
    private boolean isLoading = false;

    public DialogBlockFragment() {
    }

    public static DialogBlockFragment getInstance(){
        DialogBlockFragment dialog = new DialogBlockFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_okno_block,container,false);

        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubtitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);

        //상대방 유저 uid
        final Bundle bundle = getArguments();
        partnerUid = bundle.getString(Strings.partnerUid);
        mPartnerUser = (User) bundle.getSerializable(Strings.partnerUser);

        mLocation = bundle.getString(Strings.EXTRA_LOCATION); //채팅, 프롶리 평가에서만 넘어옴.

        if(mLocation != null && mLocation.equals("채팅")){
            mChatRoom = (ChatRoom) bundle.getSerializable("EXTRA_CHATTING");
            mTitleTextView.setText("연결 해제 하시겠습니까?");
            mSubtitleTextView.setText("더 이상 서로를 볼 수 없습니다.");
            mOkTextView.setText("연결 해제");
        }

        mOkTextView.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    BlockListener activity = (BlockListener) getActivity();
                    activity.block();
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


    public interface BlockListener{
        void block();
    }

}
