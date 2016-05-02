package com.udacity.assigmment.popularmovies.popularmovies.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.assigmment.popularmovies.popularmovies.R;
import com.udacity.assigmment.popularmovies.popularmovies.data.MovieReviewColumns;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by arunkoul on 14/04/16.
 */
public class MovieReviewCursorAdapter extends CursorRecyclerViewAdapter<MovieReviewCursorAdapter.ViewHolder> {

    Context mContext;
    ViewHolder mVh;

    public MovieReviewCursorAdapter(Context context, Cursor cursor) {
        super(context, null);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_review_view_item, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        mVh = vh;
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);

        viewHolder.mMovieReviewContent.setText(cursor.getString(cursor.getColumnIndex(MovieReviewColumns.CONTENT)));
        viewHolder.mMovieReviewContentAuthor.setText(mContext.getString(R.string.author) + cursor.getString(cursor.getColumnIndex(MovieReviewColumns.AUTHOR)));

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_review_content)
        TextView mMovieReviewContent;
        @Bind(R.id.tv_review_author)
        TextView mMovieReviewContentAuthor;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}
