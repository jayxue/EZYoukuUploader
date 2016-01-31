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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.wms.ezyoukuuploader.sdk.R;
import com.wms.ezyoukuuploader.sdk.handler.HandlerMessage;
import com.wms.ezyoukuuploader.sdk.util.DialogUtil;
import com.wms.ezyoukuuploader.sdk.util.MessageUtil;
import com.wms.ezyoukuuploader.sdk.util.SharedPreferenceUtil;
import com.wms.ezyoukuuploader.sdk.youku.YoukuConstants;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetch Youku OAuth2 access token.
 */
public class FetchYoukuAccessTokenTask extends AsyncTask<String, Void, String> {

	private Context context = null;
	private Handler handler = null;

	private ProgressDialog progressDialog = null;

	public FetchYoukuAccessTokenTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	protected void onPreExecute() {
		progressDialog = DialogUtil.showWaitingProgressDialog(context, ProgressDialog.STYLE_SPINNER, context.getString(R.string.authorizingUser), false);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected String doInBackground(String... params) {
		// params[0] is refresh token
		String refreshToken = params[0];

		String accessToken = null;

		HttpPost httpPost = new HttpPost(YoukuConstants.YOUKU_OAUTH2_URL);
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair(YoukuConstants.PARAM_CLIENT_ID, context.getString(R.string.YOUKU_APP_CLIENT_ID)));
		parameters.add(new BasicNameValuePair(YoukuConstants.PARAM_CLIENT_SECRET, context.getString(R.string.YOUKU_APP_CLIENT_SECRET)));
		parameters.add(new BasicNameValuePair(YoukuConstants.PARAM_GRANT_TYPE, YoukuConstants.GRANT_TYPE_REFRESH_TOKEN));
		parameters.add(new BasicNameValuePair(YoukuConstants.PARAM_REFRESH_TOKEN, refreshToken));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				JSONObject object = new JSONObject(result);
				accessToken = object.getString(YoukuConstants.PARAM_ACCESS_TOKEN);
				String newRefreshToken = object.getString(YoukuConstants.PARAM_REFRESH_TOKEN);
				SharedPreferenceUtil.savePreferenceItemByName(context, SharedPreferenceUtil.YoukuRefreshToken, newRefreshToken);
				SharedPreferenceUtil.savePreferenceItemByName(context, SharedPreferenceUtil.YoukuAccessToken, accessToken);
			}
		}
		catch (Exception e) {
			// accessToken will remain null
		};
		return accessToken;
	}

	protected void onPostExecute(String result) {
		progressDialog.dismiss();
		if(result == null) {
			MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUKU_ACCESS_TOKEN_REFRESH_FAILED);
		}
		else {
			MessageUtil.sendHandlerMessage(handler, HandlerMessage.YOUKU_ACCESS_TOKEN_REFRESH_SUCCESS);
		}
	}
}
