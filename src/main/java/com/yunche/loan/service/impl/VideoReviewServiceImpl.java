package com.yunche.loan.service.impl;

import com.google.common.collect.Sets;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VideoReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.yunche.loan.config.constant.LoanFileEnum.*;

/**
 * @author liuzhe
 * @date 2018/9/12
 */
@Service
public class VideoReviewServiceImpl implements VideoReviewService {


    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;


    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();

        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalBaseInfo(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));

        // 视频面签视频
        recombinationVO.setPath(loanQueryService.selectVideoFacePath(orderId));

        // 55-签字视频; 56-问话视频;
        Set<Byte> fileTypes = Sets.newHashSet(SIGNATURE_VIDEO.getType(), INTERROGATION_VIDEO.getType());
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, fileTypes));

        return ResultBean.ofSuccess(recombinationVO);
    }
}
