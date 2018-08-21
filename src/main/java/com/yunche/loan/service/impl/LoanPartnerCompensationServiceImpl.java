package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDOKey;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.domain.vo.FinancialSchemeVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalInfoVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.LoanApplyCompensationDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanFileService;
import com.yunche.loan.service.LoanPartnerCompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;

/**
 * 合伙人代偿
 */
@Service
public class LoanPartnerCompensationServiceImpl implements LoanPartnerCompensationService {
    
    
    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;
    /**
     * 合伙人代偿信息保存
     * @param universalCompensationParam
     * @return
     */
    @Override
    @Transactional
    public Void save(UniversalCompensationParam universalCompensationParam) {
        Preconditions.checkNotNull(universalCompensationParam,"参数有误");
        Preconditions.checkNotNull(universalCompensationParam.getOrderId(),"业务单号不能为空");
        Preconditions.checkNotNull(universalCompensationParam.getApplyCompensationDate(),"代偿申请日期不能为空");
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(universalCompensationParam.getOrderId());
        Preconditions.checkNotNull(loanOrderDO,"订单不存在：["+universalCompensationParam.getOrderId()+"]");
        int count = loanApplyCompensationDOMapper.updateByPrimaryKeySelective(universalCompensationParam);
        Preconditions.checkArgument(count>0,"合伙人代偿保存失败");

        //保存图片
        ResultBean<Void> resultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(loanOrderDO.getLoanCustomerId(), universalCompensationParam.getFiles(),UPLOAD_TYPE_NORMAL );
        Preconditions.checkArgument(resultBean.getSuccess(),"合伙人代偿打款凭证保存失败");

        return null;
    }

    /**
     * 合伙人代偿详情
     * @param query
     * @return
     */
    @Override
    public ResultBean detail(UniversalCompensationQuery query) {
        Preconditions.checkNotNull(query,"参数有误");
        Preconditions.checkNotNull(query.getOrderId(),"业务单号不能为空");
        Preconditions.checkNotNull(query.getApplyCompensationDate(),"代偿申请日期不能为空");


        //数据查询
        LoanApplyCompensationDOKey doKey = new LoanApplyCompensationDOKey();
        doKey.setOrderId(query.getOrderId());
        doKey.setApplyCompensationDate(query.getApplyCompensationDate());
        LoanApplyCompensationDO applyCompensation = loanApplyCompensationDOMapper.selectByPrimaryKey(doKey);
        if(applyCompensation.getPartnerCompensationAmount()==null){
            BigDecimal amount = applyCompensation.getCompensationAmount();//代偿金额
            BigDecimal ratio = applyCompensation.getRiskTakingRatio().divide(new BigDecimal("100"));//比例

            //合伙人代偿金额 = 代偿金额*风险承担比例
            BigDecimal partnerCompensationAmount = amount.multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            applyCompensation.setPartnerCompensationAmount(partnerCompensationAmount);
        }


        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(query.getOrderId());//客户基本信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(query.getOrderId());
        Set<Byte> types = new HashSet<Byte>();
        types.add(LoanFileEnum.COMPENSATION_PAYMENT_VOUCHER.getType());
        List<UniversalMaterialRecordVO> materialRecord = loanQueryDOMapper.selectUniversalCustomerFileByTypes(query.getOrderId(), types);

        //返回数据
        RecombinationVO<Object> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setMaterials(materialRecord);
        recombinationVO.setApplyCompensation(applyCompensation);
        return ResultBean.ofSuccess(recombinationVO);
    }
}
