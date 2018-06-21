package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.TelephoneVerifyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TelephoneVerifyServiceImpl implements TelephoneVerifyService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanTelephoneVerifyDOMapper loanTelephoneVerifyDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {

        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for(UniversalCreditInfoVO universalCreditInfoVO:credits){
            if(!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())){
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId,Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setTelephone_des(loanTelephoneVerifyDOMapper.selectByPrimaryKey(orderId));
        recombinationVO.setCredits(credits);
        recombinationVO.setHome(loanQueryDOMapper.selectUniversalHomeVisitInfo(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_telephone_verify",orderId));
        recombinationVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderId(orderId));
        recombinationVO.setSupplement(loanQueryDOMapper.selectUniversalSupplementInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalMaterialRecord(orderId));
        recombinationVO.setCustomers(customers);

        return recombinationVO;
    }

    @Override
    public void update(TelephoneVerifyParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));

        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();

        if(loanOrderDO!=null){
            if(loanCarInfoId!=null){
                LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
                loanCarInfoDO.setId(loanCarInfoId);
                loanCarInfoDO.setGpsNum(StringUtils.isBlank(param.getCar_gps_num())?null:Integer.valueOf(param.getCar_gps_num()));
                loanCarInfoDO.setCarKey(StringUtils.isBlank(param.getCar_key())?null:new Byte(param.getCar_key()));
                loanCarInfoDOMapper.updateByPrimaryKeySelective(loanCarInfoDO);
            }
            if(loanFinancialPlanId!=null){
                if(StringUtils.isNotBlank(param.getFinancial_cash_deposit()) || StringUtils.isNotBlank(param.getFinancial_extra_fee())) {
                    LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
                    loanFinancialPlanDO.setId(loanFinancialPlanId);
                    loanFinancialPlanDO.setCashDeposit(StringUtils.isBlank(param.getFinancial_cash_deposit()) ? null : new BigDecimal(param.getFinancial_cash_deposit()));
                    loanFinancialPlanDO.setExtraFee(StringUtils.isBlank(param.getFinancial_extra_fee()) ? null : new BigDecimal(param.getFinancial_extra_fee()));
                    loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
                }
            }
        }
    }

    /**
     *  导出 EXCEL
     * @return
     */
    @Override
    public String export(String startDate,String endDate) {
        String ossResultKey = createExcelFile(startDate,endDate);
        return ossResultKey;
    }
    /**
     * 导出文件
     * @param startDate
     * @param endDate
     * @return
     */
    private String createExcelFile(String startDate,String endDate){

        String timestamp =  new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = timestamp+id+".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath()+File.separator+fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {

            out = new FileOutputStream(file);
            // TODO 开始 结束时间外部传入
            List<TelephoneVerifyNodeOrdersVO> list = loanStatementDOMapper.statisticsTelephoneVerifyNodeOrders(startDate, endDate);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();

            //申请单号、贷款金额、客户名称、证件类型、证件号、业务员名称、合伙人名称、gps数量、操作人、操作日期、提交状态、订单状态，备注
            ArrayList<String> header = Lists.newArrayList("申请单号","贷款金额","客户名称","证件类型","证件号",
                    "业务员名称","合伙人名称","gps数量","操作人","操作日期","提交状态","订单状态","备注"
            );

            XSSFRow headRow = sheet.createRow(0);
            for(int i=0;i<header.size();i++){
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            XSSFRow row =null;
            XSSFCell cell = null;
            for(int i=0;i<list.size();i++){
                TelephoneVerifyNodeOrdersVO telephoneVerifyNodeOrdersVO = list.get(i);
                //创建行
                row = sheet.createRow(i+1);

                cell = row.createCell(0);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOrder_id());

                cell= row.createCell(1);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getLoan_amount());

                cell= row.createCell(2);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCustomer_name());

                cell= row.createCell(3);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCustomer_card_type());

                cell= row.createCell(4);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCustomer_id_card());

                cell= row.createCell(5);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getSaleman_name());

                cell= row.createCell(6);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getPartner_name());

                cell= row.createCell(7);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getGps_number());

                cell= row.createCell(8);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOp_user_name());

                cell = row.createCell(9);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOp_time());

                cell = row.createCell(10);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCommit_status());

                cell = row.createCell(11);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getAction());

                cell = row.createCell(12);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOp_info());
            }
            //文件宽度自适应
            sheet.autoSizeColumn((short)0);
            sheet.autoSizeColumn((short)1);
            sheet.autoSizeColumn((short)2);
            sheet.autoSizeColumn((short)3);
            sheet.autoSizeColumn((short)4);
            sheet.autoSizeColumn((short)5);
            sheet.autoSizeColumn((short)6);
            sheet.autoSizeColumn((short)7);
            sheet.autoSizeColumn((short)8);
            sheet.autoSizeColumn((short)9);
            sheet.autoSizeColumn((short)10);
            sheet.autoSizeColumn((short)11);
            sheet.autoSizeColumn((short)12);

            workbook.write(out);
            //上传OSS
            OSSClient ossClient = OSSUnit.getOSSClient();
            String bucketName= ossConfig.getBucketName();
            String diskName = ossConfig.getDownLoadDiskName();
            OSSUnit.deleteFile(ossClient,bucketName,diskName+File.separator,fileName);
            OSSUnit.uploadObject2OSS(ossClient, file, bucketName, diskName + File.separator);
        } catch (Exception e) {
            Preconditions.checkArgument(false,e.getMessage());
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                Preconditions.checkArgument(false,e.getMessage());
            }
        }

        return ossConfig.getDownLoadDiskName()+File.separator+fileName;
    }



}
