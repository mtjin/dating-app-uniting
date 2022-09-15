package com.unilab.uniting.fragments.setprofile;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;
import com.unilab.uniting.adapter.setprofile.EditClickListener;
import com.unilab.uniting.adapter.setprofile.SetProfileEditAdapter;
import com.unilab.uniting.fragments.dialog.DialogEditListener;
import com.unilab.uniting.utils.MyProfile;

import java.util.ArrayList;
import java.util.Arrays;

public class DialogEditFragment extends DialogFragment implements View.OnClickListener{

    //TAG
    public static final String  TAG_EVENT_DIALOG = "dialog_event";

    //xml
    private TextView mTitleTextView;
    private RecyclerView mDialogEditRecyclerView;
    private SetProfileEditAdapter mSetProfileEditAdapter;

    private ArrayList<String> itemList = new ArrayList<>();
    private String from = "";
    private String itemChecked = "";

    public DialogEditFragment() {
    }

    public static DialogEditFragment getInstance(){
        DialogEditFragment dialog = new DialogEditFragment();
        return  dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_setprofile_edit,container,false);


        mTitleTextView = view.findViewById(R.id.dialog_setprofile_tv_title);
        mDialogEditRecyclerView = view.findViewById(R.id.dialog_setprofile_rv_content);

        final Bundle bundle = getArguments();
        from = bundle.getString("title");
        itemChecked = bundle.getString("item");
        mTitleTextView.setText(from);

        switch (from) {
            case "혈액형":
                itemList = new ArrayList<String>(Arrays.asList("A형", "B형", "AB형", "O형"));
                break;
            case "체형":
                if(MyProfile.getUser().getGender().equals("남자")){
                    itemList = new ArrayList<String>(Arrays.asList("보통", "마른", "통통", "슬림 탄탄", "건장", "근육질"));
                }else{
                    itemList = new ArrayList<String>(Arrays.asList("보통", "마른", "통통", "슬림 탄탄", "글래머", "다소 볼륨"));
                }
                break;
            case "음주":
                itemList = new ArrayList<String>(Arrays.asList("전혀 마시지 못함", "어쩔 수 없을 때만", "가끔 마심", "어느 정도 즐김", "술자리를 즐김 "));
                break;
            case "키":
                itemList.add("140cm 이하");
                for(int height = 140; height < 199; height++){
                    itemList.add(height + "cm");
                }
                itemList.add("200cm 이상");
                break;
            case "지역":
                itemList = new ArrayList<String>(Arrays.asList("강남구", "강동구", "강북구","강서구", "관악구","광진구", "구로구", "금천구","노원구","도봉구","동대문구","동작구", "마포구", "서대문구","서초구", "성동구","성북구", "송파구", "양천구", "영등포구", "용산구","은평구", "종로구", "중랑구", "중구",
                        "인천", "경기", "대전", "세종", "부산", "대구", "광주", "울산", "강원", "충북", "충남", "전북", "전남", "경북", "경남"));
                break;
            case "종교":
                itemList = new ArrayList<String>(Arrays.asList("무교", "기독교", "천주교", "불교", "원불교", "기타"));
                break;
            case "흡연":
                itemList = new ArrayList<String>(Arrays.asList("비흡연", "흡연"));
                break;
        }

        setAdapter();


        return view;
    }


    @Override
    public void onClick(View v) {
        dismiss();
    }

    private void setAdapter(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mDialogEditRecyclerView.setLayoutManager(layoutManager);
        mSetProfileEditAdapter = new SetProfileEditAdapter(getActivity(), itemList, editClickListener, itemChecked);
        mDialogEditRecyclerView.setAdapter(mSetProfileEditAdapter);

    }

    private EditClickListener editClickListener = new EditClickListener() {
        @Override
        public void listenDialogClick(String checked) {
            //체크된 아이템 받고, 기존 체크는 지움
            itemChecked = checked;
            mSetProfileEditAdapter.notifyDataSetChanged();
            setAdapter();

            //다이어로그 닫으면서 원래 액티비티로 데이터 전송
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getDialog() != null && getDialog().isShowing()) {
                        DialogEditListener activity = (DialogEditListener) getActivity();
                        activity.updateProfile(from, checked);
                        dismiss();
                    }
                }
            }, 300);
        }

        @Override
        public void listenPersonalityDialogClick(ArrayList<String> checkedList) {

        }
    };

}
