##  Adform Advertising Android SDK

Adform brings brand advertising to the programmatic era at scale, making display advertising simple, relevant and rewarding!

### [Getting Started](https://github.com/adform/adform-android-sdk/wiki/Getting-Started)

### **Basic integrations**

* [Integrating Inline Ad](https://github.com/adform/adform-android-sdk/wiki/Integrating-Inline-Ad)
* [Integrating Full Screen Overlay Ad](https://github.com/adform/adform-android-sdk/wiki/Integrating-Full-Screen-Overlay-Ad)
* [Integrating Interstitial Ad](https://github.com/adform/adform-android-sdk/wiki/Integrating-Interstitial-Ad)
* [Integrating Adhesion Ad](https://github.com/adform/adform-android-sdk/wiki/Integrating-Adhesion-Ad)
* [Video Ad Integration](https://github.com/adform/adform-android-sdk/wiki/Video-Ad-Integration)

### **Advanced integrations**

* [Advanced integration of Inline Ad](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-Inline-Ad)
* [Advanced integration of Full Screen Overlay Ad](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-Full-Screen-Overlay-Ad)
* [Advanced integration of Interstitial Ad](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-Interstitial-Ad)
* [Advanced integration of Adhesion Ad](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-Adhesion-Ad)
* [Advanced integration of AdInline ListView](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-AdInline-ListView)
* [Instream video ads integration](https://github.com/adform/adform-android-sdk/wiki/Instream-video-ads-implementation)

### **Other**

* [Adding Custom Values](https://github.com/adform/adform-android-sdk/wiki/Adding-Custom-Values)
* [Adding Keywords](https://github.com/adform/adform-android-sdk/wiki/Adding-keywords)
* [Adding Key Value Pairs](https://github.com/adform/adform-android-sdk/wiki/Adding-key-value-pairs)
* [Adding Search Words](https://github.com/adform/adform-android-sdk/wiki/Adding-Search-Words)
* [Location](https://github.com/adform/adform-android-sdk/wiki/Location)
* [Security](https://github.com/adform/adform-android-sdk/wiki/Security)
* [Ad Tags](https://github.com/adform/adform-android-sdk/wiki/Ad-Tags)
* [Header Bidding](https://github.com/adform/adform-android-sdk/wiki/Header-Bidding)
* [Specifying banner loading behaviour](https://github.com/adform/adform-android-sdk/wiki/Specifying-banner-loading-behaviour)
* [GDPR](https://github.com/adform/adform-android-sdk/wiki/GDPR)
* [Logs](https://github.com/adform/adform-android-sdk/wiki/Enable-Disable-debug-logs)

### **Mediations adapters**

* [AdMob](https://github.com/adform/adform-android-sdk/wiki/Advertising-SDK-adapter-for-AdMob-mediation)
* [DFP](https://github.com/adform/adform-android-sdk/wiki/Advertising-SDK-adapter-for-DFP-mediation)

### **Plugins**

* [Unity](https://github.com/adform/adform-advertising-sdk-unity-plugin)

# Release Notes

This part lists release notes from all versions of Adform Mobile Advertising Android SDK.

## 2.19.2
Removed device identifiers sending.

## 2.19.1
Fixed bug when READ_PHONE_STATE state permission wasn't checked for Android 11.

## 2.19.0
Added logs enabling/disabling

## 2.18.0
Added support for custom ad size in interstitial and overlay ads.

## 2.17.1
* Fixed Smart Ad Size issue, when changing device orientation

## 2.17.0
* Added Header Bidding SDK as dependency in Adform SDK maven configuration. 

## 2.16.0
* Added US policy(CCPA) support.

## 2.15.0
* IAB Transparent Consent Framework 2.0 support.

## 2.14.0
* Migrated to AndroidX.
* Fixed AdInline restore when navigating back to fragment. 

## 2.13.7
* Added User-Agent header to impression request.

## 2.13.6
* Added possibility to remove key value.

## 2.13.5
* "Skip" button showing fix in video ad. 

## 2.13.4
* VAST 3.0 skip compatability.

## 2.13.3
* Video ad resume fix.
* Added onAdClose(Adinline) callback for video ad.

## 2.13.2
* Webview crash fix

Since version 69 Chrome changed the way it handles paths to external resources. This causes Chrome WebView to crash in case external fonts are loaded.

This release fixes the way we handle external resource paths therefore is recommended to all users of the Adform Android SDK.

## 2.13.1
* Minor changes for Prebid integration.

## 2.13.0
* Added GDPR support. More info in [GDPR](https://github.com/adform/adform-android-sdk/wiki/GDPR)

## 2.12.0

* Added ability to specify banner loading behaviour. More info in [Specifying banner loading behaviour](https://github.com/adform/adform-android-sdk/wiki/Specifying-banner-loading-behaviour)

## 2.11.0

* Added possibility to set width and height for SmartAdSize. More info in [Advanced integration of Inline Ad](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-Inline-Ad)

## 2.10.0

* Added possibility to open ad in a internal browser.

## 2.9.1

* Fixed AdInline NPE

## 2.9.0

* It is now possible to specify which ADX servers SDK should use by setting ADX domain. Check out documentation on how to do it [here](https://github.com/adform/adform-android-sdk/wiki/Changing-ADX-Domain).

## 2.8.2

* Fixed adinterstitial load failed event.

## 2.8.1

* Fixed issue with ad interstitial key-values sending.

## 2.8.0

* Added Search Words support.

## 2.7.5

* Fixed position controller NPE in inline ad.

## 2.7.4

* Fixed multimap serialization bug. 

## 2.7.3

* It is now possible to define multiple key value pairs with the same key for all banners.

## 2.7.2

* Ad container size fix bug fix. 

## 2.7.1

* View port update script bug fix.

## 2.7.0

* Added video ads support in AdOverlay type.
* Minor bug fixes.

## 2.6.0

* Added In-Stream ads support.
* Minor bug fixes.

## 2.5.0

* Included Adform Header Bidding SDK v.1.0.
* Added Adform recycler view adapter for displaying content with adinline banners.
* Added additional 'price' and 'customData' parameters to ad views for header bidding support.
* Minor bug fixes.

## 2.4.5

* Fixed issue when webview crashes after destroy on devices without chromium.

## 2.4.4

* Added helper classes for Unity plugin.

## 2.4.3

* Added an option to AdHesion ads to enable close button. By default it is disabled.
* Added autohide feature to AdHesion ads. If enabled, AdHesion ad will hide when user interacts wit the application and reveal itself when the interaction ends.

## 2.4.2

* Fixed banner loading in webview bug.

## 2.4.1

* Fixed webview crash when trying to load URL using destroyed webview.

## 2.4.0

* Smart ad size feature - now ad views can dynamically adapt to multiple screen sizes when used with smart ad size. For more details [check here](https://github.com/adform/adform-android-sdk/wiki/Advanced-integration-of-Inline-Ad#smart-ad-size).
* Ad tag support - now ad views can load html or url ad tags provided by developer. For more details [check here](https://github.com/adform/adform-android-sdk/wiki/Ad-Tags).
* MRAID viewable percentage support - now MRAID banners may listen for viewablePercentageChange event or use getViewablePercentage() method to know how much of the creative is viewable.  
* Minnor bug fixes

## 2.3.2

* Fixed AdOverlay display bug for devices with no navigation bar. 

## 2.3.1

* Fixed issue with X509TrustManager implementation.
* Mraid resize fixes.

## 2.3.0

* Multiple sizes feature.
* Mraid resize feature.
* Added HTTPS support.
* Minor fixes.

## 2.2.5

* Fixed issues which occurred on activity restore

## 2.2.4

* Compliance with Android 6.0

## 2.2.3

* Minor stability fix for displaying content and ads in view pager
* Added additional event reporting when displaying ads with the view pager

## 2.2.2

* Fixed a bug with touch event passing to webview when in viewpager when using AdInterstitial
 
## 2.2.1

* Added an ability for the AdInterstitial pager container to hold WebViews dynamically

## 2.2

* Video Ads Support (VAST compatible)
* Targeting by Keywords and Key Values added

## 2.0.4.1

* Minor update for additional handling in third party banners.

## 2.0.4

* Added more variants in banner loading.

## 2.0.3

* Fixed a bug with geo location tracking.

## 2.0.2

* Minor bug fixes for ListView implementation.

## 2.0.1

* Stability fixes

## 2.0.0

* New formats introduced: Adhesion, Overlay and Interstitial
* Open RTB protocol support;
* Performance improvements;

## 1.0

Library is capable of expanding one/two way ads. 

## 0.2.2

* Added an ability to display interstitial ad without animation, inner browser changed to external.

## 0.2.1

* Minor fixes

## 0.2

* Interstitial ad

## 0.1.2

* Refresh Rate override option added;
* onAdLoadFail listener added;

## 0.1.1

* Bug Fixes
* NewRelic library made as optional;

## 0.1.0

* first release;



