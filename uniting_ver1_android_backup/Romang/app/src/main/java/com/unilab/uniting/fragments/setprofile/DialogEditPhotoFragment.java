package com.unilab.uniting.fragments.setprofile;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unilab.uniting.R;
import com.unilab.uniting.fragments.dialog.DialogEditListener;

import java.util.ArrayList;

public class DialogEditPhotoFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";
    final static int PICK_IMAGE = 1;
    final static String TAG = "EDIT_PHOTO_DIALOG";
    final static String SCREENING = "1"; //심사중
    final static String PASS = "2"; //사진 심사 합격
    final static String FAIL = "3"; //불합

    //value
    private int selectNum = 0;
    private ArrayList<String> mFinalPhotoUrlList;

    public DialogEditPhotoFragment() {
    }

    public static DialogEditPhotoFragment getInstance(){
        DialogEditPhotoFragment dialog = new DialogEditPhotoFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_edit_photo,container,false);

        TextView represent = view.findViewById(R.id.dialog_edit_photo_represent);
        TextView gallery = view.findViewById(R.id.dialog_edit_photo_gallery);
        TextView delete = view.findViewById(R.id.dialog_edit_photo_delete);
        TextView cancel = view.findViewById(R.id.dialog_edit_photo_cancel);
        View line1 = view.findViewById(R.id.dialog_edit_photo_line1);
        View line2 = view.findViewById(R.id.dialog_edit_photo_line2);
        View line3 = view.findViewById(R.id.dialog_edit_photo_line3);

        //상대방 유저 uid
        final Bundle bundle = getArguments();
        selectNum = bundle.getInt("selectNum");
        mFinalPhotoUrlList = bundle.getStringArrayList("mFinalPhotoUrlList");

        if(selectNum < 2){
            delete.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
        }
        if(selectNum == 0 || selectNum >=  mFinalPhotoUrlList.size()){
            represent.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
        }


        // 대표사진 설정
        represent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이어로그 닫으면서 원래 액티비티로 데이터 전송
                DialogEditListener activity = (DialogEditListener) getActivity();
                activity.changeRepresentPhoto();
                if (getDialog() != null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        });

        // 갤러리로 이동
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, PICK_IMAGE);
                if (getDialog() != null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        });

        //사진 삭제
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //다이어로그 닫으면서 원래 액티비티로 데이터 전송
                DialogEditListener activity = (DialogEditListener) getActivity();
                activity.removePhoto();
                if (getDialog() != null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }

            }
        });

        //취소 클릭시
        cancel.setOnClickListener(new View.OnClickListener() {
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

}
