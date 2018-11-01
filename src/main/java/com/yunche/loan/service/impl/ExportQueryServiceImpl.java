package com.yunche.loan.service.impl;


import com.aliyun.oss.OSSClient;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.BizAreaDO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.ExportQueryService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

@Service
@Transactional
public class ExportQueryServiceImpl implements ExportQueryService
{
    private static final Pattern pattern = Pattern.compile(".*长期.*");

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private ChartDOMapper chartDOMapper;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Autowired
    private BizAreaDOMapper bizAreaDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    /**
     * 导出 EXCEL 银行征信查询
     *
     * @return
     */
    @Override
    public String exportBankCreditQuery(ExportBankCreditQueryVerifyParam exportBankCreditQueryVerifyParam)
    {
        //大区
        if (exportBankCreditQueryVerifyParam.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(exportBankCreditQueryVerifyParam.getBiz_areaId());
            selfAndChildBiz_area.add(exportBankCreditQueryVerifyParam.getBiz_areaId());
            exportBankCreditQueryVerifyParam.setBizAreaList(selfAndChildBiz_area);

        }

        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportBankCreditQueryVerifyParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportBankCreditQueryVerifyParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List<ExportBankCreditQueryVO> list = loanStatementDOMapper.exportBankCreditQuerys(exportBankCreditQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行","担保类型", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("BankCredit",list,header,ExportBankCreditQueryVO.class,ossConfig);
        return ossResultKey;
    }

    //导出
    public String exportBankCreditQueryForChart(BankCreditChartParam param)
    {
        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }

        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        System.out.println("=========");
        List<BankCreditChartVO> list = chartDOMapper.selectBankCreditChartVO(param);
        System.out.println("=========");
        ArrayList<String> header = Lists.newArrayList("大区","业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("BankCredit",list,header,BankCreditChartVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 社会征信查询
     *
     * @return
     */
    @Override
    public String expertSocialCreditQuery(ExportSocialCreditQueryVerifyParam exportSocialCreditQueryVerifyParam)
    {
        //大区
        if (exportSocialCreditQueryVerifyParam.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(exportSocialCreditQueryVerifyParam.getBiz_areaId());
            selfAndChildBiz_area.add(exportSocialCreditQueryVerifyParam.getBiz_areaId());
            exportSocialCreditQueryVerifyParam.setBizAreaList(selfAndChildBiz_area);

        }
        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportSocialCreditQueryVerifyParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportSocialCreditQueryVerifyParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        List<ExportSocialCreditQueryVO> list = new ArrayList<>();

            list = loanStatementDOMapper.exportSocialCreditQuerys1(exportSocialCreditQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行","担保类型", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("SocialCredit",list,header,ExportSocialCreditQueryVO.class,ossConfig);
        return ossResultKey;
    }

    @Override
    public String expertSocialCreditQueryForChart(SocialCreditChartParam param) {
        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List list = chartDOMapper.selectSocialCreditChartVO(param);

        ArrayList<String> header = Lists.newArrayList("大区","业务区域", "业务关系", "客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "主贷人姓名", "与主贷人关系", "征信结果", "征信申请时间", "征信查询时间", "提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("SocialCredit",list,header,SocialCreditChartVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 财务垫款明细查询
     *
     * @return
     */
    @Override
    public String expertRemitDetailQuery(ExportRemitDetailQueryVerifyParam exportRemitDetailQueryVerifyParam)
    {
        //大区
        if (exportRemitDetailQueryVerifyParam.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(exportRemitDetailQueryVerifyParam.getBiz_areaId());
            selfAndChildBiz_area.add(exportRemitDetailQueryVerifyParam.getBiz_areaId());
            exportRemitDetailQueryVerifyParam.setBizAreaList(selfAndChildBiz_area);

        }
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

    @Override
    public String expertRemitDetailQueryForChart(FinancialDepartmentRemitDetailChartParam param) {
        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        PageHelper.startPage(0, 10000, false);
        List list = chartDOMapper.selectFinancialDepartmentRemitDetailChartVO(param);
        ArrayList<String> header = Lists.newArrayList("大区","业务区域","客户姓名", "身份证号",
                "手机号", "贷款银行", "业务团队", "业务员", "车型", "车价", "执行利率", "首付款", "贷款金额", "银行分期本金", "打款金额",
                "创建时间","垫款时间","提交人"
        );


        String ossResultKey = POIUtil.createExcelFile("RemitDetail",list,header,FinancialDepartmentRemitDetailChartVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 资料审核明细查询
     *
     * @return
     */
    @Override
    public String expertMaterialReviewQuery(ExportMaterialReviewQueryVerifyParam exportMaterialReviewQueryVerifyParam)
    {
        //大区
        if (exportMaterialReviewQueryVerifyParam.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(exportMaterialReviewQueryVerifyParam.getBiz_areaId());
            selfAndChildBiz_area.add(exportMaterialReviewQueryVerifyParam.getBiz_areaId());
            exportMaterialReviewQueryVerifyParam.setBizAreaList(selfAndChildBiz_area);

        }

        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportMaterialReviewQueryVerifyParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportMaterialReviewQueryVerifyParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List<ExportMaterialReviewDetailQueryVO> list = loanStatementDOMapper.exportMaterialReviewQuerys(exportMaterialReviewQueryVerifyParam);

        ArrayList<String> header = Lists.newArrayList("业务区域", "业务团队", "客户姓名", "身份证号",
                 "贷款银行", "银行分期本金", "垫款日期", "资料接收日期", "资料齐全日期", "资料审核提交日期", "资料审核状态", "资料增补次数", "资料增补内容",
                "提车资料提交时间","资料增补时间","合同上交银行日期","垫款超期天数","纸审超期天数","备注"
        );


        String ossResultKey = POIUtil.createExcelFile("MaterialReview",list,header,ExportMaterialReviewDetailQueryVO.class,ossConfig);
        return ossResultKey;
    }

    @Override
    public String expertMaterialReviewQueryForChart(MaterialReviewParam param) {
        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List list = chartDOMapper.selectMaterialReviewChartVO(param);
        ArrayList<String> header = Lists.newArrayList("大区","业务区域", "业务团队", "客户姓名", "身份证号",
                "贷款银行","申请征信时间", "银行分期本金", "垫款日期", "资料接收日期", "资料齐全日期", "资料审核提交日期", "资料审核状态", "资料增补次数", "资料增补内容",
                "提车资料提交时间","资料增补时间","合同上交银行日期","垫款超期天数","纸审超期天数","备注"
        );


        String ossResultKey = POIUtil.createExcelFile("MaterialReview",list,header,MaterialReviewChartVO.class,ossConfig);
        return ossResultKey;
    }

    /**
     * 导出 EXCEL 抵押超期
     *
     * @return
     */
    @Override
    public String expertMortgageOverdueQuery(ExportMortgageOverdueQueryVerifyParam exportMortgageOverdueQueryVerifyParam)
    {
        //大区
        if (exportMortgageOverdueQueryVerifyParam.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(exportMortgageOverdueQueryVerifyParam.getBiz_areaId());
            selfAndChildBiz_area.add(exportMortgageOverdueQueryVerifyParam.getBiz_areaId());
            exportMortgageOverdueQueryVerifyParam.setBizAreaList(selfAndChildBiz_area);

        }

        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportMortgageOverdueQueryVerifyParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportMortgageOverdueQueryVerifyParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List<ExportMortgageOverdueQueryVO> list = loanStatementDOMapper.exportMortgageOverdueQuerys(exportMortgageOverdueQueryVerifyParam);
        for(ExportMortgageOverdueQueryVO exportMortgageOverdueQueryVO : list){
            exportMortgageOverdueQueryVO.setArea_name(getAreaName(exportMortgageOverdueQueryVO.getArea_id()));
        }

        ArrayList<String> header = Lists.newArrayList("上牌地", "业务团队", "客户姓名", "身份证号","手机号",
                "贷款银行", "车辆型号", "车牌号","车架号" ,"车价", "贷款金融", "银行分期本金", "垫款日期", "银行放款日期", "抵押资料公司寄合伙人",
                "抵押资料合伙人接收时间","抵押状态","抵押日期","抵押超期天数","提交人"
        );


        String ossResultKey = createExcelFile("MortgageOverdue",list,header,ExportMortgageOverdueQueryVO.class,ossConfig);
        return ossResultKey;
    }
    //特殊处理
    public static <T> String createExcelFile(String fname, List<T> list, List<String> header, Class<T> clazz, OSSConfig ossConfig) {
        StringBuilder fileName = new StringBuilder();
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        Long id = SessionUtils.getLoginUser().getId();
        fileName.append(fname).append(timestamp).append(id).append(".xlsx");
        //创建workbook
        File file = new File(ossConfig.getDownLoadBasepath() + File.separator + fileName);
        FileOutputStream out = null;
        XSSFWorkbook workbook = null;

        try {

            out = new FileOutputStream(file);

            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet();

            XSSFRow headRow = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                XSSFCell cell = headRow.createCell(i);
                cell.setCellValue(header.get(i));
            }
            XSSFRow row = null;
            XSSFCell cell = null;

            Field[] fields = clazz.getDeclaredFields();

            List<Method> getMethods = new ArrayList();


            for (int i = 0; i < fields.length; i++)
            {
                Field field = fields[i];
                // 此处应该判断beanObj,property不为null
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                getMethods.add(pd.getReadMethod());
            }


            //设置数据
            for (int i = 0; i < list.size(); i++)
            {
                T data = list.get(i);
                //创建行
                row = sheet.createRow(i + 1);

                for (int j = 1; j < getMethods.size(); j++)
                {
                    cell = row.createCell(j-1);
                    //判断----将Date特殊处理
                    if(getMethods.get(j).invoke(data) instanceof Date)
                    {
                        cell.setCellValue((Date) getMethods.get(j).invoke(data));
                    }
                    else if (getMethods.get(j).invoke(data) instanceof BigDecimal)
                    {

                        cell.setCellValue( ((BigDecimal)getMethods.get(j).invoke(data)).toString());

                    }
                    else{
                        cell.setCellValue((String) getMethods.get(j).invoke(data));
                    }

                }

            }

            for (int j = 0; j < getMethods.size(); j++)
            {
                //文件宽度自适应
                sheet.autoSizeColumn((short) j);
            }
            workbook.write(out);
            //上传OSS
            OSSClient ossClient = OSSUnit.getOSSClient();
            String bucketName = ossConfig.getBucketName();
            String diskName = ossConfig.getDownLoadDiskName();
            OSSUnit.deleteFile(ossClient, bucketName, diskName + File.separator, fileName.toString());
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


    public String getAreaName(Long areaId){
        String tmpApplyLicensePlateArea ="";
        if (areaId != null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(areaId, VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if ("3".equals(String.valueOf(baseAreaDO.getLevel()))) {
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                if(cityDO!=null){
                    baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                    baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
                }

            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }
        return tmpApplyLicensePlateArea;
    }

    @Override
    public String expertMortgageOverdueQueryForChart(MortgageOverdueParam param) {
        //大区
        if (param.getBiz_areaId() !=null)
        {
            List<Long> selfAndChildBiz_area = getSelfAndChildBiz_area(param.getBiz_areaId());
            selfAndChildBiz_area.add(param.getBiz_areaId());
            param.setBizAreaList(selfAndChildBiz_area);

        }
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        PageHelper.startPage(0, 10000, false);
        List list = chartDOMapper.selectMortgageOverdueChartVO(param);
        ArrayList<String> header = Lists.newArrayList("大区","业务区域", "业务团队", "客户姓名", "身份证号","手机号","征信申请时间",
                "贷款银行", "车辆型号", "车牌号", "车价", "贷款金额", "银行分期本金", "垫款日期", "银行放款日期", "抵押资料公司寄合伙人",
                "抵押资料合伙人接收时间","抵押状态","抵押日期","抵押超期天数"
        );


        String ossResultKey = POIUtil.createExcelFile("MortgageOverdue",list,header,MortgageOverdueChartVO.class,ossConfig);
        return ossResultKey;
    }

    @Override
    public String exportOrders(ExportOrdersParam exportOrdersParam) {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportOrdersParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportOrdersParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

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
                ,"银行征信结果"
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
                ,"资料接收时间"
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
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  客户信息中新增客户信息导出功能
    */
    @Override
    public String exportCustomerInfo(ExportCustomerInfoParam exportCustomerInfoParam)
    {
        Long loginUserId = SessionUtils.getLoginUser().getId();

        exportCustomerInfoParam.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        exportCustomerInfoParam.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //根据筛选条件  银行、合同资料公司至银行-确认接收时间、合伙人团队、主贷人姓名  过滤主贷人信息
        List<ExportCustomerInfoVO> list = loanStatementDOMapper.exportCustomerInfo(exportCustomerInfoParam);
        CompplexHeader compplexHeader =new CompplexHeader();
        List<ExportCustomerInfoVO> exportCustomerInfoVOList =new ArrayList<>();

        if (list.size() != 0) {
            //去空
            exportCustomerInfoVOList = list.stream()
                    .filter(exportCustomerInfoVO -> exportCustomerInfoVO != null)
                    .collect(Collectors.toList());


            exportCustomerInfoVOList.forEach(
                    e ->
                    {
                        //选出紧急联系人
                        List<FamilyLinkManVO> familyLinkManList = loanStatementDOMapper.exportFamilyLinkManList(e.getPCustomerId());
                        e.setFamilyLinkManList(familyLinkManList);
                        //关联人要导出全部共贷人和银行担保
                        List<GuarantorLinkManVO> guarantorLinkManList = loanStatementDOMapper.exportGuarantorLinkManList(e.getPCustomerId());
                        //判断有效期-计算主贷人总资产

                        //去空
                        List<GuarantorLinkManVO> collect = guarantorLinkManList.stream()
                                .filter(guarantorLinkManVO -> guarantorLinkManVO != null)
                                .collect(Collectors.toList());
                        collect.forEach(guarantorLinkManVO -> {
                            if (guarantorLinkManVO.getLinkManIdentityValidity() != null && pattern.matcher(guarantorLinkManVO.getLinkManIdentityValidity()).find()) {
                                guarantorLinkManVO.setBooleanLongTerm("是");
                                System.out.println("姓名" + e.getPName() + "====担保人名字" + guarantorLinkManVO.getLinkManName());
                            } else {
                                guarantorLinkManVO.setBooleanLongTerm("否");
                            }


                        });

                        e.setGuarantorLinkManList(collect);
                        //计算主贷人总资产
                        BigDecimal totalAsset = new BigDecimal(0);
                        for (GuarantorLinkManVO guarantorLinkManVO : collect) {
                            if (guarantorLinkManVO.getLinkManYearIncome() != null) {
                                totalAsset = totalAsset.add(guarantorLinkManVO.getLinkManYearIncome());
                            }
                        }

                        if (e.getPYearIncome() != null) {
                            totalAsset = totalAsset.add(e.getPYearIncome());
                        }

                        e.setTotalAsset(totalAsset);


                        //将中文姓名导出拼音
                        if (e.getPName()!=null){
                            e.setPyName(POIUtil.getPingYin(e.getPName()));
                        }

                    }
            );

            //计算共贷人和担保人数最大值
            int max = exportCustomerInfoVOList.stream().max(new Comparator<ExportCustomerInfoVO>() {
                @Override
                public int compare(ExportCustomerInfoVO o1, ExportCustomerInfoVO o2) {
                    return o1.getGuarantorLinkManList().size() - o2.getGuarantorLinkManList().size();
                }
            }).get().getGuarantorLinkManList().size();

            compplexHeader.setCount(max);
            System.out.println("=======================" + max + "=======================================");
        }

        //TODO  后期用linkedMap修改
                   //动态生成列表头---主要是后面共贷人和担保人需取最大数量值
        ArrayList<String> pheader = Lists.newArrayList(
                "姓名",
                "姓名拼音",
                "性别",
                "出生日期",
                "证件号码",
                "证件有效截止日",
                "国籍",
                "婚姻状况",
                "教育程度",
                "手机号",
                "住宅地址",
                "住宅电话",
                "邮编",
                "住宅状况",
                "单位名称",
                "单位地址",
                "邮编",
                "单位电话",
                "单位经济性质",
                "所属行业",
                "职业",
                "职务",
                "年收入",
                "首付款",
                "贷款金额",
                "贷款期限",
                "还款人月均总收入",
                "个人总资产",
                "进口车标志",
                "生产厂商",
                "汽车品牌",
                "款式规格",
                "购车年月",
                "车牌号码",
                "车架号",
                "发动机号",
                "汽车办理抵押地区",
                "汽车权属人姓名",
                "申请人与抵押物权属人关系",

                "亲属联系人1姓名",
                "关系",
                "手机号",
                "亲属联系人2姓名",
                "关系",
                "手机号"

        );

        ArrayList<String> aheader = Lists.newArrayList(
                "客户姓名",
                "关联人证件号码",
                "性别",
                "证件是否长期有效",
                "证件有效期截止日",
                "与申请人关系",
                "个人年收入",
                "住宅地址",
                "单位名称",
                "单位地址",
                "手机号"


        );

        compplexHeader.setPheader(pheader);
        compplexHeader.setAheader(aheader);



        //特殊导出
        String ossResultKey = POIUtil.createComplexExcelFile("customerInfo",exportCustomerInfoVOList,ExportCustomerInfoVO.class,FamilyLinkManVO.class,GuarantorLinkManVO.class,compplexHeader,ossConfig);

        return ossResultKey;
    }

    @Override
    public String expertAwaitRemitDetailChart(AwaitRemitDetailChartParam param) {

        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List list = chartDOMapper.selectAwaitRemitDetailChartVO(param);
        ArrayList<String> header = Lists.newArrayList("主贷姓名", "身份证号", "业务员", "业务部门","经办人",
                "经办时间", "打款金额", "贷款金额", "执行利率%", "银行分期本金"
        );


        String ossResultKey = POIUtil.createExcelFile("MortgageOverdue",list,header,AwaitRemitDetailChartVO.class,ossConfig);
        return ossResultKey;
    }

    @Override
    public String expertCompanyRemitDetailChart(CompanyRemitDetailChartParam param) {

        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List<CompanyRemitDetailChartVO> list = chartDOMapper.selectCompanyRemitDetailChartVO(param);
        ArrayList<String> header = Lists.newArrayList("客户编号", "主贷姓名", "身份证号", "业务员","大区",
                "省份", "业务团队", "按揭银行", "执行利率%", "车型", "首付款", "分期总额", "打款金额", "垫款时间"

        );


        String ossResultKey = POIUtil.createExcelFile("MortgageOverdue",list,header,CompanyRemitDetailChartVO.class,ossConfig);
        return ossResultKey;
    }


    public List<Long> getSelfAndChildBiz_area(Long parentId)
    {
        List<BizAreaDO> bizAreaDOs = bizAreaDOMapper.getByParentId(parentId, VALID_STATUS);
        //递归查询所有的子区域--用缓存优化
        List<Long> longList = bizAreaDOs.stream()
                .map(bizAreaDO -> bizAreaDO.getId())
                .collect(Collectors.toList());
        return longList;

    }
}
