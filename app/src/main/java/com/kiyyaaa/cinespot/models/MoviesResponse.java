package com.kiyyaaa.cinespot.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MoviesResponse {
    @SerializedName("page")
    private String page;

    @SerializedName("result")
    private List<MoviesModel> result;

    public String getPage() {
        return page;
    }

    public List<MoviesModel> getResult() {
        return result;
    }

    public List<MoviesModel> getMovies() {
        return result;
    }
}
