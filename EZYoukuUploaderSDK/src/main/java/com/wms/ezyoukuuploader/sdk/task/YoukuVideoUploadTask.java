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

package com.wms.ezyoukuuploader.sdk.task;

import android.os.AsyncTask;
import android.widget.Toast;

import com.wms.ezyoukuuploader.sdk.R;
import com.wms.ezyoukuuploader.sdk.activity.UploadVideoActivity;
import com.wms.ezyoukuuploader.sdk.util.DialogUtil;
import com.wms.ezyoukuuploader.sdk.util.SharedPreferenceUtil;
import com.wms.ezyoukuuploader.sdk.youku.YoukuConstants;
import com.youku.uploader.IUploadResponseHandler;
import com.youku.uploader.YoukuUploader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class YoukuVideoUploadTask extends AsyncTask<String, Integer, String> {

	private UploadVideoActivity activity = null;

	public YoukuVideoUploadTask(UploadVideoActivity activity) {
		this.activity = activity;
	}
	
	protected void onPreExecute() {

	}
	
	@Override
	protected String doInBackground(String... params) { // params[0] is file name, params[1] is video title, params[2] is description
		YoukuUploader youkuUploader = activity.getYoukuUploader();

		HashMap<String, String> parameters = new HashMap<>();
		String accessToken = SharedPreferenceUtil.getPreferenceItemByName(activity, SharedPreferenceUtil.YoukuAccessToken);
		parameters.put(YoukuConstants.PARAM_ACCESS_TOKEN, accessToken);

		HashMap<String, String> uploadInfo = new HashMap<>();
		uploadInfo.put("file_name", params[0]);
		uploadInfo.put("title", params[1]);
		uploadInfo.put("description", params[2]);
		uploadInfo.put("tags", "EZYoukuUploader");

		youkuUploader.upload(parameters, uploadInfo, new IUploadResponseHandler() {

			@Override
			public void onStart() {
				activity.toggleUploadingFlag(true);
				activity.toggleWidgetsEnabled(false);
				activity.resetProgress();
				Toast.makeText(activity, activity.getString(R.string.startUploadingVideo), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(JSONObject response) {
				activity.getTextViewProgress().setText("100%");
				try {
					String videoId = response.getString("video_id");
					activity.getTextViewVideoUrl().setText("http://v.youku.com/v_show/id_" + videoId);
				}
				catch (JSONException e) {
					// URL will not be updated
				}

				activity.toggleWidgetsEnabled(true);
				activity.preventUploadingSameVideo();
				Toast.makeText(activity, activity.getString(R.string.videoUploadCompleted), Toast.LENGTH_LONG).show();
			}

			@Override
			public void onProgressUpdate(int counter) {
				activity.getProgressBarUploadVideo().setProgress(counter);
				if (counter < 10) {
					activity.getTextViewProgress().setText(" 0" + counter + "%");
				}
				else {
					activity.getTextViewProgress().setText(" " + counter + "%");
				}
			}

			@Override
			public void onFailure(JSONObject errorResponse) {
				activity.toggleWidgetsEnabled(true);
				DialogUtil.showExceptionAlertDialog(activity, activity.getString(R.string.videoUploadFailedTitle), activity.getString(R.string.videoUploadFailed));
			}

			@Override
			public void onFinished() {
				activity.toggleUploadingFlag(false);
			}
		});

		return null;
	}
    
    protected void onPostExecute(String result) {

    }

}
