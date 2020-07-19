package com.example.mudskipper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mudskipper.R;
import com.example.mudskipper.activity.TrendingActivity;
import com.example.mudskipper.model.MovieModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DiscoverFragment extends Fragment implements SearchView.OnQueryTextListener {

    LinearLayout category_lay,trending_layout;
    Context context_f;
    ArrayList<String> category_arraylist =new ArrayList<>();

    //search bar
    //declare variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editSearch;
    //String [] movieList;
    ArrayList<MovieModel> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        category_lay = view.findViewById(R.id.category_lay);
        trending_layout = view.findViewById(R.id.trending_layout);

        //locate the ListView in listview_main.xml
        list = view.findViewById(R.id.listview);
        list.setVisibility(View.GONE);

        category_arraylist.clear();
        category_arraylist = new ArrayList<>();
        category_arraylist.add("Action");
        category_arraylist.add("Comedy");
        category_arraylist.add("Music Videos");
        category_arraylist.add("Short Films");
        for (int i = 0; i < category_arraylist.size(); i++) {
            MovieModel movieNames = new MovieModel(category_arraylist.get(i));
            //bind all strings into an array
            arrayList.add(movieNames);
        }
        //pass results to ListViewAdapter class
        adapter = new ListViewAdapter(context_f, arrayList);
        //bind Adapter to ListView
        list.setAdapter(adapter);
        editSearch = view.findViewById(R.id.search);
        editSearch.setOnQueryTextListener(this);

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
    @Override
    public boolean onQueryTextSubmit(String query)
    {
        if (!(query == null || query.trim().isEmpty())) {
            list.setVisibility(View.VISIBLE);
        }
        else {
            list.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        list.setVisibility(View.VISIBLE);
        String text = newText;
        adapter.filter(text);
        return false;
    }

    //search adapter

    public class ListViewAdapter extends BaseAdapter
    {

        // Declare Variables

        Context mContext;
        LayoutInflater inflater;
        private List<MovieModel> animalNamesList = null;
        private ArrayList<MovieModel> arraylist;

        public ListViewAdapter(Context context, List<MovieModel> animalNamesList)
        {
            mContext = context;
            this.animalNamesList = animalNamesList;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<>();
            this.arraylist.addAll(animalNamesList);
        }

        public class ViewHolder {
            TextView name;
        }

        @Override
        public int getCount()
        {
            return animalNamesList.size();
        }

        @Override
        public MovieModel getItem(int position)
        {
            return animalNamesList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent)
        {
            final ViewHolder holder;
            if (view == null)
            {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.listview_item, null);
                // Locate the TextViews in listview_item.xml
                holder.name = view.findViewById(R.id.name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // set the results into TextViews
            holder.name.setText(animalNamesList.get(position).getMovieName());
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    list.setVisibility(View.GONE);
                }
            });
            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            animalNamesList.clear();
            if (charText.length() == 0)
            {
                animalNamesList.addAll(arraylist);
            } else {
                for (MovieModel wp : arraylist)
                {
                    if (wp.getMovieName().toLowerCase(Locale.getDefault()).contains(charText))
                    {
                        animalNamesList.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}