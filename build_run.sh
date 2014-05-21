#!/bin/bash
#current_device='CB5A1VUBV2'
current_device=$(adb devices | grep -w device | awk '{print $1}')
echo 'Found device: '$current_device
start_time=$(date +%s)
echo "*** Starting build script ***"
gradle --daemon -p /Users/mariusm/Projects/ads/AdformSdk/ assembleDebug
echo "*** Starting Android install ***"
adb -s $current_device uninstall com.adform.sample.app
adb -s $current_device install /Users/mariusm/Projects/ads/AdformSdk/AdformDemo/build/apk/AdformDemo-debug-unaligned.apk
echo "*** Starting activity ***"
adb -s $current_device shell am start -n "com.adform.sample.app/com.adform.sample.app.DemoActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
end_time=$(date +%s)
DIFF=$(( $end_time - $start_time ))
echo "Execution completed in $DIFF s."
# say "Execution completed in $DIFF seconds"
# Play complete sound
osascript -e "display notification \"Execution completed in $DIFF s.\" with title \"Execution complete\""
afplay ~/Downloads/fall_3.m4r