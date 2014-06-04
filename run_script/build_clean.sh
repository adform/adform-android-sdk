echo "*** Cleaning old apk's ***"
if [ -d /Users/mariusm/Projects/ads/AdformSdkProject/AdformDemo/build/apk/ ];
        then echo "Directory exist, cleaning it...";
        cd /Users/mariusm/Projects/ads/AdformSdkProject/AdformDemo/build/apk/
        rm *;
        else echo "Directory does not exist, proceeding...";
fi

