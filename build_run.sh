#!/bin/bash
echo "*** Cleaning old apk's ***"
cd /Users/mariusm/Projects/ads/AdformSdk/AdformDemo/build/apk/
rm *;
cd /Users/mariusm/Projects/ads/AdformSdk/
start_time=$(date +%s)
echo "*** Starting build script ***"
gradle --daemon assembleDebug
sh build_start.sh
end_time=$(date +%s)
DIFF=$(( $end_time - $start_time ))
echo "Execution completed in $DIFF s."
# say "Execution completed in $DIFF seconds"
# Play complete sound
osascript -e "display notification \"Execution completed in $DIFF s.\" with title \"Execution complete\""
afplay ~/Downloads/fall_3.m4r