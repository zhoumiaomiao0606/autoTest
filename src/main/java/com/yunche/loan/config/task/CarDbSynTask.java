package com.yunche.loan.config.task;

import com.yunche.loan.config.anno.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CarDbSynTask
{
    private static final Logger LOG = LoggerFactory.getLogger(CarDbSynTask.class);

    @Scheduled(cron = "0 0 2 ? * SAT")
    @DistributedLock(200)
    public void synCarDb() {

    }
}
