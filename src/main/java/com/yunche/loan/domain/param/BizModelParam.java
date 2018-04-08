package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.BizModelVO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/4/8
 */
@Data
public class BizModelParam {

    private Long bizId;

    private String title;

    private String description;

    private String scene;

    private String custTarget;

    private Integer carType;

    private Integer status;

    /**
     * 区域ID-合伙人ID列表
     */
    private List<RelaAreaIdPartnerIdList> relaAreaIdPartnerIdList;
    /**
     * 金融产品ID列表
     */
    private List<Long> financialProductIdList;

    @Data
    public static class RelaAreaIdPartnerIdList {
        /**
         * 限制区域ID
         */
        private Long areaId;
        /**
         * 区域内 -限制的合伙人ID列表
         */
        private List<Long> partnerIdList;
    }
}
