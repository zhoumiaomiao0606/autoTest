package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.queryObj.AuthQuery;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.PageVO;
import com.yunche.loan.service.AuthService;
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
 * @date 2018/1/29
 */
@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;


//    /**
//     * 权限资源实体-级联列表
//     * <p>
//     * 菜单-页面-操作 级联列表
//     *
//     * @return
//     */
//    @GetMapping("/list")
//    public ResultBean<AuthVO> listAuth() {
//        logger.info("list");
//        return authService.listAuth();
//    }

    /**
     * 菜单-级联列表
     *
     * @return
     */
    @GetMapping("/listMenu")
    public ResultBean<List<LevelVO>> listMenu() {
        logger.info("listMenu");
        return authService.listMenu();
    }

    /**
     * 当前用户组 - 所有的operation权限列表 - 对应的(选中状态展示)  -分页条件查询
     * <p>
     * 可选条件：userGroupID、menuId、pageName、operationName、areaId
     *
     * @return
     */
    @PostMapping("/listOperation")
    public ResultBean<List<PageVO>> listOperation(@RequestBody AuthQuery query) {
        logger.info("listOperation");
        return authService.listOperation(query);
    }

//    @GetMapping("/listOperation")
//    public ResultBean<List<BaseVO>> listOperation() {
//        logger.info("listOperation");
//        return authService.listOperation();
//    }


    /**
     * 当前用户组 - 已绑定的operation权限列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listBindOperation", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<PageVO>> listBindOperation(@RequestBody AuthQuery query) {
        logger.info(Arrays.asList("listBindOperation", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return authService.listBindOperation(query);
    }

    /**
     * 当前用户组 - 未绑定的operation权限列表   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
    @PostMapping(value = "/listUnbindOperation", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<PageVO>> listUnbindOperation(@RequestBody AuthQuery query) {
        logger.info(Arrays.asList("listBindOperation", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
        return authService.listUnbindOperation(query);
    }

    /**
     * 当前用户组 - 所有的operation权限列表 - 对应的(选中状态展示)   -分页查询
     *
     * @param query id、pageIndex、pageSize
     * @return
     */
//    @PostMapping(value = "/listOperation", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultBean<List<PageVO>> listOperation(@RequestBody RelaQuery query) {
//        logger.info(Arrays.asList("listOperation", JSON.toJSONString(query)).stream().collect(Collectors.joining("\u0001")));
//        return authService.listOperation(query);
//    }
}
