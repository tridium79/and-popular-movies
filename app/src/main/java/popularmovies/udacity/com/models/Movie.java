package popularmovies.udacity.com.models;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class Movie implements Serializable{

    private int id;
    private String title;
    private String thumbnailUrl;
    private String synopsis;
    private float userRating;
    private Date releaseDate;

    private final String DATE_FORMAT = "yyyy-MM-dd";

    public Movie() {
    }

    public Movie(int id, String title, String thumbnailUrl, String synopsis, float userRating, String releaseDate) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.synopsis = synopsis;
        this.userRating = userRating;

        try {
            this.releaseDate = new SimpleDateFormat(DATE_FORMAT).parse(releaseDate);
        } catch (ParseException e) {
            Log.w(TAG, "Movie: ", e);
            this.releaseDate = new Date();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        final String baseUrl = "http://image.tmdb.org/t/p/";
        final String size = "w185";
        return baseUrl + size + thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public float getUserRating() {
        return userRating / 2;
    }

    public void setUserRating(float userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return new SimpleDateFormat(DATE_FORMAT).format(releaseDate);
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
}
