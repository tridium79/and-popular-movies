package popularmovies.udacity.com.utils;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import popularmovies.udacity.com.models.Movie;
import popularmovies.udacity.com.popularmovies.R;

import static android.content.ContentValues.TAG;

public class NetworkUtils {

    private static final String API_ENDPOINT = "http://api.themoviedb.org/3";
    private static final String API_PARAM = "api_key";

    public static final String MOST_POPULAR_MOVIES = "movie/popular";
    public static final String TOP_RATED_MOVIES = "movie/top_rated";

    private static final String API_MOVIE_RESULTS_KEY = "results";
    private static final String API_MOVIE_TITLE_KEY = "original_title";
    private static final String API_MOVIE_THUMBNAIL_URL_KEY = "poster_path";
    private static final String API_MOVIE_SYNOPSIS_KEY = "overview";
    private static final String API_MOVIE_USER_RATING_KEY = "vote_average";
    private static final String API_MOVIE_RELEASE_DATE_KEY = "release_date";

    public static List<Movie> getMovies(String sortType, String apiKey) {
        return getMoviesFromApi(buildUrl(sortType, apiKey));
    }

    private static List<Movie> getMoviesFromApi(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return parseMovieJson(scanner.next());
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "getPopularMovies: ", e);
        } finally {
            urlConnection.disconnect();
        }

        return new ArrayList(0);
    }

    private static URL buildUrl(String sortType, String apiKey) {
        try {
            Uri builtUri = Uri.parse(API_ENDPOINT)
                    .buildUpon()
                    .appendEncodedPath(sortType)
                    .appendQueryParameter(API_PARAM, apiKey)
                    .build();

            return new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildUrl: ", e);
        }

        return null;
    }

    private static List<Movie> parseMovieJson(String movieData) {
        List movies = new ArrayList();

        JSONObject moviesJson = null;
        try {
            moviesJson = new JSONObject(movieData);
        } catch (JSONException e) {
            Log.e(TAG, "parseMovieJson: ", e);
        }

        if (moviesJson == null) {
            return movies;
        }

        try {
            JSONArray movieResults = moviesJson.getJSONArray(API_MOVIE_RESULTS_KEY);

            for (int i = 0; i < movieResults.length(); i++) {
                JSONObject movieJson = (JSONObject) movieResults.get(i);

                String title = movieJson.optString(API_MOVIE_TITLE_KEY);
                String thumbnailUrl = movieJson.optString(API_MOVIE_THUMBNAIL_URL_KEY);
                String synopsis = movieJson.optString(API_MOVIE_SYNOPSIS_KEY);
                float userRating = (float) movieJson.optDouble(API_MOVIE_USER_RATING_KEY, 0.0);
                String releaseDate = movieJson.optString(API_MOVIE_RELEASE_DATE_KEY);

                movies.add(new Movie(title, thumbnailUrl, synopsis, userRating, releaseDate));
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseMovieJson: ", e);
        }

        return movies;
    }
}
