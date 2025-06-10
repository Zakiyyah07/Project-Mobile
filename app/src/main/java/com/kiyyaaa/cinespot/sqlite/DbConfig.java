package com.kiyyaaa.cinespot.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;

import androidx.annotation.Nullable;

import com.kiyyaaa.cinespot.models.MoviesModel;

import java.util.ArrayList;
import java.util.List;

public class DbConfig extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database-cinespot";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "cinespot";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULL_NAME = "fullName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_IS_LOGGED_IN = "isLoggedIn";
    public static final String TABLE_FAVORITES = "favorite";
    public static final String COLUMN_FAV_ID = "fav_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_MOVIE_ID = "movie_id";
    public static final String COLUMN_TITLE = "seriesTitle";
    public static final String COLUMN_OVERVIEW = "overview";
    public static final String COLUMN_POSTER_LINK = "posterLink";

    public DbConfig(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_FULL_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_IS_LOGGED_IN + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITES + " ("
                + COLUMN_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_MOVIE_ID + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_OVERVIEW + " TEXT,"
                + COLUMN_POSTER_LINK + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String username, String password, String fullName, String email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_IS_LOGGED_IN, 0);
        db.insert(TABLE_NAME, null, values);
        db.close();
        return false;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID}, COLUMN_USERNAME + " = ?", new String[]{username}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public void updateRecord(int id, int isLoggedIn) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_IS_LOGGED_IN, isLoggedIn);
            db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        }
    }

    public boolean insertFavorite(int userId, MoviesModel movie) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID,   userId);
        values.put(COLUMN_MOVIE_ID,  movie.getRank());
        values.put(COLUMN_TITLE,     movie.getSeriesTitle());
        values.put(COLUMN_OVERVIEW,  movie.getOverview());
        values.put(COLUMN_POSTER_LINK, movie.getPosterLink());
        long row = db.insertWithOnConflict(
                TABLE_FAVORITES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return row != -1;
    }

    public void deleteFavorite(int userId, String movieRank) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(
                TABLE_FAVORITES,
                COLUMN_USER_ID + " = ? AND " + COLUMN_MOVIE_ID + " = ?",
                new String[]{ String.valueOf(userId), movieRank }
        );
        db.close();
    }

    public boolean isFavorite(int userId, String movieRank) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_FAVORITES,
                new String[]{ COLUMN_FAV_ID },
                COLUMN_USER_ID + " = ? AND " + COLUMN_MOVIE_ID + " = ?",
                new String[]{ String.valueOf(userId), movieRank },
                null, null, null
        );
        boolean exists = (c != null && c.getCount() > 0);
        if (c != null) c.close();
        db.close();
        return exists;
    }

    public List<MoviesModel> getAllFavorites(int userId) {
        List<MoviesModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_FAVORITES, null,
                COLUMN_USER_ID + " = ?",
                new String[]{ String.valueOf(userId) },
                null, null, null
        );
        if (c != null && c.moveToFirst()) {
            do {
                MoviesModel m = new MoviesModel();
                m.setRank(c.getString(c.getColumnIndexOrThrow(COLUMN_MOVIE_ID)));
                m.setSeriesTitle(c.getString(c.getColumnIndexOrThrow(COLUMN_TITLE)));
                m.setOverview(c.getString(c.getColumnIndexOrThrow(COLUMN_OVERVIEW)));
                m.setPosterLink(c.getString(c.getColumnIndexOrThrow(COLUMN_POSTER_LINK)));
                list.add(m);
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return list;
    }
}