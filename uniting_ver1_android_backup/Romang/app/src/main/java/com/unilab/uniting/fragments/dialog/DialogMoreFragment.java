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

import com.unilab.uniting.R;
import com.unilab.uniting.activities.report.ReportActivity;
import com.unilab.uniting.model.ChatRoom;
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Meeting;
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

public class DialogMoreFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //putExtra Key
    final int HOME_PAGE = 0;

    //value
    private String mLoaction;
    private String mPartnerUid;
    private User mPartnerUser; //신고할 유저
    private CommunityPost mCommunityPost; //신고할 커뮤니티 포스트
    private CommunityComment mCommunityComment; //신고할 커뮤니티 댓글
    private Meeting mMeeting; //신고할 미팅
    private ChatRoom mChatRoom; //신고할 채팅방

    public DialogMoreFragment() {
    }

    public static DialogMoreFragment getInstance(){
        DialogMoreFragment dialog = new DialogMoreFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_custom,container,false);
        TextView report = view.findViewById(R.id.item_more_report);
        TextView block = view.findViewById(R.id.item_more_block);
        TextView cancel = view.findViewById(R.id.item_more_cancel);
        View line = view.findViewById(R.id.dialog_more_view);

        //상대방 유저 uid
        final Bundle bundle = getArguments();
        mLoaction = bundle.getString(Strings.EXTRA_LOCATION);
        switch (mLoaction){
            case "프로필카드":
            case "프로필평가":
                mPartnerUid = bundle.getString(Strings.partnerUid);
                mPartnerUser = (User) bundle.getSerializable(Strings.partnerUser);
                break;
            case "게시물":
                mCommunityPost = (CommunityPost) bundle.getSerializable(Strings.EXTRA_COMMUNITY_POST);
                block.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                break;
            case "댓글":
                mCommunityComment = (CommunityComment) bundle.getSerializable(Strings.EXTRA_COMMUNITY_COMMENT);
                block.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                break;
            case "미팅":
                mMeeting = (Meeting) bundle.getSerializable(Strings.EXTRA_MEETING);
                block.setVisibility(View.GONE);
                line.setVisibility(View.GONE);
                if(mMeeting.getHostUid().equals(MyProfile.getUser().getUid())){
                    report.setText("미팅 삭제");
                }
                break;
            case "채팅" :
                block.setText("연결 해제");
                mChatRoom = (ChatRoom) bundle.getSerializable(Strings.EXTRA_CHATTING);
                //파트너 uid
                if (mChatRoom.getUserUidList().get(0).equals(FirebaseHelper.mUid)) {
                    mPartnerUid = mChatRoom.getUserUidList().get(1);
                } else {
                    mPartnerUid = mChatRoom.getUserUidList().get(0);
                }
                //파트너 유저 객체
                if(mChatRoom.getUser0().getUid().equals(mPartnerUid)){
                    mPartnerUser = mChatRoom.getUser0();
                }else{
                    mPartnerUser = mChatRoom.getUser1();
                }
                break;

        }

        // 신고 클릭시 신고 화면으로 넘어감.
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ReportActivity.class);
                intent.putExtra(Strings.EXTRA_LOCATION, mLoaction);
                switch (mLoaction){
                    case "댓글":
                        intent.putExtra(Strings.EXTRA_COMMUNITY_COMMENT,mCommunityComment);
                        break;
                    case "게시물":
                        intent.putExtra(Strings.EXTRA_COMMUNITY_POST, mCommunityPost);
                        break;
                    case "프로필카드":
                    case "프로필평가":
                        intent.putExtra(Strings.EXTRA_USER, mPartnerUser);
                        break;
                    case "미팅":
                        intent.putExtra(Strings.EXTRA_MEETING, mMeeting);
                        break;
                    case "채팅":
                        intent.putExtra(Strings.EXTRA_CHATTING, mChatRoom);
                }
                startActivity(intent);
                if(getDialog()!=null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        });

        //차단 클릭시 다시 한 번 확인하는 다이얼로그로 변경.
        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBlockDialog();

            }
        });

        //취소 클릭시
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getDialog()!=null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

    private void showBlockDialog(){
        DialogBlockFragment dialog = DialogBlockFragment.getInstance();
        Bundle bundle = new Bundle();
        bundle.putString(Strings.partnerUid, mPartnerUid);
        bundle.putSerializable(Strings.partnerUser, mPartnerUser);
        if(mLoaction.equals("채팅")){
            bundle.putString(Strings.EXTRA_LOCATION, "채팅");
            bundle.putSerializable(Strings.EXTRA_CHATTING, mChatRoom);
        }
        if(mLoaction.equals("프로필평가")){
            bundle.putString(Strings.EXTRA_LOCATION, "프로필평가");
        }

        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), DialogBlockFragment.TAG_EVENT_DIALOG);

        if(getDialog()!=null && getDialog().isShowing()) {
            getDialog().dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        if(getDialog()!=null && getDialog().isShowing()) {
            getDialog().dismiss();
        }

    }
}
