package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.ZhonganInfoDO;
import com.yunche.loan.domain.query.BankCreditPrincipalQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ZhonganInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ZhonganInfoDO record);

    int insertSelective(ZhonganInfoDO record);

    ZhonganInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ZhonganInfoDO record);

    int updateByPrimaryKey(ZhonganInfoDO record);

    List<ZhonganInfoDO> selectByOrderId(@Param("idcard") String idcard, @Param("customername") String customername);
    //征信申请查询
    List<ZhonganInfoDO> selectByCreaditOrderId(@Param("orderId")Long orderId);
    //查询订单人员是否全部查过征信
    List<LoanCustomerDO> selectCusByOrderId(@Param("orderId")Long orderId);

    ZhonganInfoDO selectZNByOrderIdAndIdcard(@Param("orderId")Long orderId,@Param("idCard")String idCard);

    ZhonganNameVO selectZhonganName(@Param("orderId")Long orderId);

    List<BusinessApprovalReportVO> businessApproval(BaseQuery query);

    BusinessApprovalReportTotalVO businessApprovalTotal(BaseQuery query);

    List<BusinessApprovalReportVO> businessApprovalExport(BaseQuery query);


    List<ContractSetReportVO> contractSet(ContractSetQuery query);

    ContractSetReportTotalVO contractSetTotal(ContractSetQuery query);

    List<ContractSetReportVO> contractSetExport(ContractSetQuery query);


    List<BankCreditPrincipalVO> bankCreditPrincipal(BankCreditPrincipalQuery query);

    ContractSetReportTotalVO bankCreditPrincipalTotal(BankCreditPrincipalQuery query);

    List<BankCreditPrincipalVO> bankCreditPrincipalExport(BankCreditPrincipalQuery query);



    List<BankCreditPrincipalVO> bankCreditAll(BankCreditPrincipalQuery query);

    ContractSetReportTotalVO bankCreditAllTotal(BankCreditPrincipalQuery query);

    List<BankCreditPrincipalVO> bankCreditAllExport(BankCreditPrincipalQuery query);

    List<TelBankCountVO> telBankCount(BankCreditPrincipalQuery query);

    List<TelUserCountVO> telUserCount(BankCreditPrincipalQuery query);

    List<TelPartnerCountVO> telPartnerCount(BankCreditPrincipalQuery query);

    List<ActionParMoneyVO> selectActionParMoney(@Param("actionIds")Set<String> actionIds,
                                                @Param("gmtCreateStart1")String gmtCreateStart1,
                                                @Param("gmtCreateEnd1")String gmtCreateEnd1,
                                                @Param("bizAreaId")String bizAreaId,
                                                @Param("partnerId")String partnerId,
                                                @Param("juniorIds")Set<String> juniorIds,
                                                @Param("maxGroupLevel")Long maxGroupLevel);
    List<String> selectBankByUserId(@Param("UserId")Long userId);
}