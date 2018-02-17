package com.example.android.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.details.ActivityDetailsMoviePopulator;
import com.example.android.popularmovies.details.FragmentDetailMoviesPopulator;
import com.example.android.popularmovies.networking.MovieEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoviePopulatorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        MoviesFetchSyncTask.Listener, MovieAdapter.Callbacks {

    private boolean dual_window;
    private MovFragment movFragment;
    private MovieAdapter movAdapter;
    private static final String BACKUP_MOVIES = "BACKUP_MOVIES";
    private static final String BACKUP_SORTBY = "BACKUP_SORTBY";
    private String mySortAs = MoviesFetchSyncTask.STRING_MOST_POPULAR;
    private static final int FAV_MOVIES_LOADER = 0;

    @Bind(R.id.my_toolbar)
    Toolbar my_toolbar;
    @Bind(R.id.my_list_movie)
    RecyclerView recycler_view;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<MovieEntity> movieEntities = movAdapter.getMovies();
        if (movieEntities != null && !movieEntities.isEmpty()) {
            outState.putParcelableArrayList(BACKUP_MOVIES, movieEntities);
        }
        outState.putString(BACKUP_SORTBY, mySortAs);

        if (!mySortAs.equals(MoviesFetchSyncTask.STRING_FAVORITES)) {
            getSupportLoaderManager().destroyLoader(FAV_MOVIES_LOADER);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_movie_activity, menu);

        switch (mySortAs) {
            case MoviesFetchSyncTask.STRING_MOST_POPULAR:
                menu.findItem(R.id.menu_option_sort_by_most_popular).setChecked(true);
                break;
            case MoviesFetchSyncTask.STRING_TOP_RATED:
                menu.findItem(R.id.menu_option_sort_by_top_rated).setChecked(true);
                break;
            case MoviesFetchSyncTask.STRING_FAVORITES:
                menu.findItem(R.id.menu_option_sort_by_favorites).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);
        String name = MovFragment.class.getName();
        this.movFragment = (MovFragment) getSupportFragmentManager().findFragmentByTag(name);
        if (this.movFragment == null) {
            this.movFragment = new MovFragment();
            getSupportFragmentManager().beginTransaction().add(this.movFragment, name).commit();
        }
        my_toolbar.setTitle(R.string.title_movie_key);
        setSupportActionBar(my_toolbar);



        movAdapter = new MovieAdapter(new ArrayList<MovieEntity>(), this);
        recycler_view.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.grid_number_cols)));
        recycler_view.setAdapter(movAdapter);

        dual_window = findViewById(R.id.my_movie_container_detail) != null;

        if (savedInstanceState != null) {
            mySortAs = savedInstanceState.getString(BACKUP_SORTBY);
            if (savedInstanceState.containsKey(BACKUP_MOVIES)) {
                List<MovieEntity> movieEntities = savedInstanceState.getParcelableArrayList(BACKUP_MOVIES);
                movAdapter.add(movieEntities);
                findViewById(R.id.my_progress_bar).setVisibility(View.GONE);

                if (mySortAs.equals(MoviesFetchSyncTask.STRING_FAVORITES)) {
                    getSupportLoaderManager().initLoader(FAV_MOVIES_LOADER, null, this);
                }
            }
            populateNoMoviesMessage();
        } else {
            display_movies(mySortAs);
        }
    }


    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        movAdapter.add(cursor);
        populateNoMoviesMessage();
        findViewById(R.id.my_progress_bar).setVisibility(View.GONE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        findViewById(R.id.my_progress_bar).setVisibility(View.VISIBLE);
        return new CursorLoader(this,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
    }

    private void display_movies(String sortBy) {
        if (sortBy.equals(MoviesFetchSyncTask.STRING_FAVORITES)) {
            getSupportLoaderManager().initLoader(FAV_MOVIES_LOADER, null, this);
        } else {
            findViewById(R.id.my_progress_bar).setVisibility(View.VISIBLE);
            MoviesFetchSyncTask.NotifyAboutTaskExecutionCommand taskCompletionCommand =
                    new MoviesFetchSyncTask.NotifyAboutTaskExecutionCommand(this.movFragment);
            new MoviesFetchSyncTask(sortBy, taskCompletionCommand).execute();        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_option_sort_by_top_rated:
                if (mySortAs.equals(MoviesFetchSyncTask.STRING_FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAV_MOVIES_LOADER);
                }
                mySortAs = MoviesFetchSyncTask.STRING_TOP_RATED;
                display_movies(mySortAs);
                item.setChecked(true);
                break;
            case R.id.menu_option_sort_by_most_popular:
                if (mySortAs.equals(MoviesFetchSyncTask.STRING_FAVORITES)) {
                    getSupportLoaderManager().destroyLoader(FAV_MOVIES_LOADER);
                }
                mySortAs = MoviesFetchSyncTask.STRING_MOST_POPULAR;
                display_movies(mySortAs);
                item.setChecked(true);
                break;
            case R.id.menu_option_sort_by_favorites:
                mySortAs = MoviesFetchSyncTask.STRING_FAVORITES;
                item.setChecked(true);
                display_movies(mySortAs);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void open(MovieEntity movieEntity, int position) {
        if (dual_window) {
            Bundle argz = new Bundle();
            argz.putParcelable(FragmentDetailMoviesPopulator.ARGUMENT_MOVIE, movieEntity);
            FragmentDetailMoviesPopulator fragment = new FragmentDetailMoviesPopulator();
            fragment.setArguments(argz);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.my_movie_container_detail, fragment)
                    .commit();
        } else {
            Intent myIntent = new Intent(this, ActivityDetailsMoviePopulator.class);
            myIntent.putExtra(FragmentDetailMoviesPopulator.ARGUMENT_MOVIE, movieEntity);
            startActivity(myIntent);
        }
    }

    public static class MovFragment extends Fragment implements MoviesFetchSyncTask.Listener {
        private boolean myPaused = false;
        private ChiefDirective waiting_chief_directive = null;

        public MovFragment() {
        }

        @Override
        public void onFetchFinished(ChiefDirective chiefDirective) {
            if (getActivity() instanceof MoviesFetchSyncTask.Listener && !myPaused) {
                MoviesFetchSyncTask.Listener movies_fetch_sync_task_listener = (MoviesFetchSyncTask.Listener) getActivity();
                movies_fetch_sync_task_listener.onFetchFinished(chiefDirective);
                waiting_chief_directive = null;
            } else {
                waiting_chief_directive = chiefDirective;
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            myPaused = false;
            if (waiting_chief_directive != null) {
                onFetchFinished(waiting_chief_directive);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            myPaused = true;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }

    @Override
    public void onFetchFinished(ChiefDirective chiefDirective) {
        if (chiefDirective instanceof MoviesFetchSyncTask.NotifyAboutTaskExecutionCommand) {
            movAdapter.add(((MoviesFetchSyncTask.NotifyAboutTaskExecutionCommand) chiefDirective).getMovies());
            populateNoMoviesMessage();
            findViewById(R.id.my_progress_bar).setVisibility(View.GONE);
        }
    }

    private void populateNoMoviesMessage() {
        if (movAdapter.getItemCount() == 0) {
            if (!(mySortAs.equals(MoviesFetchSyncTask.STRING_FAVORITES))) {
                findViewById(R.id.no_state_empty_container).setVisibility(View.VISIBLE);
                findViewById(R.id.no_state_empty_favorites_container).setVisibility(View.GONE);
            } else {
                findViewById(R.id.no_state_empty_container).setVisibility(View.GONE);
                findViewById(R.id.no_state_empty_favorites_container).setVisibility(View.VISIBLE);
            }
        } else {
            findViewById(R.id.no_state_empty_container).setVisibility(View.GONE);
            findViewById(R.id.no_state_empty_favorites_container).setVisibility(View.GONE);
        }
    }
}
