package com.movies.popular.albert.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alberto Casas Ortiz.
 */
class MoviesDBHelper extends SQLiteOpenHelper{
    //Name of the database.
    private static final String DATABASE_NAME = "waitlist.db";

    //Version of the database.
    private static final int DATABASE_VERSION = 3;

    /**
     * Constructor of the DBHelper.
     * @param context Context of the aplication.
     */
    public MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL ," +
                MoviesContract.MoviesEntry.COLUMN_MOVIEDB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
