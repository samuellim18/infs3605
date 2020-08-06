package com.example.mudskipper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddCollaboratorDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private AutoCompleteTextView et_name;
    //private EditText et_name, et_role;
    private String role;
    private Spinner role_spinner;
    private AddCollaboratorDialogListener listener;
    private ArrayList<String> collaborator = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_collaborator_dialog, null);

        role_spinner = view.findViewById(R.id.role_spinner);
        ArrayAdapter<CharSequence> role_adapter = ArrayAdapter.createFromResource(getContext(), R.array.roles, android.R.layout.simple_spinner_item);
        role_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role_spinner.setAdapter(role_adapter);
        role_spinner.setOnItemSelectedListener(this);

        builder.setView(view)
                .setTitle("Add Collaborator")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String collab_name = et_name.getText().toString();
                        String collab_email = collab_name.substring(collab_name.indexOf("(")+1,collab_name.indexOf(")"));
                        AddCollaboratorDialogListener listener = (AddCollaboratorDialogListener) getTargetFragment();
                        listener.applyTexts(collab_name, role, collab_email);
                    }
                });

        getCollaborators();

        et_name = view.findViewById(R.id.edit_collaborator_name);
        ArrayAdapter<String> name_adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, collaborator);
        et_name.setAdapter(name_adapter);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddCollaboratorDialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        role = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface AddCollaboratorDialogListener{
        void applyTexts(String name, String role, String email);
    }

    private void getCollaborators(){
        CollectionReference collaborators = db.collection("users");
        collaborators.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot collaborators : document.getDocuments()) {
                        collaborator.add(collaborators.getString("name") + " (" + collaborators.getString("email") + ")");
                    }

                }
            }
        });
    }
}
