package com.unilab.uniting.adapter.launch;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;

import java.util.ArrayList;

public class
LaunchSignUpAdapter extends RecyclerView.Adapter<LaunchSignUpAdapter.SignUpViewHolder> {

    private Context context;
    private ArrayList<String> currentItemList;
    private SignUpClickListener signUpClickListener;
    private int checkedPosition;
    private ArrayList<String> checkedPersonalityList;
    private boolean isLoading = false;


    public LaunchSignUpAdapter(Context context, ArrayList<String> currentItemList, SignUpClickListener signUpClickListener, int checkedPosition, ArrayList<String> checkedPersonalityList) {
        this.context = context;
        this.currentItemList = currentItemList;
        this.signUpClickListener = signUpClickListener;
        this.checkedPosition = checkedPosition;
        this.checkedPersonalityList = checkedPersonalityList;
    }

    @NonNull
    @Override
    public SignUpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_signup_all_in_one, parent, false);
        return new SignUpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SignUpViewHolder signUpViewHolder, final int position) {
        String item = currentItemList.get(position);

        //프로필 정보 세팅
        signUpViewHolder.checkBox.setText(item);

        if (currentItemList.contains("열정적인")) { //성격은 3개 받아야되서 따로함.
            if (checkedPersonalityList.contains(item)) {
                signUpViewHolder.checkBox.setChecked(true);
            } else {
                signUpViewHolder.checkBox.setChecked(false);
            }
        } else {
            if (position == checkedPosition) {
                signUpViewHolder.checkBox.setChecked(true);
            } else {
                signUpViewHolder.checkBox.setChecked(false);
            }
        }


    }

    public void clear() {
        currentItemList.clear();
    }

    public void add(String item) {
        currentItemList.add(item);
    }

    @Override
    public int getItemCount() {
        return currentItemList.size();
    }

    class SignUpViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;


        public SignUpViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_signup_all_in_one);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isLoading) {
                        isLoading = true;
                        checkBox.setEnabled(false);
                        if (currentItemList.contains("열정적인")) { //성격일 때
                            if (checkBox.isChecked()) {
                                if (checkedPersonalityList.size() >= 3) { //이미 3개 채워져있는 경우 더 체크 못하게.
                                    checkBox.setChecked(false);
                                    Toast.makeText(context, "최대 3개만 골라주세요.", Toast.LENGTH_SHORT).show();
                                    isLoading = false;
                                    checkBox.setEnabled(true);
                                } else {
                                    checkedPersonalityList.add(checkBox.getText().toString());
                                    if (checkedPersonalityList.size() == 3) { // 3개 채웠을 때는 딜레이 걸기
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                signUpClickListener.listenPersonalityClick(checkedPersonalityList);
                                                notifyDataSetChanged();
                                                checkedPosition = -1; //체크 표시 리셋
                                                isLoading = false;
                                                checkBox.setEnabled(true);

                                            }
                                        }, 200);

                                    } else {
                                        signUpClickListener.listenPersonalityClick(checkedPersonalityList);
                                        notifyDataSetChanged();
                                        checkedPosition = -1; //체크 표시 리셋
                                        isLoading = false;
                                        checkBox.setEnabled(true);
                                    }
                                }
                            } else {
                                checkedPersonalityList.remove(checkBox.getText().toString());
                                notifyDataSetChanged();
                                signUpClickListener.listenPersonalityClick(checkedPersonalityList);
                                checkedPosition = -1; //체크 표시 리셋
                                isLoading = false;
                                checkBox.setEnabled(true);
                            }
                        } else { //성격 제외 나머지 항목일 때
                            checkedPosition = getAdapterPosition();
                            notifyDataSetChanged();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    signUpClickListener.listenClick(checkedPosition);
                                    checkedPosition = -1; //체크 표시 리셋
                                    isLoading = false;
                                    checkBox.setEnabled(true);
                                }
                            }, 200);
                        }
                    }
                }
            });

        }
    }
}
