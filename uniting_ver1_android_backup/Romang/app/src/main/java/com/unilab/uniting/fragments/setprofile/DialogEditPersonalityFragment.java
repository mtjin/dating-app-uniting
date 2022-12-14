package com.unilab.uniting.fragments.setprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;
import com.unilab.uniting.adapter.setprofile.EditClickListener;
import com.unilab.uniting.adapter.setprofile.SetProfileEditPersonalityAdapter;
import com.unilab.uniting.fragments.dialog.DialogEditListener;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogEditPersonalityFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mTitleTextView;
    private RecyclerView mDialogEditRecyclerView;
    private Button mSubmitButton;

    private SetProfileEditPersonalityAdapter mSetProfileEditPersonalityAdapter;
    private ArrayList<String> itemList = new ArrayList<>();
    private ArrayList<String> checkedItemList = new ArrayList<>();


    public DialogEditPersonalityFragment() {
    }

    public static DialogEditPersonalityFragment getInstance(){
        DialogEditPersonalityFragment dialog = new DialogEditPersonalityFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_setprofile_edit_personality,container,false);

        mTitleTextView = view.findViewById(R.id.dialog_setprofile_tv_title);
        mDialogEditRecyclerView = view.findViewById(R.id.dialog_setprofile_rv_content);
        mSubmitButton = view.findViewById(R.id.dialog_setprofile_btn_submit);

        final Bundle bundle = getArguments();
        checkedItemList = bundle.getStringArrayList("item");


        mTitleTextView.setText("??????");
        itemList = new ArrayList<String>(Arrays.asList("????????????", "?????????", "?????????", "?????????","????????????", "?????????", "?????????", "?????????", "????????????", "????????????", "????????????", "????????????", "????????????", "?????????"));
        setAdapter();

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedItemList.size() == 0){
                    Toast.makeText(getActivity(), "1??? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                }else{
                    //??????????????? ???????????? ?????? ??????????????? ????????? ??????
                    if (getDialog() != null && getDialog().isShowing()) {
                        DialogEditListener activity = (DialogEditListener) getActivity();
                        activity.updatePersonality(checkedItemList);
                        dismiss();
                    }
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

    private void setAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mDialogEditRecyclerView.setLayoutManager(layoutManager);
        mSetProfileEditPersonalityAdapter = new SetProfileEditPersonalityAdapter(getActivity(), itemList, editClickListener, checkedItemList);
        mDialogEditRecyclerView.setAdapter(mSetProfileEditPersonalityAdapter);
    }

    private EditClickListener editClickListener = new EditClickListener() {
        @Override
        public void listenDialogClick(String checked) {
        }

        @Override
        public void listenPersonalityDialogClick(ArrayList<String> checkedList) {
            checkedItemList = checkedList;
        }
    };

}
