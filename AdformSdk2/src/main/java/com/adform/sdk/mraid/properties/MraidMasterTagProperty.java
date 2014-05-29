package com.adform.sdk.mraid.properties;

/**
 * Created by mariusm on 08/05/14.
 * A property that holds master id that identifies which ad should be loaded.
 */
public class MraidMasterTagProperty extends MraidBaseProperty {
    private final int mMasterId;

    MraidMasterTagProperty(int masterTag) {
        mMasterId = masterTag;
    }

    public static MraidMasterTagProperty createWithMasterTag(int masterTag) {
        return new MraidMasterTagProperty(masterTag);
    }

    @Override
    public String toGet() {
        return "mid="+ mMasterId;
    }
    @Override
    public String toJson() {
        return "\"master_tag_id\":"+mMasterId;
    }
}
