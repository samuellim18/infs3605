package com.example.mudskipper.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mudskipper.R;
import com.example.mudskipper.activity.EditProfileActivity;
import com.example.mudskipper.adapter.FragmentViewPagerActivity;
import com.example.mudskipper.databinding.ActivityFragmentViewPagerBinding;
import com.example.mudskipper.databinding.FragmentFragmentViewPagerBinding;
import com.example.mudskipper.databinding.FragmentTestSliderBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class TestSliderFragment extends Fragment {

    FragmentTestSliderBinding binding;

    // tab titles
    private String[] titles = new String[]{"About", "Projects"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentTestSliderBinding.inflate(getLayoutInflater());

        init();
        return binding.getRoot();
    }
    private void init() {
        // removing toolbar elevation
        //getSupportActionBar().setElevation(0);
        binding.editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });
        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(getActivity()));

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
}