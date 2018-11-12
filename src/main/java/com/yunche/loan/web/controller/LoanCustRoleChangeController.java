package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.LoanCustRoleChangeHisDetailVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.service.LoanCustRoleChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/11/12
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/cust/roleChange")
public class LoanCustRoleChangeController {

    @Autowired
    private LoanCustRoleChangeService loanCustRoleChangeService;


    /**
     * 搜索的数据范围为贷款申请提交前的订单
     *
     * @param name 主贷人姓名
     * @return
     */
    @GetMapping(value = "/queryRoleChangeOrder")
    public ResultBean<List<UniversalCustomerOrderVO>> queryRoleCustomerOrder(@RequestParam(required = false) String name) {
        return ResultBean.ofSuccess(loanCustRoleChangeService.queryRoleChangeOrder(name));
    }

    /**
     * 编辑页面-detail
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/edit/detail")
    public ResultBean<RecombinationVO> editDetail(@RequestParam Long orderId) {
        return ResultBean.ofSuccess(loanCustRoleChangeService.editDetail(orderId));
    }

    /**
     * 编辑-save
     *
     * @param orderId
     * @param customers
     * @return
     */
    @PostMapping(value = "/edit/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> editSave(@RequestParam Long orderId,
                                     @RequestBody List<CustomerParam> customers) {
        return ResultBean.ofSuccess(loanCustRoleChangeService.editSave(orderId, customers));
    }

    /**
     * 历史记录-列表条件查询
     *
     * @param taskListQuery
     * @return
     */
    @PostMapping(value = "/his/list", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<TaskListVO>> queryHisList(@RequestBody TaskListQuery taskListQuery) {
        return loanCustRoleChangeService.queryHisList(taskListQuery);
    }

    /**
     * 历史记录-详情
     *
     * @param roleChangeHisId
     * @return
     */
    @GetMapping(value = "/his/detail")
    public ResultBean<LoanCustRoleChangeHisDetailVO> hisDetail(@RequestParam Long roleChangeHisId) {
        return ResultBean.ofSuccess(loanCustRoleChangeService.hisDetail(roleChangeHisId));
    }
}
