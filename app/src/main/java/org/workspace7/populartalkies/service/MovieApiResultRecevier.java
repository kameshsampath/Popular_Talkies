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

package org.workspace7.populartalkies.service;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * @author Kamesh Sampath<kamesh.sampath@hotmail.com>
 */
@SuppressLint("ParcelCreator")
public class MovieApiResultRecevier extends ResultReceiver {

    public MovieApiResultRecevier(Handler handler) {
        super(handler);
    }
    public void setRecevier(Receiver receiver) {
        mRecevier = receiver;
    }
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mRecevier != null) {
            mRecevier.notifyRefresh(resultCode, resultData);
        }
    }

    private Receiver mRecevier;
}
