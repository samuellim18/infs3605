package com.example.mudskipper.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mudskipper.R;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileAboutDetailsFragment extends Fragment {
    ArrayList<Integer> img_arraylist =new ArrayList<>();
    private Context mContext_f;
    RecyclerView myRecyclerView;
    Button follow;
    //ProfileFragment.HomeRecyclerViewAdapter adapter;
    CircleImageView profile_photo;
    ImageView profile_menu;
    String TAG = "Profile ";
    String name,mobile_phone, skills,description;
    TextView profileName, profileDescription, profileEmail,profilePhone, profileSkills ;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://test-a2467.appspot.com/");
    StorageReference storageRef = storage.getReference();
    String email = currentUser.getEmail();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_profile_details, container, false);
        initialiseUI(view);
        setData();
        return view;
    }

    public void initialiseUI(View view){
        profilePhone = view.findViewById(R.id.profilePhone);
        profileName = view.findViewById(R.id.profile_name);
        profileDescription= view.findViewById(R.id.profileDescription);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileSkills = view.findViewById(R.id.profileSkills);

    }

    public void setData(){
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name=document.getString("name");
                        mobile_phone=document.getString("mobile_phone");
                        skills = document.getString("skills");
                        description = document.getString("description");
                        profileName.setText(name);
                        profileEmail.setText(email);
                        //profilePhone.setText("Phone no: " + mobile_phone);
                        profileSkills.setText(skills);
                        profileDescription.setText("Description: " + description);

                        System.out.println(document);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}