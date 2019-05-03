package com.course.controller;

import com.course.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
@Api(value="v1",description = "my first version demo")
@RequestMapping("v1")
public class demo {
    //首先获取一个执行sql语句的对象
    @Autowired
    private SqlSessionTemplate template;

    @RequestMapping(value="/getUserCount",method= RequestMethod.GET)
    @ApiOperation(value="get user count",httpMethod ="GET" )
    public int getUserCount(){
       return template.selectOne("getUserCount");

    }

    @RequestMapping(value="/addUser",method=RequestMethod.POST)
    @ApiOperation(value = "插入数据",httpMethod = "post")
  public int adduser(@RequestBody User user){
       return template.insert("adduser",user);

    }

    @RequestMapping(value="/updateUser",method=RequestMethod.POST)
    public int updateUser(@RequestBody User user){

        return template.update("updateUser",user);
    }

    @RequestMapping(value="/deleteUser",method = RequestMethod.GET)
    public int deleteUser(@RequestParam int id){

        return template.delete("deleteUser",id);
    }
}
