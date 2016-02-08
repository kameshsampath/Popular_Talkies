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
 * Parcelable that will hold the Trailers
 */
public class Trailer implements Parcelable {

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {

        @Override
        public Trailer createFromParcel(Parcel source) {

            return new Trailer(source);
        }

        @Override
        public Trailer[] newArray(int size) {

            return new Trailer[size];
        }
    };
    public Trailer(String name, String key) {
        this.name = name;
        this.key = key;
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(key);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    private Trailer(Parcel in) {

        name = in.readString();
        key = in.readString();
    }

    String name;
    String key;
}