package com.yamu.data.sample.service.resources.service;

import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.service.common.entity.ConstantEntity;
import com.yamu.data.sample.service.resources.entity.po.ResourceHomePageParseRealtime;
import com.yamu.data.sample.service.resources.mapper.ResourceHomePageParseRealtimeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

/**
 * @author getiejun
 * @date 2021/10/19
 */
@Service
public class ResourceHomePageParseRealtimeServer {

    @Autowired
    private ResourceHomePageParseRealtimeMapper realtimeMapper;

    private static final String CODE_CNT_D = "parse_cnt_d";

    private static final String CODE_CNT_M = "parse_cnt_m";

    private static final String CODE_CNT_Y = "parse_cnt_y";

    private static final String CODE_CNT_W = "parse_cnt_w";

    public JSONObject findParseRealtime() {
        List<ResourceHomePageParseRealtime> parseRealtimeList = realtimeMapper.findAll();
        BigInteger dayParseCnt = calculateDayParseCnt(parseRealtimeList, ConstantEntity.INTERVAL_1D);
        BigInteger weekParseCnt = calculateDayParseCnt(parseRealtimeList, ConstantEntity.INTERVAL_1W);
        BigInteger monthParseCnt = calculateDayParseCnt(parseRealtimeList, ConstantEntity.INTERVAL_1M);
        BigInteger yearParseCnt = calculateDayParseCnt(parseRealtimeList, ConstantEntity.INTERVAL_1Y);
        JSONObject result = new JSONObject();
        result.put("yearParseCnt",yearParseCnt);
        result.put("monthParseCnt", monthParseCnt);
        result.put("weekParseCnt", weekParseCnt);
        result.put("dayParseCnt", dayParseCnt);
        return result;
    }

    private BigInteger calculateDayParseCnt(List<ResourceHomePageParseRealtime> parseRealtimeList, String interval) {
        BigInteger result = BigInteger.ZERO;
        ResourceHomePageParseRealtime parseRealtimeDay = new ResourceHomePageParseRealtime();
        ResourceHomePageParseRealtime parseRealtimeWeek = new ResourceHomePageParseRealtime();
        ResourceHomePageParseRealtime parseRealtimeMonth = new ResourceHomePageParseRealtime();
        ResourceHomePageParseRealtime parseRealtimeYear = new ResourceHomePageParseRealtime();
        for (ResourceHomePageParseRealtime parseRealtime : parseRealtimeList) {
            if(CODE_CNT_D.equals(parseRealtime.getCode())) {
                parseRealtimeDay = parseRealtime;
            } else if (CODE_CNT_W.equals(parseRealtime.getCode())) {
                parseRealtimeWeek = parseRealtime;
            } else if (CODE_CNT_M.equals(parseRealtime.getCode())) {
                parseRealtimeMonth = parseRealtime;
            } else {
                parseRealtimeYear = parseRealtime;
            }
        }
        switch (interval){
            case ConstantEntity.INTERVAL_1W:
                result = parseRealtimeDay.getCnt1min().add(parseRealtimeDay.getCnt10min()).add(parseRealtimeDay.getCntH()).add(parseRealtimeWeek.getCntD());
                break;
            case ConstantEntity.INTERVAL_1M:
                result = parseRealtimeDay.getCnt1min().add(parseRealtimeDay.getCnt10min()).add(parseRealtimeDay.getCntH()).add(parseRealtimeMonth.getCntD());
                break;
            case ConstantEntity.INTERVAL_1Y:
                result = parseRealtimeDay.getCnt1min().add(parseRealtimeDay.getCnt10min()).add(parseRealtimeDay.getCntH()).add(parseRealtimeYear.getCntD());
                break;
            default:
                result = parseRealtimeDay.getCnt1min().add(parseRealtimeDay.getCnt10min()).add(parseRealtimeDay.getCntH());
                break;
        }
        return result;
    }
}
