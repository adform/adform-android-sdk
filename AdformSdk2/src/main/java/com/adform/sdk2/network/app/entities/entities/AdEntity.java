package com.adform.sdk2.network.app.entities.entities;

import com.adform.sdk2.network.ito.network.NetworkResponse;
import com.adform.sdk2.network.ito.network.NetworkResponseParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;

/**
 * Created by mariusm on 23/04/14.
 */
public class AdEntity implements NetworkResponseParser<AdEntity> {
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

    @Override
    public NetworkResponse parse(String data, Class<AdEntity> clazz) throws IOException {
        final AdServingEntity response = new AdServingEntity();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(data);
            JSONObject jsonAdServing = (JSONObject)((JSONObject)obj).get("adServing");
            response.setAdEntity(new AdEntity());
            JSONObject jsonAdEntity = (JSONObject)jsonAdServing.get("ad");
            TagDataEntity tagDataEntity = new TagDataEntity();
            JSONObject jsonTagDataEntity = (JSONObject)jsonAdEntity.get("tagData");
            tagDataEntity.setSrc((String)jsonTagDataEntity.get("src"));
            response.getAdEntity().setTagDataEntity(tagDataEntity);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        NetworkResponse<AdServingEntity> networkResponse =
                new NetworkResponse<AdServingEntity>(response);
        return networkResponse;
    }
}
