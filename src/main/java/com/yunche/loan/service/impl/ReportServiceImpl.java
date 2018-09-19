package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.CarTypeEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.query.BankCreditPrincipalQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.ZhonganInfoDOMapper;
import com.yunche.loan.service.ReportService;
import org.apache.poi.ss.util.CellRangeAddress;
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
import java.util.*;

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
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<ContractSetReportVO> list = zhonganInfoDOMapper.contractSet(query);
        PageInfo<ContractSetReportVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();

        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
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

    @Override
    public ResultBean<List<BankCreditPrincipalVO>> bankCreditPrincipal(BankCreditPrincipalQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<BankCreditPrincipalVO> list = zhonganInfoDOMapper.bankCreditPrincipal(query);
        PageInfo<BankCreditPrincipalVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();

        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ContractSetReportTotalVO bankCreditPrincipalTotal(BankCreditPrincipalQuery query) {
        return zhonganInfoDOMapper.bankCreditPrincipalTotal(query);
    }

    @Override
    public String bankCreditPrincipalExport(BankCreditPrincipalQuery query) {
        List<BankCreditPrincipalVO> list = zhonganInfoDOMapper.bankCreditPrincipalExport(query);
        return createBankCreditPrincipalExcelFile(list);
    }

    @Override
    public ResultBean<List<BankCreditPrincipalVO>> bankCreditAll(BankCreditPrincipalQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<BankCreditPrincipalVO> list = zhonganInfoDOMapper.bankCreditAll(query);
        PageInfo<BankCreditPrincipalVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();

        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ContractSetReportTotalVO bankCreditAllTotal(BankCreditPrincipalQuery query) {
        return zhonganInfoDOMapper.bankCreditAllTotal(query);
    }

    @Override
    public String bankCreditAllExport(BankCreditPrincipalQuery query) {
        List<BankCreditPrincipalVO> list = zhonganInfoDOMapper.bankCreditAllExport(query);
        return createBankCreditPrincipalExcelFile(list);
    }

    @Override
    public ResultBean<List<TelBankCountVO>> telBankCount(BankCreditPrincipalQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<TelBankCountVO> list = zhonganInfoDOMapper.telBankCount(query);
        PageInfo<TelBankCountVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();

        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public String telBankCountExport(BankCreditPrincipalQuery query) {
        List<TelBankCountVO> list = zhonganInfoDOMapper.telBankCount(query);
        return createTelBankExcelFile(list);
    }

    @Override
    public ResultBean<List<TelUserCountVO>> telUserCount(BankCreditPrincipalQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<TelUserCountVO> list = zhonganInfoDOMapper.telUserCount(query);
        PageInfo<TelUserCountVO> pageInfo = new PageInfo<>(list);
        Long totalNum = pageInfo.getTotal();

        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public String telUserCountExport(BankCreditPrincipalQuery query) {
        List<TelUserCountVO> list = zhonganInfoDOMapper.telUserCount(query);
        return createTelUserExcelFile(list);
    }

    @Override
    public ResultBean<List<TelPartnerCountVO>> telPartnerCount(BankCreditPrincipalQuery query) {
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<TelPartnerCountVO> list = zhonganInfoDOMapper.telPartnerCount(query);
        PageInfo<TelPartnerCountVO> pageInfo = new PageInfo<>(list);
        Set<String> action0 = new HashSet();
        action0.add("0");
        List<ActionParMoneyVO> list0 = zhonganInfoDOMapper.selectActionParMoney(action0,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> action1 = new HashSet();
        action1.add("1");
        List<ActionParMoneyVO> list1 = zhonganInfoDOMapper.selectActionParMoney(action1,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> action2 = new HashSet();
        action2.add("2");
        List<ActionParMoneyVO> list2 = zhonganInfoDOMapper.selectActionParMoney(action2,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> action3 = new HashSet();
        action3.add("3");
        List<ActionParMoneyVO> list3 = zhonganInfoDOMapper.selectActionParMoney(action3,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> actionTotal = new HashSet();
        action3.add("3");
        action3.add("2");
        action3.add("1");
        action3.add("0");
        List<ActionParMoneyVO> listTotal = zhonganInfoDOMapper.selectActionParMoney(actionTotal,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());
        for(TelPartnerCountVO telPartnerCountVO:list){
            for(ActionParMoneyVO actionParMoneyVO:list0){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setRepulseMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:list1){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setPassMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:list2){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setChanelMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:list3){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setSupplementMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:listTotal){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setTotalMoney(actionParMoneyVO.getLoanAmount());
                }
            }
        }

        Long totalNum = pageInfo.getTotal();

        return ResultBean.ofSuccess(list, new Long(totalNum).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public String telPartnerCountExport(BankCreditPrincipalQuery query) {
        List<TelPartnerCountVO> list = zhonganInfoDOMapper.telPartnerCount(query);
        Set<String> action0 = new HashSet();
        action0.add("0");
        List<ActionParMoneyVO> list0 = zhonganInfoDOMapper.selectActionParMoney(action0,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> action1 = new HashSet();
        action1.add("1");
        List<ActionParMoneyVO> list1 = zhonganInfoDOMapper.selectActionParMoney(action1,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> action2 = new HashSet();
        action2.add("2");
        List<ActionParMoneyVO> list2 = zhonganInfoDOMapper.selectActionParMoney(action2,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> action3 = new HashSet();
        action3.add("3");
        List<ActionParMoneyVO> list3 = zhonganInfoDOMapper.selectActionParMoney(action3,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());

        Set<String> actionTotal = new HashSet();
        action3.add("3");
        action3.add("2");
        action3.add("1");
        action3.add("0");
        List<ActionParMoneyVO> listTotal = zhonganInfoDOMapper.selectActionParMoney(actionTotal,query.getGmtCreateStart1(),
                query.getGmtCreateEnd1(),query.getBizAreaId(),query.getPartnerId());
        for(TelPartnerCountVO telPartnerCountVO:list){
            for(ActionParMoneyVO actionParMoneyVO:list0){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setRepulseMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:list1){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setPassMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:list2){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setChanelMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:list3){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setSupplementMoney(actionParMoneyVO.getLoanAmount());
                }
            }
            for(ActionParMoneyVO actionParMoneyVO:listTotal){
                if(actionParMoneyVO.getParId().equals(telPartnerCountVO.getPId())){
                    telPartnerCountVO.setTotalMoney(actionParMoneyVO.getLoanAmount());
                }
            }
        }
        return createTelPartnerExcelFile(list);
    }
    private String createTelPartnerExcelFile(List<TelPartnerCountVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "TelPartner"+timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            sheet.addMergedRegion(new CellRangeAddress(0,1,1,1));
            sheet.addMergedRegion(new CellRangeAddress(0,0,2,4));
            sheet.addMergedRegion(new CellRangeAddress(0,0,5,7));
            sheet.addMergedRegion(new CellRangeAddress(0,0,8,10));
            sheet.addMergedRegion(new CellRangeAddress(0,0,11,13));
            sheet.addMergedRegion(new CellRangeAddress(0,0,14,16));
            XSSFRow headRow = sheet.createRow(0);
            XSSFCell cell0 = headRow.createCell(0);
            cell0.setCellValue("大区");
            XSSFCell cell1= headRow.createCell(1);
            cell1.setCellValue("合伙人");
            XSSFCell cell2 = headRow.createCell(2);
            cell2.setCellValue("电审结果-打回");
            XSSFCell cell3 = headRow.createCell(5);
            cell3.setCellValue("电审结果-资料增补");
            XSSFCell cell4 = headRow.createCell(8);
            cell4.setCellValue("电审结果-通过");
            XSSFCell cell5 = headRow.createCell(11);
            cell5.setCellValue("电审结果-弃单");
            XSSFCell cell6 = headRow.createCell(14);
            cell6.setCellValue("经办汇总");

            XSSFRow headRow1 = sheet.createRow(1);
            ArrayList<String> header1 = Lists.newArrayList("单量", "占比","贷款金额",
                    "单量", "占比","贷款金额","单量", "占比","贷款金额","单量", "占比","贷款金额","单量", "占比","贷款金额"
            );
            for (int i = 0; i < header1.size(); i++) {
                XSSFCell cell = headRow1.createCell(i+1);
                cell.setCellValue(header1.get(i));
            }
            XSSFRow row = null;
            XSSFCell cell = null;
            for (int i = 0; i < list.size(); i++) {
                TelPartnerCountVO telPartnerCountVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 2);

                cell = row.createCell(0);
                cell.setCellValue(telPartnerCountVO.getAName());

                cell = row.createCell(1);
                cell.setCellValue(telPartnerCountVO.getPName());

                cell = row.createCell(2);
                cell.setCellValue(telPartnerCountVO.getRepulse());

                cell = row.createCell(3);
                cell.setCellValue(telPartnerCountVO.getRepulseRatio());

                cell = row.createCell(4);
                cell.setCellValue(telPartnerCountVO.getRepulseMoney());

                cell = row.createCell(5);
                cell.setCellValue(telPartnerCountVO.getSupplement());

                cell = row.createCell(6);
                cell.setCellValue(telPartnerCountVO.getSupplementRatio());

                cell = row.createCell(7);
                cell.setCellValue(telPartnerCountVO.getSupplementMoney());

                cell = row.createCell(8);
                cell.setCellValue(telPartnerCountVO.getPass());

                cell = row.createCell(9);
                cell.setCellValue(telPartnerCountVO.getPassRatio());

                cell = row.createCell(10);
                cell.setCellValue(telPartnerCountVO.getPassMoney());

                cell = row.createCell(11);
                cell.setCellValue(telPartnerCountVO.getChanel());

                cell = row.createCell(12);
                cell.setCellValue(telPartnerCountVO.getChanelRatio());

                cell = row.createCell(13);
                cell.setCellValue(telPartnerCountVO.getChanelMoney());

                cell = row.createCell(14);
                cell.setCellValue(telPartnerCountVO.getTotal());

                cell = row.createCell(15);
                cell.setCellValue(telPartnerCountVO.getTotalRatio());

                cell = row.createCell(16);
                cell.setCellValue(telPartnerCountVO.getTotalMoney());


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
            sheet.autoSizeColumn((short) 12);
            sheet.autoSizeColumn((short) 13);
            sheet.autoSizeColumn((short) 14);
            sheet.autoSizeColumn((short) 15);
            sheet.autoSizeColumn((short) 16);

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

    private String createTelUserExcelFile(List<TelUserCountVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "TelUser"+timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();
            sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));
            sheet.addMergedRegion(new CellRangeAddress(0,0,1,2));
            sheet.addMergedRegion(new CellRangeAddress(0,0,3,4));
            sheet.addMergedRegion(new CellRangeAddress(0,0,5,6));
            sheet.addMergedRegion(new CellRangeAddress(0,0,7,8));
            sheet.addMergedRegion(new CellRangeAddress(0,0,9,10));
            XSSFRow headRow = sheet.createRow(0);
            XSSFCell cell0 = headRow.createCell(0);
            cell0.setCellValue("审核员");
            XSSFCell cell1= headRow.createCell(1);
            cell1.setCellValue("电审结果-打回");
            XSSFCell cell2 = headRow.createCell(3);
            cell2.setCellValue("电审结果-资料增补");
            XSSFCell cell3 = headRow.createCell(5);
            cell3.setCellValue("电审结果-通过");
            XSSFCell cell4 = headRow.createCell(7);
            cell4.setCellValue("电审结果-弃单");
            XSSFCell cell5 = headRow.createCell(9);
            cell5.setCellValue("经办汇总");

            XSSFRow headRow1 = sheet.createRow(1);
            ArrayList<String> header1 = Lists.newArrayList("单量", "占比",
                    "单量", "占比","单量", "占比","单量", "占比","单量", "占比"
            );
            for (int i = 0; i < header1.size(); i++) {
                XSSFCell cell = headRow1.createCell(i+1);
                cell.setCellValue(header1.get(i));
            }
            XSSFRow row = null;
            XSSFCell cell = null;
            for (int i = 0; i < list.size(); i++) {
                TelUserCountVO telUserCountVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 2);

                cell = row.createCell(0);
                cell.setCellValue(telUserCountVO.getUserName());

                cell = row.createCell(1);
                cell.setCellValue(telUserCountVO.getRepulse());
                //

                cell = row.createCell(2);
                cell.setCellValue(telUserCountVO.getRepulseRatio());

                cell = row.createCell(3);
                cell.setCellValue(telUserCountVO.getSupplement());

                cell = row.createCell(4);
                cell.setCellValue(telUserCountVO.getSupplementRatio());

                cell = row.createCell(5);
                cell.setCellValue(telUserCountVO.getPass());

                cell = row.createCell(6);
                cell.setCellValue(telUserCountVO.getPassRatio());

                cell = row.createCell(7);
                cell.setCellValue(telUserCountVO.getChanel());

                cell = row.createCell(8);
                cell.setCellValue(telUserCountVO.getChanelRatio());

                cell = row.createCell(9);
                cell.setCellValue(telUserCountVO.getTotal());

                cell = row.createCell(10);
                cell.setCellValue(telUserCountVO.getTotalRatio());


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


    private String createTelBankExcelFile(List<TelBankCountVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "TelBank"+timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("大区", "合伙人", "中国工商银行哈尔滨顾乡支行", "中国工商银行杭州城站支行",
                    "中国工商银行南京江宁支行", "中国工商银行台州路桥支行"
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
                TelBankCountVO telBankCountVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(telBankCountVO.getAreaName());

                cell = row.createCell(1);
                cell.setCellValue(telBankCountVO.getPartnerName());
                //

                cell = row.createCell(2);
                cell.setCellValue(telBankCountVO.getHEB());

                cell = row.createCell(3);
                cell.setCellValue(telBankCountVO.getHZ());

                cell = row.createCell(4);
                cell.setCellValue(telBankCountVO.getNJ());

                cell = row.createCell(5);
                cell.setCellValue(telBankCountVO.getTZ());

                cell = row.createCell(6);
                cell.setCellValue(telBankCountVO.getTotal());


            }
            //文件宽度自适应
            sheet.autoSizeColumn((short) 0);
            sheet.autoSizeColumn((short) 1);
            sheet.autoSizeColumn((short) 2);
            sheet.autoSizeColumn((short) 3);
            sheet.autoSizeColumn((short) 4);
            sheet.autoSizeColumn((short) 5);
            sheet.autoSizeColumn((short) 6);

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


    private String createBankCreditPrincipalExcelFile(List<BankCreditPrincipalVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "BankCreditPrincipal"+timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(file);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("查询机构", "提交日期", "查询客户", "身份证号",
                    "经销商/业务员", "业务团队", "业务品种", "提交机构"
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
                BankCreditPrincipalVO bankCreditPrincipalVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(bankCreditPrincipalVO.getBank());

                cell = row.createCell(1);
                cell.setCellValue(bankCreditPrincipalVO.getCreateTime());
                //

                cell = row.createCell(2);
                cell.setCellValue(bankCreditPrincipalVO.getCName());

                cell = row.createCell(3);
                cell.setCellValue(bankCreditPrincipalVO.getIdCard());

                cell = row.createCell(4);
                cell.setCellValue(bankCreditPrincipalVO.getEName());

                cell = row.createCell(5);
                cell.setCellValue(bankCreditPrincipalVO.getPName());

                cell = row.createCell(6);
                cell.setCellValue(bankCreditPrincipalVO.getBusinessVariety());

                cell = row.createCell(7);
                cell.setCellValue(bankCreditPrincipalVO.getSubmitOrganization());


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
     * 导出文件
     *
     * @param
     * @return
     */
    private String createExcelFile(List<BusinessApprovalReportVO> list) {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = "ContractSet"+timestamp + id + ".xlsx";
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
