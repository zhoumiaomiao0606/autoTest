package com.yunche.loan.domain.queryObj;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/8.
 */
public class OrderListQuery extends BaseQuery {

    private String orderNbr;

    private String custName;

    private String phone;

    private String identityNumber;

    private Long areaId;

    private String prov;

    private String city;

    private Long partnerId;

    private String startDateString;

    private String endDateString;

    private Date startDate;

    private Date endDate;

    private String todoProcessTask;

    private String doneProcessTask;

    public Date getStartDate() {
        if (StringUtils.isEmpty(startDateString)) return null;
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        try {
            startDate = fmt.parse(startDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    public Date getEndDate() {
        if (StringUtils.isEmpty(endDateString)) return null;
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = null;
        try {
            endDate = fmt.parse(endDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return endDate;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getOrderNbr() {
        return orderNbr;
    }

    public void setOrderNbr(String orderNbr) {
        this.orderNbr = orderNbr;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getTodoProcessTask() {
        return todoProcessTask;
    }

    public void setTodoProcessTask(String todoProcessTask) {
        this.todoProcessTask = todoProcessTask;
    }

    public String getDoneProcessTask() {
        return doneProcessTask;
    }

    public void setDoneProcessTask(String doneProcessTask) {
        this.doneProcessTask = doneProcessTask;
    }
}
