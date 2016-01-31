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

package com.wms.ezyoukuuploader.sdk.activity;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wms.ezyoukuuploader.sdk.R;
import com.wms.ezyoukuuploader.sdk.util.SharedPreferenceUtil;
import com.wms.ezyoukuuploader.sdk.youku.YoukuConstants;

/**
 * Get refresh token from Youku. Before uploading a new video, the refresh token will be used to refresh OAuth access token.
 */
public class GetRefreshTokenActivity extends Activity {

	private WebView webviewYouku = null;
	private ProgressBar progressBarLoading = null;

	private String authorizationCode = null;
	private String accessToken = null;
	private String refreshToken = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.youku_login);

		progressBarLoading = (ProgressBar) findViewById(R.id.progressBarLoading);

		webviewYouku = (WebView) findViewById(R.id.webViewYouku);
		webviewYouku.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				String[] str = url.split("=")[1].split("&");
				authorizationCode = str[0];
				String refreshToken = getRefreshToken();
				if (refreshToken != null) {
					Toast.makeText(getApplicationContext(), getString(R.string.loginSuccessful), Toast.LENGTH_LONG).show();
					finish();
				}

				return false;
			}
		});
		webviewYouku.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				if (progress == 100) {
					progressBarLoading.setVisibility(View.INVISIBLE);
				}
			}
		});
		String url = YoukuConstants.YOUKU_AUTHORIZE_URL + "?" + YoukuConstants.PARAM_CLIENT_ID + "=" + getString(R.string.YOUKU_APP_CLIENT_ID) + "&" + YoukuConstants.PARAM_RESPONSE_TYPE + "=" + YoukuConstants.PARAM_AUTHORIZATION_CODE + "&" + YoukuConstants.PARAM_REDIRECT_URI + "=" + getString(R.string.YOUKU_APP_REDIRECT_URI);
		webviewYouku.loadUrl(url);

		Toast.makeText(this, getString(R.string.pleaseLogin), Toast.LENGTH_LONG).show();
	}

	private String getRefreshToken() {

		Thread t = new Thread(new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				HttpPost httpPost = new HttpPost(YoukuConstants.YOUKU_OAUTH2_URL);
				List<NameValuePair> params = new ArrayList<>();
				params.add(new BasicNameValuePair(YoukuConstants.PARAM_CLIENT_ID, getString(R.string.YOUKU_APP_CLIENT_ID)));
				params.add(new BasicNameValuePair(YoukuConstants.PARAM_CLIENT_SECRET, getString(R.string.YOUKU_APP_CLIENT_SECRET)));
				params.add(new BasicNameValuePair(YoukuConstants.PARAM_GRANT_TYPE, YoukuConstants.GRANT_TYPE_AUTHORIZATION_CODE));
				params.add(new BasicNameValuePair(YoukuConstants.PARAM_AUTHORIZATION_CODE, authorizationCode));
				params.add(new BasicNameValuePair(YoukuConstants.PARAM_REDIRECT_URI, getString(R.string.YOUKU_APP_REDIRECT_URI)));
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpPost);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						String result = EntityUtils.toString(httpResponse.getEntity());
						JSONObject object = new JSONObject(result);
						accessToken = object.getString(YoukuConstants.PARAM_ACCESS_TOKEN);
						refreshToken = object.getString(YoukuConstants.PARAM_REFRESH_TOKEN);
						SharedPreferenceUtil.savePreferenceItemByName(GetRefreshTokenActivity.this, SharedPreferenceUtil.YoukuAccessToken, accessToken);
						SharedPreferenceUtil.savePreferenceItemByName(GetRefreshTokenActivity.this, SharedPreferenceUtil.YoukuRefreshToken, refreshToken);
					}
				}
				catch (Exception e) {
					// refreshToken remains null. No need to do anything
				}
			}

		});

		t.start();
		try {
			t.join();
		}
		catch (InterruptedException e) {
			// refreshToken remains null. No need to do anything
		}

		return refreshToken;
	}

	@Override
	protected void onDestroy() {
		if(webviewYouku != null){
			webviewYouku = null;
		}

		super.onDestroy();
	}

}