package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.EmployeeQuery;
import com.yunche.loan.domain.query.UserGroupQuery;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/user/group")
public class UserGroupController {

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
        return userGroupService.create(userGroupParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody UserGroupParam userGroupParam) {
        return userGroupService.update(userGroupParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
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
        return userGroupService.unbindAuth(id, authIds, type);
    }

    /**
     * 修改用户组关联的区域
     * @param userGroupParam
     * @return
     */
    @PostMapping(value = "/updatearea")
    public ResultBean<Void> modifyArea(@RequestBody UserGroupParam userGroupParam){

        return userGroupService.updateUserArea(userGroupParam);
    }


    /**
     * 修改用户组关联的银行
     * @param userGroupParam
     * @return
     */
    @PostMapping(value = "/updatebank")
    public ResultBean<Void> modifybank(@RequestBody UserGroupParam userGroupParam){
        return userGroupService.updateUserBank(userGroupParam);
    }
}
