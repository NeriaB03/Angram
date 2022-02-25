package com.example.angram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private final List<String> images;
    private final int numberOfImages;

    public GridAdapter(Context c, List<String> images, int numberOfImages) {
        mContext = c;
        this.images = images;
        this.numberOfImages = numberOfImages;
    }

    @Override
    public int getCount() {
        return images != null ? images.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_images, null);
            if(this.numberOfImages > 0) {
                ImageView imageView = (ImageView) grid.findViewById(R.id.imagesGridLayout);
                Picasso.get().load(images.get(position)).into(imageView);
            }
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
