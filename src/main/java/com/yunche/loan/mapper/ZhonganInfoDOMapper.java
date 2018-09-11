package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.ZhonganInfoDO;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.query.ContractSetQuery;
import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
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
}