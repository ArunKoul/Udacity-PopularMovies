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

import com.squareup.picasso.Picasso;
import com.udacity.assigmment.popularmovies.popularmovies.R;
import com.udacity.assigmment.popularmovies.popularmovies.data.MovieTrailerColumns;
import com.udacity.assigmment.popularmovies.popularmovies.ui.fragment.DetailFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by arunkoul on 14/04/16.
 */
public class MovieTrailerCursorAdapter extends CursorRecyclerViewAdapter<MovieTrailerCursorAdapter.ViewHolder> {

    Context mContext;
    ViewHolder mVh;

    private Cursor mCursor;

    private DetailFragment.OnMovieTrailerSelectedListener mMovieTrailerSelectedListener;

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String trialerVideoId = (String) view.getTag();
            mMovieTrailerSelectedListener.onMovieTrialerSelected(trialerVideoId);
        }
    };

    public MovieTrailerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        mCursor = cursor;


        try {
            mMovieTrailerSelectedListener = (DetailFragment.OnMovieTrailerSelectedListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + " must implement OnMovieTrailerSelectedListener");
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_view_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        mVh = vh;
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);
        mCursor = cursor;

        viewHolder.mMovieTrailerName.setText(cursor.getString(cursor.getColumnIndex(MovieTrailerColumns.NAME)));

        String posterPath = "http://img.youtube.com/vi/" + cursor.getString(cursor.getColumnIndex(MovieTrailerColumns.KEY)) + "/0.jpg";

        Picasso.with(mContext)
                .load(posterPath)
                .placeholder(R.color.colorPrimary)
                .into(viewHolder.mMovieTrailerImageView);

        viewHolder.mMovieTrailerName.setTag(cursor.getString(cursor.getColumnIndex(MovieTrailerColumns.KEY)));
        viewHolder.mMovieTrailerImageView.setTag(cursor.getString(cursor.getColumnIndex(MovieTrailerColumns.KEY)));

        viewHolder.mMovieTrailerName.setOnClickListener(clickListener);
        viewHolder.mMovieTrailerImageView.setOnClickListener(clickListener);


    }

    public Cursor getCursor() {
        return mCursor;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.trailer_item_title)
        TextView mMovieTrailerName;
        @Bind(R.id.trailer_item_thumbnail)
        ImageView mMovieTrailerImageView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
