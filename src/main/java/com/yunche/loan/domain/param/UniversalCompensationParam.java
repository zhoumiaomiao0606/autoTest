package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class UniversalCompensationParam extends LoanApplyCompensationDO{

    private List<FileVO> files = Collections.EMPTY_LIST;
}
