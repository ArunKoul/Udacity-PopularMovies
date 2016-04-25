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
    public static final String _ID = BaseColumns._ID;

    @DataType(DataType.Type.INTEGER)
    @References(table = PopularMovieDatabase.MOVIES, column = MovieColumns._ID)
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    public static final String KEY = "key";

    @DataType(DataType.Type.TEXT)
    public static final String NAME = "name";

    @DataType(DataType.Type.TEXT)
    public static final String SITE = "site";

    @DataType(DataType.Type.INTEGER)
    public static final String SIZE = "size";

    @DataType(DataType.Type.TEXT)
    public static final String TYPE = "type";
}
