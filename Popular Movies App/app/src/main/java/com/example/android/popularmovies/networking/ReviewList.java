package com.example.android.popularmovies.networking;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewList {

    @SerializedName("results")
    private List<ReviewEntity> reviewEntities = new ArrayList<>();

    public List<ReviewEntity> getReviewEntities() {
        return reviewEntities;
    }
}
