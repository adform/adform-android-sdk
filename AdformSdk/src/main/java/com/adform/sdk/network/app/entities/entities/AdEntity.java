package com.adform.sdk.network.app.entities.entities;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;

/**
 * Created by mariusm on 23/04/14.
 */
public class AdEntity {
    private String trackingUrlBase;
    private int refreshInterval;
    private TagDataEntity tagDataEntity;

    public AdEntity(String trackingUrlBase, int refreshInterval, TagDataEntity tagDataEntity) {
        this.trackingUrlBase = trackingUrlBase;
        this.refreshInterval = refreshInterval;
        this.tagDataEntity = tagDataEntity;
    }

    public AdEntity() {
    }

    public AdEntity(TagDataEntity tagDataEntity) {
        this.tagDataEntity = tagDataEntity;
    }

    public String getTrackingUrlBase() {
        return trackingUrlBase;
    }

    public void setTrackingUrlBase(String trackingUrlBase) {
        this.trackingUrlBase = trackingUrlBase;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public TagDataEntity getTagDataEntity() {
        return tagDataEntity;
    }

    public void setTagDataEntity(TagDataEntity tagDataEntity) {
        this.tagDataEntity = tagDataEntity;
    }

    public static AdEntity readEntity(JsonReader reader) throws IOException  {
        String trackingUrlBase = null;
        int refreshInterval = 30;
        TagDataEntity tagData = null;
        if (reader.peek() == JsonToken.BEGIN_OBJECT) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (reader.peek() != JsonToken.NULL) {
                    if (name.equals("trackingUrlBase")) {
                        trackingUrlBase = reader.nextString();
                    } else if (name.equals("refreshInterval")) {
                        refreshInterval = reader.nextInt();
                    } else if (name.equals("tagData")) {
                        tagData = TagDataEntity.readEntity(reader);
                    } else {
                        reader.skipValue();
                    }
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } else
            return null;
        return new AdEntity(trackingUrlBase, refreshInterval, tagData);
    }


}
