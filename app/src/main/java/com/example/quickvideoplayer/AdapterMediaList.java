package com.example.quickvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterMediaList extends RecyclerView.Adapter<AdapterMediaList.MyViewHolder> {

    //Create the arrayList & declaration
    ArrayList<ModelMedia> mediaList = new ArrayList<ModelMedia>();
    Context context;

    //Constructor
    AdapterMediaList(Context context) {
        this.context = context;
    }

    //* LayoutFlater => Instance the layout XML file to the corresponding View object
    @NonNull
    @Override
    public AdapterMediaList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_media, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMediaList.MyViewHolder holder, int position) {
        final ModelMedia item = mediaList.get(position);
        holder.tv_title.setText(item.getTitle());
        holder.tv_duration.setText(item.getDuration());
        Glide.with(context).load(item.getData()).into(holder.imageView);

        //Check the media type and get the id of media (video or image)
        if (item.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Activity_Video_Player.class);
                    intent.putExtra("videoId", item.getId());
                    v.getContext().startActivity(intent);
                }
            });
        } else if (item.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Activity_Photo_View.class);
                    intent.putExtra("photoId", item.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView tv_title, tv_duration;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            imageView = itemView.findViewById(R.id.imageView_thumbnail);
        }
    }
}
