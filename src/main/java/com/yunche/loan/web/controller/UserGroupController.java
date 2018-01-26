package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.viewObj.*;
import com.yunche.loan.service.UserGroupService;
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
@RequestMapping("/user/group")
public class UserGroupController {

    private static final Logger logger = LoggerFactory.getLogger(UserGroupController.class);

    @Autowired
    private UserGroupService userGroupService;


    /**
     * 创建用户组(角色)
     * <p>
     * 同时绑定权限列表
     * 同时绑定员工列表
     *
     * @param userGroupParam
     * @return
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody UserGroupParam userGroupParam) {
        logger.info(Arrays.asList("create", JSON.toJSONString(userGroupParam)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.create(userGroupParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody UserGroupParam userGroupParam) {
        logger.info(Arrays.asList("update", JSON.toJSONString(userGroupParam)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.update(userGroupParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.delete(id);
    }

    /**
     * 根据ID获取详情
     *
     * @param id
     * @return
     */
    @GetMapping("/getById")
    public ResultBean<UserGroupVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.getById(id);
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
    public ResultBean<List<UserGroupVO>> query(@RequestBody UserGroupQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.query(query);
    }

    /**
     * 当前用户组已绑定的员工列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listEmployee", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<EmployeeVO>> listEmployee(@RequestBody BaseQuery query) {
        logger.info(Arrays.asList("listEmployee", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.listEmployee(query);
    }

    /**
     * 当前用户组已绑定的权限列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listAuth", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AuthVO>> listAuth(@RequestBody BaseQuery query) {
        logger.info(Arrays.asList("listAuth", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.listAuth(query);
    }

    /**
     * 绑定员工列表      -支持列表
     *
     * @param id          用户组ID
     * @param employeeIds 员工ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/bindEmployee")
    public ResultBean<Void> bindEmployee(@RequestParam("id") Long id,
                                         @RequestParam("employeeIds") String employeeIds) {
        logger.info(Arrays.asList("bindEmployee", JSON.toJSONString(id), JSON.toJSONString(employeeIds)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.bindEmployee(id, employeeIds);
    }

    /**
     * 解绑员工列表      -支持列表
     *
     * @param id          用户组ID
     * @param employeeIds 员工ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/unbindEmployee")
    public ResultBean<Void> unbindEmployee(@RequestParam("id") Long id,
                                           @RequestParam("employeeIds") String employeeIds) {
        logger.info(Arrays.asList("unbindEmployee", JSON.toJSONString(id), JSON.toJSONString(employeeIds)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.unbindEmployee(id, employeeIds);
    }

    /**
     * 绑定权限列表      -支持列表
     *
     * @param id      用户组ID
     * @param authIds 权限ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/bindAuth")
    public ResultBean<Void> bindAuth(@RequestParam("id") Long id,
                                     @RequestParam("authIds") String authIds) {
        logger.info(Arrays.asList("bindAuth", JSON.toJSONString(id), JSON.toJSONString(authIds)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.bindAuth(id, authIds);
    }

    /**
     * 解绑权限列表      -支持列表
     *
     * @param id      用户组ID
     * @param authIds 权限ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/unbindAuth")
    public ResultBean<Void> unbindAuth(@RequestParam("id") Long id,
                                       @RequestParam("authIds") String authIds) {
        logger.info(Arrays.asList("unbindAuth", JSON.toJSONString(id), JSON.toJSONString(authIds)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.unbindAuth(id, authIds);
    }
}
