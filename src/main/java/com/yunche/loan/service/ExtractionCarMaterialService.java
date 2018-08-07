package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.ExtractionCarMaterialCardQuery;
import com.yunche.loan.domain.vo.ExtractionCarMaterialCardVO;

import java.util.List;

public interface ExtractionCarMaterialService
{

     ResultBean<List<ExtractionCarMaterialCardVO>> selectMaterialCard(ExtractionCarMaterialCardQuery extractionCarMaterialQuery);

}
