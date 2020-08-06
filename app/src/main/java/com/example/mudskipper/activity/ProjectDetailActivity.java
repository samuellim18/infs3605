package com.example.mudskipper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mudskipper.R;
import com.example.mudskipper.adapter.CollaboratorAdapter;
import com.example.mudskipper.fragment.ProjectFragment;
import com.example.mudskipper.model.CollaboratorModel;
import com.example.mudskipper.model.ProjectModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectDetailActivity extends YouTubeBaseActivity {
    TextView project_name, project_description, project_collaborator, tv_toolbar, tv_likes,creator_name;
    Button delete_project_btn;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ProjectModel project;
    ArrayList<CollaboratorModel> collaborator = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @BindView(R.id.toolbar)
    androidx.appcompat.widget.Toolbar toolbar;

    @BindView(R.id.youtube_player_view)
    YouTubePlayerView youtube_player_view;

    YouTubePlayer.OnInitializedListener onInitializedListener;

    String video_id = "";
    private static String YOUTUBE_API_KEY = "AIzaSyAQL8_xEae7uQ-8Mdfzj9Wvs7Kb1aVb7SU";
    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;
    LinearLayout likearea;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    Button tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        creator_name=findViewById(R.id.creator_name);
        tag=findViewById(R.id.tag);
        likearea = findViewById(R.id.likearea);
        likearea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalLike = 0;

                if (Integer.parseInt(project.getLikes()) > 0) {
                    totalLike = (Integer.parseInt(project.getLikes()) + 1);
                } else {
                    totalLike = 1;
                }
                tv_likes.setText(totalLike + "");
                project.setLikes(totalLike + "");
                DocumentReference ref = db.collection("projects").document(project.getProject_name());
                ref.update("likes", project.getLikes());
            }
        });


        mRecyclerView = findViewById(R.id.collaboratorRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        project_name = findViewById(R.id.tv_project_name);
        project_description = findViewById(R.id.tv_project_description);
        project_collaborator = findViewById(R.id.tv_project_collaborator);
        tv_toolbar = findViewById(R.id.tv_toolbar);
        tv_likes = findViewById(R.id.tv_likes);
        delete_project_btn = findViewById(R.id.delete_project_btn);
        delete_project_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProject();
            }
        });

        getProjectDetail();

    }

    public void getProjectDetail() {
        Intent intent = getIntent();
        String name = intent.getStringExtra(ProjectFragment.EXTRA_MESSAGE);

        DocumentReference docRef = db.collection("projects").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        project = new ProjectModel(document.getString("project_name"), document.getString("project_description"), document.getString("video_link"), document.getString("likes"), document.getDouble("view_count"));
                        if(document.getString("email")!=null)
                            project.email= document.getString("email");
                        if(document.getString("category_id")!=null)
                            project.tag= document.getString("category_id");

                        Typeface myFont = ResourcesCompat.getFont(getApplicationContext(), R.font.lato);
                        project_name.setText(project.getProject_name());
                        tv_toolbar.setText(project.getProject_name());
                        project_description.setText(project.getProject_description());
                        video_id = project.getVideo_id();
                        tv_likes.setText(project.getLikes());

                        List<String> collab = (List<String>) document.get("collaborators");
                        List<String> collab_role = (List<String>) document.get("collaborator_role");
                        for (int i = 0; i < collab.size(); i++) {
                            collaborator.add(new CollaboratorModel(collab.get(i), collab_role.get(i)));
                        }
                        if(project.email!=null){
                            DocumentReference userRef = db.collection("users").document(project.email);
//                            CollectionReference projects = db.collection("users");
//                            Query query = projects.whereEqualTo("email", project.email);
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if(document.getString("name")!=null){
                                            creator_name.setText(document.getString("name"));
                                        }

                                    }
                                }
                            });

                        }
                        if(project.tag!=null){
                            if (project.tag.length() > 0) {
                                DocumentReference userRef = db.collection("categories").document(project.tag);
//                            CollectionReference projects = db.collection("users");
//                            Query query = projects.whereEqualTo("email", project.email);
                                userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if(document.getString("category_name")!=null){
                                                tag.setText(document.getString("category_name"));
                                            }

                                        }
                                    }
                                });

                            }
                            tag.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(ProjectDetailActivity.this, CategoryProjectsActivity.class).putExtra("category_id", project.tag));

                                }
                            });

                        }

                        playVideo();
                        getCollaborators();
                        addProjectView();
                    } else {
                        //Log.d(TAG, "No such document");
                        System.out.println("No such doc");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                    System.out.println("No such doc");
                }
            }
        });





    }

    public void playVideo() {
        ButterKnife.bind(this);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(video_id);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        youtube_player_view.initialize(YOUTUBE_API_KEY, onInitializedListener);
    }

    public void getCollaborators() {
        showCollaborators();
    }

    public void showCollaborators() {
        CollaboratorAdapter.RecyclerViewClickListener listener = new CollaboratorAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };

        mAdapter = new CollaboratorAdapter(collaborator, listener);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void deleteProject() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Project");
        builder.setMessage("Are you sure you want to delete this project?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("projects").document(project.getProject_name()).delete();
                Toast.makeText(getApplicationContext(), "Project deleted", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.setCancelable(false);
        builder.show();
    }

    private void addProjectView() {

        int view = 0;
        if (project.getView_count() > 0) {
            view = (project.getView_count() + 1);
        } else {
            view = 1;
        }

        DocumentReference ref = db.collection("projects").document(project.getProject_name());
        ref.update("view_count", view).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("==onComplete==>",task.isSuccessful()+"");
            }

        });
    }
}