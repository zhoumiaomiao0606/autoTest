package com.yunche.loan.web.controller;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfVideoFaceBankDO;
import com.yunche.loan.domain.query.ConfVideoFaceBankPartnerQuery;
import com.yunche.loan.domain.vo.ConfVideoFaceBankPartnerVO;
import com.yunche.loan.service.ConfVideoFaceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/1/3
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/conf/videoFace")
public class ConfVideoFaceController {

    @Autowired
    private ConfVideoFaceService confVideoFaceService;


    @ApiOperation("人工面签设置编辑")
    @PostMapping(value = "/artificial/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> artificialUpdate(@RequestBody ConfVideoFaceBankDO confVideoFaceBankDO) {
        confVideoFaceService.artificialUpdate(confVideoFaceBankDO);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    @ApiOperation("人工面签设置详情")
    @GetMapping("/artificial/detail")
    public ResultBean<ConfVideoFaceBankDO> artificialDetail(@RequestParam Long bankId) {
        return ResultBean.ofSuccess(confVideoFaceService.artificialDetail(bankId));
    }

    @ApiOperation("机器面签设置编辑")
    @GetMapping("/machine/update")
    public ResultBean<Void> machineUpdate(@RequestParam Long bankId,
                                          @RequestParam Long partnerId,
                                          @RequestParam Byte status) {
        confVideoFaceService.machineUpdate(bankId, partnerId, status);
        return ResultBean.ofSuccess();
    }

    @ApiOperation("机器面签设置列表详情")
    @PostMapping(value = "/machine/list", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<ConfVideoFaceBankPartnerVO>> listMachine(@RequestBody ConfVideoFaceBankPartnerQuery query) {
        PageInfo<ConfVideoFaceBankPartnerVO> pageInfo = confVideoFaceService.listMachine(query);
        return ResultBean.ofSuccess(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
