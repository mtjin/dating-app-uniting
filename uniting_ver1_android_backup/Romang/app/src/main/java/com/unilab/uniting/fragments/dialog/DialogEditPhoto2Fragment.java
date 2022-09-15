package com.unilab.uniting.fragments.dialog;

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

public class DialogEditPhoto2Fragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";
    final static int PICK_IMAGE = 1;

    public DialogEditPhoto2Fragment() {
    }

    public static DialogEditPhoto2Fragment getInstance(){
        DialogEditPhoto2Fragment dialog = new DialogEditPhoto2Fragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_edit_photo_2,container,false);

        TextView gallery = view.findViewById(R.id.dialog_edit_photo_gallery);
        TextView delete = view.findViewById(R.id.dialog_edit_photo_delete);
        TextView cancel = view.findViewById(R.id.dialog_edit_photo_cancel);
        View line2 = view.findViewById(R.id.dialog_edit_photo_line2);
        View line3 = view.findViewById(R.id.dialog_edit_photo_line3);

        //상대방 유저 uid
        final Bundle bundle = getArguments();

        // 갤러리로 이동
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, PICK_IMAGE);
                if(getDialog()!=null && getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        });

        //사진 삭제
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getDialog() != null && getDialog().isShowing()) {
                    //다이어로그 닫으면서 원래 액티비티로 데이터 전송
                    DialogEdit2Listener activity = (DialogEdit2Listener) getActivity();
                    activity.removePhoto();
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

    public interface DialogEdit2Listener {
        void removePhoto();

    }


}


