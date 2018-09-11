package com.yunche.loan.service.impl;


import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanStatementDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.ExportQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExportQueryServiceImpl implements ExportQueryService
{
    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    /**
     * 导出 EXCEL 银行征信查询
     *
     * @return
     */
    @Override
    public String exportBankCreditQuery(ExportBankCreditQueryVerifyParam exportBankCreditQueryVerifyParam)
    {
        String startDate = exportBankCreditQueryVerifyParam.getStartDate();
        String endDate = exportBankCreditQueryVerifyParam.getEndDate();

        List<ExportBankCreditQueryVO> list = loanStatementDOMapper.exportBankCreditQuerys(exportBankCreditQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行","担保类型", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("BankCredit",list,header,ExportBankCreditQueryVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 社会征信查询
     *
     * @return
     */
    @Override
    public String expertSocialCreditQuery(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam) {
        String startDate = exportSocialCreditQueryVerifyParam.getStartDate();
        String endDate = exportSocialCreditQueryVerifyParam.getEndDate();

        List<ExportSocialCreditQueryVO> list = loanStatementDOMapper.exportSocialCreditQuerys(exportSocialCreditQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行","担保类型", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("SocialCredit",list,header,ExportSocialCreditQueryVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 财务垫款明细查询
     *
     * @return
     */
    @Override
    public String expertRemitDetailQuery(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam) {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportRemitDetailQueryVerifyParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportRemitDetailQueryVerifyParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        List<ExportRemitDetailQueryVO> list = loanStatementDOMapper.exportRemitDetailQuerys(exportRemitDetailQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域","客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "车型", "车价", "执行利率", "首付款", "贷款金额", "银行分期本金", "打款金额",
                "创建时间","垫款时间","提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("RemitDetail",list,header,ExportRemitDetailQueryVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 资料审核明细查询
     *
     * @return
     */
    @Override
    public String expertMaterialReviewQuery(ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam) {
        String startDate = exportMaterialReviewQueryVerifyParam.getStartDate();
        String endDate = exportMaterialReviewQueryVerifyParam.getEndDate();

        List<ExportMaterialReviewDetailQueryVO> list = loanStatementDOMapper.exportMaterialReviewQuerys(exportMaterialReviewQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务团队", "客户姓名", "身份证号",
                 "贷款银行", "银行分期本金", "垫款日期", "资料接收日期", "资料齐全日期", "资料审核提交日期", "资料审核状态", "资料增补次数", "资料增补内容",
                "提车资料提交时间","资料增补时间","合同上交银行日期","垫款超期天数","纸审超期天数","备注"
        );


        String ossResultKey = POIUtil.createExcelFile("MaterialReview",list,header,ExportMaterialReviewDetailQueryVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 抵押超期
     *
     * @return
     */
    @Override
    public String expertMortgageOverdueQuery(ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam) {
        String startDate = exportMortgageOverdueQueryVerifyParam.getStartDate();
        String endDate = exportMortgageOverdueQueryVerifyParam.getEndDate();

        List<ExportMortgageOverdueQueryVO> list = loanStatementDOMapper.exportMortgageOverdueQuerys(exportMortgageOverdueQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务团队", "客户姓名", "身份证号","手机号",
                "贷款银行", "车辆型号", "车牌号", "车价", "贷款金融", "银行分期本金", "垫款日期", "银行放款日期", "抵押资料公司寄合伙人",
                "抵押资料合伙人接收时间","抵押状态","抵押日期","抵押超期天数","提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("MortgageOverdue",list,header,ExportMortgageOverdueQueryVO.class,ossConfig);
        return ossResultKey;
    }

    @Override
    public String exportOrders(ExportOrdersParam exportOrdersParam) {

        List<ExportOrdersVO> list = loanStatementDOMapper.exportOrders(exportOrdersParam);

        ArrayList<String> header = Lists.newArrayList(
                "业务编号"
                ,"客户姓名"
                ,"身份证"
                ,"手机号"
                ,"业务员"
                ,"合伙人"
                ,"车价"
                ,"贷款银行"
                ,"基准品估计"
                ,"贷款金额"
                ,"银行分期本金"
                ,"打款金额"
                ,"期数"
                ,"车型"
                ,"车辆类型"
                ,"车牌号"
                ,"车辆引擎编号"
                ,"车架号"
                ,"车辆注册日期"
                ,"车辆颜色"
                ,"车排量"
                ,"车辆使用年限"
                ,"征信申请时间"
                ,"银行征信申请时间"
                ,"社会征信申请时间"
                ,"贷款申请时间"
                ,"上门家访时间"
                ,"电审时间"
                ,"提车资料录入时间"
                ,"资料审核时间"
                ,"合同套打时间"
                ,"gps安装时间"
                ,"上牌抵押时间"
                ,"车辆保险录入时间"
                ,"业务付款时间"
                ,"业务审批时间"
                ,"放款审核时间"
                ,"打款审核时间"
                ,"资料归档时间"
                ,"银行放款时间"
                ,"银行卡寄送时间"
        );
        String ossResultKey = POIUtil.createExcelFile("MortgageOrders",list,header,ExportOrdersVO.class,ossConfig);
        return ossResultKey;
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  财务打款单导出垫款明细
    */
    @Override
    public String expertRemitDetailQueryForRemitOrder(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam)
    {
        //TODO

        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportRemitDetailQueryVerifyParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportRemitDetailQueryVerifyParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        List<ExportRemitDetailQueryForRemitOrderVO> list = loanStatementDOMapper.exportRemitDetailForRemitOrderQuerys(exportRemitDetailQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域","客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "车型", "车价", "执行利率", "首付款", "贷款金额", "银行分期本金", "打款金额",
                "公司收益","履约金","上牌押金","GPS使用费","风险金","公正评估费","上省外牌","基础保证金","其他","返利不内扣","返利金额","额外费用",
                "创建时间","垫款时间","退款时间","提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("RemitDetailForRemitOrder",list,header,ExportRemitDetailQueryForRemitOrderVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 金投行过桥处理(借款申请记录)
     * @param param
     * @return
     */
    @Override
    public String exportApplyLoanPush(ExportApplyLoanPushParam param) {
        return null;
    }


    /**
     * 导出文件
     *
     * @param
     * @return
     */
   /* private String createExcelFile(ExpertBankCreditQueryVerifyParam expertBankCreditQueryVerifyParam) {

        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = timestamp + id + ".xlsx";
        //创建workbook
        File file = new File("D:" + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {

            out = new FileOutputStream(file);

            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();



            //申请单号	客户名称	证件类型	证件号	业务员	合伙人团队	贷款金额	gps数量	申请单状态	提交状态	备注	审核员	审核时间
            XSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            XSSFRow row = null;
            XSSFCell cell = null;
            for (int i = 0; i < list.size(); i++) {
                ExpertBankCreditQueryVO expertBankCreditQueryVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(expertBankCreditQueryVO.getArea_name());

                cell = row.createCell(1);
                cell.setCellValue(expertBankCreditQueryVO.getCustomer_name());
                //

                cell = row.createCell(2);
                cell.setCellValue(expertBankCreditQueryVO.getCust_type());

                cell = row.createCell(3);
                cell.setCellValue(expertBankCreditQueryVO.getCustomer_name());

                cell = row.createCell(4);
                cell.setCellValue(expertBankCreditQueryVO.getCustomer_id_card());

                cell = row.createCell(5);
                cell.setCellValue(expertBankCreditQueryVO.getCustomer_mobile());

                cell = row.createCell(6);
                cell.setCellValue(expertBankCreditQueryVO.getPrincipal_base());

                cell = row.createCell(7);
                cell.setCellValue(expertBankCreditQueryVO.getBank());

                cell = row.createCell(8);
                cell.setCellValue(expertBankCreditQueryVO.getPartner_name());

                cell = row.createCell(9);
                cell.setCellValue(expertBankCreditQueryVO.getSalesman_name());

                cell = row.createCell(10);
                cell.setCellValue(expertBankCreditQueryVO.getPrincipal_name());

                cell = row.createCell(11);
                cell.setCellValue(expertBankCreditQueryVO.getCredit_result());

                cell = row.createCell(12);
                cell.setCellValue(expertBankCreditQueryVO.getCredit_apply_time());


                cell = row.createCell(13);
                cell.setCellValue(expertBankCreditQueryVO.getCredit_query_time());

                cell = row.createCell(14);
                cell.setCellValue(expertBankCreditQueryVO.getOp_info());


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
    }*/
}
