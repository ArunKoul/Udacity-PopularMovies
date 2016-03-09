package com.udacity.assigmment.popularmovies.popularmovies;

import java.io.Serializable;

/**
 * Created by arunkoul on 07/03/16.
 */
class MovieData implements Serializable {

    public String poster_path;
    public boolean adult;
    public String overview;
    public String release_date;
    public long id;
    public String original_title;
    public String original_language;
    public String title;
    public String backdrop_path;
    private String popularity;
    private int vote_count;
    public boolean video;
    public float vote_average;

}
