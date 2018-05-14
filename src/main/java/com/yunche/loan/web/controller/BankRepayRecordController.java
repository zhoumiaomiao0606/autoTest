package com.yunche.loan.web.controller;


import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankRepayImpRecordDO;
import com.yunche.loan.mapper.BankRepayImpRecordDOMapper;
import com.yunche.loan.service.BankRepayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/repaymentRecord")
public class BankRepayRecordController {

    @Autowired
    BankRepayRecordService bankRepayRecordService;

    @Autowired
    BankRepayImpRecordDOMapper bankRepayImpRecordDOMapper;

    @GetMapping(value = "/list")
    public ResultBean query(@RequestParam("pageIndex") Integer pageIndex, @RequestParam("pageSize") Integer pageSize,
                            @RequestParam(value = "fileName",required = false) String fileName,
                            @RequestParam(value = "startDate",required = false)String startDate,
                            @RequestParam(value = "endDate",required = false)String endDate) {
       return bankRepayRecordService.batchFileList(pageIndex,pageSize,fileName,startDate,endDate);
    }

    @GetMapping(value = "/imp")
    public ResultBean importFile(@RequestParam("key") String ossKey){
      return bankRepayRecordService.importFile(ossKey);

    }

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("pageIndex") Integer pageIndex,
                             @RequestParam("pageSize") Integer pageSizes,
                             @RequestParam("bankRepayImpRecordId") Long bankRepayImpRecordId,
                             @RequestParam(value = "userName",required = false) String userName,
                             @RequestParam(value = "idCard",required = false) String idCard,
                             @RequestParam(value = "isCustomer",required = false) Byte isCustomer) {
        Preconditions.checkNotNull(bankRepayImpRecordId,"批次号不能为空");
        return bankRepayRecordService.detail(pageIndex,pageSizes,bankRepayImpRecordId,userName,idCard,isCustomer);
    }

    @GetMapping(value = "/download")
    public ResultBean download(@RequestParam("bankRepayImpRecordId")Long bankRepayImpRecordId){
        Preconditions.checkNotNull(bankRepayImpRecordId,"批次号不能为空");
        BankRepayImpRecordDO impRecordDO = bankRepayImpRecordDOMapper.selectByPrimaryKey(bankRepayImpRecordId);
        return ResultBean.ofSuccess(impRecordDO);
    }


//    @InitBinder
//    protected void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"), true));
//    }


}
