package com.example.quickvideoplayer;

import android.content.ContentUris;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class Activity_Video_Player extends AppCompatActivity {

    private long videoId;

    private MenuItem favoriteMenuItem;
    private MenuItem[] speedMenuItems;

    private PlayerView playerView;
    private SimpleExoPlayer player;

    private FavoriteSharedPreferences favoriteSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for full screen
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI();
        }

        setContentView(R.layout.activity_video_player);
        videoId = getIntent().getExtras().getLong("videoId");
        favoriteSharedPreferences = FavoriteSharedPreferences.getInstance(this);
        initializeViews();
    }

    private int getOrientation() {
        return getResources().getConfiguration().orientation;
    }


    // * set & getSupportActionBar => sets the toolbar as the app bar for the activity
    private void initializeViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playerView = findViewById(R.id.playerView);
    }

    private void initializePlayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setControllerAutoShow(getOrientation() == Configuration.ORIENTATION_LANDSCAPE);

        //Check the otientation and display or hide the play back controlls
        if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
            playerView.showController();

        } else {
            playerView.hideController();
        }

        Uri videoUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), videoId);
        MediaSource mediaSource = buildMediaSource(videoUri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
    }

    //Build data source
    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }


    //Refer each options ( favorite, full screen and video play speed)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_video_player, menu);

        favoriteMenuItem = menu.findItem(R.id.favorite_unchecked);
        setFavorite(favoriteSharedPreferences.getFavorite(videoId));

        //if screen is full screen, change the full screen icon to exit full screen icon.
        MenuItem fullScreen = menu.findItem(R.id.fullscreen);
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            fullScreen.setIcon(R.drawable.ic_fullscreen_exit);
        }

        MenuItem speed_025MenuItem = menu.findItem(R.id.speed_025);
        MenuItem speed_05MenuItem = menu.findItem(R.id.speed_05);
        MenuItem speed_normalMenuItem = menu.findItem(R.id.speed_normal);
        MenuItem speed_125MenuItem = menu.findItem(R.id.speed_125);
        MenuItem speed_15MenuItem = menu.findItem(R.id.speed_15);
        MenuItem speed_2MenuItem = menu.findItem(R.id.speed_2);
        speedMenuItems = new MenuItem[]{speed_025MenuItem, speed_05MenuItem, speed_normalMenuItem, speed_125MenuItem, speed_15MenuItem, speed_2MenuItem};

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.favorite_unchecked:
                setFavorite(!favoriteMenuItem.isChecked());
                break;

                //video screen (check is the curren screen is portrait and changes to landscape or portrait when button clicked)
            case R.id.fullscreen:
                if (getOrientation() == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;

                //video play speed
            case R.id.speed_025:
                setSpeed(0);
                break;

            case R.id.speed_05:
                setSpeed(1);
                break;

            case R.id.speed_normal:
                setSpeed(2);
                break;

            case R.id.speed_125:
                setSpeed(3);
                break;

            case R.id.speed_15:
                setSpeed(4);
                break;

            case R.id.speed_2:
                setSpeed(5);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Full screen & normal screen
    @Override
    public void onBackPressed() {
        if (getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else {
            super.onBackPressed();
        }
    }

    //Favorite checked & unchecked
    private void setFavorite(boolean isEnabled) {
        favoriteSharedPreferences.setFavorite(videoId, isEnabled);

        if (isEnabled) {
            favoriteMenuItem.setChecked(true);
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_checked);

        } else {
            favoriteMenuItem.setChecked(false);
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_unchecked);
        }
    }

    // *onWindowFocusChanged => used to enable the full screen mode
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && getOrientation() == Configuration.ORIENTATION_LANDSCAPE) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // *DecorView is the view that actually holds the window’s background drawable.
        View decorView = getWindow().getDecorView();
    }

    //set video play speed option among 0.25 X, 0.5 X, normal, 1.25 X, 1.5 X, or 2X
    //default video play speed is 1 X
    private void setSpeed(int index) {
        float speed;
        switch (index) {
            case 0:
                speed = 0.25f;
                break;

            case 1:
                speed = 0.5f;
                break;

            case 3:
                speed = 1.25f;
                break;

            case 4:
                speed = 1.5f;
                break;

            case 5:
                speed = 2f;
                break;

            default:
                speed = 1f;
                break;
        }
        player.setPlaybackParameters(new PlaybackParameters(speed));

        //Check the video play speed and change it if it's been changed.
        for (int i = 0; i < speedMenuItems.length; i++) {
            speedMenuItems[i].setChecked(i == index);
        }
    }

    //Check the sdk version, and display the app
    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 28) {
            initializePlayer();
        }
    }

    //Check the sdk version or player has empty value, and interaction with user
    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT < 28 || player == null) {
            initializePlayer();
        }
    }

    //Check the sdk version and call the onPause when other activity opens
    @Override
    protected void onPause() {
        if (Util.SDK_INT < 28) {
            releasePlayer();
        }
        super.onPause();
    }

    //Check the sdk version and call the onStop() when moved to other activity
    @Override
    protected void onStop() {
        if (Util.SDK_INT >= 28) {
            releasePlayer();
        }
        super.onStop();
    }
}
