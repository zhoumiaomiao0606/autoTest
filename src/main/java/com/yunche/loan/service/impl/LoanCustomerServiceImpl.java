package com.yunche.loan.service.impl;

import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.ImageUtil;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.AllCustDetailParam;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanFileService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_CANCEL;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
@Service
public class LoanCustomerServiceImpl implements LoanCustomerService {

    private static Logger logger = LoggerFactory.getLogger(LoanCustomerServiceImpl.class);


    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private OSSConfig ossConfig;


    @Override
    @Transactional
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkNotNull(principalLenderId, "主贷人ID不能为空");
        Preconditions.checkNotNull(commonLenderId, "共贷人ID不能为空");

        // get 原共贷人的 cust_relation
        Byte custRelation = loanCustomerDOMapper.getCustRelationById(commonLenderId);
        // 与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;
        // 1、当共贷人关系与主贷人关系为父母的时候，切换后，共贷人与主贷人的关系变更为子女
        // 2、当共贷人关系与主贷人关系为子女的时候，切换后，共贷人与主贷人的关系变更为父母
        if (CUST_RELATION_fu_mu.equals(custRelation)) {
            custRelation = CUST_RELATION_zi_nv;
        } else if (CUST_RELATION_zi_nv.equals(custRelation)) {
            custRelation = CUST_RELATION_fu_mu;
        }

        // 编辑原主贷人
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        principalLenderDO.setId(principalLenderId);
        principalLenderDO.setCustType(CUST_TYPE_COMMON);
        principalLenderDO.setPrincipalCustId(commonLenderId);
        principalLenderDO.setCustRelation(custRelation);
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);


        // 编辑原共贷人
        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
        commonLenderDO.setId(commonLenderId);
        commonLenderDO.setCustType(CUST_TYPE_PRINCIPAL);
        commonLenderDO.setPrincipalCustId(commonLenderId);
        commonLenderDO.setCustRelation(CUST_RELATION_self);
        commonLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);


        // 编辑所有(其他)关联人的 主贷人ID
        loanCustomerDOMapper.updatePrincipalCustId(principalLenderId, commonLenderId);


        // 编辑业务单主贷人
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(orderId);
        loanOrderDO.setLoanCustomerId(commonLenderId);
        loanOrderDO.setGmtModify(new Date());
        loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        return ResultBean.ofSuccess(null, "主共贷人切换成功");
    }

    @Override
    public ResultBean<CustDetailVO> detailAll(Long orderId, Byte fileUploadType) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanOrderDOMapper.getCustIdById(orderId);

        // 根据主贷人ID获取客户详情列表
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalLenderId, null, VALID_STATUS);

        CustDetailVO custDetailVO = new CustDetailVO();
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {
            // 填充客户详情信息
            fillCustInfo(custDetailVO, loanCustomerDOList, fileUploadType);
        }

        // 视频面签是否上传成功
        VideoFaceLogDO videoFaceLogDO = videoFaceLogDOMapper.lastVideoFaceLogByOrderId(orderId);
        if (null != videoFaceLogDO) {

            String path = videoFaceLogDO.getPath();
            if (StringUtils.isNotBlank(path)) {

                // http://yunche-videosign.oss-cn-hangzhou.aliyuncs.com/video/2018/201808/20180814/11-06-00-128_宋绍兰_57.mp4
                // https://yunche-videosign.oss-cn-hangzhou.aliyuncs.com/MP4/2018/201808/20180813/1808131023390914733/1534130255581.364990.mp4
                if (path.contains("://yunche-videosign.oss-cn-hangzhou.aliyuncs.com")) {
                    custDetailVO.setSaveVideoFace(true);
                }
            }
        }
        custDetailVO.setSaveVideoFace(false);

        return ResultBean.ofSuccess(custDetailVO);
    }


    @Override
    @Transactional
    public ResultBean<Void> updateAll(AllCustDetailParam allCustDetailParam) {
        Preconditions.checkNotNull(allCustDetailParam, "客户信息不能为空");

        checkIdCard(allCustDetailParam);

        updateOrInsertLoanCustomer(allCustDetailParam);

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }


    @Override
    @Transactional
    public ResultBean<Long> create(LoanCustomerDO loanCustomerDO) {
        Preconditions.checkNotNull(loanCustomerDO, "客户信息不能为空");
        Preconditions.checkNotNull(loanCustomerDO.getCustType(), "客户类型不能为空");

        if (!CUST_TYPE_PRINCIPAL.equals(loanCustomerDO.getCustType())) {
            Preconditions.checkNotNull(loanCustomerDO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        // 共贷人与主贷人关系只能为配偶
        if (CUST_TYPE_COMMON.equals(loanCustomerDO.getCustType())) {
            Byte custRelation = loanCustomerDO.getCustRelation();
            if (null != custRelation) {
                Preconditions.checkArgument(CUST_RELATION_pei_ou.equals(custRelation), "共贷人与主贷人关系只能为[配偶]");
            }
        }

        loanCustomerDO.setStatus(VALID_STATUS);
        loanCustomerDO.setGmtCreate(new Date());
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");

        if (CUST_TYPE_PRINCIPAL.equals(loanCustomerDO.getCustType())) {
            LoanCustomerDO customerDO = new LoanCustomerDO();
            customerDO.setId(loanCustomerDO.getId());
            customerDO.setPrincipalCustId(loanCustomerDO.getId());
            int updateCount = loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
            Preconditions.checkArgument(updateCount > 0, "设置主贷人ID失败");
        }

        return ResultBean.ofSuccess(loanCustomerDO.getId(), "创建客户信息成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(LoanCustomerDO loanCustomerDO) {
        Preconditions.checkNotNull(loanCustomerDO, "客户信息不能为空");
        Preconditions.checkNotNull(loanCustomerDO.getId(), "客户ID不能为空");

        // 共贷人与主贷人关系只能为配偶
        if (CUST_TYPE_COMMON.equals(loanCustomerDO.getCustType())) {
            Byte custRelation = loanCustomerDO.getCustRelation();
            if (null != custRelation) {
                Preconditions.checkArgument(CUST_RELATION_pei_ou.equals(custRelation), "共贷人与主贷人关系只能为[配偶]");
            }
        }

        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "编辑客户信息失败");

        return ResultBean.ofSuccess(null, "编辑客户信息成功");
    }

    @Override
    public CustomerVO getById(Long id) {
        Preconditions.checkNotNull(id, "客户ID不能为空");

        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(loanCustomerDO, "客户ID有误，客户不存在");

        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(loanCustomerDO, customerVO);
        // 无files

        return customerVO;
    }

    @Override
    public ResultBean<LoanRepeatVO> checkRepeat(String idCard, Long orderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(idCard), "身份证号不能为空");

        LoanRepeatVO loanRepeatVO = new LoanRepeatVO();

        List<Long> principalCustIdList = loanCustomerDOMapper.listPrincipalCustIdByIdCard(idCard);

        if (!CollectionUtils.isEmpty(principalCustIdList)) {

            List<String> orderIdList = principalCustIdList.parallelStream()
                    .filter(Objects::nonNull)
                    .map(customerId -> {

                        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByCustomerId(customerId);
                        if (null != loanOrderDO) {
                            //过滤状态为弃单的订单
                            LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(loanOrderDO.getId());
                            if (null != loanProcessDO && !ORDER_STATUS_CANCEL.equals(loanProcessDO.getOrderStatus())) {
                                return String.valueOf(loanOrderDO.getId());
                            }
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());


            if (null != orderId && !CollectionUtils.isEmpty(orderIdList)) {
                orderIdList.remove(String.valueOf(orderId));
            }
            loanRepeatVO.setOrderIdList(orderIdList);
        }

        return ResultBean.ofSuccess(loanRepeatVO, "成功");
    }

    @Override
    public ResultBean<CustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据主贷人ID获取客户详情列表
        ResultBean<CustDetailVO> resultBean = detailAll(orderId, null);
        return resultBean;
    }

    @Override
    @Transactional
    public ResultBean<Long> addRelaCustomer(CustomerParam customerParam) {
        Preconditions.checkNotNull(customerParam, "客户信息不能为空");

        // check
        checkIdCard(customerParam.getPrincipalCustId(), customerParam.getIdCard());

        // convert
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        convertLoanCustomer(customerParam, loanCustomerDO);

        ResultBean<Long> resultBean = create(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        // 文件信息保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(resultBean.getData(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        // enable_type：增信增补(自动)打回----如果是特殊关联人--则不需要征信打回
        if (!CUST_TYPE_SPECIAL_CONTACT.equals(customerParam.getCustType()))
        {
            enable(String.valueOf(resultBean.getData()), ENABLE_TYPE_CREDIT_SUPPLEMENT);
        }

        return ResultBean.ofSuccess(resultBean.getData(), "创建关联人成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> delRelaCustomer(Long customerId) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        loanCustomerDO.setId(customerId);
        loanCustomerDO.setStatus(INVALID_STATUS);

        ResultBean<Void> resultBean = update(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        return ResultBean.ofSuccess(null, "删除关联人成功");
    }

    @Override
    public BankAndSocietyResultVO bankPicExport(List<Long> list) {
        BankAndSocietyResultVO bankAndSocietyResultVO = new BankAndSocietyResultVO();
        List<String> filePathString = new ArrayList<>();
        if (list != null) {
            if (list.size() != 0) {
                List<BankAndSocietyPicVO> fileList = loanFileDOMapper.selectFileInfoByCusId(list);
                if (fileList != null) {
                    for (BankAndSocietyPicVO bankAndSocietyPicVO : fileList) {
                        List<String> picPath = new ArrayList<>();
                        String[] total = bankAndSocietyPicVO.getPath().replace("\"", "").replace("[", "").replace("]", "").split(",");
                        for (String s : total) {
                            if (s != null && !"".equals(s)) {
                                picPath.add(s);
                            }
                        }
                        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(bankAndSocietyPicVO.getCustomerId(), null);
                        if (loanCustomerDO != null && picPath.size() != 0) {
                            String fileName = loanCustomerDO.getId() + loanCustomerDO.getName() + loanCustomerDO.getIdCard() + ".jpg";
                            String retPath = ImageUtil.mergeImage2Pic_NO_COMPROCESS(fileName, picPath);
                            File file = new File(retPath);
                            //上传OSS
                            OSSClient ossClient = OSSUnit.getOSSClient();
                            String bucketName = ossConfig.getBucketName();
                            String diskName = "img" + File.separator + "bank";
                            OSSUnit.deleteFile(ossClient, bucketName, diskName + File.separator, fileName.toString());
                            OSSUnit.uploadObject2OSS(ossClient, file, bucketName, diskName + File.separator);
                            retPath = retPath.substring(4);
                            filePathString.add(diskName + retPath);
                        }
                    }
                }
            }
        }
        bankAndSocietyResultVO.setPicList(filePathString);
        return bankAndSocietyResultVO;
    }

    @Override
    public BankAndSocietyResultVO societyPicExport(List<Long> list) {
        BankAndSocietyResultVO bankAndSocietyResultVO = new BankAndSocietyResultVO();
        List<String> filePathString = new ArrayList<>();
        if (list != null) {
            if (list.size() != 0) {
                List<BankAndSocietyPicVO> fileList = loanFileDOMapper.selectSocFileInfoByCusId(list);
                if (fileList != null) {
                    for (BankAndSocietyPicVO bankAndSocietyPicVO : fileList) {
                        List<String> picPath = new ArrayList<>();
                        String[] total = bankAndSocietyPicVO.getPath().replace("\"", "").replace("[", "").replace("]", "").split(",");
                        for (String s : total) {
                            if (s != null && !"".equals(s)) {
                                picPath.add(s);
                            }
                        }
                        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(bankAndSocietyPicVO.getCustomerId(), null);
                        if (loanCustomerDO != null && picPath.size() != 0) {
                            String fileName = loanCustomerDO.getId() + loanCustomerDO.getName() + loanCustomerDO.getIdCard() + ".jpg";
                            String retPath = ImageUtil.mergeImage2Pic_NO_COMPROCESS(fileName, picPath);

                            File file = new File(retPath);
                            //上传OSS
                            OSSClient ossClient = OSSUnit.getOSSClient();
                            String bucketName = ossConfig.getBucketName();
                            String diskName = "img" + File.separator + "society";
                            OSSUnit.deleteFile(ossClient, bucketName, diskName + File.separator, fileName.toString());
                            OSSUnit.uploadObject2OSS(ossClient, file, bucketName, diskName + File.separator);
                            retPath = retPath.substring(4);
                            filePathString.add(diskName + retPath);
                        }
                    }
                }
            }
        }
        bankAndSocietyResultVO.setPicList(filePathString);
        return bankAndSocietyResultVO;
    }

    @Override
    @Transactional
    public Long enable(String ids, Byte enableType) {
        Preconditions.checkArgument(StringUtils.isNotBlank(ids), "ids不能为空");
        Preconditions.checkNotNull(enableType, "enableType不能为空");

        // 1、更新所选客户 打回状态：1 -> 已打回(可编辑)
        List<Long> idList = Arrays.stream(ids.split("\\,"))
                .filter(StringUtils::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toList());

        Preconditions.checkArgument(!CollectionUtils.isEmpty(idList), "ids不能为空");

        long count = loanCustomerDOMapper.batchUpdateEnable(idList, BaseConst.K_YORN_YES, enableType);

        // 2、打回标记重置   此次未打回的客户全部重置为：  0 -> 未打回(不可编辑)
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(idList.get(0), VALID_STATUS);
        Preconditions.checkNotNull(loanCustomerDO, "客户不存在，客户ID=" + idList.get(0));
        Long principalCustId = loanCustomerDO.getPrincipalCustId();
        Preconditions.checkNotNull(principalCustId, "客户异常，无关联主贷人！客户ID=" + idList.get(0));


        List<Long> allCustomerId = loanCustomerDOMapper.listIdByPrincipalCustIdAndType(principalCustId, null, VALID_STATUS);
        allCustomerId.removeAll(idList);

        if (!CollectionUtils.isEmpty(allCustomerId)) {

            // 其他客户  >>  重置为：0 -> 未打回(不可编辑)
            long count2 = loanCustomerDOMapper.batchUpdateEnable(allCustomerId, BaseConst.K_YORN_NO, null);
        }

        return count;
    }

    /**
     * 重置订单下所有客户的可编辑标记  ： 0-否；
     *
     * @param principalId
     */
    @Override
    public void updateCustomerEnable(Long principalId) {

        List<Long> customerIdList = loanCustomerDOMapper.listIdByPrincipalCustIdAndType(principalId, null, VALID_STATUS);

        loanCustomerDOMapper.batchUpdateEnable(customerIdList, BaseConst.K_YORN_NO, null);
    }


    private void convertLoanCustomer(CustomerParam customerParam, LoanCustomerDO loanCustomerDO) {
        if (null != customerParam) {
            BeanUtils.copyProperties(customerParam, loanCustomerDO);
        }
    }

    /**
     * 填充客户详情信息
     *
     * @param custDetailVO
     * @param loanCustomerDOList
     * @param fileUploadType
     */
    private void fillCustInfo(CustDetailVO custDetailVO, List<LoanCustomerDO> loanCustomerDOList, Byte fileUploadType) {

        List<CustomerVO> commonLenderList = Lists.newArrayList();
        List<CustomerVO> guarantorList = Lists.newArrayList();
        List<CustomerVO> emergencyContactList = Lists.newArrayList();
        List<CustomerVO> specialContactList = Lists.newArrayList();

        loanCustomerDOList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 主贷人
                    if (CUST_TYPE_PRINCIPAL.equals(e.getCustType())) {

                        CustomerVO principalLender = new CustomerVO();
                        BeanUtils.copyProperties(e, principalLender);

                        // fillFiles
                        fillFiles(principalLender, fileUploadType);

                        // fillCredit
                        fillCredit(principalLender, e.getId());
                        custDetailVO.setPrincipalLender(principalLender);
                    }

                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {

                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);

                        // fillFiles
                        fillFiles(commonLender, fileUploadType);

                        // fillCredit
                        fillCredit(commonLender, e.getId());

                        commonLenderList.add(commonLender);
                    }

                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {

                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);

                        // fillFiles
                        fillFiles(guarantor, fileUploadType);

                        // fillCredit
                        fillCredit(guarantor, e.getId());

                        guarantorList.add(guarantor);
                    }

                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {

                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);

                        // fillFiles
                        fillFiles(emergencyContact, fileUploadType);

                        // fillCredit
                        fillCredit(emergencyContact, e.getId());

                        emergencyContactList.add(emergencyContact);
                    }
                    //特殊联系人
                    else if (CUST_TYPE_SPECIAL_CONTACT.equals(e.getCustType())) {

                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);

                        // fillFiles
                        //fillFiles(emergencyContact, fileUploadType);

                        // fillCredit
                        //fillCredit(emergencyContact, e.getId());

                        specialContactList.add(emergencyContact);
                    }
                });

        List<CustomerVO> sortedCommonLenderList = commonLenderList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());
        List<CustomerVO> sortedGuarantorList = guarantorList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());
        List<CustomerVO> sortedEmergencyContactList = emergencyContactList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());
        List<CustomerVO> sortedSpecialContactList = emergencyContactList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());

        custDetailVO.setCommonLenderList(sortedCommonLenderList);
        custDetailVO.setGuarantorList(sortedGuarantorList);
        custDetailVO.setEmergencyContactList(sortedEmergencyContactList);
        custDetailVO.setSpecialContactList(sortedSpecialContactList);
    }

    /**
     * 填充征信信息
     *
     * @param principalLender
     * @param customerId
     */
    private void fillCredit(CustomerVO principalLender, Long customerId) {
        List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(customerId, null);
        if (!CollectionUtils.isEmpty(loanCreditInfoDOS)) {
            loanCreditInfoDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        if (CREDIT_TYPE_BANK.equals(e.getType())) {
                            principalLender.setBankCreditResult(e.getResult());
                            principalLender.setBankCreditInfo(e.getInfo());
                            principalLender.setBankCreditNote(loanQueryDOMapper.selectLastBankInterfaceSerialNoteByTransCode(e.getCustomerId(), "applyCredit"));
                            principalLender.setBankCreditStatus(loanQueryDOMapper.selectLastBankInterfaceSerialStatusByTransCode(e.getCustomerId(), "applyCredit"));
                        } else if (CREDIT_TYPE_SOCIAL.equals(e.getType())) {
                            principalLender.setSocialCreditResult(e.getResult());
                            principalLender.setSocialCreditInfo(e.getInfo());
                        }
                    });
        }
    }

    private void fillFiles(CustomerVO customerVO, Byte fileUploadType) {
        ResultBean<List<FileVO>> fileResultBean = loanFileService.listByCustomerId(customerVO.getId(), fileUploadType);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());
        customerVO.setFiles(fileResultBean.getData());
    }

    private void updateOrInsertLoanCustomer(AllCustDetailParam allCustDetailParam) {

        // 主贷人
        CustomerParam principalLender = allCustDetailParam.getPrincipalLender();
        updateOrInsertCustomer(principalLender);

        // 共贷人列表
        List<CustomerParam> commonLenderList = allCustDetailParam.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderList)) {

            commonLenderList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 担保人列表
        List<CustomerParam> guarantorList = allCustDetailParam.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorList)) {

            guarantorList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 紧急联系人列表
        List<CustomerParam> emergencyContactList = allCustDetailParam.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactList)) {

            emergencyContactList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 特殊联系人列表
        List<CustomerParam> specialContactList = allCustDetailParam.getSpecialContactList();
        if (!CollectionUtils.isEmpty(specialContactList)) {

            specialContactList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }
    }

    private void updateOrInsertCustomer(CustomerParam customerParam) {
        if (null == customerParam) {
            return;
        }

        if (null == customerParam.getId()) {
            // insert
            createLoanCustomer(customerParam);
        } else {
            // update
            LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
            BeanUtils.copyProperties(customerParam, loanCustomerDO);
            ResultBean<Void> resultBean = update(loanCustomerDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

            // 文件信息保存
            ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(customerParam.getId(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
            Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());
        }
    }

    /**
     * 新增用户信息
     *
     * @param customerParam
     * @return
     */
    private Long createLoanCustomer(CustomerParam customerParam) {


        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerParam, loanCustomerDO);

        ResultBean<Long> createCustomerResult = create(loanCustomerDO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), "创建客户信息失败");

        // 文件信息保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(createCustomerResult.getData(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        // 返回客户ID
        return createCustomerResult.getData();
    }


    /**
     * 身份证重复校验
     *
     * @param principalCustId
     * @param idCard
     */
    private void checkIdCard(Long principalCustId, String idCard) {

        if (StringUtils.isNotBlank(idCard)) {

            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalCustId, null, VALID_STATUS);
            if (!CollectionUtils.isEmpty(loanCustomerDOS)) {

                String trimIdCard = idCard.trim();
                loanCustomerDOS.stream()
                        .forEach(e -> {

                            if (StringUtils.isNotBlank(e.getIdCard())) {

                                Preconditions.checkArgument(!trimIdCard.equals(e.getIdCard().trim()),
                                        "有身份证号码重复，请先检查再提交");
                            }
                        });
            }
        }
    }


    /**
     * 身份证重复校验
     *
     * @param allCustDetailParam
     */
    private void checkIdCard(AllCustDetailParam allCustDetailParam) {

        CustomerParam principalLender = allCustDetailParam.getPrincipalLender();
        if (principalLender == null) {
            return;
        }
        Preconditions.checkNotNull(principalLender, "主贷人不能为空");

        Long principalCustId = principalLender.getId();
        Preconditions.checkNotNull(principalCustId, "主贷人Id不能为空");

        List<String> idCardList = Lists.newArrayList(principalLender.getIdCard());

        List<CustomerParam> guarantorList = allCustDetailParam.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorList)) {

            guarantorList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String idCard = e.getIdCard();
                        if (StringUtils.isNotBlank(idCard)) {

                            idCard = idCard.trim();
                            Preconditions.checkArgument(!idCardList.contains(idCard), "有身份证号码重复，请先检查再提交");
                            idCardList.add(idCard);
                        }
                    });
        }

        List<CustomerParam> emergencyContactList = allCustDetailParam.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactList)) {

            emergencyContactList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String idCard = e.getIdCard();
                        if (StringUtils.isNotBlank(idCard)) {

                            idCard = idCard.trim();
                            Preconditions.checkArgument(!idCardList.contains(idCard), "有身份证号码重复，请先检查再提交");
                            idCardList.add(idCard);
                        }
                    });
        }
        List<CustomerParam> specialContactList = allCustDetailParam.getSpecialContactList();
        if (!CollectionUtils.isEmpty(specialContactList)) {

            specialContactList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String idCard = e.getIdCard();
                        if (StringUtils.isNotBlank(idCard)) {

                            idCard = idCard.trim();
                            Preconditions.checkArgument(!idCardList.contains(idCard), "有身份证号码重复，请先检查再提交");
                            idCardList.add(idCard);
                        }
                    });
        }

        List<CustomerParam> commonLenderList = allCustDetailParam.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderList)) {

            commonLenderList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String idCard = e.getIdCard();
                        if (StringUtils.isNotBlank(idCard)) {

                            idCard = idCard.trim();
                            Preconditions.checkArgument(!idCardList.contains(idCard), "有身份证号码重复，请先检查再提交");
                            idCardList.add(idCard);
                        }
                    });
        }

    }
}
