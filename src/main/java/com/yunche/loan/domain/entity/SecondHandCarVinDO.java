package com.yunche.loan.domain.entity;

import java.util.Date;

public class SecondHandCarVinDO {
    private Long id;

    private Long parnter_id;

    private String vin;

    private String plate_num;

    private String vehicle_type;

    private String owner;

    private String use_character;

    private String addr;

    private String model;

    private String engine_num;

    private String register_date;

    private String issue_date;

    private String appproved_passenger_capacity;

    private String approved_load;

    private String file_no;

    private String gross_mass;

    private String inspection_record;

    private String overall_dimension;

    private String traction_mass;

    private String unladen_mass;

    private Date query_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParnter_id() {
        return parnter_id;
    }

    public void setParnter_id(Long parnter_id) {
        this.parnter_id = parnter_id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin == null ? null : vin.trim();
    }

    public String getPlate_num() {
        return plate_num;
    }

    public void setPlate_num(String plate_num) {
        this.plate_num = plate_num == null ? null : plate_num.trim();
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type == null ? null : vehicle_type.trim();
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner == null ? null : owner.trim();
    }

    public String getUse_character() {
        return use_character;
    }

    public void setUse_character(String use_character) {
        this.use_character = use_character == null ? null : use_character.trim();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model == null ? null : model.trim();
    }

    public String getEngine_num() {
        return engine_num;
    }

    public void setEngine_num(String engine_num) {
        this.engine_num = engine_num == null ? null : engine_num.trim();
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date == null ? null : register_date.trim();
    }

    public String getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(String issue_date) {
        this.issue_date = issue_date == null ? null : issue_date.trim();
    }

    public String getAppproved_passenger_capacity() {
        return appproved_passenger_capacity;
    }

    public void setAppproved_passenger_capacity(String appproved_passenger_capacity) {
        this.appproved_passenger_capacity = appproved_passenger_capacity == null ? null : appproved_passenger_capacity.trim();
    }

    public String getApproved_load() {
        return approved_load;
    }

    public void setApproved_load(String approved_load) {
        this.approved_load = approved_load == null ? null : approved_load.trim();
    }

    public String getFile_no() {
        return file_no;
    }

    public void setFile_no(String file_no) {
        this.file_no = file_no == null ? null : file_no.trim();
    }

    public String getGross_mass() {
        return gross_mass;
    }

    public void setGross_mass(String gross_mass) {
        this.gross_mass = gross_mass == null ? null : gross_mass.trim();
    }

    public String getInspection_record() {
        return inspection_record;
    }

    public void setInspection_record(String inspection_record) {
        this.inspection_record = inspection_record == null ? null : inspection_record.trim();
    }

    public String getOverall_dimension() {
        return overall_dimension;
    }

    public void setOverall_dimension(String overall_dimension) {
        this.overall_dimension = overall_dimension == null ? null : overall_dimension.trim();
    }

    public String getTraction_mass() {
        return traction_mass;
    }

    public void setTraction_mass(String traction_mass) {
        this.traction_mass = traction_mass == null ? null : traction_mass.trim();
    }

    public String getUnladen_mass() {
        return unladen_mass;
    }

    public void setUnladen_mass(String unladen_mass) {
        this.unladen_mass = unladen_mass == null ? null : unladen_mass.trim();
    }

    public Date getQuery_time() {
        return query_time;
    }

    public void setQuery_time(Date query_time) {
        this.query_time = query_time;
    }
}