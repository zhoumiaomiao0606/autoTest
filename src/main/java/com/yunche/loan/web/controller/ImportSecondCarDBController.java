package com.yunche.loan.web.controller;

import com.yunche.loan.config.constant.BaseExceptionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.AuthQuery;
import com.yunche.loan.domain.vo.CascadeVO;
import com.yunche.loan.domain.vo.PageVO;
import com.yunche.loan.service.AuthService;
import com.yunche.loan.service.ImportDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/29
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/import")
public class ImportSecondCarDBController
{
    @Resource
    private ImportDbService importDbService;

    @GetMapping("/db")
    public ResultBean db()
    {
        return importDbService.db();
    }
}
