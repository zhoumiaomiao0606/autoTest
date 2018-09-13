package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.constant.VideoFaceConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.query.VideoFaceQuery;
import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.domain.vo.VideoFaceFlagVO;
import com.yunche.loan.domain.vo.VideoFaceLogVO;
import com.yunche.loan.domain.vo.VideoFaceQuestionAnswerVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.CarService;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.VideoFaceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BankConst.*;
import static com.yunche.loan.config.constant.CarConst.CAR_BRAND;
import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.CarConst.CAR_MODEL;
import static com.yunche.loan.config.constant.ExportExcelConst.*;
import static com.yunche.loan.config.constant.VideoFaceConst.*;
import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMddHHmmss;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
@Service
public class VideoFaceServiceImpl implements VideoFaceService {

    // log
    private static final Logger logger = LoggerFactory.getLogger(VideoFaceServiceImpl.class);

    public static final String HTML_NEW_LINE = "<br/>";

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private CarService carService;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private PartnerDOMapper partnerDOMapper;


    @Override
    @Transactional
    public ResultBean<Long> saveLog(VideoFaceLogDO videoFaceLogDO) {
        Preconditions.checkNotNull(videoFaceLogDO.getOrderId(), "订单号不能为空");
        Preconditions.checkNotNull(videoFaceLogDO.getBankId(), "银行ID不能为空");

        if (StringUtils.isBlank(videoFaceLogDO.getBankName())) {
            videoFaceLogDO.setBankName(bankCache.getNameById(videoFaceLogDO.getBankId()));
        }

        // 机器面签
        if (FACE_SIGN_TYPE_MACHINE.equals(videoFaceLogDO.getType())) {
            //  车型名称
            if (null != videoFaceLogDO.getCarDetailId()) {
                String carName = carService.getName(videoFaceLogDO.getCarDetailId(), CAR_DETAIL, CAR_MODEL);
                if (StringUtils.isNotBlank(carName)) {
                    videoFaceLogDO.setCarName(carName);
                }
            }

            // action 默认：PASS（机器面签不审核，默认通过！）
            videoFaceLogDO.setAction(VideoFaceConst.ACTION_PASS);
        }

        // 担保公司  就一个   写死
        videoFaceLogDO.setGuaranteeCompanyId(GUARANTEE_COMPANY_ID);
        videoFaceLogDO.setGuaranteeCompanyName(GUARANTEE_COMPANY_NAME);

        videoFaceLogDO.setGmtCreate(new Date());
        videoFaceLogDO.setGmtModify(new Date());
        int count = videoFaceLogDOMapper.insertSelective(videoFaceLogDO);
        Preconditions.checkArgument(count > 0, "保存失败");

        return ResultBean.ofSuccess(videoFaceLogDO.getId(), "保存成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLog(VideoFaceLogDO videoFaceLogDO) {
        Preconditions.checkArgument(null != videoFaceLogDO && null != videoFaceLogDO.getId(), "id不能为空");

        videoFaceLogDO.setGmtModify(new Date());
        int count = videoFaceLogDOMapper.updateByPrimaryKeySelective(videoFaceLogDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<List<VideoFaceLogVO>> listLog(VideoFaceQuery videoFaceQuery) {

        // checkPermission
        checkPermission(videoFaceQuery);

        // export
        if (null != videoFaceQuery.getPageIndex() && null != videoFaceQuery.getPageSize()) {
            PageHelper.startPage(videoFaceQuery.getPageIndex(), videoFaceQuery.getPageSize(), true);
        }

        List<VideoFaceLogDO> videoFaceLogDOList = videoFaceLogDOMapper.query(videoFaceQuery);

        List<VideoFaceLogVO> videoFaceLogVOList = videoFaceLogDOList.parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {

                    VideoFaceLogVO videoFaceLogVO = new VideoFaceLogVO();
                    BeanUtils.copyProperties(e, videoFaceLogVO);
                    videoFaceLogVO.setOrderId(String.valueOf(e.getOrderId()));

                    return videoFaceLogVO;
                })
                .collect(Collectors.toList());

        // 取分页信息
        PageInfo<VideoFaceLogDO> pageInfo = new PageInfo<>(videoFaceLogDOList);

        return ResultBean.ofSuccess(videoFaceLogVOList, Math.toIntExact(pageInfo.getTotal()),
                pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<VideoFaceLogVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        VideoFaceLogDO videoFaceLogDO = videoFaceLogDOMapper.selectByPrimaryKey(id);

        VideoFaceLogVO videoFaceLogVO = new VideoFaceLogVO();
        if (null != videoFaceLogDO) {
            PartnerDO partnerDO = partnerDOMapper.queryPartnerInfoByOrderId(videoFaceLogDO.getOrderId());
            String partnerName = partnerDO.getName();
            BeanUtils.copyProperties(videoFaceLogDO, videoFaceLogVO);
            videoFaceLogVO.setOrderId(String.valueOf(videoFaceLogDO.getOrderId()));
            videoFaceLogVO.setPartnerName(partnerName);
        }
        return ResultBean.ofSuccess(videoFaceLogVO);
    }

    @Override
    public ResultBean<String> exportLog(VideoFaceQuery videoFaceQuery) {

        // 导出不分页
        videoFaceQuery.setPageIndex(null);
        videoFaceQuery.setPageSize(null);

        // 指定时间基点：以当前时间为基点，之后的数据不处理
        if (null == videoFaceQuery.getGmtCreateEnd()) {
            videoFaceQuery.setGmtCreateEnd(new Date());
        }

        // 需要导出的数据
        ResultBean<List<VideoFaceLogVO>> listResultBean = listLog(videoFaceQuery);
        Preconditions.checkArgument(listResultBean.getSuccess(), listResultBean.getMsg());
        List<VideoFaceLogVO> data = listResultBean.getData();

        // now
        String now = LocalDateTime.now().format(formatter_yyyyMMddHHmmss);

        // 文件名
        String exportFileName = "面签记录_" + now + ".xlsx";
        String[] cellTitle = {"担保公司名称", "客户姓名", "客户身份证号码", "面签类型", "面签员工",
                "生成时间", "审核结果", "视频路径", "定位位置",};

        // 声明一个工作薄
        XSSFWorkbook workBook = new XSSFWorkbook();

        // 生成一个表格
        XSSFSheet sheet = workBook.createSheet();

        // sheet name
        workBook.setSheetName(0, "面签记录");

        // 创建表格标题行  第一行
        XSSFRow titleRow = sheet.createRow(0);
        for (int i = 0; i < cellTitle.length; i++) {
            // 列
            titleRow.createCell(i).setCellValue(cellTitle[i]);
        }

        // 插入需导出的数据
        for (int i = 0; i < data.size(); i++) {

            // 创建行
            XSSFRow row = sheet.createRow(i + 1);

            // 行数据
            VideoFaceLogVO videoFaceLogVO = data.get(i);

            // 列
            row.createCell(0).setCellValue(videoFaceLogVO.getGuaranteeCompanyName());
            row.createCell(1).setCellValue(videoFaceLogVO.getCustomerName());
            row.createCell(2).setCellValue(videoFaceLogVO.getCustomerIdCard());
            row.createCell(3).setCellValue(videoFaceLogVO.getTypeVal());
            row.createCell(4).setCellValue(videoFaceLogVO.getAuditorName());
            row.createCell(5).setCellValue(videoFaceLogVO.getGmtCreateStr());
            row.createCell(6).setCellValue(videoFaceLogVO.getActionVal());
            row.createCell(7).setCellValue(videoFaceLogVO.getPath());
            row.createCell(8).setCellValue(videoFaceLogVO.getAddress());
        }

        // 自动调整列宽
        POIUtil.autoSizeColumn(sheet, cellTitle.length);

        // file
        File file = new File("/tmp/" + exportFileName);

        FileOutputStream os = null;
        try {
            // 文件输出流
            os = new FileOutputStream(file);
            workBook.write(os);

        } catch (FileNotFoundException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            try {
                os.flush();

            } catch (IOException e) {
                logger.error("", e);
            }

            try {
                os.close();
            } catch (IOException e) {
                logger.error("", e);
            }
        }

        // 上传到OSS
        OSSUnit.uploadObject2OSS(OSSUnit.getOSSClient(), file, BUCKET_NAME_VIDEO_FACE, OSS_DISK_NAME_VIDEO_FACE);

        return ResultBean.ofSuccess(OSS_DISK_NAME_VIDEO_FACE + exportFileName);
    }

    @Override
    public ResultBean<List<String>> listQuestion(Long bankId, Long orderId, String address) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");
        Preconditions.checkNotNull(orderId, "orderId不能为空");

//        List<BankRelaQuestionDO> bankRelaQuestionDOList = bankRelaQuestionDOMapper.listByBankIdAndType(bankId, null);

        List<String> questionList = Collections.EMPTY_LIST;

        // 1
        if (BANK_ID_ICBC_HangZhou_City_Station_Branch.equals(bankId)) {
            questionList = get_Question_List_ICBC_HangZhou_City_Station_Branch(bankId, orderId);
        }
        // 2
        if (BANK_ID_ICBC_Harbin_GuXiang_Branch.equals(bankId)) {
            questionList = get_Question_List_ICBC_Harbin_City_Station_Branch(bankId, orderId);
        }

        // 3
        else if (BANK_ID_ICBC_TaiZhou_LuQiao_Branch.equals(bankId) || BANK_ID_ICBC_TaiZhou_LuQiao__Branch_TEST.equals(bankId)) {
            questionList = get_Question_List_ICBC_TaiZhou_LuQiao_Branch(bankId, orderId, address);
        }

        return ResultBean.ofSuccess(questionList);
    }

    @Override
    public VideoFaceFlagVO isFlag(Long orderId) {
        VideoFaceFlagVO videoFaceFlagVO = new VideoFaceFlagVO();
        VideoFaceLogDO videoFaceLogDO = videoFaceLogDOMapper.lastVideoFaceLogByOrderId(orderId);
        if (videoFaceLogDO == null) {
            videoFaceFlagVO.setFlag("0");
        } else {
            videoFaceFlagVO.setFlag("1");
        }
        return videoFaceFlagVO;
    }

    private List<String> get_Question_List_ICBC_Harbin_City_Station_Branch(Long bankId, Long orderId){
        VideoFaceQuestionAnswerVO videoFaceQuestionAnswerVO = setAndGetVideoFaceQuestionAnswerVO(bankId, orderId);
        String question_1 = "1、您好，请问是" + redText(videoFaceQuestionAnswerVO.getCustomerName()) + "先生/女士吗？您的身份证号码多少？参考答案：是。"+redText(videoFaceQuestionAnswerVO.getCustomerIdCard());
        String question_2 = "2、您是否通过哈尔滨云车汽车服务有限公司向工商银行哈尔滨顾乡支行申请一笔汽车专项分期付款？参考答案：是";
        String question_3 = "3、您现在的工作单位是什么？月收入多少？";
        String question_4 = "4、您所购车辆为"+redText(videoFaceQuestionAnswerVO.getCarBrandName())+redText(videoFaceQuestionAnswerVO.getCarName())
                +",车辆价格为"+redText(videoFaceQuestionAnswerVO.getCarPrice())+"，分期金额为"+redText(videoFaceQuestionAnswerVO.getBankPeriodPrincipal())
                +"元，分期期限为"+redText(videoFaceQuestionAnswerVO.getLoanTime())+"月，每月还款"+redText(videoFaceQuestionAnswerVO.getEachMonthRepay())+"元。以上信息是否确认无误?" +
                "参考答案：是";
        String question_5 ="您是否知晓本笔车贷业务由哈尔滨云车汽车服务有限公司进行担保，该公司会根据协议约定向您收取担保服务费，我行仅向您收取协议中约定的分期付款手续费。参考答案：是";
        String question_6 ="请您务必在合同上填写正确的手机号码和联系地址";
        String question_7 ="请您现在在信用卡申请表上签名";
        String question_8 ="请您现在在征信授权书上签名";
        String question_9 ="请您现在在汽车专项分期付款业务申请表上签名";
        String question_10 ="请您现在在汽车专项分期付款/担保合同上签名";
        String question_11 ="感谢您的配合，温馨提示：为了保障您的信用记录，请于每月到期还款日前存入足额本息，确保按时还款。";

        List<String> questionList = Lists.newArrayList(question_1, question_2, question_3, question_4, question_5,
                question_6, question_7, question_8, question_9, question_10,
                question_11);
        return questionList;
    }

    private List<String> get_Question_List_ICBC_TaiZhou_LuQiao_Branch(Long bankId, Long orderId, String address) {

        VideoFaceQuestionAnswerVO videoFaceQuestionAnswerVO = setAndGetVideoFaceQuestionAnswerVO(bankId, orderId);

        String question_1 = "1、你好，这里是工商银行路桥支行，请问是" + redText(videoFaceQuestionAnswerVO.getCustomerName()) + "先生/女士吗？（参考答案）是";
        String question_2 = "2、我是工商银行路桥支行的工作人员，请问您现在是否需要在我行申请一笔信用卡购车分期付款业务？您对此分期付款业务情况是否了解（参考答案）是";
        String question_3 = "3、下面需要核对一下您的身份信息（选问项，选三个或以上）";

        String yyyy_MM_dd = "";
        String idCard_last_six_num = "";

        String idCard = videoFaceQuestionAnswerVO.getCustomerIdCard();
        if (StringUtils.isNotBlank(idCard)) {
            String year = idCard.substring(6, 10);
            String month = idCard.substring(10, 12);
            String day = idCard.substring(12, 14);

            yyyy_MM_dd = year + "-" + month + "-" + day;
            idCard_last_six_num = idCard.substring(12, 18);
        }

        String question_4 = "4、Q1请问您的出生年月日是？   参考答案：" + redText(yyyy_MM_dd);
        String question_5 = "5、Q2你的身份证号码后六位是什么？   参考答案：" + redText(idCard_last_six_num);
        String question_6 = "6、Q3你所购车辆的型号是什么？   参考答案：" + redText(videoFaceQuestionAnswerVO.getCarName());
        String question_7 = "7、Q4您现在的工作单位是什么？（面签人员无法核实）";
        String question_8 = "8、Q5您单位地址是？   （面签人员无法核实）";
        String question_9 = "9、Q6您家庭住址是哪里？   （面签人员无法核实）";
        String question_10 = "10、Q7您身份证上的地址是哪里？   （面签人员无法核实）";
        String question_11 = "11、Q8您所购买车辆是什么颜色？   （面签人员无法核实）";
        String question_12 = "12、Q9你现在所处位置？   参考答案：" + redText(address);
        String question_13 = "13、请问您购买的是什么品牌的汽车？购买车辆是否自用？   （参考答案）是  车辆品牌：" + redText(videoFaceQuestionAnswerVO.getCarBrandName());
        String question_14 = "14、您了解该笔贷款是由浙江鑫宝行融资担保有限公司提供担保的吗？   参考答案：了解";
        String question_15 = "15、请您务必在合同上填写正确的手机号码和联系地址";
        String question_16 = "16、请您现在在信用卡申请书、分期付款合同以及客户告知书上签名";
        String question_17 = "17、银行：请您认真仔细阅读担保方签署相关协议，该协议内容以及协议中约定的在您未按时、足额清偿债务时担保方可采取的措施等，" +
                "均与工商银行无关。您办理该笔分期业务无需向我行和担保公司缴纳任何保证金和押金。" + HTML_NEW_LINE +
                "银行：请您务必下载和使用工银融E联，通过申请时预留手机号注册登录后，即可享受相应服务。" + HTML_NEW_LINE +
                "银行：感谢您的配合，业务办理成功后，请您留意查收合同及客户告知书并仔细阅读，后续如有问题，欢迎致电我行告知书上的汽车分期服务专线电话。";

        List<String> questionList = Lists.newArrayList(question_1, question_2, question_3, question_4, question_5,
                question_6, question_7, question_8, question_9, question_10,
                question_11, question_12, question_13, question_14, question_15,
                question_16, question_17);

        return questionList;
    }

    private List<String> get_Question_List_ICBC_HangZhou_City_Station_Branch(Long bankId, Long orderId) {

        VideoFaceQuestionAnswerVO videoFaceQuestionAnswerVO = setAndGetVideoFaceQuestionAnswerVO(bankId, orderId);

        String question_1 = "1、你好，这里是工商银行杭州城站支行，请问是" + redText(videoFaceQuestionAnswerVO.getCustomerName()) + "先生/女士吗？（参考答案）是";
        String question_2 = "2、我是工商银行杭州分行城站支行的工作人员,请问您现在是否需要在我行申请一笔信用卡汽车分期付款业务用于购买汽车？（参考答案）是";
        String question_3 = "3、下面需要核对一下您的身份信息（选问项，选三个或以上）";
        String question_4 = "4、请报一下您的身份证号？（参考答案）" + redText(videoFaceQuestionAnswerVO.getCustomerIdCard());
        String question_5 = "5、请问您现在的工作单位是什么？（参考答案）" + redText(videoFaceQuestionAnswerVO.getIncomeCertificateCompanyName());
        String question_6 = "6、请问征信查询授权书是您本人签字吗？（参考答案）是";
        String question_7 = "7、请问您办理业务所需的个人信息材料都是您本人提供并签字的吗？（参考答案）是";
        String question_8 = "8、您了解该笔贷款是由浙江鑫宝行担保有限公司担保的吗？（参考答案）是";
        String question_9 = "9、请您翻开《牡丹信用卡透支分期付款/抵押合同》 第一页确认相关信息。" +
                "您申请信用卡汽车分期业务用于购买" + redText(videoFaceQuestionAnswerVO.getCarBrandName()) + "品牌的汽车，" +
                "车辆交易总价" + redText(videoFaceQuestionAnswerVO.getCarPrice()) + "元，" +
                "您自行支付的首付款" + redText(videoFaceQuestionAnswerVO.getDownPaymentMoney()) + "元，" +
                "您申请透支金额" + redText(videoFaceQuestionAnswerVO.getLoanAmount()) + "元用于支付剩余交易款项，" +
                "申请透支金额" + redText(videoFaceQuestionAnswerVO.getBankPeriodPrincipal().subtract(videoFaceQuestionAnswerVO.getLoanAmount())) + "元用于支付汽车金融服务费，" +
                "合计透支金额" + redText(videoFaceQuestionAnswerVO.getBankPeriodPrincipal()) + "元。" +
                "您首月需还款" + redText(videoFaceQuestionAnswerVO.getFirstMonthRepay()) + "元，" +
                "每月还款" + redText(videoFaceQuestionAnswerVO.getEachMonthRepay()) + "元，"
                + redText(videoFaceQuestionAnswerVO.getLoanTime() / 12) + "年" +
                "总计需还款" + redText(videoFaceQuestionAnswerVO.getPrincipalInterestSum()) + "元。" +
                "以上信息您是否确认无误？";
        String question_10 = "10、在您足额偿清合同约定的所有债务之前，您所购车辆的商业保险保单的第一受益人为工商银行，请问您是否同意？（参考答案）是";
        String question_11 = "11、我行审批通过后将根据您的授权对您的信用卡进行激活并将您的分期款项汇给浙江鑫宝行担保有限公司账户，您是否有异议？（参考答案）否";
        String question_12 = "12、请您务必在合同上填写正确的手机号码和联系地址";
        String question_13 = "13、现在请您在信用卡分期表、分期付款/抵押合同以及客户告知书上签字";
        String question_14 = "14、银行：请您认真阅读您与担保方签订的相关协议，该协议内容以及协议中约定的在您未按时、足额清偿债务时担保方可采取的措施等，均与工商银行无关。" +
                "您办理该笔分期业务无需向我行和担保公司缴纳任何保证金和押金。" + HTML_NEW_LINE +
                "银行：请您务必下载和使用工银融E联，通过申请时预留手机号注册登录后，即可享受相应服务。" + HTML_NEW_LINE +
                "银行：感谢您的配合，业务办理成功后，我行会向您邮寄合同和客户告知书，" + HTML_NEW_LINE +
                "请您注意查收并仔细阅读，后续如有问题，欢迎致电客户告知书上的汽车分期服务专线电话。";

        List<String> questionList = Lists.newArrayList(question_1, question_2, question_3, question_4, question_5,
                question_6, question_7, question_8, question_9, question_10,
                question_11, question_12, question_13, question_14);

        return questionList;
    }

    private VideoFaceQuestionAnswerVO setAndGetVideoFaceQuestionAnswerVO(Long bankId, Long orderId) {

        VideoFaceQuestionAnswerVO videoFaceQuestionAnswerVO = new VideoFaceQuestionAnswerVO();

        // order
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        // customer info
        CustomerVO customerVO = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());
        if (null != customerVO) {
            videoFaceQuestionAnswerVO.setCustomerName(customerVO.getName());
            videoFaceQuestionAnswerVO.setCustomerIdCard(customerVO.getIdCard());
            videoFaceQuestionAnswerVO.setIncomeCertificateCompanyName(customerVO.getIncomeCertificateCompanyName());
        }

        // financial plan
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
        if (null != loanFinancialPlanDO) {
            // carPrice
            videoFaceQuestionAnswerVO.setCarPrice(loanFinancialPlanDO.getCarPrice());
            // 意向贷款金额    -> 银行分期本金
            videoFaceQuestionAnswerVO.setBankPeriodPrincipal(loanFinancialPlanDO.getBankPeriodPrincipal());
            // 每月还款
            videoFaceQuestionAnswerVO.setEachMonthRepay(loanFinancialPlanDO.getEachMonthRepay());
            // 贷款期数
            videoFaceQuestionAnswerVO.setLoanTime(loanFinancialPlanDO.getLoanTime());
            // 总还款        -> 本息合计
            videoFaceQuestionAnswerVO.setPrincipalInterestSum(loanFinancialPlanDO.getPrincipalInterestSum());
            //首付款
            videoFaceQuestionAnswerVO.setDownPaymentMoney(loanFinancialPlanDO.getDownPaymentMoney());
            //贷款金额
            videoFaceQuestionAnswerVO.setLoanAmount(loanFinancialPlanDO.getLoanAmount());
            //金融服务费
            videoFaceQuestionAnswerVO.setBankFee(loanFinancialPlanDO.getBankFee());
            //银行分期本金
            videoFaceQuestionAnswerVO.setBankPeriodPrincipal(loanFinancialPlanDO.getBankPeriodPrincipal());
            //首月还款
            videoFaceQuestionAnswerVO.setFirstMonthRepay(loanFinancialPlanDO.getFirstMonthRepay());
        }

        // carInfo
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
        if (null != loanCarInfoDO) {
            String car_brand_model_name = carService.getName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL, CAR_MODEL);
            String carBrandName = carService.getName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL, CAR_BRAND);

            videoFaceQuestionAnswerVO.setCarName(car_brand_model_name);
            videoFaceQuestionAnswerVO.setCarBrandName(carBrandName);
        }

        return videoFaceQuestionAnswerVO;
    }

    /**
     * 加红
     *
     * @param text
     * @return
     */
    private String redText(Object text) {

        boolean isEmpty = null == text || (text instanceof String && StringUtils.isBlank((String) text));
        if (isEmpty) {
            text = "未知";
        }

        // 数字：向上取整
        else if (text instanceof Number) {
            Number num = (Number) text;
            double ceil = Math.ceil(num.doubleValue());
            int intVal = Double.valueOf(ceil).intValue();
            text = intVal;
        }

        String redText = "<font color='red'>" + text + "</font>";
        return redText;
    }

    /**
     * checkPermission
     *
     * @param videoFaceQuery
     */
    private void checkPermission(VideoFaceQuery videoFaceQuery) {

        // checkPermission
        Long loginUserBankId = SessionUtils.getLoginUser().getBankId();
        Preconditions.checkNotNull(loginUserBankId, "您无权操作[视频面签]");

        Long queryBankId = videoFaceQuery.getBankId();

        // 仅管理员  拥有所有银行权限 -->  -1
        if (!ADMIN_VIDEO_FACE_BANK_ID.equals(loginUserBankId)) {

            // 不能穿空
            if (null == queryBankId) {
                videoFaceQuery.setBankId(loginUserBankId);
            } else {

                // 不能查自身以外的银行数据
                Preconditions.checkArgument(loginUserBankId.equals(queryBankId), "您无权操作：" + bankCache.getNameById(queryBankId));
            }

        }
    }

}
