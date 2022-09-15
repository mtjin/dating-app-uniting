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
import com.unilab.uniting.model.CommunityComment;
import com.unilab.uniting.model.CommunityPost;
import com.unilab.uniting.model.Meeting;

import java.util.Objects;

public class DialogMore2Fragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //putExtra Key
    final String EXTRA_USER = "EXTRA_USER";
    final String EXTRA_LOCATION = "EXTRA_LOCATION";
    final String EXTRA_COMMUNITY_POST = "EXTRA_COMMUNITY_POST";
    final String EXTRA_COMMUNITY_COMMENT = "EXTRA_COMMUNITY_COMMENT";
    final String EXTRA_MEETING = "EXTRA_MEETING";

    //value
    private String mLocation;
    private CommunityPost mCommunityPost; //커뮤니티 포스트
    private CommunityComment mCommunityComment; //커뮤니티 댓글
    private Meeting mMeeting; // 미팅


    public DialogMore2Fragment() {
    }

    public static DialogMore2Fragment getInstance(){
        DialogMore2Fragment dialog = new DialogMore2Fragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_custom,container,false);
        TextView report = view.findViewById(R.id.item_more_report);
        TextView delete = view.findViewById(R.id.item_more_block);
        TextView cancel = view.findViewById(R.id.item_more_cancel);
        View line = view.findViewById(R.id.dialog_more_view);

        report.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        delete.setText("삭제하기");


        //상대방 유저 uid
        Bundle bundle = getArguments();
        mLocation = bundle.getString(EXTRA_LOCATION);

        switch (mLocation){
            case "게시물":
                mCommunityPost = (CommunityPost) bundle.getSerializable(EXTRA_COMMUNITY_POST);
                break;
            case "댓글":
                mCommunityComment = (CommunityComment) bundle.getSerializable(EXTRA_COMMUNITY_COMMENT);
                break;
            case "미팅":
                mMeeting = (Meeting) bundle.getSerializable(EXTRA_MEETING);
                break;
        }

        //삭제
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mLocation){
                    case "게시물":
                        DialogDeleteFragment dialog = DialogDeleteFragment.getInstance();
                        Bundle bundle = new Bundle();
                        bundle.putString(EXTRA_LOCATION, mLocation);
                        bundle.putSerializable(EXTRA_COMMUNITY_POST, mCommunityPost);
                        dialog.setArguments(bundle);
                        dialog.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), DialogDeleteFragment.TAG_EVENT_DIALOG);
                        dismiss();
                        break;
                    case "댓글":
                        DialogDeleteFragment dialog2 = DialogDeleteFragment.getInstance();
                        Bundle bundle2 = new Bundle();
                        bundle2.putString(EXTRA_LOCATION, mLocation);
                        bundle2.putSerializable(EXTRA_COMMUNITY_COMMENT, mCommunityComment);
                        dialog2.setArguments(bundle2);
                        dialog2.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), DialogDeleteFragment.TAG_EVENT_DIALOG);
                        dismiss();
                        break;
                    case "미팅":
                        DialogDeleteFragment dialog3 = DialogDeleteFragment.getInstance();
                        Bundle bundle3 = new Bundle();
                        bundle3.putString(EXTRA_LOCATION, mLocation);
                        bundle3.putSerializable(EXTRA_MEETING, mMeeting);
                        dialog3.setArguments(bundle3);
                        dialog3.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), DialogDeleteFragment.TAG_EVENT_DIALOG);
                        dismiss();
                        break;
                }


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

    private void showDeleteDialog(){

    }


    @Override
    public void onClick(View v) {
        if(getDialog()!=null && getDialog().isShowing()) {
            getDialog().dismiss();
        }

    }
}
