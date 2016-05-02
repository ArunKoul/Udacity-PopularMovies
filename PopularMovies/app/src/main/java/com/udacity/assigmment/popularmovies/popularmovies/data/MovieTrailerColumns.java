package com.udacity.assigmment.popularmovies.popularmovies.data;

import android.provider.BaseColumns;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

/**
 * Created by arunkoul on 13/04/16.
 */
public interface MovieTrailerColumns {
    @DataType(DataType.Type.TEXT)
    @PrimaryKey
    String _ID = BaseColumns._ID;

    @DataType(DataType.Type.INTEGER)
    @References(table = PopularMovieDatabase.MOVIES, column = MovieColumns._ID)
    String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    String KEY = "key";

    @DataType(DataType.Type.TEXT)
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    String SITE = "site";

    @DataType(DataType.Type.INTEGER)
    String SIZE = "size";

    @DataType(DataType.Type.TEXT)
    String TYPE = "type";
}
