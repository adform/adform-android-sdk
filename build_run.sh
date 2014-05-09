#!/bin/bash

echo "*** Starting build script ***"
gradle --daemon assembleDebug
echo "*** Starting Android install ***"
adb -s 192.168.56.101:5555 uninstall com.adform.sample.app
adb -s 192.168.56.101:5555 install AdformDemo/build/apk/AdformDemo-debug-unaligned.apk
echo "*** Starting activity ***"
adb -s 192.168.56.101:5555 shell am start -n "com.adform.sample.app/com.adform.sample.app.MainActivity2" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER