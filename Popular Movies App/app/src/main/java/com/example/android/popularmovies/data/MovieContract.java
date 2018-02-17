package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String PATH_MOVIE = "movie";
    public static final String BASE_CONTENT_AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + BASE_CONTENT_AUTHORITY);

    public static final class MovieEntry implements BaseColumns {

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + BASE_CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String _TABLE_NAME = "movie";
        public static final String _COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String _COLUMN_MOVIE_ID = "movie_id";
        public static final String _COLUMN_MOVIE_TITLE = "original_title";
        public static final String _COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String _COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String _COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";
        public static final String _COLUMN_MOVIE_OVERVIEW = "overview";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static final String[] MOVIE_COLUMNS = {
                _COLUMN_MOVIE_ID,
                _COLUMN_MOVIE_TITLE,
                _COLUMN_MOVIE_POSTER_PATH,
                _COLUMN_MOVIE_OVERVIEW,
                _COLUMN_MOVIE_VOTE_AVERAGE,
                _COLUMN_MOVIE_RELEASE_DATE,
                _COLUMN_MOVIE_BACKDROP_PATH
        };

        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_TITLE = 1;
        public static final int COL_MOVIE_POSTER_PATH = 2;
        public static final int COL_MOVIE_OVERVIEW = 3;
        public static final int COL_MOVIE_VOTE_AVERAGE = 4;
        public static final int COL_MOVIE_RELEASE_DATE = 5;
        public static final int COL_MOVIE_BACKDROP_PATH = 6;
    }
}
