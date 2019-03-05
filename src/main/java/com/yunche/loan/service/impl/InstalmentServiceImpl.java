package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.LoanFileConst;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.constant.TermFileEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.InstalmentUpdateParam;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InstalmentService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static com.yunche.loan.config.constant.LoanFileEnum.*;

import static com.yunche.loan.config.constant.LoanFileEnum.CAR_INVOICE;
import static com.yunche.loan.config.constant.LoanFileEnum.S9016;

@Service
@Transactional
public class InstalmentServiceImpl implements InstalmentService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {


        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (orderDO == null) {
            throw new BizException("此订单不存在");
        }
        //台州
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(orderDO.getLoanBaseInfoId());
        if("中国工商银行台州路桥支行".equals(loanBaseInfoDO.getBank())){
            //发票
            // INVOICE((byte) 19, "发票"),    S9012((byte) 76, "购车发票"),
            List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(orderDO.getLoanCustomerId(), S9012.getType(), LoanFileConst.UPLOAD_TYPE_NORMAL);
            if(CollectionUtils.isEmpty(loanFileDOS)){
                String path = getPath(orderDO.getLoanCustomerId(), INVOICE.getType());
                LoanFileDO loanFileDO = new LoanFileDO();
                loanFileDO.setCustomerId(orderDO.getLoanCustomerId());
                loanFileDO.setPath(path);
                loanFileDO.setType(S9012.getType());
                loanFileDO.setUploadType(LoanFileConst.UPLOAD_TYPE_NORMAL);
                loanFileDO.setGmtCreate(new Date());
                loanFileDOMapper.insertSelective(loanFileDO);
            }
            //保单
            // POLICY((byte) 21, "保单"), S9016((byte) 80, "机动车辆保险单"),
            List<LoanFileDO> policyDOS = loanFileDOMapper.listByCustomerIdAndType(orderDO.getLoanCustomerId(), S9016.getType(), LoanFileConst.UPLOAD_TYPE_NORMAL);
            if(CollectionUtils.isEmpty(policyDOS)){
                String path = getPath(orderDO.getLoanCustomerId(), POLICY.getType());
                LoanFileDO loanFileDO = new LoanFileDO();
                loanFileDO.setCustomerId(orderDO.getLoanCustomerId());
                loanFileDO.setPath(path);
                loanFileDO.setType(S9016.getType());
                loanFileDO.setUploadType(LoanFileConst.UPLOAD_TYPE_NORMAL);
                loanFileDO.setGmtCreate(new Date());
                loanFileDOMapper.insertSelective(loanFileDO);
            }

            //申请分期登记证书
            // CERTIFICATE((byte) 20, "合格证/登记证书"), S9020((byte) 84, "机动车登记证书（权证）复印件"),
            List<LoanFileDO> certificateLists = loanFileDOMapper.listByCustomerIdAndType(orderDO.getLoanCustomerId(), S9020.getType(), LoanFileConst.UPLOAD_TYPE_NORMAL);
            if(CollectionUtils.isEmpty(certificateLists)){
                String path = getPath(orderDO.getLoanCustomerId(), CERTIFICATE.getType());
                LoanFileDO loanFileDO = new LoanFileDO();
                loanFileDO.setCustomerId(orderDO.getLoanCustomerId());
                loanFileDO.setPath(path);
                loanFileDO.setType(S9020.getType());
                loanFileDO.setUploadType(LoanFileConst.UPLOAD_TYPE_NORMAL);
                loanFileDO.setGmtCreate(new Date());
                loanFileDOMapper.insertSelective(loanFileDO);
            }
        }

//        set.add(S9016.getType());
//        set.add(CAR_INVOICE.getType());
//        List list = loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, set);

        Set<Byte> types = new HashSet<>();
        for (TermFileEnum e : TermFileEnum.values()) {
            types.add(e.getKey());
        }


        RecombinationVO<ApplyDiviGeneralInfoVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(loanQueryDOMapper.selectApplyDiviGeneralInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, types));
        recombinationVO.setVideoFace(loanQueryService.selectVideoFaceLog(orderId));

        //

        return recombinationVO;
    }

    @Override
    public void update(InstalmentUpdateParam param) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));

        if (orderDO == null) {
            throw new BizException("此订单不存在");
        }
        //更新基准评估价
        Long financialPlanId = orderDO.getLoanFinancialPlanId();
        if (financialPlanId == null) {
            throw new BizException("金融方案信息不存在");
        } else {
            LoanFinancialPlanDO financialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(financialPlanId);
            if (param.getAppraisal() != null) {
                financialPlanDO.setAppraisal(param.getAppraisal());
                int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(financialPlanDO);
                Preconditions.checkArgument(count > 0, "更新产品基准评估价失败");
            }
        }

        Long customerId = orderDO.getLoanCustomerId();
        if (customerId == null) {
            throw new BizException("客户不存在");
        }

        if (customerId != null) {
            if (param.getFiles() != null) {
                if (!param.getFiles().isEmpty()) {
                    for (UniversalFileParam universalFileParam : param.getFiles()) {
                        List<LoanFileDO> uploadList = loanFileDOMapper.listByCustomerIdAndType(customerId, new Byte(universalFileParam.getType()), null);
                        for (LoanFileDO loanFileDO : uploadList) {
                            loanFileDOMapper.deleteByPrimaryKey(loanFileDO.getId());
                        }
                        LoanFileDO loanFileDO = new LoanFileDO();
                        loanFileDO.setCustomerId(customerId);
                        loanFileDO.setPath(JSON.toJSONString(universalFileParam.getUrls()));
                        loanFileDO.setType(new Byte(universalFileParam.getType()));
                        loanFileDO.setUploadType(new Byte("1"));
                        loanFileDO.setGmtCreate(new Date());
                        loanFileDO.setGmtModify(new Date());
                        loanFileDO.setStatus(new Byte("0"));
                        loanFileDOMapper.insertSelective(loanFileDO);
                    }
                }
            }
        }
        VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
        vehicleInformationDO.setId(orderDO.getVehicleInformationId());
        vehicleInformationDO.setAssess_use_year(param.getVehicle_assess_use_year());
        vehicleInformationDOMapper.updateByPrimaryKeySelective(vehicleInformationDO);
    }


    private String getPath(Long customerId,Byte type){
        List<LoanFileDO> old1 = loanFileDOMapper.listByCustomerIdAndType(customerId, type, LoanFileConst.UPLOAD_TYPE_NORMAL);
        List<LoanFileDO> old2 = loanFileDOMapper.listByCustomerIdAndType(customerId, type, LoanFileConst.UPLOAD_TYPE_SUPPLEMENT);
        ArrayList<String> paths = Lists.newArrayList();
        List<LoanFileDO> newList = Lists.newArrayList();
        newList.addAll(old1);
        newList.addAll(old2);
        newList.stream().forEach(e->{
            List<String> urls = JSON.parseArray(e.getPath(), String.class);
            paths.addAll(urls);
        });
        String s = JSON.toJSONString(paths);
        return s;
    }
}
