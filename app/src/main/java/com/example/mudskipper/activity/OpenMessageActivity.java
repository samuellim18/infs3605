package com.example.mudskipper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mudskipper.model.User;
import com.example.mudskipper.databinding.ActivityOpenMessageBinding;
import com.example.mudskipper.fragment.ChatMessageFragment;
import com.example.mudskipper.fragment.ChatUserFragment;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpenMessageActivity extends AppCompatActivity {
    ActivityOpenMessageBinding binding;
    CircleImageView message_profile_pic;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    // tab titles
    private String[] titles = new String[]{"Messages", "Users"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //uses view binding to link the XML
        binding = ActivityOpenMessageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);
            }
        });
        System.out.println(username);
        init();
        binding.tabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();
            }
        });
    }

    //Initializes the layour with a tab view and view pager which allows the user to swipe between the message and users tabs
    private void init() {
        // removing toolbar elevation
        //getSupportActionBar().setElevation(0);

        binding.viewPager.setAdapter(new ViewPagerFragmentAdapter(this));

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
                    return new ChatMessageFragment();
                case 1:
                    return new ChatUserFragment();

            }
            return new ChatMessageFragment();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }
}
