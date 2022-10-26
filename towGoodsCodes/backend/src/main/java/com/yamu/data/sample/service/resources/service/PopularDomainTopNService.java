package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.StrUtil;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainDetail;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainTopN;
import com.yamu.data.sample.service.resources.mapper.PopularDomainDetailMapper;
import com.yamu.data.sample.service.resources.mapper.PopularDomainTopNMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author getiejun
 * Date 2020-07-1
 */
@Service
public class PopularDomainTopNService {

    @Autowired
    private PopularDomainTopNMapper popularDomainTopNMapper;

    @Autowired
    private PopularDomainDetailMapper popularDomainDetailMapper;

    private static final String RESOURCE_POPULAR_DOMAIN_TOP_N_TABLE_PREFIX = "rpt_resource_popular_domain_topn_";

    private static final String RESOURCE_POPULAR_DOMAIN_DETAIL_TABLE_PREFIX = "rpt_resource_popular_domain_detail_";

    public PageResult findRankNumber(PopularDomainTopN popularDomainTopN) throws Exception {
        checkFindRankNumberParam(popularDomainTopN);
        long seconds = getSeconds(popularDomainTopN.getStartTime(), popularDomainTopN.getEndTime());
        String resultTable = RESOURCE_POPULAR_DOMAIN_TOP_N_TABLE_PREFIX + (seconds > (24 * 60 * 60) ? "1d" : (seconds > (60 * 60) ? "1h" : "1min"));
        // 查TopN对象
        Long total = popularDomainTopNMapper.countFindRankNumberByParam(popularDomainTopN, resultTable);
        List<PopularDomainTopN> dataList1 = popularDomainTopNMapper.findRankNumberByParam(popularDomainTopN, resultTable);

        if (dataList1.size() > 0) {
            // 取TopN域名
            ArrayList<String> domainList = Lists.newArrayList();
            dataList1.forEach(topN -> {
                domainList.add(topN.getDomainName());
            });
            // 计算上个时间段
            getLastTime(popularDomainTopN);
            // 查上个时间TopN对象
            List<PopularDomainTopN> dataList2 = popularDomainTopNMapper.findLastRankNumber(popularDomainTopN, resultTable, domainList);
            // 组装返回对象
            dataList1.forEach(x1 -> {
                dataList2.forEach(x2 -> {
                    if (x1.getDomainName().equals(x2.getDomainName())) {
                        x1.setLastRankNumber(x2.getLastRankNumber());
                        x1.setLastParseTotalCnt(x2.getLastParseTotalCnt());
                        x1.setParseVariationCnt(x1.getParseTotalCnt().subtract(x2.getLastParseTotalCnt()));
                    }
                });
            });
        }
        dataList1.forEach(x -> {
            if (x.getLastParseTotalCnt() == null)
                x.setLastParseTotalCnt(new BigInteger("0"));
            if (x.getParseVariationCnt() == null)
                x.setParseVariationCnt(x.getParseTotalCnt());
        });
        PageResult pageResult = new PageResult(total, dataList1);
        return pageResult;
    }

    private void getLastTime(PopularDomainTopN popularDomainTopN) {
        //计算环比时间段
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startLocalTime = LocalDateTime.parse(popularDomainTopN.getStartTime(), FORMATTER);
        LocalDateTime endLocalTime = LocalDateTime.parse(popularDomainTopN.getEndTime(), FORMATTER);
        //计算时间间隔
        Duration between = Duration.between(startLocalTime, endLocalTime);
        // 向前推一个时间段
        LocalDateTime startTime = startLocalTime.minus(between);
        LocalDateTime endTime = endLocalTime.minus(between);
        popularDomainTopN.setStartTime(startTime.format(FORMATTER));
        popularDomainTopN.setEndTime(endTime.format(FORMATTER));
    }

    private void checkFindRankNumberParam(PopularDomainTopN popularDomainTopN) {
        if (StrUtil.isEmpty(popularDomainTopN.getStartTime()) || StrUtil.isEmpty(popularDomainTopN.getEndTime())) {
            popularDomainTopN.formatParseTime("1d", "1d");
        }
    }

    public List<PopularDomainTopN> downloadByParam(PopularDomainTopN popularDomainTopN) throws Exception {
        checkDownloadByParamMethodParam(popularDomainTopN);
        long seconds = getSeconds(popularDomainTopN.getStartTime(), popularDomainTopN.getEndTime());
        String resultTable = RESOURCE_POPULAR_DOMAIN_TOP_N_TABLE_PREFIX + (seconds > (24 * 60 * 60) ? "1d" : (seconds > (60 * 60) ? "1h" : "1min"));
        // 计算上个时间段
        getLastTimeDown(popularDomainTopN);
        List<PopularDomainTopN> dataList = popularDomainTopNMapper.download(popularDomainTopN, resultTable);
        dataList.forEach(x -> {
            if (x.getLastParseTotalCnt() == null)
                x.setLastParseTotalCnt(new BigInteger("0"));
            x.setParseVariationCnt(x.getParseTotalCnt().subtract(x.getLastParseTotalCnt()));
        });
        return dataList;
    }

    private void getLastTimeDown(PopularDomainTopN popularDomainTopN) {
        //计算环比时间段
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startLocalTime = LocalDateTime.parse(popularDomainTopN.getStartTime(), FORMATTER);
        LocalDateTime endLocalTime = LocalDateTime.parse(popularDomainTopN.getEndTime(), FORMATTER);
        //计算时间间隔
        Duration between = Duration.between(startLocalTime, endLocalTime);
        // 向前推一个时间段
        LocalDateTime startTime = startLocalTime.minus(between);
        LocalDateTime endTime = endLocalTime.minus(between);
        popularDomainTopN.setLastStartTime(startTime.format(FORMATTER));
        popularDomainTopN.setLastEndTime(endTime.format(FORMATTER));
    }

    private void checkDownloadByParamMethodParam(PopularDomainTopN popularDomainTopN) {
        if (StrUtil.isEmpty(popularDomainTopN.getStartTime()) || StrUtil.isEmpty(popularDomainTopN.getEndTime())) {
            popularDomainTopN.formatParseTime("1d", "1d");
        }
        popularDomainTopN.setLimit(10000L);
        popularDomainTopN.setOffset(0L);
    }

    public PageResult findDomainDetail(PopularDomainDetail popularDomainDetail) throws Exception {
        checkFindDomainDetailParam(popularDomainDetail);
        long seconds = getSeconds(popularDomainDetail.getStartTime(), popularDomainDetail.getEndTime());
        String resultTable = RESOURCE_POPULAR_DOMAIN_DETAIL_TABLE_PREFIX + (seconds > (24 * 60 * 60) ? "1d" : (seconds > (60 * 60) ? "1h" : "1min"));
        Long total = popularDomainDetailMapper.countFindDomainDetailByParam(popularDomainDetail, resultTable);
        List<PopularDomainDetail> dataList = popularDomainDetailMapper.findDomainDetailByParam(popularDomainDetail, resultTable);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindDomainDetailParam(PopularDomainDetail popularDomainDetail) {
        if (StrUtil.isEmpty(popularDomainDetail.getStartTime()) || StrUtil.isEmpty(popularDomainDetail.getEndTime())) {
            popularDomainDetail.setDefaultTime();
        }
    }

    private long getSeconds(String startTime, String endTime) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date start = format.parse(startTime);
        Date end = format.parse(endTime);
        return (end.getTime() - start.getTime()) / 1000;
    }
}
