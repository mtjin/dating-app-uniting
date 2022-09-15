package com.unilab.uniting.activities.setprofile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.unilab.uniting.R;
import com.unilab.uniting.utils.BasicActivity;
import com.unilab.uniting.utils.DateUtil;
import com.unilab.uniting.utils.FirebaseHelper;
import com.unilab.uniting.utils.MyProfile;

import java.util.HashMap;
import java.util.Map;

public class SetProfile_8Suggestion extends BasicActivity {
    //xml
    private LinearLayout mBack;
    private Button mSubmit;
    private RelativeLayout mLoaderLayout;
    private EditText mContentEditText;
    private TextView mToolbarTextView;

    //value
    private String mContent = "";
    private View.OnClickListener onClickListener;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile_8_suggestion);


        initView();
        setOnClickListener();
    }

    private void  initView(){

        View view = findViewById(R.id.toolbar_write_id);
        mBack = view.findViewById(R.id.toolbar_back);
        mSubmit = view.findViewById(R.id.toolbar_submit_btn);
        mContentEditText = findViewById(R.id.setprofile_suggestion8_et_content);
        mToolbarTextView = findViewById(R.id.toolbar_title);
        mToolbarTextView.setText("제안");

        //로딩중
        mLoaderLayout = findViewById(R.id.loaderLayout);
        mLoaderLayout.setClickable(true);
    }

    private void setOnClickListener(){

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.toolbar_back:
                        finish();
                        break;
                    case R.id.toolbar_submit_btn:
                        mContent = mContentEditText.getText().toString();
                        if(mContent.length() < 6){
                            Toast.makeText(SetProfile_8Suggestion.this, "내용이 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                        }else{
                            Map<String , Object> map = new HashMap<>();
                            map.put("content" , mContent);
                            map.put("done" , false);
                            map.put("uid", FirebaseHelper.mUid);
                            map.put("date", DateUtil.getDateSec());
                            map.put("email", MyProfile.getUser().getEmail());
                            map.put("nickname", MyProfile.getUser().getNickname());
                            if(!isLoading){
                                isLoading = true;
                                mLoaderLayout.setVisibility(View.VISIBLE);
                                FirebaseHelper.db.collection("Suggestion").document(DateUtil.getTimeStampUnix()).set(map)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SetProfile_8Suggestion.this, "의견 감사합니다:)", Toast.LENGTH_SHORT).show();
                                                mLoaderLayout.setVisibility(View.GONE);
                                                isLoading = false;
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SetProfile_8Suggestion.this, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
                                                mLoaderLayout.setVisibility(View.GONE);
                                                isLoading = false;
                                            }
                                        });
                            }


                        }
                        break;
                }
            }
        };

        mBack.setOnClickListener(onClickListener);
        mSubmit.setOnClickListener(onClickListener);
        mContentEditText.setOnClickListener(onClickListener);
    }

}
