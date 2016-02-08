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

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.data.MovieContract;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.service.MovieApiResultRecevier;
import org.workspace7.populartalkies.service.MovieService;
import org.workspace7.populartalkies.service.Receiver;
import org.workspace7.populartalkies.util.Utility;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment implements Receiver {

    public MovieListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.movie_list_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMovieApiResultRecevier = new MovieApiResultRecevier(new Handler());
        mMovieApiResultRecevier.setRecevier(this);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mSwipeToRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.layout_movie_list);
        mSwipeToRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.orange,
                R.color.green);
        mSwipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                int categoryIndex = Utility.getMovieCategoryIndex(getActivity(), mMovieListBy);
                ((MovieListCallback) getActivity()).handleCategorySelection(categoryIndex);
            }
        });

        Bundle args = getArguments();

        if (args != null) {
            mTwoPane = args.getBoolean(MainActivity.TWO_PANE);
        }

        final int posterListItemRes = R.layout.movie_list_item;

        if (savedInstanceState != null &&
                savedInstanceState.containsKey(MOVIE_LIST)) {

            mMovies = savedInstanceState.getParcelableArrayList(MOVIE_LIST);

            if (mMovieListAdapter == null) {
                mMovieListAdapter = new MovieListAdapter(getActivity(), posterListItemRes, mMovies,
                        mTwoPane);
            } else {
                mMovieListAdapter.clear();
                mMovieListAdapter.setItems(mMovies);
            }
        } else {
            mMovies = new ArrayList<>();
            mMovieListAdapter = new MovieListAdapter(getActivity(), posterListItemRes, mTwoPane);
        }

        //RecycleViewGridLayoutManager

        int spanCount = getResources().getInteger(R.integer.card_span);
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);

        //gridLayoutManager.
        mMoviePosterRecycleView = (RecyclerView) rootView.findViewById(R.id.movie_posters_view);
        mMoviePosterRecycleView.setLayoutManager(gridLayoutManager);
        mMoviePosterRecycleView.setItemAnimator(new DefaultItemAnimator());
        mMoviePosterRecycleView.setAdapter(mMovieListAdapter);

        if (savedInstanceState != null &&
                savedInstanceState.containsKey(SELECTED_ITEM_POSITION)) {
            mSelectedPosition = savedInstanceState.getInt(SELECTED_ITEM_POSITION);
            MovieListAdapter.mSelectedItemPosition = mSelectedPosition;
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_refresh: {
                int categoryIndex = Utility.getMovieCategoryIndex(getActivity(), mMovieListBy);
                ((MovieListCallback) getActivity()).handleCategorySelection(categoryIndex);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mMovies != null) {
            outState.putParcelableArrayList(MOVIE_LIST, mMovies);
            outState.putInt(SELECTED_ITEM_POSITION, MovieListAdapter.mSelectedItemPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void notifyRefresh(int resultCode, Bundle args) {

        if (args != null) {
            switch (resultCode) {
                case MovieService.SERVICE_COMPLETED: {
                    mSwipeToRefreshLayout.setRefreshing(false);
                    mMovies = mMovieListAdapter.getItems();
                    break;
                }
                case MovieService.SERVICE_RUNNING: {
                    if (!mSwipeToRefreshLayout.isRefreshing()) {
                        mSwipeToRefreshLayout.setRefreshing(true);
                    }
                    Movie movie = args.getParcelable(MovieService.RESPONSE_MOVIE);
                    mMovieListAdapter.addMovie(movie);
                    break;
                }
                case MovieService.SERVICE_ERROR: {
                    //TODO any error feedback
                    mSwipeToRefreshLayout.setRefreshing(false);
                    break;
                }
                default: {
                    if (mMovieListAdapter != null) {
                        mMovieListAdapter.clear();
                    }
                    mTwoPane = args.getBoolean(MainActivity.TWO_PANE, false);
                    mMovieListBy = args.getString(MainActivity.MOVIE_LIST_BY);
                    if (MainActivity.MOVIE_LIST_BY_FAV.equalsIgnoreCase(mMovieListBy)) {
                        loadMovies(MovieService.SERVICE_REQUEST_FAVORITE_MOVIES, mMovieListBy);
                    } else {
                        loadMovies(MovieService.SERVICE_REQUEST_MOVIE_LIST, mMovieListBy);
                    }
                }
            }
        }
    }

    protected void createListRefreshArguments() {

        Bundle args = new Bundle();
        args.putBoolean(MovieDetailsActivity.TWO_PANE, mTwoPane);
        args.putString(MainActivity.MOVIE_LIST_BY, mMovieListBy);
    }

    protected void refreshFavoriteFlag(Movie changedMovie) {

        int movieIdx = mMovies.indexOf(changedMovie);

        if (movieIdx != -1) {
            changedMovie = mMovies.get(movieIdx);
            byte isStillFavorite = isFavoriteMovie(changedMovie.getId());

            if (isStillFavorite == 1) {
                changedMovie.setFavorite(isStillFavorite);
                mMovies.set(movieIdx, changedMovie);
            } else if (MainActivity.MOVIE_LIST_BY_FAV.equals(mMovieListBy)) {
                mMovies.remove(movieIdx);
                mMovieListAdapter.remove(changedMovie);
                if (mTwoPane) {
                    ((MovieListCallback) getActivity()).onUnFavorite(new Bundle());
                }
            }
        }
    }

    private void loadMovies(int requestId, String sortBy) {

        //Start the  Movies Service service
        Intent movieApiServiceIntent = new Intent(Intent.ACTION_SYNC,
                null,
                getActivity(),
                MovieService.class);

        movieApiServiceIntent.putExtra(MovieService.REQUEST_PARAM_SORT_BY,
                sortBy);

        movieApiServiceIntent.putExtra(MovieService.RECEIVER
                , mMovieApiResultRecevier);

        movieApiServiceIntent.putExtra(MovieService.REQUEST_ID, requestId);

        getActivity().startService(movieApiServiceIntent);
    }

    //FIXME use the method from MovieService
    private byte isFavoriteMovie(int movieId) {

        byte bFavorite = 0;
        Cursor cursor =
                getActivity().getContentResolver()
                        .query(MovieContract.FavoriteMovieEntry
                                        .buildMovieUri(movieId),
                                null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                bFavorite = 1;
            }
        }
        cursor.close();

        return bFavorite;
    }

    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private static final String MOVIE_LIST = "MOVIES_LIST";
    private static final String SELECTED_ITEM_POSITION = "SELECTED_ITEM_POSITION";
    private MovieListAdapter mMovieListAdapter;
    private ArrayList<Movie> mMovies;
    private RecyclerView mMoviePosterRecycleView;
    private boolean mTwoPane;
    private MovieApiResultRecevier mMovieApiResultRecevier;
    private ProgressDialog mProgressDialog;
    private String mMovieListBy;

    private int mSelectedPosition;
    private SwipeRefreshLayout mSwipeToRefreshLayout;
}
