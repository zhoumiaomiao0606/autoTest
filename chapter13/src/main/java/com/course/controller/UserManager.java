package com.course.controller;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j
@RestController
@Api(value="v1",description = "用户管理系统")
@RequestMapping("v1")
public class UserManager {
}
