package com.example.mudskipper.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mudskipper.R;

import java.util.ArrayList;

public class ProfileProjectDetailsFragment extends Fragment {
    private Context mContext_f;
    ArrayList<Integer> img_arraylist =new ArrayList<>();
    RecyclerView myRecyclerView;
    HomeRecyclerViewAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_project_details, container, false);
        myRecyclerView = view.findViewById(R.id.recyclerview_project);
        img_arraylist.clear();
        for (int i = 0; i < 35; i++)
        {
            img_arraylist.add(R.drawable.dummy_trending);
            img_arraylist.add(R.drawable.dummy_trending);
        }
        System.out.println(img_arraylist.size());
        adapter = new HomeRecyclerViewAdapter(mContext_f);
        int numberOfColumns = 2;
        myRecyclerView.setLayoutManager(new GridLayoutManager(mContext_f, numberOfColumns));
        myRecyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void onAttach(@NonNull Context context)
    {
        mContext_f= context;
        super.onAttach(context);
    }
    public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>
    {

        Context context;


        public HomeRecyclerViewAdapter(Context context)
        {
            this.context = context;
        }

        @NonNull
        @Override
        public HomeRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_row,parent,false);
            HomeRecyclerViewAdapter.ViewHolder viewHolder = new HomeRecyclerViewAdapter.ViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull HomeRecyclerViewAdapter.ViewHolder holder, final int position)
        {
            //   Glide.with(context).load(userInformation.getImage()).into(holder.menuImage);

            holder.menuImage.setImageResource(img_arraylist.get(position));
            holder.menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                }
            });
        }

        @Override
        public int getItemCount()
        {
            return img_arraylist.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {

            public ImageView menuImage;

            public ViewHolder(@NonNull View itemView)
            {
                super(itemView);
                context = itemView.getContext();
                menuImage = itemView.findViewById(R.id.menu_image);
            }
        }
    }
}