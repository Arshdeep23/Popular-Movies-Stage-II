package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.networking.MovieEntity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieAdapter
        extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {


    private final ArrayList<MovieEntity> myMovies;
    private final static String LOG_TAG = MovieAdapter.class.getSimpleName();
    private final Callbacks myCallbacks;

    public MovieAdapter(ArrayList<MovieEntity> movies, Callbacks callbacks) {
        this.myCallbacks = callbacks;
        myMovies = movies;
    }

    public interface Callbacks {
        void open(MovieEntity movieEntity, int position);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MovieEntity movieEntity = myMovies.get(position);
        final Context context = holder.myView.getContext();

        holder.myMovieEntity = movieEntity;
        holder.myTitleView.setText(movieEntity.getMyTitle());

        String url_for_poster = movieEntity.getMyPosterUrl(context);
        if (url_for_poster == null) {
            holder.myTitleView.setVisibility(View.VISIBLE);
        }

        Picasso.with(context)
                .load(movieEntity.getMyPosterUrl(context))
                .config(Bitmap.Config.RGB_565)
                .into(holder.myThumbnailView,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                if (holder.myMovieEntity.getMyId() != movieEntity.getMyId()) {
                                    holder.cleanOut();
                                } else {
                                    holder.myThumbnailView.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onError() {
                                holder.myTitleView.setVisibility(View.VISIBLE);
                            }
                        }
                );

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCallbacks.open(movieEntity, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_individual_content, parent, false);
        final Context context = rootview.getContext();
        int grid_cols_number = context.getResources()
                .getInteger(R.integer.grid_number_cols);

        rootview.getLayoutParams().height = (int) (parent.getWidth() / grid_cols_number *
                MovieEntity.MY_POSTER_ASPECT_RATIO);

        return new ViewHolder(rootview);
    }



    @Override
    public int getItemCount() {
        return myMovies.size();
    }


    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanOut();
    }

    public void add(List<MovieEntity> movies) {
        myMovies.clear();
        myMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View myView;
        @Bind(R.id.my_movie_list_thumbnail)
        ImageView myThumbnailView;
        @Bind(R.id.title)
        TextView myTitleView;
        public MovieEntity myMovieEntity;

        public void cleanOut() {
            final Context cleanContext = myView.getContext();
            myTitleView.setVisibility(View.GONE);
            Picasso.with(cleanContext).cancelRequest(myThumbnailView);
            myThumbnailView.setVisibility(View.INVISIBLE);
            myThumbnailView.setImageBitmap(null);
        }

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            myView = view;
        }

    }

    public ArrayList<MovieEntity> getMovies() {
        return myMovies;
    }

    public void add(Cursor cursor) {
        myMovies.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String my_title = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_TITLE);
                String my_rating = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_VOTE_AVERAGE);
                long my_id = cursor.getLong(MovieContract.MovieEntry.COL_MOVIE_ID);
                String my_releaseDate = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_RELEASE_DATE);
                String my_backdropPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_BACKDROP_PATH);
                String my_posterPath = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_POSTER_PATH);
                String my_overview = cursor.getString(MovieContract.MovieEntry.COL_MOVIE_OVERVIEW);

                MovieEntity movieEntity = new MovieEntity(my_id, my_title, my_posterPath, my_overview, my_rating, my_releaseDate, my_backdropPath);
                myMovies.add(movieEntity);
            } while (cursor.moveToNext());
        }
        notifyDataSetChanged();
    }

}
