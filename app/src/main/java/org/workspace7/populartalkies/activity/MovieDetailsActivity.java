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

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.service.Receiver;

public class MovieDetailsActivity extends AppCompatActivity implements MovieDetailsFragment.MovieFavoritedCallback {

    public static final String MOVIE_DETAILS = "MOVIE_DETAILS";
    public static final String TWO_PANE = "TWO_PANE";
    public static final String MOVIE_TRAILERS = "MOVIE_TRAILERS";

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
    public void movieDetailsChanged(Bundle args) {

        if (args != null) {
            Movie favoritedMovie = args.getParcelable(MOVIE_DETAILS);
            if (favoritedMovie != null) {
                Intent refreshFavFlagIntent = new Intent();
                refreshFavFlagIntent.putExtra(MOVIE_DETAILS, favoritedMovie);
                setResult(MainActivity.REQ_CODE_MOVIE_DETAILS, refreshFavFlagIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_details);

        if (mMovie == null) {
            mMovie = getIntent().getParcelableExtra(MOVIE_DETAILS);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Display Home button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Bundle args = new Bundle();

        if (savedInstanceState == null) {
            args.putParcelable(MOVIE_DETAILS, mMovie);
            Receiver receiver = (Receiver) getSupportFragmentManager().findFragmentById(R.id.movie_details_fragment);
            receiver.notifyRefresh(-1, args);
        } else if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DETAILS)) {
            mMovie = savedInstanceState.getParcelable(MOVIE_DETAILS);
            args.putParcelable(MOVIE_DETAILS, mMovie);
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(MOVIE_DETAILS, mMovie);
        super.onSaveInstanceState(outState);
    }
    private static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private Movie mMovie;
}
