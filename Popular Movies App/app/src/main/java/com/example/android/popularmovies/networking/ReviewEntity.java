package com.example.android.popularmovies.networking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ReviewEntity implements Parcelable {

    @SerializedName("url")
    private String myUrl;
    @SerializedName("author")
    private String myAuthor;
    @SerializedName("content")
    private String myContent;
    @SerializedName("id")
    private String myId;


    public String getMyUrl() {
        return myUrl;
    }

    public String getMyContent() {
        return myContent;
    }

    public String getMyAuthor() {
        return myAuthor;
    }

    public static final Parcelable.Creator<ReviewEntity> CREATOR = new Creator<ReviewEntity>() {
        public ReviewEntity createFromParcel(Parcel mySource) {
            ReviewEntity reviewEntity = new ReviewEntity();
            reviewEntity.myId = mySource.readString();
            reviewEntity.myAuthor = mySource.readString();
            reviewEntity.myContent = mySource.readString();
            reviewEntity.myUrl = mySource.readString();
            return reviewEntity;
        }

        public ReviewEntity[] newArray(int array_size) {
            return new ReviewEntity[array_size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcellable, int flags) {
        parcellable.writeString(myId);
        parcellable.writeString(myAuthor);
        parcellable.writeString(myContent);
        parcellable.writeString(myUrl);
    }
}
