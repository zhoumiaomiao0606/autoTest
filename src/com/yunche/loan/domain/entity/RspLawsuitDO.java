package com.yunche.loan.domain.entity;

public class RspLawsuitDO {
    private Long id;

    private String sortTimeString;

    private String dataType;

    private String body;

    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSortTimeString() {
        return sortTimeString;
    }

    public void setSortTimeString(String sortTimeString) {
        this.sortTimeString = sortTimeString == null ? null : sortTimeString.trim();
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType == null ? null : dataType.trim();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body == null ? null : body.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }
}