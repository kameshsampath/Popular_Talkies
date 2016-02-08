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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry.TABLE_NAME;
import static org.workspace7.populartalkies.data.MovieContract.FavoriteMovieEntry._ID;
import static org.workspace7.populartalkies.data.MovieDbHelper.DATABASE_NAME;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class TestDb extends AndroidTestCase {

    public void setUp() {

        mContext.deleteDatabase(DATABASE_NAME);
    }

    @SmallTest
    public void testCreateDB() {

        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(TABLE_NAME);

        mContext.deleteDatabase(DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was created without movie favorites table",
                tableNameHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(_ID);
        locationColumnHashSet.add(COLUMN_MOVIE_POSTER_PATH);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie favorite entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    @SmallTest
    public long testMovieFavoriteTable() {

        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        ContentValues testValues = new ContentValues();
        testValues.put(_ID, 123456);
        testValues.put(COLUMN_MOVIE_POSTER_PATH, "/popularmovies.png");

        long rowId = db.insert(TABLE_NAME, null, testValues);
        assertTrue(rowId != -1);

        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        assertTrue("No Record inserted by the query", cursor.moveToFirst());

        validateCurrentRecord("Unable to find the inserted record", cursor, testValues);

        cursor.close();
        db.close();

        return rowId;

    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}
