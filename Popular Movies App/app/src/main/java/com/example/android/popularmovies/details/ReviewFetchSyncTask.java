package com.example.android.popularmovies.details;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.networking.TheMovieEngineService;
import com.example.android.popularmovies.networking.ReviewEntity;
import com.example.android.popularmovies.networking.ReviewList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewFetchSyncTask extends AsyncTask<Long, Void, List<ReviewEntity>> {

    private final Listener myListener;
    public static String MY_LOG_TAG = ReviewFetchSyncTask.class.getSimpleName();


    interface Listener {
        void onReviewsFetchFinished(List<ReviewEntity> reviewEntities);
    }

    public ReviewFetchSyncTask(Listener listener) {
        myListener = listener;
    }

    @Override
    protected void onPostExecute(List<ReviewEntity> reviewEntities) {
        if (reviewEntities != null) {
            myListener.onReviewsFetchFinished(reviewEntities);
        } else {
            myListener.onReviewsFetchFinished(new ArrayList<ReviewEntity>());
        }
    }

    @Override
    protected List<ReviewEntity> doInBackground(Long... params) {
        if (params.length == 0) {
            return null;
        }
        long theMovieId = params[0];

        Retrofit MyRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheMovieEngineService theMovieEngineService = MyRetrofit.create(TheMovieEngineService.class);
        Call<ReviewList> reviewListCall = theMovieEngineService.findReviewsById(theMovieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<ReviewList> reviewListResponse = reviewListCall.execute();
            ReviewList myReviewList = reviewListResponse.body();
            return myReviewList.getReviewEntities();
        } catch (IOException e) {
            Log.e(MY_LOG_TAG, "A problem occurred talking to the movie db ", e);
        }

        return null;
    }


}
