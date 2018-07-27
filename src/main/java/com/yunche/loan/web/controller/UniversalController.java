package com.yunche.loan.web.controller;


import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.vo.Postcode;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/universal")
public class UniversalController {

    @Resource
    private LoanQueryService loanQueryService;

    @Resource
    private PartnerDOMapper partnerDOMapper;


    @GetMapping(value = "/customer")
    public ResultBean customerDetail(@RequestParam String customer_id) {

        return ResultBean.ofSuccess(loanQueryService.universalCustomerDetail(Long.valueOf(customer_id)));
    }

    @GetMapping(value = "/getPartnerLeaderId")
    public ResultBean getPartnerLeaderId(@RequestParam @Validated String partnerId) {
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(Long.valueOf(partnerId),new Byte("0"));
        if(partnerDO == null){
            throw new BizException("合伙人团队不存在");
        }
        if(partnerDO.getLeaderId() == null){
            throw new BizException("请先设置合伙人leader");
        }
        return ResultBean.ofSuccess(partnerDO.getLeaderId());
    }
}
