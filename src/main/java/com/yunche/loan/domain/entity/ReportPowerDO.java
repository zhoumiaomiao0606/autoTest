package com.yunche.loan.domain.entity;

import lombok.Data;

@Data
public class ReportPowerDO {
    private String groupName;

    private String reportDep;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getReportDep() {
        return reportDep;
    }

    public void setReportDep(String reportDep) {
        this.reportDep = reportDep == null ? null : reportDep.trim();
    }
}