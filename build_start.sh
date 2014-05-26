#!/bin/bash
#current_device='CB5A1VUBV2'
cd /Users/mariusm/Projects/ads/AdformSdk/AdformDemo/build/apk/
current_device=$(adb devices | grep -w device | awk '{print $1}')
echo 'Found device: '$current_device
echo "*** Starting Android install ***"
adb -s $current_device uninstall com.adform.sample.app
lookingFor="AdformDemo-release.apk"
foundReleaseApk=$(find . -name "$lookingFor")
echo $foundReleaseApk
if [ "$foundReleaseApk" == "./$lookingFor" ]
then
    echo "*** Installing release version ***"
    adb -s $current_device install AdformDemo-release.apk
else
    echo "*** Installing debug version ***"
    adb logcat -c
    adb -s $current_device install AdformDemo-debug-unaligned.apk
fi
echo "*** Starting activity ***"
adb -s $current_device shell am start -n "com.adform.sample.app/com.adform.sample.app.DemoActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

