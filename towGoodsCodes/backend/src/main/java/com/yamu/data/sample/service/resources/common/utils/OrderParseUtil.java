package com.yamu.data.sample.service.resources.common.utils;

import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 根据和前端约定解析order by
 *
 * @author xh.wu
 * @date 2021/11/3
 */
public class OrderParseUtil {

    private static final String defaultOrderByEvery = "parse_time desc,parse_total_cnt desc";
    private static final String defaultOrderByAll = "parse_total_cnt desc";
    private static final List<String> columnCodes = new ArrayList<>();
    public static final List<String> orderCodes = new ArrayList<>();

    static {
        Collections.addAll(columnCodes, "1", "2", "3");
        Collections.addAll(orderCodes, "up", "down");
    }

    public static String parse(String sortWay, String statisticsWay) {
        if (!checkSortWay(sortWay)) {
            if (StatisticsWayEnum.EVERY.getType().equals(statisticsWay)) {
                return defaultOrderByEvery;
            } else {
                return defaultOrderByAll;
            }
        }
        String columnCode = sortWay.split("_")[0], orderCode = sortWay.split("_")[1];
        String[] columns;
        String order = "";
        switch (columnCode) {
            case "1": {
                columns = new String[]{"(net_in_parse_total_cnt/a_record_parse_total_cnt)"};
                break;
            }
            case "2": {
                columns = new String[]{"(net_out_parse_total_cnt/a_record_parse_total_cnt)"};
                break;
            }
            case "3": {
                columns = new String[]{"(within_parse_total_cnt/parse_total_cnt)"};
                break;
            }
            default:
                columns = new String[]{};
                break;
        }
        switch (orderCode) {
            case "up": {
                order = "asc";
                break;
            }
            case "down": {
                order = "desc";
                break;
            }
            default:
                break;
        }
        StringBuilder orderBy = new StringBuilder();
        if (StatisticsWayEnum.EVERY.getType().equals(statisticsWay)) {
            orderBy.append("parse_time desc,");
        }
        for (String column : columns) {
            orderBy.append(column).append(" ").append(order).append(",");
        }
        orderBy.append("parse_total_cnt desc");
        return orderBy.toString();
    }

    private static boolean checkSortWay(String sortWay) {
        if (StringUtils.isEmpty(sortWay)) {
            return false;
        }
        String[] parts = sortWay.split("_");
        if (parts.length != 2) {
            return false;
        }
        return columnCodes.contains(parts[0]) && orderCodes.contains(parts[1]);
    }
}
