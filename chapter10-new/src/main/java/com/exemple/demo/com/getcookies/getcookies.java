package com.exemple.demo.com.getcookies;

import com.beust.jcommander.Parameter;
import com.exemple.demo.com.bean.user;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Api(value = "/",description = "这是我全部的get")
public class getcookies {
    @ApiOperation(value = "这是返回中有cookies")
    @RequestMapping(value = "/getcookieszmm",method = RequestMethod.GET)
    public String getwithcookies(HttpServletResponse response){
      //  HttpServletRequest  装请求头
        //HttpServletResponce 装响应头
        Cookie cookie= new Cookie("login","true");
        response.addCookie(cookie);
        return "恭喜你测试成功";
    }
@ApiOperation(value = "这是访问携带参数")
@RequestMapping(value = "/gitwithcookies",method = RequestMethod.GET)
    public String getwithcookies(HttpServletRequest request){
        Cookie[] cookies=request.getCookies();

    if(Objects.isNull(cookies)) return "必须要携带cookies";


        for(Cookie cookie :cookies){


            if(cookie.getName().equals("login")&&cookie.getValue().equals("true")){
return "携带cookie访问成功";
            }
        }
      return "必须携带cookie";
}
//两种需要携带参数访问
    @ApiOperation(value = "第一种携带参数")
    @RequestMapping(value = "/get/with/param",method =RequestMethod.GET)
    public Map<String,Integer>  testparam(@RequestParam  Integer start,
                                          @RequestParam Integer end
                                          ){
        Map<String,Integer> map =new HashMap<>();
        map.put("方便面",10);
        map.put("下拉条",10);
        map.put("好吃",20);
        return  map;

    }
    //第二种方式
    @ApiOperation(value = "第二种携带参数")
    @RequestMapping(value = "/test/with/param1/start}/{end}")
    public Map<String,Integer>  testparam1(@PathVariable Integer start,
                                          @PathVariable Integer end
    ){
        Map<String,Integer> map1 =new HashMap<>();
        map1.put("方便面",10);
        map1.put("下拉条",10);
        map1.put("好吃",20);
        return  map1;

    }

    @RequestMapping(value = "/postwithcookies",method = RequestMethod.POST)
    @ApiOperation(value = "登录成功后获取user信息",httpMethod = "post")
    public String  postwithcookies(HttpServletRequest request,
                                   @RequestBody user u){
user user1;
        Cookie[] cookies=request.getCookies();

for(Cookie cookie:cookies){
    if(cookie.getName().equals("login")
    &&cookie.getValue().equals("true")
    && u.getUsername().equals("zhoumiaomiao")
    &&u.getPassword().equals("111111")){
         user1=new user();
        user1.setAge("18");
        user1.setName("不知道");
        user1.setPassword("666666");
        user1.setSex("nv");
        user1.setUsername("你猜");

        return user1.toString();

    }

}
return  "参数不合法";
    }


}
