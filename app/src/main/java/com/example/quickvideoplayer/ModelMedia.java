package com.example.quickvideoplayer;

import android.net.Uri;
import android.provider.MediaStore;

public class ModelMedia {

    long id;
    String mediaType;
    Uri data;
    String title, duration;


    //Constructor
    public ModelMedia(long id, String mediaType, Uri data, String title) {
        this.id = id;
        this.mediaType = mediaType;
        this.data = data;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Uri getData() {
        return data;
    }

    public void setData(Uri data) {
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    //Check media type (image or video)
    public int getMediaType() {
        if (mediaType.equals("" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE))
        {
            return MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        }
        else if (mediaType.equals("" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO))
        {
            return MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        }
        else
            {
                //do nothing
                return -1;
            }
    }
}
