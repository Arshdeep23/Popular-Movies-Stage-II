package com.example.android.popularmovies.networking;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MovieList {

    @SerializedName("results")
    private List<MovieEntity> moviesCollection = new ArrayList<>();

    public List<MovieEntity> getMoviesCollection() {
        return moviesCollection;
    }
}

