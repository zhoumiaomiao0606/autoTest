package com.yunche.loan.web.controller;


import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreateExpensesDetailParam;
import com.yunche.loan.domain.param.LegworkReimbursementParam;
import com.yunche.loan.domain.param.SubimitVisitDoorParam;
import com.yunche.loan.domain.vo.LegworkReimbursementUpdateParam;
import com.yunche.loan.domain.vo.LegworkReimbursementVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.SubimitVisitDoorVO;
import com.yunche.loan.service.LegworkReimbursementService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/legworkreimbursement")
public class LegworkReimbursementController {

    @Resource
    private LegworkReimbursementService legworkReimbursementService;

    /**
     * 拖车关联订单
     */
    @PostMapping(value = "/subimitVisitDoorList")
    public ResultBean<List<SubimitVisitDoorVO>> subimitVisitDoorList(@RequestBody @Validated @Valid SubimitVisitDoorParam param) {
        PageInfo pageInfo = legworkReimbursementService.subimitVisitDoorList(param);
        return ResultBean.ofSuccess(pageInfo == null ? null : pageInfo.getList(), new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
    /**
     * 拖车关联订单详情
     */
    @PostMapping(value = "/createExpensesDetail")
    public ResultBean<Long> createExpensesDetail(@RequestBody @Validated @Valid CreateExpensesDetailParam param) {
        return ResultBean.ofSuccess(legworkReimbursementService.createExpensesDetail(param),"编辑成功");
    }

    /**
     * 拖车关联订单详情
     */
    @GetMapping(value = "/expensesDetail")
    public ResultBean<RecombinationVO> expensesDetail(@RequestParam Long id) {
        return ResultBean.ofSuccess(legworkReimbursementService.expensesDetail(id),"编辑成功");
    }


    /**
     * 拖车关联订单详情
     */
    @PostMapping(value = "/ ")
    public ResultBean<Void> expensesUpdate(@RequestBody @Validated @Valid LegworkReimbursementUpdateParam param) {
        legworkReimbursementService.expensesUpdate(param);
        return ResultBean.ofSuccess(null,"编辑成功");
    }



    /**
     * 拖车关联订单详情
     */
    @PostMapping(value = "/legworkReimbursementList")
    public ResultBean<List<LegworkReimbursementVO>> list(@RequestBody @Validated @Valid LegworkReimbursementParam param) {
        PageInfo pageInfo = legworkReimbursementService.list(param);
        return ResultBean.ofSuccess(pageInfo == null ? null : pageInfo.getList(), new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }
}
