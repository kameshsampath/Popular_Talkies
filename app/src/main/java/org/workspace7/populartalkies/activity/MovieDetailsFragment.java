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

package org.workspace7.populartalkies.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.data.MovieContract;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.parcel.Trailer;
import org.workspace7.populartalkies.service.MovieApiHelper;
import org.workspace7.populartalkies.service.MovieApiResultRecevier;
import org.workspace7.populartalkies.service.MovieService;
import org.workspace7.populartalkies.service.Receiver;
import org.workspace7.populartalkies.util.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements Receiver {

    public MovieDetailsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieApiResultRecevier = new MovieApiResultRecevier(new Handler());
        mMovieApiResultRecevier.setRecevier(this);
        mMovieTrailersAdapter = new MovieTrailersAdapter(getActivity(), R.layout.trailer_list_item);

        Bundle args = getArguments();

        if (args != null) {
            mMovie = args.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
            mTwoPane = args.getBoolean(MovieDetailsActivity.TWO_PANE);
        } else if (savedInstanceState != null
                && savedInstanceState.containsKey(MovieDetailsActivity.MOVIE_DETAILS)) {
            mMovie = savedInstanceState.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
            mTrailers = savedInstanceState.getParcelableArrayList(MovieDetailsActivity.MOVIE_TRAILERS);
            mTwoPane = savedInstanceState.getBoolean(MovieDetailsActivity.TWO_PANE);
            mMovieTrailersAdapter.setItems(mTrailers);
        }

        View detailsView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ViewHolder viewHolder = new ViewHolder(detailsView);
        detailsView.setTag(viewHolder);

        mTrailersRecycleView = (RecyclerView) detailsView.findViewById(R.id.trailers_list_view);
        mTrailersRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mTrailersRecycleView.setItemAnimator(new DefaultItemAnimator());
        mTrailersRecycleView.setAdapter(mMovieTrailersAdapter);

        if (mTwoPane) {
            final View redReviewsView = detailsView.findViewById(R.id.details_movie_review);
            if (redReviewsView != null) {
                redReviewsView.setVisibility(View.GONE);
            }
        }

        if (mMovie != null) {
            fillDetails(viewHolder);
            int movieId = mMovie.getId();
            if (mTrailers == null || mTrailers.isEmpty()) {
                loadTrailers(movieId);
            }
        }

        return detailsView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mMovie != null) {
            outState.putParcelable(MovieDetailsActivity.MOVIE_DETAILS, mMovie);
            outState.putParcelableArrayList(MovieDetailsActivity.MOVIE_TRAILERS, mTrailers);
        }
        outState.putBoolean(MovieDetailsActivity.TWO_PANE, mTwoPane);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void notifyRefresh(int resultCode, Bundle args) {

        if (args != null) {
            switch (resultCode) {
                case MovieService.SERVICE_COMPLETED: {

                    mTrailers = args
                            .getParcelableArrayList(MovieService.RESPONSE_MOVIE_TRAILERS);

                    if (mMovieTrailersAdapter == null) {
                        mMovieTrailersAdapter = new MovieTrailersAdapter(getActivity(), R.layout
                                .trailer_list_item);
                    }
                    mMovieTrailersAdapter.clear();
                    mMovieTrailersAdapter.setItems(mTrailers);

                    break;
                }
                case MovieService.SERVICE_RUNNING: {
                    break;
                }
                case MovieService.SERVICE_ERROR: {
                    break;
                }
                default: {
                    mMovie = args.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
                    mTwoPane = args.getBoolean(MovieDetailsActivity.TWO_PANE);
                    ViewHolder viewHolder = (ViewHolder) getView().getTag();
                    fillDetails(viewHolder);
                    loadTrailers(mMovie.getId());
                }
            }
        }

    }

    public interface MovieFavoritedCallback {

        void movieDetailsChanged(Bundle args);
    }

    private void loadTrailers(int movieId) {

        //Start the  Trailers service

        Intent movieApiServiceIntent = new Intent(Intent.ACTION_SYNC,
                null,
                getActivity(),
                MovieService.class);

        movieApiServiceIntent.putExtra(MovieService.REQUEST_PARAM_MOVIE_ID,
                String.valueOf(movieId));

        movieApiServiceIntent.putExtra(MovieService.RECEIVER
                , mMovieApiResultRecevier);

        movieApiServiceIntent.putExtra(MovieService.REQUEST_ID,
                MovieService.SERVICE_REQUEST_MOVIE_TRAILERS);

        getActivity().startService(movieApiServiceIntent);

    }

    private void fillDetails(final ViewHolder viewHolder) {

        if (!mTwoPane) {
            CharSequence movieReviewsText = viewHolder.movieReviewView.getText();
            SpannableString spanText = new SpannableString(movieReviewsText);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {

                    ds.setUnderlineText(false);
                }

                @Override
                public void onClick(View widget) {

                    Intent movieReviewsIntent = new Intent(getActivity(), MovieReviewsActivity.class);
                    movieReviewsIntent.putExtra(MovieDetailsActivity.MOVIE_DETAILS, mMovie);
                    startActivity(movieReviewsIntent);

                }
            };

            spanText.setSpan(clickableSpan, 0, spanText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            viewHolder.movieReviewView.setText(spanText);
            viewHolder.movieReviewView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        //Populate the View
        final Uri imageUri = MovieApiHelper
                .moviePosterPath(mMovie.getPosterPath(), getString(R.string.poster_w154));

        try {
            Utility.loadImageIntoView(getActivity(), R.drawable.movie_poster_placeholder_w154,
                    viewHolder.moviePosterView,
                    imageUri);
        } catch (IllegalStateException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        String releaseYear = Utility.yearFromDate(mMovie.getReleaseDate());
        viewHolder.releaseYearView.setText(releaseYear);

        viewHolder.movieSynopsisView.setText(mMovie.getSynopsis());

        String moveTime = getString(R.string.movie_time_format, mMovie.getRuntime());
        viewHolder.movieTimeView.setText(moveTime);

        float voteAverage = mMovie.getVoteAverage();

        viewHolder.movieRatingView.setRating(voteAverage / 2.0f);

        byte bFavoriteFlag = mMovie.getFavorite();

        viewHolder.favoriteButton.setChecked(bFavoriteFlag == 1);

        viewHolder.favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final ContentValues favoriteMovie = new ContentValues();

                final int movieId = mMovie.getId();

                favoriteMovie.put(MovieContract.FavoriteMovieEntry._ID, movieId);

                favoriteMovie.put(MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());

                final Uri favoriteMovieUri = MovieContract.FavoriteMovieEntry
                        .buildMovieUri(movieId);

                final ContentResolver contentResolver = getActivity().getContentResolver();

                final Uri movieFavoriteContentUri = MovieContract.FavoriteMovieEntry.CONTENT_URI;

                final String[] whereArgs = new String[]{String.valueOf(movieId)};

                if (isChecked) {

                    Cursor cursor =
                            contentResolver
                                    .query(favoriteMovieUri, null, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {

                        Log.v(LOG_TAG, "Movie " + mMovie.getTitle() + " already in favorites");

                        contentResolver.update(movieFavoriteContentUri,
                                favoriteMovie, SELECTION_WHERE, whereArgs);
                    } else {

                        Log.v(LOG_TAG, "Adding " + mMovie.getTitle() + " to favorites");

                        contentResolver.insert(movieFavoriteContentUri, favoriteMovie);
                    }

                    if (cursor != null) {
                        cursor.close();
                    }

                } else {
                    contentResolver.delete(movieFavoriteContentUri, SELECTION_WHERE, whereArgs);
                    mMovie.setFavorite((byte) 0);
                }

                if (getActivity() instanceof MovieFavoritedCallback) {
                    Bundle args = new Bundle();
                    args.putParcelable(MovieDetailsActivity.MOVIE_DETAILS, mMovie);
                    ((MovieFavoritedCallback) getActivity()).movieDetailsChanged(args);
                } else if (mTwoPane) {
                    MovieListFragment movieListFragment =
                            (MovieListFragment) getActivity()
                                    .getSupportFragmentManager().findFragmentById(R.id.movies_list_fragment);
                    if (movieListFragment != null) {
                        movieListFragment.refreshFavoriteFlag(mMovie);
                    }

                }

            }
        });
    }

    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    private static final String SELECTION_WHERE = MovieContract.FavoriteMovieEntry._ID + " = ? ";
    private boolean mTwoPane;
    private MovieTrailersAdapter mMovieTrailersAdapter;
    private MovieApiResultRecevier mMovieApiResultRecevier;
    private RecyclerView mTrailersRecycleView;
    private ArrayList<Trailer> mTrailers;

    private static class ViewHolder {

        public ViewHolder(View view) {

            moviePosterView = (ImageView) view.findViewById(R.id.movie_poster_image);
            releaseYearView = (TextView) view.findViewById(R.id.details_movie_release_year);
            movieSynopsisView = (TextView) view.findViewById(R.id.details_movie_synopsis);
            movieTimeView = (TextView) view.findViewById(R.id.details_movie_time);
            movieRatingView = (RatingBar) view.findViewById(R.id.details_movie_rating);
            movieReviewView = (TextView) view.findViewById(R.id.details_movie_review);
            favoriteButton = (ToggleButton) view.findViewById(R.id.favorite_movie);
        }

        ImageView moviePosterView;
        TextView releaseYearView;
        TextView movieSynopsisView;
        TextView movieTimeView;
        TextView movieReviewView;
        RatingBar movieRatingView;
        ToggleButton favoriteButton;
    }

    Movie mMovie;

}
