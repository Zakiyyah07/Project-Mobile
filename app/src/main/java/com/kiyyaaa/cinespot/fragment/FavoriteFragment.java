package com.kiyyaaa.cinespot.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.kiyyaaa.cinespot.utils.Utils;

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
    private SharedPreferences preferences;
    private ExecutorService executor;
    private Handler handler;
    private ApiService service;

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

        dbConfig = new DbConfig(requireContext());
        preferences = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        service = ApiConfig.getClient().create(ApiService.class);

        retryButton.setOnClickListener(v -> loadFavorites());
        loadFavorites();
    }

    private int getCurrentUserId() {
        return preferences.getInt("user_id", -1);
    }

    private void loadFavorites() {
        int userId = getCurrentUserId();
        progressBar.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        errorState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        if (Utils.isNetworkAvailable(requireContext())) {
            // Jika online: fetch API lalu filter berdasarkan rank favorit lokal
            service.getMovies().enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(@NonNull Call<MoviesResponse> call, @NonNull Response<MoviesResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    if (!isAdded() || !response.isSuccessful() || response.body() == null) {
                        showErrorState();
                        return;
                    }
                    List<MoviesModel> allMovies = response.body().getMovies();
                    // dapatkan semua rank favorit dari DB
                    List<MoviesModel> localFavs = dbConfig.getAllFavorites(userId);
                    List<String> favRanks = new ArrayList<>();
                    for (MoviesModel m : localFavs) {
                        favRanks.add(m.getRank());
                    }
                    // filter
                    List<MoviesModel> favoriteMovies = new ArrayList<>();
                    for (MoviesModel movie : allMovies) {
                        if (favRanks.contains(movie.getRank())) {
                            favoriteMovies.add(movie);
                        }
                    }
                    displayFavorites(favoriteMovies);
                }

                @Override
                public void onFailure(@NonNull Call<MoviesResponse> call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    showErrorState();
                }
            });
        } else {
            // Offline: tampilkan langsung data favorit lokal
            executor.execute(() -> {
                List<MoviesModel> favoriteMovies = dbConfig.getAllFavorites(getCurrentUserId());
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    displayFavorites(favoriteMovies);
                });
            });
        }
    }

    private void displayFavorites(List<MoviesModel> favoriteMovies) {
        if (favoriteMovies == null || favoriteMovies.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            favoriteAdapter = new FavoriteAdapter(
                    getParentFragmentManager(),
                    favoriteMovies,
                    getCurrentUserId());
            recyclerView.setAdapter(favoriteAdapter);
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    private void showErrorState() {
        errorState.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdown();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }
}