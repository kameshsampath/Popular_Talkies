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

package org.workspace7.populartalkies.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.workspace7.populartalkies.R;
import org.workspace7.populartalkies.parcel.Trailer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class Utility {

    public static long stringToDate(String dateStr) {

        long dateInMillis = 0;

        try {
            dateInMillis = RELEASE_DATE_FORMATTER.parse(dateStr).getTime();
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing date " + dateStr);
        }

        return dateInMillis;
    }

    public static String yearFromDate(long dateInMillis) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        int year = calendar.get(Calendar.YEAR);
        return String.valueOf(year);
    }

    public static String yearFromDate(String dateStr) {

        int year;
        try {
            Date date = RELEASE_DATE_FORMATTER.parse(dateStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date.getTime());
            year = calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing date " + dateStr);
            year = 1970; //set to default start date
        }
        return String.valueOf(year);
    }

    public static int dpToPx(Context context, float dp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        Log.v(LOG_TAG, "PX:" + px);
        return px;
    }

    public static Uri buildYouTubeUri(String key) {

        return Uri.parse("vnd.youtube://" + key);
    }

    public static Intent shareTrailerIntent(Trailer trailer) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        final Uri youtoubeWebUri = buildYouTubeWebUri(trailer.getKey());
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, trailer.getName());
        shareIntent.putExtra(Intent.EXTRA_TEXT, youtoubeWebUri.toString());
        return shareIntent;
    }

    public static Uri buildYouTubeWebUri(String key) {

        return Uri.parse("http://www.youtube.com/watch?v=" + key);
    }

    public static Drawable drawableFromBitmap(Context context, int drawableResId) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable
                .movie_poster_placeholder_w185);

        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);

        return drawable;
    }

    public static int getMinWidth(Drawable drawable) {

        if (drawable != null) {
            return drawable.getMinimumWidth();
        }

        return -1;
    }

    public static int getMinHeight(Drawable drawable) {

        if (drawable != null) {
            return drawable.getMinimumHeight();
        }

        return -1;
    }

    public static void loadImageIntoView(Context context, int drawableResId, ImageView imageView,
                                         Uri imageUri)
            throws IllegalStateException {

        Drawable drawable = drawableFromBitmap(context, drawableResId);

        int width = getMinWidth(drawable);

        int height = getMinHeight(drawable);

        Glide.with(context).load(imageUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(width, height)
                .fitCenter().into
                (imageView);

    }

    public static Intent youTubeIntent(Trailer trailer) {

        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW);
        final String key = trailer.getKey();
        youtubeIntent.setData(buildYouTubeUri(key));
        youtubeIntent.putExtra(Intent.EXTRA_SUBJECT, trailer.getName());
        return youtubeIntent;
    }

    public static int getMovieCategoryIndex(Context context, String categoryStr) {

        String[] movieCategories = context.getResources().getStringArray(R.array.movie_category_values);

        int categoryIndex = 0;

        if (movieCategories != null) {
            for (int i = 0; i < movieCategories.length; i++) {
                if (movieCategories[i].equalsIgnoreCase(categoryStr)) {
                    categoryIndex = i;
                    break;
                }
            }
        }

        return categoryIndex;
    }

    private static final String LOG_TAG = Utility.class.getSimpleName();
    static final DateFormat RELEASE_DATE_FORMATTER = new SimpleDateFormat(Constants.RELEASE_DATE_FORMAT);
}
