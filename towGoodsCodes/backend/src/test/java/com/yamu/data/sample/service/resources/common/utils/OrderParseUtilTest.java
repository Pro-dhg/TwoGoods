package com.yamu.data.sample.service.resources.common.utils;

import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import org.junit.jupiter.api.Test;

class OrderParseUtilTest {

    @Test
    void parse() {

        String[] sortWays = {"", null, "123", "up_down", "1_up", "1_down", "2_up","2_down","3_up","3_down"};

        for (String sortWay : sortWays) {
            String result = OrderParseUtil.parse(sortWay, StatisticsWayEnum.ALL.getType());
            System.out.println(result);
        }
        for (String sortWay : sortWays) {
            String result = OrderParseUtil.parse(sortWay, StatisticsWayEnum.EVERY.getType());
            System.out.println(result);
        }
    }
}