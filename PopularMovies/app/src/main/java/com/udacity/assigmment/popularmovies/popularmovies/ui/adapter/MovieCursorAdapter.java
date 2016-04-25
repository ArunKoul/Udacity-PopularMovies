package com.udacity.assigmment.popularmovies.popularmovies.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.assigmment.popularmovies.popularmovies.R;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieData;
import com.udacity.assigmment.popularmovies.popularmovies.data.MovieColumns;
import com.udacity.assigmment.popularmovies.popularmovies.ui.fragment.MovieFragment;
import com.udacity.assigmment.popularmovies.popularmovies.util.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by arunkoul on 14/04/16.
 */
public class MovieCursorAdapter extends CursorRecyclerViewAdapter<MovieCursorAdapter.ViewHolder> {

    Context mContext;
    ViewHolder mVh;

    private MovieFragment.OnMovieSelectedListener mOnMovieSelectedListenerCallback;
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MovieData movieData = (MovieData) view.getTag();
            Toast.makeText(mContext, movieData.getTitle(), Toast.LENGTH_SHORT).show();
            mOnMovieSelectedListenerCallback.onMovieSelected(movieData, false);
        }
    };

    public MovieCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;

        try {
            mOnMovieSelectedListenerCallback = (MovieFragment.OnMovieSelectedListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement OnMovieSelectedListener");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        mVh = vh;
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);

        viewHolder.mTextView.setText(cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE)));


        String posterPath = cursor.getString(cursor.getColumnIndex(MovieColumns.POSTER_PATH));
        if (posterPath == null) {
            posterPath = cursor.getString(cursor.getColumnIndex(MovieColumns.BACKDROP_PATH));
        }

        Picasso.with(mContext)
                .load(Constant.THE_MOVIE_IMAGE_BASE_URL + posterPath)
                .placeholder(R.color.colorPrimary)
                .into(viewHolder.mImageView);


        viewHolder.mTextView.setOnClickListener(clickListener);
        viewHolder.mImageView.setOnClickListener(clickListener);

        MovieData movieData = new MovieData();
        movieData.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieColumns.POSTER_PATH)));
        movieData.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieColumns.BACKDROP_PATH)));
        movieData.setTitle(cursor.getString(cursor.getColumnIndex(MovieColumns.TITLE)));
        movieData.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieColumns.ORIGINAL_TITLE)));
        movieData.setOriginalLanguage(cursor.getString(cursor.getColumnIndex(MovieColumns.ORIGINAL_LANGUAGE)));
        movieData.setOverview(cursor.getString(cursor.getColumnIndex(MovieColumns.OVERVIEW)));
        movieData.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieColumns.RELEASE_DATE)));
        movieData.setPopularity(cursor.getString(cursor.getColumnIndex(MovieColumns.POPULARITY)));
        movieData.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(MovieColumns.VOTE_AVERAGE)));
        movieData.setVoteCount(cursor.getInt(cursor.getColumnIndex(MovieColumns.VOTE_COUNT)));
        movieData.setId(cursor.getLong(cursor.getColumnIndex(MovieColumns._ID)));

        viewHolder.mImageView.setTag(movieData);
        viewHolder.mTextView.setTag(movieData);


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_title)
        TextView mTextView;
        @Bind(R.id.movie_thumbnail)
        ImageView mImageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

}
