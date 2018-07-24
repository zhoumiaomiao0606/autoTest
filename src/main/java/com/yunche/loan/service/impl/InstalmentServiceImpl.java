package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InstalmentUpdateParam;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.mapper.LoanFileDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.InstalmentService;
import lombok.Setter;
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
    @Override
    public RecombinationVO detail(Long orderId) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));

        if(orderDO == null){
            throw new BizException("此订单不存在");
        }
        Set<Byte> types = new HashSet<Byte>();
        types.add(new Byte("30"));
        types.add(new Byte("31"));
        types.add(new Byte("32"));
        types.add(new Byte("33"));
        types.add(new Byte("34"));
        types.add(new Byte("35"));
        types.add(new Byte("36"));
        types.add(new Byte("37"));
        types.add(new Byte("38"));
        types.add(new Byte("39"));
        types.add(new Byte("40"));
        types.add(new Byte("41"));
        types.add(new Byte("42"));
        types.add(new Byte("43"));
        types.add(new Byte("44"));
        types.add(new Byte("45"));
        types.add(new Byte("46"));
        types.add(new Byte("47"));
        types.add(new Byte("48"));
        types.add(new Byte("49"));
        types.add(new Byte("50"));
        types.add(new Byte("51"));
        types.add(new Byte("52"));
        types.add(new Byte("53"));
        types.add(new Byte("54"));

        RecombinationVO<ApplyDiviGeneralInfoVO> recombinationVO = new RecombinationVO<ApplyDiviGeneralInfoVO>();
        recombinationVO.setInfo(loanQueryDOMapper.selectApplyDiviGeneralInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId,types));
        return recombinationVO;
    }

    @Override
    public void update(InstalmentUpdateParam param) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));

        if(orderDO == null){
            throw new BizException("此订单不存在");
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
