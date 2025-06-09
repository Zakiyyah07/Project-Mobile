// HomeFragment.java
package com.kiyyaaa.cinespot.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kiyyaaa.cinespot.R;
import com.kiyyaaa.cinespot.adapter.MoviesAdapter;
import com.kiyyaaa.cinespot.api.ApiConfig;
import com.kiyyaaa.cinespot.api.ApiService;
import com.kiyyaaa.cinespot.models.MoviesModel;
import com.kiyyaaa.cinespot.models.MoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private MoviesAdapter moviesAdapter;
    private SearchView searchView;
    private List<MoviesModel> moviesList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout errorState;
    private TextView errorTitle, errorSubtitle;
    private MaterialButton retryButton;
    private ApiService apiService;

    // Untuk debouncing search
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY_MS = 500; // Delay 500ms

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.movieRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        progressBar = view.findViewById(R.id.progressBar);
        errorState = view.findViewById(R.id.errorState);
        errorTitle = view.findViewById(R.id.errorTitle);
        errorSubtitle = view.findViewById(R.id.errorSubtitle);
        retryButton = view.findViewById(R.id.retryButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moviesAdapter = new MoviesAdapter(moviesList, getContext());
        recyclerView.setAdapter(moviesAdapter);

        apiService = ApiConfig.getClient().create(ApiService.class);

        retryButton.setOnClickListener(v -> loadMovies());

        loadMovies();

        // Real-time search dengan debouncing
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                final String q = newText.trim();
                searchRunnable = () -> {
                    if (q.isEmpty()) {
                        loadMovies();
                    } else {
                        performSearch(q);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
                return true;
            }
        });
    }

    private void loadMovies() {
        progressBar.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);

        apiService.getMovies().enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResult() != null
                        && !response.body().getResult().isEmpty()) {

                    moviesList.clear();
                    moviesList.addAll(response.body().getResult());
                    moviesAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);

                } else {
                    showLoadError();
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showLoadError();
                Toast.makeText(getContext(),
                        "Failed to load movies: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String title) {
        progressBar.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);

        apiService.getMovieByTitle(title).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResult() != null
                        && !response.body().getResult().isEmpty()) {

                    moviesList.clear();
                    moviesList.addAll(response.body().getResult());
                    moviesAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);

                } else {
                    showSearchNoMatch();
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showLoadError();
                Toast.makeText(getContext(),
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoadError() {
        errorTitle.setText("Failed to Load Movies");
        errorSubtitle.setText("Check your internet connection");
        retryButton.setVisibility(View.VISIBLE);
        errorState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showSearchNoMatch() {
        errorTitle.setText("Search did not match");
        errorSubtitle.setText("");
        retryButton.setVisibility(View.GONE);
        errorState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }
}
