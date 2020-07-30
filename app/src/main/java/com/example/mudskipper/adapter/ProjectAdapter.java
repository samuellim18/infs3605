package com.example.mudskipper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.R;
import com.example.mudskipper.model.ProjectModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private ArrayList<ProjectModel> projectList;
    private RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public ProjectAdapter(ArrayList<ProjectModel> projectList, RecyclerViewClickListener listener){
        this.projectList = projectList;
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mThumbnail;
        public TextView mName;
        public TextView mDescription;
        private RecyclerViewClickListener mListener;

        public ViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
            mThumbnail = itemView.findViewById(R.id.thumbnailIv);
            mName = itemView.findViewById(R.id.projectNameTv);
            mDescription = itemView.findViewById(R.id.projectDescriptionTv);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list, parent, false);
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.ViewHolder holder, int position) {
        ProjectModel project = projectList.get(position);
        holder.mName.setText(project.getProject_name());
        holder.mDescription.setText(project.getProject_description());
        Picasso.get().load("https://img.youtube.com/vi/" + project.getVideo_id() + "/hqdefault.jpg").into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }
}
