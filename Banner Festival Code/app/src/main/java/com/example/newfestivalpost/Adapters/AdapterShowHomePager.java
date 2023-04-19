package com.example.newfestivalpost.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.newfestivalpost.ModelRetrofit.VideoHomeData;
import com.example.newfestivalpost.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterShowHomePager extends PagerAdapter {

    private ArrayList<VideoHomeData> images;
    private LayoutInflater inflater;
    private Context context;

    public AdapterShowHomePager(Context context, ArrayList<VideoHomeData> images) {
        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View myImageLayout = inflater.inflate(R.layout.item_showhome_pager, container, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.imageslide);


        Log.e("kkkkkkkk", "instantiateItem: "+images.get(position).getImage_url() );
        Glide.with(context).load(images.get(position).getImage_url()).into(myImage);

        container.addView(myImageLayout, 0);
        return myImageLayout;    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View) object);
    }
}
