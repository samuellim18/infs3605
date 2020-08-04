package com.example.mudskipper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidbuts.multispinnerfilter.MultiSpinner;
import com.example.mudskipper.R;
import com.example.mudskipper.model.SkillModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.ViewHolder> {
    private ArrayList<SkillModel> projectList;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public SkillsAdapter(ArrayList<SkillModel> projectList, RecyclerViewClickListener listener){
        this.projectList = projectList;
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mSkillName;
        public MultiSpinner multiSpinner;
        private RecyclerViewClickListener mListener;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
            /*Context context = v.getContext();
            Intent intent = new Intent(context, ProjectDetailActivity.class);
            context.startActivity(intent);*/
        }
    }

    @NonNull
    @Override
    public SkillsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SkillsAdapter.ViewHolder holder, int position) {
        SkillModel skill = projectList.get(position);
        holder.mSkillName.setText((CharSequence) skill.getSkillName());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}