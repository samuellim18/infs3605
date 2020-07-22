package com.example.mudskipper.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mudskipper.R;
import com.example.mudskipper.fragment.ProjectFragment;
import com.example.mudskipper.model.ProjectModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProjectDetailActivity extends YouTubeBaseActivity {
    TextView project_name, project_description;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @BindView(R.id.youtube_player_view)
    YouTubePlayerView youtube_player_view;

    YouTubePlayer.OnInitializedListener onInitializedListener;

    //String video_id = "dQw4w9WgXcQ";
    String video_id = "";
    private static String YOUTUBE_API_KEY = "AIzaSyAQL8_xEae7uQ-8Mdfzj9Wvs7Kb1aVb7SU";
    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);

        project_name = findViewById(R.id.tv_project_name);
        project_description = findViewById(R.id.tv_project_description);

        getProjectDetail();

    }

    public void getProjectDetail(){
        Intent intent = getIntent();
        String name = intent.getStringExtra(ProjectFragment.EXTRA_MESSAGE);

        DocumentReference docRef = db.collection("projects").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ProjectModel project = new ProjectModel(document.getString("project_name"), document.getString("project_description"), document.getString("video_link"));

                        project_name.setText(project.getProject_name());
                        project_description.setText(project.getProject_description());
                        video_id = project.getVideo_id();
                        play_video();

                        //project_name.setText(document.getString("project_name"));
                        //project_description.setText(document.getString("project_description"));

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

    public void play_video(){
        ButterKnife.bind(this);
        onInitializedListener = new YouTubePlayer.OnInitializedListener()
        {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b)
            {
                youTubePlayer.loadVideo(video_id);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        };
        youtube_player_view.initialize(YOUTUBE_API_KEY,onInitializedListener);
    }

}