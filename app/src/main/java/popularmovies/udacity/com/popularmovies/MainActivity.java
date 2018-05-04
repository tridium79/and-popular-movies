package popularmovies.udacity.com.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import popularmovies.udacity.com.models.Movie;
import popularmovies.udacity.com.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private RadioGroup mRadioGroup;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private MainMoviesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String API_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        API_KEY = this.getResources().getString(R.string.movie_db_api_key);

        mRadioGroup = findViewById(R.id.toggleGroup);
        mRadioGroup.setOnCheckedChangeListener(toggleListener);

        mLoadingIndicator = findViewById(R.id.main_loading_indicator);

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MainMoviesAdapter(new ArrayList<Movie>(0));
        mRecyclerView.setAdapter(mAdapter);

        if (isOnline()) {
            new GetMoviesQueryTask().execute(API_KEY, NetworkUtils.MOST_POPULAR_MOVIES);
        } else {
            showNetworkErrorToast();
        }
    }

    private static final RadioGroup.OnCheckedChangeListener toggleListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
            for (int j = 0; j < radioGroup.getChildCount(); j++) {
                final ToggleButton view = (ToggleButton) radioGroup.getChildAt(j);
                view.setChecked(view.getId() == i);
            }
        }
    };

    public void onToggle(View view) {
        int currentlySelectedToggleId = mRadioGroup.getCheckedRadioButtonId();
        int selectedToggleId = view.getId();

        mRadioGroup.clearCheck();
        mRadioGroup.check(selectedToggleId);

        if (currentlySelectedToggleId != selectedToggleId) {
            if (isOnline()) {
                switch (selectedToggleId) {
                    case R.id.toggle_most_popular:
                        new GetMoviesQueryTask().execute(API_KEY, NetworkUtils.MOST_POPULAR_MOVIES);
                        break;
                    default: // R.id.toggle_top_rated:
                        new GetMoviesQueryTask().execute(API_KEY, NetworkUtils.TOP_RATED_MOVIES);
                        break;
                }
            } else {
                showNetworkErrorToast();
            }
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
        @Override
        protected void onPreExecute() {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... strings) {
            return NetworkUtils.getMovies(strings[0], strings[1]);
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