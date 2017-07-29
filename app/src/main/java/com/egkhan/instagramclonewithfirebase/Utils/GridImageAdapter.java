package com.egkhan.instagramclonewithfirebase.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.egkhan.instagramclonewithfirebase.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by EgK on 7/29/2017.
 */

public class GridImageAdapter extends ArrayAdapter<String> {
    Context context;
    LayoutInflater layoutInflater;
    int layoutResource;
    String append;
    ArrayList<String> imageURLs;

    public GridImageAdapter(Context context, int layoutResource, String append, ArrayList<String> imageURLs) {
        super(context,layoutResource,imageURLs);
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutResource = layoutResource;
        this.append = append;
        this.imageURLs = imageURLs;
    }

    static class ViewHolder
    {
        SquareImageView image;
        ProgressBar progressBar;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(layoutResource,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.progressBar = (ProgressBar)convertView.findViewById(R.id.gridImageProgressBar);
            viewHolder.image = (SquareImageView)convertView.findViewById(R.id.gridImageView);

            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String imageUrl = getItem(position);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imageUrl, viewHolder.image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                if(viewHolder.progressBar!= null)
                {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                if(viewHolder.progressBar!= null)
                {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if(viewHolder.progressBar!= null)
                {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                if(viewHolder.progressBar!= null)
                {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }
            }
        });
        return convertView;
    }
}
