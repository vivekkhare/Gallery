package com.blackondev.my.gallery;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

class Holder extends RecyclerView.ViewHolder {
    ImageView imageView;
    TextView fileName,folderName;
    CheckBox checkbox;
    public Holder(View itemView) {
        super(itemView);
        imageView= itemView.findViewById(R.id.img);
        fileName= itemView.findViewById(R.id.txtImgFileName);
        folderName= itemView.findViewById(R.id.txtImgFolderName);
        checkbox= itemView.findViewById(R.id.checkbox);

    }
}
