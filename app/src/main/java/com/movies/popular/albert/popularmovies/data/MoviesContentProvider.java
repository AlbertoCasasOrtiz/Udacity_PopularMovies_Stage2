package com.movies.popular.albert.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Alberto Casas Ortiz.
 */
public class MoviesContentProvider extends ContentProvider {

    //Movie db helper.
    private MoviesDBHelper dbHelper;

    //Ints to identify single or multiple operations.
    public static final int MOVIES = 100;
    public static final int MOVIES_WITH_ID = 101;

    //Uri matcher.
    private final static UriMatcher uriMatcher = buildUriMatcher();

    /**
     * Build a URI matcher.
     * @return An URI matcher.
     */
    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MoviesContract.AUTHORITY, MoviesContract.PATH_MOVIES + "/#", MOVIES_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        this.dbHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Cursor retCursor;
        String mSelection;
        String[] mSelectionArgs;

        switch (match) {
            case MOVIES:
                retCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIES_WITH_ID:
                mSelection = MoviesContract.MoviesEntry.COLUMN_MOVIEDB_ID + "  LIKE ?";
                mSelectionArgs = new String[]{uri.getPathSegments().get(1)};
                retCursor = db.query(MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Wrong URI: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri returnUri = null;
        long id;

        switch (match) {
            case MOVIES:
                id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);

                if(id > 0){
                    returnUri = ContentUris.withAppendedId(MoviesContract.MoviesEntry.CONTENT_URI, id);
                }

                break;
            default:
                throw new UnsupportedOperationException("Wrong URI: " + uri);
        }

        if (id != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        int res;
        String mWhere;
        String[] mWhereArgs;

        switch (match) {
            case MOVIES_WITH_ID:
                mWhere = MoviesContract.MoviesEntry.COLUMN_MOVIEDB_ID + "  LIKE ?";
                mWhereArgs = new String[]{uri.getPathSegments().get(1)};
                res = db.delete(MoviesContract.MoviesEntry.TABLE_NAME,
                        mWhere,
                        mWhereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Wrong URI: " + uri);
        }

        if (res != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return res;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
