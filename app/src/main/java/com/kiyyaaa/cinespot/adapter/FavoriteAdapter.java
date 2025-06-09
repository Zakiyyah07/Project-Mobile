package com.kiyyaaa.cinespot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiyyaaa.cinespot.DetailActivity;
import com.kiyyaaa.cinespot.R;
import com.kiyyaaa.cinespot.models.MoviesModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private final FragmentManager fragmentManager;
    private final List<MoviesModel> moviesModels;
    private final int userId;

    public FavoriteAdapter(FragmentManager fragmentManager, List<MoviesModel> moviesModelList, int userId) {
        this.fragmentManager = fragmentManager;
        this.moviesModels   = moviesModelList;
        this.userId         = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoviesModel movie = moviesModels.get(position);
        holder.bind(movie, userId);

        holder.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, DetailActivity.class);
            intent.putExtra("moviesModel", movie);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return moviesModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView releaseDateView;
        private final TextView ratingView;
        private final TextView overviewView;
        private final ImageView posterView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.movieTitle);
            releaseDateView = itemView.findViewById(R.id.movieReleaseDate);
            ratingView = itemView.findViewById(R.id.movieRating);
            overviewView = itemView.findViewById(R.id.movieOverview);
            posterView = itemView.findViewById(R.id.moviePoster);
        }

        public void bind(MoviesModel moviesModel, int userId) {
            Picasso.get()
                    .load(moviesModel.getPosterLink())
                    .into(posterView);
            titleView.setText(moviesModel.getSeriesTitle());
            releaseDateView.setText(moviesModel.getReleasedYear());
            ratingView.setText(moviesModel.getImdbRating());
            overviewView.setText(moviesModel.getOverview());
        }
    }
}
