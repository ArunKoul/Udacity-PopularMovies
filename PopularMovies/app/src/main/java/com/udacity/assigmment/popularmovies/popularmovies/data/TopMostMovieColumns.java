package com.udacity.assigmment.popularmovies.popularmovies.data;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by arunkoul on 13/04/16.
 */
public interface TopMostMovieColumns {
    //@DataType(DataType.Type.INTEGER) @References(table = PopularMovieDatabase.MOVIES, column = MovieColumns._ID)
    //public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String _ID = BaseColumns._ID;

    @DataType(DataType.Type.INTEGER)
    @References(table = PopularMovieDatabase.MOVIES, column = MovieColumns._ID)
    public static final String MOVIE_ID = "movie_id";

}
