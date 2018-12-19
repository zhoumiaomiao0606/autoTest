package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.PartnerBankAccountDO;
import com.yunche.loan.domain.entity.PartnerDO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class PartnerParam extends PartnerDO {

    private String leaderIdCard;

    private String leaderEmail;

    private String leaderDingDing;

    /**
     * 业务产品ID列表
     */
    private List<Long> bizModelIdList;

    /**
     * 关联银行卡账号列表
     */
    private List<PartnerBankAccountDO> bankAccountList = new ArrayList<>();

    /**
     * 上牌地列表
     */

    private List<Long> areaIdList;

    private String gpsCompany;

    private String gpsAccount;

    private String partnerCode;

    /*private String partnerGroup;*/
}
