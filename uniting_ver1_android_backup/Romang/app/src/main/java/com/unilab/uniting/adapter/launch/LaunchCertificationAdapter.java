package com.unilab.uniting.adapter.launch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unilab.uniting.R;

import java.util.List;

public class
LaunchCertificationAdapter extends RecyclerView.Adapter<LaunchCertificationAdapter.UniversityViewHolder> {

    private Context context;
    private List<String> currentItemList;
    private UniversityClickListener universityClickListener;

    public LaunchCertificationAdapter(Context context, List<String> currentItemList, UniversityClickListener universityClickListener) {
        this.context = context;
        this.currentItemList = currentItemList;
        this.universityClickListener = universityClickListener;

    }

    @NonNull
    @Override
    public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_certification_university, parent, false);
        return new UniversityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UniversityViewHolder universityViewHolder, final int position) {
        String item = currentItemList.get(position);

        //프로필 정보 세팅
        universityViewHolder.universityTextView.setText(item);

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

    class UniversityViewHolder extends RecyclerView.ViewHolder {
        public TextView universityTextView;


        public UniversityViewHolder(@NonNull View itemView) {
            super(itemView);
            universityTextView = itemView.findViewById(R.id.item_certification_tv_university);

            universityTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String univ = universityTextView.getText().toString();
                    universityClickListener.click(univ);
                }
            });

        }
    }

    public interface UniversityClickListener {
        void click(String university);
    }
}
