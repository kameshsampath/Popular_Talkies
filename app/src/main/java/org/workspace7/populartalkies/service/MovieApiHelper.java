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

package org.workspace7.populartalkies.service;

import android.net.Uri;
import android.util.Log;

import org.workspace7.populartalkies.BuildConfig;
import org.workspace7.populartalkies.util.Constants;
import org.workspace7.populartalkies.util.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MovieApiHelper implements Constants {

    public static final String GET_REQUEST_METHOD = "GET";

    /**
     * @return
     */
    public static String callApi(Uri uri) {

        HttpURLConnection urlConnection = null;

        BufferedReader reader = null;

        String jsonResponse = null;

        try {

            URL url = new URL(uri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(GET_REQUEST_METHOD);
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonResponse = buffer.toString();

            //Log.v(LOG_TAG, "API JSON Response: " + jsonResponse);

        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonResponse;
    }

    public static Uri movieDiscoveryPath(String sortDesc) {

        Uri.Builder uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(PATH_DISCOVERY)
                .appendPath(PATH_MOVIE)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THEMOVIEDB_API_KEY);

        if (sortDesc != null) {
            uriBuilder.appendQueryParameter(PARAM_SORT, sortDesc);
        } else {
            String releaseYear = Utility.yearFromDate(System.currentTimeMillis());
            uriBuilder.appendQueryParameter(PARAM_PRIMARY_RELEASE_DATE, releaseYear);
        }

        return uriBuilder.build();
    }

    public static Uri movieExtraDetailsPath(String movieId) {

        return Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(movieId)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THEMOVIEDB_API_KEY)
                .build();

    }

    public static Uri moviePosterPath(String imagePath, String imageSize) {

        return Uri.parse(MOVIE_POSTER_PATH_URL).buildUpon()
                .appendPath(imageSize)
                .appendEncodedPath(imagePath)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THEMOVIEDB_API_KEY)
                .build();
    }

    protected static Uri movieTrailersPath(String movieId) {

        return Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(URL_PATH_MOVIE)
                .appendPath(movieId)
                .appendPath(URL_PATH_TRAILERS)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THEMOVIEDB_API_KEY)
                .build();
    }

    protected static Uri movieReviewsPath(String movieId) {

        return Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(URL_PATH_MOVIE)
                .appendPath(movieId)
                .appendPath(URL_PATH_REVIEWS)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.THEMOVIEDB_API_KEY)
                .build();
    }

    private MovieApiHelper() {

    }

    private static final String LOG_TAG = MovieApiHelper.class.getSimpleName();

}
