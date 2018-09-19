package com.yunche.loan.service;

import com.google.zxing.WriterException;
import com.yunche.loan.config.result.ResultBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LoadQrService {
    void downloadQr(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws WriterException, IOException;


    ResultBean apkDownload();
}
