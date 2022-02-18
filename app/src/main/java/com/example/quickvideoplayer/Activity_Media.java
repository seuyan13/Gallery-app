package com.example.quickvideoplayer;

/* Assignment 3
 * Mobile Application Development 159336
 * Due date = Monday, 1 November 2021, 11:55 PM
 *
 * Student name : Seungwoon Yang
 * Student ID - 21008279
 *

 * <Description>
 * Gallery app (load the files that saved in the mobile device)
 */

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Activity_Media extends AppCompatActivity {

    private MenuItem videoScreen;
    private MenuItem imageScreen;
    private MenuItem favoriteScreen;
    private AdapterMediaList adapterMediaList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        initializeViews();

        //Check permission.
        //if permission is not granted, ask the permission, else display the gallery app
        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
        {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else {
            loadMedia(0);
        }
    }

    //Call the variables in the menu_screen.xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_screen, menu);
        videoScreen = menu.findItem(R.id.video_screen);
        imageScreen = menu.findItem(R.id.image_screen);
        favoriteScreen = menu.findItem(R.id.favorite_screen);

        return true;
    }

    //Check the options (display chosen option among only image, video, or favorite)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.video_screen:
                setScreenType(0);
                break;

            case R.id.image_screen:
                setScreenType(1);
                break;

            case R.id.favorite_screen:
                setScreenType(2);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Call the variables in the activity_media.xml
    private void initializeViews() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView_videos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); //display 2 medias in the one row
        adapterMediaList = new AdapterMediaList(this);
        recyclerView.setAdapter(adapterMediaList);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean granted = true;

        //check the permission result. only true or false
        for(int result: grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                granted = false;
            }
        }

        //if granted has true value it starts otherwise, display "permission deined" message
        if(granted)
        {
            loadMedia(0);
        }
        else
            Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT);
    }

    /**
     * @param screenType 0: video, 1: image, 2: favorite
     */
    private void setScreenType(int screenType) {

        //if all the screebTypes haven't been checked, set videoScreen is true
        if (videoScreen == null || imageScreen == null || favoriteScreen == null) {
            videoScreen.setChecked(true);
            imageScreen.setChecked(false);
            favoriteScreen.setChecked(false);
            return;
        }

        //Chose screenType
        switch (screenType) {
            case 0:
                videoScreen.setChecked(true);
                imageScreen.setChecked(false);
                favoriteScreen.setChecked(false);
                break;

            case 1:
                videoScreen.setChecked(false);
                imageScreen.setChecked(true);
                favoriteScreen.setChecked(false);
                break;

            case 2:
                videoScreen.setChecked(false);
                imageScreen.setChecked(false);
                favoriteScreen.setChecked(true);
                break;
        }

        loadMedia(screenType);
    }

    //Display the media
    private void loadMedia(final int screenType) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String[] projection = {MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME, MediaStore.Files.FileColumns.MEDIA_TYPE};
                String selection = null;
                switch (screenType) {

                    //Video
                    case 0:
                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                        break;

                   //Image
                    case 1:
                        selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                        break;

                    //Favorite
                    case 2:
                        selection = FavoriteSharedPreferences.getInstance(Activity_Media.this).getFavoriteSelection();
                        break;
                }

                //Display the files by date added (order by desc)
                String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
                Uri contentUri = MediaStore.Files.getContentUri("external");
                Cursor cursor = getApplication().getContentResolver().query(contentUri, projection, selection, null, sortOrder);

                //if cursor is not empty (= if data is not empty), get data of id, display name, media type
                if (cursor != null) {
                    int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
                    int titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    int mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);

                    //Create an arrayList
                    final ArrayList<ModelMedia> mediaList = new ArrayList<>();

                    //Each moveToNext searches and looks for the next match
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(idColumn);
                        String title = cursor.getString(titleColumn);
                        String mediaType = cursor.getString(mediaTypeColumn);
                        String duration_formatted = "";

                        Uri data = ContentUris.withAppendedId(contentUri, id);

                        final ModelMedia media = new ModelMedia(id, mediaType, data, title);

                        //check if MediaType is video then retrieving fame & meta data from video
                        if (media.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(Activity_Media.this, data);

                            try {
                                int duration, sec, minute, hour;

                                //Duration of video with hours:minute:second
                                duration = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                                sec = (duration / 1000) % 60;
                                minute = (duration / (1000 * 60)) % 60;
                                hour = duration / (1000 * 60 * 60);


                                //If hrs is 0, display the duration with minutes:seconds
                                //Set displaying decimal umber with zeros upto two digits
                                //Using UK number format
                                if (hour == 0) {
                                    duration_formatted = String.valueOf(minute).concat(":".concat(String.format(Locale.UK, "%02d", sec)));
                                } else {
                                    duration_formatted = String.valueOf(hour).concat(":".concat(String.format(Locale.UK, "%02d", minute).concat(":".concat(String.format(Locale.UK, "%02d", sec)))));
                                }
                                media.setDuration(duration_formatted);

                            }
                            catch (Exception ignore) { }
                        }
                        //add to mediaList
                        mediaList.add(media);
                    }

                    cursor.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterMediaList.mediaList = mediaList;
                            adapterMediaList.notifyDataSetChanged();
                        }
                    });
                }

            }
        }
        .start();
    }
}
