package com.yunche.loan.config.util;

import com.google.common.eventbus.EventBus;
import org.springframework.stereotype.Component;

@Component
public class EventBusCenter
{
    public static final EventBus eventBus = new EventBus();
}
