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
import com.unilab.uniting.model.User;
import com.unilab.uniting.utils.MyProfile;
import com.unilab.uniting.utils.Strings;

public class DialogDeleteFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    final int HOME_PAGE = 0;

    //xml
    private TextView mOkTextView;
    private TextView mNoTextView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;

    //value
    private User mUser = MyProfile.getUser();
    private String mLocation = ""; //위치
    private Meeting mMeeting;
    private CommunityPost mCommunityPost;
    private CommunityComment mCommunityComment;
    private boolean isLoading = false;

    public DialogDeleteFragment() {
    }

    public static DialogDeleteFragment getInstance(){
        DialogDeleteFragment dialog = new DialogDeleteFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_okno_block, container, false);

        mOkTextView = view.findViewById(R.id.profilecard_dialog_tv_ok);
        mNoTextView = view.findViewById(R.id.profilecard_dialog_tv_no);
        mTitleTextView = view.findViewById(R.id.profilecard_dialog_tv_title);
        mSubtitleTextView = view.findViewById(R.id.profilecard_dialog_tv_subtitle);

        mSubtitleTextView.setVisibility(View.GONE);
        mOkTextView.setText("삭제");

        //상대방 유저 uid
        final Bundle bundle = getArguments();
        mLocation = bundle.getString(Strings.EXTRA_LOCATION);
        switch (mLocation) {
            case "미팅":
                mMeeting = (Meeting) bundle.getSerializable(Strings.EXTRA_MEETING);
                mTitleTextView.setText("미팅을 삭제하시겠습니까?");
                break;
            case "게시물":
                mCommunityPost = (CommunityPost) bundle.getSerializable(Strings.EXTRA_COMMUNITY_POST);
                mTitleTextView.setText("게시물을 삭제하시겠습니까?");
                break;
            case "댓글":
                mCommunityComment = (CommunityComment) bundle.getSerializable(Strings.EXTRA_COMMUNITY_COMMENT);
                mTitleTextView.setText("댓글을 삭제하시겠습니까?");
                break;
        }


        mOkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    DeleteListener activity = (DeleteListener) getActivity();
                    switch (mLocation) {
                        case "미팅":
                            activity.deleteMeeting();
                            break;
                        case "게시물":
                            activity.deletePost(mCommunityPost);

                            break;
                        case "댓글":
                            activity.deleteComment(mCommunityComment);
                            break;
                    }
                    dismiss();
                }
            }
        });

        mNoTextView.setOnClickListener(new View.OnClickListener() {
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

    public interface DeleteListener{
        void deleteMeeting();
        void deletePost(CommunityPost post);
        void deleteComment(CommunityComment comment);
    }

}
