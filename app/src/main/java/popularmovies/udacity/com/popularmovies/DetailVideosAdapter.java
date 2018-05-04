package popularmovies.udacity.com.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import popularmovies.udacity.com.models.Movie;
import popularmovies.udacity.com.models.Video;

public class DetailVideosAdapter extends RecyclerView.Adapter<DetailVideosAdapter.ViewHolder> {

    private List<Video> videoData;

    public DetailVideosAdapter() {
        this.videoData = new ArrayList<Video>(0);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View singleVideoView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail_videos, parent, false);

        return new ViewHolder(singleVideoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Video video = videoData.get(position);

        Picasso.with(holder.thumbnailIV.getContext())
                .load(video.getThumbnailUrl())
                .into(holder.thumbnailIV);
        holder.titleTV.setText(video.getName());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//
//                Intent intent = new Intent(context, DetailActivity.class);
//                intent.putExtra("MovieDetails", video);
//
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return videoData.size();
    }

    public void setData(List<Video> data) {
        this.videoData = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView thumbnailIV;
        private final TextView titleTV;

        private ViewHolder(View v) {
            super(v);

            thumbnailIV = v.findViewById(R.id.detail_video_thumbnail_iv);
            titleTV = v.findViewById(R.id.detail_video_name_tv);
        }
    }

}
