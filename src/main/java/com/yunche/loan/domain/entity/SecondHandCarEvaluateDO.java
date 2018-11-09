package com.yunche.loan.domain.entity;

import java.math.BigDecimal;
import java.util.Date;

public class SecondHandCarEvaluateDO {
    private Long id;

    private String vin;

    private Long parnter_id;

    private String owner;

    private String plate_num;

    private String engine_num;

    private Date register_date;

    private String make_name;

    private String model_name;

    private String name;

    private String style_color;

    private BigDecimal evaluate_price;

    private Date query_time;

    private Byte state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin == null ? null : vin.trim();
    }

    public Long getParnter_id() {
        return parnter_id;
    }

    public void setParnter_id(Long parnter_id) {
        this.parnter_id = parnter_id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num == null ? null : plate_num.trim();
    }

    public String getEngine_num() {
        return engine_num;
    }

    public void setEngine_num(String engine_num) {
        this.engine_num = engine_num == null ? null : engine_num.trim();
    }

    public Date getRegister_date() {
        return register_date;
    }

    public void setRegister_date(Date register_date) {
        this.register_date = register_date;
    }

    public String getMake_name() {
        return make_name;
    }

    public void setMake_name(String make_name) {
        this.make_name = make_name == null ? null : make_name.trim();
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name == null ? null : model_name.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getStyle_color() {
        return style_color;
    }

    public void setStyle_color(String style_color) {
        this.style_color = style_color == null ? null : style_color.trim();
    }

    public BigDecimal getEvaluate_price() {
        return evaluate_price;
    }

    public void setEvaluate_price(BigDecimal evaluate_price) {
        this.evaluate_price = evaluate_price;
    }

    public Date getQuery_time() {
        return query_time;
    }

    public void setQuery_time(Date query_time) {
        this.query_time = query_time;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }
}