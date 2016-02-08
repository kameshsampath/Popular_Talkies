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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.MovieReview;

import java.util.ArrayList;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder> {

    public MovieReviewsAdapter(Context context, int layoutResId, String posterPath) {

        this(context, layoutResId, new ArrayList<MovieReview>(), posterPath);
    }

    public MovieReviewsAdapter(Context context, int layoutResId, ArrayList<MovieReview> items, String
            posterPath) {

        mContext = context;
        mItems = items;
        mPosterPath = posterPath;
        mRowLayoutRes = layoutResId;

    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    public MovieReview getItem(int position) {

        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public void setItems(ArrayList<MovieReview> items) {

        this.mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(mRowLayoutRes, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        MovieReview movieReview = getItem(position);

        viewHolder.mMovieReview = movieReview;

        viewHolder.mAuthorView.setText(movieReview.getAuthor());

        viewHolder.mContentView.setText(movieReview.getContent());

    }

    public void clear() {

        mItems.clear();
        notifyDataSetChanged();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {

            super(itemView);
            mAuthorView = (TextView) itemView.findViewById(R.id.movie_review_author);
            mContentView = (TextView) itemView.findViewById(R.id.movie_review_content);
            movieImageView = (ImageView) itemView.findViewById(R.id.movie_poster_image);
        }

        private TextView mAuthorView;
        private TextView mContentView;
        private ImageView movieImageView;
        private MovieReview mMovieReview;
    }

    private final Context mContext;
    private final String mPosterPath;
    private ArrayList<MovieReview> mItems;
    private int mRowLayoutRes;
}
