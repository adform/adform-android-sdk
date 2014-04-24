package com.adform.sdk2.network.app.entities.entities;

/**
 * Created by mariusm on 23/04/14.
 */
public class TagDataEntity {
    private int id;
    private int asset;
    private int banner;
    private String icid;
    private String icidt;
    private String imprid;
    private String impressionUrl;
    // todo: clickUrl entity
    private String src;

    public TagDataEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAsset() {
        return asset;
    }

    public void setAsset(int asset) {
        this.asset = asset;
    }

    public int getBanner() {
        return banner;
    }

    public void setBanner(int banner) {
        this.banner = banner;
    }

    public String getIcid() {
        return icid;
    }

    public void setIcid(String icid) {
        this.icid = icid;
    }

    public String getIcidt() {
        return icidt;
    }

    public void setIcidt(String icidt) {
        this.icidt = icidt;
    }

    public String getImprid() {
        return imprid;
    }

    public void setImprid(String imprid) {
        this.imprid = imprid;
    }

    public String getImpressionUrl() {
        return impressionUrl;
    }

    public void setImpressionUrl(String impressionUrl) {
        this.impressionUrl = impressionUrl;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
