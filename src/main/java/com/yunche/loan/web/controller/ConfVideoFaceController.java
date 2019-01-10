package com.yunche.loan.web.controller;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfVideoFaceArtificialDO;
import com.yunche.loan.domain.query.ConfVideoFaceMachineQuery;
import com.yunche.loan.domain.vo.MachineVideoFaceVO;
import com.yunche.loan.domain.vo.ConfVideoFaceVO;
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
@RequestMapping({"/api/v1/conf/videoFace", "/api/v1/app/conf/videoFace"})
@ApiOperation("视频面签设置")
public class ConfVideoFaceController {

    @Autowired
    private ConfVideoFaceService confVideoFaceService;


    @ApiOperation("人工面签设置编辑")
    @PostMapping(value = "/artificial/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> artificialUpdate(@RequestBody ConfVideoFaceArtificialDO confVideoFaceArtificialDO) {
        confVideoFaceService.artificialUpdate(confVideoFaceArtificialDO);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    @ApiOperation("人工面签设置详情")
    @GetMapping("/artificial/detail")
    public ResultBean<ConfVideoFaceArtificialDO> artificialDetail(@RequestParam Long bankId) {
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
    public ResultBean<List<MachineVideoFaceVO>> listMachine(@RequestBody ConfVideoFaceMachineQuery query) {
        PageInfo<MachineVideoFaceVO> pageInfo = confVideoFaceService.listMachine(query);
        return ResultBean.ofPageInfo(pageInfo);
    }

    @ApiOperation(value = "总面签设置详情", notes = "包含机器/人工面签设置详情")
    @GetMapping("/detail")
    public ResultBean<ConfVideoFaceVO> detail(@RequestParam Long bankId,
                                              @RequestParam Long partnerId) {
        return ResultBean.ofSuccess(confVideoFaceService.detail(bankId, partnerId));
    }
}
