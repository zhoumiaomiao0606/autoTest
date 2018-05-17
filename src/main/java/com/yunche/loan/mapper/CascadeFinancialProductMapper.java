package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.FinancialProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CascadeFinancialProductMapper {


    @Select("select bank_name from financial_product where prod_id in (select prod_id from biz_model_rela_financial_prod a where a.biz_id in(select biz_id from biz_model_rela_area_partners  where group_id=#{partnerId})) GROUP BY bank_name")
    List <String>findBankListByPartnerId(@Param("partnerId") Long partnerId);




    @Select("select bank_name AS bankName,prod_id AS prodId,prod_name AS prodName ,category_superior AS categorySuperior " +
            "from financial_product where bank_name = #{bankName} and status='0' and   prod_id in (select prod_id from biz_model_rela_financial_prod a where a.biz_id in (select biz_id from biz_model_rela_area_partners  where group_id = #{partnerId}))")
    List<FinancialProductDO> findParamByBank(@Param("partnerId") Long partnerId, @Param("bankName") String  bankName);

}
