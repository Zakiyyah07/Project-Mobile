package com.kiyyaaa.cinespot.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kiyyaaa.cinespot.R;
import com.kiyyaaa.cinespot.adapter.FavoriteAdapter;
import com.kiyyaaa.cinespot.api.ApiConfig;
import com.kiyyaaa.cinespot.api.ApiService;
import com.kiyyaaa.cinespot.models.MoviesModel;
import com.kiyyaaa.cinespot.models.MoviesResponse;
import com.kiyyaaa.cinespot.sqlite.DbConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyState;
    private View errorState;
    private MaterialButton retryButton;
    private View progressBar;
    private FavoriteAdapter favoriteAdapter;
    private DbConfig dbConfig;
    private ApiService service;
    private SharedPreferences preferences;
    private ExecutorService executor;
    private Handler handler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyState = view.findViewById(R.id.emptyState);
        errorState = view.findViewById(R.id.errorState);
        retryButton = view.findViewById(R.id.retryButton);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        dbConfig = new DbConfig(requireActivity());
        preferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        int userId = preferences.getInt("user_id", -1);
        Log.d("FavoriteFragment", "Logged in userId = " + userId);

        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        retryButton.setOnClickListener(v -> loadFavorites(userId));

        loadFavorites(userId);
    }

    private void loadFavorites(int userId) {
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        executor.execute(() -> {
            Cursor cursor = dbConfig.getFavoriteMovieByUserId(userId);
            List<String> favoriteMoviesRank = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String rank = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.COLUMN_MOVIE_ID));
                    favoriteMoviesRank.add(rank);
                    Log.d("FavoriteFragment", "Fav rank: " + rank);
                } while (cursor.moveToNext());
                cursor.close();
            }
            handler.post(() -> fetchAndDisplayFavorites(favoriteMoviesRank, userId));
        });
    }

    private void fetchAndDisplayFavorites(List<String> favoriteMoviesRank, int userId) {
        service = ApiConfig.getClient().create(ApiService.class);
        service.getMovies().enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (!isAdded() || !response.isSuccessful() || response.body() == null) {
                    showErrorState();
                    return;
                }
                List<MoviesModel> allMovies = response.body().getMovies();
                List<MoviesModel> favoriteMovies = new ArrayList<>();
                for (MoviesModel movie : allMovies) {
                    if (favoriteMoviesRank.contains(movie.getRank())) {
                        favoriteMovies.add(movie);
                    }
                }
                favoriteAdapter = new FavoriteAdapter(
                        getParentFragmentManager(),
                        favoriteMovies,
                        userId
                );
                recyclerView.setAdapter(favoriteAdapter);

                if (favoriteMovies.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                Log.e("FavoriteFragment", "API failure: " + t.getMessage());
                progressBar.setVisibility(View.GONE);
                showErrorState();
            }
        });
    }

    private void showErrorState() {
        errorState.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        int userId = preferences.getInt("user_id", -1);
        loadFavorites(userId);
    }
}
