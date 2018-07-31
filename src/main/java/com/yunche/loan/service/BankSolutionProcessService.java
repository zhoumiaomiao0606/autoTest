package com.yunche.loan.service;

import com.yunche.loan.domain.param.ICBCApiCallbackParam;

public interface BankSolutionProcessService {

    public String fileDownload(String fileName,String fileType);

    public void applyCreditCallback(ICBCApiCallbackParam.ApplyCreditCallback applyCreditCallback);

    public void applyDiviGeneralCallback(ICBCApiCallbackParam.ApplyDiviGeneralCallback applyDiviGeneralCallback);

    public void multimediaUploadCallback(ICBCApiCallbackParam.MultimediaUploadCallback multimediaUploadCallback);

    public void creditCardApplyCallback(ICBCApiCallbackParam.CreditCardApplyCallback creditCardApplyCallback);

    public ICBCApiCallbackParam.Ans artificialgainImage(ICBCApiCallbackParam.ArtificialGainImageCallback artificialGainImageCallback);
}
