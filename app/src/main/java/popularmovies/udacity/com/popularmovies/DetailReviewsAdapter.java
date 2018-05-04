package popularmovies.udacity.com.popularmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import popularmovies.udacity.com.models.Review;

public class DetailReviewsAdapter extends RecyclerView.Adapter<DetailReviewsAdapter.ViewHolder> {

    private List<Review> reviewsData;

    public DetailReviewsAdapter() {
        this.reviewsData = new ArrayList<Review>(0);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View singleReviewView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail_reviews, parent, false);

        return new ViewHolder(singleReviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Review review = reviewsData.get(position);

        holder.contentTV.setText(review.getContent());
        holder.authorTV.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviewsData.size();
    }

    public void setData(List<Review> data) {
        this.reviewsData = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView contentTV;
        private final TextView authorTV;

        private ViewHolder(View v) {
            super(v);

            contentTV = v.findViewById(R.id.detail_reviews_content_tv);
            authorTV = v.findViewById(R.id.detail_reviews_author_tv);
        }
    }

}
