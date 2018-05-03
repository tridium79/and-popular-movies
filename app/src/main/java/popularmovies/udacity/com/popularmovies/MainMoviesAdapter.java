package popularmovies.udacity.com.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import popularmovies.udacity.com.models.Movie;

public class MainMoviesAdapter extends RecyclerView.Adapter<MainMoviesAdapter.ViewHolder> {

    private List<Movie> movieData;

    public MainMoviesAdapter(List<Movie> movieData) {
        this.movieData = movieData;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View singleMovieView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_recyclerview_item, parent, false);

        return new ViewHolder(singleMovieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Movie movie = movieData.get(position);

        Picasso.with(holder.thumbnailIV.getContext())
                .load(movie.getThumbnailUrl())
                .into(holder.thumbnailIV);
        holder.titleTV.setText(movie.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("MovieDetails", movie);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieData.size();
    }

    public void setMovieData(List<Movie> movieData) {
        this.movieData = movieData;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnailIV;
        public TextView titleTV;

        public ViewHolder(View v) {
            super(v);

            thumbnailIV = (ImageView) v.findViewById(R.id.main_movie_thumbnail);
            titleTV = (TextView) v.findViewById(R.id.main_movie_title);
        }
    }

}
