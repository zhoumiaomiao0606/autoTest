package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.constant.ExpressCompanyEnum;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessApprovalConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.LoanBankCardSendExpParam;
import com.yunche.loan.domain.vo.UniversalBankCardSendVO;
import com.yunche.loan.mapper.LoanBankCardSendDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanBankCardSendService;
import com.yunche.loan.service.LoanProcessService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Service
public class LoanBankCardSendServiceImpl implements LoanBankCardSendService {

    @Autowired
    private LoanBankCardSendDOMapper loanBankCardSendDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanProcessService loanProcessService;


    @Override
    public ResultBean<Void> save(LoanBankCardSendDO loanBankCardSendDO) {
        Preconditions.checkNotNull(loanBankCardSendDO.getOrderId(), "订单号不能为空");


        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(loanBankCardSendDO.getOrderId());
        if (orderDO != null) {
            LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(orderDO.getLoanCustomerId(), BaseConst.VALID_STATUS);
            customerDO.setBankCardTransmitAddress(loanBankCardSendDO.getExpressSendAddress());
            loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
        }
        LoanBankCardSendDO existDO = loanBankCardSendDOMapper.selectByPrimaryKey(loanBankCardSendDO.getOrderId());

        if (null == existDO) {
            // create
            loanBankCardSendDO.setGmtCreate(new Date());
            loanBankCardSendDO.setGmtModify(new Date());
            int count = loanBankCardSendDOMapper.insertSelective(loanBankCardSendDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            loanBankCardSendDO.setGmtModify(new Date());
            int count = loanBankCardSendDOMapper.updateByPrimaryKeySelective(loanBankCardSendDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    @Override
    public ResultBean<UniversalBankCardSendVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        UniversalBankCardSendVO universalBankCardSendVO = loanQueryDOMapper.selectUniversalBankCardSend(orderId);

        return ResultBean.ofSuccess(universalBankCardSendVO);
    }

    @Override
    public ResultBean<Integer> imp(String ossKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(ossKey), "ossKey不能为空");

        // 收集数据
        List<LoanBankCardSendDO> loanBankCardSendDOList = Lists.newArrayList();

        try {

            // readFile
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, ossKey);


            for (int i = 0; i < rowList.size(); i++) {

                // 当前行数
                int rowNum = i + 1;

                // 当前行数据
                String[] row = rowList.get(i);

                // 空行跳过
                if (ArrayUtils.isEmpty(row)) {
                    continue;
                }

                // 列
                int line = 0;
                // 当前行，具体列val
                String rowVal = "";
                int num =loanBankCardSendDOMapper.countByorderId(Long.valueOf(row[0]));
                if(num == 0){
                    LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(row[0]));
                    if (orderDO != null) {
                        LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(orderDO.getLoanCustomerId(), BaseConst.VALID_STATUS);
                        customerDO.setBankCardTransmitAddress(row[3]);
                        loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
                    }
                    LoanBankCardSendDO loanBankCardSendDO = new LoanBankCardSendDO();

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setOrderId(Long.valueOf(rowVal));
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setCardholderName(rowVal);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setCardholderPhone(rowVal);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setExpressSendAddress(rowVal);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setRepayCardNum(rowVal);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setExpressCom(ExpressCompanyEnum.getKeyByValue(rowVal));
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setExpressSendNum(rowVal);
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    try {
                        rowVal = row[line++];
                        loanBankCardSendDO.setExpressSendDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(rowVal));
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                    }

                    loanBankCardSendDO.setGmtCreate(new Date());
                    loanBankCardSendDO.setGmtModify(new Date());

                    loanBankCardSendDOList.add(loanBankCardSendDO);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<LoanBankCardSendDO> insertList = new ArrayList<>();
        for(LoanBankCardSendDO loanBankCardSendDO:loanBankCardSendDOList){
            LoanBankCardSendDO l = loanBankCardSendDOMapper.selectByPrimaryKey(loanBankCardSendDO.getOrderId());
            if(l == null){
                insertList.add(loanBankCardSendDO);
            }else{
                int num =loanBankCardSendDOMapper.countByorderId(loanBankCardSendDO.getOrderId());
                if(num == 0){
                    loanBankCardSendDOMapper.updateByPrimaryKeySelective(loanBankCardSendDO);
                    ApprovalParam approvalParam = new ApprovalParam();
                    approvalParam.setOrderId(loanBankCardSendDO.getOrderId());
                    approvalParam.setTaskDefinitionKey(LoanProcessEnum.BANK_CARD_SEND.getCode());
                    approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
                    approvalParam.setNeedLog(true);
                    approvalParam.setCheckPermission(false);
                    ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
                    Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
                }

            }
        }
        // batchInsert
        int count = batchInsert(insertList);
        for(LoanBankCardSendDO loanBankCardSendDO:insertList){
            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setOrderId(loanBankCardSendDO.getOrderId());
            approvalParam.setTaskDefinitionKey(LoanProcessEnum.BANK_CARD_SEND.getCode());
            approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
            approvalParam.setNeedLog(true);
            approvalParam.setCheckPermission(false);
            ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
            Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());

        }

        return ResultBean.ofSuccess(count, "导入成功");
    }

    @Override
    public String export(LoanBankCardSendExpParam loanBankCardSendExpParam) {
        List<LoanBankCardSendDO> list = loanBankCardSendDOMapper.loanBankCardSendExp(loanBankCardSendExpParam);

        return createTelBankExcelFile(list);
    }

    private String createTelBankExcelFile(List<LoanBankCardSendDO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "银行卡寄送" + timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        try {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("业务编号","收卡人姓名","收卡人电话","收卡人地址",
                    "还款卡号","邮寄公司","邮寄单号","邮寄日期(yyyy-MM-dd)"
            );
            //申请单号	客户名称	证件类型	证件号	业务员	合伙人团队	贷款金额	gps数量	申请单状态	提交状态	备注	审核员	审核时间
            XSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            XSSFRow row = null;
            XSSFCell cell = null;
            for (int i = 0; i < list.size(); i++) {
                LoanBankCardSendDO loanBankCardSendDO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(loanBankCardSendDO.getOrderId()+"");

                cell = row.createCell(1);
                cell.setCellValue(loanBankCardSendDO.getCardholderName());
                //

                cell = row.createCell(2);
                cell.setCellValue(loanBankCardSendDO.getCardholderPhone());

                cell = row.createCell(3);
                cell.setCellValue(loanBankCardSendDO.getExpressSendAddress());

                cell = row.createCell(4);
                cell.setCellValue(loanBankCardSendDO.getRepayCardNum());

                cell = row.createCell(5);
                cell.setCellValue(ExpressCompanyEnum.getValueByKey(loanBankCardSendDO.getExpressCom()));

                cell = row.createCell(6);
                cell.setCellValue(loanBankCardSendDO.getExpressSendNum());

                cell = row.createCell(7);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                if(loanBankCardSendDO.getExpressSendDate() == null){
                    cell.setCellValue("");
                }else{
                    cell.setCellValue(sdf.format(loanBankCardSendDO.getExpressSendDate()));
                }

            }
            //文件宽度自适应
            sheet.autoSizeColumn((short) 0);
            sheet.autoSizeColumn((short) 1);
            sheet.autoSizeColumn((short) 2);
            sheet.autoSizeColumn((short) 3);
            sheet.autoSizeColumn((short) 4);
            sheet.autoSizeColumn((short) 5);
            sheet.autoSizeColumn((short) 6);
            sheet.autoSizeColumn((short) 7);

            workbook.write(out);
            //上传OSS
            OSSClient ossClient = OSSUnit.getOSSClient();
            String bucketName = ossConfig.getBucketName();
            String diskName = ossConfig.getDownLoadDiskName();
            OSSUnit.deleteFile(ossClient, bucketName, diskName + File.separator, fileName);
            OSSUnit.uploadObject2OSS(ossClient, file, bucketName, diskName + File.separator);
        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Preconditions.checkArgument(false, e.getMessage());
            }
        }
        return ossConfig.getDownLoadDiskName() + File.separator + fileName;
    }



    /**
     * 批量导入
     *
     * @param loanBankCardSendDOList
     * @return
     */
    private int batchInsert(List<LoanBankCardSendDO> loanBankCardSendDOList) {

        if (CollectionUtils.isEmpty(loanBankCardSendDOList)) {
            return 0;
        }

        // batchInsert
        int count = loanBankCardSendDOMapper.batchInsert(loanBankCardSendDOList);
        return count;
    }
}
