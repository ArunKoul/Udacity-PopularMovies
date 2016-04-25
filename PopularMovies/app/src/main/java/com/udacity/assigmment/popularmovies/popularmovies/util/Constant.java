package com.udacity.assigmment.popularmovies.popularmovies.util;

import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieContract;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieReviewContract;
import com.udacity.assigmment.popularmovies.popularmovies.contract.MovieTrailerVideoContract;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by arunkoul on 09/03/16.
 */
public class Constant {
    public static final String THE_MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    public static final String THE_MOVIE_BASE_URL = "http://api.themoviedb.org/3/";

    public static final String BUNDLE_ARG_DATA = "data";
    public static final String API_KEYID_PARAM = "api_key";

    public interface PopularMoviesEndpointInterface {
        @GET("movie/popular")
        Observable<MovieContract> getPopularMovies(@Query(API_KEYID_PARAM) String apiKey);

        @GET("movie/top_rated")
        Observable<MovieContract> getTopRatedMovies(@Query(API_KEYID_PARAM) String apiKey);

        @GET("movie/{id}/videos")
        Observable<MovieTrailerVideoContract> getMoviesTrialers(@Path("id") long movieId, @Query(API_KEYID_PARAM) String apiKey);

        @GET("movie/{id}/reviews")
        Observable<MovieReviewContract> getMoviesReviews(@Path("id") long movieId, @Query(API_KEYID_PARAM) String apiKey);
    }

}
