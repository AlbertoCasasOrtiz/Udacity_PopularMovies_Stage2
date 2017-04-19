package com.movies.popular.albert.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Alberto Casas Ortiz.
 */
public class MoviesContract {

    public static final String AUTHORITY = "com.movies.popular.albert.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movie";

    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_MOVIEDB_ID = "movieId";
    }
}
