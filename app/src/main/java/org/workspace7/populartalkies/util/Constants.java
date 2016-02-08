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

import org.workspace7.populartalkies.data.MovieContract;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public interface Constants {

    String MOVIE_BASE_URL = "http://api.themoviedb.org/3";

    String MOVIE_POSTER_PATH_URL = "http://image.tmdb.org/t/p";

    String YOUTUBE_URL = "http//www.youtube.com/watch";

    String URL_PATH_MOVIE = "movie";

    String URL_PATH_TRAILERS = "videos";

    String URL_PATH_REVIEWS = "reviews";

    String RELEASE_DATE_FORMAT = "yyyy-mm-dd";

    String PARAM_API_KEY = "api_key";

    String PARAM_SORT = "sort_by";

    String PARAM_PRIMARY_RELEASE_DATE = "primary_release_year";

    String PARAM_YOUTUBE_V = "v";

    String PATH_DISCOVERY = "discover";

    String PATH_MOVIE = "movie";

    String JSON_ATTR_ID = "id";

    String JSON_ATTR_RESULTS = "results";

    String JSON_ATTR_TITLE = "title";

    String JSON_ATTR_POSTER_PATH = "poster_path";

    String JSON_ATTR_OVERVIEW = "overview";

    String JSON_ATTR_RELEASE_DATE = "release_date";

    String JSON_ATTR_VOTE_AVERAGE = "vote_average";

    String JSON_ATTR_RUNTIME = "runtime";

    String JSON_ATTR_KEY = "key";

    String JSON_ATTR_NAME = "name";

    String JSON_ATTR_AUTHOR = "author";

    String JSON_ATTR_CONTENT = "content";


    String[] MOVIE_PROJECTION_COLUMNS = {
            MovieContract.FavoriteMovieEntry._ID,
            MovieContract.FavoriteMovieEntry.COLUMN_MOVIE_POSTER_PATH,
    };

    //Projection indicies - update this to match project columns a
    int COL_MOVIE_ID = 0;
    int COL_MOVIE_POSTER = 1;

    String MOVIE_ID = "MOVIE_ID";
}
