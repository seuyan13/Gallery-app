package com.example.quickvideoplayer;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;

public class Activity_Photo_View extends AppCompatActivity {

    private long photoId;
    private MenuItem favoriteMenuItem;
    private FavoriteSharedPreferences favoriteSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        photoId = getIntent().getExtras().getLong("photoId");
        favoriteSharedPreferences = FavoriteSharedPreferences.getInstance(this);
        initializeViews();
    }

    private void initializeViews() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PhotoView photoView = findViewById(R.id.photoView);

        Uri photoUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), photoId);
        photoView.setImageURI(photoUri);
    }

    //Options of image file (favorite, back to home) - create the option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_photo_view, menu);
        favoriteMenuItem = menu.findItem(R.id.favorite_unchecked);
        setFavorite(favoriteSharedPreferences.getFavorite(photoId));

        return true;
    }

    //Options of image file (favorite, back to home) - make to work the function of the options
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.favorite_unchecked:
                setFavorite(!favoriteMenuItem.isChecked());
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //check if the favorite is checked or unchecked and change the icon of favorite
    private void setFavorite(boolean isEnabled) {
        favoriteSharedPreferences.setFavorite(photoId, isEnabled);

        if (isEnabled) {
            favoriteMenuItem.setChecked(true);
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_checked);

        } else {
            favoriteMenuItem.setChecked(false);
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_unchecked);
        }
    }
}
