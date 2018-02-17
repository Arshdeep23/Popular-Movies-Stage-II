package com.example.android.popularmovies.networking;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MovieEntity implements Parcelable {

    public static final String MY_LOG_TAG = MovieEntity.class.getSimpleName();
    public static final float MY_POSTER_ASPECT_RATIO = 1.5f;



    @SerializedName("poster_path")
    private String myPoster;
    @SerializedName("overview")
    private String myOverview;
    @SerializedName("vote_average")
    private String myUserRating;
    @SerializedName("id")
    private long myId;
    @SerializedName("original_title")
    private String myTitle;
    @SerializedName("backdrop_path")
    private String myBackdrop;
    @SerializedName("release_date")
    private String myReleaseDate;

    private MovieEntity() {
    }

    public MovieEntity(long id, String title, String poster, String overview, String userRating,
                       String releaseDate, String backdrop) {
        myUserRating = userRating;
        myBackdrop = backdrop;
        myReleaseDate = releaseDate;
        myPoster = poster;
        myOverview = overview;
        myId = id;
        myTitle = title;
    }



    public long getMyId() {
        return myId;
    }

    @Nullable
    public String getMyTitle() {
        return myTitle;
    }

    public String getMyReleaseDate(Context context) {
        String myInputPattern = "yyyy-MM-dd";
        SimpleDateFormat myInputFormat = new SimpleDateFormat(myInputPattern, Locale.US);
        if (myReleaseDate != null && !myReleaseDate.isEmpty()) {
            try {
                Date myDate = myInputFormat.parse(myReleaseDate);
                return DateFormat.getDateInstance().format(myDate);
            } catch (ParseException except) {
                Log.e(MY_LOG_TAG, "The Release data was not parsed successfully: " + myReleaseDate);
            }
        } else {
            myReleaseDate = context.getString(R.string.date_key_missing);
        }

        return myReleaseDate;
    }

    public String getMyPoster() {
        return myPoster;
    }

    @Nullable
    public String getMyPosterUrl(Context context) {
        if (myPoster != null && !myPoster.isEmpty()) {
            return context.getResources().getString(R.string.url_string_downloading_poster) + myPoster;
        }
        return null;
    }



    public String getMyReleaseDate() {
        return myReleaseDate;
    }

    @Nullable
    public String getMyOverview() {
        return myOverview;
    }

    @Nullable
    public String getMyUserRating() {
        return myUserRating;
    }

    @Nullable
    public String getMyBackdropUrl(Context context) {
        if (myBackdrop != null && !myBackdrop.isEmpty()) {
            return context.getResources().getString(R.string.url_string_downloading_backdrop) +
                    myBackdrop;
        }
        return null;
    }

    public String getBackdrop() {
        return myBackdrop;
    }

    public static final Parcelable.Creator<MovieEntity> CREATOR = new Creator<MovieEntity>() {
        public MovieEntity createFromParcel(Parcel mySource) {
            MovieEntity movieEntity = new MovieEntity();
            movieEntity.myId = mySource.readLong();
            movieEntity.myTitle = mySource.readString();
            movieEntity.myPoster = mySource.readString();
            movieEntity.myOverview = mySource.readString();
            movieEntity.myUserRating = mySource.readString();
            movieEntity.myReleaseDate = mySource.readString();
            movieEntity.myBackdrop = mySource.readString();
            return movieEntity;
        }

        public MovieEntity[] newArray(int size) {
            return new MovieEntity[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcellable, int flags) {
        parcellable.writeLong(myId);
        parcellable.writeString(myTitle);
        parcellable.writeString(myPoster);
        parcellable.writeString(myOverview);
        parcellable.writeString(myUserRating);
        parcellable.writeString(myReleaseDate);
        parcellable.writeString(myBackdrop);
    }
}
