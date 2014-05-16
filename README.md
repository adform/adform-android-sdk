adform-android-sdk
==================
To be able to use AdformSDK, first your need to get your master tag. It can be obtained by registering to the Adform system.

How to add AdformSDK to your project
====================================
### General info
* AdformSDK runs on Android 4.0, so created project version should be 4.0 and above.
* Also the instructions described here are done on IntelliJ 13.1. These instructions should be compatible with Android Studio also.
* **For more detailed documentation, scroll below**!

### Project preparations for AdformSDK (Short) 

1. Download project library `AdformSDK.jar` latest version. Currently latest version is 0.2. 
2. Insert library into your project.
3. Update `build.gradle` file with
		
		dependencies {
		    compile 'com.google.android.gms:play-services:4.2.42'
    		compile fileTree(dir: 'libs', include: ['*.jar'])
		}
		
4. Update `AndroidManifest.xml` with snipped shown below between `<manifest></manifest>` tags.

		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
		
5. Update `AndroidManifest.xml` with snipped shown below between `<application></application>` tags.

	    <meta-data android:name="com.google.android.gms.version"
	               android:value="@integer/google_play_services_version" />

Thats it! If you had any problems, a more detailed implementation is described `below`.

### Project preparations for AdformSDK (Detailed)
These instructions are given assuming this is a new project.

1. Download project library `AdformSDK.jar` latest version. Currently latest version is 0.2. 
2. Insert library into your project. This can be done by copying the AdformSDK.jar file into `libs` directory.
3. Update `build.gradle` file, to load the library when compiling project and add additional needed library. 

If you are creating new project, this should be already done.

		dependencies {
		    compile 'com.google.android.gms:play-services:4.2.42'
    		compile fileTree(dir: 'libs', include: ['*.jar'])
		}

When something is edited in the build file, project should be refreshed for IDE to take effect. That can be done by pressing View -> Tool windows -> Gradle. In opened windows press "refresh" icon.

Note that, we are also adding "Google play" services for additional functionality that is needed in AdformSDK. "Google play" services version may varie depending on Android platform.

4. Also we need to set up some permissions that are needed for the AdformSDK to work. We need an internet connection, and an ability to check if internet avalaible. This is done by editing `AndroidManifest.xml` file and adding code snippet between the `<manifest></manifest>` tags.

		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
		
Furthermore, we need some additional data for the AdformSDK to work, that is inserted between `<application></application>` tags.

	    <meta-data android:name="com.google.android.gms.version"
	               android:value="@integer/google_play_services_version" />
	               
And that is it! Your library is ready to be used.	               
	
