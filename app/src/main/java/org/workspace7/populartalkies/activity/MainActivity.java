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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.stetho.Stetho;

import org.workspace7.populartalkies.BuildConfig;
import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.util.PreferenceHelper;
import org.workspace7.populartalkies.util.Utility;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class MainActivity extends AppCompatActivity implements MovieListCallback {

    public static final int REQ_CODE_MOVIE_DETAILS = 3000;
    public static final String MOVIE_LIST_EVERYTHING = "everything";
    public static final String MOVIE_LIST_BY_FAV = "favorites";
    public static final String TWO_PANE = "TWO_PANE";
    public static final String MOVIE_LIST_BY = "MOVIE_LIST_BY";
    public static final String LAST_SEEN_MOVIE_CATEGORY = "LAST_SEEN_MOVIE_CATEGORY";

    public boolean onOptionsItemSelected(MenuItem item) {

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onPosterClicked(Movie movie) {

        if (mTwoPane) {
            mViewableMovie = movie;

            Bundle args = new Bundle();

            args.putParcelable(MovieDetailsActivity.MOVIE_DETAILS, movie);

            args.putBoolean(MovieDetailsActivity.TWO_PANE, true);

            //Details for MovieDetails Fragment
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_details_container, movieDetailsFragment, TAG_MOVIE_DETAILS)
                    .commit();

            //Details for MovieReviewDetails
            MovieReviewsFragment movieReviewsFragment = new MovieReviewsFragment();
            movieReviewsFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_reviews_container, movieReviewsFragment, TAG_MOVIE_REVIEWS)
                    .commit();

            mMovieDetailsSlider.openPane();

        } else {
            Intent detailsActivity = new Intent(this, MovieDetailsActivity.class);
            detailsActivity.putExtra(MovieDetailsActivity.MOVIE_DETAILS, movie);
            startActivityForResult(detailsActivity, MainActivity.REQ_CODE_MOVIE_DETAILS);
        }
    }

    @Override
    public void onUnFavorite(Bundle args) {

        if (mTwoPane && MOVIE_LIST_BY_FAV.equalsIgnoreCase(mMovieListBy)) {

            //Remove MovieDetails Fragment
            MovieDetailsFragment movieDetailsFragment =
                    (MovieDetailsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_MOVIE_DETAILS);

            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(movieDetailsFragment)
                    .commit();

            //Remove MovieReviewDetails
            MovieReviewsFragment movieReviewsFragment =
                    (MovieReviewsFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_MOVIE_REVIEWS);
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(movieReviewsFragment)
                    .commit();

        }
    }

    public void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }

        if (mSharedPrefrences.contains(LAST_SEEN_MOVIE_CATEGORY)
                && savedInstanceState == null) {
            int categoryIndex = mSharedPrefrences.getInt(LAST_SEEN_MOVIE_CATEGORY, 0);
            handleCategorySelection(categoryIndex);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(mTitle);
    }

    public void handleCategorySelection(int position) {

        if (mWelcomeUi != null && mWelcomeUi.isShown()) {
            mWelcomeUi.setVisibility(View.GONE);
        }
        mMovieListBy =
                getResources().getStringArray(R.array.movie_category_values)[position];

        String movieListBySelected;
        String title;

        if (MOVIE_LIST_EVERYTHING.equals(mMovieListBy)) {
            movieListBySelected = null;
            title = getString(R.string.movie_release_year_format,
                    Utility.yearFromDate(System.currentTimeMillis()));
        } else {
            movieListBySelected = mMovieListBy;
            title = mMovieCategories[position];
        }

        MovieListFragment movieListFragment = (MovieListFragment)
                getSupportFragmentManager().findFragmentById(R.id.movies_list_fragment);

        Bundle args = new Bundle();

        args.putString(MOVIE_LIST_BY, movieListBySelected);
        args.putBoolean(TWO_PANE, mTwoPane);

        movieListFragment.notifyRefresh(-1, args);

        //Feedback
        mMovieCategoriesList.setItemChecked(position, true);
        setTitle(title);
        mDrawerLayout.closeDrawer(mMovieCategoriesList);
        //Save it in preferences
        SharedPreferences.Editor editor = mSharedPrefrences.edit();
        editor.putInt(LAST_SEEN_MOVIE_CATEGORY, position);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mTitle = getString(R.string.app_name);
        if (BuildConfig.USE_STETHO) {
            Log.i(LOG_TAG, "Stetho debugging enabled");
            Stetho.initializeWithDefaults(this);
        }
        mMovieCategories = getResources().getStringArray(R.array.movie_category_labels);
        mSharedPrefrences = PreferenceHelper.getInstance(this).getSharedPreferences();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.slider_movie_details) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                Bundle args = new Bundle();
                args.putBoolean(TWO_PANE, mTwoPane);

                MovieListFragment movieListFragment = new MovieListFragment();

                movieListFragment.setArguments(args);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movies_list_fragment, movieListFragment)
                        .commit();
            }
            mMovieDetailsSlider = (SlidingPaneLayout) findViewById(R.id.slider_movie_details);
            mMovieDetailsSlider.openPane();
        } else {
            mTwoPane = false;
        }

        createDrawer();

        if (!mSharedPrefrences.contains(LAST_SEEN_MOVIE_CATEGORY)) {
            mWelcomeUi = findViewById(R.id.welcome_ui);
            if (mWelcomeUi != null) {
                mWelcomeUi.setVisibility(View.VISIBLE);
                Button buttonGetStarted = (Button)
                        mWelcomeUi.findViewById(R.id.get_started);
                buttonGetStarted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mDrawerLayout.openDrawer(mMovieCategoriesList);
                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (REQ_CODE_MOVIE_DETAILS == requestCode && data != null) {

            Movie changedMovie = data.getParcelableExtra(MovieDetailsActivity.MOVIE_DETAILS);

            if (changedMovie != null) {

                MovieListFragment movieListFragment = (MovieListFragment)
                        getSupportFragmentManager().findFragmentById(R.id.movies_list_fragment);

                movieListFragment.refreshFavoriteFlag(changedMovie);
            }

        }
    }

    private void createDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.movies_category_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {

                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mMovieCategoriesList = (ListView) mDrawerLayout.findViewById(R.id.movie_categories);
        mMovieCategoriesList.setAdapter(new MovieCategoriesAdapter(this, mMovieCategories));
        mMovieCategoriesList.setOnItemClickListener(new MovieCategoriesListener());

    }

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String TAG_MOVIE_DETAILS = "org.workspace7.populartalkies.fragment.movie.details";
    private static final String TAG_MOVIE_REVIEWS = "org.workspace7.populartalkies.fragment.movie.reviews";
    private String mMovieListBy;
    private SlidingPaneLayout mMovieDetailsSlider;
    private CharSequence mTitle;
    private boolean mTwoPane;
    private Movie mViewableMovie;
    //Navigation drawer variables
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mMovieCategoriesList;
    private String[] mMovieCategories;
    private SharedPreferences mSharedPrefrences;
    private View mWelcomeUi;

    private final class MovieCategoriesListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            handleCategorySelection(position);
        }
    }
}
