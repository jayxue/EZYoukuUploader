# EZYoukuUploader - Android library for eazily uploading videos to Youku

An Android library that helps deverlopers easily create apps with the functionality of uploading videos to Youku.

![Demo Screenshot 1](https://github.com/jayxue/EZYoukuUploader/blob/master/EZYoukuUploaderSDK/src/main/res/raw/screenshot_1.png)
![Demo Screenshot 2](https://github.com/jayxue/EZYoukuUploader/blob/master/EZYoukuUploaderSDK/src/main/res/raw/screenshot_2.png)
![Demo Screenshot 3](https://github.com/jayxue/EZYoukuUploader/blob/master/EZYoukuUploaderSDK/src/main/res/raw/screenshot_3.png)
![Demo Screenshot 3](https://github.com/jayxue/EZYoukuUploader/blob/master/EZYoukuUploaderSDK/src/main/res/raw/screenshot_4.png)

Details
-------
Youku (www.youku.com) is the largest video hosting service founded in China, the same famous as YouTube to the world.

This Android library facilitates developers to create Android applications with the functionality of uploading videos to Youku.

The major features include:
* Shoot new videos or pick videos from gallery for uploading.
* Enter title and description for a video to upload.
* Ask user to confirm before starting uploading.
* Ask user to login to Youku and authorize user with OAuth2.
* Show progress of uploading.
* Display video URL after uploading is successfully completed.

Usage
-----

In order to utilize this library, you just need to do some configurations without writing any code.

* Import the EZYoukuUploaderSDK module into your Android Studio project. Add dependency to the module to your app project.
* In your Android app's ```AndroidManifest.xml```, make sure that you have the following permissions:
  * ```android.permission.INTERNET```
  * ```android.permission.WRITE_EXTERNAL_STORAGE```
  * ```android.permission.CAMERA```
* In your app's ```AndroidManifest.xml```, include the activities:
  * ```com.wms.ezyoukuuploader.sdk.activity.UploadVideoActivity```
  * ```com.wms.ezyoukuuploader.sdk.activity.GetRefreshTokenActivity```
* In your app's ```res/values/strings.xml```,
  * Set ```app_name``` (name of your application).
* Replace your app's ic_launcher icons.
* In order to upload videos to Youku, you need to register as a Youku developer and create an app on Youku Apps Console (http://cloud.youku.com/app). After you configure the Youku app, put the following information in your Android app's ```values/strings.xml```:
  * ```YOUKU_APP_CLIENT_ID```
  * ```YOUKU_APP_CLIENT_SECRET```
  * ```YOUKU_APP_REDIRECT_URI```
 
Of course you can modify any components of the library or add new components to customize your Android app's functionality.

Acknowledgement
---------------

This library utilizes the following libraries:
* Youku Andorid SDK: http://cloud.youku.com/down/index

Developer
---------
* Jay Xue <yxue24@gmail.com>, Waterloo Mobile Studio

License
-------

    Copyright 2016 Waterloo Mobile Studio

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.