adform-android-sdk
==================
To be able to use AdformSDK, first your need to get your master tag. It can be obtained by registering to the Adform system.

How to add AdformSDK to your project
====================================
## General info
* AdformSDK runs on Android 4.0, so created project version should be 4.0 and above.
* Also the instructions described here are done on IntelliJ 13.1. These instructions should be compatible with Android Studio also.
* **For more detailed documentation, scroll below**!

## Project preparations for AdformSDK (Short) 

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

Thats it! If you had any problems, a more detailed implementation is described **below**.

## Basic AdformSDK View implementation
To add an ad view, simply insert a view with a path `com.adform.sdk2.view.CoreAdView`. This can be done like this:

	<com.adform.sdk2.view.CoreAdView
			master_id="1234"
			android:layout_width="wrap_content"
			android:layout_height="wrap_content" />

* Note that, when initializing a view master tag is set by providing it to the view parameters.
* Rignt now, this view does not respond to width and height, and it is set by default vales (320x50 for phones, 728x90 for tablets). 
* Gravity can be changed by inserting the adView into the container and changing its position.


## Advanced AdformSDK View implementation 

There should not be any problems by using the AdformSDK in any kind of context that saves and returns instance (simple activity, fragment) or the one that does not destroy its instance (like fragment with retainInstance true). 

### Adding custom values to AdformSDK

To add custom additional values, first View must be found.

		CoreAdView mAdView = (CoreAdView) view.findViewById(R.id.custom_ad_view);

Later on, just add wanted values.

        mAdView.addCustomParam("customTestId", "1234567890");
        mAdView.addCustomParam("customTestId2", "1234567890");
        mAdView.addCustomParam("customTestId3", "1234567890");
        mAdView.addCustomParam("customTestId4", "1234567890");

These values also can be cleared by using snippet below.

        mAdView.clearCustomParams();

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



## Project preparations for AdformSDK (Detailed)
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
	
