package com.example.android.popularmovies.networking;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieEngineService {


    @GET("3/movie/{id}/reviews")
    Call<ReviewList> findReviewsById(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{sort_by}")
    Call<MovieList> discoverMovies(@Path("sort_by") String sortBy, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<TrailerList> findTrailersById(@Path("id") long movieId, @Query("api_key") String apiKey);

}