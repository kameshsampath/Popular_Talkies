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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.parcel.MovieReview;
import org.workspace7.populartalkies.service.MovieApiHelper;
import org.workspace7.populartalkies.service.MovieApiResultRecevier;
import org.workspace7.populartalkies.service.MovieService;
import org.workspace7.populartalkies.service.Receiver;
import org.workspace7.populartalkies.util.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieReviewsFragment extends Fragment implements Receiver {

    public MovieReviewsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Setup recevier
        mMovieApiResultRecevier = new MovieApiResultRecevier(new Handler());
        mMovieApiResultRecevier.setRecevier(this);

        Bundle args = getArguments();

        if (args != null) {
            mMovie = args.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
            mTwoPane = args.getBoolean(MovieDetailsActivity.TWO_PANE);
        } else if (savedInstanceState != null) {
            mMovie = savedInstanceState.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
            mMovieReviews = savedInstanceState.getParcelableArrayList(MovieReviewsActivity.MOVIE_REVIEWS);
            mTwoPane = savedInstanceState.getBoolean(MovieDetailsActivity.TWO_PANE);
        }

        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        mReviewsRecycleView = (RecyclerView) rootView.findViewById(R.id.reviews_list_view);
        mReviewsRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewsRecycleView.setItemAnimator(new DefaultItemAnimator());

        if (mMovie != null) {
            setPoster(mMovie.getPosterPath());

            if (mMovieReviewsAdapter == null) {
                mMovieReviewsAdapter = new MovieReviewsAdapter(getActivity(),
                        R.layout.review_list_item, mMovie.getPosterPath());
                mReviewsRecycleView.setAdapter(mMovieReviewsAdapter);
            }

            if (mMovieReviews == null || mMovieReviews.isEmpty()) {
                loadReviews(mMovie.getId());
            } else {
                mMovieReviewsAdapter.setItems(mMovieReviews);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mMovie != null) {
            outState.putParcelable(MovieDetailsActivity.MOVIE_DETAILS, mMovie);
        }

        if (mMovieReviews != null && !mMovieReviews.isEmpty()) {
            outState.putParcelableArrayList(MovieReviewsActivity.MOVIE_REVIEWS, mMovieReviews);
        }
        outState.putBoolean(MovieDetailsActivity.TWO_PANE, mTwoPane);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void notifyRefresh(int resultCode, Bundle args) {

        if (args != null) {

            switch (resultCode) {
                case MovieService.SERVICE_COMPLETED: {

                    mMovieReviews = args
                            .getParcelableArrayList(MovieService.RESPONSE_MOVIE_REVIEWS);
                    mMovieReviewsAdapter.setItems(mMovieReviews);
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
                    if (mMovie != null) {
                        setPoster(mMovie.getPosterPath());
                        if (mMovieReviewsAdapter == null) {
                            mMovieReviewsAdapter = new MovieReviewsAdapter(getActivity(), R.layout
                                    .review_list_item, mMovie.getPosterPath());
                            mReviewsRecycleView.setAdapter(mMovieReviewsAdapter);
                        }
                        loadReviews(mMovie.getId());
                    }
                }

            }
        }

    }

    private void setPoster(String posterPath) {

        if (getView() != null) {
            mPoster = (ImageView) getView().findViewById(R.id.movie_poster_image);

            if (mPoster != null) {

                Uri imageUri = MovieApiHelper
                        .moviePosterPath(posterPath, getString(R.string.poster_w185));

                Utility.loadImageIntoView(getActivity(), R.drawable.movie_poster_placeholder_w185,
                        mPoster, imageUri);
            }
        }
    }

    private void loadReviews(int movieId) {

        //Start the  Movies service
        mMovieApiResultRecevier = new MovieApiResultRecevier(new Handler());
        mMovieApiResultRecevier.setRecevier(this);

        Intent movieApiServiceIntent = new Intent(Intent.ACTION_SYNC,
                null,
                getActivity(),
                MovieService.class);

        movieApiServiceIntent.putExtra(MovieService.REQUEST_PARAM_MOVIE_ID,
                String.valueOf(movieId));

        movieApiServiceIntent.putExtra(MovieService.RECEIVER
                , mMovieApiResultRecevier);

        movieApiServiceIntent.putExtra(MovieService.REQUEST_ID,
                MovieService.SERVICE_REQUEST_MOVIE_REVIEWS);

        getActivity().startService(movieApiServiceIntent);

    }

    private MovieReviewsAdapter mMovieReviewsAdapter;
    private Movie mMovie;
    private boolean mTwoPane;
    private RecyclerView mReviewsRecycleView;
    private ArrayList<MovieReview> mMovieReviews;
    private MovieApiResultRecevier mMovieApiResultRecevier;

    private ImageView mPoster;
}
