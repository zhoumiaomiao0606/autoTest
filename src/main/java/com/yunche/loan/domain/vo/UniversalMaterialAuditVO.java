package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/8/1
 */
@Data
public class UniversalMaterialAuditVO {

    /**
     * 资料齐全日期
     */
    private String completeMaterialDate;
    /**
     * 备注
     */
    private String remark;
}
