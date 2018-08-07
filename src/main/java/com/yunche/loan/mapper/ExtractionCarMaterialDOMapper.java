package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.ExtractionCarMaterialCardQuery;
import com.yunche.loan.domain.vo.ExtractionCarMaterialCardVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExtractionCarMaterialDOMapper
{
    /** 
    * @Author: ZhongMingxiao 
    * @Param: 页码、页大小 
    * @return:  
    * @Date:  
    * @Description:  
    */
    List<ExtractionCarMaterialCardVO> selectMaterialCard(ExtractionCarMaterialCardQuery extractionCarMaterialQuery);
}
