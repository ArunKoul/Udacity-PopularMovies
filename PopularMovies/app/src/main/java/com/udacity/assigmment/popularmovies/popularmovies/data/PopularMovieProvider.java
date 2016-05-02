package com.udacity.assigmment.popularmovies.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.NotifyDelete;
import net.simonvt.schematic.annotation.NotifyInsert;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by arunkoul on 13/04/16.
 */

//@ContentProvider(name = "MyProviderBase", authority = PopularMovieProvider.AUTHORITY, database = PopularMovieDatabase.class)
@ContentProvider(authority = PopularMovieProvider.AUTHORITY, database = PopularMovieDatabase.class)
public class PopularMovieProvider {

    public static final String AUTHORITY = "com.udacity.assigmment.popularmovies.popularmovies";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public PopularMovieProvider() {
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();

        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String MOVIES = "movies";
        String MOVIE_REVIEWS = "movie_reviews";
        String MOVIE_TRAILERS = "movie_trailers";
        String FAVORITE_MOVIES = "favorite_movies";
        String IS_FAVORITE_MOVIES = "is_favorite_movies";
        String POPULAR_MOVIES = "popular_movies";
        String TOPMOST_MOVIES = "topmost_movies";

        String FAVORITE_JOIN_MOVIES = "favorite_join_movies";
    }

    @TableEndpoint(table = PopularMovieDatabase.MOVIES)
    public static class Movies {
        @ContentUri(
                path = Path.MOVIES,
                type = "vnd.android.cursor.dir/movie")
        public static final Uri CONTENT_URI = buildUri(Path.MOVIES);

        @InexactContentUri(
                name = "MOVIE_ID",
                path = Path.MOVIES + "/#",
                type = "vnd.android.cursor.item/movie",
                whereColumn = MovieColumns._ID,
                pathSegment = 1)

        public static Uri withId(long id) {
            return buildUri(Path.MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = PopularMovieDatabase.POPULAR_MOVIES)
    public static class PopularMovies {
        @ContentUri(
                path = Path.POPULAR_MOVIES,
                type = "vnd.android.cursor.dir/popular_movies",
                //defaultSort = MovieColumns.POPULARITY + " DESC",
                join = "LEFT JOIN " + PopularMovieDatabase.MOVIES + " on " +
                        PopularMovieDatabase.POPULAR_MOVIES + "." + PopularMovieColumns.MOVIE_ID + " = " +
                        PopularMovieDatabase.MOVIES + "." + PopularMovieColumns._ID


        )
        public static final Uri CONTENT_URI = buildUri(Path.POPULAR_MOVIES);

        @InexactContentUri(
                name = "POPULAR_MOVIES_ID",
                path = Path.POPULAR_MOVIES + "/#",
                type = "vnd.android.cursor.item/popular_movies",
                whereColumn = PopularMovieColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return buildUri(Path.POPULAR_MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = PopularMovieDatabase.TOPMOST_MOVIES)
    public static class TopMostMovies {
        @ContentUri(
                path = Path.TOPMOST_MOVIES,
                type = "vnd.android.cursor.dir/topmost_movies",
                defaultSort = MovieColumns.VOTE_AVERAGE + " DESC",
                join = "LEFT JOIN " + PopularMovieDatabase.MOVIES + " on " +
                        PopularMovieDatabase.TOPMOST_MOVIES + "." + PopularMovieColumns.MOVIE_ID + " = " +
                        PopularMovieDatabase.MOVIES + "." + PopularMovieColumns._ID
        )
        public static final Uri CONTENT_URI = buildUri(Path.TOPMOST_MOVIES);

        @InexactContentUri(
                name = "TOPMOST_MOVIES_ID",
                path = Path.TOPMOST_MOVIES + "/#",
                type = "vnd.android.cursor.item/topmost_movies",
                whereColumn = TopMostMovieColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return buildUri(Path.TOPMOST_MOVIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = PopularMovieDatabase.MOVIE_REVIEWS)
    public static class MovieReviews {
        @ContentUri(
                path = Path.MOVIE_REVIEWS,
                type = "vnd.android.cursor.dir/movie_reviews"
        )
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_REVIEWS);

        @InexactContentUri(
                name = "MOVIE_REVIEW_ID",
                path = Path.MOVIE_REVIEWS + "/#",
                type = "vnd.android.cursor.item/movie_reviews",
                whereColumn = MovieReviewColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return buildUri(Path.MOVIE_REVIEWS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = PopularMovieDatabase.MOVIE_TRAILER)
    public static class MovieTrailers {
        @ContentUri(
                path = Path.MOVIE_TRAILERS,
                type = "vnd.android.cursor.dir/movie_trailers"
        )
        public static final Uri CONTENT_URI = buildUri(Path.MOVIE_TRAILERS);

        @InexactContentUri(
                name = "MOVIE_TRAILER_ID",
                path = Path.MOVIE_TRAILERS + "/#",
                type = "vnd.android.cursor.item/movie_trailers",
                whereColumn = MovieTrailerColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return buildUri(Path.MOVIE_TRAILERS, String.valueOf(id));
        }
    }


    @TableEndpoint(table = PopularMovieDatabase.FAVORITE_MOVIES)
    public static class FavoriteMovies {

        @ContentUri(
                path = Path.FAVORITE_MOVIES,
                type = "vnd.android.cursor.dir/favorite_movies",
                join = "LEFT JOIN " + PopularMovieDatabase.MOVIES + " on " +
                        PopularMovieDatabase.FAVORITE_MOVIES + "." + FavoriteMovieColumns.MOVIE_ID + " = " +
                        PopularMovieDatabase.MOVIES + "." + PopularMovieColumns._ID
        )
        public static Uri CONTENT_URI = buildUri(Path.FAVORITE_MOVIES);

        @InexactContentUri(
                name = "FAVORITE_MOVIES_ID",
                path = Path.FAVORITE_MOVIES + "/#",
                type = "vnd.android.cursor.item/favorite_movies",
                whereColumn = FavoriteMovieColumns._ID,
                pathSegment = 1
        )

        public static Uri withId(long id) {
            return buildUri(Path.FAVORITE_MOVIES, String.valueOf(id));
        }

        @NotifyInsert(paths = Path.FAVORITE_MOVIES)
        public static Uri[] onInsert(ContentValues values) {
            return new Uri[]{FavoriteMovies.CONTENT_URI};
        }

        @NotifyDelete(paths = Path.FAVORITE_MOVIES)
        public static Uri[] onDelete() {
            return new Uri[]{FavoriteMovies.CONTENT_URI, Movies.CONTENT_URI};
        }

    }

    @TableEndpoint(table = PopularMovieDatabase.FAVORITE_MOVIES)
    public static class IsFavoriteMovies {

        @ContentUri(
                path = Path.IS_FAVORITE_MOVIES,
                type = "vnd.android.cursor.dir/is_favorite_movies"
        )
        public static final Uri CONTENT_URI = buildUri(Path.IS_FAVORITE_MOVIES);
        static final String FAV_CUR = "(SELECT * FROM "
                + PopularMovieDatabase.FAVORITE_MOVIES
                + " WHERE "
                + PopularMovieDatabase.FAVORITE_MOVIES
                + "."
                + FavoriteMovieColumns.MOVIE_ID
                + "="
                + PopularMovieDatabase.POPULAR_MOVIES
                + "."
                + PopularMovieColumns._ID
                + ")";

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();

            map.put(PopularMovieDatabase.FAVORITE_MOVIES, FAV_CUR);

            return map;
        }

        @InexactContentUri(
                name = "FAVORITE_MOVIES_ID",
                path = Path.IS_FAVORITE_MOVIES + "/#",
                type = "vnd.android.cursor.item/is_favorite_movies",
                whereColumn = FavoriteMovieColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id) {
            return buildUri(Path.IS_FAVORITE_MOVIES, String.valueOf(id));
        }
    }


    /*
    @TableEndpoint(table = PopularMovieDatabase.FAVORITE_MOVIES)
    public static final class Notes {

        @ContentUri(
                path = PopularMovieDatabase.FAVORITE_MOVIES,
                type = "vnd.android.cursor.dir/com.udacity.assigmment.popularmovies.popularmovies.content.models." + PopularMovieDatabase.FAVORITE_MOVIES
        )

        public static final Uri CONTENT_URI = buildUri(PopularMovieDatabase.FAVORITE_MOVIES);
    }
    */


}
