package com.two.service.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DemoScheduler {

    @Scheduled(cron = "0 */1 * * * ?")
    public void testTasks() {
        //TODO
        //System.out.println("这是个demo");
    }

}
