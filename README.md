adform-android-sdk
==================
To be able to use AdformSDK, first your need to get your master tag. It can be obtained by registering to the Adform system.

How to add AdformSDK to your project
====================================
## General info
* AdformSDK runs on Android 4.0, so created project version should be 4.0 and above.
* Also the instructions described here are done on IntelliJ 13.1. These instructions should be compatible with Android Studio also.

## Project preparations for AdformSDK (Short) 

In this example project will be called `AdformExportDemo`.

1. Download project library `AdformSdk_0.4.14.jar` latest version. 
2. Insert library into your project.
3. Project has 2 `build.gradle` files (One for top project and one for project module). Update module `build.gradle` file by inserting `Google Play` services, `New Relic` library, and `SDK`. Everything should look something like this:
		
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
            compile 'com.google.android.gms:play-services:4.2.42'
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
                android:name="com.adform.sdk.activities.AdformInterstitialActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

Thats it!

## Basic AdformSDK Banner View implementation
To add an ad view, simply insert a view with a path `com.adform.sdk.view.CoreAdView`. This can be done like this:

	<com.adform.sdk.view.CoreAdView
			master_id="111111"
			publisher_id="222222"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content" />

* Note that, when initializing a view, `master_id` and `publisher_id`can be provided by setting through view parameters. Also this can be set by setting  parameters in the programming code like in snippet below.

        mAdView.setMasterId(111111);
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
        mAdView.setCustomParams(new CustomParamBuilder()
                        .addCustomParam("gender", "female")
                        .addCustomParam("age", "23")
                        .buildParams()
        );
        mAdView.clearCustomParams();

        // ...or use variable to store custom params.
        HashMap<String, String> customParams = new CustomParamBuilder()
                .addCustomParam("gender", "female")
                .addCustomParam("age", "23")
                .buildParams();
        mAdView.setCustomParams(customParams);

These values also can be cleared by using snippet below.

        mAdView.clearCustomParams();
        
### Expandable view

Library is capable of expanding one/two way ads. The implementation is the same, as using a CoreAdView. Executing a mraid function expand with its setting, a view is expanded in the front. 

### ListView implementation

For more complicated implementation like ListView, its adapter should *always* reuse views as every new AdView implementation starts its inner refresh service. 

	public class TestAdapter3 extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;
    
        public TestAdapter3(Context context, ArrayList<String> values) {
            super(context, R.layout.lw_layout_1_3, values);
            this.context = context;
            this.values = values;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = convertView;
    
            // Determines which type of view to use.
            if (getItemViewType(position) == 1) {
    
                // In this case we use AdView. If no instance is returned from scrap list, we initialize a new one
                if ((rowView != null && !(rowView.getTag() instanceof ViewAdHolder)) || rowView == null) {
                    rowView = inflater.inflate(R.layout.lw_layout_2_3, parent, false);
                    ViewAdHolder viewHolder = new ViewAdHolder();
                    rowView.setTag(viewHolder);
                }
                ViewAdHolder holder = (ViewAdHolder) rowView.getTag();
            } else {
    
                // We initialize a simple view if no instance is returned from the scrap list.
                if ((rowView != null && !(rowView.getTag() instanceof ViewTextHolder)) || rowView == null) {
                    rowView = inflater.inflate(R.layout.lw_layout_1_3, parent, false);
                    ViewTextHolder viewHolder = new ViewTextHolder();
                    viewHolder.text = (TextView) rowView.findViewById(R.id.text_view);
    
                    // We set a view holder as a tag for later reuse
                    rowView.setTag(viewHolder);
                }
                
                // Initialized anew or returned from scrap list we assign needed values.
                ViewTextHolder holder = (ViewTextHolder) rowView.getTag();
                holder.text.setText(values.get(position));
            }
    
            return rowView;
        }
    
        // Determines the case that defines which type of view should be used
        // In this case when fifth item is displayed, adapter shows Ad, otherwise it shows simpleview
        @Override
        public int getItemViewType(int position) {
            if (position == 5)
                return 1;
            return 0;
        }
    
        // Determines different type of views to use
        // In this case we use 2 types, one for ad and one for simple view
        @Override
        public int getViewTypeCount() {
            return 2;
        }
    
        // Instance holder that is used when reusing the *same* view but assigning different values
        static class ViewTextHolder {
            public TextView text;
        }
    
        static class ViewAdHolder {}
    }
    
Also, it is recommended to clean/destroy view when fragment/activity is no longer needed.

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null)
            mAdapter.clear();
        if (mListView != null)
            mListView.setAdapter(null);
    }

### Interstitial implementation

In the example below, interstitial ad implementation will be showed. This example contains a simple network loading manager and a bit of additional code, to represent how ad preloading should be implemented. 

The interstitial ad has two types, preload info before showing and show info instantly, so there are two buttons (one loads info, another one displays it). 

* If info is loaded from the network, show button uses it to display it. 
* If info was not loaded, show button loads it automatically and shows it.

        public class DemoFragment5 extends Fragment implements View.OnClickListener,
                AdformContentLoadManager.ContentLoaderListener {
        
            // A variable that contains a key for persistent loaded ad saving
            public static final String CONTENT_LOADER_INFO = "CONTENT_LOADER_INFO";
            // A flag that handles when the ad should be shown
            private boolean showAfterLoad = false;
            // Manager that handles network loading tasks
            private AdformContentLoadManager mAdformContentLoadManager;
            // Dummy view is used to collect all the paramters that are needed for the request
            private DummyView mDummyView;
        
            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }
        
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                // We just initialize view for it to collect all the required info
                mDummyView = new DummyView(getActivity());
                // Adding custom data
                mDummyView.setMasterId(222222);
                mDummyView.setPublisherId(666666);
        
                // Initializing loading manager
                mAdformContentLoadManager = new AdformContentLoadManager();
            }
        
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.activity_main4, null);
                // Initializing buttons, that provide loading and showing functionality
                View loadButton = view.findViewById(R.id.load_button);
                if (loadButton != null)
                    loadButton.setOnClickListener(this);
                View showButton = view.findViewById(R.id.show_button);
                if (showButton != null)
                    showButton.setOnClickListener(this);
                return view;
            }
        
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.load_button: {
                        // Loading information from the network with the provided link
                        if (!mAdformContentLoadManager.isLoading())
                            loadContract();
                        break;
                    }
                    case R.id.show_button: {
                        // Showing information that was loaded
                        showAfterLoad = true;
                        if (mAdformContentLoadManager.getLastRawResponse() == null)
                            loadContract();
                        else {
                            showAfterLoad = false;
                            // Opening interstitial window with content response
                            // and its impression url
                            AdformInterstitialActivity.startActivity(getActivity(),
                                    mAdformContentLoadManager.getLastRawResponse(),
                                    mAdformContentLoadManager.getLastAdServingResponse()
                                            .getAdEntity().getTagDataEntity().getImpressionUrl()
                            );
                        }
                        break;
                    }
                }
            }
        
            private void loadContract() {
                if (!mAdformContentLoadManager.isLoading()) {
                    try {
                        // First we need to load a contract for the ad
                        // We use additional parameters from the dummy view.
                        mAdformContentLoadManager.loadContent(
                                mAdformContentLoadManager.getContractTask(
                                        mDummyView.getUrlProperties(),
                                        mDummyView.getRequestProperties())
                        );
                        mAdformContentLoadManager.setListener(new AdformContentLoadManager.ContentLoaderListener() {
                            @Override
                            public void onContentMraidLoadSuccessful(String content) {
                                try {
                                    // After successfully loading contract, we load the real ad content
                                    mAdformContentLoadManager.loadContent(mAdformContentLoadManager.getRawGetTask(
                                            content, true
                                    ));
                                    mAdformContentLoadManager.setListener(DemoFragment5.this);
                                } catch (AdformContentLoadManager.ContentLoadException e) {
                                    e.printStackTrace();
                                }
                            }
        
                            @Override
                            public void onContentLoadFailed() {}
                        });
                    } catch (AdformContentLoadManager.ContentLoadException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        
            // Response callback when loaded mraid type of content
            @Override
            public void onContentMraidLoadSuccessful(String content) {
                showToast("Loaded MRaid ad");
                if (showAfterLoad) {
                    showAfterLoad = false;
                    AdformInterstitialActivity.startActivity(getActivity(),
                            mAdformContentLoadManager.getLastRawResponse(),
                            mAdformContentLoadManager.getLastAdServingResponse()
                                    .getAdEntity().getTagDataEntity().getImpressionUrl());
                }
            }
        
            // Callback when something went wrong, and couldn't load content
            @Override
            public void onContentLoadFailed() {
                showToast("Error loading interstitial ad");
            }
        
            private void showToast(String message) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        
        }