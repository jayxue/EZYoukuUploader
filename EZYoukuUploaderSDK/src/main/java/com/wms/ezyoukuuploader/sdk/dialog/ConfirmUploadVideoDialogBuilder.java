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

package com.wms.ezyoukuuploader.sdk.dialog;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

import com.wms.ezyoukuuploader.sdk.R;
import com.wms.ezyoukuuploader.sdk.activity.GetRefreshTokenActivity;
import com.wms.ezyoukuuploader.sdk.activity.UploadVideoActivity;
import com.wms.ezyoukuuploader.sdk.handler.RefreshAccessTokenHandler;
import com.wms.ezyoukuuploader.sdk.task.FetchYoukuAccessTokenTask;
import com.wms.ezyoukuuploader.sdk.util.ActivityUtil;
import com.wms.ezyoukuuploader.sdk.util.SharedPreferenceUtil;

public class ConfirmUploadVideoDialogBuilder extends Builder {

	private UploadVideoActivity activity = null;

	public ConfirmUploadVideoDialogBuilder(UploadVideoActivity activity) {
		super(activity);
		this.activity = activity;
		this.setCancelable(false);
		this.setMessage(getContext().getString(R.string.confirmUploadVideo));
		this.setNegativeButton(getContext().getString(R.string.cancel), null);
		this.setPositiveButton(getContext().getString(R.string.yes), new ConfirmUploadVideoDialogOnClickListener());
	}

	private class ConfirmUploadVideoDialogOnClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String refreshCode = SharedPreferenceUtil.getPreferenceItemByName(activity, SharedPreferenceUtil.YoukuRefreshToken);
			if(refreshCode.isEmpty()) {
				// The user has never logged in. Open another activity to login
				ActivityUtil.goToActivity(activity, GetRefreshTokenActivity.class);
			}
			else {
				// The user has logged in. Fetch new access token with the refresh token
				FetchYoukuAccessTokenTask fetchAccessTokenTask = new FetchYoukuAccessTokenTask(activity, new RefreshAccessTokenHandler(activity));
				fetchAccessTokenTask.execute(refreshCode);
			}
		}

	}
}
