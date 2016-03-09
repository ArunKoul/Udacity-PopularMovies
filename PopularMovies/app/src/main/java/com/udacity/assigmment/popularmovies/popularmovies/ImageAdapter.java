package com.udacity.assigmment.popularmovies.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arunkoul on 07/03/16.
 */
class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private List<MovieData> mMovieDataList;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        if(mMovieDataList != null) {
            return mMovieDataList.size();
        }

        return 0;
    }

    public Object getItem(int position) {

        if(mMovieDataList != null) {
            return mMovieDataList.get(position);
        }

        return null;
    }

    public long getItemId(int position) {
        if(mMovieDataList != null) {
            return mMovieDataList.get(position).id;
        }

        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {

            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.movie_image_width),
                    mContext.getResources().getDimensionPixelSize(R.dimen.movie_image_height)));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(
                    mContext.getResources().getDimensionPixelSize(R.dimen.grid_image_padding),
                    mContext.getResources().getDimensionPixelSize(R.dimen.grid_image_padding),
                    mContext.getResources().getDimensionPixelSize(R.dimen.grid_image_padding),
                    mContext.getResources().getDimensionPixelSize(R.dimen.grid_image_padding));

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setTag(mMovieDataList.get(position));


        String posterPath = mMovieDataList.get(position).poster_path;
        if(posterPath == null) {
            posterPath = mMovieDataList.get(position).backdrop_path;
        }

        Picasso.with(mContext)
                .load(Constant.THE_MOVIE_IMAGE_BASE_URL + posterPath)
                .placeholder(R.color.colorAccent)
                .into(imageView);

        return imageView;
    }

    public void setData(List<MovieData> data) {

        if(mMovieDataList != null && !mMovieDataList.isEmpty()){
            mMovieDataList.clear();
        } else {
            mMovieDataList = new ArrayList<>();
        }

        mMovieDataList.addAll(data);
        notifyDataSetChanged();
    }
}
