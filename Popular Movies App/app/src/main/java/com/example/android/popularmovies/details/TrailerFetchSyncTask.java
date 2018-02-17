package com.example.android.popularmovies.details;

import android.os.AsyncTask;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.networking.TheMovieEngineService;
import com.example.android.popularmovies.networking.TrailerEntity;
import com.example.android.popularmovies.networking.TrailerList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrailerFetchSyncTask extends AsyncTask<Long, Void, List<TrailerEntity>> {

    private final Listener myListener;
    public static String MY_LOG_TAG = TrailerFetchSyncTask.class.getSimpleName();


    interface Listener {
        void onFetchFinished(List<TrailerEntity> trailerEntities);
    }

    public TrailerFetchSyncTask(Listener listener) {
        myListener = listener;
    }

    @Override
    protected void onPostExecute(List<TrailerEntity> trailerEntities) {
        if (trailerEntities != null) {
            myListener.onFetchFinished(trailerEntities);
        } else {
            myListener.onFetchFinished(new ArrayList<TrailerEntity>());
        }
    }

    @Override
    protected List<TrailerEntity> doInBackground(Long... params) {
        if (params.length == 0) {
            return null;
        }
        long theMovieId = params[0];

        Retrofit MyRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheMovieEngineService theMovieEngineService = MyRetrofit.create(TheMovieEngineService.class);
        Call<TrailerList> trailerListCall = theMovieEngineService.findTrailersById(theMovieId,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<TrailerList> trailerListResponse = trailerListCall.execute();
            TrailerList myTrailerList = trailerListResponse.body();
            return myTrailerList.getTrailerEntities();
        } catch (IOException e) {
            Log.e(MY_LOG_TAG, "Trailer could not be fetched!!!", e);
        }

        return null;
    }


}
