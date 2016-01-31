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

package com.wms.ezyoukuuploader.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {

	public static String YoukuRefreshToken = "YoukuRefreshToken";
	public static String YoukuAccessToken = "YoukuAccessToken";

	private static String preferencesFileName = "PreferencesFile";

	public static String getPreferenceItemByName(Context context, String itemName) {
		SharedPreferences settings = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE);
		return settings.getString(itemName, "");
	}

	public static void savePreferenceItemByName(Context context, String itemName, String value) {
		SharedPreferences.Editor editor = context.getSharedPreferences(preferencesFileName, Activity.MODE_PRIVATE).edit();
		editor.putString(itemName, value);
		editor.commit();
	}

}
