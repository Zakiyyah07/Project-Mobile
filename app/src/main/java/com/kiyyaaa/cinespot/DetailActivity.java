package com.kiyyaaa.cinespot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.kiyyaaa.cinespot.models.MoviesModel;
import com.kiyyaaa.cinespot.sqlite.DbConfig;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private DbConfig dbConfig;
    private ImageButton backButton;
    private ImageView posterDetail;
    private TextView titleDetail;
    private TextView releaseDateDetail;
    private TextView ratingTextDetail;
    private TextView genreText;
    private TextView overview;
    private MaterialButton favoriteButton;
    private boolean isFavorite = false;
    private int loggedInUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbConfig = new DbConfig(this);
        backButton = findViewById(R.id.backButton);
        posterDetail = findViewById(R.id.posterDetail);
        titleDetail = findViewById(R.id.titleDetail);
        releaseDateDetail = findViewById(R.id.releaseDateDetail);
        ratingTextDetail = findViewById(R.id.ratingTextDetail);
        genreText = findViewById(R.id.genreText);
        overview = findViewById(R.id.overview);
        favoriteButton = findViewById(R.id.favoriteButton);

        backButton.setOnClickListener(view -> finish());

        loggedInUserId = getLoggedInUserId();

        MoviesModel moviesModel = getIntent().getParcelableExtra("moviesModel");
        if (moviesModel != null) {
            showMovieDetails(moviesModel);
            isFavorite = dbConfig.isFavorite(loggedInUserId, moviesModel.getRank());
            updateFavoriteIcon();

            favoriteButton.setOnClickListener(v -> {
                toggleFavorite(moviesModel);
            });
        } else {
            Toast.makeText(this, "Movie data not available", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showMovieDetails(MoviesModel moviesModel) {
        titleDetail.setText(moviesModel.getSeriesTitle());
        releaseDateDetail.setText(moviesModel.getReleasedYear());
        ratingTextDetail.setText(moviesModel.getImdbRating());
        genreText.setText(moviesModel.getGenre());
        overview.setText(moviesModel.getOverview());
        Picasso.get().load(moviesModel.getPosterLink()).into(posterDetail);
    }

    private void toggleFavorite(MoviesModel moviesModel) {
        if (isFavorite) {
            dbConfig.deleteFavorite(loggedInUserId, moviesModel.getRank());
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            dbConfig.insertFavorite(loggedInUserId, moviesModel);
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        isFavorite = !isFavorite;
        updateFavoriteIcon();
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            favoriteButton.setIconResource(R.drawable.love);
            favoriteButton.setIconTintResource(R.color.favorite_red);
        } else {
            favoriteButton.setIconResource(R.drawable.fav);
            favoriteButton.setIconTintResource(R.color.white);
        }
    }

    private int getLoggedInUserId() {
        SharedPreferences sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("user_id", -1);
    }
}
