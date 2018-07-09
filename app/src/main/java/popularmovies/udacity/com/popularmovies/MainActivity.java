package popularmovies.udacity.com.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import popularmovies.udacity.com.data.FavoriteMovieContentProvider;
import popularmovies.udacity.com.data.FavoriteMovieContract;
import popularmovies.udacity.com.data.FavoriteMovieDbHelper;
import popularmovies.udacity.com.models.Movie;
import popularmovies.udacity.com.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner mSortOptionsSpinner;
    private ProgressBar mLoadingIndicator;

    private FavoriteMovieDbHelper dbHelper;

    private RecyclerView mRecyclerView;
    private MainMoviesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_KEY = this.getResources().getString(R.string.movie_db_api_key);

        dbHelper = new FavoriteMovieDbHelper(this);

        mSortOptionsSpinner = findViewById(R.id.sort_options_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.main_sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSortOptionsSpinner.setAdapter(adapter);
        mSortOptionsSpinner.setOnItemSelectedListener(this);

        mLoadingIndicator = findViewById(R.id.main_loading_indicator);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MainMoviesAdapter(new ArrayList<Movie>(0));
        mRecyclerView.setAdapter(mAdapter);

        loadSortOptionPreference();

        if (isOnline()) {
            refreshMovies(mSortOptionsSpinner.getSelectedItemPosition());
        } else {
            showNetworkErrorToast();
        }
    }

    private void saveSortOptionPreference(int position) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt(getResources().getString(R.string.main_sort_option_spinner_key), position);
        editor.commit();
    }

    private void loadSortOptionPreference() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        int sortOptionsSelectedOption = sharedPreferences.getInt(getResources().getString(R.string.main_sort_option_spinner_key), 0);
        mSortOptionsSpinner.setSelection(sortOptionsSelectedOption);
    }

    // for spinner selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isOnline()) {
            showNetworkErrorToast();
            return;
        }

        saveSortOptionPreference(position);

        refreshMovies(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    private void refreshMovies(int position) {
        switch (position) {
            case 0:
                new GetMoviesQueryTask(null).execute(API_KEY, NetworkUtils.MOST_POPULAR_MOVIES);
                break;
            case 1:
                new GetMoviesQueryTask(null).execute(API_KEY, NetworkUtils.TOP_RATED_MOVIES);
                break;
            case 2:
                ContentResolver contentResolver = getContentResolver();
                Uri getMoviesUri = FavoriteMovieContract.CONTENT_URI.buildUpon().appendPath("movies").build();
                Cursor cursor = contentResolver.query(getMoviesUri, null, null, null, null);

                List<Integer> favoriteMovieIds = new ArrayList<>();

                if (cursor != null && cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        int movieId = cursor.getInt(cursor.getColumnIndex(FavoriteMovieContract.FavoriteMovie.COLUMN_MOVIE_ID));
                        favoriteMovieIds.add(movieId);
                        cursor.moveToNext();
                    }
                }

                if (favoriteMovieIds.isEmpty()) {
                    Toast.makeText(this, getResources().getString(R.string.main_no_favorite_movies_toast_text), Toast.LENGTH_LONG).show();
                }

                new GetMoviesQueryTask(favoriteMovieIds).execute(API_KEY, NetworkUtils.FAVORITE_MOVIES);
                break;
        }
    }

    // sourced from https://stackoverflow.com/a/4009133
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void showNetworkErrorToast() {
        Toast.makeText(this, R.string.no_network_connectivity_error, Toast.LENGTH_LONG).show();
    }

    private class GetMoviesQueryTask extends AsyncTask<String, Void, List<Movie>> {
        List<Integer> favoriteMovieIds;

        public GetMoviesQueryTask(List<Integer> favoriteMovieIds) {
            super();

            this.favoriteMovieIds = favoriteMovieIds;
        }

        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            if (strings[1].equals(NetworkUtils.FAVORITE_MOVIES)) {
                List<Movie> favoriteMovies = new ArrayList<>();

                for(Integer movieId : favoriteMovieIds) {
                    Movie movie = NetworkUtils.getMovie(strings[0], movieId);

                    if (movie != null) {
                        favoriteMovies.add(movie);
                    }
                }

                return favoriteMovies;
            } else {
                return NetworkUtils.getMovies(strings[0], strings[1]);
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mAdapter.setMovieData(movies);
            mAdapter.notifyDataSetChanged();

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.scrollToPosition(0);
        }
    }
}