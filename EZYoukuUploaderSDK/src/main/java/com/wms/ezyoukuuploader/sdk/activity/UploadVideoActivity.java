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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.wms.ezyoukuuploader.sdk.R;
import com.wms.ezyoukuuploader.sdk.dialog.ConfirmUploadVideoDialogBuilder;
import com.wms.ezyoukuuploader.sdk.listener.ImageButtonBackgroundSelector;
import com.wms.ezyoukuuploader.sdk.task.YoukuVideoUploadTask;
import com.wms.ezyoukuuploader.sdk.util.ActivityUtil;
import com.wms.ezyoukuuploader.sdk.util.DialogUtil;
import com.wms.ezyoukuuploader.sdk.util.FileUtil;
import com.youku.uploader.YoukuUploader;

import java.io.File;
import java.util.UUID;

public class UploadVideoActivity extends Activity {

	private ProgressBar progressBarUploadVideo = null;
	private EditText editTextVideoTitle = null;
	private EditText editTextVideoDescription = null;
	private TextView textViewFilePath = null;
	private TextView textViewVideoUrl = null;
	private TextView textViewProgress = null;
	private VideoView videoViewPreview = null;

	private ImageButton imageButtonTakeVideo = null;
	private ImageButton imageButtonGallery = null;
	private ImageButton imageButtonUploadVideo = null;

	private String videoFileName = null;

	private boolean isUploading = false;

	private YoukuUploader youkuUploader = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.upload_video);

		editTextVideoTitle = (EditText) findViewById(R.id.editTextTitle);
		editTextVideoDescription = (EditText) findViewById(R.id.editTextDescription);

		textViewFilePath = (TextView) findViewById(R.id.textViewFilePath);
		textViewVideoUrl = (TextView) findViewById(R.id.textViewVideoUrl);
		textViewProgress = (TextView) findViewById(R.id.textViewProgress);

		progressBarUploadVideo = (ProgressBar) findViewById(R.id.progressBarUploadVideo);

		videoViewPreview = (VideoView) findViewById(R.id.videoViewPreview);

		imageButtonUploadVideo = (ImageButton) findViewById(R.id.imageButtonUploadVideo);
		imageButtonUploadVideo.setOnClickListener(new ImageButtonUploadVideoOnClickListener());
		imageButtonUploadVideo.setOnTouchListener(new ImageButtonBackgroundSelector());
		imageButtonUploadVideo.setEnabled(false);

		imageButtonTakeVideo = (ImageButton) findViewById(R.id.imageButtonTakeVideo);
		imageButtonTakeVideo.setOnClickListener(new ImageButtonTakeVideoOnClickListener());
		imageButtonTakeVideo.setOnTouchListener(new ImageButtonBackgroundSelector());

		imageButtonGallery = (ImageButton) findViewById(R.id.imageButtonGallery);
		imageButtonGallery.setOnClickListener(new ImageButtonGalleryOnClickListener());
		imageButtonGallery.setOnTouchListener(new ImageButtonBackgroundSelector());

		youkuUploader = YoukuUploader.getInstance(getString(R.string.YOUKU_APP_CLIENT_ID), getString(R.string.YOUKU_APP_CLIENT_SECRET), this);

		// Do not show the soft keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public void onBackPressed() {
		if(isUploading) {
			Toast.makeText(this, getString(R.string.videoBeingUploaded), Toast.LENGTH_LONG).show();
		}
		else {
			super.onBackPressed();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == IntentRequestCode.TAKE_VIDEO && resultCode == RESULT_OK) {
			// videoFileName has been prepared for taking video
			File file = new File(videoFileName);
			// On Android 2.2, the file may not be created, therefore we need to check the returned URI.
			if (!file.exists()) {
				if (data.getData() != null) {
					videoFileName = getRealPathFromURI(data.getData());
					if(videoFileName != null) {
						onVideoReady();
					}
				}
				else {
					videoFileName = null;
					Toast.makeText(this, getString(R.string.noVideoAvailable), Toast.LENGTH_LONG).show();
				}
			}
			else {
				onVideoReady();
			}
		}
		else if (requestCode == IntentRequestCode.PICK_UP_VIDEO && resultCode == RESULT_OK) {
			Uri selectedVideo = data.getData();
			videoFileName = getRealPathFromURI(selectedVideo);
			if(videoFileName != null) {
				onVideoReady();
			}
			else {
				Toast.makeText(this, getString(R.string.noVideoAvailable), Toast.LENGTH_LONG).show();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private String getRealPathFromURI(Uri contentUri) {
		String filePath = null;
		String[] projection = { MediaStore.Video.Media.DATA };
		Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
		if(cursor != null && cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
			filePath = cursor.getString(columnIndex);
			cursor.close();
		}
		return filePath;
	}

	private class ImageButtonUploadVideoOnClickListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View v) {
			// A video should be available before uploading
			if(videoFileName == null) {
				Toast.makeText(UploadVideoActivity.this, R.string.takeOrSelectVideo, Toast.LENGTH_LONG).show();
				return;
			}

			// Title must be provided for a video
			if(editTextVideoTitle.getText().toString().trim().isEmpty()) {
				DialogUtil.showDialog(UploadVideoActivity.this, getString(R.string.enterVideoTitle));
				return;
			}

			new ConfirmUploadVideoDialogBuilder(UploadVideoActivity.this).create().show();
		}

	}

	private File getTempVideoFile() {
		// The method below will return a file path like: /mnt/sdcard/com.company.app
		videoFileName = FileUtil.getAppExternalStoragePath(this);
		File file = new File(videoFileName);
		if (!file.exists()) {
			// Create the folder if it does not exist
			file.mkdir();
		}

		// Generate a UUID as file name and attach to path
		videoFileName += "/" + UUID.randomUUID().toString() + ".3gp";

		file = new File(videoFileName);
		return file;
	}

	private class ImageButtonTakeVideoOnClickListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View v) {
			startTakingVideo();
		}

	}

	private class ImageButtonGalleryOnClickListener implements ImageButton.OnClickListener {

		@Override
		public void onClick(View v) {
			startPickingVideo();
		}

	}

	private void startTakingVideo() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempVideoFile()));
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 300);
		startActivityForResult(intent, IntentRequestCode.TAKE_VIDEO);
	}

	private void startPickingVideo() {
		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		try {
			startActivityForResult(intent, IntentRequestCode.PICK_UP_VIDEO);
		}
		catch (ActivityNotFoundException e) {
			 // On Andriod 2.2, the above method may cause exception due to not finding an activity to handle the intent. Use the method below instead.
			Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
			mediaChooser.setType("video/*");
			startActivityForResult(mediaChooser, IntentRequestCode.PICK_UP_VIDEO);
		}
		catch (SecurityException e) {
			// When picking up videos, there may be an exception like:
			//  java.lang.SecurityException:
			//      Permission Denial:
			//      starting Intent { act=android.intent.action.PICK
			//      dat=content://media/external/video/media
			//      cmp=com.android.music/.VideoBrowserActivity } from ProcessRecord
			// Try another way to start the intent
			intent = new Intent(Intent.ACTION_PICK, null);
			intent.setType("video/*");
			try {
				startActivityForResult(intent, IntentRequestCode.PICK_UP_VIDEO);
			} catch (Exception ex) {
				DialogUtil.showExceptionAlertDialog(UploadVideoActivity.this, getString(R.string.cannotPickUpVideo), getString(R.string.notSupportedOnDevice));
			}
		}
	}

	private void onVideoReady() {
		MediaController mediaController = new MediaController(this);
		videoViewPreview.setVisibility(View.VISIBLE);
		videoViewPreview.setVideoPath(videoFileName);
		videoViewPreview.setMediaController(mediaController);
		videoViewPreview.requestFocus();
		videoViewPreview.start();
		videoViewPreview.pause();

		imageButtonUploadVideo.setEnabled(true);
		imageButtonUploadVideo.setImageResource(R.drawable.upload);

		textViewFilePath.setText(videoFileName);

		editTextVideoTitle.setText("");
		editTextVideoDescription.setText("");
		textViewVideoUrl.setText(getString(R.string.noUrlYet));

		resetProgress();

		Toast.makeText(this, R.string.pressVideoToPreview, Toast.LENGTH_LONG).show();
	}

	// Disable or enable widgets upon video uploading or not-uploading
	public void toggleWidgetsEnabled(boolean enabled) {
		editTextVideoTitle.setEnabled(enabled);
		editTextVideoDescription.setEnabled(enabled);
		imageButtonTakeVideo.setEnabled(enabled);
		imageButtonGallery.setEnabled(enabled);
		imageButtonUploadVideo.setEnabled(enabled);
		videoViewPreview.setEnabled(enabled);
	}

	public void resetProgress() {
		progressBarUploadVideo.setProgress(0);
		textViewProgress.setText(" 00%");
	}

	public void preventUploadingSameVideo() {
		imageButtonUploadVideo.setEnabled(false);
		imageButtonUploadVideo.setImageResource(R.drawable.upload_disabled);
	}

	public TextView getTextViewProgress() {
		return textViewProgress;
	}

	public ProgressBar getProgressBarUploadVideo() {
		return progressBarUploadVideo;
	}

	public TextView getTextViewVideoUrl() {
		return textViewVideoUrl;
	}

	public String getVideoTitle() {
		return editTextVideoTitle.getText().toString();
	}

	public String getVideoDescription() {
		return editTextVideoDescription.getText().toString();
	}

	public void startGetRefreshTokenActivity() {
		ActivityUtil.goToActivity(this, GetRefreshTokenActivity.class);
	}

	public void uploadVideo() {
		YoukuVideoUploadTask youkuVideoUploadTask = new YoukuVideoUploadTask(this);
		youkuVideoUploadTask.execute(videoFileName, editTextVideoTitle.getText().toString(), editTextVideoDescription.getText().toString());
	}

	public void toggleUploadingFlag(boolean isUploading) {
		this.isUploading = isUploading;
	}

	public YoukuUploader getYoukuUploader() {
		return youkuUploader;
	}
}
