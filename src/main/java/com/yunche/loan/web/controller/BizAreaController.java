package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BizAreaQuery;
import com.yunche.loan.domain.entity.BizAreaDO;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.domain.vo.BizAreaVO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.service.BizAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/biz/area")
public class BizAreaController {

    @Autowired
    private BizAreaService bizAreaService;


    /**
     * 创建业务区域
     * <p>
     * 同时绑定城市列表
     *
     * @param bizAreaParam
     * @return
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody BizAreaParam bizAreaParam) {
        return bizAreaService.create(bizAreaParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return bizAreaService.delete(id);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody BizAreaDO bizAreaDO) {
        return bizAreaService.update(bizAreaDO);
    }

    /**
     * 根据ID获取详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public ResultBean<BizAreaVO> getById(@RequestParam("id") Long id) {
        return bizAreaService.getById(id);
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
    public ResultBean<List<BizAreaVO>> query(@RequestBody BizAreaQuery query) {
        return bizAreaService.query(query);
    }

    /**
     * 获取所有业务区域对象  -级联列表展示
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ResultBean<List<CascadeVO>> listAll() {
        return bizAreaService.listAll();
    }

    /**
     * 当前业务区域所覆盖的城市列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listArea", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<CascadeAreaVO.Prov>> listArea(@RequestBody BizAreaQuery query) {
        return bizAreaService.listArea(query);
    }

    /**
     * 绑定关联的城市列表
     *
     * @param id
     * @param areaIds
     * @return
     */
    @GetMapping(value = "/bindArea")
    public ResultBean<Void> bindArea(@RequestParam("id") Long id,
                                     @RequestParam("areaIds") String areaIds) {
        return bizAreaService.bindArea(id, areaIds);
    }

    /**
     * 解绑关联的城市列表
     *
     * @param id
     * @param areaIds
     * @return
     */
    @GetMapping(value = "/unbindArea")
    public ResultBean<Void> deleteRelaArea(@RequestParam("id") Long id,
                                           @RequestParam("areaIds") String areaIds) {
        return bizAreaService.unbindArea(id, areaIds);
    }


    /**
     * 关联合伙人列表
     *
     * @param  id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listPartner", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<CascadeAreaVO.Partner>> listPartner(@RequestParam Long id) {
        return bizAreaService.listPartner(id);
    }

    /**
     * 绑定关联的合伙人
     *
     * @param id
     * @param partnerId
     * @return
     */
    @GetMapping(value = "/bindPartner")
    public ResultBean<Void> bindPartner(@RequestParam("id") Long id,
                                     @RequestParam("partnerId") Long partnerId) {
        return null;
    }

    /**
     * 解绑关联的合伙人
     *
     * @param id
     * @param partnerId
     * @return
     */
    @GetMapping(value = "/unbindPartner")
    public ResultBean<Void> unbindPartner(@RequestParam("id") Long id,
                                           @RequestParam("partnerId") Long partnerId) {
        return null;
    }



}
