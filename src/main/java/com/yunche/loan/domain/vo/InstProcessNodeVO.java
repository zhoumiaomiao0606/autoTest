package com.yunche.loan.domain.vo;

import com.yunche.loan.config.constant.ProcessActionEnum;

import java.io.Serializable;
import java.util.Date;

public class InstProcessNodeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long nodeId;

    private Long orderId;

    private String processInstId;

    private String nodeName;

    private String nodeCode;

    private String nextNodeCode;

    private String previousNodeCode;

    private String feature;

    private Long operatorId;

    private String operatorName;

    private String operatorRole;

    private String status;

    private String action;

    private Date gmtCreate;

    private Date gmtModify;

    public String getAction() {
        if (status == null) return action;
        if (ProcessActionEnum.PASS.name().equals(status)) {
            return "审核通过";
        } else if (ProcessActionEnum.CANCEL.name().equals(status)) {
            return "弃单";
        } else if (ProcessActionEnum.REJECT.name().equals(status)) {
            return "打回修改";
        } else if (ProcessActionEnum.SUPPLEMENT.name().equals(status)) {
            return "增补资料";
        }
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName == null ? null : nodeName.trim();
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode == null ? null : nodeCode.trim();
    }

    public String getNextNodeCode() {
        return nextNodeCode;
    }

    public void setNextNodeCode(String nextNodeCode) {
        this.nextNodeCode = nextNodeCode == null ? null : nextNodeCode.trim();
    }

    public String getPreviousNodeCode() {
        return previousNodeCode;
    }

    public void setPreviousNodeCode(String previousNodeCode) {
        this.previousNodeCode = previousNodeCode == null ? null : previousNodeCode.trim();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName == null ? null : operatorName.trim();
    }

    public String getOperatorRole() {
        return operatorRole;
    }

    public void setOperatorRole(String operatorRole) {
        this.operatorRole = operatorRole == null ? null : operatorRole.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }
}