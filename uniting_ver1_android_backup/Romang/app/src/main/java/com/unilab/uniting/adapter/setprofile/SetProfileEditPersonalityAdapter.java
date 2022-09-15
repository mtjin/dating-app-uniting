package com.unilab.uniting.adapter.setprofile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;

import java.util.ArrayList;

public class SetProfileEditPersonalityAdapter extends RecyclerView.Adapter<SetProfileEditPersonalityAdapter.EditViewHolder> {

    private Context context;
    private ArrayList<String> totalItemList;
    private EditClickListener editClickListener;
    private ArrayList<String> checkedItemList;
    private ArrayList<String> initialCheckedItemList;

    public SetProfileEditPersonalityAdapter(Context context, ArrayList<String> totalItemList, EditClickListener editClickListener, ArrayList<String> initialCheckedItemList) {
        this.context = context;
        this.totalItemList = totalItemList;
        this.editClickListener = editClickListener;
        this.initialCheckedItemList = initialCheckedItemList;
        this.checkedItemList = initialCheckedItemList;
    }

    @NonNull
    @Override
    public EditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_personality, parent, false);
        return new EditViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EditViewHolder EditViewHolder, final int position) {
        String item = totalItemList.get(position);

        //프로필 정보 세팅
        EditViewHolder.checkBox.setText(item);
        if(initialCheckedItemList.contains(item)){
            EditViewHolder.checkBox.setChecked(true);
        }
    }

    public void clear(){
        totalItemList.clear();
    }

    public void add(String item){
        totalItemList.add(item);
    }

    @Override
    public int getItemCount() {
        return totalItemList.size();
    }

    class EditViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;


        public EditViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_personality_checkbox);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkBox.isChecked()){
                        if(checkedItemList.size() >= 3){
                            checkBox.setChecked(false);
                            Toast.makeText(context, "3개만 골라주세요.", Toast.LENGTH_SHORT).show();
                        }else{
                            checkedItemList.add(checkBox.getText().toString());
                            editClickListener.listenPersonalityDialogClick(checkedItemList);
                        }
                    }else{
                        checkedItemList.remove(checkBox.getText().toString());
                        editClickListener.listenPersonalityDialogClick(checkedItemList);
                    }
                }
            });

        }
    }

}
