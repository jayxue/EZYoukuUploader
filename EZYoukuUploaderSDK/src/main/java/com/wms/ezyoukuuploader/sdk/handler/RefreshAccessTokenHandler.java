/*
 * Copyright 2016 Waterloo Mobile Studio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wms.ezyoukuuploader.sdk.handler;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.wms.ezyoukuuploader.sdk.R;
import com.wms.ezyoukuuploader.sdk.activity.UploadVideoActivity;

public class RefreshAccessTokenHandler extends Handler {

	private UploadVideoActivity activity;

	public RefreshAccessTokenHandler(UploadVideoActivity activity) {
		this.activity = activity;
	}

	public void handleMessage(Message msg) {
		super.handleMessage(msg);

		if (msg.what == HandlerMessage.YOUKU_ACCESS_TOKEN_REFRESH_FAILED) {
			Toast.makeText(activity, activity.getString(R.string.relogin), Toast.LENGTH_LONG).show();
			activity.startGetRefreshTokenActivity();
		}
		else if(msg.what == HandlerMessage.YOUKU_ACCESS_TOKEN_REFRESH_SUCCESS) {
			// User has successfully logged in and access token was successfully refreshed, so start uploading
			activity.uploadVideo();
		}
	}

}
