package com.example.mudskipper.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mudskipper.R;
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

import java.util.ArrayList;

public class CategoryProjectsActivity extends Activity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ProjectModel> project = new ArrayList<>();
    private ProgressDialog progressDialog;
    public static final String EXTRA_MESSAGE = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String category_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_category_projects);
        mRecyclerView = findViewById(R.id.projectRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        category_id = getIntent().getExtras().getString("category_id");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading projects");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);


        progressDialog.show();
        getProjects();

    }


    public void getProjects() {
        CollectionReference projects = db.collection("projects");
        Query query = projects.whereEqualTo("category_id", category_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot projects : document.getDocuments()) {
                        project.add(new ProjectModel(projects.getString("project_name"), projects.getString("project_description"), projects.getString("video_link"), projects.getString("likes"), projects.getDouble("view_count")));
                    }

                    showProjects();
                }
            }
        });
    }

    public void showProjects() {
        ProjectAdapter.RecyclerViewClickListener listener = new ProjectAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(CategoryProjectsActivity.this, ProjectDetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, project.get(position).getProject_name());
                startActivity(intent);
            }
        };
        mAdapter = new ProjectAdapter(project, listener);
        mRecyclerView.setAdapter(mAdapter);
        progressDialog.dismiss();
    }
}