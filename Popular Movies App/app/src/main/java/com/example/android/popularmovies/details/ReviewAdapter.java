package com.example.android.popularmovies.details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.networking.ReviewEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final Callbacks myCallbacks;
    private final ArrayList<ReviewEntity> myReviewEntities;
    private final static String MY_LOG_TAG = ReviewAdapter.class.getSimpleName();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View myView;
        @Bind(R.id.my_content_review)
        TextView myContentView;
        @Bind(R.id.my_author_review)
        TextView myAuthorView;
        public ReviewEntity myReviewEntity;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            myView = view;
        }
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ReviewEntity reviewEntity = myReviewEntities.get(position);

        holder.myReviewEntity = reviewEntity;
        holder.myContentView.setText(reviewEntity.getMyContent());
        holder.myAuthorView.setText(reviewEntity.getMyAuthor());

        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCallbacks.read(reviewEntity, holder.getAdapterPosition());
            }
        });
    }

    public interface Callbacks {
        void read(ReviewEntity reviewEntity, int position);
    }



    public ReviewAdapter(ArrayList<ReviewEntity> reviewEntities, Callbacks callbacks) {
        myCallbacks = callbacks;
        myReviewEntities = reviewEntities;
    }

    @Override
    public int getItemCount() {
        return myReviewEntities.size();
    }

    public void add(List<ReviewEntity> reviewEntities) {
        myReviewEntities.clear();
        myReviewEntities.addAll(reviewEntities);
        notifyDataSetChanged();
    }

    public ArrayList<ReviewEntity> getReviews() {
        return myReviewEntities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.individual_review_content, parent, false);
        return new ViewHolder(rootview);
    }


}