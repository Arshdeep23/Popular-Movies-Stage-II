package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MoviesContentProviderExtention extends ContentProvider {
    static final int MOVIES = 100;
    private static final UriMatcher myUriMatcher = buildUriMatcher();
    private MovieOpenHelper movieOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final String contentAuthority = MovieContract.BASE_CONTENT_AUTHORITY;
        final UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        myUriMatcher.addURI(contentAuthority, MovieContract.PATH_MOVIE, MOVIES);
        return myUriMatcher;
    }

    @Override
    public boolean onCreate() {
        movieOpenHelper = new MovieOpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (myUriMatcher.match(uri)) {
            case MOVIES: {
                cursor = movieOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry._TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri not known: " + uri);
        }
        if (getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int uriMatch = myUriMatcher.match(uri);
        switch (uriMatch) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Uri not known: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int uriMatch = myUriMatcher.match(uri);
        final SQLiteDatabase db = movieOpenHelper.getWritableDatabase();
        Uri uri1;
        switch (uriMatch) {
            case MOVIES: {
                long l = db.insert(MovieContract.MovieEntry._TABLE_NAME, null, values);
                if (l > 0) {
                    uri1 = MovieContract.MovieEntry.buildMovieUri(l);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri not known" + uri);
        }
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return uri1;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int uriMatch = myUriMatcher.match(uri);
        int delRows;
        final SQLiteDatabase db = movieOpenHelper.getWritableDatabase();

        if (null == selection) {
            selection = "1";
        }
        switch (uriMatch) {
            case MOVIES:
                delRows = db.delete(
                        MovieContract.MovieEntry._TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uri not known" + uri);
        }

        if (getContext() != null && delRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int uriMatch = myUriMatcher.match(uri);
        int upRows;
        final SQLiteDatabase db = movieOpenHelper.getWritableDatabase();


        switch (uriMatch) {
            case MOVIES:
                upRows = db.update(MovieContract.MovieEntry._TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uri not known" + uri);
        }
        if (getContext() != null && upRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return upRows;
    }
}
