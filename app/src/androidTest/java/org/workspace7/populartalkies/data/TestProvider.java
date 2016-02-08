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

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.ArrayList;
import java.util.List;


import static org.workspace7.populartalkies.data.MovieContract.CONTENT_AUTHORITY;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry.CONTENT_TYPE;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry.CONTENT_URI;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry.TABLE_NAME;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry._ID;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class TestProvider extends AndroidTestCase {

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                CONTENT_URI,
                null,
                null
        );
    }

    public void deleteAllRecordsFromDB() {

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public void setUp() throws Exception {

        super.setUp();
        deleteAllRecordsFromProvider();
    }

    @SmallTest
    public void testProviderRegistry() {

        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                PopularTalkiesProvider.class.getName());
        try {

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: PopularTalkies Provider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + CONTENT_AUTHORITY,
                    providerInfo.authority, CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: PopularTalkies Provider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    @SmallTest
    public void testGetType() {
        // content://org.workspace7.populartalkies/movie/
        String type = mContext.getContentResolver().getType(CONTENT_URI);
        assertEquals("Error: the MovieFavoriteEntry CONTENT_URI should return MovieFavoriteEntry.CONTENT_TYPE",
                CONTENT_TYPE, type);

        int testMovie = 1234;

        // content://org.workspace7.populartalkies/movie/1234
        type = mContext.getContentResolver().getType(
                MovieContract.FavoriteMovieEntry.buildMovieUri(testMovie));

        // vnd.android.cursor.dir/org.workspace7.populartalkies/movie
        assertEquals("Error: the MovieFavoriteEntry CONTENT_URI with movie should return MovieFavoriteEntry.CONTENT_ITEM_TYPE",
                FavoriteMovieEntry.CONTENT_ITEM_TYPE, type);

    }

    @SmallTest
    public int testInsertOperationsTest() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, 1234);
        contentValues.put(COLUMN_MOVIE_POSTER_PATH, "/popularmovies.png");

        Uri favoritesUri = mContext.getContentResolver()
                .insert(FavoriteMovieEntry.CONTENT_URI, contentValues);
        assertTrue(favoritesUri != null);

        //Query and check the values
        Cursor cursor =
                mContext.getContentResolver().query(FavoriteMovieEntry.buildMovieUri(1234), null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals(1234, cursor.getInt(0));
        assertEquals("/popularmovies.png", cursor.getString(1));

        cursor.close();

        return 1234;
    }

    @SmallTest
    public void testUpdateOperationsTest() {

        int movieId = testInsertOperationsTest();

        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, 1234);
        contentValues.put(COLUMN_MOVIE_POSTER_PATH, "/popularmovies1.png");

        String where = FavoriteMovieEntry._ID + " = ? ";

        String[] whereArgs = new String[]{String.valueOf(movieId)};

        int updated = mContext.getContentResolver()
                .update(FavoriteMovieEntry.CONTENT_URI, contentValues, where, whereArgs);

        assertTrue(updated == 1);

        //Query and check the values
        Cursor cursor =
                mContext.getContentResolver().query(FavoriteMovieEntry.buildMovieUri(1234), null, null, null, null);
        assertTrue(cursor.moveToFirst());
        assertEquals(1234, cursor.getInt(0));
        assertEquals("/popularmovies1.png", cursor.getString(1));

        cursor.close();
    }

    @SmallTest
    public void testQueryOperationsTest() {

        List<ContentValues> favorites = new ArrayList<>();
        ContentValues contentValues = new ContentValues();
        contentValues.put(_ID, 1234);
        contentValues.put(COLUMN_MOVIE_POSTER_PATH, "/popularmovies.png");
        favorites.add(contentValues);
        contentValues = new ContentValues(contentValues);
        contentValues.put(_ID, 4567);
        contentValues.put(COLUMN_MOVIE_POSTER_PATH, "/popularmovies1.png");
        favorites.add(contentValues);

        for (ContentValues favorite : favorites
                ) {
            Uri favoritesUri = mContext.getContentResolver()
                    .insert(FavoriteMovieEntry.CONTENT_URI, favorite);
            assertTrue(favoritesUri != null);
        }
        //Query and check the values
        Cursor cursor =
                mContext.getContentResolver().query(FavoriteMovieEntry.CONTENT_URI, null, null, null, null);
        assertEquals(2, cursor.getCount());

        assertTrue(cursor.moveToNext());
        assertEquals(1234, cursor.getInt(0));
        assertEquals("/popularmovies.png", cursor.getString(1));

        assertTrue(cursor.moveToNext());
        assertEquals(4567, cursor.getInt(0));
        assertEquals("/popularmovies1.png", cursor.getString(1));

        cursor.close();
    }
}
