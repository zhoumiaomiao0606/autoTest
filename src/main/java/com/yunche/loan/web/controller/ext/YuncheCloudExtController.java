package com.yunche.loan.web.controller.ext;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.LoginUserExtInfo;
import com.yunche.loan.service.YuncheCloudExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/12/4
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/ext/yunche-cloud")
public class YuncheCloudExtController {

    @Autowired
    private YuncheCloudExtService yuncheCloudExtService;


    @GetMapping(value = "/getLoginUserExtInfoByLoginUserId")
    public ResultBean<LoginUserExtInfo> getLoginUserExtInfoByLoginUserId(@RequestParam("loginUserId") Long loginUserId) {
        LoginUserExtInfo loginUserExtInfo = yuncheCloudExtService.getLoginUserExtInfoByLoginUserId(loginUserId);
        return ResultBean.ofSuccess(loginUserExtInfo);
    }
}
