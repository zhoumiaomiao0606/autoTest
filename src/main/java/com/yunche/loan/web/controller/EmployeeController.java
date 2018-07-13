package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.param.EmployeeParam;
import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.vo.EmployeeVO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.LoginVO;
import com.yunche.loan.domain.vo.UserGroupVO;
import com.yunche.loan.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody EmployeeParam employeeParam) {
        return employeeService.create(employeeParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody EmployeeDO employeeDO) {
        return employeeService.update(employeeDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return employeeService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<EmployeeVO> getById(@RequestParam("id") Long id) {
        return employeeService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<EmployeeVO>> query(@RequestBody EmployeeQuery query) {
        return employeeService.query(query);
    }

    /**
     * 员工-级联列表
     *
     * @return
     */
//    @RequiresPermissions("/employee/list")
    @GetMapping("/list")
    public ResultBean<List<CascadeVO>> listAll() {
        return employeeService.listAll();
    }

    /**
     * 职位列表
     *
     * @return
     */
    @GetMapping("/listTitle")
    public ResultBean<List<String>> listTitle() {
        return employeeService.listTitle();
    }

    /**
     * 获取当前用户已绑定的用户组(角色)列表   -分页查询
     *
     * @param query 支持area_id筛选
     * @return
     */
    @PostMapping(value = "/listUserGroup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<UserGroupVO>> UserGroup(@RequestBody RelaQuery query) {
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
        return employeeService.unbindUserGroup(id, userGroupIds);
    }

    /**
     * 用户登录
     *
     * @param employeeParam
     * @return
     */
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<LoginVO> login(HttpServletRequest request,
                                     HttpServletResponse response,
                                     @RequestBody EmployeeParam employeeParam) {
        return employeeService.login(request, response, employeeParam);
    }

    @GetMapping(value = "/login_")
    public ResultBean<LoginVO> login_(HttpServletRequest request,
                                      HttpServletResponse response,
                                      @RequestParam String username,
                                      @RequestParam String password) {
        EmployeeParam employeeParam = new EmployeeParam();
        employeeParam.setUsername(username);
        employeeParam.setPassword(password);
        return employeeService.login(request, response, employeeParam);
    }

    /**
     * 用户登出
     *
     * @return
     */
    @Limiter(route = "/api/v1/employee/logout")
    @GetMapping("/logout")
    public ResultBean<Void> logout() {
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
        return employeeService.editPassword(employeeParam);
    }

    /**
     * 重置密码
     *
     * @param id 邮箱找回
     * @return
     */
    @GetMapping(value = "/password/reset")
    public ResultBean<Void> resetPassword(@RequestParam("email") Long id) {
        return employeeService.resetPassword(id);
    }
}
