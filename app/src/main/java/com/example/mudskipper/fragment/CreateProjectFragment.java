package com.example.mudskipper.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
    private ListView collaborator_list;
    ArrayAdapter collaborator_adapter;
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

        collaborator_list = view.findViewById(R.id.lv_collaborator);
        collaborator_adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_2, android.R.id.text1, collaborator_name){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(collaborator_name.get(position));
                text2.setText(collaborator_role.get(position));
                return view;
            }
        };
        collaborator_list.setAdapter(collaborator_adapter);

        return view;
    }

    private void addNewProject(){
        project_nameS = project_name.getText().toString();
        project_descriptionS = project_description.getText().toString();
        video_linkS = video_link.getText().toString();
        emailS = currentUser.getEmail();

        if (TextUtils.isEmpty(project_nameS) || TextUtils.isEmpty(project_descriptionS) || TextUtils.isEmpty(video_linkS)) {
            Toast.makeText(getContext(), "Missing field(s)", Toast.LENGTH_LONG).show();
            return;
        }

        if (collaborator_name.size() == 0) {
            Toast.makeText(getContext(), "Please add at least one collaborator", Toast.LENGTH_LONG).show();
            return;
        }

        Map<String, Object> newProject = new HashMap<>();
        newProject.put("project_name", project_nameS);
        newProject.put("project_description", project_descriptionS);
        newProject.put("video_link", video_linkS);
        newProject.put("email", emailS);
        newProject.put("likes", "0");

        newProject.put("collaborators", collaborator_name);
        newProject.put("collaborator_role", collaborator_role);

        db.collection("projects")
                .document(project_nameS)
                .set(newProject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
        collaborator_name.add(name);
        collaborator_role.add(role);
        collaborator_adapter.notifyDataSetChanged();
    }

}
