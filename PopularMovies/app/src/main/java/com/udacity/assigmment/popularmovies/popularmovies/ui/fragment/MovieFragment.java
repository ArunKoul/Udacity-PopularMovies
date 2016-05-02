package com.udacity.assigmment.popularmovies.popularmovies.ui.fragment;
/**
 * Created by arunkoul on 25/02/16.
 */

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.udacity.assigmment.popularmovies.popularmovies.BuildConfig;
import com.udacity.assigmment.popularmovies.popularmovies.R;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieContract;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieData;
import com.udacity.assigmment.popularmovies.popularmovies.data.MovieColumns;
import com.udacity.assigmment.popularmovies.popularmovies.data.PopularMovieColumns;
import com.udacity.assigmment.popularmovies.popularmovies.data.PopularMovieProvider;
import com.udacity.assigmment.popularmovies.popularmovies.data.TopMostMovieColumns;
import com.udacity.assigmment.popularmovies.popularmovies.ui.adapter.MovieCursorAdapter;
import com.udacity.assigmment.popularmovies.popularmovies.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CURSOR_LOADER_ID = 0;
    @Bind(R.id.movie_recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.movie_loading_progressbar_layout)
    LinearLayout mProgressLinearLayout;
    //@Bind(R.id.gridview_movies) GridView mMovieGridView;
    private ProgressDialog mLoadingMovieProgressBar;
    private OnMovieSelectedListener mOnMovieSelectedListenerCallback;
    private OnHttpResponseListner mOnHttpResponseListnerCallback;
    private Subscription subscription;
    private MovieCursorAdapter mCursorAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FavroiteContentObserver favoriteContentObserver;

    private static final String SELECTED_KEY = "selected_position";
    public int mPosition = GridView.INVALID_POSITION;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        favoriteContentObserver = new FavroiteContentObserver(new Handler());
        getActivity().getContentResolver().
                registerContentObserver(
                        PopularMovieProvider.IsFavoriteMovies.CONTENT_URI,
                        true,
                        favoriteContentObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(getActivity(), getColumnSize());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCursorAdapter = new MovieCursorAdapter(getActivity(), null);
        mRecyclerView.setAdapter(mCursorAdapter);

        mProgressLinearLayout.setVisibility(View.VISIBLE);

        updatePopularMovieRx();

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == WHAT) {
                    MovieData movieData = null;

                    Bundle bundle = msg.getData();
                    if(bundle != null) movieData = (MovieData) bundle.getParcelable(Constant.BUNDLE_ARG_DATA);

                    mOnMovieSelectedListenerCallback.onMovieSelected(movieData, true, mPosition);

                }
            }
        };


        return rootView;
    }

    public void updatePopularMovieRx() {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String spinnerOption = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.THE_MOVIE_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        Constant.PopularMoviesEndpointInterface apiService = retrofit.create(Constant.PopularMoviesEndpointInterface.class);

        final Observable<MovieContract> moviesData;
        if (spinnerOption.equals(getString(R.string.pref_sort_by_most_popular))) {
            moviesData = apiService.getPopularMovies(BuildConfig.POPULAR_MOVIE_API_KEY);
        } else {
            moviesData = apiService.getTopRatedMovies(BuildConfig.POPULAR_MOVIE_API_KEY);
        }

        subscription = moviesData
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieContract>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onCompleted() {
                        mProgressLinearLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mLoadingMovieProgressBar != null && mLoadingMovieProgressBar.isShowing()) {
                            mLoadingMovieProgressBar.dismiss();
                            mLoadingMovieProgressBar = null;
                        }

                        // cast to retrofit.HttpException to get the response code
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            mOnHttpResponseListnerCallback.onError(response.code(), response.getMessage());
                        } else {
                            mOnHttpResponseListnerCallback.onError(-1, getString(R.string.internal_server_error));
                        }

                    }

                    @Override
                    public void onNext(MovieContract movieContract) {
                        if (spinnerOption.equals(getString(R.string.pref_sort_by_most_popular))) {
                            insertMovieData(movieContract.getMovieDatas(), true);
                        } else {
                            insertMovieData(movieContract.getMovieDatas(), false);
                        }

                    }
                });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnMovieSelectedListenerCallback = (OnMovieSelectedListener) context;
            mOnHttpResponseListnerCallback = (OnHttpResponseListner) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener and OnHttpResponseListner");
        }
    }

    @Override
    public void onDestroyView() {
        getActivity().getContentResolver().unregisterContentObserver(favoriteContentObserver);

        super.onDestroyView();

        this.subscription.unsubscribe();
        ButterKnife.unbind(this);

        mHandler = null;

    }

    private int getColumnSize() {
        /*
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;

        return Math.round(dpWidth/160); //TODO: Fixed value
        */

        return 2;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ((GridLayoutManager) mLayoutManager).setSpanCount(getColumnSize());
    }

    /**
     * insertMovieData
     *
     * @param movieDatas
     * @param isPopularMovie
     */
    public void insertMovieData(List<MovieData> movieDatas, boolean isPopularMovie) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>(movieDatas.size());

        Uri uri;
        for (MovieData movieData : movieDatas) {
            ContentProviderOperation.Builder movieDataBuilder;
            ContentProviderOperation.Builder movieTypeDataBuilder;


            if (isPopularMovie) {
                uri = PopularMovieProvider.PopularMovies.CONTENT_URI;
            } else {
                uri = PopularMovieProvider.TopMostMovies.CONTENT_URI;
            }

            movieDataBuilder = ContentProviderOperation.newInsert(PopularMovieProvider.Movies.CONTENT_URI);
            movieTypeDataBuilder = ContentProviderOperation.newInsert(uri);

            movieDataBuilder.withValue(MovieColumns._ID, movieData.getId());
            movieDataBuilder.withValue(MovieColumns.BACKDROP_PATH, movieData.getBackdropPath());
            movieDataBuilder.withValue(MovieColumns.ORIGINAL_LANGUAGE, movieData.getOriginalLanguage());
            movieDataBuilder.withValue(MovieColumns.ORIGINAL_TITLE, movieData.getOriginalTitle());
            movieDataBuilder.withValue(MovieColumns.OVERVIEW, movieData.getOverview());
            movieDataBuilder.withValue(MovieColumns.POPULARITY, movieData.getPopularity());
            movieDataBuilder.withValue(MovieColumns.POSTER_PATH, movieData.getPosterPath());
            movieDataBuilder.withValue(MovieColumns.RELEASE_DATE, movieData.getReleaseDate());
            movieDataBuilder.withValue(MovieColumns.TITLE, movieData.getTitle());
            movieDataBuilder.withValue(MovieColumns.VOTE_AVERAGE, movieData.getVoteAverage());
            movieDataBuilder.withValue(MovieColumns.VOTE_COUNT, movieData.getVoteCount());


            movieTypeDataBuilder.withValue(isPopularMovie ? PopularMovieColumns.MOVIE_ID : TopMostMovieColumns.MOVIE_ID, movieData.getId());

            batchOperations.add(movieDataBuilder.build());
            batchOperations.add(movieTypeDataBuilder.build());

            //-- Delete Old Record --//
            String where = MovieColumns._ID + "=" + movieData.getId();
            getActivity().getContentResolver().delete(PopularMovieProvider.Movies.CONTENT_URI, where, null);

            String whereType = isPopularMovie ? PopularMovieColumns.MOVIE_ID : TopMostMovieColumns.MOVIE_ID + "=" + movieData.getId();
            getActivity().getContentResolver().delete(uri, whereType, null);

        }

        try {
            getActivity().getContentResolver().applyBatch(PopularMovieProvider.AUTHORITY, batchOperations);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("MovieFragment", "Error applying batch insert", e);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular));

        Uri uri;
        String sortOrder = null;
        if (sortBy.equals(getString(R.string.pref_sort_by_most_popular))) {
            uri = PopularMovieProvider.PopularMovies.CONTENT_URI;
            //sortOrder = MovieColumns.POPULARITY + " DESC";
        } else if (sortBy.equals(getString(R.string.pref_sort_by_high_rated))) {
            uri = PopularMovieProvider.TopMostMovies.CONTENT_URI;
            sortOrder = MovieColumns.VOTE_AVERAGE + " DESC";

        } else {
            uri = PopularMovieProvider.FavoriteMovies.CONTENT_URI;
        }

        getActivity().getContentResolver().notifyChange(PopularMovieProvider.FavoriteMovies.CONTENT_URI, null);


        return new CursorLoader(getActivity(), uri,
                null,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean isDataInMovieTable = data.moveToFirst();

        mCursorAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.scrollToPosition(mPosition);
        }


        Bundle bundle = null;
        if (isDataInMovieTable) {
            if(mPosition != GridView.INVALID_POSITION) data.move(mPosition);

            final MovieData movieData = MovieData.fromCursor(data);

            bundle = new Bundle();
            bundle.putParcelable(Constant.BUNDLE_ARG_DATA, movieData);
        }

        Message msg = mHandler.obtainMessage(WHAT);
        msg.setData(bundle);

        mHandler.sendMessage(msg);

        if (isDataInMovieTable) {
            mProgressLinearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPrefs.edit();

        switch (item.getItemId()) {
            case R.id.menuSortPopular:
                editor.putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular));
                editor.commit();

                updatePopularMovieRx();
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

                return true;

            case R.id.menuSortRating:
                editor.putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_high_rated));
                editor.commit();

                updatePopularMovieRx();
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

                return true;
            case R.id.menuSortFavorite:
                editor.putString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_favorite));
                editor.commit();

                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mRecyclerView.scrollToPosition(mPosition);
        }

    }


    public interface OnMovieSelectedListener {
        void onMovieSelected(MovieData movieData, boolean displayFristItem, int position);
    }

    public interface OnHttpResponseListner {
        void onSucess(MovieContract movieContract);

        void onError(int statusCode, String message);

    }

    private class FavroiteContentObserver extends ContentObserver {
        public FavroiteContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String spinnerOptionSelected = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular));
            if (spinnerOptionSelected.equals(getString(R.string.pref_sort_by_favorite))) {
                getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, MovieFragment.this);
            }

        }
    }

    final int WHAT = 1;
    private Handler mHandler;


}