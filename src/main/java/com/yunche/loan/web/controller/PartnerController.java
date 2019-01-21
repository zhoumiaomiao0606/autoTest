package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankCodeDO;
import com.yunche.loan.domain.param.BankCodeParam;
import com.yunche.loan.domain.param.InsOrUpBankListParam;
import com.yunche.loan.domain.query.BizModelQuery;
import com.yunche.loan.domain.query.PartnerQuery;
import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.vo.BizModelVO;
import com.yunche.loan.domain.vo.EmployeeVO;
import com.yunche.loan.domain.vo.PartnerAccountVO;
import com.yunche.loan.domain.vo.PartnerVO;
import com.yunche.loan.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/partner")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;


    /**
     * 创建合伙人
     * <p>
     * 同时绑定业务产品列表
     *
     * @param partnerParam
     * @return
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody PartnerParam partnerParam) {
        return partnerService.create(partnerParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody  PartnerParam partnerParam) {
        return partnerService.update(partnerParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return partnerService.delete(id);
    }

    @GetMapping(value = "/selectAllBankId")
    public ResultBean selectAllBankId(@RequestParam(name = "bankId",required = false) Integer bankId,@RequestParam(name = "level",required = false) Byte level)
    {
        return ResultBean.ofSuccess(partnerService.selectAllBankId(bankId,level));
    }

    @GetMapping(value = "/selectAllBankName")
    public ResultBean selectAllBankName()
    {
        return ResultBean.ofSuccess(partnerService.selectAllBankId(null,null));
    }

    @GetMapping(value = "/selectBankNameByParentId")
    public ResultBean selectBankNameByParentId(@RequestParam("bankId") Integer bankId)
    {
        return ResultBean.ofSuccess(partnerService.selectBankNameByParentId(bankId));
    }

    @GetMapping(value = "/selectBankListByParentName")
    public ResultBean selectBankListByParentName(@RequestParam("bankName") String bankName)
    {
        return ResultBean.ofSuccess(partnerService.selectBankListByParentName(bankName));
    }

    @PostMapping(value = "/insertBankName")
    public ResultBean insertBankName(@RequestBody @Validated BankCodeDO bankCodeDO)
    {
        return partnerService.insertBankName(bankCodeDO);
    }

    @PostMapping(value = "/insertOrUpdateBankList")
    public ResultBean insertOrUpdateBankList(@RequestBody @Validated InsOrUpBankListParam insOrUpBankListParam)
    {
        return partnerService.insertOrUpdateBankList(insOrUpBankListParam);
    }

    @PostMapping(value = "/bankNameList")
    public ResultBean bankNameList(@RequestBody @Validated BankCodeParam param)
    {
        return partnerService.bankNameList(param);
    }

    @GetMapping(value = "/deleteByBankId")
    public ResultBean deleteByBankId(@RequestParam("bankId") Integer bankId)
    {
        return partnerService.deleteByBankId(bankId);
    }

    /**
     * 根据ID获取详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public ResultBean<PartnerVO> getById(@RequestParam("id") Long id) {
        return partnerService.getById(id);
    }

    /**
     * 分页条件查询
     * <p>
     * 一级区域列表可通过传入level=1查询
     * 下级区域列表可通过传入parentId查询
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<PartnerVO>> query(@RequestBody PartnerQuery query) {
        return partnerService.query(query);
    }

    /**
     * 当前合伙人关联的业务产品列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listBizModel")
    public ResultBean<List<BizModelVO>> listBizModel(@RequestBody BizModelQuery query) {
        return partnerService.listBizModel(query);
    }

    /**
     * 绑定业务产品      -支持列表
     *
     * @param id          合伙人ID
     * @param bizModelIds 业务产品ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/bindBizModel")
    public ResultBean<Void> bindBizModel(@RequestParam("id") Long id,
                                         @RequestParam("bizModelIds") String bizModelIds) {
        return partnerService.bindBizModel(id, bizModelIds);
    }

    /**
     * 解绑关联的业务产品      -支持列表
     *
     * @param id          合伙人ID
     * @param bizModelIds 业务产品ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/unbindBizModel")
    public ResultBean<Void> unbindBizModel(@RequestParam("id") Long id,
                                           @RequestParam("bizModelIds") String bizModelIds) {
        return partnerService.unbindBizModel(id, bizModelIds);
    }

    /**
     * 当前合伙人关联的业务员列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listEmployee")
    public ResultBean<List<EmployeeVO>> listEmployee(@RequestBody RelaQuery query) {
        return partnerService.listEmployee(query);
    }

    /**
     * 绑定(外包)员工列表      -支持列表
     *
     * @param id          合伙人ID
     * @param employeeIds (外包)员工ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/bindEmployee")
    public ResultBean<Void> bindEmployee(@RequestParam("id") Long id,
                                         @RequestParam("employeeIds") String employeeIds) {
        return partnerService.bindEmployee(id, employeeIds);
    }

    /**
     * 解绑(外包)员工列表      -支持列表
     *
     * @param id          合伙人ID
     * @param employeeIds (外包)员工ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/unbindEmployee")
    public ResultBean<Void> unbindEmployee(@RequestParam("id") Long id,
                                           @RequestParam("employeeIds") String employeeIds) {
        return partnerService.unbindEmployee(id, employeeIds);
    }

    /**
     * 员工所属合伙人的账号信息列表
     *
     * @param employeeId
     * @return
     */
    @GetMapping(value = "/listAccount")
    public ResultBean<PartnerAccountVO> listAccount(@RequestParam("employeeId") Long employeeId)
    {
        return partnerService.listAccount(employeeId);
    }

    /**
     * 员工所属合伙人被授权的银行列表
     *
     * @param employeeId
     * @return
     */
    @GetMapping(value = "/listBank")
    public ResultBean<Set<String>> listBank(@RequestParam(value = "employeeId", required = false) Long employeeId,
                                            @RequestParam(value = "partnerId", required = false) Long partnerId) {
        return partnerService.listBank(employeeId, partnerId);
    }

    @PostMapping(value = "/updatearea")
    public ResultBean modifyPartnerArea(@RequestBody PartnerParam partnerParam) {
        return partnerService.updatePartnerArea(partnerParam);
    }
}
