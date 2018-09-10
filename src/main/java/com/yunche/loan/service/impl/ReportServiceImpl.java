package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.CarTypeEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.ZhonganInfoDOMapper;
import com.yunche.loan.service.ReportService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private ZhonganInfoDOMapper zhonganInfoDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    //待垫款客户业务审批明细list
    @Override
    public ResultBean<List<BusinessApprovalReportVO>> businessApproval(BaseQuery query) {
        List<BusinessApprovalReportVO> list = zhonganInfoDOMapper.businessApproval(query);
        int totalNum = 0;
        if(list !=null){
            totalNum = list.size();
            return ResultBean.ofSuccess(list, totalNum, query.getPageIndex(), query.getPageSize());
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }
    //待垫款客户业务审批明细快捷统计
    @Override
    public BusinessApprovalReportTotalVO businessApprovalTotal(BaseQuery query) {
        return zhonganInfoDOMapper.businessApprovalTotal(query);
    }
    //待垫款客户业务审批明细导出
    @Override
    public String businessApprovalExport(BaseQuery query) {
        List<BusinessApprovalReportVO> list = zhonganInfoDOMapper.businessApprovalExport(query);
        return createExcelFile(list);
    }
    //合同套打list
    @Override
    public ResultBean<List<ContractSetReportVO>> contractSet(ContractSetQuery query) {
        List<ContractSetReportVO> list = zhonganInfoDOMapper.contractSet(query);
        int totalNum = 0;
        if(list !=null){
            totalNum = list.size();
            return ResultBean.ofSuccess(list, totalNum, query.getPageIndex(), query.getPageSize());
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ContractSetReportTotalVO contractSetTotal(ContractSetQuery query) {
        return zhonganInfoDOMapper.contractSetTotal(query);
    }

    @Override
    public String contractSetExport(ContractSetQuery query) {
        List<ContractSetReportVO> list = zhonganInfoDOMapper.contractSetExport(query);
        return createContractExcelFile(list);
    }
    //合同套打快捷统计

    /**
     * 导出文件
     *
     * @param
     * @return
     */
    private String createExcelFile(List<BusinessApprovalReportVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {

            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("主贷姓名", "身份证号", "业务员", "业务部门",
                    "经办人", "经办时间", "打款金额", "贷款金额", "执行利率%", "银行分期本金"
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
                BusinessApprovalReportVO businessApprovalReportVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(businessApprovalReportVO.getId());

                cell = row.createCell(1);
                cell.setCellValue(businessApprovalReportVO.getCName());
                //

                cell = row.createCell(2);
                cell.setCellValue(businessApprovalReportVO.getIdCArd());

                cell = row.createCell(3);
                cell.setCellValue(businessApprovalReportVO.getSName());

                cell = row.createCell(4);
                cell.setCellValue(businessApprovalReportVO.getPName());

                cell = row.createCell(5);
                cell.setCellValue(businessApprovalReportVO.getHName());

                cell = row.createCell(6);
                cell.setCellValue(businessApprovalReportVO.getGDate());

                cell = row.createCell(7);
                cell.setCellValue(businessApprovalReportVO.getRemitAmount());

                cell = row.createCell(8);
                cell.setCellValue(businessApprovalReportVO.getLoanAmount());

                cell = row.createCell(9);
                cell.setCellValue(businessApprovalReportVO.getSignRate());

                cell = row.createCell(10);
                cell.setCellValue(businessApprovalReportVO.getBankPeriodPrincipal());
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
            sheet.autoSizeColumn((short) 8);
            sheet.autoSizeColumn((short) 9);
            sheet.autoSizeColumn((short) 10);
            sheet.autoSizeColumn((short) 11);

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
     * 导出文件
     *
     * @param
     * @return
     */
    private String createContractExcelFile(List<ContractSetReportVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {

            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("查询机构", "提交日期", "业务员", "业务团队",
                    "主贷人姓名", "主贷人身份证", "配偶姓名", "配偶身份证"
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
                ContractSetReportVO contractSetReportVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(contractSetReportVO.getBank());

                cell = row.createCell(1);
                cell.setCellValue(contractSetReportVO.getCreateTime());
                //

                cell = row.createCell(2);
                cell.setCellValue(contractSetReportVO.getEName());

                cell = row.createCell(3);
                cell.setCellValue(contractSetReportVO.getPName());

                cell = row.createCell(4);
                cell.setCellValue(contractSetReportVO.getCusName());

                cell = row.createCell(5);
                cell.setCellValue(contractSetReportVO.getCusIdCard());

                cell = row.createCell(6);
                cell.setCellValue(contractSetReportVO.getSpouseName());

                cell = row.createCell(7);
                cell.setCellValue(contractSetReportVO.getSpouseIdCard());
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
            sheet.autoSizeColumn((short) 8);

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
}
