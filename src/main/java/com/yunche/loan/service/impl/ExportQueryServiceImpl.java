package com.yunche.loan.service.impl;


import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanStatementDOMapper;
import com.yunche.loan.service.ExportQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                "手机号", "贷款银行", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信提交时间", "提交人"
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
                "手机号", "贷款银行", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信提交时间", "提交人"
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
        String startDate = exportRemitDetailQueryVerifyParam.getStartDate();
        String endDate = exportRemitDetailQueryVerifyParam.getEndDate();

        List<ExportRemitDetailQueryVO> list = loanStatementDOMapper.exportRemitDetailQuerys(exportRemitDetailQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域","客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "车型", "车价", "执行利率", "首付款", "贷款金额", "银行分期本金", "打款金额",
                "创建时间","提交时间","提交人"
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

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务团队", "客户姓名", "身份证号",
                "贷款银行", "车辆型号", "车牌号", "车价", "贷款金融", "银行分期本金", "垫款日期", "银行放款日期", "抵押资料公司寄合伙人",
                "抵押资料合伙人接收时间","抵押状态","抵押日期","抵押超期天数","免责","提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("MortgageOverdue",list,header,ExportMortgageOverdueQueryVO.class,ossConfig);
        return ossResultKey;
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
