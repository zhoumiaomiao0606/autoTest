package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanInfoSupplementDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InfoSupplementParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanInfoSupplementDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
    private LoanQueryService loanQueryService;


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
        Preconditions.checkArgument(
                !(CollectionUtils.isEmpty(infoSupplementParam.getFiles()) && StringUtils.isNotBlank(infoSupplementParam.getRemark())),
                "资料信息和备注不能都为空");

        // save remark
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
    public ResultBean<UniversalInfoSupplementVO> detail(Long infoSupplementId) {

        UniversalInfoSupplementVO detail = loanQueryService.selectUniversalInfoSupplementDetail(infoSupplementId);

        return ResultBean.ofSuccess(detail);
    }

    @Override
    public ResultBean<List<UniversalInfoSupplementVO>> history(Long orderId) {

        List<UniversalInfoSupplementVO> history = loanQueryService.selectUniversalInfoSupplementHistory(orderId);

        return ResultBean.ofSuccess(history);
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
