package com.udacity.assigmment.popularmovies.popularmovies.contract;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by arunkoul on 11/04/16.
 */
public class MovieReviewContract {

    private long id;
    private int page;

    //private String result;
    @SerializedName("results")
    private List<MovieReview> movieReviews;

    private int total_pages;
    private int total_result;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MovieReview> getMovieReviews() {
        return movieReviews;
    }

    public void setMovieReviews(List<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public int getTotalPages() {
        return total_pages;
    }

    public void setTotalPages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotalResult() {
        return total_result;
    }

    public void setTotalResult(int total_result) {
        this.total_result = total_result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
