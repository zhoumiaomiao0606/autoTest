package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.param.VehicleInformationUpdateParam;
import com.yunche.loan.domain.vo.ApplyLicensePlateDepositInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.ApplyLicensePlateDepositInfoService;
import com.yunche.loan.service.VehicleInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

@Service
@Transactional
public class ApplyLicensePlateDepositInfoServiceImpl implements ApplyLicensePlateDepositInfoService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private ApplyLicensePlateDepositInfoDOMapper applyLicensePlateDepositInfoDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private VehicleInformationService vehicleInformationService;

    @Autowired
    private LoanQueryServiceImpl loanQueryService;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Resource
    private LoanFileDOMapper loanFileDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {
        ApplyLicensePlateDepositInfoVO applyLicensePlateDepositInfoVO = loanQueryDOMapper.selectApplyLicensePlateDepositInfo(orderId);

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);

        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        String tmpApplyLicensePlateArea = null;
        if (applyLicensePlateDepositInfoVO.getApply_license_plate_area() != null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(applyLicensePlateDepositInfoVO.getApply_license_plate_area()), VALID_STATUS);
            if ("3".equals(String.valueOf(baseAreaDO.getLevel()))) {
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            applyLicensePlateDepositInfoVO.setHasApplyLicensePlateArea(baseAreaDO);

            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
            applyLicensePlateDepositInfoVO.setApply_license_plate_area(tmpApplyLicensePlateArea);
        }
        RecombinationVO<ApplyLicensePlateDepositInfoVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(applyLicensePlateDepositInfoVO);
        recombinationVO.setCustomers(customers);
        Set<Byte> types = new HashSet<>();
        types.add(new Byte("23"));
        types.add(new Byte("20"));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, types));
        return recombinationVO;
    }

    @Override
    public void update(ApplyLicensePlateDepositInfoUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }

        Long foundationId = loanOrderDO.getApplyLicensePlateDepositInfoId();//关联ID
        if (foundationId == null) {
            //新增提交
            ApplyLicensePlateDepositInfoDO V = BeanPlasticityUtills.copy(ApplyLicensePlateDepositInfoDO.class, param);
            applyLicensePlateDepositInfoDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setApplyLicensePlateDepositInfoId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {
            if (applyLicensePlateDepositInfoDOMapper.selectByPrimaryKey(foundationId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                ApplyLicensePlateDepositInfoDO V = BeanPlasticityUtills.copy(ApplyLicensePlateDepositInfoDO.class, param);
                V.setId(foundationId);
                applyLicensePlateDepositInfoDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                ApplyLicensePlateDepositInfoDO V = BeanPlasticityUtills.copy(ApplyLicensePlateDepositInfoDO.class, param);
                V.setId(foundationId);
                applyLicensePlateDepositInfoDOMapper.updateByPrimaryKeySelective(V);

            }
        }

        vehicleInformationService.update(BeanPlasticityUtills.copy(VehicleInformationUpdateParam.class, param));


        Long customerId = loanOrderDO.getLoanCustomerId();

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
    }
}
