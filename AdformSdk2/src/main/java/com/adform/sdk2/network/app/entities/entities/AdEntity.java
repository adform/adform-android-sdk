package com.adform.sdk2.network.app.entities.entities;

import android.util.JsonReader;

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
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("trackingUrlBase")) {
                trackingUrlBase = reader.nextString();
            } else if (name.equals("refreshInterval")) {
                refreshInterval = reader.nextInt();
            } else if (name.equals("tagData")) {
                tagData = TagDataEntity.readEntity(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new AdEntity(trackingUrlBase, refreshInterval, tagData);
    }


}
