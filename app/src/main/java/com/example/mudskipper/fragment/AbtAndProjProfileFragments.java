package com.example.mudskipper.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mudskipper.R;
import com.example.mudskipper.activity.EditProfileActivity;
import com.example.mudskipper.activity.LoginActivity;
import com.example.mudskipper.activity.OpenMessageActivity;

import com.example.mudskipper.adapter.FragmentViewPagerActivity;
import com.example.mudskipper.databinding.ActivityFragmentViewPagerBinding;
//import com.example.mudskipper.databinding.FragmentTestSliderBinding;
import com.example.mudskipper.databinding.FragmentAbtProjectProfileBinding;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AbtAndProjProfileFragments extends Fragment {

    FragmentAbtProjectProfileBinding binding;
    String TAG = "AbtAndProjProfileFragments";

    // tab titles
    private String[] titles = new String[]{"About", "Projects"};

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email = currentUser.getEmail();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAbtProjectProfileBinding.inflate(getLayoutInflater());

        init();
        return binding.getRoot();
    }

    private void init() {
        // removing toolbar elevation
        //getSupportActionBar().setElevation(0);
//        binding.profilePhoto.setImageResource(R.drawable.user_default_black);
//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.getInstance().signOut();
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//            }
//        });
        setData();
        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));
//        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getContext(), OpenMessageActivity.class));
//            }
//        });

        // attaching tab mediator
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(titles[position])).attach();
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ProfileAboutDetailsFragment();
                case 1:
                    return new ProfileProjectDetailsFragment();
            }
            return new ProfileAboutDetailsFragment();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }

    public void setData() {
        DocumentReference docRef = db.collection("users").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
            binding.profileName.setText(document.getString("name") + "");
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