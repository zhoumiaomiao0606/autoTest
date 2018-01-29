package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.RelaQuery;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.BaseVO;
import com.yunche.loan.domain.viewObj.LevelVO;
import com.yunche.loan.domain.viewObj.PageVO;
import com.yunche.loan.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    /**
     * 权限资源实体-级联列表
     * <p>
     * 菜单-页面-操作 级联列表
     *
     * @return
     */
    @GetMapping("/list")
    public ResultBean<AuthVO> listAuth() {
        logger.info("list");
        return authService.listAuth();
    }

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
     * 页面分页条件查询
     *
     * @return 菜单ID
     */
    @PostMapping("/listPage")
    public ResultBean<Object> listPage(@RequestBody RelaQuery query) {
        logger.info("listPage");
        return authService.listPage(query);
    }

//    @GetMapping("/listOperation")
//    public ResultBean<List<BaseVO>> listOperation() {
//        logger.info("listOperation");
//        return authService.listOperation();
//    }


}
