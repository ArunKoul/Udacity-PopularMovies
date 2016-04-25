package com.udacity.assigmment.popularmovies.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by arunkoul on 13/04/16.
 */

//@Database(className = "MyDatabase", fileName = PopularMovieDatabase.DB_FILE, version = PopularMovieDatabase.VERSION)
@Database(version = PopularMovieDatabase.VERSION)
public class PopularMovieDatabase {

    public static final int VERSION = 1;
    @Table(MovieColumns.class)
    public static final String MOVIES = "movies";
    @Table(MovieReviewColumns.class)
    public static final String MOVIE_REVIEWS = "movie_reviews";
    @Table(MovieTrailerColumns.class)
    public static final String MOVIE_TRAILER = "movie_trailer";
    @Table(FavoriteMovieColumns.class)
    public static final String FAVORITE_MOVIES = "favorite_movies";
    @Table(PopularMovieColumns.class)
    public static final String POPULAR_MOVIES = "popular_movies";
    @Table(TopMostMovieColumns.class)
    public static final String TOPMOST_MOVIES = "topmost_movies";

    private PopularMovieDatabase() {
    }


    /*
    // Migrations
    // Just put the sql, and increment {@ #VERSION} value
    public static final String[] MIGRATIONS = {};


    @OnUpgrade
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = MIGRATIONS[i];
            db.beginTransaction();
            try {
                db.execSQL(migration);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e(PopularMovieDatabase.class.getSimpleName(), String.format(
                        "Failed to upgrade database with script: %s", migration), e);
                break;
            }
        }
    }
    */

}
