package com.yunche.loan.config.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * @author liuzhe
 * @date 2018/5/28
 */
public class ThreadPool {

    /**
     * 线程池
     */
    public static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("biz-pool-%d").build();

    public static final ExecutorService executorService = new ThreadPoolExecutor(
            0,
            50,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy()
    );

}
