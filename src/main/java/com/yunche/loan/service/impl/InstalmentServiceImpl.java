package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.TermFileEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InstalmentUpdateParam;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.mapper.LoanFileDOMapper;
import com.yunche.loan.mapper.LoanFinancialPlanDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.InstalmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;
    @Override
    public RecombinationVO detail(Long orderId) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

        if(orderDO == null){
            throw new BizException("此订单不存在");
        }
        Set<Byte> types = new HashSet<Byte>();
        for (TermFileEnum e : TermFileEnum.values()) {
            types.add(e.getKey());
        }

        String path = loanQueryDOMapper.selectVideoFacePath(orderId);
        if(StringUtils.isNotBlank(path)){
            path =  path.replace("https://yunche-videosign.oss-cn-hangzhou.aliyuncs.com","");
            path.trim();
        }


        RecombinationVO<ApplyDiviGeneralInfoVO> recombinationVO = new RecombinationVO<ApplyDiviGeneralInfoVO>();
        recombinationVO.setInfo(loanQueryDOMapper.selectApplyDiviGeneralInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId,types));
        recombinationVO.setPath(path);
        return recombinationVO;
    }

    @Override
    public void update(InstalmentUpdateParam param) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));

        if(orderDO == null){
            throw new BizException("此订单不存在");
        }
        //更新基准评估价
        Long financialPlanId = orderDO.getLoanFinancialPlanId();
        if(financialPlanId ==null){
            throw new BizException("金融方案信息不存在");
        }else{
            LoanFinancialPlanDO financialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(financialPlanId);
            if(param.getAppraisal()!=null){
                financialPlanDO.setAppraisal(param.getAppraisal());
                int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(financialPlanDO);
                Preconditions.checkArgument(count>0,"更新产品基准评估价失败");
            }
        }

        Long customerId = orderDO.getLoanCustomerId();
        if(customerId == null){
            throw new BizException("客户不存在");
        }

        if (customerId != null) {
            if (param.getFiles() != null) {
                if (!param.getFiles().isEmpty()) {
                    for (UniversalFileParam universalFileParam : param.getFiles()) {
                        List<LoanFileDO> uploadList = loanFileDOMapper.listByCustomerIdAndType(customerId,new Byte(universalFileParam.getType()), null);
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
