package com.adform.sdk2.network.app.entities.entities;

import com.adform.sdk2.network.base.ito.network.NetworkResponse;
import com.adform.sdk2.network.base.ito.network.NetworkResponseParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;

public class AdServingEntity {

	private static final long serialVersionUID = 3271938798582141269L;

    private String version;
    private AdEntity adEntity;

    public AdServingEntity() {}

    public AdServingEntity(String version, AdEntity adEntity) {
        this.version = version;
        this.adEntity = adEntity;
    }

    public AdEntity getAdEntity() {
        return adEntity;
    }

    public void setAdEntity(AdEntity adEntity) {
        this.adEntity = adEntity;
    }

    public static NetworkResponseParser<AdServingEntity> responseParser = new NetworkResponseParser<AdServingEntity>() {
        @Override
        public NetworkResponse parse(String data, Class<AdServingEntity> clazz) throws IOException {
            final AdServingEntity response = new AdServingEntity();
            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(data);

                // Parsing AdServing
                JSONObject jsonAdServing = (JSONObject)((JSONObject)obj).get("adServing");

                // Parsing Ad
                JSONObject jsonAdEntity = (JSONObject)jsonAdServing.get("ad");
                AdEntity adEntity = new AdEntity();
                Object refreshInterval = jsonAdEntity.get("refreshInterval");
                if (refreshInterval != null)
                    adEntity.setRefreshInterval(Integer.parseInt(refreshInterval.toString()));
                response.setAdEntity(adEntity);

                // Parsing TagData
                JSONObject jsonTagDataEntity = (JSONObject)jsonAdEntity.get("tagData");
                TagDataEntity tagDataEntity = new TagDataEntity();
                tagDataEntity.setSrc((String)jsonTagDataEntity.get("src"));
                adEntity.setTagDataEntity(tagDataEntity);

                // Parsing meta
                JSONObject jsonMeta = (JSONObject)jsonAdServing.get("meta");
                MetaEntity metaEntity = new MetaEntity();
                Object code = jsonMeta.get("code");
                if (code != null)
                    metaEntity.setCode(Integer.parseInt(code.toString()));
            } catch (final Exception e) {
                e.printStackTrace();
            }
            NetworkResponse<AdServingEntity> networkResponse =
                    new NetworkResponse<AdServingEntity>(response);
            return networkResponse;
        }
    };
}
