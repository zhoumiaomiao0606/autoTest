package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BizAreaQuery;
import com.yunche.loan.domain.param.BizAreaParam;
import com.yunche.loan.domain.viewObj.AreaVO;
import com.yunche.loan.domain.viewObj.BizAreaVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.service.BizAreaService;
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
 * @date 2018/1/19
 */
@CrossOrigin
@RestController
@RequestMapping("/biz/area")
public class BizAreaController {

    private static final Logger logger = LoggerFactory.getLogger(BizAreaController.class);

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
        logger.info(Arrays.asList("create", JSON.toJSONString(bizAreaParam)).stream().collect(Collectors.joining("-")));
        return bizAreaService.create(bizAreaParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody BizAreaParam bizAreaParam) {
        logger.info(Arrays.asList("update", JSON.toJSONString(bizAreaParam)).stream().collect(Collectors.joining("-")));
        return bizAreaService.update(bizAreaParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
        return bizAreaService.delete(id);
    }

    /**
     * 删除关联的城市
     *
     * @param id
     * @param areaId
     * @return
     */
    @GetMapping(value = "/delete/relaArea")
    public ResultBean<Void> deleteRelaArea(@RequestParam("id") Long id,
                                           @RequestParam("areaId") Long areaId) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id), JSON.toJSONString(areaId)).stream().collect(Collectors.joining("-")));
        return bizAreaService.deleteRelaArea(id, areaId);
    }

    /**
     * 根据ID获取详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public ResultBean<BizAreaVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("-")));
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
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return bizAreaService.query(query);
    }

    /**
     * 当前业务区域所覆盖的城市列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listCity", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AreaVO.Prov>> listCity(@RequestBody BizAreaQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("-")));
        return bizAreaService.listCity(query);
    }

    /**
     * 获取所有业务区域对象  -级联列表展示
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ResultBean<List<LevelVO>> listAll() {
        logger.info("list");
        return bizAreaService.listAll();
    }

}
