package com.kiyyaaa.cinespot.api;

import com.kiyyaaa.cinespot.models.MoviesModel;
import com.kiyyaaa.cinespot.models.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    String RAPID_API_KEY  = "4f170d761fmsha2af21edd2709f9p19436cjsn826b051c8ca3";
    String RAPID_API_HOST = "imdb-top-1000-movies-series.p.rapidapi.com";

    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST
    })
    @GET("list/1")
    Call<MoviesResponse> getMovies();

    @Headers({
            "X-RapidAPI-Key: " + RAPID_API_KEY,
            "X-RapidAPI-Host: " + RAPID_API_HOST,
            "Content-Type: application/x-www-form-urlencoded"
    })
    @FormUrlEncoded
    @POST("bytitle")
    Call<MoviesResponse> getMovieByTitle(@Field("title") String title);
}