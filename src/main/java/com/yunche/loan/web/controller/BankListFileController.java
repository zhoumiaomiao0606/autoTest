package com.yunche.loan.web.controller;


import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankFileListDO;
import com.yunche.loan.mapper.BankFileListDOMapper;
import com.yunche.loan.service.BankListFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/bankfile")
public class BankListFileController {

    @Autowired
    BankListFileService bankListFileService;

    @Autowired
    BankFileListDOMapper bankFileListDOMapper;
    @GetMapping(value = "/list")
    public ResultBean query(@RequestParam("pageIndex") Integer pageIndex, @RequestParam("pageSize") Integer pageSize,
                            @RequestParam(value = "fileType")String fileType,
                            @RequestParam(value = "fileName",required = false) String fileName,
                            @RequestParam(value = "startDate",required = false)String startDate,
                            @RequestParam(value = "endDate",required = false)String endDate) {
        return bankListFileService.batchFileList(pageIndex,pageSize,fileName,startDate,endDate,fileType);
    }

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("pageIndex") Integer pageIndex,
                             @RequestParam("pageSize") Integer pageSizes,
                             @RequestParam("listId") Long listId,
                             @RequestParam(value = "userName",required = false) String userName,
                             @RequestParam(value = "idCard",required = false) String idCard,
                             @RequestParam(value = "isCustomer",required = false) Byte isCustomer) {
        Preconditions.checkNotNull(listId,"批次号不能为空");
        return bankListFileService.detail(pageIndex,pageSizes,listId,userName,idCard,isCustomer);
    }

    @GetMapping(value = "/download")
    public ResultBean download(@RequestParam("listId")Long listId){
        Preconditions.checkNotNull(listId,"批次号不能为空");
        BankFileListDO bankFileListDO = bankFileListDOMapper.selectByPrimaryKey(listId);
        Preconditions.checkNotNull(bankFileListDO,"文件不存在");
        Preconditions.checkArgument(bankFileListDO.getFileKey()!=null,"文件不存在");
        return ResultBean.ofSuccess(bankFileListDO);
    }

}
