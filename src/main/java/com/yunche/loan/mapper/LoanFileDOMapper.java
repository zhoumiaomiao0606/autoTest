package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.vo.BankAndSocietyPicVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LoanFileDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(LoanFileDO record);

    int insertSelective(LoanFileDO record);

    LoanFileDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFileDO record);

    int updateByPrimaryKey(LoanFileDO record);

    /**
     * 根据客户ID 和 上传类型 查询列表
     *
     * @param customerId
     * @param type       文件类型：1-身份证;2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;
     *                   6-驾驶证;7- 户口本;8- 银行流水;9-结婚证;10-房产证;
     *                   11-定位照;12-合影;13-房子照片;14-家访视频
     * @param uploadType 1-正常上传;  2-资料增补上传;
     * @return
     */
    List<LoanFileDO> listByCustomerIdAndType(@Param("customerId") Long customerId,
                                             @Param("type") Byte type,
                                             @Param("uploadType") Byte uploadType);

    /**
     * 批量插入
     *
     * @param loanFileDOS
     * @return
     */
    int batchInsert(List<LoanFileDO> loanFileDOS);

    /**
     * 根据增补单ID 删除
     *
     * @param infoSupplementId
     * @return
     */
    int deleteByInfoSupplementId(Long infoSupplementId);

    List<BankAndSocietyPicVO> selectFileInfoByCusId(@Param("cusIds") List<Long> cusIds);

    List<BankAndSocietyPicVO> selectSocFileInfoByCusId(@Param("cusIds") List<Long> cusIds);
}