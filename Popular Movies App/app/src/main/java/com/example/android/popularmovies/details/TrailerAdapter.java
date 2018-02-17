package com.example.android.popularmovies.details;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.networking.TrailerEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder> {

    private final static String MY_LOG_TAG = TrailerAdapter.class.getSimpleName();

    private final ArrayList<TrailerEntity> myTrailerEntities;
    private final Callbacks myCallbacks;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View myView;
        @Bind(R.id.my_thumbnail_trailer)
        ImageView myThumbnailView;
        public TrailerEntity myTrailerEntity;

        public ViewHolder(View rootview) {
            super(rootview);
            ButterKnife.bind(this, rootview);
            myView = rootview;
        }
    }

    public interface Callbacks {
        void watch(TrailerEntity trailerEntity, int position);
    }

    public TrailerAdapter(ArrayList<TrailerEntity> trailerEntities, Callbacks callbacks) {
        myTrailerEntities = trailerEntities;
        myCallbacks = callbacks;
    }

    public void add(List<TrailerEntity> trailerEntities) {
        myTrailerEntities.clear();
        myTrailerEntities.addAll(trailerEntities);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TrailerEntity trailerEntity = myTrailerEntities.get(position);
        final Context context = holder.myView.getContext();

        float padLeft = 0;
        if (position == 0) {
            padLeft = context.getResources().getDimension(R.dimen.detail_of_horizontal_padding);
        }

        float padRight = 0;
        if (position + 1 != getItemCount()) {
            padRight = context.getResources().getDimension(R.dimen.detail_of_horizontal_padding) / 2;
        }

        holder.myView.setPadding((int) padLeft, 0, (int) padRight, 0);

        holder.myTrailerEntity = trailerEntity;

        String myThumbnailUrl = "http://img.youtube.com/vi/" + trailerEntity.getMyKey() + "/0.jpg";
        Log.i(MY_LOG_TAG, "thumbnailUrl -> " + myThumbnailUrl);

        Picasso.with(context)
                .load(myThumbnailUrl)
                .config(Bitmap.Config.RGB_565)
                .into(holder.myThumbnailView);

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCallbacks.watch(trailerEntity, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return myTrailerEntities.size();
    }



    public ArrayList<TrailerEntity> getTrailers() {
        return myTrailerEntities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_trailer_content, parent, false);
        return new ViewHolder(rootview);
    }
}

