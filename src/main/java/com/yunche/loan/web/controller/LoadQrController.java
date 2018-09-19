package com.yunche.loan.web.controller;

import com.google.zxing.WriterException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.LoadQrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loadqr")
public class LoadQrController {

    @Autowired
    LoadQrService loadQrService;

    @GetMapping(value = "/query")
    public void downloadQr(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, WriterException {
        loadQrService.downloadQr(httpServletRequest, httpServletResponse);
    }

    @GetMapping(value = "/apk")
    public ResultBean apkDownload(){
        return loadQrService.apkDownload();
    }
}