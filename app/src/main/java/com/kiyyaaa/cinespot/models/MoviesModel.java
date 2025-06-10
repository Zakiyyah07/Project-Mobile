package com.kiyyaaa.cinespot.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MoviesModel implements Parcelable {

    private String id;
    @SerializedName("rank")
    private String rank;
    @SerializedName("Series_Title")
    private String seriesTitle;

    @SerializedName("Released_Year")
    private String releasedYear;

    @SerializedName("Genre")
    private String genre;

    @SerializedName("IMDB_Rating")
    private String imdbRating;

    @SerializedName("Overview")
    private String overview;

    @SerializedName("Poster_Link")
    private String posterLink;

    public MoviesModel() {}

    protected MoviesModel(Parcel in) {
        id = in.readString();
        rank = in.readString();
        seriesTitle = in.readString();
        releasedYear = in.readString();
        genre = in.readString();
        imdbRating = in.readString();
        overview = in.readString();
        posterLink = in.readString();
    }

    public static final Creator<MoviesModel> CREATOR = new Creator<MoviesModel>() {
        @Override
        public MoviesModel createFromParcel(Parcel in) {
            return new MoviesModel(in);
        }

        @Override
        public MoviesModel[] newArray(int size) {
            return new MoviesModel[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getSeriesTitle() {
        return seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getReleasedYear() {
        return releasedYear;
    }

    public void setReleasedYear(String releasedYear) {
        this.releasedYear = releasedYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterLink() {
        return posterLink;
    }

    public void setPosterLink(String posterLink) {
        this.posterLink = posterLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(rank);
        parcel.writeString(seriesTitle);
        parcel.writeString(releasedYear);
        parcel.writeString(genre);
        parcel.writeString(imdbRating);
        parcel.writeString(overview);
        parcel.writeString(posterLink);
    }
}