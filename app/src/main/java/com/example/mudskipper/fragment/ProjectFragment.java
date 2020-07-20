package com.example.mudskipper.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ProjectFragment extends Fragment {
    private Button create_project_bt;
    private String emailS;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ProjectModel> project = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        mRecyclerView = view.findViewById(R.id.projectRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        create_project_bt = view.findViewById(R.id.create_project_btn);
        create_project_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new CreateProjectFragment());
                fragmentTransaction.commit();

            }
        });

        getProjects();

        return view;

    }

    public void getProject() {
        DocumentReference docRef = db.collection("projects").document("frL4vZG97l7PKgFGGaOo");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String project_name = document.getString("project_name");
                        System.out.println("PROJECT NAME: " + project_name);
                    } else {
                        //Log.d(TAG, "No such document");
                        System.out.println("No such doc");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                    System.out.println("No such doc");
                }

            }
        });
    }

    public void getProjects() {
        emailS = currentUser.getEmail();
        CollectionReference projects = db.collection("projects");
        Query query = projects.whereEqualTo("email", emailS);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    //ArrayList<ProjectModel> project = new ArrayList<>();
                    for (DocumentSnapshot projects : document.getDocuments()) {
                        project.add(new ProjectModel(projects.getString("project_name"), projects.getString("project_description"), projects.getString("video_link")));
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

            }
        };

        mAdapter = new ProjectAdapter(project, listener);
        mRecyclerView.setAdapter(mAdapter);

    }

}
