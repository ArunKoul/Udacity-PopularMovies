package com.udacity.assigmment.popularmovies.popularmovies.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by arunkoul on 13/04/16.
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    public static final String POSTER_PATH = "poster_path";

    @DataType(DataType.Type.TEXT)
    public static final String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT)
    public static final String RELEASE_DATE = "release_date";

    @DataType(DataType.Type.TEXT)
    public static final String ORIGINAL_TITLE = "original_title";

    @DataType(DataType.Type.TEXT)
    public static final String ORIGINAL_LANGUAGE = "original_language";

    @DataType(DataType.Type.TEXT)
    public static final String TITLE = "title";

    @DataType(DataType.Type.TEXT)
    public static final String BACKDROP_PATH = "backdrop_path";

    @DataType(DataType.Type.TEXT)
    public static final String POPULARITY = "popularity";

    @DataType(DataType.Type.INTEGER)
    public static final String VOTE_COUNT = "vote_count";

    @DataType(DataType.Type.REAL)
    public static final String VOTE_AVERAGE = "vote_average";
}
