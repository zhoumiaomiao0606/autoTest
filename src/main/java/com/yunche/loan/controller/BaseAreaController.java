package com.yunche.loan.controller;

import com.yunche.loan.bo.BaseAreaBO;
import com.yunche.loan.result.ResultBOBean;
import com.yunche.loan.service.BaseAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@CrossOrigin
@RestController
@RequestMapping("/area")
public class BaseAreaController {

    private static final Logger logger = LoggerFactory.getLogger(BaseAreaController.class);

    @Autowired
    BaseAreaService baseAreaService;

    @GetMapping("/getById")
    public ResultBOBean<BaseAreaBO> getByid(@RequestParam("id") Integer id) {
        logger.info(Arrays.asList("getById", id.toString()).stream().collect(Collectors.joining("-")));
        return baseAreaService.getById(id);
    }

}
