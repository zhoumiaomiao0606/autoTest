package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.domain.entity.LoanInfoSupplementDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InfoSupplementParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanInfoSupplementDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_SUPPLEMENT;

/**
 * @author liuzhe
 * @date 2018/7/25
 */
@Service
public class LoanInfoSupplementServiceImpl implements LoanInfoSupplementService {

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private DictService dictService;

    @Autowired
    private LoanBaseInfoService loanBaseInfoService;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanInfoSupplementDOMapper loanInfoSupplementDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;


    @Override
    public ResultBean<Void> upload(InfoSupplementParam infoSupplementParam) {
        Preconditions.checkNotNull(infoSupplementParam.getCustomerId(), "客户ID不能为空");
        Preconditions.checkNotNull(infoSupplementParam.getSupplementOrderId(), "增补单ID不能为空");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(infoSupplementParam.getFiles()) ||
                StringUtils.isNotBlank(infoSupplementParam.getRemark()), "资料信息或备注为空");

        // files
        saveFiles(infoSupplementParam);

        Long suppermentOrderId = infoSupplementParam.getSupplementOrderId();
        String remark = infoSupplementParam.getRemark();

        LoanInfoSupplementDO loanInfoSupplementDO = new LoanInfoSupplementDO();
        loanInfoSupplementDO.setRemark(remark);
        loanInfoSupplementDO.setId(suppermentOrderId);
        int count = loanInfoSupplementDOMapper.updateByPrimaryKeySelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "增补失败");

        List<FileVO> files = infoSupplementParam.getFiles();
        files.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 已经增补过的图片 ——> 正常上传
                    ResultBean<Void> moveResultBean = loanFileService.moveOldSupplementToNormal(infoSupplementParam.getCustomerId(), e.getType());
                    Preconditions.checkArgument(moveResultBean.getSuccess(), moveResultBean.getMsg());

                    // 保存新增补的文件 ——> 增补上传
                    ResultBean<Void> saveResultBean = loanFileService.saveNewSupplementFiles(infoSupplementParam.getCustomerId(), e.getType(), e.getUrls());
                    Preconditions.checkArgument(saveResultBean.getSuccess(), saveResultBean.getMsg());
                });

        return ResultBean.ofSuccess(null, "资料增补成功");
    }

    @Override
    public ResultBean<InfoSupplementVO> detail__(Long supplementOrderId) {
        Preconditions.checkNotNull(supplementOrderId, "增补单不能为空");

        LoanInfoSupplementDO loanInfoSupplementDO = loanInfoSupplementDOMapper.selectByPrimaryKey(supplementOrderId);
        Preconditions.checkNotNull(loanInfoSupplementDO, "增补单不存在");

        InfoSupplementVO infoSupplementVO = new InfoSupplementVO();

        // 增补信息
        infoSupplementVO.setSupplementOrderId(supplementOrderId);
        infoSupplementVO.setSupplementType(loanInfoSupplementDO.getType());
        infoSupplementVO.setSupplementTypeText(getInfoSupplementTypeText(loanInfoSupplementDO.getType()));
        infoSupplementVO.setSupplementInfo(loanInfoSupplementDO.getInfo());
        infoSupplementVO.setSupplementContent(loanInfoSupplementDO.getContent());
        infoSupplementVO.setSupplementStartDate(loanInfoSupplementDO.getStartTime());
        infoSupplementVO.setRemark(loanInfoSupplementDO.getRemark());

        Long orderId = loanInfoSupplementDO.getOrderId();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        infoSupplementVO.setOrderId(String.valueOf(orderId));

        // 客户信息
        if (null != loanOrderDO.getLoanCustomerId()) {
            CustomerVO customerVO = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());

            if (null != customerVO) {
                infoSupplementVO.setCustomerId(customerVO.getId());
                infoSupplementVO.setCustomerName(customerVO.getName());
                infoSupplementVO.setIdCard(customerVO.getIdCard());
            }
        }

        // 业务员信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoResultBean.getSuccess(), loanBaseInfoResultBean.getMsg());
        LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoResultBean.getData();
        if (null != loanBaseInfoVO) {
            BaseVO salesman = loanBaseInfoVO.getSalesman();
            if (null != salesman) {
                infoSupplementVO.setSalesmanId(salesman.getId());
                infoSupplementVO.setSalesmanName(salesman.getName());
            }
            BaseVO partner = loanBaseInfoVO.getPartner();
            if (null != partner) {
                infoSupplementVO.setPartnerId(partner.getId());
                infoSupplementVO.setPartnerName(partner.getName());
            }
        }

        // 客户及文件分类列表
        fillCustomerAndFile(infoSupplementVO, orderId);

        return ResultBean.ofSuccess(infoSupplementVO);
    }


    @Override
    public ResultBean<Void> save(InfoSupplementParam infoSupplementParam) {
        Preconditions.checkNotNull(infoSupplementParam.getCustomerId(), "客户ID不能为空");
        Preconditions.checkNotNull(infoSupplementParam.getSupplementOrderId(), "增补单ID不能为空");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(infoSupplementParam.getFiles()) ||
                StringUtils.isNotBlank(infoSupplementParam.getRemark()), "资料信息或备注为空");


        LoanInfoSupplementDO loanInfoSupplementDO = new LoanInfoSupplementDO();

        loanInfoSupplementDO.setId(infoSupplementParam.getSupplementOrderId());
        loanInfoSupplementDO.setRemark(infoSupplementParam.getRemark());

        int count = loanInfoSupplementDOMapper.updateByPrimaryKeySelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "保存失败");

        // save file
        loanFileService.save(infoSupplementParam.getFiles(), infoSupplementParam.getSupplementOrderId(),
                infoSupplementParam.getCustomerId(), UPLOAD_TYPE_SUPPLEMENT);

        return ResultBean.ofSuccess(null, "保存成功");
    }

    @Override
    public ResultBean<InfoSupplementVO2> detail(Long infoSupplementId) {
        Preconditions.checkNotNull(infoSupplementId, "增补单ID不能为空");

        // getAll
        List<InfoSupplementVO2> infoSupplementVO2List = loanQueryDOMapper.selectUniversalInfoSupplement(infoSupplementId);

        // check
        Preconditions.checkArgument(!CollectionUtils.isEmpty(infoSupplementVO2List), "增补单不存在");

        // group by
        List<InfoSupplementVO2> infoSupplementVOList = groupByInfoSupplementId(infoSupplementVO2List);

        return ResultBean.ofSuccess(infoSupplementVOList.get(0));
    }

    @Override
    public ResultBean<List<InfoSupplementVO2>> history(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        // getAll
        List<InfoSupplementVO2> infoSupplementVO2List = loanQueryDOMapper.selectUniversalCollectionInfoSupplement(orderId);

        // group by
        List<InfoSupplementVO2> infoSupplementVOList = groupByInfoSupplementId(infoSupplementVO2List);

        // sort
        List<InfoSupplementVO2> sortList = sortByEndTime(infoSupplementVOList);

        return ResultBean.ofSuccess(sortList);
    }

    /**
     * sort
     *
     * @param infoSupplementVOList
     */
    private List<InfoSupplementVO2> sortByEndTime(List<InfoSupplementVO2> infoSupplementVOList) {

        if (!CollectionUtils.isEmpty(infoSupplementVOList)) {

            List<InfoSupplementVO2> sortList = infoSupplementVOList.stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(InfoSupplementVO2::getEndTime).reversed())
                    .collect(Collectors.toList());

            return sortList;
        }

        return infoSupplementVOList;
    }

    private List<InfoSupplementVO2> groupByInfoSupplementId(List<InfoSupplementVO2> infoSupplementVOList) {

        if (CollectionUtils.isEmpty(infoSupplementVOList)) {
            return Collections.EMPTY_LIST;
        }

        Map<Long, InfoSupplementVO2> idDetailMap = Maps.newHashMap();

        // text - kvMap
        Map<String, String> kvMap = dictService.getKVMap("infoSupplementType");

        infoSupplementVOList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Long infoSupplementId = e.getSupplementOrderId();

                    if (idDetailMap.containsKey(infoSupplementId)) {

                        InfoSupplementVO2 infoSupplementVO = idDetailMap.get(infoSupplementId);

                        List<FileVO2> files = infoSupplementVO.getFiles();

                        // file
                        FileVO2 fileVO = new FileVO2();
                        fileVO.setFileId(e.getFileId());
                        fileVO.setType(e.getFileType());
                        fileVO.setName(e.getFileTypeText());
                        fileVO.setUrls(e.getFilePath());

                        // add files
                        files.add(fileVO);

                    } else {

                        InfoSupplementVO2 infoSupplementVO = new InfoSupplementVO2();

                        BeanUtils.copyProperties(e, infoSupplementVO);

                        // type text
                        String supplementTypeText = getInfoSupplementTypeText(e.getType(), kvMap);
                        infoSupplementVO.setTypeText(supplementTypeText);

                        // files
                        FileVO2 fileVO = new FileVO2();
                        fileVO.setFileId(e.getFileId());
                        fileVO.setType(e.getFileType());
                        fileVO.setName(e.getFileTypeText());
                        fileVO.setUrls(e.getFilePath());

                        infoSupplementVO.setFiles(Lists.newArrayList(fileVO));

                        idDetailMap.put(infoSupplementId, infoSupplementVO);
                    }

                });

        // val
        Collection<InfoSupplementVO2> values = idDetailMap.values();
        return Lists.newArrayList(values);
    }


    /**
     * 填充客户及文件信息
     *
     * @param infoSupplementVO
     * @param orderId
     */
    private void fillCustomerAndFile(InfoSupplementVO infoSupplementVO, Long orderId) {

        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId, UPLOAD_TYPE_SUPPLEMENT);
        Preconditions.checkArgument(custDetailVOResultBean.getSuccess(), custDetailVOResultBean.getMsg());

        CustDetailVO custDetailVO = custDetailVOResultBean.getData();
        if (null != custDetailVO) {

            CustomerVO principalLenderVO = custDetailVO.getPrincipalLender();
//            List<CustomerVO> commonLenderVOList = custDetailVO.getCommonLenderList();
//            List<CustomerVO> guarantorVOList = custDetailVO.getGuarantorList();
//            List<CustomerVO> emergencyContactVOList = custDetailVO.getEmergencyContactList();

            if (null != principalLenderVO) {
                InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
                fillCustomerFile(principalLenderVO, customerFile);
                infoSupplementVO.setPrincipalLender(customerFile);
            }

//            if (!CollectionUtils.isEmpty(commonLenderVOList)) {
//
//                List<InfoSupplementVO.CustomerFile> commonLenderList = Lists.newArrayList();
//                commonLenderVOList.parallelStream()
//                        .filter(Objects::nonNull)
//                        .forEach(e -> {
//                            InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
//                            fillCustomerFile(e, customerFile);
//                            commonLenderList.add(customerFile);
//                        });
//                infoSupplementVO.setCommonLenderList(commonLenderList);
//            }
//
//            if (!CollectionUtils.isEmpty(guarantorVOList)) {
//
//                List<InfoSupplementVO.CustomerFile> guarantorList = Lists.newArrayList();
//                guarantorVOList.parallelStream()
//                        .filter(Objects::nonNull)
//                        .forEach(e -> {
//                            InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
//                            fillCustomerFile(e, customerFile);
//                            guarantorList.add(customerFile);
//                        });
//                infoSupplementVO.setGuarantorList(guarantorList);
//            }
//
//            if (!CollectionUtils.isEmpty(emergencyContactVOList)) {
//                List<InfoSupplementVO.CustomerFile> emergencyContactList = Lists.newArrayList();
//                emergencyContactVOList.parallelStream()
//                        .filter(Objects::nonNull)
//                        .forEach(e -> {
//                            InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
//                            fillCustomerFile(e, customerFile);
//                            emergencyContactList.add(customerFile);
//                        });
//                infoSupplementVO.setEmergencyContactList(emergencyContactList);
//            }
        }
    }

    private void fillCustomerFile(CustomerVO customerVO, InfoSupplementVO.CustomerFile customerFile) {
        customerFile.setCustomerId(customerVO.getId());
        customerFile.setCustomerName(customerVO.getName());
        customerFile.setFiles(customerVO.getFiles());
    }

    /**
     * 增补类型文本值
     *
     * @param supplementType
     * @return
     */
    public String getInfoSupplementTypeText(Byte supplementType) {

        Map<String, String> kvMap = dictService.getKVMap("infoSupplementType");

        return getInfoSupplementTypeText(supplementType, kvMap);
    }

    /**
     * 增补类型文本值
     *
     * @param supplementType
     * @param kvMap
     * @return
     */
    public String getInfoSupplementTypeText(Byte supplementType, Map<String, String> kvMap) {

        if (CollectionUtils.isEmpty(kvMap)) {
            kvMap = dictService.getKVMap("infoSupplementType");
        }

        String supplementTypeText = kvMap.get(String.valueOf(supplementType));

        return supplementTypeText;
    }


    /**
     * save
     *
     * @param infoSupplementParam
     */
    private void saveFiles(InfoSupplementParam infoSupplementParam) {

        LoanInfoSupplementDO loanInfoSupplementDO = new LoanInfoSupplementDO();

        loanInfoSupplementDO.setId(infoSupplementParam.getSupplementOrderId());

        int count = loanInfoSupplementDOMapper.updateByPrimaryKeySelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "保存失败");
    }
}
