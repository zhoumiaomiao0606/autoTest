package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanCustomerDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface LoanCustomerDOMapper {

    List<LoanCustomerDO> listByPrincipalCustIdAndRelation(@Param("principalCustId") Long principalLenderId,
                                                          @Param("custRelation") Byte custRelation);

    int deleteByPrimaryKey(Long id);

    int insert(LoanCustomerDO record);

    int insertSelective(LoanCustomerDO record);

    LoanCustomerDO selectByPrimaryKey(@Param("id") Long id,
                                      @Param("status") Byte status);

    int updateByPrimaryKeySelective(LoanCustomerDO record);

    int updateByPrimaryKeyWithBLOBs(LoanCustomerDO record);

    int updateByPrimaryKey(LoanCustomerDO record);

    /**
     * 根据主贷人ID和客户类别 获取客户列表(共贷人/担保人/紧急联系人)   [含OR id = principalCustId]
     *
     * @param principalLenderId
     * @param custType
     * @param status
     * @return
     */
    List<LoanCustomerDO> listByPrincipalCustIdAndType(@Param("principalCustId") Long principalLenderId,
                                                      @Param("custType") Byte custType,
                                                      @Param("status") Byte status);


    List<Long> listIdByPrincipalCustIdAndType(@Param("principalCustId") Long principalLenderId,
                                              @Param("custType") Byte custType,
                                              @Param("status") Byte status);

    /**
     * 编辑贷款人
     *
     * @param oldPrincipalLenderId 旧的主贷人ID
     * @param newPrincipalLenderId 新的主贷人ID
     */
    int updatePrincipalCustId(@Param("oldPrincipalLenderId") Long oldPrincipalLenderId,
                              @Param("newPrincipalLenderId") Long newPrincipalLenderId);

    /**
     * 获取文件信息
     *
     * @param id
     * @return
     */
    String getFilesById(Long id);

    /**
     * 通过身份证获取主贷人ID列表
     *
     * @param idCard
     * @return
     */
    List<Long> listPrincipalCustIdByIdCard(@Param("idCard") String idCard);


    /**
     * 根据orderid获得关于这个主贷人的自己和关联的客户
     *
     * @param orderId
     * @param types
     * @return
     */
    List<LoanCustomerDO> selectSelfAndRelevanceCustomersByCustTypes(@Param("orderId") Long orderId,
                                                                    @Param("types") Set types);

    /**
     * 根据客户ID 获取 cust_relation
     *
     * @param id
     * @return
     */
    Byte getCustRelationById(Long id);

    /**
     * 查询订单人员是否全部查过征信
     *
     * @param orderId
     * @return
     */
    List<LoanCustomerDO> selectCusByOrderId(@Param("orderId") Long orderId);

    List<LoanCustomerDO> selectCusByOrderIdAll(@Param("orderId") Long orderId);


    List<LoanCustomerDO> selectByIdCard(@Param("idCard") String idCard);

    /**
     * 批量编辑enable
     *
     * @param idList
     * @param enable
     * @param enableType
     * @return
     */
    long batchUpdateEnable(@Param("idList") List<Long> idList,
                           @Param("enable") Byte enable,
                           @Param("enableType") Byte enableType);

    List<LoanCustomerDO> selectAllcustAndFiles(Long loanCustomerId);
}