package com.udacity.assigmment.popularmovies.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {
    private MovieData mMovieData;

    public DetailFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mMovieData = (MovieData) savedInstanceState.getSerializable(Constant.BUNDLE_ARG_DATA);
        }

        return inflater.inflate(R.layout.fragment_detail, container, false);
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

    public void updateMovieDetailView(MovieData movieData) {
        ImageView moviePosterImageView = (ImageView) getActivity().findViewById(R.id.iv_movie_poster);

        if(movieData != null) {
            ((TextView) getActivity().findViewById(R.id.textview_movie_name)).setText(movieData.title);
            ((TextView) getActivity().findViewById(R.id.textview_movie_release_data)).setText(movieData.release_date);
            ((TextView) getActivity().findViewById(R.id.textview_movie_overview)).setText(movieData.overview);

            String posterPath = movieData.poster_path;
            if(posterPath == null) {
                posterPath = movieData.backdrop_path;
            }

            Picasso.with(getActivity())
                    .load(Constant.THE_MOVIE_IMAGE_BASE_URL + posterPath)
                    .placeholder(R.color.colorAccent)
                    .into(moviePosterImageView);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constant.BUNDLE_ARG_DATA, mMovieData);
    }


}
