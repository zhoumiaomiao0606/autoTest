package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.PartnerBankAccountDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PartnerBankAccountDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PartnerBankAccountDO record);

    int insertSelective(PartnerBankAccountDO record);

    PartnerBankAccountDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PartnerBankAccountDO record);

    int updateByPrimaryKey(PartnerBankAccountDO record);

    /**
     * 批量插入
     *
     * @param partnerBankAccountDOS
     * @return
     */
    int batchInsert(List<PartnerBankAccountDO> partnerBankAccountDOS);

    /**
     * 获取合伙人账户列表
     *
     * @param partnerId
     * @return
     */
    List<PartnerBankAccountDO> listByPartnerId(Long partnerId);

    /**
     * @param partnerId
     * @return
     */
    int deleteByPartnerId(Long partnerId);
}