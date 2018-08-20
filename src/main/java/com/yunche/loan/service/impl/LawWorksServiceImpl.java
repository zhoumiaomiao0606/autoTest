package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.FeeRegisterParam;
import com.yunche.loan.domain.param.FileInfoParam;
import com.yunche.loan.domain.param.ForceParam;
import com.yunche.loan.domain.param.LitigationParam;
import com.yunche.loan.domain.query.LawWorkQuery;
import com.yunche.loan.domain.vo.LawWorksVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LawWorksService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public LawWorksVO detail(Long orderid,Long bankRepayImpRecordId) {
        List<LoanApplyCompensationDO> list1 = loanApplyCompensationDOMapper.selectByOrderId(orderid);
        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderid);

        LawWorksVO lawWorksVO = new LawWorksVO();
        lawWorksVO.setResult(lawWorkQuery);
        lawWorksVO.setLitigationStateDO(litigationStateDOMapper.selectByIdAndType(orderid,"0",bankRepayImpRecordId));
        List<LitigationDO> list = litigationDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        if(list == null){
            list = new ArrayList<LitigationDO>();
        }
        ForceDO forceDO = forceDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        FeeRegisterDO feeRegisterDO = feeRegisterDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        FileInfoDO fileInfoDO = fileInfoDOMapper.selectByOrderId(orderid,bankRepayImpRecordId);
        lawWorksVO.setFeeRegisterDO(feeRegisterDO == null ? new FeeRegisterDO():feeRegisterDO);
        lawWorksVO.setFileInfoDO(fileInfoDO == null ? new FileInfoDO():fileInfoDO);
        lawWorksVO.setForceDO(forceDO == null ? new ForceDO():forceDO);
        lawWorksVO.setList(list);
        lawWorksVO.setLoanApplyCompensation(list1);

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderid);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        lawWorksVO.setCustomers(customers);

        lawWorksVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderid));
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
    public void fileInfoInstall(FileInfoParam param) {
        Long id = param.getId();
        FileInfoDO fileInfoDO = new FileInfoDO();
        BeanUtils.copyProperties(param, fileInfoDO);
        if(id == null){
            fileInfoDOMapper.insertSelective(fileInfoDO);
        }else{
            fileInfoDOMapper.updateByPrimaryKeySelective(fileInfoDO);
        }
    }

    @Override
    public void litigationRevoke(LitigationStateDO litigationStateDO) {
        litigationStateDO.setCollectionType("0");
        litigationStateDOMapper.insertSelective(litigationStateDO);
    }
}
