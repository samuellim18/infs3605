package com.example.login_test.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login_test.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class EditProfileActivity extends AppCompatActivity {
    String oldName, oldPhone, oldDescription,oldSkills;
    String newName, newPhone, newDescription,newSkills;
    TextView emailTV, epSkills;
    Button saveData;
    EditText epName, epPhone, epDescription;
    String TAG = "edit prof";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String email = currentUser.getEmail();
    TextView mItemSelected;
    String[] skillItem;
    boolean[] selectedSkills;
    ArrayList<Integer> mSkills = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        initializeUI();
        getData();
    }

    public void initializeUI(){
        epName = findViewById(R.id.editP_name);
        epDescription = findViewById(R.id.editP_description);
        epPhone = findViewById(R.id.editP_mobile);
        epSkills = findViewById(R.id.editP_skills);
        emailTV  =findViewById(R.id.editP_tv_email);
    }

    public void getData(){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        oldName=document.getString("name");
                        epName.setText(oldName);
                        emailTV.setText(email);
                        oldPhone=document.getString("mobile_phone");
                        oldSkills = document.getString("skills");
                        oldDescription = document.getString("description");
                        epPhone.setText(oldPhone);
                        epSkills.setText(oldSkills);
                        epDescription.setText(oldDescription);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }

            }
        });
    }
    private View.OnClickListener awesomeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateUserDetails();
        }
    };

    private void updateUserDetails() {

    }

}
