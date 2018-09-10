package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.LoanDataFlowParam;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.domain.vo.UniversalDataFlowDetailVO;
import com.yunche.loan.service.LoanDataFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/loanDataFlow", "/api/v1/app/loanDataFlow"})
public class LoanDataFlowController {

    @Autowired
    private LoanDataFlowService loanDataFlowService;


    @GetMapping(value = "/detail")
    public ResultBean<UniversalDataFlowDetailVO> detail(@RequestParam Long id) {
        return loanDataFlowService.detail(id);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody LoanDataFlowParam loanDataFlowParam) {
        return loanDataFlowService.create(loanDataFlowParam);
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Integer> update(@RequestBody LoanDataFlowParam loanDataFlowParam) {
        return loanDataFlowService.update(loanDataFlowParam);
    }

    @GetMapping(value = "/flowDept")
    public ResultBean<List<BaseVO>> flowOutDept() {
        return loanDataFlowService.flowDept();
    }

    @GetMapping("/queryDataFlowCustomerOrder")
    public ResultBean<List<UniversalCustomerOrderVO>> queryDataFlowCustomerOrder(@RequestParam(required = false) String name) {
        return loanDataFlowService.queryDataFlowCustomerOrder(name);
    }

    @GetMapping(value = "/imp")
    public ResultBean<Integer> imp(@RequestParam(value = "key") String ossKey) {
        return loanDataFlowService.imp(ossKey);
    }

    @PostMapping(value = "/exp")
    public ResultBean<String> export(@RequestBody @Validated TaskListQuery taskListQuery) {
        return loanDataFlowService.export(taskListQuery);
    }

    /**
     * 批量接收
     *
     * @param ids 逗号分隔
     * @return
     */
    @GetMapping(value = "/batchReceived")
    public ResultBean<Integer> batchReceived(@RequestParam String ids) {
        return loanDataFlowService.batchReceived(ids);
    }

    /**
     * 获取dataFlowId
     *
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    @GetMapping(value = "/getDataFlowId")
    public ResultBean<Long> getDataFlowId(@RequestParam Long orderId,
                                          @RequestParam String taskDefinitionKey) {
        return loanDataFlowService.getDataFlowId(orderId, taskDefinitionKey);
    }
}
