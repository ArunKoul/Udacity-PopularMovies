package com.udacity.assigmment.popularmovies.popularmovies.contract;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by arunkoul on 11/04/16.
 */
public class MovieTrailerVideoContract {

    private long id;

    @SerializedName("results")
    private List<MovieTrialerVideo> movieTrialerVideos;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<MovieTrialerVideo> getMovieTrialerVideos() {
        return movieTrialerVideos;
    }

    public void setMovieTrialerVideos(List<MovieTrialerVideo> movieTrialerVideos) {
        this.movieTrialerVideos = movieTrialerVideos;
    }
}
