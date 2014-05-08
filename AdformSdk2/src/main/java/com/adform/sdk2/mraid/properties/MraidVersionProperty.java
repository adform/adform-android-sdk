package com.adform.sdk2.mraid.properties;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds API version
 */
public class MraidVersionProperty extends MraidBaseProperty {
    private final String mVersion;

    MraidVersionProperty(String version) {
        mVersion = version;
    }

    public static MraidVersionProperty createWithVersion(String version) {
        return new MraidVersionProperty(version);
    }

    @Override
    public String toGetPair() {
        return "ver="+ mVersion;
    }
}
