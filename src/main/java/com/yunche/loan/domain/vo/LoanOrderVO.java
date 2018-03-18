package com.yunche.loan.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

/**
 * 业务单基本信息
 *
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class LoanOrderVO {
    /**
     * 业务单号
     */
    private String id;
    /**
     * 客户【主贷人】
     */
    private BaseVO customer;
    /**
     * 合伙人
     */
    private BaseVO partner;
    /**
     * 业务员
     */
    private BaseVO salesman;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 业务单创建时间
     */
    private Date gmtCreate;

    /**
     * 当前任务节点审核状态:  0-全部;   1-已提交(审核);   2-未提交(审核);
     */
    private Integer taskStatus;

    /**
     * 当前任务节点
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String currentTask;

    /**
     * 任务类型：1-未提交;  2-打回;
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer taskType;
    /**
     * 任务类型文本：1-未提交;  2-打回;
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String taskTypeText;

    /**
     * 资料增补列表查询展示：
     * 增补类型：1-电审增补;  2-资料审核增补;
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer infoSupplementType;

    /**
     * 客户列表查询展示： -已贷客户
     * 还款状态： 1-正常还款;  2-非正常还款;  3-已结清;
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer repayStatus;
}
