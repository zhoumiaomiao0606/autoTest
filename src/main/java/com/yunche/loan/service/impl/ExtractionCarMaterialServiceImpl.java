/**
 * @author: ZhongMingxiao
 * @create: 2018-08-07 09:09
 * @description: 提车资料服务实现
 **/
package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.ExtractionCarMaterialCardQuery;
import com.yunche.loan.domain.vo.ExtractionCarMaterialCardVO;
import com.yunche.loan.mapper.ExtractionCarMaterialDOMapper;
import com.yunche.loan.service.ExtractionCarMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtractionCarMaterialServiceImpl implements ExtractionCarMaterialService
{

    @Autowired
    private ExtractionCarMaterialDOMapper extractionCarMaterialMapper;

    @Override
    public ResultBean<List<ExtractionCarMaterialCardVO>> selectMaterialCard(ExtractionCarMaterialCardQuery extractionCarMaterialQuery)
        {

            PageHelper.startPage(extractionCarMaterialQuery.getPageIndex(), extractionCarMaterialQuery.getPageSize(), true);
            List<ExtractionCarMaterialCardVO> list = extractionCarMaterialMapper.selectMaterialCard(extractionCarMaterialQuery);
            PageInfo<ExtractionCarMaterialCardVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
