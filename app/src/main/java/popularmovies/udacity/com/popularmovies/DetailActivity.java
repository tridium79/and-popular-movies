package popularmovies.udacity.com.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import popularmovies.udacity.com.data.FavoriteMovieContract;
import popularmovies.udacity.com.data.FavoriteMovieDbHelper;
import popularmovies.udacity.com.models.Movie;
import popularmovies.udacity.com.models.Review;
import popularmovies.udacity.com.models.Video;
import popularmovies.udacity.com.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DETAIL";

    private String API_KEY;

    private ImageView mThumbnailIV;
    private TextView mTitleTV;
    private TextView mSynopsisTV;
    private RatingBar mUserRatingRB;
    private TextView mReleaseDateTV;
    private TextView mFavoriteButton;

    private RecyclerView mVideosRV;
    private DetailVideosAdapter mVideosAdapter;
    private RecyclerView.LayoutManager mVideosLayoutManager;

    private RecyclerView mReviewsRV;
    private DetailReviewsAdapter mReviewsAdapter;
    private RecyclerView.LayoutManager mReviewsLayoutManager;

    private Movie movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        API_KEY = getResources().getString(R.string.movie_db_api_key);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        // get data
        movieData = (Movie) intent.getSerializableExtra("MovieDetails");
        final FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(movieData.getTitle());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // UI elements
        mThumbnailIV = findViewById(R.id.detail_thumbnail_iv);
        mTitleTV = findViewById(R.id.detail_title_tv);
        mSynopsisTV = findViewById(R.id.detail_synopsis_tv);
        mUserRatingRB = findViewById(R.id.detail_user_rating_rb);
        mReleaseDateTV = findViewById(R.id.detail_release_date_tv);
        mFavoriteButton = findViewById(R.id.detail_favorite_button);

        final boolean isFavorite = dbHelper.getIsFavorite(Integer.toString(movieData.getId()));
        setIsFavoriteButton(isFavorite);

        // onclick listener
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();

                Uri insertUri = FavoriteMovieContract.CONTENT_URI.buildUpon().appendEncodedPath("movie/favorite").build();

                ContentValues cv = new ContentValues();
                cv.put(FavoriteMovieContract.FavoriteMovie.COLUMN_MOVIE_ID, movieData.getId());

                Uri result = contentResolver.insert(insertUri, cv);

                setIsFavoriteButton(result != null);
            }
        });

        // videos
        mVideosRV = findViewById(R.id.detail_videos_rv);

        mVideosAdapter = new DetailVideosAdapter();
        mVideosLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mVideosRV.setAdapter(mVideosAdapter);
        mVideosRV.setLayoutManager(mVideosLayoutManager);

        new VideosQueryTask().execute(API_KEY, String.valueOf(movieData.getId()), NetworkUtils.DETAIL_MOVIE_VIDEOS);

        // reviews
        mReviewsRV = findViewById(R.id.detail_reviews_rv);

        mReviewsAdapter = new DetailReviewsAdapter();
        mReviewsLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mReviewsRV.setAdapter(mReviewsAdapter);
        mReviewsRV.setLayoutManager(mReviewsLayoutManager);

        new ReviewsQueryTask().execute(API_KEY, String.valueOf(movieData.getId()), NetworkUtils.DETAIL_MOVIE_REVIEWS);

        populateUI();
    }

    private void populateUI() {
        mTitleTV.setText(movieData.getTitle());
        mSynopsisTV.setText(movieData.getSynopsis());
        mUserRatingRB.setRating(movieData.getUserRating());
        mReleaseDateTV.setText(movieData.getReleaseDate());

        Picasso.with(this)
                .load(movieData.getThumbnailUrl())
                .into(mThumbnailIV);
    }

    private void setIsFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            mFavoriteButton.setText(getResources().getText(R.string.detail_favorite_button_unfavorite));
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        } else {
            mFavoriteButton.setText(getResources().getText(R.string.detail_favorite_button_favorite));
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.lightGrey));
        }
    }

    private class VideosQueryTask extends AsyncTask<String, Void, List<Video>> {

        @Override
        protected List<Video> doInBackground(String... strings) {
            return NetworkUtils.getVideos(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(List<Video> videos) {
            mVideosAdapter.setData(videos);
            mVideosAdapter.notifyDataSetChanged();
        }
    }

    private class ReviewsQueryTask extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... strings) {
            return NetworkUtils.getReviews(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            mReviewsAdapter.setData(reviews);
            mReviewsAdapter.notifyDataSetChanged();
        }
    }

}
