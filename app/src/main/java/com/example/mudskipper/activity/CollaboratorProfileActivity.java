package com.example.mudskipper.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.mudskipper.R;
import com.example.mudskipper.fragment.CollaboratorProfileFragment;

import butterknife.ButterKnife;

public class CollaboratorProfileActivity extends AppCompatActivity {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborator_profile);
        ButterKnife.bind(this);

        String email = getIntent().getExtras().getString(ProjectDetailActivity.EXTRA_MESSAGE,"defaultKey");
        Bundle bundle = new Bundle();
        bundle.putString("collab_email", email);

        fragment = new CollaboratorProfileFragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();

    }
}