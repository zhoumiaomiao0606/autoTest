package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.BizModelQuery;
import com.yunche.loan.domain.queryObj.PartnerQuery;
import com.yunche.loan.domain.queryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.PartnerDO;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.PartnerVO;
import com.yunche.loan.service.PartnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@CrossOrigin
@RestController
@RequestMapping("/partner")
public class PartnerController {

    private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);

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
        logger.info(Arrays.asList("create", JSON.toJSONString(partnerParam)).stream().collect(Collectors.joining("\u0001")));
        return partnerService.create(partnerParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody PartnerDO partnerDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(partnerDO)).stream().collect(Collectors.joining("\u0001")));
        return partnerService.update(partnerDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return partnerService.delete(id);
    }

    /**
     * 根据ID获取详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public ResultBean<PartnerVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
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
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
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
        logger.info(Arrays.asList("listBizModel", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
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
        logger.info(Arrays.asList("bindBizModel", JSON.toJSONString(id), JSON.toJSONString(bizModelIds)).stream().collect(Collectors.joining("\u0001")));
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
        logger.info(Arrays.asList("unbindBizModel", JSON.toJSONString(id), JSON.toJSONString(bizModelIds)).stream().collect(Collectors.joining("\u0001")));
        return partnerService.unbindBizModel(id, bizModelIds);
    }

    /**
     * 当前合伙人关联的业务产品列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listEmployee")
    public ResultBean<List<EmployeeVO>> listEmployee(@RequestBody RelaQuery query) {
        logger.info(Arrays.asList("listEmployee", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
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
        logger.info(Arrays.asList("bindEmployee", JSON.toJSONString(id), JSON.toJSONString(employeeIds)).stream().collect(Collectors.joining("\u0001")));
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
        logger.info(Arrays.asList("unbindEmployee", JSON.toJSONString(id), JSON.toJSONString(employeeIds)).stream().collect(Collectors.joining("\u0001")));
        return partnerService.unbindEmployee(id, employeeIds);
    }
}
