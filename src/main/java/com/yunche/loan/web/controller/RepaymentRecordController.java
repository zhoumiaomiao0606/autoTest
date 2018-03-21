package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.service.RepaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/repaymentRecord")
public class RepaymentRecordController {

    @Autowired
    RepaymentRecordService repaymentRecordService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("idCard") String idCard,@RequestParam("repayCardId") String repayCardId,@RequestParam("currentOverdueTimes") Integer currentOverdueTimes ) {
        RepaymentRecordDOKey repaymentRecordDOKey=new RepaymentRecordDOKey();
        repaymentRecordDOKey.setIdCard(idCard);
        repaymentRecordDOKey.setRepayCardId(repayCardId);
        repaymentRecordDOKey.setCurrentOverdueTimes(currentOverdueTimes);

        return repaymentRecordService.query(repaymentRecordDOKey);
    }

    @GetMapping(value = "/imp")
    public ResultBean importFile(@RequestParam("filePathName") String filePathName){

      return repaymentRecordService.importFile(filePathName);

    }


}
