#!/bin/bash
start_time=$(date +%s)
echo "*** Starting release script ***"
gradle --daemon -p /Users/mariusm/Projects/ads/AdformSdk/ clean assembleRelease
find /Users/mariusm/Projects/ads/AdformSdk/out -name "*unaligned*" -exec rm {} \;
cp /Users/mariusm/Projects/ads/AdformSdk/out/*.apk /Users/mariusm/Google\ Drive/Android\ Builds/
end_time=$(date +%s)
DIFF=$(( $end_time - $start_time ))
echo "Execution completed in $DIFF s."
osascript -e "display notification \"Execution completed in $DIFF s.\" with title \"Execution complete\""
afplay ~/Downloads/fall_3.m4r