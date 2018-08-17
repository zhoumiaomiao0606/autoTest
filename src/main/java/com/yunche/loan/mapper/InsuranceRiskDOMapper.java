package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceRiskDO;
import com.yunche.loan.domain.query.RiskQuery;
import com.yunche.loan.domain.vo.RiskQueryVO;
import com.yunche.loan.domain.vo.newInsuranceVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InsuranceRiskDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(InsuranceRiskDO record);

    int insertSelective(InsuranceRiskDO record);

    InsuranceRiskDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsuranceRiskDO record);

    int updateByPrimaryKey(InsuranceRiskDO record);

    List<InsuranceRiskDO> riskInfoByOrderId(@Param("orderId") Long orderId,@Param("insuranceYear") Byte insuranceYear);

    List<RiskQueryVO> insuranceRiskList(RiskQuery query);

    int insuranceRiskCount(RiskQuery query);

    List<newInsuranceVO> newInsuranceByOrderId(@Param("orderId") Long orderId);

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  根据订单id查询所有的出险信息
    */
    List<InsuranceRiskDO> allRiskInfoByOrderId(@Param("orderId") Long orderId);
}