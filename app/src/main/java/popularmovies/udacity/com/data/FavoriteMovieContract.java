package popularmovies.udacity.com.data;

import android.net.Uri;
import android.provider.BaseColumns;

public final class FavoriteMovieContract {

    public static final String CONTENT_AUTHORITY = "popularmovies.udacity.com.popularmovies.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class FavoriteMovie implements BaseColumns {
        public static final String TABLE_NAME = "favoriteMovies";
        public static final String COLUMN_MOVIE_ID = "movieId";
    }

}
