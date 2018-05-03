package popularmovies.udacity.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import popularmovies.udacity.com.models.Movie;

public class DetailActivity extends AppCompatActivity {

    private ImageView mThumbnailIV;
    private TextView mTitleTV;
    private TextView mSynopsisTV;
    private RatingBar mUserRatingRB;
    private TextView mReleaseDateTV;

    private Movie movieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        // get data
        movieData = (Movie) intent.getSerializableExtra("MovieDetails");

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

        populateUI();
    }

    private void populateUI() {
        mTitleTV.setText(movieData.getTitle());
        mSynopsisTV.setText(movieData.getSynopsis());
        mUserRatingRB.setRating(movieData.getUserRating());
        mReleaseDateTV.setText(movieData.getReleaseDate().toString());

        Picasso.with(this)
                .load(movieData.getThumbnailUrl())
                .into(mThumbnailIV);
    }

}
