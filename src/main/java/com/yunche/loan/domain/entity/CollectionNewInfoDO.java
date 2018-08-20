package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;
@Data
public class CollectionNewInfoDO extends CollectionNewInfoDOKey {
    private String isvisit;

    private String circumstances;

    private String police;

    private String notarization;

    private String secondMortgage;

    private String collectionManid;

    private String islaw;

    private String dispatchedStaff;

    private Date dispatchedDate;


}