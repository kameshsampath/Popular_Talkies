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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.util.Utility;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class MovieCategoriesAdapter extends ArrayAdapter<String> {

    public MovieCategoriesAdapter(Context context) {

        this(context,
                context.getResources().getStringArray(R.array.movie_category_labels));
    }

    public MovieCategoriesAdapter(Context context, String[] items) {

        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String categoryText = getItem(position);

        View rootView;

        if (convertView == null) {
            rootView = LayoutInflater.from(getContext())
                    .inflate(R.layout.movie_caterories_drawer_item, parent, false);
            convertView = rootView;
        }

        ViewHolder viewHolder = new ViewHolder(convertView);

        int drawable;
        String categoryLabel = categoryText;

        if ("popular".equalsIgnoreCase(categoryText)) {
            drawable = R.drawable.ic_popular;
        } else if ("Top Rated".equalsIgnoreCase(categoryText)) {
            drawable = R.drawable.ic_rating;
        } else if ("Favorites".equalsIgnoreCase(categoryText)) {
            drawable = R.drawable.ic_favorite;
        } else {
            categoryLabel = getContext().getString(R.string.movie_release_year_format,
                    Utility.yearFromDate(System.currentTimeMillis()));
            drawable = R.drawable.ic_release_year;
        }

        viewHolder.categoryText.setText(categoryLabel);
        viewHolder.categoryIcon.setImageResource(drawable);

        return convertView;
    }

    private final class ViewHolder {

        public ViewHolder(View view) {

            categoryIcon = (ImageView) view.findViewById(R.id.movie_category_icon);
            categoryText = (TextView) view.findViewById(R.id.movie_category_text);
        }

        private ImageView categoryIcon;
        private TextView categoryText;
    }
}
