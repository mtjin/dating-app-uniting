package com.unilab.uniting.adapter.setprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;

import java.util.ArrayList;

public class SetProfileEditAdapter extends RecyclerView.Adapter<SetProfileEditAdapter.EditViewHolder> {

    private Context context;
    private ArrayList<String> currentItemList;
    private EditClickListener editClickListener;
    private String itemChecked;
    private int positionChecked = -1;

    private boolean isDone = false;


    public SetProfileEditAdapter(Context context, ArrayList<String> currentItemList, EditClickListener editClickListener, String itemChecked) {
        this.context = context;
        this.currentItemList = currentItemList;
        this.editClickListener = editClickListener;
        this.itemChecked = itemChecked;
    }

    @NonNull
    @Override
    public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_signup_all_in_one, parent, false);
        return new EditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EditViewHolder EditViewHolder, final int position) {
        String item = currentItemList.get(position);

        //프로필 정보 세팅
        EditViewHolder.checkBox.setText(item);
        if(item.equals(itemChecked)){
            EditViewHolder.checkBox.setChecked(true);
            positionChecked = position;
        }else{
            EditViewHolder.checkBox.setChecked(false);
        }

    }

    public void clear(){
        currentItemList.clear();
    }

    public void add(String item){
        currentItemList.add(item);
    }

    @Override
    public int getItemCount() {
        return currentItemList.size();
    }

    class EditViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;


        public EditViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_signup_all_in_one);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isDone){
                        editClickListener.listenDialogClick(currentItemList.get(getAdapterPosition()));
                    }else{
                        checkBox.setChecked(false);
                    }
                    isDone = true;
                }
            });
        }
    }

}
