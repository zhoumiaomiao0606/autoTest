package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCustomerDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanCustomerDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanCustomerDO record);

    int insertSelective(LoanCustomerDO record);

    LoanCustomerDO selectByPrimaryKey(@Param("id") Long id, @Param("status") Byte status);

    int updateByPrimaryKeySelective(LoanCustomerDO record);

    int updateByPrimaryKeyWithBLOBs(LoanCustomerDO record);

    int updateByPrimaryKey(LoanCustomerDO record);

    /**
     * 根据主贷人ID和客户类别 获取客户列表(共贷人/担保人/紧急联系人)
     *
     * @param principalLenderId
     * @param custType
     * @return
     */
    List<LoanCustomerDO> listByPrincipalCustIdAndType(@Param("principalCustId") Long principalLenderId, @Param("custType") Byte custType);

    /**
     * 编辑贷款人
     *
     * @param oldPrincipalLenderId 旧的主贷人ID
     * @param newPrincipalLenderId 新的主贷人ID
     */
    int updatePrincipalCustId(@Param("oldPrincipalLenderId") Long oldPrincipalLenderId, @Param("newPrincipalLenderId") Long newPrincipalLenderId);

    /**
     * 获取文件信息
     *
     * @param id
     * @return
     */
    String getFilesById(Long id);
}