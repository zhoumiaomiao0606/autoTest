package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 提车资料
 */
@Data
public class VehicleInfoVO {

    /**
     * 业务编号
     */
    Long orderId;

    /**
     * 主贷人姓名
     */
    String principalLenderName;
    /**
     * 证件号码
     */
    String idCard;
    /**
     * 合伙人团队
     */
    String partnerName;

    /**
     * 业务员
     */
    String salesman;
    /**
     * 打回人员
     */
    String backOperName;
    /**
     * 创建时间
     */
    Date gmtCreate;
    /**
     * 资料类别列表
     */
    List<FileVO> fileVOS;
    /**
     * 客户ID
     */
    private Long customerId;
}
