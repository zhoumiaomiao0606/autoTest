package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.EmployeeQuery;
import com.yunche.loan.domain.queryObj.UserGroupQuery;
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
    @PostMapping(value = "/listBindEmployee", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<EmployeeVO>> listEmployee(@RequestBody EmployeeQuery query) {
        logger.info(Arrays.asList("listBindEmployee", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.listBindEmployee(query);
    }

    /**
     * 当前用户组未绑定的员工列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listUnbindEmployee", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<EmployeeVO>> listUnbindEmployee(@RequestBody EmployeeQuery query) {
        logger.info(Arrays.asList("listUnbindEmployee", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.listUnbindEmployee(query);
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
     * 编辑权限列表（以当前authIds为准，全量更新）      -支持列表
     *
     * @param id      用户组ID
     * @param areaId  限定权限使用的业务区域(城市)ID
     *                <p>
     *                替换所有的areaId
     *                <p>
     *                需求：所有的权限都只限制在当前的城市下！！！全部权限绑定同一个城市，故以当前areaId为准！！！
     * @param authIds 权限ID列表 逗号分隔
     * @param type    权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     * @return
     */
    @GetMapping(value = "/editAuth")
    public ResultBean<Void> editAuth(@RequestParam("id") Long id,
                                     @RequestParam("areaId") Long areaId,
                                     @RequestParam("authIds") String authIds,
                                     @RequestParam("type") Byte type) {
        logger.info(Arrays.asList("editAuth", JSON.toJSONString(id), JSON.toJSONString(authIds), JSON.toJSONString(type)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.editAuth(id, areaId, authIds, type);
    }

    /**
     * 绑定权限列表（仅绑定新增权限）      -支持列表
     *
     * @param id      用户组ID
     * @param areaId  限定权限使用的业务区域(城市)ID
     *                <p>
     *                替换所有的areaId
     *                <p>
     *                需求：所有的权限都只限制在当前的城市下！！！全部权限绑定同一个城市，故以当前areaId为准！！！
     * @param authIds 权限ID列表 逗号分隔
     * @param type    权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     * @return
     */
    @GetMapping(value = "/bindAuth")
    public ResultBean<Void> bindAuth(@RequestParam("id") Long id,
                                     @RequestParam("areaId") Long areaId,
                                     @RequestParam("authIds") String authIds,
                                     @RequestParam("type") Byte type) {
        logger.info(Arrays.asList("bindAuth", JSON.toJSONString(id), JSON.toJSONString(authIds), JSON.toJSONString(type)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.bindAuth(id, areaId, authIds, type);
    }

    /**
     * 解绑权限列表      -支持列表
     *
     * @param id      用户组ID
     * @param authIds 权限ID列表 逗号分隔
     * @param type    权限类型  1:MENU; 2:PAGE; 3:OPERATION;
     * @return
     */
    @GetMapping(value = "/unbindAuth")
    public ResultBean<Void> unbindAuth(@RequestParam("id") Long id,
                                       @RequestParam("authIds") String authIds,
                                       @RequestParam("type") Byte type) {
        logger.info(Arrays.asList("unbindAuth", JSON.toJSONString(id), JSON.toJSONString(authIds), JSON.toJSONString(type)).stream().collect(Collectors.joining("\u0001")));
        return userGroupService.unbindAuth(id, authIds, type);
    }
}
