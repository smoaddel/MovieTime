package io.saeed.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class ImageAdapter extends ArrayAdapter {

    private Context mContext;
    private ArrayList<Map<String,String>> images;
//    private static final String baseURL = "http://image.tmdb.org/t/p/w342";
    private static final String MDB_POSTER = "poster_path";

    public ImageAdapter(Context context, ArrayList<Map<String,String>> images){
        super(context, 0, images);
        mContext = context;
        this.images = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else{
            imageView = (ImageView)convertView;
        }

        String poster = images.get(position).get(MDB_POSTER);
        Picasso.with(mContext).load(poster).into(imageView);
        return imageView;

    }
}