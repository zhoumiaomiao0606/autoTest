package com.yunche.loan.domain.entity;

import java.util.Date;

public class LitigationDO {
    private Long id;

    private Long orderId;

    private Date registerDate;

    private String registerId;

    private String plaintiff;

    private String defendant;

    private String ruleCourt;

    private String litigationTotal;

    private String litigationMoney;

    private String undertakeCourt;

    private String undertakeCourtTel;

    private String clerk;

    private String clerkTel;

    private Date sittingDate;

    private String publicationFee;

    private String sentence;

    private Date preservationDate;

    private String preservationMoney;

    private String preservationFee;

    private String preservationJudge;

    private Date effectDate;

    private String preservationJudgeTel;

    private Date returnDate;

    private String returnMoney;

    private String remarks;

    private String litigationNo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId == null ? null : registerId.trim();
    }

    public String getPlaintiff() {
        return plaintiff;
    }

    public void setPlaintiff(String plaintiff) {
        this.plaintiff = plaintiff == null ? null : plaintiff.trim();
    }

    public String getDefendant() {
        return defendant;
    }

    public void setDefendant(String defendant) {
        this.defendant = defendant == null ? null : defendant.trim();
    }

    public String getRuleCourt() {
        return ruleCourt;
    }

    public void setRuleCourt(String ruleCourt) {
        this.ruleCourt = ruleCourt == null ? null : ruleCourt.trim();
    }

    public String getLitigationTotal() {
        return litigationTotal;
    }

    public void setLitigationTotal(String litigationTotal) {
        this.litigationTotal = litigationTotal == null ? null : litigationTotal.trim();
    }

    public String getLitigationMoney() {
        return litigationMoney;
    }

    public void setLitigationMoney(String litigationMoney) {
        this.litigationMoney = litigationMoney == null ? null : litigationMoney.trim();
    }

    public String getUndertakeCourt() {
        return undertakeCourt;
    }

    public void setUndertakeCourt(String undertakeCourt) {
        this.undertakeCourt = undertakeCourt == null ? null : undertakeCourt.trim();
    }

    public String getUndertakeCourtTel() {
        return undertakeCourtTel;
    }

    public void setUndertakeCourtTel(String undertakeCourtTel) {
        this.undertakeCourtTel = undertakeCourtTel == null ? null : undertakeCourtTel.trim();
    }

    public String getClerk() {
        return clerk;
    }

    public void setClerk(String clerk) {
        this.clerk = clerk == null ? null : clerk.trim();
    }

    public String getClerkTel() {
        return clerkTel;
    }

    public void setClerkTel(String clerkTel) {
        this.clerkTel = clerkTel == null ? null : clerkTel.trim();
    }

    public Date getSittingDate() {
        return sittingDate;
    }

    public void setSittingDate(Date sittingDate) {
        this.sittingDate = sittingDate;
    }

    public String getPublicationFee() {
        return publicationFee;
    }

    public void setPublicationFee(String publicationFee) {
        this.publicationFee = publicationFee == null ? null : publicationFee.trim();
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence == null ? null : sentence.trim();
    }

    public Date getPreservationDate() {
        return preservationDate;
    }

    public void setPreservationDate(Date preservationDate) {
        this.preservationDate = preservationDate;
    }

    public String getPreservationMoney() {
        return preservationMoney;
    }

    public void setPreservationMoney(String preservationMoney) {
        this.preservationMoney = preservationMoney == null ? null : preservationMoney.trim();
    }

    public String getPreservationFee() {
        return preservationFee;
    }

    public void setPreservationFee(String preservationFee) {
        this.preservationFee = preservationFee == null ? null : preservationFee.trim();
    }

    public String getPreservationJudge() {
        return preservationJudge;
    }

    public void setPreservationJudge(String preservationJudge) {
        this.preservationJudge = preservationJudge == null ? null : preservationJudge.trim();
    }

    public Date getEffectDate() {
        return effectDate;
    }

    public void setEffectDate(Date effectDate) {
        this.effectDate = effectDate;
    }

    public String getPreservationJudgeTel() {
        return preservationJudgeTel;
    }

    public void setPreservationJudgeTel(String preservationJudgeTel) {
        this.preservationJudgeTel = preservationJudgeTel == null ? null : preservationJudgeTel.trim();
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnMoney() {
        return returnMoney;
    }

    public void setReturnMoney(String returnMoney) {
        this.returnMoney = returnMoney == null ? null : returnMoney.trim();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks == null ? null : remarks.trim();
    }

    public String getLitigationNo() {
        return litigationNo;
    }

    public void setLitigationNo(String litigationNo) {
        this.litigationNo = litigationNo == null ? null : litigationNo.trim();
    }
}