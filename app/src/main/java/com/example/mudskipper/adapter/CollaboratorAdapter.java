package com.example.mudskipper.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.R;
import com.example.mudskipper.model.CollaboratorModel;
import com.example.mudskipper.model.ProjectModel;

import java.util.ArrayList;

public class CollaboratorAdapter extends RecyclerView.Adapter<CollaboratorAdapter.ViewHolder> {
    private ArrayList<CollaboratorModel> collaboratorList;
    private CollaboratorAdapter.RecyclerViewClickListener mListener;

    public interface RecyclerViewClickListener {
        void onClick(View view, int position);
    }

    public CollaboratorAdapter(ArrayList<CollaboratorModel> collaboratorList, CollaboratorAdapter.RecyclerViewClickListener listener){
        this.collaboratorList = collaboratorList;
        this.mListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //public ImageView mThumbnail;
        public TextView mName, mRole;

        private CollaboratorAdapter.RecyclerViewClickListener mListener;

        public ViewHolder(@NonNull View itemView, CollaboratorAdapter.RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
            mName = itemView.findViewById(R.id.collaborator_name);
            mRole = itemView.findViewById(R.id.collaborator_role);

        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }

    }

    @NonNull
    @Override
    public CollaboratorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.collaborator_list, parent, false);
        CollaboratorAdapter.ViewHolder vh = new CollaboratorAdapter.ViewHolder(v, mListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CollaboratorModel collaborator = collaboratorList.get(position);
        holder.mName.setText(collaborator.getCollaborator_name());
        holder.mRole.setText(collaborator.getCollaborator_role());

    }

    @Override
    public int getItemCount() {
        return collaboratorList.size();
    }
}
