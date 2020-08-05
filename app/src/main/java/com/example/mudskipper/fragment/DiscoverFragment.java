package com.example.mudskipper.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
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
import com.example.mudskipper.activity.ProjectDetailActivity;
import com.example.mudskipper.activity.TrendingActivity;
import com.example.mudskipper.model.MovieModel;
import com.example.mudskipper.model.ProjectModel;
import com.example.mudskipper.model.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiscoverFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static final String EXTRA_MESSAGE = "";
    LinearLayout category_lay, trending_layout;
    Context context_f;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //search bar
    //declare variables
    ListView list;
    ListViewAdapter adapter;
    SearchView editSearch;
    //String [] movieList;
    ArrayList<MovieModel> arrayList = new ArrayList<>();
    private ArrayList<ProjectModel> project = new ArrayList<>();
    //  ArrayList<String> category_arraylist =new ArrayList<>();
    private ArrayList<CategoryModel> category_arraylist = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        category_lay = view.findViewById(R.id.category_lay);
        trending_layout = view.findViewById(R.id.trending_layout);

        //locate the ListView in listview_main.xml
        list = view.findViewById(R.id.listview);
        list.setVisibility(View.GONE);

        category_arraylist.clear();
//        category_arraylist = new ArrayList<>();
//        category_arraylist.add("Action");
//        category_arraylist.add("Comedy");
//        category_arraylist.add("Music Videos");
//        category_arraylist.add("Short Films");

        for (int i = 0; i < category_arraylist.size(); i++) {
//            MovieModel movieNames = new MovieModel(category_arraylist.get(i));
            MovieModel movieNames = new MovieModel(category_arraylist.get(i).category_name);
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
//        for ( int i=0; i<category_arraylist.size(); i++){
//            View category_view = LayoutInflater.from(context_f).inflate(R.layout.category_row, null);
//            TextView category_name = category_view.findViewById(R.id.category_name);
//            category_name.setText(category_arraylist.get(i).category_name);
//
//            ImageView category_image = category_view.findViewById(R.id.category_image);
//            Glide.with(this).load(category_arraylist.get(i).category_image).into(category_image);
//
//            category_lay.addView(category_view);
//        }

        getCategories();

        /*
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
         */

        getProjects();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        context_f = context;
        super.onAttach(context);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!(query == null || query.trim().isEmpty())) {
            list.setVisibility(View.VISIBLE);
        } else {
            list.setVisibility(View.GONE);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        list.setVisibility(View.VISIBLE);
        String text = newText;
        adapter.filter(text);
        return false;
    }

    //search adapter

    public class ListViewAdapter extends BaseAdapter {
        // Declare Variables
        Context mContext;
        LayoutInflater inflater;
        private List<MovieModel> animalNamesList = null;
        private ArrayList<MovieModel> arraylist;

        public ListViewAdapter(Context context, List<MovieModel> animalNamesList) {
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
        public int getCount() {
            return animalNamesList.size();
        }

        @Override

        public MovieModel getItem(int position) {
            return animalNamesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
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
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.setVisibility(View.GONE);
                }
            });
            return view;
        }

        // Filter Class
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            animalNamesList.clear();
            if (charText.length() == 0) {
                animalNamesList.addAll(arraylist);
            } else {
                for (MovieModel wp : arraylist) {
                    if (wp.getMovieName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        animalNamesList.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void getCategories() {
        CollectionReference categories = db.collection("Categories");
//      Query query = categories.whereEqualTo("email", "z@gmail.com");
        categories.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot categories : document.getDocuments()) {
                        CategoryModel categoryModel = new CategoryModel();
                        categoryModel.category_name = categories.getString("category_name");
                        categoryModel.category_image = categories.getString("category_image");
                        categoryModel.object_id = categories.getString("object_id");
                        category_arraylist.add(categoryModel);
                    }
                    addCategoryView();
                }
            }
        });
    }

    private void addCategoryView() {
        category_lay.removeAllViews();
        for (int i = 0; i < category_arraylist.size(); i++) {
            View category_view = LayoutInflater.from(context_f).inflate(R.layout.category_row, null);
//            ImageView category_image = category_view.findViewById(R.id.category_image);
//            Picasso.get().load(category_arraylist.get(i).category_image).into(category_image);
//            Glide.with(this).load(category_arraylist.get(i).category_image).into(category_image);
            TextView category_name = category_view.findViewById(R.id.category_name);
            category_name.setText(category_arraylist.get(i).category_name);
            category_lay.addView(category_view);

            MovieModel movieNames = new MovieModel(category_arraylist.get(i).category_name);
            //bind all strings into an array
            arrayList.add(movieNames);
        }

        //pass results to ListViewAdapter class
        adapter = new ListViewAdapter(context_f, arrayList);
        //bind Adapter to ListView
        list.setAdapter(adapter);
    }

    public void getProjects(){
        CollectionReference projects = db.collection("projects");
        projects.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    QuerySnapshot document = task.getResult();
                    for (DocumentSnapshot projects : document.getDocuments()){
                        project.add(new ProjectModel(projects.getString("project_name"), projects.getString("project_description"), projects.getString("video_link"), projects.getString("likes")));
                    }
//                    if (project.size() == 1) {
//                        num_of_project.setText(project.size() + " Project");
//                    } else {
//                        num_of_project.setText(project.size() + " Projects");
//                    }
                showProjects();
            }
        }
    });
}

    private void showProjects(){
        trending_layout.removeAllViews();
        for ( int i=0; i<project.size(); i++){
            View category_view = LayoutInflater.from(context_f).inflate(R.layout.trending_video_row, null);
            TextView category_name = category_view.findViewById(R.id.category_name);
            TextView total_likes = category_view.findViewById(R.id.total_likes);
            total_likes.setText(project.get(i).getLikes());
            ImageView trending_image = category_view.findViewById(R.id.trending_image);
            trending_image.setImageResource(R.drawable.dummy_trending);
            category_name.setText(project.get(i).getProject_name());
            Picasso.get().load("https://img.youtube.com/vi/" + project.get(i).getVideo_id() + "/hqdefault.jpg").into(trending_image);
            trending_layout.addView(category_view);

            int finalI = i;
            category_view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), ProjectDetailActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, project.get(finalI).getProject_name());
                    startActivity(intent);
                }
            });
        }
    }
}