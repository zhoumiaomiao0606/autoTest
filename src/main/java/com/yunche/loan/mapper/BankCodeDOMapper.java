package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BankCodeDO;
import com.yunche.loan.domain.param.BankCodeParam;
import com.yunche.loan.domain.vo.BankCodeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BankCodeDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BankCodeDO record);

    int insertSelective(BankCodeDO record);

    BankCodeDO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BankCodeDO record);

    int updateByPrimaryKey(BankCodeDO record);

    List<BankCodeDO> selectByBankId(@Param("bankId") Integer bankId,@Param("level") Byte level);

    List<BankCodeDO> selectBankNameByParentId(Integer bankId);

    BankCodeDO selectByBankNameIsExist(String name);

    List<BankCodeVO> bankNameList(BankCodeParam param);

    int deleteBankByParentId(Integer parentId);

    List<BankCodeDO> selectBankListByParentName(String bankName);
}