package com.example.newfestivalpost.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.newfestivalpost.Activities.ActivityMyPostSingleView;
import com.example.newfestivalpost.R;
import com.example.newfestivalpost.Utills.Constance;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;


public class AdapterMyPostList extends RecyclerView.Adapter<AdapterMyPostList.ViewHolder> {
    private Context context;
    private ArrayList<File> fileArrayList;
    String type;

    public AdapterMyPostList(Context context, ArrayList<File> files,String type) {
        this.context = context;
        this.fileArrayList = files;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_rv_mypostlist, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        File fileItem = fileArrayList.get(position);
        final String urlpath = fileItem.getAbsolutePath();
        final String filename = fileItem.getName();
        Glide.with(context)
                .load(urlpath)
                .into(viewHolder.iv_mypostimg);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type.equals("image"))
                {
                    Constance.mypostImageList=fileArrayList;
                }
                else {
                    Constance.mypostVideoList=fileArrayList;
                }
                Intent i=new Intent(context, ActivityMyPostSingleView.class);
                i.putExtra("mypostposition",position);
                i.putExtra("type",type);
                context.startActivity(i);
            }
        });

    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Application not found to open this file", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_mypostimg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_mypostimg = itemView.findViewById(R.id.iv_mypostimg);



        }
    }


}