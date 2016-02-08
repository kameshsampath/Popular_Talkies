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

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Movie;

public class MovieReviewsActivity extends AppCompatActivity {

    public static final String MOVIE_REVIEWS = "MOVIE_REVIEWS";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {

        Intent parentActivityIntent = super.getParentActivityIntent();
        if (parentActivityIntent != null) {
            parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return parentActivityIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_reviews);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if (savedInstanceState == null) {
            mMovie = getIntent().getParcelableExtra(MovieDetailsActivity.MOVIE_DETAILS);
            mTwoPane = getIntent().getBooleanExtra(MovieDetailsActivity.TWO_PANE, false);
        } else {
            mMovie = savedInstanceState.getParcelable(MovieDetailsActivity.MOVIE_DETAILS);
            mTwoPane = savedInstanceState.getBoolean(MovieDetailsActivity.TWO_PANE);
        }

        if (mMovie != null) {
            MovieReviewsFragment movieReviewsFragment = (MovieReviewsFragment)
                    getSupportFragmentManager().findFragmentById(
                            R.id.fragment_movie_reviews);
            if (movieReviewsFragment != null) {
                Log.v(LOG_TAG, "Read reviews for movie :" + mMovie.getId());
                Bundle args = new Bundle();
                args.putParcelable(MovieDetailsActivity.MOVIE_DETAILS, mMovie);
                args.putBoolean(MovieDetailsActivity.TWO_PANE, mTwoPane);
                movieReviewsFragment.notifyRefresh(-1, args);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(MovieDetailsActivity.MOVIE_DETAILS, mMovie);
        outState.putBoolean(MovieDetailsActivity.TWO_PANE, mTwoPane);
        super.onSaveInstanceState(outState);
    }

    private static final String LOG_TAG = MovieReviewsActivity.class.getSimpleName();
    private Movie mMovie;
    private boolean mTwoPane;
}
