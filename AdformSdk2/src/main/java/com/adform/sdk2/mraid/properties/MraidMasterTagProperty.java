package com.adform.sdk2.mraid.properties;

/**
 * Created by mariusm on 08/05/14.
 */
public class MraidMasterTagProperty extends MraidBaseProperty {
    private final String mAdvertisingId;

    MraidMasterTagProperty(String masterTag) {
        mAdvertisingId = masterTag;
    }

    public static MraidMasterTagProperty createWithMasterTag(String masterTag) {
        return new MraidMasterTagProperty(masterTag);
    }

    @Override
    public String toGetPair() {
        return "mid="+ mAdvertisingId;
    }
}
