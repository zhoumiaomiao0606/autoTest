package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/4/12
 */
@Data
public class LoanTelephoneVerifyParam extends LoanTelephoneVerifyDO {

    //签单类型
    private Byte signatureType;

    //保存电审风险分担加成图片
    private List<FileVO> files = Collections.EMPTY_LIST;
}
