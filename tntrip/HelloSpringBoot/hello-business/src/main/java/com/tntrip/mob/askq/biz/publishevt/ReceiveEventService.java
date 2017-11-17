package com.tntrip.mob.askq.biz.publishevt;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by nuc on 2016/10/22.
 */
@Component
public class ReceiveEventService implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("Receiving event: " + event);
    }
}
