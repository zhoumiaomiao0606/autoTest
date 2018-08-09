package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class EmployeeVO {

    private Long id;

    private String name;

    private String idCard;

    private String mobile;

    private String email;

    private String dingDing;

    private String title;

    private Date entryDate;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private Byte type;

    private Boolean selected;
    /**
     * 所属部门
     */
    private List<BaseVO> department;
    /**
     * 直接上级
     */
    private List<BaseVO> parent;
    /**
     * 直接上级name
     */
    private String parentName;
    /**
     * 合伙人ID
     */
    private Long partnerId;
    /**
     * 合伙人名称
     */
    private String partnerName;

    List<List<Long>> bizAreaIdList;
}
