package com.yunche.loan.domain.entity;

import com.sun.org.apache.xpath.internal.operations.String;
import com.yunche.loan.config.constant.LoanAmountGradeEnum;
import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class TelephoneVerifyVO {

    /**
     * 头部基本信息
     */
    private BaseInfo baseInfo;

    /**
     * 增补内容
     */
    private List<FileVO> files;

    /**
     * 贷款信息
     */
    private LoanInfo loanInfo;

    @Data
    public static class LoanInfo {
        /**
         * 业务单单号
         */
        private String orderId;
        /**
         * 主贷人姓名
         */
        private String principalLenderName;
        /**
         * 身份证
         */
        private String idCard;
        /**
         * 业务区域
         */
        private String bizArea;
        /**
         * 合伙人
         */
        private String partnerName;
        /**
         * 贷款产品
         */
        private String financialProductName;




    }


    @Data
    public static class BaseInfo {
        /**
         * 业务单单号
         */
        private String orderId;
        /**
         * 主贷人姓名
         */
        private String principalLenderName;
        /**
         * 身份证
         */
        private String idCard;
        /**
         * 业务员
         */
        private String salesmanName;
        /**
         * 手机号
         */
        private String mobile;
        /**
         * 贷款额
         */
        private BigDecimal loanAmount;
        /**
         * 合伙人
         */
        private String partnerName;
        /**
         * GPS安装个数
         */
        private Integer gpsNum;
        /**
         * 是否留备用钥匙
         */
        private Byte carKey;
        /**
         * 电审结果
         */
        private Byte telephoneVerifyResult;
        /**
         * 电审描述
         */
        private Byte telephoneVerifyInfo;
    }
}
