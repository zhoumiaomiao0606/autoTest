package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.CarTypeEnum;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.TelephoneVerifyParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanQueryService;
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
import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;

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
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;


    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private CurrentNodeManagerDOMapper currentNodeManagerDOMapper;

    @Override
    public RecombinationVO detail(Long orderId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for (UniversalCreditInfoVO universalCreditInfoVO : credits) {
            if (!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())) {
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId, Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO!=null && loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        RecombinationVO recombinationVO = new RecombinationVO();

        LoanTelephoneVerifyDO loanTelephoneVerifyDO = loanTelephoneVerifyDOMapper.selectByPrimaryKey(orderId);
        //显示
        Set<Byte> types = new HashSet<Byte>();
        types.add(LoanFileEnum.LETTER_COMMITMENT_SIGNED_PARTNERS.getType());
        List<UniversalMaterialRecordVO> materialRecord = loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, types);

        recombinationVO.setMaterials(materialRecord);


        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), new Byte("0"));

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        universalInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);
        universalInfoVO.setRiskBearRate(partnerDO.getRiskBearRate()==null?new BigDecimal("0"):partnerDO.getRiskBearRate());
        recombinationVO.setInfo(universalInfoVO);

        if (loanTelephoneVerifyDO!=null)
        {
            CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(orderId);
            if (currentNodeManagerDO !=null)
            {
                loanTelephoneVerifyDO.setPassTime(currentNodeManagerDO.getUsertaskTelephoneVerifyCreateTime());
                loanTelephoneVerifyDO.setPassName(currentNodeManagerDO.getUsertaskTelephoneVerifyGmtUserName());
            }

            UniversalApprovalInfo universalApprovalInfo = loanQueryDOMapper.selectUniversalApprovalInfo(TELEPHONE_VERIFY.getCode(), orderId);
            if (universalApprovalInfo !=null)
            {
                loanTelephoneVerifyDO.setRemark(universalApprovalInfo.getInfo());
            }

        }

        recombinationVO.setTelephone_des(loanTelephoneVerifyDO);
        recombinationVO.setCredits(credits);
        recombinationVO.setHome(loanQueryDOMapper.selectUniversalHomeVisitInfo(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo(TELEPHONE_VERIFY.getCode(), orderId));
        recombinationVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderId(orderId));
        recombinationVO.setSupplement(loanQueryService.selectUniversalInfoSupplementHistory(orderId));
        recombinationVO.setCustomers(customers);

        return recombinationVO;
    }

    @Override
    public void update(TelephoneVerifyParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));

        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();
        Long loanCustomerId = loanOrderDO.getLoanCustomerId();

        if (loanOrderDO != null) {
            if (loanCarInfoId != null) {
                LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
                loanCarInfoDO.setId(loanCarInfoId);
                loanCarInfoDO.setGpsNum(StringUtils.isBlank(param.getCar_gps_num()) ? null : Integer.valueOf(param.getCar_gps_num()));
                loanCarInfoDO.setCarKey(StringUtils.isBlank(param.getCar_key()) ? null : new Byte(param.getCar_key()));
                loanCarInfoDOMapper.updateByPrimaryKeySelective(loanCarInfoDO);
            }
            if (loanFinancialPlanId != null) {
                if (StringUtils.isNotBlank(param.getFinancial_cash_deposit()) || StringUtils.isNotBlank(param.getFinancial_extra_fee())) {
                    LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
                    loanFinancialPlanDO.setId(loanFinancialPlanId);
                    loanFinancialPlanDO.setCashDeposit(StringUtils.isBlank(param.getFinancial_cash_deposit()) ? null : new BigDecimal(param.getFinancial_cash_deposit()));
                    loanFinancialPlanDO.setExtraFee(StringUtils.isBlank(param.getFinancial_extra_fee()) ? null : new BigDecimal(param.getFinancial_extra_fee()));
                    loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
                }
            }
            if (loanCustomerId != null) {
                if (StringUtils.isNotBlank(param.getOpenCardOrder())) {
                    LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
                    loanCustomerDO.setId(loanCustomerId);
                    loanCustomerDO.setOpenCardOrder(param.getOpenCardOrder());
                    loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
                }
            }
        }
    }

    /**
     * 导出 EXCEL
     *
     * @return
     */
    @Override
    public String export(TelephoneVerifyParam telephoneVerifyParam) {
        String ossResultKey = createExcelFile(telephoneVerifyParam);
        return ossResultKey;
    }

    /**
     * 导出文件
     *
     * @param telephoneVerifyParam
     * @return
     */
    private String createExcelFile(TelephoneVerifyParam telephoneVerifyParam) {
        String startDate = telephoneVerifyParam.getStartDate();
        String endDate = telephoneVerifyParam.getEndDate();
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        String fileName = timestamp + id + ".xlsx";
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;
        try {

            out = new FileOutputStream(file);
            List<TelephoneVerifyNodeOrdersVO> list = loanStatementDOMapper.statisticsTelephoneVerifyNodeOrders(telephoneVerifyParam);
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();


            ArrayList<String> header = Lists.newArrayList("申请单号", "客户名称", "证件类型", "证件号",
                    "业务员", "合伙人团队","合伙人编码","合伙人组别", "贷款银行", "贷款金额", "银行分期本金", "gps数量", "审核结果", "审核状态", "审核员","领取时间","反馈时间", "审核时间", "备注", "车辆类型"
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
                TelephoneVerifyNodeOrdersVO telephoneVerifyNodeOrdersVO = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOrder_id());

                cell = row.createCell(1);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCustomer_name());
                //

                cell = row.createCell(2);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCustomer_card_type());

                cell = row.createCell(3);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCustomer_id_card());

                cell = row.createCell(4);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getSaleman_name());

                cell = row.createCell(5);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getPartner_name());

                cell = row.createCell(6);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getPartner_code());

                cell = row.createCell(7);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getPartner_group());

                cell = row.createCell(8);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getBank());

                cell = row.createCell(9);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getLoan_amount());

                cell = row.createCell(10);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getBank_period_principal());

                cell = row.createCell(11);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getGps_number());

                cell = row.createCell(12);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getAction());

                cell = row.createCell(13);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getCommit_status());

                cell = row.createCell(14);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOp_user_name());

                cell = row.createCell(15);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getReceive_time());

                cell = row.createCell(16);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getFeedback_time());

                cell = row.createCell(17);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOp_time());

                cell = row.createCell(18);
                cell.setCellValue(telephoneVerifyNodeOrdersVO.getOp_info());

                cell = row.createCell(19);
                cell.setCellValue(CarTypeEnum.getValueByKey(telephoneVerifyNodeOrdersVO.getCar_type()));
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
            sheet.autoSizeColumn((short) 17);
            sheet.autoSizeColumn((short) 18);
            sheet.autoSizeColumn((short) 19);
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
