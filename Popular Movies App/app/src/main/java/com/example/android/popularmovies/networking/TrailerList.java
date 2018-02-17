package com.example.android.popularmovies.networking;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class TrailerList {

    @SerializedName("results")
    private List<TrailerEntity> trailerEntities = new ArrayList<>();

    public List<TrailerEntity> getTrailerEntities() {
        return trailerEntities;
    }
}
