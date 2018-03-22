package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.RepaymentRecordVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import lombok.Data;

import java.util.List;


@Data
public class RepaymentRecordParam  extends RepaymentRecordVO{

    //TODO 详情界面展示要素待补充


    /**
     * 联系电话
     */
    private String mobile;


    private Area area;

    /**
     * 分期金额
     */
    /**
     * 业务区域
     */
    @Data
    public static class  Area{

            private String provience;//省份
            private  String  city;//城市
    }
    /**
     * 业务合伙人
     */
    public String parterner;
    /**
     * 征信银行
     */
    public String creditBank;

    /**
     *客户详细信息
     */
    List<UniversalCustomerVO> universalCustomerVOS;
}
