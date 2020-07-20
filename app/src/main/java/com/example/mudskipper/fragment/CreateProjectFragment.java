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

import com.example.mudskipper.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CreateProjectFragment extends Fragment {
    private EditText project_name, project_description, video_link;
    private String project_nameS, project_descriptionS, video_linkS, emailS;
    private Button add_project_btn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);

        project_name = view.findViewById(R.id.et_project_name);
        project_description = view.findViewById(R.id.et_project_description);
        video_link = view.findViewById(R.id.et_video_link);
        add_project_btn = view.findViewById(R.id.add_project_btn);
        add_project_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProject();
            }
        });

        return view;
    }

    private void addNewProject(){
        project_nameS = project_name.getText().toString();
        project_descriptionS = project_description.getText().toString();
        video_linkS = video_link.getText().toString();
        //emailS = mAuth.getCurrentUser().getEmail();
        emailS = currentUser.getEmail();

        Map<String, Object> newProject = new HashMap<>();
        newProject.put("project_name", project_nameS);
        newProject.put("project_description", project_descriptionS);
        newProject.put("video_link", video_linkS);
        newProject.put("email", emailS);

        db.collection("projects")
                .document(emailS + " " + project_nameS)
                .set(newProject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.d(TAG, "DocumentSnapshot added!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

}
