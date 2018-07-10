package com.yunche.loan.config.feign.response;

public class ApplycreditstatusResponse {

    private String retcode;
    private String retmsg;
    /**
     * 00:初始状态
     10:预处理阶段(第一次调用api接口后生成的状态，页面查询也是凭这个字段查询)
     11:调用联机开卡协议
     12:后台页面受理退回
     13：公司方受理退回成功
     14：系统预判拒绝
     20:联机开卡受理成功
     21:联机开卡受理失败
     30:主机返回开卡成功
     31:主机返回开卡失败
     44:银行内部处理中

     */
    private String status;
    private String notes;
    private String updatetime;
    private String cardno;
}
