package com.udacity.assigmment.popularmovies.popularmovies; /**
 * Created by arunkoul on 25/02/16.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


public class MovieFragment extends Fragment {

    private ProgressDialog mLoadingMovieProgressBar;

    private OnMovieSelectedListener mCallback;
    private ImageAdapter mImageAdapter;
    private GridView mMovieGridView;

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mImageAdapter = new ImageAdapter(getActivity());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieGridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        mMovieGridView.setHorizontalSpacing(10);
        mMovieGridView.setVerticalSpacing(10);
        mMovieGridView.setAdapter(mImageAdapter);
        mMovieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                mCallback.onMovieSelected(position, (MovieData) view.getTag());

                // Set the item as checked to be highlighted when in two-pane layout
                mMovieGridView.setItemChecked(position, true);
            }
        });

        return rootView;
    }

    private void updatePopularMovie() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_most_popular));

        FetchPopularMoviesTask popularMovieTask = new FetchPopularMoviesTask();
        popularMovieTask.execute(sortBy);
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePopularMovie();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.detail_fragment) != null) {
            mMovieGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener");
        }
    }

    // The container Activity must implement this interface so the frag can deliver messages
    public interface OnMovieSelectedListener {
        void onMovieSelected(int position, MovieData movieData);
    }

    public class FetchPopularMoviesTask extends AsyncTask<String, Void, List<MovieData>> {

        private final String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        private List<MovieData> getPopularMovieDataFromJson(String moviesJsonStr) {

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(moviesJsonStr).getAsJsonObject();
            JsonArray jArray = jsonObject.getAsJsonArray("results");

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            return Arrays.asList(gson.fromJson(jArray, MovieData[].class));

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mLoadingMovieProgressBar = new ProgressDialog(getActivity());
            mLoadingMovieProgressBar.setMessage(getString(R.string.loading_movies));
            mLoadingMovieProgressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mLoadingMovieProgressBar.show();
        }

        @Override
        protected List<MovieData> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String sortBy = params[0];

            String popularMovieJsonStr = null;

            try {
                String sortByValue;

                if(sortBy.equals(getString(R.string.pref_sort_by_most_popular))) {
                    sortByValue = Constant.SORT_BY_POPULARTY;
                } else {
                    sortByValue = Constant.SORT_BY_VOTE_COUNT;
                }

                Uri builtUri = Uri.parse(Constant.THE_MOVIE_DISCOVER_BASE_URL).buildUpon()
                        .appendQueryParameter(Constant.SORT_BY_PARAM, sortByValue)
                        .appendQueryParameter(Constant.API_KEYID_PARAM, BuildConfig.POPULAR_MOVIE_API_KEY)
                        .build();



                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                popularMovieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return getPopularMovieDataFromJson(popularMovieJsonStr);

        }

        @Override
        protected void onPostExecute(List<MovieData> result) {
            if (result != null) {
                mImageAdapter.setData(result);
            }

            if(mLoadingMovieProgressBar != null && mLoadingMovieProgressBar.isShowing()) {
                mLoadingMovieProgressBar.dismiss();
                mLoadingMovieProgressBar = null;
            }
        }
    }
}