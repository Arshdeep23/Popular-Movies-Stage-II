package com.example.android.popularmovies.details;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.android.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ActivityDetailsMoviePopulator extends AppCompatActivity {

    @Bind(R.id.my_detail_toolbar)
    Toolbar myToolbar;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_populator_movie_detail);
        ButterKnife.bind(this);
        ActionBar movie_detailed_action_bar = getSupportActionBar();
        if (movie_detailed_action_bar != null) {
            movie_detailed_action_bar.setDisplayHomeAsUpEnabled(true);
        }
        setSupportActionBar(myToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(FragmentDetailMoviesPopulator.ARGUMENT_MOVIE,
                    getIntent().getParcelableExtra(FragmentDetailMoviesPopulator.ARGUMENT_MOVIE));
            FragmentDetailMoviesPopulator fragmentDetailMoviesPopulator = new FragmentDetailMoviesPopulator();
            fragmentDetailMoviesPopulator.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.my_movie_container_detail, fragmentDetailMoviesPopulator)
                    .commit();
        }
    }


}
