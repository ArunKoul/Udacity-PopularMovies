package com.udacity.assigmment.popularmovies.popularmovies.data;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by arunkoul on 13/04/16.
 */
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    String POSTER_PATH = "poster_path";

    @DataType(DataType.Type.TEXT)
    String OVERVIEW = "overview";

    @DataType(DataType.Type.TEXT)
    String RELEASE_DATE = "release_date";

    @DataType(DataType.Type.TEXT)
    String ORIGINAL_TITLE = "original_title";

    @DataType(DataType.Type.TEXT)
    String ORIGINAL_LANGUAGE = "original_language";

    @DataType(DataType.Type.TEXT)
    String TITLE = "title";

    @DataType(DataType.Type.TEXT)
    String BACKDROP_PATH = "backdrop_path";

    @DataType(DataType.Type.TEXT)
    String POPULARITY = "popularity";

    @DataType(DataType.Type.INTEGER)
    String VOTE_COUNT = "vote_count";

    @DataType(DataType.Type.REAL)
    String VOTE_AVERAGE = "vote_average";
}
