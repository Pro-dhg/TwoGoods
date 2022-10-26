package com.yamu.data.sample.service.resources.controller.popularDomain;

import com.yamu.data.sample.service.resources.ResourcesApplication;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopNType;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ResourcesApplication.class)
@WebAppConfiguration
class PopularDomainTopNTypeControllerTest {

    @Autowired
    private PopularDomainTopNTypeController controller;

    @Test
    void findTableDetail() throws InterruptedException {
        PopularDomainTopNType topNType = new PopularDomainTopNType();
        topNType.setStartTime("2021-11-04 10:35:20");
        topNType.setEndTime("2021-11-04 10:45:20");
        topNType.setOffset(0L);
        topNType.setLimit(20L);
        topNType.setQueryType("1min");

        String[] sortWays = {"", null, "123", "up_down", "1_up", "1_down", "2_up","2_down","3_up","3_down"};
        topNType.setStatisticsWay(StatisticsWayEnum.EVERY.getType());
        for (String sortWay : sortWays) {
            topNType.setSortWay(sortWay);
            controller.findTableDetail(topNType);
            Thread.sleep(1000L);
        }
        topNType.setStatisticsWay(StatisticsWayEnum.ALL.getType());
        for (String sortWay : sortWays) {
            topNType.setSortWay(sortWay);
            controller.findTableDetail(topNType);
            Thread.sleep(1000L);
        }
    }
}