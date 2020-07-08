package com.example.login_test.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.login_test.R;
import com.example.login_test.activity.TrendingActivity;

import java.util.ArrayList;


public class DiscoverFragment extends Fragment
{

    LinearLayout category_lay,trending_layout;
    Context context_f;
    ArrayList<String> category_arraylist =new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        category_lay = view.findViewById(R.id.category_lay);
        trending_layout = view.findViewById(R.id.trending_layout);


        category_arraylist.clear();
        category_arraylist =new ArrayList<>();
        category_arraylist.add("Action");
        category_arraylist.add("Comedy");
        category_arraylist.add("Music Videos");
        category_arraylist.add("Short Films");
        category_lay.removeAllViews();
        for ( int i=0; i<category_arraylist.size(); i++){
            View category_view = LayoutInflater.from(context_f).inflate(R.layout.category_row, null);
            TextView category_name = category_view.findViewById(R.id.category_name);
            category_name.setText(category_arraylist.get(i));

            category_lay.addView(category_view);
        }
        trending_layout.removeAllViews();
        for ( int i=0; i<10; i++){
            View category_view = LayoutInflater.from(context_f).inflate(R.layout.trending_video_row, null);
            TextView category_name = category_view.findViewById(R.id.category_name);
            ImageView trending_image = category_view.findViewById(R.id.trending_image);
            trending_image.setImageResource(R.drawable.dummy_trending);
            category_name.setText("Category video "+(i+1));

            trending_layout.addView(category_view);

            category_view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context_f, TrendingActivity.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        context_f= context;
        super.onAttach(context);
    }
}