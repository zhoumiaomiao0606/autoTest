package com.exemple.demo.com.getcookies;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(value = "/",description = "这是我的post请求")
@RequestMapping(value = "/v1")
public class postcookies {

    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ApiOperation(value = "登录成功后获取cookie信息",httpMethod ="post")
    public String login(HttpServletResponse response,
                        @RequestParam(value = "username",required = true)  String username,
                        @RequestParam(value = "password",required = true) String password){
        if(username.equals("zhoumiaomiao") && password.equals("111111")){
            Cookie cookie=new Cookie("login","true");
            response.addCookie(cookie);
            return "恭喜你登录成功";
        }else {
            return "登录失败";
        }

    }
}
