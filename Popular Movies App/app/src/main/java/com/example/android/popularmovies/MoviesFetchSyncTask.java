package com.example.android.popularmovies;

import android.os.AsyncTask;
import android.support.annotation.StringDef;
import android.util.Log;

import com.example.android.popularmovies.networking.MovieEntity;
import com.example.android.popularmovies.networking.TheMovieEngineService;
import com.example.android.popularmovies.networking.MovieList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesFetchSyncTask extends AsyncTask<Void, Void, List<MovieEntity>> {

    public final static String STRING_TOP_RATED = "top_rated";
    public final static String STRING_FAVORITES = "favorites";
    public final static String STRING_MOST_POPULAR = "popular";

    public static String MY_LOG_TAG = MoviesFetchSyncTask.class.getSimpleName();

    @StringDef({STRING_MOST_POPULAR, STRING_TOP_RATED, STRING_FAVORITES})
    public @interface SORT_BY {
    }
    private final NotifyAboutTaskExecutionCommand myDirection;
    private
    @SORT_BY
    String mySortOrder = STRING_MOST_POPULAR;

    interface Listener {
        void onFetchFinished(ChiefDirective chiefDirective);
    }


    public MoviesFetchSyncTask(@SORT_BY String sortBy, NotifyAboutTaskExecutionCommand command) {
        mySortOrder = sortBy;
        myDirection = command;
    }

    @Override
    protected List<MovieEntity> doInBackground(Void... params) {

        Retrofit myRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TheMovieEngineService theMovieEngineService = myRetrofit.create(TheMovieEngineService.class);
        Call<MovieList> movieListCall = theMovieEngineService.discoverMovies(mySortOrder,
                BuildConfig.THE_MOVIE_DATABASE_API_KEY);
        try {
            Response<MovieList> listResponse = movieListCall.execute();
            MovieList my_movie_list = listResponse.body();
            return my_movie_list.getMoviesCollection();

        } catch (IOException e) {
            Log.e(MY_LOG_TAG, "Could not fetch movies!!!", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<MovieEntity> movies) {
        if (movies != null) {
            myDirection.myMovies = movies;
        } else {
            myDirection.myMovies = new ArrayList<>();
        }
        myDirection.executeNow();
    }

    public static class NotifyAboutTaskExecutionCommand implements ChiefDirective {
        private List<MovieEntity> myMovies;
        private MoviesFetchSyncTask.Listener myListener;

        public List<MovieEntity> getMovies() {
            return myMovies;
        }

        @Override
        public void executeNow() {
            myListener.onFetchFinished(this);
        }

        public NotifyAboutTaskExecutionCommand(MoviesFetchSyncTask.Listener listener) {
            myListener = listener;
        }


    }
}
