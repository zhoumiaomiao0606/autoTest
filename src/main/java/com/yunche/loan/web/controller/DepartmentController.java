package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.DepartmentQuery;
import com.yunche.loan.domain.entity.DepartmentDO;
import com.yunche.loan.domain.param.DepartmentParam;
import com.yunche.loan.domain.vo.DepartmentVO;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.UserGroupVO;
import com.yunche.loan.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;


    //    @RequiresPermissions("/department/create")
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody DepartmentParam departmentParam) {
        return departmentService.create(departmentParam);
    }

    //    @RequiresPermissions("/department/update")
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody DepartmentDO departmentDO) {
        return departmentService.update(departmentDO);
    }

    //    @RequiresPermissions("/department/delete")
    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long areaId) {
        return departmentService.delete(areaId);
    }

    //    @RequiresPermissions("/department/getById")
    @GetMapping("/getById")
    public ResultBean<DepartmentVO> getById(@RequestParam("id") Long id) {
        return departmentService.getById(id);
    }

    //    @RequiresPermissions("/department/query")
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<DepartmentVO>> query(@RequestBody DepartmentQuery query) {
        return departmentService.query(query);
    }

    /**
     * 部门级联列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResultBean<List<CascadeVO>> listAll() {
        return departmentService.listAll();
    }

    /**
     * 已绑定的用户组列表
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listUserGroup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<UserGroupVO>> listUserGroup(@RequestBody BaseQuery query) {
        return departmentService.listUserGroup(query);
    }

    /**
     * 绑定用户组列表
     *
     * @param id
     * @param userGroupIds
     * @return
     */
    @GetMapping("/bindUserGroup")
    public ResultBean<Void> bindUserGroup(@RequestParam("id") Long id,
                                          @RequestParam("userGroupIds") String userGroupIds) {
        return departmentService.bindUserGroup(id, userGroupIds);
    }

    /**
     * 解绑用户组列表
     *
     * @param id
     * @param userGroupIds
     * @return
     */
    @GetMapping("/unbindUserGroup")
    public ResultBean<Void> unbindUserGroup(@RequestParam("id") Long id,
                                            @RequestParam("userGroupIds") String userGroupIds) {
        return departmentService.unbindUserGroup(id, userGroupIds);
    }
}
