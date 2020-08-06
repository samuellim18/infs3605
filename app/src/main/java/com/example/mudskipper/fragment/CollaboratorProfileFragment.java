package com.example.mudskipper.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mudskipper.R;
import com.example.mudskipper.activity.EditProfileActivity;
import com.example.mudskipper.activity.LoginActivity;
import com.example.mudskipper.activity.OpenMessageActivity;
import com.example.mudskipper.databinding.FragmentAbtProjectProfileBinding;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class CollaboratorProfileFragment extends Fragment {

    String email;
    FragmentAbtProjectProfileBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // tab titles
    private String[] titles = new String[]{"About", "Projects"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAbtProjectProfileBinding.inflate(getLayoutInflater());

        email = getArguments().getString("collab_email");

        init();
        return binding.getRoot();
    }
    private void init() {
        // removing toolbar elevation
        //getSupportActionBar().setElevation(0);
        binding.profilePhoto.setImageResource(R.drawable.user_default_black);
        binding.logout.setVisibility(View.GONE);
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        binding.editProfileBtn.setVisibility(View.GONE);
        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));
        binding.messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OpenMessageActivity.class));
            }
        });

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
                    Bundle bundle = new Bundle();
                    bundle.putString("collab_email", email);
                    Fragment fragment = new CollaboratorProfileAboutDetailsFragment();
                    fragment.setArguments(bundle);
                    return fragment;
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

}