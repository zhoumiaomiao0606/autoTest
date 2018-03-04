package com.yunche.loan.domain.param;

import com.yunche.loan.domain.viewObj.CustomerVO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
@Data
public class CustDetailParam {
    /**
     * 主贷人
     */
    private CustomerVO principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerVO> commonLenderList;
    /**
     * 担保人列表
     */
    private List<CustomerVO> guarantorList;
    /**
     * 紧急联系人列表
     */
    private List<CustomerVO> emergencyContactList;
}
