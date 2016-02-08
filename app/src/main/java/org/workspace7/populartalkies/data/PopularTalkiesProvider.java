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

package org.workspace7.populartalkies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


import static org.workspace7.populartalkies.data.MovieContract.CONTENT_AUTHORITY;
import static org.workspace7.populartalkies.data.MovieContract.PATH_FAVORITE_MOVIE;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class PopularTalkiesProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        sUriMatcher = buildUriMatcher(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri) {

        int uriType = sUriMatcher.match(uri);

        switch (uriType) {
            case FAVORITE: {
                return MovieContract.FavoriteMovieEntry.CONTENT_ITEM_TYPE;
            }
            case FAVORITES: {
                return MovieContract.FavoriteMovieEntry.CONTENT_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown Uri " + uri);
            }
        }
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case FAVORITES: {
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        MovieContract.FavoriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case FAVORITE: {
                retCursor = movieByPK(uri, projection, sortOrder);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri " + uri);
            }
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;

    }
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        final int type = sUriMatcher.match(uri);

        Uri returnUri;

        switch (type) {
            case FAVORITES: {
                long _id = db.insert(MovieContract.FavoriteMovieEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MovieContract.FavoriteMovieEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row with uri" + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown  URI " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        final int type = sUriMatcher.match(uri);

        int deleted;

        if (selection == null) {
            selection = "1";
        }

        switch (type) {
            case FAVORITES: {
                deleted = db.delete(MovieContract.FavoriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown  URI " + uri);
            }
        }

        if (deleted != 0) {
            Uri notificationUri = uri;
            if (selectionArgs != null && selectionArgs.length > 0) {
                notificationUri = MovieContract.FavoriteMovieEntry
                        .buildMovieUri(Integer.parseInt(selectionArgs[0]));
            }
            getContext().getContentResolver().notifyChange(notificationUri, null);
        }

        return deleted;
    }
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        final int type = sUriMatcher.match(uri);

        int updated;

        if (selection == null) {
            selection = "1";
        }

        switch (type) {
            case FAVORITES: {
                updated = db.update(MovieContract.FavoriteMovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException(" Unknown  URI " + uri);
            }
        }

        if (updated != 0) {

            Uri notificationUri = uri;

            if (selectionArgs != null && selectionArgs.length > 0) {
                notificationUri = MovieContract.FavoriteMovieEntry
                        .buildMovieUri(Integer.parseInt(selectionArgs[0]));
            }

            getContext().getContentResolver().notifyChange(notificationUri, null);
        }

        return updated;
    }

    private UriMatcher buildUriMatcher(Context context) {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, PATH_FAVORITE_MOVIE, FAVORITES);

        uriMatcher.addURI(authority, PATH_FAVORITE_MOVIE + "/#", FAVORITE);

        return uriMatcher;
    }
    private Cursor movieByPK(Uri uri, String[] projection, String sortOrder) {

        String movieId = MovieContract.FavoriteMovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs = new String[]{movieId};

        Cursor cursor = mMovieDbHelper.getReadableDatabase().query(
                MovieContract.FavoriteMovieEntry.TABLE_NAME,
                projection,
                sMovieIdSelection,
                selectionArgs,
                null,
                null,
                sortOrder);

        return cursor;
    }

    private static final String LOG_TAG = PopularTalkiesProvider.class.getSimpleName();
    private static final String sMovieIdSelection = MovieContract.FavoriteMovieEntry._ID + " = ? ";
    private UriMatcher sUriMatcher;
    private MovieDbHelper mMovieDbHelper;
    static final int FAVORITE = 200;
    static final int FAVORITES = 202;

}
