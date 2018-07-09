package popularmovies.udacity.com.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import static popularmovies.udacity.com.data.FavoriteMovieContract.FavoriteMovie.*;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorite_movies.db";

    private static final int DATABASE_VERSION = 1;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY NOT NULL" + ")";

        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean getIsFavorite(String movieId) {
        return DatabaseUtils.queryNumEntries(this.getReadableDatabase(), TABLE_NAME,
                COLUMN_MOVIE_ID + " = ?", new String[] { movieId}) > 0;
    }

    // returns isFavorite value to update
    public boolean toggleIsFavorite(ContentValues contentValues) {
        SQLiteDatabase db = this.getWritableDatabase();

        String movieId = contentValues.getAsString(FavoriteMovieContract.FavoriteMovie.COLUMN_MOVIE_ID);

        if (getIsFavorite(movieId)) {
            db.delete(TABLE_NAME, COLUMN_MOVIE_ID + " = ?", new String[] { movieId });
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MOVIE_ID, movieId);

            db.insert(TABLE_NAME, null, values);
            return true;
        }
    }

    public Cursor getFavoriteMovieIds() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(FavoriteMovieContract.FavoriteMovie.TABLE_NAME, null, null, null, null, null, null);
    }
}
