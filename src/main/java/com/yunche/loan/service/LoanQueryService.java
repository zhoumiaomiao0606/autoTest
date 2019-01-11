package com.yunche.loan.service;

import com.yunche.loan.domain.vo.*;

import java.util.List;

public interface LoanQueryService {

    boolean selectCheckOrderInBankInterfaceSerial(Long orderId, String transCode);

    UniversalCustomerDetailVO universalCustomerDetail(Long customerId);

    String selectTelephoneVerifyLevel();

    Integer selectBankInterFaceSerialOrderStatusByOrderId(Long orderId, String transCode);

    Integer selectBankOpenCardStatusByOrderId(Long orderId);

    void checkBankInterFaceSerialStatus(Long customerId, String transCode);

    String selectLastBankInterfaceSerialNoteByTransCode(Long customerId, String transCode);

    BankInterfaceSerialReturnVO selectLastBankInterfaceSerialByTransCode(Long customerId, String transCode);

    /**
     * 单个资料增补单 -detail
     *
     * @param infoSupplementId
     * @return
     */
    UniversalInfoSupplementVO selectUniversalInfoSupplementDetail(Long infoSupplementId);

    /**
     * 单个订单的-所有增补历史    -已提交的
     *
     * @param orderId
     * @return
     */
    List<UniversalInfoSupplementVO> selectUniversalInfoSupplementHistory(Long orderId);

    /**
     * 当前客户的 文件列表  （包含：正常上传 & 增补上传，且已根据upload_type作了聚合）
     *
     * @param customerId
     * @return
     */
    List<UniversalCustomerFileVO> selectUniversalCustomerFile(Long customerId);

    /**
     * 视频面签path
     *
     * @param orderId
     * @return
     */
    String selectVideoFacePath(Long orderId);

    /**
     * 视频面签记录
     *
     * @param orderId
     * @return
     */
    UniversalVideoFaceLogVO selectVideoFaceLog(Long orderId);
}
