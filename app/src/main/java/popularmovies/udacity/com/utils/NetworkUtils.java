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
import popularmovies.udacity.com.models.Review;
import popularmovies.udacity.com.models.Video;

import static android.content.ContentValues.TAG;

public class NetworkUtils {

    private static final String API_ENDPOINT = "http://api.themoviedb.org/3/movie";
    private static final String API_PARAM = "api_key";

    // main
    public static final String MOST_POPULAR_MOVIES = "popular";
    public static final String TOP_RATED_MOVIES = "top_rated";
    public static final String FAVORITE_MOVIES = "favorites";

    private static final String API_MOVIE_ID_KEY = "id";
    private static final String API_RESULTS_KEY = "results";
    private static final String API_MOVIE_TITLE_KEY = "original_title";
    private static final String API_MOVIE_THUMBNAIL_URL_KEY = "poster_path";
    private static final String API_MOVIE_SYNOPSIS_KEY = "overview";
    private static final String API_MOVIE_USER_RATING_KEY = "vote_average";
    private static final String API_MOVIE_RELEASE_DATE_KEY = "release_date";

    // detail
    public static final String DETAIL_MOVIE_VIDEOS = "videos";
    private static final String API_MOVIE_VIDEO_KEY_KEY = "key";
    private static final String API_MOVIE_VIDEO_NAME_KEY = "name";

    public static final String DETAIL_MOVIE_REVIEWS = "reviews";
    private static final String API_MOVIE_REVIEW_AUTHOR = "author";
    private static final String API_MOVIE_REVIEW_CONTENT = "content";

    public static List<Movie> getMovies(String apiKey, String contentType) {
        String apiResult = getContentFromApi(buildUrl(apiKey, null, contentType));

        if (apiResult != null) {
            return parseMoviesJson(apiResult);
        } else {
            return new ArrayList<Movie>(0);
        }
    }

    public static Movie getMovie(String apiKey, Integer movieId) {
        String apiResult = getContentFromApi(buildUrl(apiKey, movieId.toString(), null));

        if (apiResult != null) {
            JSONObject movieJson = JsonUtils.convertStringToJsonObject(apiResult);

            if (movieJson == null) {
                return null;
            }

            return parseMovieJson(movieJson);
        }

        return null;
    }

    public static List<Video> getVideos(String apiKey, String movieId, String contentType) {
        String apiResult = getContentFromApi(buildUrl(apiKey, movieId, contentType));

        if (apiResult != null) {
            return parseVideoJson(apiResult);
        } else {
            return new ArrayList<Video>(0);
        }
    }

    public static List<Review> getReviews(String apiKey, String movieId, String contentType) {
        String apiResult = getContentFromApi(buildUrl(apiKey, movieId, contentType));

        if (apiResult != null) {
            return parseReviewJson(apiResult);
        } else {
            return new ArrayList<Review>(0);
        }
    }

    private static String getContentFromApi (URL url) {
        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "getPopularMovies: ", e);
        } finally {
            urlConnection.disconnect();
        }

        return null;
    }

    private static URL buildUrl(String apiKey, String movieId, String sortType) {
        try {
            Uri.Builder uriBuilder = Uri.parse(API_ENDPOINT).buildUpon();

            if (movieId != null) {
                uriBuilder.appendPath(movieId);
            }

            if (sortType != null) {
                uriBuilder.appendPath(sortType);
            }

            uriBuilder.appendQueryParameter(API_PARAM, apiKey);

            return new URL(uriBuilder.build().toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "buildUrl: ", e);
        }

        return null;
    }

    private static List<Movie> parseMoviesJson(String moviesData) {
        List<Movie> movies = new ArrayList<>();

        JSONObject moviesJson = JsonUtils.convertStringToJsonObject(moviesData);
        if (moviesJson == null) {
            return movies;
        }

        try {
            JSONArray movieResults = moviesJson.getJSONArray(API_RESULTS_KEY);

            for (int i = 0; i < movieResults.length(); i++) {
                movies.add(parseMovieJson((JSONObject) movieResults.get(i)));
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseMoviesJson: ", e);
        }

        return movies;
    }

    private static Movie parseMovieJson(JSONObject movieJson) {
        int id = movieJson.optInt(API_MOVIE_ID_KEY, 0);
        String title = movieJson.optString(API_MOVIE_TITLE_KEY);
        String thumbnailUrl = movieJson.optString(API_MOVIE_THUMBNAIL_URL_KEY);
        String synopsis = movieJson.optString(API_MOVIE_SYNOPSIS_KEY);
        float userRating = (float) movieJson.optDouble(API_MOVIE_USER_RATING_KEY, 0.0);
        String releaseDate = movieJson.optString(API_MOVIE_RELEASE_DATE_KEY);

        return new Movie(id, title, thumbnailUrl, synopsis, userRating, releaseDate);
    }

    private static List<Video> parseVideoJson(String videoData) {
        List<Video> videos = new ArrayList<Video>();

        JSONObject reviewsJson = JsonUtils.convertStringToJsonObject(videoData);
        if (reviewsJson == null) {
            return videos;
        }

        try {
            JSONArray reviewResults = reviewsJson.getJSONArray(API_RESULTS_KEY);

            for (int i = 0; i < reviewResults.length(); i++) {
                JSONObject videoJson = (JSONObject) reviewResults.get(i);

                String name = videoJson.optString(API_MOVIE_VIDEO_NAME_KEY);
                String key = videoJson.optString(API_MOVIE_VIDEO_KEY_KEY);

                videos.add(new Video(name, key));
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseVideoJson: ", e);
        }

        return videos;
    }

    private static List<Review> parseReviewJson(String reviewData) {
        List<Review> reviews = new ArrayList<Review>();

        JSONObject reviewsJson = JsonUtils.convertStringToJsonObject(reviewData);
        if (reviewsJson == null) {
            return reviews;
        }

        try {
            JSONArray reviewResults = reviewsJson.getJSONArray(API_RESULTS_KEY);

            for (int i = 0; i < reviewResults.length(); i++) {
                JSONObject reviewJson = (JSONObject) reviewResults.get(i);

                String author = reviewJson.optString(API_MOVIE_REVIEW_AUTHOR);
                String content = reviewJson.optString(API_MOVIE_REVIEW_CONTENT);

                reviews.add(new Review(author, content));
            }
        } catch (JSONException e) {
            Log.e(TAG, "parseReviewJson: ", e);
        }

        return reviews;
    }
}
