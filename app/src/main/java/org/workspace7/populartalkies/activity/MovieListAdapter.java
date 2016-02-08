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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Movie;
import org.workspace7.populartalkies.service.MovieApiHelper;
import org.workspace7.populartalkies.util.Utility;

import java.util.ArrayList;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder> {

    public MovieListAdapter(Context context, int rowLayoutRes, boolean twoPane) {

        this(context, rowLayoutRes, new ArrayList<Movie>(), twoPane);
    }

    public MovieListAdapter(Context context, int rowLayoutRes, ArrayList<Movie> items, boolean twoPane) {

        mItems = items;
        mContext = context;
        mRowLayoutRes = rowLayoutRes;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayoutRes,
                viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        final Movie movie = getItem(position);
        //Attach the movie ViewHolder
        viewHolder.mMovie = movie;

        String imagePath = movie.getPosterPath();

        Uri imageUri = MovieApiHelper.moviePosterPath(imagePath,
                mContext.getString(R.string.poster_w185));

        DownloadMoviePosterTask downloadMoviePosterTask =
                new DownloadMoviePosterTask(mContext);

        final Drawable drawable = Utility.drawableFromBitmap(mContext, R.drawable
                .movie_poster_placeholder_w185);

        downloadMoviePosterTask.execute(imageUri.toString(),
                String.valueOf(Utility.getMinWidth(drawable)),
                String.valueOf(Utility.getMinHeight(drawable)));

        Utility.loadImageIntoView(mContext, R.drawable.movie_poster_placeholder_w185,
                viewHolder.moviePosterView, imageUri);

        if (position == mSelectedItemPosition) {
            if (mTwoPane) {
                toggleCard((CardView) viewHolder.itemView);
                ((MainActivity) mContext).onPosterClicked(movie);
            }
        }
    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    @Override
    public long getItemId(int position) {

        if (position > 1) {
            return getItem(position).getId();
        }

        return super.getItemId(position);
    }

    public void clear() {

        mItems.clear();
        notifyDataSetChanged();
    }

    public Movie getItem(int position) {

        return mItems.get(position);
    }

    public void remove(Movie changedMovie) {

        mItems.remove(changedMovie);
        notifyDataSetChanged();
    }

    public void addMovie(Movie movie) {

        mItems.add(movie);
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getItems() {

        return mItems;
    }

    public void setItems(ArrayList<Movie> items) {

        this.mItems = items;
        notifyDataSetChanged();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {

            super(itemView);
            moviePosterView = (ImageView) itemView.findViewById(R.id.movie_poster_image_item);

            //Attach listener
            moviePosterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    toggleCard((CardView) itemView);
                    mSelectedItemPosition = mItems.indexOf(mMovie);
                    //Callback to MovieListCallback activity method
                    if (mContext instanceof MovieListCallback) {
                        ((MainActivity) mContext).onPosterClicked(mMovie);
                    }
                }
            });

        }

        private ImageView moviePosterView;
        private Movie mMovie;
    }

    protected void toggleCard(CardView itemView) {

        if (mSelectedCardView != null) {
            mSelectedCardView.setSelected(!mSelectedCardView.isSelected());
            String colorStr = mContext.getResources().getString(R.string
                    .card_backgroundcolor);
            mSelectedCardView.setCardBackgroundColor(Color.parseColor(colorStr));
        }

        mSelectedCardView = itemView;
        mSelectedCardView.setSelected(true);
        if (mSelectedCardView.isSelected()) {
            String colorStr = mContext.getResources().getString(R.string
                    .card_backgroundcolor_selected);
            mSelectedCardView.setCardBackgroundColor(Color.parseColor(colorStr));
        }
    }

    protected static int mSelectedItemPosition;
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();
    private boolean mTwoPane;
    private CardView mSelectedCardView;
    private ArrayList<Movie> mItems;
    private Context mContext;
    private int mRowLayoutRes;
}
