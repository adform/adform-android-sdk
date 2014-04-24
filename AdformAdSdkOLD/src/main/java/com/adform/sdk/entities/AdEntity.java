package com.adform.sdk.entities;

/**
 * Created by mariusm on 23/04/14.
 */
public class AdEntity {
    private String trackingUrlBase;
    private String assetsUrlBase;
    private int refreshInterval;
    private TagDataEntity tagDataEntity;
    private MetaEntity metaEntity;

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

    public String getAssetsUrlBase() {
        return assetsUrlBase;
    }

    public void setAssetsUrlBase(String assetsUrlBase) {
        this.assetsUrlBase = assetsUrlBase;
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

    public MetaEntity getMetaEntity() {
        return metaEntity;
    }

    public void setMetaEntity(MetaEntity metaEntity) {
        this.metaEntity = metaEntity;
    }
}
