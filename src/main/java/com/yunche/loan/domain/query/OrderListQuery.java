package com.yunche.loan.domain.query;

import com.yunche.loan.config.constant.LoanProcessConst;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by zhouguoliang on 2018/2/8.
 */
@Data
public class OrderListQuery extends BaseQuery {

    // ------------------- 节点条件
    private String taskDefinitionKey;

    private Byte taskStatus;


    // ------------------- 订单条件
    private Long orderId;

    private String custName;

    private String custIdCard;

    private Long bankId;

    private String bankName;


    // ------------------- 数据权限
    /**
     * 合伙人权限
     */
    private List<Long> partnerIdList;
    /**
     * 银行权限
     */
    private List<String> bankNameList;
    /**
     * 业务员权限
     */
    private Set<String> salesmanIdList;


    // ------------------- 动态SQL
    /**
     * 动态SQL：loan_process  动态节点字段
     */
    private String loanProcessFiled;


    /**
     * 动态SQL：loan_process 动态节点字段 --> 根据taskDefinitionKey动态生成
     *
     * @return
     */
    public String getLoanProcessFiled() {
        if (StringUtils.isNotBlank(taskDefinitionKey)) {
            String[] keyArr = taskDefinitionKey.split(LoanProcessConst.TASK_KEY_USER_TASK_PREFIX_2);
            if (keyArr.length == 2) {
                return keyArr[1];
            }
        }
        return null;
    }
}
