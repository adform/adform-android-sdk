#!/bin/bash
sh run_script/build_clean.sh;
cd /Users/mariusm/Projects/ads/AdformSdkProject/


echo "*** Starting release script ***";
gradle clean assembleRelease;

sh run_script/build_start.sh

# Copy script to gdocs
echo "Copy release version? ";
yes="y";
read -s isCopy;
if [ "$isCopy" == "$yes" ]
then
    echo "*** Starting copy release version to GDocs ***";
    find /Users/mariusm/Projects/ads/AdformSdkProject/out -name "*unaligned*" -exec rm {} \;
    cp /Users/mariusm/Projects/ads/AdformSdkProject/out/*.apk /Users/mariusm/Google\ Drive/Android\ Builds/;
else
    echo "*** Skipping copy ***";
fi
sh run_script/build_complete.sh