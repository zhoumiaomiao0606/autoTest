package com.yunche.loan.domain.entity;

public class InstallGpsDO {
    private Long id;

    private Long oder_id;

    private String gps_number;

    private String feature;

    private Byte status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOder_id() {
        return oder_id;
    }

    public void setOder_id(Long oder_id) {
        this.oder_id = oder_id;
    }

    public String getGps_number() {
        return gps_number;
    }

    public void setGps_number(String gps_number) {
        this.gps_number = gps_number == null ? null : gps_number.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}