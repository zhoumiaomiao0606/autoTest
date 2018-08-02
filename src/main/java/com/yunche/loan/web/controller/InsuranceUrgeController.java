package com.yunche.loan.web.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.RenewInsuranceParam;
import com.yunche.loan.domain.query.InsuranceListQuery;
import com.yunche.loan.domain.vo.InsuranceUrgeVO;
import com.yunche.loan.service.InsuranceUrgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/insuranceurge")
public class InsuranceUrgeController {

    @Autowired
    private InsuranceUrgeService insuranceUrgeService;

    /**
     *
     * @return
     */
    @PostMapping("/list")
    public ResultBean list(@RequestBody InsuranceListQuery insuranceListQuery){

        PageHelper.startPage(insuranceListQuery.getPageIndex(), insuranceListQuery.getPageSize(), true);

        List list = insuranceUrgeService.list(insuranceListQuery);

        PageInfo<InsuranceUrgeVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @PostMapping(value = "/renew")
    public ResultBean renewInsurance(@RequestBody RenewInsuranceParam renewInsuranceParam){

        insuranceUrgeService.renew(renewInsuranceParam);

        return ResultBean.ofSuccess(null,"保存成功");
    }
    /**
     * 催保分配详情页
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("orderId") Long orderId){

        return insuranceUrgeService.detail(orderId);
    }

    @GetMapping("/renewDetail")
    public ResultBean renewDetail(@RequestParam("id") Long id, @RequestParam("orderId") Long orderId){
        return insuranceUrgeService.renewDetail(id,orderId);
    }

    /**
     * 您好:{客户}(先生/女士)
     这里是中顺汽车有限公司(你车子担保公司)的车险专员:{业务员}!你的车子保险金额已经核算好.
     车损险:#{}保费:0.00.
     三者万,保费:0.00.
     (司机/乘客万1座)保费:0.00.
     盗抢险:0.00.自燃险:0.00.
     玻璃险:0.00.
     不计免赔特约险:0.00.
     交强险:0.00.车船税:0.00.
     共计:0.00.
     公司法代账号：6222081202007385758
     ,包功.工商银行城站支行.
     如有问题可以直接联系我.
     电话:001
     （*按揭期间要在我们担保公司投保,
     不然保证金会将受到影响,谢谢.）

     */
    @PostMapping("/genesms")
    public ResultBean generateSms(@RequestBody RenewInsuranceParam renewInsuranceParam){
        String sms = insuranceUrgeService.generateSms(renewInsuranceParam);
        return ResultBean.ofSuccess(sms);
    }

    @PostMapping(value = "/sendSms",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean sendSms(@RequestBody RenewInsuranceParam param){
        return insuranceUrgeService.sendSms(param);

    }

    @GetMapping(value = "/approval")
    public ResultBean approval(@RequestParam Long orderId){
        return insuranceUrgeService.approval(orderId);

    }
}
