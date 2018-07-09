package popularmovies.udacity.com.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoriteMovieContentProvider extends ContentProvider {
    private static final int MOVIES_LIST = 1;
    private static final int MOVIE_FAVORITE_ACTION = 2;
    private static final UriMatcher URI_MATCHER;

    private FavoriteMovieDbHelper dbHelper;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(FavoriteMovieContract.CONTENT_AUTHORITY, "movies", MOVIES_LIST);
        URI_MATCHER.addURI(FavoriteMovieContract.CONTENT_AUTHORITY, "movie/favorite", MOVIE_FAVORITE_ACTION);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new FavoriteMovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (URI_MATCHER.match(uri) == MOVIES_LIST) {
            return dbHelper.getFavoriteMovieIds();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(URI_MATCHER.match(uri)) {
            case MOVIES_LIST:
                break;
            case MOVIE_FAVORITE_ACTION:
                break;
            default:
                return null;
        }

        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (URI_MATCHER.match(uri) == MOVIE_FAVORITE_ACTION) {
            boolean isFavorite = dbHelper.toggleIsFavorite(values);

            if (isFavorite) {
                return uri;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
