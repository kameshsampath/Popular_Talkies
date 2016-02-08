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

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.workspace7.populartalkies.data.MovieContract;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.parcel.MovieReview;
import org.workspace7.populartalkies.parcel.Trailer;

import java.util.ArrayList;


import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_AUTHOR;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_CONTENT;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_ID;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_KEY;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_NAME;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_OVERVIEW;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_POSTER_PATH;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_RELEASE_DATE;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_RUNTIME;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_TITLE;
import static org.workspace7.populartalkies.util.Constants.JSON_ATTR_VOTE_AVERAGE;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class MovieService extends IntentService {

    public static final String SERVICE_NAME = "org.workspace7.popularmovies.serviec.MovieService";
    public static final String RECEIVER = "receiver";
    public static final String REQUEST_ID = "REQUEST_ID";
    public static final String REQUEST_PARAM_MOVIE_ID = "SERVICE_REQUEST_PARAM_MOVIE_ID";
    public static final String REQUEST_PARAM_SORT_BY = "REQUEST_PARAM_MOVIE_ID";
    public static final String RESPONSE_MOVIE_REVIEWS = "RESPONSE_MOVIE_REVIEWS";
    public static final String RESPONSE_MOVIE_LIST = "RESPONSE_MOVIE_LIST";
    public static final String RESPONSE_MOVIE = "RESPONSE_MOVIE";
    public static final int SERVICE_REQUEST_MOVIE_TRAILERS = 1001;
    public static final int SERVICE_REQUEST_MOVIE_REVIEWS = 1002;
    public static final int SERVICE_REQUEST_MOVIE_LIST = 1003;
    public static final int SERVICE_REQUEST_MOVIE_EXTRA_DETAILS = 1004;
    public static final int SERVICE_REQUEST_FAVORITE_MOVIES = 1005;
    public static final int SERVICE_ERROR = 0;
    public static final int SERVICE_RUNNING = 1;
    public static final int SERVICE_COMPLETED = 2;
    public static String RESPONSE_MOVIE_TRAILERS = "RESPONSE_MOVIE_TRAILERS";

    public MovieService() {

        super(SERVICE_NAME);
    }

    public byte isFavoriteMovie(int movieId) {

        byte bFavorite = 0;
        Cursor cursor = getContentResolver()
                .query(MovieContract.FavoriteMovieEntry
                                .buildMovieUri(movieId),
                        null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {

                bFavorite = 1;
            }
            cursor.close();
        }

        return bFavorite;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mResultReceiver = intent.getParcelableExtra(RECEIVER);

        String movieId = intent.getStringExtra(REQUEST_PARAM_MOVIE_ID);

        String sortBy = intent.getStringExtra(REQUEST_PARAM_SORT_BY);

        final int serviceRequestId = intent.getIntExtra(REQUEST_ID, -1);

        Uri movieApiUri;

        Bundle serviceResponse = new Bundle();
        //Status to caller that service started
        //mResultReceiver.send(SERVICE_RUNNING, new Bundle());

        switch (serviceRequestId) {
            case SERVICE_REQUEST_MOVIE_TRAILERS: {

                if (movieId != null) {
                    movieApiUri = MovieApiHelper.movieTrailersPath(movieId);
                    final String strResponse = MovieApiHelper.callApi(movieApiUri);
                    if (strResponse != null) {
                        try {
                            ArrayList<Trailer> trailers = buildTrailersFromJSON(strResponse);
                            serviceResponse.putParcelableArrayList(RESPONSE_MOVIE_TRAILERS, trailers);
                            mResultReceiver.send(SERVICE_COMPLETED, serviceResponse);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error loading trailers for movie " + movieId, e);
                            mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                        }
                    } else {
                        mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                    }
                } else {
                    mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                }
                break;
            }
            case SERVICE_REQUEST_MOVIE_REVIEWS: {

                if (movieId != null) {
                    movieApiUri = MovieApiHelper.movieReviewsPath(movieId);
                    final String strResponse = MovieApiHelper.callApi(movieApiUri);
                    if (strResponse != null) {
                        try {
                            ArrayList<MovieReview> movieReviews = buildReviewsFromJSON(strResponse);
                            serviceResponse.putParcelableArrayList(RESPONSE_MOVIE_REVIEWS, movieReviews);
                            mResultReceiver.send(SERVICE_COMPLETED, serviceResponse);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error loading reviews for movie " + movieId, e);
                            mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                        }
                    } else {
                        mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                    }
                } else {
                    mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                }
                break;
            }
            case SERVICE_REQUEST_MOVIE_LIST: {

                movieApiUri = MovieApiHelper.movieDiscoveryPath(sortBy);
                final String strResponse = MovieApiHelper.callApi(movieApiUri);
                if (strResponse != null) {
                    try {
                        buildMovieListFromJSON(strResponse);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Error loading movies with sort desc " + sortBy, e);
                        mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                    }
                } else {
                    mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                }
                break;
            }
            case SERVICE_REQUEST_MOVIE_EXTRA_DETAILS: {
                if (movieId != null) {
                    movieApiUri = MovieApiHelper.movieReviewsPath(movieId);
                    final String strResponse = MovieApiHelper.callApi(movieApiUri);
                    if (strResponse != null) {
                        try {
                            Movie movie = buildMovieExtraDetailsFromJSON(strResponse);
                            serviceResponse.putParcelable(RESPONSE_MOVIE, movie);
                            mResultReceiver.send(SERVICE_COMPLETED, serviceResponse);
                        } catch (JSONException e) {
                            Log.e(LOG_TAG, "Error loading movies extra details for movie id " + movieId, e);
                            mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                        }
                    } else {
                        mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                    }
                } else {
                    mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                }
                break;
            }
            case SERVICE_REQUEST_FAVORITE_MOVIES: {
                try {
                    favoriteMovies();
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error loading favorite movies " + movieId, e);
                    mResultReceiver.send(SERVICE_ERROR, serviceResponse);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Service Request Id "
                        + serviceRequestId + " not supported or invalid");
            }
        }

    }

    private ArrayList<Trailer> buildTrailersFromJSON(String strResponse)
            throws JSONException {

        ArrayList<Trailer> trailers = new ArrayList<>();

        if (strResponse != null) {

            JSONObject jTrailersResponse = new JSONObject(strResponse);

            JSONArray jTrailers = jTrailersResponse
                    .getJSONArray(MovieApiHelper.JSON_ATTR_RESULTS);

            for (int j = 0; j < jTrailers.length(); j++) {

                JSONObject jTrailer = jTrailers.getJSONObject(j);

                String name = jTrailer.getString(JSON_ATTR_NAME);

                String key = jTrailer.getString(JSON_ATTR_KEY);

                Trailer trailer = new Trailer(name, key);

                trailers.add(trailer);
            }
        }
        return trailers;
    }

    private ArrayList<MovieReview> buildReviewsFromJSON(String strResponse)
            throws JSONException {

        ArrayList<MovieReview> movieReviews = new ArrayList<>();

        JSONObject jReviewsResponse = new JSONObject(strResponse);

        JSONArray jReviews = jReviewsResponse.getJSONArray(MovieApiHelper.JSON_ATTR_RESULTS);

        for (int j = 0; j < jReviews.length(); j++) {

            JSONObject jReview = jReviews.getJSONObject(j);

            String author = jReview.getString(JSON_ATTR_AUTHOR);

            String content = jReview.getString(JSON_ATTR_CONTENT);

            MovieReview movieReview = new MovieReview(author, content);

            movieReviews.add(movieReview);
        }
        return movieReviews;
    }

    private void buildMovieListFromJSON(String strResponse)
            throws JSONException {

        Bundle serviceResponse;

        ArrayList<Movie> movies = new ArrayList<>();

        if (strResponse != null) {
            try {
                JSONObject jsonResponse = new JSONObject(strResponse);

                JSONArray jMovies = jsonResponse.getJSONArray(MovieApiHelper.JSON_ATTR_RESULTS);

                for (int i = 0; i < jMovies.length(); i++) {

                    JSONObject jMovie = jMovies.getJSONObject(i);
                    Movie movie = new Movie();
                    int movieId = jMovie.getInt(JSON_ATTR_ID);
                    movie.setId(movieId);
                    String posterPath = jMovie.getString(JSON_ATTR_POSTER_PATH);
                    movie.setPosterPath(posterPath);
                    fetchMovieExtraDetails(movie, movieId);
                    movie.setFavorite(isFavoriteMovie(movieId));
                    serviceResponse = new Bundle();
                    serviceResponse.putParcelable(RESPONSE_MOVIE, movie);
                    mResultReceiver.send(SERVICE_RUNNING, serviceResponse);
                    movies.add(movie);
                }
            } catch (JSONException e) {
                Log.v(LOG_TAG, "Error while getting movies", e);
            }
        }
        serviceResponse = new Bundle();
        serviceResponse.putParcelableArrayList(RESPONSE_MOVIE_LIST, movies);
        mResultReceiver.send(SERVICE_COMPLETED, serviceResponse);
    }

    private Movie buildMovieExtraDetailsFromJSON(String strResponse)
            throws JSONException {

        Movie movie = new Movie();

        if (strResponse != null) {

            JSONObject jMovie = new JSONObject(strResponse);

            int movieId = jMovie.getInt(JSON_ATTR_ID);
            movie.setId(movieId);

            String posterPath = jMovie.getString(JSON_ATTR_POSTER_PATH);

            movie.setPosterPath(posterPath);

            movie.setTitle(jMovie.getString(JSON_ATTR_TITLE));

            movie.setSynopsis(jMovie.getString(JSON_ATTR_OVERVIEW));

            movie.setReleaseDate(jMovie.getString(JSON_ATTR_RELEASE_DATE));

            String strVoteAverage = jMovie.getString(JSON_ATTR_VOTE_AVERAGE);

            movie.setVoteAverage(Float.parseFloat(strVoteAverage));

            String strRuntime = jMovie.getString(JSON_ATTR_RUNTIME);

            try {
                movie.setRuntime(Integer.parseInt(strRuntime));
            } catch (NumberFormatException e) {
                Log.w(LOG_TAG, "Movie " + movieId + " does not have valid runtime");
                movie.setRuntime(90);
            }

        }
        return movie;
    }

    private ArrayList<Movie> favoriteMovies() throws JSONException {

        Bundle serviceResponse;

        ArrayList<Movie> movies = new ArrayList<>();

        Cursor cfavoriteMovies = getContentResolver().
                query(MovieContract.FavoriteMovieEntry.CONTENT_URI, null, null, null, null);

        if (cfavoriteMovies != null) {
            while (cfavoriteMovies.moveToNext()) {
                Movie movie = new Movie();
                int movieId = cfavoriteMovies.getInt(0);
                movie.setId(movieId);
                movie.setPosterPath(cfavoriteMovies.getString(1));
                movie.setFavorite((byte) 1);
                fetchMovieExtraDetails(movie, movieId);
                serviceResponse = new Bundle();
                serviceResponse.putParcelable(RESPONSE_MOVIE, movie);
                mResultReceiver.send(SERVICE_RUNNING, serviceResponse);
                movies.add(movie);
            }

            cfavoriteMovies.close();
        }

        serviceResponse = new Bundle();
        serviceResponse.putParcelableArrayList(RESPONSE_MOVIE_LIST, movies);
        mResultReceiver.send(SERVICE_COMPLETED, serviceResponse);
        return movies;
    }

    private void fetchMovieExtraDetails(Movie movie, int movieId) throws JSONException {

        Uri movieExtraDetailsUri = MovieApiHelper
                .movieExtraDetailsPath(String.valueOf(movieId));

        final String strMovieExtraDetailsResponse = MovieApiHelper.callApi(movieExtraDetailsUri);

        if (strMovieExtraDetailsResponse != null) {

            JSONObject jMovie = new JSONObject(strMovieExtraDetailsResponse);

            movie.setTitle(jMovie.getString(JSON_ATTR_TITLE));

            movie.setSynopsis(jMovie.getString(JSON_ATTR_OVERVIEW));

            movie.setReleaseDate(jMovie.getString(JSON_ATTR_RELEASE_DATE));

            String strVoteAverage = jMovie.getString(JSON_ATTR_VOTE_AVERAGE);
            movie.setVoteAverage(Float.parseFloat(strVoteAverage));

            String strRuntime = jMovie.getString(JSON_ATTR_RUNTIME);

            try {
                movie.setRuntime(Integer.parseInt(strRuntime));
            } catch (NumberFormatException e) {
                Log.w(LOG_TAG, "Movie " + movieId + " does not have valid runtime");
                movie.setRuntime(90);
            }

        }
    }

    private static final String LOG_TAG = MovieService.class.getSimpleName();
    private ResultReceiver mResultReceiver;
}
