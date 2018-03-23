package com.yunche.loan;

import com.yunche.loan.config.util.Jpush;
import com.yunche.loan.service.JpushService;
import org.junit.Test;

import javax.annotation.Resource;

public class JpushTest extends BaseTest{

/*    public static void main(String[] args){
        Jpush.sendToRegistrationId("121c83f7602f3ac5d12","1","1");
    }*/

    @Resource
    private JpushService jpushService;

    @Test
    public void test(){
        //jpushService.list(1,10);
        //jpushService.read(new Long(1));
        //jpushService.push(null,null,null,null,null,null,null);
    }
}
