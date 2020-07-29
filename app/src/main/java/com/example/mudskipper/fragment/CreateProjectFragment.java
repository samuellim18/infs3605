package com.example.mudskipper.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mudskipper.AddCollaboratorDialog;
import com.example.mudskipper.R;
import com.example.mudskipper.model.ProjectModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateProjectFragment extends Fragment implements AddCollaboratorDialog.AddCollaboratorDialogListener{
    private EditText project_name, project_description, video_link;
    private String project_nameS, project_descriptionS, video_linkS, emailS;
    private Button add_project_btn, cancel_btn, add_collaborator_btn;
    private ArrayList<String> collaborator_name = new ArrayList<>();
    private ArrayList<String> collaborator_role = new ArrayList<>();
    private ArrayList<String> collaborator = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private TextView sample1, sample2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);

        sample1 = view.findViewById(R.id.et_sample);
        sample2 = view.findViewById(R.id.et_sample2);

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
        add_collaborator_btn = view.findViewById(R.id.add_collaborator_btn);
        add_collaborator_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCollaborator();
            }
        });
        cancel_btn = view.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                fragmentTransaction.replace(R.id.content_frame, new ProjectFragment());
                fragmentTransaction.commit();
            }
        });

        //getCollaborators();

        return view;
    }

    private void addNewProject(){
        project_nameS = project_name.getText().toString();
        project_descriptionS = project_description.getText().toString();
        video_linkS = video_link.getText().toString();
        emailS = currentUser.getEmail();

        Map<String, Object> newProject = new HashMap<>();
        newProject.put("project_name", project_nameS);
        newProject.put("project_description", project_descriptionS);
        newProject.put("video_link", video_linkS);
        newProject.put("email", emailS);
        newProject.put("likes", "0");

        ArrayList<String> collaborators = new ArrayList<>();
        collaborators.add("pat");
        collaborators.add("sam");
        collaborators.add("john");
        newProject.put("collaborators", collaborators);

        ArrayList<String> collaborator_role = new ArrayList<>();
        collaborator_role.add("role1");
        collaborator_role.add("role2");
        collaborator_role.add("role3");
        newProject.put("collaborator_role", collaborator_role);

        db.collection("projects")
                .document(project_nameS)
                .set(newProject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //getCollaborator();
                        Toast.makeText(getContext(), "Project added", Toast.LENGTH_LONG).show();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                        fragmentTransaction.replace(R.id.content_frame, new ProjectFragment());
                        fragmentTransaction.commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void addCollaborator(){
        AddCollaboratorDialog addCollaboratorDialog = new AddCollaboratorDialog();
        addCollaboratorDialog.setTargetFragment(CreateProjectFragment.this, 0);
        addCollaboratorDialog.show(getActivity().getSupportFragmentManager(), "Add Collab");

    }

    @Override
    public void applyTexts(String name, String role) {
        sample1.setText(name);
        sample2.setText(role);
        collaborator_name.add(name);
        collaborator_role.add(role);
    }

    private void getCollaborators(){
        emailS = currentUser.getEmail();
        CollectionReference collaborators = db.collection("users");
        //Query query = collaborators.whereEqualTo("email", emailS);
        collaborators.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot collaborators : document.getDocuments()) {
                        collaborator.add(collaborators.getString("name"));
                    }
                    for(int i = 0; i < collaborator.size(); i++){
                        System.out.println(collaborator.get(i));
                    }
                }
            }
        });
    }

}
