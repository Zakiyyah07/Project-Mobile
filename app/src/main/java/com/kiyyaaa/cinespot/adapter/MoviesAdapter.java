package com.kiyyaaa.cinespot.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kiyyaaa.cinespot.DetailActivity;
import com.kiyyaaa.cinespot.R;
import com.kiyyaaa.cinespot.models.MoviesModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private List<MoviesModel> moviesModels;
    private Context context;

    public MoviesAdapter(List<MoviesModel> moviesModels, Context context) {
        this.moviesModels = moviesModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MoviesModel moviesModel = moviesModels.get(position);
        holder.bind(moviesModel);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("moviesModel", moviesModel);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return moviesModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView, releaseDateView,  ratingView, overviewView;
        public ImageView posterView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.movieTitle);
            releaseDateView = itemView.findViewById(R.id.movieReleaseDate);
            ratingView = itemView.findViewById(R.id.movieRating);
            overviewView = itemView.findViewById(R.id.movieOverview);
            posterView = itemView.findViewById(R.id.moviePoster);
        }
        public void bind(MoviesModel moviesModel){
            Picasso.get().load(moviesModel.getPosterLink()).into(posterView);
            titleView.setText(moviesModel.getSeriesTitle());
            releaseDateView.setText(moviesModel.getReleasedYear());
            ratingView.setText(moviesModel.getImdbRating());
            overviewView.setText(moviesModel.getOverview());
        }
    }
}

