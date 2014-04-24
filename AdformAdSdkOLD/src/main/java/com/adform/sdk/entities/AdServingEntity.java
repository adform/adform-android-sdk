package com.adform.sdk.entities;

import com.adform.sdk.interfaces.Ad;

public class AdServingEntity implements Ad {

	private static final long serialVersionUID = 3271938798582141269L;

    private String version;
    private AdEntity adEntity;

    public AdServingEntity() {}

    public AdServingEntity(String version, AdEntity adEntity) {
        this.version = version;
        this.adEntity = adEntity;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void setType(int adType) {

    }

    public AdEntity getAdEntity() {
        return adEntity;
    }

    public void setAdEntity(AdEntity adEntity) {
        this.adEntity = adEntity;
    }
}
