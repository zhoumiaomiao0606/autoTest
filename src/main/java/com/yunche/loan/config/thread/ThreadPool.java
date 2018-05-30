package com.yunche.loan.config.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liuzhe
 * @date 2018/5/28
 */
public class ThreadPool {

    /**
     * 线程池
     */
    public static final ExecutorService executorService = Executors.newFixedThreadPool(30);

}
