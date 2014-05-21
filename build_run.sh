#!/bin/bash

start_time=$(date +%s)
echo "*** Starting build script ***"
gradle --daemon -p /Users/mariusm/Projects/ads/AdformSdk/ assembleDebug
echo "*** Starting Android install ***"
adb -s CB5A1VUBV2 uninstall com.adform.sample.app
adb -s CB5A1VUBV2 install /Users/mariusm/Projects/ads/AdformSdk/AdformDemo/build/apk/AdformDemo-debug-unaligned.apk
echo "*** Starting activity ***"
adb -s CB5A1VUBV2 shell am start -n "com.adform.sample.app/com.adform.sample.app.DemoActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER
end_time=$(date +%s)
DIFF=$(( $end_time - $start_time ))
echo "Execution completed in $DIFF s."
# say "Execution completed in $DIFF seconds"
# Play complete sound
osascript -e "display notification \"Execution completed in $DIFF s.\" with title \"Execution complete\""
afplay ~/Downloads/fall_3.m4r 