#!/bin/bash

sh run_script/build_clean.sh;
cd /Users/mariusm/Projects/ads/AdformSdkProject/

echo "*** Starting build script ***"
gradle assembleDebug;

sh run_script/build_start.sh
sh run_script/build_complete.sh