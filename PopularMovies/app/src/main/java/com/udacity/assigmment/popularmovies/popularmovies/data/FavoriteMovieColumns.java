package com.udacity.assigmment.popularmovies.popularmovies.data;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by arunkoul on 13/04/16.
 */
public interface FavoriteMovieColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String _ID = BaseColumns._ID;

    @DataType(DataType.Type.INTEGER)
    @References(table = PopularMovieDatabase.MOVIES, column = MovieColumns._ID)
    String MOVIE_ID = "movie_id";

}
