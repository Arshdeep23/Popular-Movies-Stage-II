package com.example.android.popularmovies.details;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.networking.MovieEntity;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.networking.ReviewEntity;
import com.example.android.popularmovies.networking.TrailerEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentDetailMoviesPopulator extends Fragment implements TrailerFetchSyncTask.Listener,
        TrailerAdapter.Callbacks, ReviewFetchSyncTask.Listener, ReviewAdapter.Callbacks {


    private ShareActionProvider share_action_provider;
    private MovieEntity movie_entity;
    private ReviewAdapter review_adapter;
    private TrailerAdapter trailer_adapter;

    public static final String BACKUP_REVIEWS = "BACKUP_REVIEWS";
    public static final String BACKUP_TRAILERS = "BACKUP_TRAILERS";
    public static final String ARGUMENT_MOVIE = "ARGUMENT_MOVIE";

    public static final String MY_LOG_TAG = FragmentDetailMoviesPopulator.class.getSimpleName();


    @Bind(R.id.my_latest_trailer_list)
    RecyclerView recycle_view_trailers;
    @Bind(R.id.your_review_list)
    RecyclerView recycler_view_reviews;

    @Bind(R.id.my_movie_date_release)
    TextView movie_release_date;
    @Bind(R.id.my_movie_overview_text)
    TextView movie_overview;
    @Bind(R.id.my_movie_rating_user)
    TextView movie_rating_view;
    @Bind({R.id.my_rating_star_first, R.id.my_rating_star_second, R.id.my_rating_star_third,
            R.id.my_rating_star_fourth, R.id.my_rating_star_fifth})
    List<ImageView> ratingStarMyViews;
    @Bind(R.id.button_add_to_favorite)
    Button button_mark_favourite;
    @Bind(R.id.my_button_to_watch_trailer)
    Button button_trailer_watch;
    @Bind(R.id.button_remove_favorites)
    Button button_remove_favourites;
    @Bind(R.id.my_movie_title_details)
    TextView movie_title_view;
    @Bind(R.id.my_movie_details_poster)
    ImageView movie_poster_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_main_detail, container, false);
        ButterKnife.bind(this, rootView);

        movie_title_view.setText(movie_entity.getMyTitle());
        movie_overview.setText(movie_entity.getMyOverview());
        movie_release_date.setText(movie_entity.getMyReleaseDate(getContext()));

        Picasso.with(getContext())
                .load(movie_entity.getMyPosterUrl(getContext()))
                .config(Bitmap.Config.RGB_565)
                .into(movie_poster_view);

        populateStars();
        populate_button_favourite();

        LinearLayoutManager layoutManager_detail
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recycle_view_trailers.setLayoutManager(layoutManager_detail);
        trailer_adapter = new TrailerAdapter(new ArrayList<TrailerEntity>(), this);
        recycle_view_trailers.setAdapter(trailer_adapter);
        recycle_view_trailers.setNestedScrollingEnabled(false);
        review_adapter = new ReviewAdapter(new ArrayList<ReviewEntity>(), this);
        recycler_view_reviews.setAdapter(review_adapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(BACKUP_TRAILERS)) {
            List<TrailerEntity> trailerEntities = savedInstanceState.getParcelableArrayList(BACKUP_TRAILERS);
            trailer_adapter.add(trailerEntities);
            button_trailer_watch.setEnabled(true);
        } else {
            display_trailers();
        }
        if (savedInstanceState != null && savedInstanceState.containsKey(BACKUP_REVIEWS)) {
            List<ReviewEntity> reviewEntities = savedInstanceState.getParcelableArrayList(BACKUP_REVIEWS);
            review_adapter.add(reviewEntities);
        } else {
            display_reviews();
        }

        return rootView;
    }

    public FragmentDetailMoviesPopulator() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<TrailerEntity> trailerEntities = trailer_adapter.getTrailers();
        if (!trailerEntities.isEmpty() && trailerEntities != null) {
            outState.putParcelableArrayList(BACKUP_TRAILERS, trailerEntities);
        }

        ArrayList<ReviewEntity> reviewEntities = review_adapter.getReviews();
        if (!reviewEntities.isEmpty() && reviewEntities != null) {
            outState.putParcelableArrayList(BACKUP_REVIEWS, reviewEntities);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_fragment_details_populator, menu);
        MenuItem shareTrailerMenuItem = menu.findItem(R.id.menu_share_trailer_option);
        share_action_provider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareTrailerMenuItem);
    }


    @Override
    public void onReviewsFetchFinished(List<ReviewEntity> reviewEntities) {
        review_adapter.add(reviewEntities);
    }

    private void populate_button_favourite() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                return isFavorite();
            }

            @Override
            protected void onPostExecute(Boolean isFavorite) {
                if (isFavorite) {
                    button_remove_favourites.setVisibility(View.VISIBLE);
                    button_mark_favourite.setVisibility(View.GONE);
                } else {
                    button_mark_favourite.setVisibility(View.VISIBLE);
                    button_remove_favourites.setVisibility(View.GONE);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        button_mark_favourite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mark_as_favorite();
                    }
                });

        button_trailer_watch.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (trailer_adapter.getItemCount() > 0) {
                            watch(trailer_adapter.getTrailers().get(0), 0);
                        }
                    }
                });

        button_remove_favourites.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        remove_favorites();
                    }
                });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARGUMENT_MOVIE)) {
            movie_entity = getArguments().getParcelable(ARGUMENT_MOVIE);
        }
        setHasOptionsMenu(true);
    }


    private boolean isFavorite() {
        Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._COLUMN_MOVIE_ID},
                MovieContract.MovieEntry._COLUMN_MOVIE_ID + " = " + movie_entity.getMyId(),
                null,
                null);

        if (movieCursor != null && movieCursor.moveToFirst()) {
            movieCursor.close();
            return true;
        } else {
            return false;
        }
    }



    public void remove_favorites() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (isFavorite()) {
                    getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry._COLUMN_MOVIE_ID + " = " + movie_entity.getMyId(), null);

                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                populate_button_favourite();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void populate_share_action_provider(TrailerEntity trailerEntity) {
        Intent share_intent = new Intent(android.content.Intent.ACTION_SEND);
        share_intent.setType("text/plain");
        share_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, movie_entity.getMyTitle());
        share_intent.putExtra(android.content.Intent.EXTRA_TEXT, trailerEntity.getMyName() + ": "
                + trailerEntity.getMyTrailerUrl());
        share_action_provider.setShareIntent(share_intent);
    }

    private void populateStars() {
        if (movie_entity.getMyUserRating() != null && !movie_entity.getMyUserRating().isEmpty()) {
            String user_rating_string = getResources().getString(R.string.movie_rating_key,
                    movie_entity.getMyUserRating());
            movie_rating_view.setText(user_rating_string);

            float rating_user = Float.valueOf(movie_entity.getMyUserRating()) / 2;
            int intPart = (int) rating_user;

            for (int i = 0; i < intPart; i++) {
                ratingStarMyViews.get(i).setImageResource(R.drawable.ic_star_icon_black_24dp);
            }

            if (Math.round(rating_user) > intPart) {
                ratingStarMyViews.get(intPart).setImageResource(
                        R.drawable.ic_star_icon_half_black_24dp);
            }

        } else {
            movie_rating_view.setVisibility(View.GONE);
        }
    }
    private void display_trailers() {
        TrailerFetchSyncTask trailerFetchSyncTask = new TrailerFetchSyncTask(this);
        trailerFetchSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie_entity.getMyId());
    }

    private void display_reviews() {
        ReviewFetchSyncTask reviewFetchSyncTask = new ReviewFetchSyncTask(this);
        reviewFetchSyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, movie_entity.getMyId());
    }

    @Override
    public void watch(TrailerEntity trailerEntity, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerEntity.getMyTrailerUrl())));
    }

    @Override
    public void read(ReviewEntity reviewEntity, int position) {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse(reviewEntity.getMyUrl())));
    }

    public void mark_as_favorite() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                if (!isFavorite()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_ID,
                            movie_entity.getMyId());
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_TITLE,
                            movie_entity.getMyTitle());
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_POSTER_PATH,
                            movie_entity.getMyPoster());
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_OVERVIEW,
                            movie_entity.getMyOverview());
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_VOTE_AVERAGE,
                            movie_entity.getMyUserRating());
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_RELEASE_DATE,
                            movie_entity.getMyReleaseDate());
                    contentValues.put(MovieContract.MovieEntry._COLUMN_MOVIE_BACKDROP_PATH,
                            movie_entity.getBackdrop());
                    getContext().getContentResolver().insert(
                            MovieContract.MovieEntry.CONTENT_URI,
                            contentValues
                    );
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                populate_button_favourite();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void onFetchFinished(List<TrailerEntity> trailerEntities) {
        trailer_adapter.add(trailerEntities);
        button_trailer_watch.setEnabled(!trailerEntities.isEmpty());

        if (trailer_adapter.getItemCount() > 0) {
            TrailerEntity trailerEntity = trailer_adapter.getTrailers().get(0);
            populate_share_action_provider(trailerEntity);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();
        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout)
                activity.findViewById(R.id.my_toolbar_layout);
        if (activity instanceof ActivityDetailsMoviePopulator && appBarLayout != null) {
            appBarLayout.setTitle(movie_entity.getMyTitle());
        }

        ImageView movieBackdrop = ((ImageView) activity.findViewById(R.id.my_movie_backdrop_image));
        if (movieBackdrop != null) {
            Picasso.with(activity)
                    .load(movie_entity.getMyBackdropUrl(getContext()))
                    .config(Bitmap.Config.RGB_565)
                    .into(movieBackdrop);
        }
    }

}
