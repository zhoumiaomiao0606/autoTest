package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.queryObj.EmployeeQuery;
import com.yunche.loan.domain.dataObj.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.CascadeVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
import com.yunche.loan.service.EmployeeService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * @date 2018/1/23
 */
@CrossOrigin
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody EmployeeParam employeeParam) {
        logger.info(Arrays.asList("create", JSON.toJSONString(employeeParam)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.create(employeeParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody EmployeeDO employeeDO) {
        logger.info(Arrays.asList("update", JSON.toJSONString(employeeDO)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.update(employeeDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("delete", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<EmployeeVO> getById(@RequestParam("id") Long id) {
        logger.info(Arrays.asList("getById", JSON.toJSONString(id)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<EmployeeVO>> query(@RequestBody EmployeeQuery query) {
        logger.info(Arrays.asList("query", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.query(query);
    }

    /**
     * 员工-级联列表
     *
     * @return
     */
    @RequiresPermissions("testPermission")
    @GetMapping("/list")
    public ResultBean<List<CascadeVO>> listAll() {
        logger.info("list");
        return employeeService.listAll();
    }

    /**
     * 职位列表
     *
     * @return
     */
    @GetMapping("/listTitle")
    public ResultBean<List<String>> listTitle() {
        logger.info("listTitle");
        return employeeService.listTitle();
    }

    /**
     * 获取当前用户已绑定的用户组(角色)列表   -分页查询
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/listUserGroup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<UserGroupVO>> UserGroup(@RequestBody BaseQuery query) {
        logger.info(Arrays.asList("listUserGroup", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.listUserGroup(query);
    }

    /**
     * 绑定用户组列表      -支持列表
     *
     * @param id           用户ID
     * @param userGroupIds 用户组ID列表 逗号分隔
     * @return
     */
    @GetMapping(value = "/bindUserGroup")
    public ResultBean<Void> bindUserGroup(@RequestParam("id") Long id,
                                          @RequestParam("userGroupIds") String userGroupIds) {
        logger.info(Arrays.asList("bindUserGroup", JSON.toJSONString(id), JSON.toJSONString(userGroupIds)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.bindUserGroup(id, userGroupIds);
    }

    /**
     * 解绑用户组列表      -支持列表
     *
     * @param id           用户ID
     * @param userGroupIds 用户组ID列表  逗号分隔
     * @return
     */
    @GetMapping(value = "/unbindUserGroup")
    public ResultBean<Void> unbindUserGroup(@RequestParam("id") Long id,
                                            @RequestParam("userGroupIds") String userGroupIds) {
        logger.info(Arrays.asList("unbindUserGroup", JSON.toJSONString(id), JSON.toJSONString(userGroupIds)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.unbindUserGroup(id, userGroupIds);
    }

    /**
     * 用户登录
     *
     * @param employeeDO
     * @return
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> login(@RequestBody EmployeeDO employeeDO) {
        logger.info(Arrays.asList("login", JSON.toJSONString(employeeDO)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.login(employeeDO);
    }

    /**
     * 用户登出
     *
     * @return
     */
    @GetMapping("/logout")
    public ResultBean<Void> logout() {
        logger.info("logout");
        return employeeService.logout();
    }

    /**
     * 修改密码
     *
     * @param employeeParam
     * @return
     */
    @PostMapping(value = "/password/edit", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> editPassword(@RequestBody EmployeeParam employeeParam) {
        logger.info(Arrays.asList("/password/edit", JSON.toJSONString(employeeParam)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.editPassword(employeeParam);
    }

    /**
     * 找回密码
     *
     * @param email 邮箱找回
     * @return
     */
    @GetMapping(value = "/password/reset")
    public ResultBean<Void> resetPassword(@RequestParam("email") String email) {
        logger.info(Arrays.asList("/password/reset", JSON.toJSONString(email)).stream().collect(Collectors.joining("\u0001")));
        return employeeService.resetPassword(email);
    }
}
