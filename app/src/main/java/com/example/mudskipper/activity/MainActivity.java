package com.example.mudskipper.activity;
//test comment
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mudskipper.R;
import com.example.mudskipper.fragment.DiscoverFragment;
import com.example.mudskipper.fragment.ProjectFragment;
import com.example.mudskipper.fragment.AbtAndProjProfileFragments;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    @BindView(R.id.navigation_bottom)
    BottomNavigationView navigationBottom;
    private String fragment_Tag = "Discover Fragment";
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fragment = new DiscoverFragment();
        loadFragment(fragment,1);
        navigationBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

                switch (item.getItemId())
                {
                    case R.id.nav_discover:
                        /*toolbar_title.setVisibility(View.GONE);
                        search_layout.setVisibility(View.VISIBLE);*/
                        fragment = new DiscoverFragment();
                        loadFragment(fragment,1);
                        return true;
                    case R.id.nav_projects:
                        /*toolbar_title.setVisibility(View.VISIBLE);
                        toolbar_title.setText("Reservations");*/
                        fragment = new ProjectFragment();
                        loadFragment(fragment,2);
                        return true;
                    case R.id.nav_profile:
                        /*toolbar_title.setVisibility(View.VISIBLE);
                        toolbar_title.setText("Messages");*/
                        fragment = new AbtAndProjProfileFragments();
                        loadFragment(fragment,3);
                        return true;
                }

                return false;
            }
        });
    }

    public void loadFragment(Fragment fragment,int fragment_number)
    {
        if (fragment_number==1)
        {
            fragment_Tag = "Discover Fragment";
        }else if (fragment_number==2)
        {
            fragment_Tag = "Project Fragment";
        }else if (fragment_number==3)
        {
            fragment_Tag = "MyProfile Fragment";
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }


}