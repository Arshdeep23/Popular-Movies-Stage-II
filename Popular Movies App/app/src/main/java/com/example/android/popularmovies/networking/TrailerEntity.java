package com.example.android.popularmovies.networking;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class TrailerEntity implements Parcelable {

    @SuppressWarnings("unused")
    public static final String MY_LOG_TAG = TrailerEntity.class.getSimpleName();

    @SerializedName("size")
    private String mySize;
    @SerializedName("id")
    private String myId;
    @SerializedName("name")
    private String myName;
    @SerializedName("key")
    private String myKey;
    @SerializedName("site")
    private String mySite;

    private TrailerEntity() {
    }

    public String getMyKey() {
        return myKey;
    }

    public String getMyName() {
        return myName;
    }

    public String getMyTrailerUrl() {
        return "http://www.youtube.com/watch?v=" + myKey;
    }

    public static final Parcelable.Creator<TrailerEntity> CREATOR = new Creator<TrailerEntity>() {
        public TrailerEntity createFromParcel(Parcel mySource) {
            TrailerEntity trailerEntity = new TrailerEntity();
            trailerEntity.myId = mySource.readString();
            trailerEntity.myKey = mySource.readString();
            trailerEntity.myName = mySource.readString();
            trailerEntity.mySite = mySource.readString();
            trailerEntity.mySize = mySource.readString();
            return trailerEntity;
        }

        public TrailerEntity[] newArray(int array_size) {
            return new TrailerEntity[array_size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcellable, int flags) {
        parcellable.writeString(myId);
        parcellable.writeString(myKey);
        parcellable.writeString(myName);
        parcellable.writeString(mySite);
        parcellable.writeString(mySize);
    }
}
