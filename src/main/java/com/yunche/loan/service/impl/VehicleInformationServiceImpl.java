package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.VehicleInformationDO;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.param.VehicleInformationUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.domain.vo.VehicleInformationVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanQueryService;
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
public class VehicleInformationServiceImpl implements VehicleInformationService {

    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;


    @Resource
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;


    @Override
    public RecombinationVO detail(Long orderId) {

        VehicleInformationVO vehicleInformationVO = loanQueryDOMapper.selectVehicleInformation(orderId);
        Long informationIdById = loanOrderDOMapper.getVehicleInformationIdById(orderId);
        VehicleInformationDO informationDO = vehicleInformationDOMapper.selectByPrimaryKey(informationIdById);
        Integer month = DateUtil.getdiffMonth1(informationDO.getTransfer_ownership_date(), informationDO.getRegister_date());
        if (null != month) {
            month = month>60?60:month;
            vehicleInformationVO.setAssess_use_year(String.valueOf(month));
        }

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        if (vehicleInformationVO != null) {

            if (vehicleInformationVO.getApply_license_plate_area() != null) {
                BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(vehicleInformationVO.getApply_license_plate_area()), VALID_STATUS);
                vehicleInformationVO.setHasApplyLicensePlateArea(baseAreaDO);
                String tmpApplyLicensePlateArea = null;
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
                vehicleInformationVO.setApply_license_plate_area(tmpApplyLicensePlateArea);
            }
        }
        RecombinationVO<VehicleInformationVO> recombinationVO = new RecombinationVO<VehicleInformationVO>();
        recombinationVO.setInfo(vehicleInformationVO);
        recombinationVO.setCustomers(customers);
        Set<Byte> types = new HashSet<Byte>();
        types.add(new Byte("19"));
        types.add(new Byte("20"));
        types.add(new Byte("21"));
        types.add(new Byte("21"));
        types.add(new Byte("22"));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, types));


        return recombinationVO;
    }

    @Override
    public void update(VehicleInformationUpdateParam param) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        Long foundationId = loanOrderDO.getVehicleInformationId();//关联ID
        if (foundationId == null) {
            //新增提交
            VehicleInformationDO V = BeanPlasticityUtills.copy(VehicleInformationDO.class, param);
            vehicleInformationDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setVehicleInformationId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {
            if (vehicleInformationDOMapper.selectByPrimaryKey(foundationId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                VehicleInformationDO V = BeanPlasticityUtills.copy(VehicleInformationDO.class, param);
                V.setId(foundationId);
                vehicleInformationDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                VehicleInformationDO V = BeanPlasticityUtills.copy(VehicleInformationDO.class, param);
                V.setId(foundationId);
                vehicleInformationDOMapper.updateByPrimaryKeySelective(V);
            }
        }


        Long customerId = loanOrderDO.getLoanCustomerId();

        if (customerId != null && param.getFiles() != null && !param.getFiles().isEmpty()) {
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
