package com.yunche.loan.web.controller;

import com.yunche.loan.config.constant.BaseExceptionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AuthQuery;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.PageVO;
import com.yunche.loan.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/29
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

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
    public ResultBean<List<CascadeVO>> listMenu() {
        return authService.listMenu();
    }


    @GetMapping("/listMenu_")
    public ResultBean<Map<String, Boolean>> listMenu_() {
        return authService.listMenu_();
    }

    @GetMapping("/listreport")
    public ResultBean<Map<String, Boolean>> listReport() {
        return authService.listReport();
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
        return authService.listUnbindOperation(query);
    }

    /**
     * 未登录
     * 会话失效后，重定向的URL  >> 写回错误信息
     *
     * @return
     */
    @GetMapping("/notLogin")
    public ResultBean<Void> notLogin() {
        return ResultBean.ofError(BaseExceptionEnum.NOT_LOGIN);
    }

    /**
     * 无权限
     * 校验无授权后，重定向的URL  >> 写回错误信息
     *
     * @return
     */
    @GetMapping("/notPermission")
    public ResultBean<Void> notPermission() {
        return ResultBean.ofError(BaseExceptionEnum.NOT_PERMISSION);
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
