package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.FeeRegisterParam;
import com.yunche.loan.domain.param.FileInfoParam;
import com.yunche.loan.domain.param.ForceParam;
import com.yunche.loan.domain.param.LitigationParam;
import com.yunche.loan.domain.query.LawWorkQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LawWorksService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

//缺逾期信息，车辆金融调原来的
@Service
@Transactional
public class LawWorksServiceImpl implements LawWorksService {
    @Resource
    private LitigationDOMapper litigationDOMapper;

    @Resource
    private ForceDOMapper forceDOMapper;

    @Resource
    private FeeRegisterDOMapper feeRegisterDOMapper;

    @Resource
    private FileInfoDOMapper fileInfoDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanQueryService loanQueryService;

    @Resource
    private LitigationStateDOMapper litigationStateDOMapper;

    @Resource
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Override
    public LawWorksVO detail(Long orderid,Long bankRepayImpRecordId) {
        List<LoanApplyCompensationDO> list1 = loanApplyCompensationDOMapper.selectByOrderId(orderid);
        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderid);

        LawWorksVO lawWorksVO = new LawWorksVO();
        lawWorksVO.setResult(lawWorkQuery);
      //  LitigationStateDO litigationStateDO = litigationStateDOMapper.selectByIdAndType(orderid,"0",bankRepayImpRecordId);
        LitigationStateDO litigationStateDO = null;
        lawWorksVO.setLitigationStateDO(litigationStateDO == null?new LitigationStateDO():litigationStateDO);
        List<LitigationDO> list = litigationDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        if(list == null){
            list = new ArrayList<LitigationDO>();
        }
        ForceDO forceDO = forceDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        FeeRegisterDO feeRegisterDO = feeRegisterDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        List<FileInfoDO> fileInfoDO = fileInfoDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        lawWorksVO.setFeeRegisterDO(feeRegisterDO == null ? new FeeRegisterDO():feeRegisterDO);
        lawWorksVO.setFileInfoDO(fileInfoDO == null ? new ArrayList<FileInfoDO>():fileInfoDO);
        lawWorksVO.setForceDO(forceDO == null ? new ForceDO():forceDO);
        lawWorksVO.setList(list);
        lawWorksVO.setLoanApplyCompensation(list1);

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderid);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        lawWorksVO.setCustomers(customers);


        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderid);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderid);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
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

        universalCarInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);

        lawWorksVO.setCar(universalCarInfoVO);
        lawWorksVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderid));

        return lawWorksVO;
    }

    @Override
    public void litigationInstall(LitigationParam param) {
        Long id = param.getId();
        LitigationDO litigationDO = new LitigationDO();
        BeanUtils.copyProperties(param, litigationDO);
        if(id == null){
            litigationDOMapper.insertSelective(litigationDO);
        }else{
            litigationDOMapper.updateByPrimaryKeySelective(litigationDO);
        }
    }

    @Override
    public void forceInstall(ForceParam param) {
        Long id = param.getId();
        ForceDO forceDO = new ForceDO();
        BeanUtils.copyProperties(param, forceDO);
        if(id == null){
            forceDOMapper.insertSelective(forceDO);
        }else{
            forceDOMapper.updateByPrimaryKeySelective(forceDO);
        }
    }

    @Override
    public void feeInstall(FeeRegisterParam param) {
        Long id = param.getId();
        FeeRegisterDO feeRegisterDO = new FeeRegisterDO();
        BeanUtils.copyProperties(param, feeRegisterDO);
        if(id == null){
            feeRegisterDOMapper.insertSelective(feeRegisterDO);
        }else{
            feeRegisterDOMapper.updateByPrimaryKeySelective(feeRegisterDO);
        }
    }

    @Override
    @Transactional
    public void fileInfoInstall(FileInfoParam param) {
        fileInfoDOMapper.deleteByOrderIdAndRecordId(param.getOrderId(),param.getBankRepayImpRecordId());
        FileInfoDO fileInfoDO = new FileInfoDO();
        BeanUtils.copyProperties(param, fileInfoDO);
        for(FileVO fileVO:param.getFiles()){
            fileInfoDO.setOrderId(param.getOrderId());
            fileInfoDO.setBankRepayImpRecordId(param.getBankRepayImpRecordId());
            fileInfoDO.setRemark(param.getRemark());
            fileInfoDO.setType(fileVO.getType()+"");
            fileInfoDO.setPath(JSON.toJSONString(fileVO.getUrls()));
            fileInfoDOMapper.insertSelective(fileInfoDO);
        }
    }

    @Override
    public void litigationRevoke(LitigationStateDO litigationStateDO) {
        litigationStateDO.setCollectionType("0");
       // litigationStateDOMapper.insertSelective(litigationStateDO);
    }
}
