package com.example.mudskipper.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileAboutDetailsFragment extends Fragment {
    ArrayList<Integer> img_arraylist = new ArrayList<>();
    private Context mContext_f;
    RecyclerView myRecyclerView;
    Button follow;
    //ProfileFragment.HomeRecyclerViewAdapter adapter;
    CircleImageView profile_photo;
    ImageView profile_menu;
    String TAG = "Profile ";
    String name, mobile_phone, skills, description;
    TextView profileName, profileDescription, profileEmail, profilePhone, profileSkills;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://test-a2467.appspot.com/");
    StorageReference storageRef = storage.getReference();
    String email = currentUser.getEmail();

    TextView actor, director, photographer, artist, freelance, work_commitments, interests;
    CardView container_actor, container_director, container_photographer, container_artist, container_freelancer, container_commitment, container_interest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_profile_details, container, false);
        initialiseUI(view);
        setData();
        return view;
    }

    public void initialiseUI(View view) {
        profilePhone = view.findViewById(R.id.profilePhone);
        profileName = view.findViewById(R.id.profile_name);
        profileDescription = view.findViewById(R.id.profileDescription);
        profileEmail = view.findViewById(R.id.profileEmail);
        profileSkills = view.findViewById(R.id.profileSkills);

        actor = view.findViewById(R.id.actor);
        director = view.findViewById(R.id.director);
        photographer = view.findViewById(R.id.photographer);
        artist = view.findViewById(R.id.artist);
        freelance = view.findViewById(R.id.freelance);
        work_commitments = view.findViewById(R.id.work_commitments);
        interests = view.findViewById(R.id.interests);

        container_actor = view.findViewById(R.id.container_actor);
        container_director = view.findViewById(R.id.container_director);
        container_photographer = view.findViewById(R.id.container_photographer);
        container_artist = view.findViewById(R.id.container_artist);
        container_freelancer = view.findViewById(R.id.container_freelancer);
        container_commitment = view.findViewById(R.id.container_commitment);
        container_interest = view.findViewById(R.id.container_interest);
    }

    public void setData() {
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name = document.getString("name");
                        mobile_phone = document.getString("mobile_phone");
                        skills = document.getString("skills");
                        description = document.getString("description");
//                        profileName.setText(name);
                        profileName.setText("");
                        profileEmail.setText(email);
                        profilePhone.setText("Phone no: " + mobile_phone);
                        profileSkills.setText("Skills: " + skills);
                        profileDescription.setText("Description: " + description);

                        List<String> acting_skills = (List<String>) document.get("acting_skills");
                        if (acting_skills != null) {
                            if (acting_skills.size() > 0) {
                                container_actor.setVisibility(View.VISIBLE);
                                for (int i = 0; i < acting_skills.size(); i++) {
                                    if (i == 0)
                                        actor.setText(acting_skills.get(i) + "");
                                    else
                                        actor.setText(actor.getText().toString().trim() + "," + acting_skills.get(i) + "");

                                }
                            }
                        }

                        List<String> directing_skills = (List<String>) document.get("directing_skills");
                        if (directing_skills != null) {
                            if (directing_skills.size() > 0) {
                                container_director.setVisibility(View.VISIBLE);
                                for (int i = 0; i < directing_skills.size(); i++) {
                                    if (i == 0)
                                        director.setText(directing_skills.get(i) + "");
                                    else
                                        director.setText(director.getText().toString().trim() + "," + directing_skills.get(i) + "");

                                }
                            }
                        }

                        List<String> photography_skills = (List<String>) document.get("photography_skills");
                        if (photography_skills != null) {
                            if (photography_skills.size() > 0) {
                                container_photographer.setVisibility(View.VISIBLE);
                                for (int i = 0; i < photography_skills.size(); i++) {
                                    if (i == 0)
                                        photographer.setText( photography_skills.get(i) + "");
                                    else
                                        photographer.setText(photographer.getText().toString().trim() + "," + photography_skills.get(i) + "");

                                }
                            }
                        }

                        List<String> design_skills = (List<String>) document.get("design_skills");
                        if (design_skills != null) {
                            if (design_skills.size() > 0) {
                                container_artist.setVisibility(View.VISIBLE);
                                for (int i = 0; i < design_skills.size(); i++)
                                {
                                    if (i == 0)
                                        artist.setText( design_skills.get(i) + "");
                                    else
                                        artist.setText(artist.getText().toString().trim() + "," + design_skills.get(i) + "");

                                }
                            }
                        }

                        List<String> freelance_skills = (List<String>) document.get("freelance_skills");
                        if (freelance_skills != null) {
                            if (freelance_skills.size() > 0) {
                                container_freelancer.setVisibility(View.VISIBLE);
                                for (int i = 0; i < freelance_skills.size(); i++)
                                {
                                    if (i == 0)
                                        freelance.setText( freelance_skills.get(i) + "");
                                    else
                                        freelance.setText(freelance.getText().toString().trim() + "," + freelance_skills.get(i) + "");

                                }
                            }
                        }

                        List<String> interests_list = (List<String>) document.get("interests");
                        if (interests_list != null) {
                            if (interests_list.size() > 0) {
                                container_interest.setVisibility(View.VISIBLE);
                                for (int i = 0; i < interests_list.size(); i++)
                                {
                                    if (i == 0)
                                        interests.setText( interests_list.get(i) + "");
                                    else
                                        interests.setText(interests.getText().toString().trim() + "," + interests_list.get(i) + "");
                                }
                            }
                        }

                        if (document.getString("work_commitments") != null) {
                            container_commitment.setVisibility(View.VISIBLE);
                            work_commitments.setText(document.getString("work_commitments") + "");

                        }

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