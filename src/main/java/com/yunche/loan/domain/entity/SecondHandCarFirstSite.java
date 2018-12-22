package com.yunche.loan.domain.entity;

import java.util.Date;

public class SecondHandCarFirstSite {
    private Long id;

    private String first_site_json;

    private Date query_time;

    private Long saleman_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst_site_json() {
        return first_site_json;
    }

    public void setFirst_site_json(String first_site_json) {
        this.first_site_json = first_site_json == null ? null : first_site_json.trim();
    }

    public Date getQuery_time() {
        return query_time;
    }

    public void setQuery_time(Date query_time) {
        this.query_time = query_time;
    }

    public Long getSaleman_id() {
        return saleman_id;
    }

    public void setSaleman_id(Long saleman_id) {
        this.saleman_id = saleman_id;
    }
}