## General info
* AdformSDK runs on Android 4.0, so created project version should be 4.0 and above.
* Also the instructions described here are done on IntelliJ 13.1. These instructions should be compatible with Android Studio also.

## Project preparations for AdformSDK (Short) 

In this example project will be called `AdformExportDemo`.

1. Download project library `AdformSdk-0.1.2.jar` latest version. 
2. Insert library into your project.
3. Project has 2 `build.gradle` files (One for top project and one for project module). Update module `build.gradle` file by inserting `Google Play` services, `New Relic` library, and `SDK`. 
Please be noted, that `New Relic` library is optional and should be imported depending on user preference.

Everything should look something like this:
		
        buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:0.9.+'
                classpath 'com.newrelic.agent.android:agent-gradle-plugin:3.+'
            }
        }
        apply plugin: 'android'
        apply plugin: 'newrelic'

        repositories {
            mavenCentral()
        }

        android {
            compileSdkVersion 19
            buildToolsVersion "19.0.2"

            defaultConfig {
                minSdkVersion 14
                targetSdkVersion 19
                versionCode 1
                versionName "1.0"
            }
            buildTypes {
                release {
                    runProguard false
                    proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
                }
            }
        }

        dependencies {
            compile 'com.google.android.gms:play-services:5.0.77'
            compile 'com.newrelic.agent.android:android-agent:3.+'
            compile fileTree(dir: 'libs', include: ['*.jar'])
        }

Top project `build.gradle` file should be the way it is, and it should look like this: 

        buildscript {
            repositories {
                mavenCentral()
            }
            dependencies {
                classpath 'com.android.tools.build:gradle:0.9.+'
            }
        }

        allprojects {
            repositories {
                mavenCentral()
            }
        }
 
		
4. Update `AndroidManifest.xml` with snippet shown below between `<manifest></manifest>` tags.

		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
		
5. Update `AndroidManifest.xml` with snipped shown below between `<application></application>` tags.

	    <meta-data android:name="com.google.android.gms.version"
	               android:value="@integer/google_play_services_version" />
		<activity
                android:theme="@android:style/Theme.Translucent.NoTitleBar"
                android:name="com.adform.sdk.activities.AdActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
		<activity
                android:theme="@android:style/Theme.Holo.Light"
                android:name="com.adform.sdk.activities.AdBrowser" android:configChanges="keyboardHidden|orientation|screenSize"/>

Thats it!

## Basic AdformSDK Banner View implementation
To add an ad view, simply insert a view with a path `com.adform.sdk.view.CoreAdView`. This can be done like this:

	<com.adform.sdk.view.CoreAdView
			mastertag_id="111111"
			publisher_id="222222"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content" />

* Note that, when initializing a view, `mastertag_id` and `publisher_id`can be provided by setting through view parameters. Also this can be set by setting  parameters in the programming code like in snippet below.

        mAdView.setMasterTagId(111111);
        mAdView.setPublisherId(222222);


* Gravity can be changed by inserting the adView into the container and changing its position.

When initializing SDK in Fragment/Activity, a **destruction event should be provided** for the view. This can be done by doing these steps: 
	
1. Get created view instance.

		mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);

2. Destroy its instance with onDestroy() method, that every Activity/Fragmet has. 

        @Override
        public void onDestroy() {
            if (mAdView != null)
                mAdView.destroy();
            super.onDestroy();
        }

More advanced implementation is discussed below (like ListView etc.)

## Advanced AdformSDK View implementation 

At the moment AdformSDK does not save its instance on screen rotation, so when device is rotated, its instance is created anew. 

### Adding custom values to AdformSDK

To add custom additional values, first View must be found.

		CoreAdView mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);

Later on, just add wanted values.

        // Use builder to set custom parameters...
        CoreAdView.setCustomParams(CustomParamBuilder.startCreating()
                        .addCustomParam("gender", "female")
                        .addCustomParam("age", "23")
                        .buildParams()
        );

        // ...or use variable to store custom params.
        HashMap<String, String> customParams = CustomParamBuilder.startCreating()
                .addCustomParam("gender", "female")
                .addCustomParam("age", "23")
                .buildParams();
        CoreAdView.setCustomParams(customParams);

These values also can be cleared by using snippet below.

        CoreAdView.clearCustomParams();

### Adding custom refresh rate

There is a possibility to add a custom refresh rate. It overrides the one that is received from the server. To achieve this, refresh rate can be set in layout xml like this...

		<com.adform.sdk.view.CoreAdView
			refresh_seconds="35"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content" />

... or it can be set from source code

        mAdView.setRefreshSeconds(35);

Also refresh rate has some features and restrictions in using it:

1. When refresh rate is set to 0, ad refreshing is disabled.
2. Refresh rate can't be set lower than 30 seconds.

### Adding basic event listeners

To add an event listener to a class, first the class must declare an `AdListener` interface...

		public class DemoFragment1 extends Fragment implements AdListener ...

...implement its interface...

		@Override
    	public void onAdLoadSuccess() {
			// Load succeeded event
    	}

    	@Override
    	public void onAdLoadFail(String failError) {
        	// Load failed event
    	}

...and bind the Ad view as the interface listener

		mAdView.setListener(this);

### Adding additional event listeners

There are more events that can be received when implementing `AdStateListener` interface.
At the moment, only `onAdVisibilityChange` event can be captured, but in the future there will be additional events:

* onAdStateChange
* onAdSizeChange
* onAdPlacementChange
* onCurrentPositionChange

Implementing `AdStateListener` is simmilar to implementing basic listeners. We declare that class answers to `AdStateListener` interface...

	public class DemoFragment1 extends Fragment implements AdStateListener...

...implements its events...

    @Override
    public void onAdVisibilityChange(boolean isVisible) {
        // Ad view visibility changed event
    }

...and lastly bind the Ad view to the interface listener

	mAdView.setStateListener(this);

