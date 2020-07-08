package com.example.login_test.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
//youtube player shows error, but still plays
import com.example.login_test.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrendingActivity extends YouTubeBaseActivity
{
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String url = "https://img.youtube.com/vi/";
    String image = "/0.jpg";
    DisplayMetrics displayMetrics = new DisplayMetrics();

    @BindView(R.id.youtube_player_view)
    YouTubePlayerView youtube_player_view;

    YouTubePlayer.OnInitializedListener onInitializedListener;

    String video_id = "dQw4w9WgXcQ";
    private static String YOUTUBE_API_KEY = "AIzaSyAQL8_xEae7uQ-8Mdfzj9Wvs7Kb1aVb7SU";
    //youtube player to play video when new video selected
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);
        ButterKnife.bind(this);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_keyboard_arrow_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

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

    /*private void initializeYoutubePlayer()
    {
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_fragment, youTubePlayerFragment);
        transaction.commit();
        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                boolean wasRestored) {

                if (!wasRestored) {
                    youTubePlayer = player;


                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    //cue the 1st video by default
                    youTubePlayer.loadVideo(video_id);
                }

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                //print or show error if initialization failed
                //Log.e(TAG, "Youtube Player View initialization failed");
            }
        });
    }*/

    /*public class VideoRecycler extends RecyclerView.Adapter<VideoRecycler.MyViewHolders> {

        private Context mContext;
        private List<String> video_list;

        String url = "https://img.youtube.com/vi/";
        String image = "/0.jpg";
        DisplayMetrics displayMetrics = new DisplayMetrics();

        public VideoRecycler(Context mContext, List<String> video)
        {
            this.mContext = mContext;
            this.video_list = video;
        }

        public class MyViewHolders extends RecyclerView.ViewHolder {

            public ImageView imageViewItem, btnYoutube_player;

            YouTubePlayerView youtube_player_view;

            public MyViewHolders(View view) {
                super(view);

                imageViewItem = (ImageView) view.findViewById(R.id.imageViewItem);
                btnYoutube_player = (ImageView) view.findViewById(R.id.btnYoutube_player);
                youtube_player_view = view.findViewById(R.id.youtube_player_view);
            }
        }

        @Override
        public VideoRecycler.MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.youtube_player_view, parent, false);

            return new VideoRecycler.MyViewHolders(itemView);
        }

        @Override
        public void onBindViewHolder(final VideoRecycler.MyViewHolders holder, final int position) {
            ((Activity) holder.itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;


            Glide.with(holder.itemView.getContext())
                    .load(url + video_list.get(position) + image).
                    apply(new RequestOptions().override(width - 36, 200))
                    .into(holder.imageViewItem);

            Log.d("myTag", url + video_list + image);

            //  Log.d("myTag", "This is my message");

            holder.imageViewItem.setVisibility(View.VISIBLE);
            holder.btnYoutube_player.setVisibility(View.VISIBLE);
            holder.youtube_player_view.setVisibility(View.GONE);

            holder.btnYoutube_player.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    holder.imageViewItem.setVisibility(View.GONE);
                    holder.youtube_player_view.setVisibility(View.VISIBLE);
                    holder.btnYoutube_player.setVisibility(View.GONE);
                    holder.youtube_player_view.initialize(new YouTubePlayerInitListener() {
                        @Override
                        public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
                            initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                                @Override
                                public void onReady() {
                                    initializedYouTubePlayer.loadVideo(video_list.get(position), 0);
                                    initializedYouTubePlayer_adpter = initializedYouTubePlayer;
                                }
                            });
                        }
                    }, true);
                }
            });
            youtube_player_view_adpter= holder.youtube_player_view;
        }
        public void video_pause()
        {
            if (initializedYouTubePlayer_adpter!= null)
            {
                initializedYouTubePlayer_adpter.pause();
            }

        }
        public void video_play()
        {
            if (initializedYouTubePlayer_adpter!= null)
            {
                initializedYouTubePlayer_adpter.play();
            }
        }

        @Override
        public int getItemCount() {
            return video_list.size();
        }
    }*/
}