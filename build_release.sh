#!/bin/bash
echo "*** Cleaning old apk's ***"
cd /Users/mariusm/Projects/ads/AdformSdkProject/AdformDemo/build/apk/
rm *;
cd /Users/mariusm/Projects/ads/AdformSdkProject/
yes="y";
start_time=$(date +%s);
echo "*** Starting release script ***";
gradle --daemon clean assembleRelease;
sh build_start.sh
echo "Copy release version? ";
read -s isCopy;
if [ "$isCopy" == "$yes" ]
then
    echo "*** Starting copy release version to GDocs ***";
    find /Users/mariusm/Projects/ads/AdformSdkProject/out -name "*unaligned*" -exec rm {} \;
    cp /Users/mariusm/Projects/ads/AdformSdkProject/out/*.apk /Users/mariusm/Google\ Drive/Android\ Builds/;
else
    echo "*** Skipping copy ***";
fi
end_time=$(date +%s);
DIFF=$(( $end_time - $start_time ))
echo "Execution completed in $DIFF s."
osascript -e "display notification \"Execution completed in $DIFF s.\" with title \"Execution complete\""
afplay ~/Downloads/fall_3.m4r