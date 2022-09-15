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
import com.unilab.uniting.utils.Strings;

public class DialogEditPhoto3Fragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    final static String TAG = "EDIT_PHOTO_DIALOG";

    public DialogEditPhoto3Fragment() {
    }

    public static DialogEditPhoto3Fragment getInstance(){
        DialogEditPhoto3Fragment dialog = new DialogEditPhoto3Fragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_edit_photo_3,container,false);

        TextView takePhoto = view.findViewById(R.id.dialog_edit_photo_takePhoto);
        TextView gallery = view.findViewById(R.id.dialog_edit_photo_gallery);
        TextView delete = view.findViewById(R.id.dialog_edit_photo_delete);
        TextView cancel = view.findViewById(R.id.dialog_edit_photo_cancel);
        View line1 = view.findViewById(R.id.dialog_edit_photo_line1);
        View line2 = view.findViewById(R.id.dialog_edit_photo_line2);
        View line3 = view.findViewById(R.id.dialog_edit_photo_line3);

        //상대방 유저 uid
        final Bundle bundle = getArguments();

        //카메라로 사진 찍기

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    //다이어로그 닫으면서 원래 액티비티로 데이터 전송
                    DialogEdit3Listener activity = (DialogEdit3Listener) getActivity();
                    activity.selectMenu(Strings.TAKE_PHOTO);
                    dismiss();
                }
            }
        });

        // 갤러리로 이동
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    //다이어로그 닫으면서 원래 액티비티로 데이터 전송
                    DialogEdit3Listener activity = (DialogEdit3Listener) getActivity();
                    activity.selectMenu(Strings.GALLERY);
                    dismiss();
                }


            }
        });

        //사진 삭제
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    //다이어로그 닫으면서 원래 액티비티로 데이터 전송
                    DialogEdit3Listener activity = (DialogEdit3Listener) getActivity();
                    activity.selectMenu(Strings.REMOVE_PHOTO);
                    dismiss();
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

    @Override
    public void onClick(View v) {
        if(getDialog()!=null && getDialog().isShowing()) {
            getDialog().dismiss();
        }
    }

    public interface DialogEdit3Listener {
        void selectMenu(String menu);

    }



}


