package com.example.mudskipper.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.example.mudskipper.activity.ProjectDetailActivity;
import com.example.mudskipper.adapter.ProjectAdapter;
import com.example.mudskipper.model.ProjectModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileProjectDetailsFragment extends Fragment {
    public static final String EXTRA_MESSAGE = "";
    private ArrayList<ProjectModel> project = new ArrayList<>();
    private Context mContext_f;
//  ArrayList<Integer> img_arraylist =new ArrayList<>();
    RecyclerView myRecyclerView;
    HomeRecyclerViewAdapter adapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_project_details, container, false);
        myRecyclerView = view.findViewById(R.id.recyclerview_project);
//        img_arraylist.clear();
//        for (int i = 0; i < 35; i++)
//        {
//            img_arraylist.add(R.drawable.dummy_trending);
//            img_arraylist.add(R.drawable.dummy_trending);
//        }
//        System.out.println(img_arraylist.size());
//        adapter = new HomeRecyclerViewAdapter(mContext_f);
//        int numberOfColumns = 2;
//        myRecyclerView.setLayoutManager(new GridLayoutManager(mContext_f, numberOfColumns));
//        myRecyclerView.setAdapter(adapter);

        getProjects();
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
            //   holder.menuImage.setImageResource(img_arraylist.get(position));

            Picasso.get().load("https://img.youtube.com/vi/" + project.get(position).getVideo_id() + "/hqdefault.jpg").into(holder.menuImage);
            holder.menuImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, project.get(position).getProject_name());
                    startActivity(intent);
                }
            });
        }

        @Override
//      public int getItemCount() { return img_arraylist.size(); }

        public int getItemCount() { return project.size(); }

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

    public void getProjects(){
        String emails = currentUser.getEmail();
        CollectionReference projects = db.collection("projects");
        Query query = projects.whereEqualTo("email", emails);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot projects : document.getDocuments()) {
                        project.add(new ProjectModel(projects.getString("project_name"), projects.getString("project_description"), projects.getString("video_link"), projects.getString("likes")));
                    }
//                    if (project.size() == 1) {
//                        num_of_project.setText(project.size() + " Project");
//                    } else {
//                        num_of_project.setText(project.size() + " Projects");
//                    }
                    showProjects();
                }
            }
        });
    }

    public void showProjects() {
        adapter = new HomeRecyclerViewAdapter(mContext_f);
        int numberOfColumns = 2;
        myRecyclerView.setLayoutManager(new GridLayoutManager(mContext_f, numberOfColumns));
        myRecyclerView.setAdapter(adapter);
    }
}