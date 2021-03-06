package com.udacity.assigmment.popularmovies.popularmovies.contract;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.udacity.assigmment.popularmovies.popularmovies.data.MovieColumns;

/**
 * Created by arunkoul on 07/03/16.
 */

public class MovieData implements Parcelable {

    private String poster_path;
    private String overview;
    private String release_date;
    private String original_title;
    private String original_language;
    private String title;
    private String backdrop_path;
    private String popularity;

    private long id;
    private int vote_count;
    private float vote_average;

    private boolean adult;
    private boolean video;

    public MovieData() {
    }

    public static MovieData fromCursor(Cursor cursor) {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        //Date date = new Date(cursor.getLong(releaseDateIndex));

        MovieData movieData = new MovieData();
        movieData.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MovieColumns._ID)));
        movieData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.TITLE)));
        movieData.setVoteAverage(cursor.getFloat(cursor.getColumnIndexOrThrow(MovieColumns.VOTE_AVERAGE)));
        movieData.setPopularity(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.POPULARITY)));
        movieData.setVoteCount(cursor.getInt(cursor.getColumnIndexOrThrow(MovieColumns.VOTE_COUNT)));
        movieData.setReleaseDate(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.RELEASE_DATE)));
        movieData.setBackdropPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.BACKDROP_PATH)));
        movieData.setOriginalLanguage(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.ORIGINAL_LANGUAGE)));
        movieData.setOverview(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.OVERVIEW)));
        movieData.setOriginalTitle(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.ORIGINAL_TITLE)));
        movieData.setPosterPath(cursor.getString(cursor.getColumnIndexOrThrow(MovieColumns.POSTER_PATH)));


        return movieData;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public void setPosterPath(String poster_path) {
        this.poster_path = poster_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return original_title;
    }

    public void setOriginalTitle(String original_title) {
        this.original_title = original_title;
    }

    public String getOriginalLanguage() {
        return original_language;
    }

    public void setOriginalLanguage(String original_language) {
        this.original_language = original_language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdrop_path;
    }

    public void setBackdropPath(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public void setVoteCount(int vote_count) {
        this.vote_count = vote_count;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public float getVoteAverage() {
        return vote_average;
    }

    public void setVoteAverage(float vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeString(popularity);

        dest.writeLong(id);
        dest.writeInt(vote_count);
        dest.writeFloat(vote_average);

        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeByte((byte) (video ? 1 : 0));

    }

    private MovieData(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readString();

        id = in.readLong();
        vote_count = in.readInt();
        vote_average = in.readFloat();

        adult = in.readByte() != 0;
        video = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {

        @Override
        public MovieData createFromParcel(Parcel source) {
            return new MovieData(source);
        }

        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
}

