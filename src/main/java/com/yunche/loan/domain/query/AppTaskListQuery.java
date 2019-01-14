package com.yunche.loan.domain.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Data
public class AppTaskListQuery extends BaseQuery {

    /**
     * 多节点查询类型：
     * <p>
     * 1-征信申请列表【未查询：   [提交征信申请单后  ,   贷款业务申请单)        】;
     * 2-征信申请列表【已查询：   [贷款业务申请单    ,   end]                 】;
     * 3-贷款申请列表【待审核：   [提交贷款申请单后  ,   电审通过)             】;
     * 4-贷款申请列表【已审核：   [电审通过后       ,    end]                】;
     * 5-客户查询列表【在贷客户： [提交征信申请单后  ,    未放款)              】;
     * 6-客户查询列表【已贷客户： [已放款           ,    end]                】;
     * 7-视频面签列表【所有列表】;
     */
    @NotNull
    private Integer multipartType;

    private String customer;

    public String getCustomer() {
        if (StringUtils.isBlank(customer)) {
            return null;
        }
        return customer;
    }


    Long employeeId;

    Long telephoneVerifyLevel;

    Long collectionLevel;

    Long financeLevel;

    Long maxGroupLevel;

    Long financeApplyLevel;

    Long refundApplyLevel;

    Long materialSupplementLevel;

    String bankName;

    /*@NotEmpty
    private String taskDefinitionKey;*/

    String idCard;
    Set<String> juniorIds = Sets.newHashSet();

    private List<Long> bizAreaIdList = Lists.newArrayList();//区域ID列表
    private List<String> bankList = Lists.newArrayList();//银行ID列表
}
