#!/bin/bash

sh run_script/build_clean.sh;
cd /Users/mariusm/Projects/ads/AdformSdkProject/

echo "*** Starting build script ***"
if [ "$1" == '-c' ]
then
    gradle --daemon clean assembleDebug;
else
    gradle --daemon assembleDebug;
fi

sh run_script/build_start.sh
sh run_script/build_complete.sh