package com.udacity.assigmment.popularmovies.popularmovies.ui.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.assigmment.popularmovies.popularmovies.BuildConfig;
import com.udacity.assigmment.popularmovies.popularmovies.R;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieData;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieReview;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieReviewContract;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieTrailerVideoContract;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieTrialerVideo;
import com.udacity.assigmment.popularmovies.popularmovies.data.FavoriteMovieColumns;
import com.udacity.assigmment.popularmovies.popularmovies.data.MovieReviewColumns;
import com.udacity.assigmment.popularmovies.popularmovies.data.MovieTrailerColumns;
import com.udacity.assigmment.popularmovies.popularmovies.data.PopularMovieProvider;
import com.udacity.assigmment.popularmovies.popularmovies.ui.adapter.MovieReviewCursorAdapter;
import com.udacity.assigmment.popularmovies.popularmovies.ui.adapter.MovieTrailerCursorAdapter;
import com.udacity.assigmment.popularmovies.popularmovies.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REVIEW_CURSOR_LOADER_ID = 0;
    private static final int TRAILER_CURSOR_LOADER_ID = 1;
    private static final String LOG_TAG = DetailFragment.class.getName();
    @Bind(R.id.loading_progressbar_layout)
    LinearLayout mProgressbarLayout;
    @Bind(R.id.loading_progressbar_msg)
    TextView mProgressbarMsgTextView;
    @Bind(R.id.btn_movie_favorite)
    Button btnFavorite;
    @Bind(R.id.tv_movie_name)
    TextView tvMovieName;
    @Bind(R.id.tv_movie_release_year)
    TextView tvMovieReleaseDate;
    @Bind(R.id.tv_movie_duration)
    TextView tvMovieDuration;
    @Bind(R.id.tv_movie_vote_average)
    TextView tvMovieRating;
    @Bind(R.id.tv_movie_overview)
    TextView tvMovieOverview;
    @Bind(R.id.iv_movie_poster)
    ImageView ivMoviePoster;
    @Bind(R.id.tv_movie_trailer_label)
    TextView tvMovieTrailerLable;
    @Bind(R.id.tv_movie_review_label)
    TextView tvMovieReviewLable;
    @Bind(R.id.movie_trailer_recycler_view)
    RecyclerView mMovieTrailerRecyclerView;
    @Bind(R.id.movie_review_recycler_view)
    RecyclerView mMovieReviewRecyclerView;
    boolean isMovieReviewLoading;
    boolean isMovieTrailerLoading;
    private MovieData mMovieData;
    private MovieTrailerCursorAdapter mMovieTrailerCursorAdapter;
    private Subscription mMovieTrailerSubscription;
    private MovieReviewCursorAdapter mMovieReviewCursorAdapter;
    private Subscription mMovieReviewSubscription;

    public DetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            mMovieData = (MovieData) savedInstanceState.getSerializable(Constant.BUNDLE_ARG_DATA);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                mMovieData = (MovieData) args.getSerializable(Constant.BUNDLE_ARG_DATA);
            }
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        //-- Movie Trailer--//
        mMovieTrailerRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mMovieTrailerLayoutManager = new LinearLayoutManager(getActivity());
        mMovieTrailerRecyclerView.setLayoutManager(mMovieTrailerLayoutManager);

        mMovieTrailerCursorAdapter = new MovieTrailerCursorAdapter(getActivity(), null);
        mMovieTrailerRecyclerView.setAdapter(mMovieTrailerCursorAdapter);


        //-- Movie Review--//
        mMovieReviewRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mMovieReviewLayoutManager = new LinearLayoutManager(getActivity());
        mMovieReviewRecyclerView.setLayoutManager(mMovieReviewLayoutManager);

        mMovieReviewCursorAdapter = new MovieReviewCursorAdapter(getActivity(), null);
        mMovieReviewRecyclerView.setAdapter(mMovieReviewCursorAdapter);

        setFavoriteBtnLable(isMovieFavorite());

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            mMovieData = (MovieData) args.getSerializable(Constant.BUNDLE_ARG_DATA);
        }

        updateMovieDetailView(mMovieData);


    }

    private void getMovieReviews() {

        if (mMovieData != null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.THE_MOVIE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            Constant.PopularMoviesEndpointInterface apiService = retrofit.create(Constant.PopularMoviesEndpointInterface.class);
            final Observable<MovieReviewContract> moviesReviews = apiService.getMoviesReviews(mMovieData.getId(), BuildConfig.POPULAR_MOVIE_API_KEY);

            mMovieReviewSubscription = moviesReviews
                    //.subscribeOn(Schedulers.newThread())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MovieReviewContract>() {

                        @Override
                        public void onStart() {
                            super.onStart();

                            tvMovieReviewLable.setVisibility(View.GONE);

                            isMovieReviewLoading = false;

                            if (isMovieTrailerLoading) {
                                mProgressbarMsgTextView.setText("Loading Movie Trailer and Reviews.\nPlease wait...");

                            } else {
                                mProgressbarMsgTextView.setText("Loading Movie Reviews.\nPlease wait...");
                            }
                        }

                        @Override
                        public void onCompleted() {

                            isMovieReviewLoading = false;

                            if (isMovieTrailerLoading) {
                                mProgressbarMsgTextView.setText("Loading Movie Trailer.\nPlease wait...");

                            } else {
                                mProgressbarLayout.setVisibility(View.GONE);
                            }

                            getLoaderManager().restartLoader(REVIEW_CURSOR_LOADER_ID, null, DetailFragment.this);

                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e(LOG_TAG, "Error while retriving movies review", e);

                            isMovieReviewLoading = false;

                            if (isMovieTrailerLoading) {
                                mProgressbarMsgTextView.setText("Loading Movie Trailer.\nPlease wait...");

                            } else {
                                mProgressbarLayout.setVisibility(View.GONE);
                            }


                        }

                        @Override
                        public void onNext(MovieReviewContract movieReviewContract) {
                            if (movieReviewContract.getMovieReviews().isEmpty()) {
                                tvMovieReviewLable.setVisibility(View.GONE);

                            } else {
                                tvMovieReviewLable.setVisibility(View.VISIBLE);
                            }

                            insertMovieReviewData(movieReviewContract.getMovieReviews());

                        }
                    });
        }
    }

    private void getMovieTailers() {

        if (mMovieData != null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.THE_MOVIE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            Constant.PopularMoviesEndpointInterface apiService = retrofit.create(Constant.PopularMoviesEndpointInterface.class);
            final Observable<MovieTrailerVideoContract> moviesTrialers = apiService.getMoviesTrialers(mMovieData.getId(), BuildConfig.POPULAR_MOVIE_API_KEY);

            mMovieTrailerSubscription = moviesTrialers
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MovieTrailerVideoContract>() {

                        @Override
                        public void onStart() {
                            super.onStart();

                            tvMovieTrailerLable.setVisibility(View.GONE);

                            isMovieTrailerLoading = true;

                            if (isMovieReviewLoading) {
                                mProgressbarMsgTextView.setText("Loading Movie Trialer and Reviews.\nPlease wait...");

                            } else {
                                mProgressbarMsgTextView.setText("Loading Movie Trialer.\nPlease wait...");
                            }
                        }

                        @Override
                        public void onCompleted() {
                            isMovieTrailerLoading = false;

                            if (isMovieReviewLoading) {
                                mProgressbarMsgTextView.setText("Loading Movie Reviews.\nPlease wait...");

                            } else {
                                mProgressbarLayout.setVisibility(View.GONE);
                            }

                            getLoaderManager().restartLoader(TRAILER_CURSOR_LOADER_ID, null, DetailFragment.this);

                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.e(LOG_TAG, "Error while retriving movies trailer", e);

                            isMovieTrailerLoading = false;

                            if (isMovieReviewLoading) {
                                mProgressbarMsgTextView.setText("Loading Movie Reviews.\nPlease wait...");

                            } else {
                                mProgressbarLayout.setVisibility(View.GONE);
                            }

                        }

                        @Override
                        public void onNext(MovieTrailerVideoContract movieTrailerVideoContract) {
                            Log.d("LOGIC TEST", "Movie trialer size: " + movieTrailerVideoContract.getMovieTrialerVideos().size());

                            if (movieTrailerVideoContract.getMovieTrialerVideos().isEmpty()) {
                                tvMovieTrailerLable.setVisibility(View.GONE);

                            } else {
                                tvMovieTrailerLable.setVisibility(View.VISIBLE);
                            }

                            insertMovieTrailerData(movieTrailerVideoContract.getMovieTrialerVideos());

                        }
                    });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMovieReviewSubscription != null) {
            mMovieReviewSubscription.unsubscribe();
        }

        if (mMovieTrailerSubscription != null) {
            mMovieTrailerSubscription.unsubscribe();
        }

        ButterKnife.unbind(this);
    }

    public void updateMovieDetailView(MovieData movieData) {
        mMovieData = movieData;
        if (movieData != null) {
            tvMovieName.setText(movieData.getTitle());
            tvMovieReleaseDate.setText(movieData.getReleaseDate());
            tvMovieRating.setText(String.format(getString(R.string.rating_value), movieData.getVoteAverage()));
            tvMovieOverview.setText(movieData.getOverview());

            String posterPath = movieData.getPosterPath();
            if (posterPath == null) {
                posterPath = movieData.getBackdropPath();
            }

            Picasso.with(getActivity())
                    .load(Constant.THE_MOVIE_IMAGE_BASE_URL + posterPath)
                    .placeholder(R.color.colorPrimary)
                    .into(ivMoviePoster);

            setFavoriteBtnLable(isMovieFavorite());


            if (mMovieReviewSubscription != null) {
                mMovieReviewSubscription.unsubscribe();
                mMovieReviewSubscription = null;
            }

            if (mMovieTrailerSubscription != null) {
                mMovieTrailerSubscription.unsubscribe();
                mMovieTrailerSubscription = null;
            }

            getMovieTailers();
            getMovieReviews();

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constant.BUNDLE_ARG_DATA, mMovieData);
    }

    public void setData(MovieData data, boolean mIsTabLayout) {
        mMovieData = data;
        if (mIsTabLayout) {
            isMovieTrailerLoading = true;
            isMovieReviewLoading = true;

            getLoaderManager().restartLoader(REVIEW_CURSOR_LOADER_ID, null, this);
            getLoaderManager().restartLoader(TRAILER_CURSOR_LOADER_ID, null, this);
        }

        mProgressbarLayout.setVisibility(View.VISIBLE);
        updateMovieDetailView(mMovieData);
    }

    public void insertMovieReviewData(List<MovieReview> movieReviews) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(movieReviews.size());

        Uri uri;
        for (MovieReview movieReview : movieReviews) {
            ContentProviderOperation.Builder builder;


            uri = PopularMovieProvider.MovieReviews.CONTENT_URI;

            builder = ContentProviderOperation.newInsert(uri);

            builder.withValue(MovieReviewColumns._ID, movieReview.getId());
            builder.withValue(MovieReviewColumns.AUTHOR, movieReview.getAuthor());
            builder.withValue(MovieReviewColumns.CONTENT, movieReview.getContent());
            builder.withValue(MovieReviewColumns.MOVIE_ID, mMovieData.getId());
            builder.withValue(MovieReviewColumns.URL, movieReview.getUrl());

            batchOperations.add(builder.build());

            //-- Delete Old Record --//
            String where = MovieReviewColumns._ID + "='" + movieReview.getId() + "'";
            getActivity().getContentResolver().delete(uri, where, null);
        }

        try {
            getActivity().getContentResolver().applyBatch(PopularMovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("DetailFragment", "Error applying batch insert", e);
        }

    }

    public void insertMovieTrailerData(List<MovieTrialerVideo> movieTrialerVideos) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(movieTrialerVideos.size());

        Uri uri;
        for (MovieTrialerVideo movieTrialerVideo : movieTrialerVideos) {
            ContentProviderOperation.Builder builder;


            uri = PopularMovieProvider.MovieTrailers.CONTENT_URI;

            builder = ContentProviderOperation.newInsert(uri);

            builder.withValue(MovieTrailerColumns._ID, movieTrialerVideo.getId());
            builder.withValue(MovieTrailerColumns.KEY, movieTrialerVideo.getKey());
            builder.withValue(MovieTrailerColumns.NAME, movieTrialerVideo.getName());
            builder.withValue(MovieTrailerColumns.MOVIE_ID, mMovieData.getId());
            builder.withValue(MovieTrailerColumns.SITE, movieTrialerVideo.getSite());
            builder.withValue(MovieTrailerColumns.SIZE, movieTrialerVideo.getSize());
            builder.withValue(MovieTrailerColumns.TYPE, movieTrialerVideo.getType());

            batchOperations.add(builder.build());

            //-- Delete Old Record --//
            String where = MovieTrailerColumns._ID + "='" + movieTrialerVideo.getId() + "'";
            getActivity().getContentResolver().delete(uri, where, null);
        }

        try {
            getActivity().getContentResolver().applyBatch(PopularMovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("DetailFragment", "Error applying batch insert", e);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mMovieData = (MovieData) args.getSerializable(Constant.BUNDLE_ARG_DATA);
        }

        getLoaderManager().initLoader(REVIEW_CURSOR_LOADER_ID, null, this);
        getLoaderManager().initLoader(TRAILER_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(REVIEW_CURSOR_LOADER_ID, null, this);
        getLoaderManager().restartLoader(TRAILER_CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        if (id == REVIEW_CURSOR_LOADER_ID) {
            uri = PopularMovieProvider.MovieReviews.CONTENT_URI;

        } else {
            uri = PopularMovieProvider.MovieTrailers.CONTENT_URI;
        }

        String where = null;
        if (mMovieData != null) {
            where = MovieReviewColumns.MOVIE_ID + "=" + mMovieData.getId();
        }

        return new CursorLoader(getActivity(), uri,
                null,
                where,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (REVIEW_CURSOR_LOADER_ID == loader.getId()) {

            if (data.moveToFirst()) {
                tvMovieReviewLable.setVisibility(View.VISIBLE);
            } else {
                tvMovieReviewLable.setVisibility(View.GONE);
            }

            mMovieReviewCursorAdapter.swapCursor(data);
        } else {
            mMovieTrailerCursorAdapter.swapCursor(data);

            if (data.moveToFirst()) {
                tvMovieTrailerLable.setVisibility(View.VISIBLE);
            } else {
                tvMovieTrailerLable.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieReviewCursorAdapter.swapCursor(null);

    }

    @OnClick(R.id.btn_movie_favorite)
    void onClick() {
        Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
        updateFavouriteFlag(!isMovieFavorite());
    }

    private void updateFavouriteFlag(boolean markFavorite) {

        if (mMovieData != null) {

            Uri uri = PopularMovieProvider.IsFavoriteMovies.CONTENT_URI;


            if (markFavorite) {
                ContentValues contentValues = new ContentValues();

                contentValues.put(FavoriteMovieColumns.MOVIE_ID, mMovieData.getId());

                getActivity().getContentResolver().insert(uri, contentValues);

            } else {
                String where = FavoriteMovieColumns.MOVIE_ID + "=" + mMovieData.getId();
                getActivity().getContentResolver().delete(uri, where, null);
            }

            setFavoriteBtnLable(markFavorite);
        }

    }

    private boolean isMovieFavorite() {
        boolean isFavorite;
        if (mMovieData == null) {
            return false;

        }

        Uri uri = PopularMovieProvider.IsFavoriteMovies.CONTENT_URI;
        String where = FavoriteMovieColumns.MOVIE_ID + "=" + mMovieData.getId();

        Cursor cursor = getActivity().getContentResolver().query(uri, null, where, null, null);

        if (cursor == null) return false;
        else isFavorite = cursor.moveToFirst();

        cursor.close();

        return isFavorite;
    }

    private void setFavoriteBtnLable(boolean isFavourite) {
        if (isFavourite) btnFavorite.setText(R.string.unmark_as_favorite);
        else btnFavorite.setText(R.string.mark_as_favorite);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuShare:
                Cursor cursor = mMovieTrailerCursorAdapter.getCursor();
                if (cursor != null && cursor.moveToFirst()) {
                    StringBuilder shareText = new StringBuilder(getString(R.string.i_found_intresting_movie))
                            .append(getString(R.string.whitespace)).append(getString(R.string.check_the_trailer_at))
                            .append(getString(R.string.whitespace)).append(getString(R.string.youtube_base_link))
                            .append(cursor.getString(cursor.getColumnIndex(MovieTrailerColumns.KEY)))
                            .append(getString(R.string.whitespace)).append(getString(R.string.shared_from_popular_movie_app));

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/html");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(shareText.toString()));
                    startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));

                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnMovieTrailerSelectedListener {
        void onMovieTrialerSelected(String trailerId);
    }
}
