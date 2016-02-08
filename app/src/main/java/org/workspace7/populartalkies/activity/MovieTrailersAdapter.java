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
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Trailer;
import org.workspace7.populartalkies.util.Utility;

import java.util.ArrayList;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.ViewHolder> {

    public MovieTrailersAdapter(Context context, int rowLayoutRes) {

        this(context, rowLayoutRes, new ArrayList<Trailer>());
    }

    public MovieTrailersAdapter(Context context, int rowLayoutRes, ArrayList<Trailer> items) {

        mItems = items;
        mContext = context;
        mRowLayoutRes = rowLayoutRes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayoutRes, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        Trailer trailer = getItem(position);

        viewHolder.mTrailer = trailer;

        viewHolder.trailerNameView.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {

        return mItems.size();
    }

    public Trailer getItem(int position) {

        return mItems.get(position);
    }

    public long getItemId(int position) {

        return 0;
    }

    public void setItems(ArrayList<Trailer> items) {

        this.mItems = items;
        notifyDataSetChanged();
    }

    public void clear() {

        mItems.clear();
        notifyDataSetChanged();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(final View itemView) {

            super(itemView);

            trailerNameView = (TextView) itemView.findViewById(R.id.trailer_name);
            trailerActions = (ImageButton) itemView.findViewById(R.id.trailer_actions);
            trailerActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PopupMenu popupMenu = new PopupMenu(mContext, trailerActions);
                    popupMenu.inflate(R.menu.trailer_actions);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            int itemId = item.getItemId();
                            switch (itemId) {
                                case R.id.action_share: {
                                    final Intent shareIntent = Utility.shareTrailerIntent(mTrailer);
                                    mContext.startActivity(Intent.createChooser(shareIntent, mContext
                                            .getString(R.string.action_share)));
                                    return true;
                                }
                                case R.id.action_play: {
                                    final Intent youtubeIntent = Utility.youTubeIntent(mTrailer);
                                    if (mContext.getPackageManager()
                                            .resolveActivity(youtubeIntent, 0) != null) {
                                        mContext.startActivity(youtubeIntent);
                                    }
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }

        Trailer mTrailer;
        ImageView trailerActions;
        TextView trailerNameView;
    }

    private static final String LOG_TAG = MovieTrailersAdapter.class.getSimpleName();

    private final Context mContext;
    private ArrayList<Trailer> mItems;
    private int mRowLayoutRes;
}
