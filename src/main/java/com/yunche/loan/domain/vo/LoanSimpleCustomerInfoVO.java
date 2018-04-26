package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/13
 */
@Data
public class LoanSimpleCustomerInfoVO {

    private Long id;

    private String name;

    private String idCard;

    private String mobile;
    /**
     * 与主贷人关系
     */
    private Byte custRelation;
    /**
     * 客户类型
     */
    private Byte custType;
    /**
     * 银行征信结果
     */
    private Byte bankCreditResult;
    /**
     * 社会征信结果
     */
    private Byte socialCreditResult;

    /**
     * 文件列表
     */
    private List<FileVO> files = Collections.EMPTY_LIST;
}
