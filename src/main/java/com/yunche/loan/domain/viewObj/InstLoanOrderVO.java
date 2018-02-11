package com.yunche.loan.domain.viewObj;

import com.yunche.loan.domain.dataObj.InstProcessNodeDO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class InstLoanOrderVO implements Serializable {
    private Long orderId;

    private String orderNbr;

    private Long custId;

    private Long prodId;

    private String processInstId;

    private Long carId;

    private Integer carType;

    private Integer carPrice;

    private Boolean carKey;

    private Integer gpsNum;

    private Integer interestRate;

    private Integer loanAmount;

    private Integer firstPayAmount;

    private Integer loanRate;

    private Integer loanStage;

    private Integer bankPrincipalAmount;

    private Integer bankChargeAmount;

    private Integer totalAmount;

    private Integer firstMonthPayAmount;

    private Integer eachMonthPayAmount;

    private Long partnerId;

    private String partnerAccountName;

    private String partnerBank;

    private String partnerAccountNum;

    private Integer partnerPayType;

    private Long insuId;

    private Long salesmanId;

    private String salesmanName;

    // 贷款额度档次:1-13W以下/2-13至20W/3-20W以上
    private Integer amountGrade;

    private Long areaId;

    private String prov;

    private String city;

    private String feature;

    private String bank;

    private Long investigatorId;

    private String investigatorName;

    private String investigateAddress;

    private String investigateContent;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String action;

    private CustBaseInfoVO custBaseInfoVO;

    private List<InstProcessNodeVO> processRecordList;

    private List<InstProcessNodeVO> todoProcessList;

    public List<InstProcessNodeVO> getTodoProcessList() {
        return todoProcessList;
    }

    public void setTodoProcessList(List<InstProcessNodeVO> todoProcessList) {
        this.todoProcessList = todoProcessList;
    }

    public List<InstProcessNodeVO> getProcessRecordList() {
        return processRecordList;
    }

    public void setProcessRecordList(List<InstProcessNodeVO> processRecordList) {
        this.processRecordList = processRecordList;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getInvestigatorId() {
        return investigatorId;
    }

    public void setInvestigatorId(Long investigatorId) {
        this.investigatorId = investigatorId;
    }

    public String getInvestigatorName() {
        return investigatorName;
    }

    public void setInvestigatorName(String investigatorName) {
        this.investigatorName = investigatorName;
    }

    public String getInvestigateAddress() {
        return investigateAddress;
    }

    public void setInvestigateAddress(String investigateAddress) {
        this.investigateAddress = investigateAddress;
    }

    public String getInvestigateContent() {
        return investigateContent;
    }

    public void setInvestigateContent(String investigateContent) {
        this.investigateContent = investigateContent;
    }

    public String getOrderNbr() {
        return orderNbr;
    }

    public void setOrderNbr(String orderNbr) {
        this.orderNbr = orderNbr;
    }

    public Boolean getCarKey() {
        return carKey;
    }

    public void setCarKey(Boolean carKey) {
        this.carKey = carKey;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerAccountName() {
        return partnerAccountName;
    }

    public void setPartnerAccountName(String partnerAccountName) {
        this.partnerAccountName = partnerAccountName;
    }

    public String getPartnerBank() {
        return partnerBank;
    }

    public void setPartnerBank(String partnerBank) {
        this.partnerBank = partnerBank;
    }

    public String getPartnerAccountNum() {
        return partnerAccountNum;
    }

    public void setPartnerAccountNum(String partnerAccountNum) {
        this.partnerAccountNum = partnerAccountNum;
    }

    public Integer getPartnerPayType() {
        return partnerPayType;
    }

    public void setPartnerPayType(Integer partnerPayType) {
        this.partnerPayType = partnerPayType;
    }

    public CustBaseInfoVO getCustBaseInfoVO() {
        return custBaseInfoVO;
    }

    public void setCustBaseInfoVO(CustBaseInfoVO custBaseInfoVO) {
        this.custBaseInfoVO = custBaseInfoVO;
    }

    public Long getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(Long salesmanId) {
        this.salesmanId = salesmanId;
    }

    public String getSalesmanName() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    public Integer getAmountGrade() {
        return amountGrade;
    }

    public void setAmountGrade(Integer amountGrade) {
        this.amountGrade = amountGrade;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public Integer getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Integer loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Integer getFirstPayAmount() {
        return firstPayAmount;
    }

    public void setFirstPayAmount(Integer firstPayAmount) {
        this.firstPayAmount = firstPayAmount;
    }

    public String getProcessInstId() {
        return processInstId;
    }

    public void setProcessInstId(String processInstId) {
        this.processInstId = processInstId;
    }

    public Integer getCarPrice() {
        return carPrice;
    }

    public void setCarPrice(Integer carPrice) {
        this.carPrice = carPrice;
    }

    public Integer getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Integer interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getLoanRate() {
        return loanRate;
    }

    public void setLoanRate(Integer loanRate) {
        this.loanRate = loanRate;
    }

    public Integer getLoanStage() {
        return loanStage;
    }

    public void setLoanStage(Integer loanStage) {
        this.loanStage = loanStage;
    }

    public Integer getBankPrincipalAmount() {
        return bankPrincipalAmount;
    }

    public void setBankPrincipalAmount(Integer bankPrincipalAmount) {
        this.bankPrincipalAmount = bankPrincipalAmount;
    }

    public Integer getBankChargeAmount() {
        return bankChargeAmount;
    }

    public void setBankChargeAmount(Integer bankChargeAmount) {
        this.bankChargeAmount = bankChargeAmount;
    }

    public Integer getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getCarType() {
        return carType;
    }

    public void setCarType(Integer carType) {
        this.carType = carType;
    }

    public Integer getGpsNum() {
        return gpsNum;
    }

    public void setGpsNum(Integer gpsNum) {
        this.gpsNum = gpsNum;
    }

    public Integer getFirstMonthPayAmount() {
        return firstMonthPayAmount;
    }

    public void setFirstMonthPayAmount(Integer firstMonthPayAmount) {
        this.firstMonthPayAmount = firstMonthPayAmount;
    }

    public Integer getEachMonthPayAmount() {
        return eachMonthPayAmount;
    }

    public void setEachMonthPayAmount(Integer eachMonthPayAmount) {
        this.eachMonthPayAmount = eachMonthPayAmount;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getInsuId() {
        return insuId;
    }

    public void setInsuId(Long insuId) {
        this.insuId = insuId;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov == null ? null : prov.trim();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature == null ? null : feature.trim();
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