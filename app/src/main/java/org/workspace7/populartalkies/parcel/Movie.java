/*******************************************************************************
 *   Copyright (c) 2016 Kamesh Sampath<kamesh.sampath@hotmail.com)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 ******************************************************************************/

package org.workspace7.populartalkies.parcel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {

            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {

            return new Movie[size];
        }
    };

    public Movie() {

    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(synopsis);
        dest.writeString(posterPath);
        dest.writeString(releaseDate);
        dest.writeFloat(voteAverage);
        dest.writeInt(runtime);
        dest.writeByte(favorite);
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getSynopsis() {
        return synopsis;
    }
    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }
    public float getVoteAverage() {
        return voteAverage;
    }
    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
    }
    public int getRuntime() {
        return runtime;
    }
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
    public String getPosterPath() {
        return posterPath;
    }
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
    public String getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public byte getFavorite() {
        return favorite;
    }
    public void setFavorite(byte favorite) {
        this.favorite = favorite;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return id == movie.id;

    }
    @Override
    public int hashCode() {
        return id;
    }

    private Movie(Parcel in) {

        id = in.readInt();
        title = in.readString();
        synopsis = in.readString();
        posterPath = in.readString();
        releaseDate = in.readString();
        voteAverage = in.readFloat();
        runtime = in.readInt();
        favorite = in.readByte();
    }

    private int id;
    private String title;
    private String synopsis;
    private String posterPath;
    private String releaseDate;
    private float voteAverage;
    private int runtime;
    private byte favorite;
}
