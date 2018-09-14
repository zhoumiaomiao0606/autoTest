package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.domain.entity.*;
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

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BankDOMapper bankDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {

        VehicleInformationVO vehicleInformationVO = loanQueryDOMapper.selectVehicleInformation(orderId);

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        if (vehicleInformationVO != null) {

            if (vehicleInformationVO.getApply_license_plate_area() != null) {
                BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(vehicleInformationVO.getApply_license_plate_area()), VALID_STATUS);
                String tmpApplyLicensePlateArea = null;
                if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                    Long parentAreaId = baseAreaDO.getParentAreaId();
                    BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                    baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                    baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
                }
                vehicleInformationVO.setHasApplyLicensePlateArea(baseAreaDO);
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
        types.add(new Byte("11"));
        types.add(new Byte("19"));
        types.add(new Byte("20"));
        types.add(new Byte("21"));
        types.add(new Byte("22"));
        types.add(new Byte("23"));
        types.add(new Byte("55"));
        types.add(new Byte("56"));
        types.add(new Byte("58"));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, types));


        return recombinationVO;
    }

    @Override
    public void update(VehicleInformationUpdateParam param) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));

        //使用年限自动计算
        Integer month =null;
        if(param.getTransfer_ownership_date()!=null && param.getRegister_date()!=null){
            month =  assessUseYear(Long.valueOf(param.getOrder_id()),DateUtil.getDate10(param.getTransfer_ownership_date()),DateUtil.getDate10(param.getRegister_date()));
            if(month !=null){
                param.setAssess_use_year(String.valueOf(month));
            }
        }

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

        String s = param.getApply_license_plate_area();
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        loanBaseInfoDO.setAreaId(Long.valueOf(s));
        LoanBaseInfoDO loanBaseInfoDO1 = loanBaseInfoDOMapper.getTotalInfoByOrderId(Long.valueOf(param.getOrder_id()));
        loanBaseInfoDO.setId(loanBaseInfoDO1.getId());
        loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);


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

    /**
     * 台州要求使用前先最多为60个月，超过60月记为60
     * 其他银行按真实月数
     * 使用年限
     * @param orderId
     * @return
     */
    private  Integer assessUseYear(Long orderId,Date transferOwnershipDate,Date registerDate){
        Integer month =null;
        if(transferOwnershipDate==null || registerDate==null){
            return null;
        }

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        Long bankId = bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        Preconditions.checkNotNull(bankId, "贷款银行不存在");

        if(IDict.K_BANK.ICBC_TZLQ.equals(String.valueOf(bankId))){
            month = DateUtil.getdiffMonth_TAIZHOU(transferOwnershipDate,registerDate);
            month = month>60?60:month;
        }else if(IDict.K_BANK.ICBC_HZCZ.equals(String.valueOf(bankId))){
            month = DateUtil.getdiffMonth_CHENGZHAN(transferOwnershipDate,registerDate);
        }else{
            //其他银行
            month = DateUtil.getdiffMonth_CHENGZHAN(transferOwnershipDate,registerDate);
        }
        return month;
    }
}
