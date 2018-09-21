package com.yunche.loan.config.task;

import lombok.Data;

import java.util.concurrent.CountDownLatch;

@Data
public class ThreadTask implements Runnable{

    public  Long orderId;
    CountDownLatch latch;
    @Override
    public void run() {
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+"-哈哈哈哈哈："+orderId);
        latch.countDown();
    }
}
