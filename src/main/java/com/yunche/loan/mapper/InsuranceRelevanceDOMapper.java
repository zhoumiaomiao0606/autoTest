package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.vo.InsuranceInfoToTallyOrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InsuranceRelevanceDOMapper {
    int deleteByPrimaryKey(Long id);

    int deleteByInsuranceInfoId(Long insuranceInfoId);

    int insertSelective(InsuranceRelevanceDO record);

    InsuranceRelevanceDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(InsuranceRelevanceDO record);

    /**
     * 获取关联保险列表
     *
     * @param insuranceInfoId
     * @return
     */
    List<InsuranceRelevanceDO> listByInsuranceInfoId(Long insuranceInfoId);

    int deleteByInsuranceInfoIdAndType(@Param("insuranceInfoId") Long insuranceInfoId, @Param("insuranceType") Byte insuranceType);

    List<InsuranceRelevanceDO> selectCompanyByNum(String insurance_number);


    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  根据订单id查询保单相关信息
    */
    List<InsuranceRelevanceDO> selectInsuranceInfoByOrderId(Long orderId);
}