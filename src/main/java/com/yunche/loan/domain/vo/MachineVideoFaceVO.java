package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2019/1/4
 */
@Data
public class MachineVideoFaceVO {

    private Long partnerId;

    private String partnerName;

    private String partnerLeaderName;
    /**
     * 机器面签状态：0-关闭；1-开启；
     */
    private Byte machineVideoFaceStatus;
}
