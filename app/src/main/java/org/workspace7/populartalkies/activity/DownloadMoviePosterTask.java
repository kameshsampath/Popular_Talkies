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
import android.net.Uri;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
public class DownloadMoviePosterTask extends AsyncTask<String, Void, Void> {

    public DownloadMoviePosterTask(Context context) {

        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        Uri imageUri = Uri.parse(params[0]);
        int width = Integer.parseInt(params[1]);
        int height = Integer.parseInt(params[2]);

        Glide.with(mContext).load(imageUri).downloadOnly(width, height);

        return null;
    }

    private static final String LOG_TAG = DownloadMoviePosterTask.class.getSimpleName();
    private final Context mContext;

}
