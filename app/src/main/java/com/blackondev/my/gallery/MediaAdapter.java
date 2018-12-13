package com.blackondev.my.gallery;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

class MediaAdapter extends RecyclerView.Adapter<Holder> {
    private final Context ctx;
    private final ArrayList<MediaModel> mediaModels;
    Picasso picasso;
    private MainActivity.ImageSelectionListener imageSelectionListener;

    public MediaAdapter(Context ctx, ArrayList<MediaModel> mediaModels, MainActivity.ImageSelectionListener imageSelectionListener) {
        this.ctx = ctx;
        this.mediaModels = mediaModels;
        picasso = Picasso.with(ctx);

        this.imageSelectionListener = imageSelectionListener;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {

        final MediaModel mediaModel = mediaModels.get(position);
        Uri uri = Uri.parse(mediaModel.get_data());
        picasso.load("file:///" + mediaModel.get_data()).resize(640, 480).onlyScaleDown().centerCrop().into(holder.imageView);
        Log.d("FILE PATH", mediaModel.get_data());
        holder.fileName.setText(mediaModel.get_display_name());
        holder.folderName.setText(mediaModel.getBucket_display_name());
        holder.checkbox.setChecked(mediaModel.isChecked());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageSelectionListener != null) {
                    boolean checked = imageSelectionListener.onImageSelected(position, !holder.checkbox.isChecked());
                    holder.checkbox.setChecked(checked);
                    mediaModel.setChecked(checked);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mediaModels.size();
    }
}
