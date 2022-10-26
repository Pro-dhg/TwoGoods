package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheDomain;
import com.yamu.data.sample.service.resources.entity.po.ResourceDomainTopnDetail;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessAssessQueryVo;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessAssessRespVo;
import com.yamu.data.sample.service.resources.mapper.CdnBusinessAssessMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.yamu.data.sample.service.common.util.ReportUtils.REPORT_NAME;
import static com.yamu.data.sample.service.common.util.ReportUtils.XAXIS;

/**
 * @author zl.chen
 * @create 2022/10/21 16:03
 */

@Service
public class CdnBusinessAssessService {

    @Resource
    private CdnBusinessAssessMapper mapper;

    public PageResult tableDetail(CdnBusinessAssessQueryVo queryVo) {

        Long total = mapper.getCount(queryVo,queryVo.getQueryTable());
        List<CdnBusinessAssessRespVo> data = getData(queryVo);
        PageResult pageResult = new PageResult(total, data);
        return pageResult;
    }

    public List<CdnBusinessAssessRespVo> getData(CdnBusinessAssessQueryVo queryVo){
        String parseTime = queryVo.getStartTime() + "~" + queryVo.getEndTime();
        List<CdnBusinessAssessRespVo> list = mapper.tableDetail(queryVo,queryVo.getQueryTable());
        Map<String, String> compareMap = new HashMap<>();
        //同比时间
        if (queryVo.getCompareRule() == 1) {
            compareMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(queryVo.getStartTime(), queryVo.getEndTime());
        }
        //环比时间
        if (queryVo.getCompareRule() == 2) {
            compareMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(queryVo.getStartTime(), queryVo.getEndTime());
        }
        //其他时间段
        if (queryVo.getCompareRule() == 3) {
            compareMap.put(ReportUtils.EASIER_START, queryVo.getOtherStartTime());
            compareMap.put(ReportUtils.EASIER_END, queryVo.getOtherEndTime());
        }
        queryVo.setStartTime(compareMap.get(ReportUtils.EASIER_START));
        queryVo.setEndTime(compareMap.get(ReportUtils.EASIER_END));
        List<CdnBusinessAssessRespVo> compareList = null;
        List<String> collect = list.stream().map(data -> {
            return data.getBusiness();
        }).collect(Collectors.toList());
        if (list.size() > 0) {
            compareList = mapper.compareTableList(queryVo, collect,queryVo.getQueryTable());
        }
        Map<String, CdnBusinessAssessRespVo> map = new HashMap<>();
        assert compareList != null;
        compareList.forEach(data -> map.put(data.getBusiness(), data));
        CdnBusinessAssessRespVo forEachObject = new CdnBusinessAssessRespVo();
        list.forEach(data -> {
            CdnBusinessAssessRespVo cndBusAssess = map.getOrDefault(data.getBusiness(), forEachObject);
            data.setParseTime(parseTime);
            data.setAssessScoreRateOfChange(buildDoubleRatio(cndBusAssess.getAssessScore(), data.getAssessScore()));
            data.setParseTotalRateOfChange(buildRatio(cndBusAssess.getParseTotal(), data.getParseTotal()));
            data.setSucRateOfChange(buildDoubleRatio(cndBusAssess.getSucRate(), data.getSucRate()));
            data.setIcpRateOfChange(buildDoubleRatio(cndBusAssess.getIcpRate(), data.getIcpRate()));
            data.setLocalNetRateOfChange(buildDoubleRatio(cndBusAssess.getLocalNetRate(), data.getLocalNetRate()));
            data.setLocalProRateOfChange(buildDoubleRatio(cndBusAssess.getLocalProRate(), data.getLocalProRate()));
            data.setNeighborhoodRateRateOfChange(buildDoubleRatio(cndBusAssess.getNeighborhoodRate(), data.getNeighborhoodRate()));
        });
        return list;
    }
    public static Double buildRatio(Long beforeData, Long data) {
        if (beforeData == 0L || data == 0L) {
            return 0D;
        }
        BigDecimal result = new BigDecimal(data).subtract(new BigDecimal(beforeData)).divide(new BigDecimal(beforeData), 4, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public static Double buildDoubleRatio(Double beforeData, Double data) {
        if (beforeData == 0D || data == 0D) {
            return 0D;
        }
        BigDecimal result = new BigDecimal(data).subtract(new BigDecimal(beforeData)).divide(new BigDecimal(beforeData), 4, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public ResponseEntity scoreTrend(CdnBusinessAssessQueryVo queryVo) {
        List<CdnBusinessAssessRespVo> list = mapper.scoreTrend(queryVo,queryVo.getQueryTable());
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(queryVo.getStartTime(), queryVo.getEndTime(), queryVo.getQueryType());

        //填充数据
        List<Double> scoreList = new ArrayList<>();
        Map<String, CdnBusinessAssessRespVo> collect = list.stream().collect(Collectors.toMap(CdnBusinessAssessRespVo::getParseTime, CdnBusinessAssessRespVo -> CdnBusinessAssessRespVo));
        if (xAxisMap.size() == list.size()) {
            list.forEach(data -> scoreList.add(data.getAssessScore()));
        } else {
            //去掉没有数据的时间点
            List<Date> removeList = new ArrayList<>();
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date date = entry.getKey();
                String key = DateUtils.formatDataToString(date, DateUtils.DEFAULT_FMT);
                CdnBusinessAssessRespVo score = collect.get(key);
                if (score == null) {
                    removeList.add(date);
                } else {
                    scoreList.add(score.getAssessScore());
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }
        //图表种类名称
        Map<String, String> legend = Maps.newLinkedHashMap();
        //图表种类数据
        Map<String, List> dataMap = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "score", "得分");
        dataMap.put(ReportUtils.LINE + "score", scoreList);
        // 报表名称
        String reportName = "得分趋势";
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        finalReport.put(REPORT_NAME, reportName);
        finalReport.put(XAXIS, xAxisMap);
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return ResponseEntity.ok(finalResult);
    }

    public ResponseEntity indicatTrend(CdnBusinessAssessQueryVo queryVo) {
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(queryVo.getStartTime(), queryVo.getEndTime(), queryVo.getQueryType());
        List<CdnBusinessAssessRespVo> list = mapper.indicatTrend(queryVo,queryVo.getQueryTable());
        Map<String, String> compareMap = new HashMap<>();
        //同比时间
        if (queryVo.getCompareRule() == 1) {
            compareMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(queryVo.getStartTime(), queryVo.getEndTime());
        }
        //环比时间
        if (queryVo.getCompareRule() == 2) {
            compareMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(queryVo.getStartTime(), queryVo.getEndTime());
        }
        //其他时间段
        if (queryVo.getCompareRule() == 3) {
            compareMap.put(ReportUtils.EASIER_START, queryVo.getOtherStartTime());
            compareMap.put(ReportUtils.EASIER_END, queryVo.getOtherEndTime());
        }

        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startLocalTime = LocalDateTime.parse(queryVo.getStartTime(), FORMATTER);
        queryVo.setStartTime(compareMap.get(ReportUtils.EASIER_START));
        queryVo.setEndTime(compareMap.get(ReportUtils.EASIER_END));
        LocalDateTime endLocalTime = LocalDateTime.parse(queryVo.getStartTime(), FORMATTER);
        List<CdnBusinessAssessRespVo> lastList = mapper.indicatTrend(queryVo,queryVo.getQueryTable());
        //计算时间间隔
        Duration between = Duration.between(endLocalTime, startLocalTime);
        Map<String, Double> netRateLastMap = new HashMap<>();
        Map<String, Double> netRateMap = new HashMap<>();
        Map<String, Double> icpRateLastMap = new HashMap<>();
        Map<String, Double> icpRateMap = new HashMap<>();
        Map<String, Double> cityRateLastMap = new HashMap<>();
        Map<String, Double> cityRateMap = new HashMap<>();
        Map<String, Double> proRateLastMap = new HashMap<>();
        Map<String, Double> proRateMap = new HashMap<>();
        Map<String, Double> sucRateLastMap = new HashMap<>();
        Map<String, Double> sucRateMap = new HashMap<>();
        for (CdnBusinessAssessRespVo cdn : lastList) {
            LocalDateTime minus = LocalDateTime.parse(cdn.getParseTime(), FORMATTER).plus(between);
            cdn.setParseTime(minus.format(FORMATTER));
            netRateLastMap.put(cdn.getParseTime(), cdn.getLocalNetRate());
            icpRateLastMap.put(cdn.getParseTime(), cdn.getIcpRate());
            cityRateLastMap.put(cdn.getParseTime(), cdn.getLocalCityRate());
            proRateLastMap.put(cdn.getParseTime(), cdn.getLocalProRate());
            sucRateLastMap.put(cdn.getParseTime(), cdn.getSucRate());
        }
        for (CdnBusinessAssessRespVo cdn : list) {
            LocalDateTime minus = LocalDateTime.parse(cdn.getParseTime(), FORMATTER).plus(between);
            cdn.setParseTime(minus.format(FORMATTER));
            netRateMap.put(cdn.getParseTime(), cdn.getLocalNetRate());
            icpRateMap.put(cdn.getParseTime(), cdn.getIcpRate());
            cityRateMap.put(cdn.getParseTime(), cdn.getLocalCityRate());
            proRateMap.put(cdn.getParseTime(), cdn.getLocalProRate());
            sucRateMap.put(cdn.getParseTime(), cdn.getSucRate());
        }

        HashMap<Date, String> xAxisMapNetRate = new HashMap<>();
        HashMap<Date, String> xAxisMapIcpRate = new HashMap<>();
        HashMap<Date, String> xAxisMapCityRate = new HashMap<>();
        HashMap<Date, String> xAxisMapProRate = new HashMap<>();
        HashMap<Date, String> xAxisMapSucRate = new HashMap<>();
        xAxisMapNetRate.putAll(xAxisMap);
        xAxisMapIcpRate.putAll(xAxisMap);
        xAxisMapCityRate.putAll(xAxisMap);
        xAxisMapProRate.putAll(xAxisMap);
        xAxisMapSucRate.putAll(xAxisMap);

        JSONObject netIndicatData = getIndicatData(xAxisMapNetRate, netRateMap, netRateLastMap, "本网率", "本网率趋势");
        JSONObject icpIndicatData = getIndicatData(xAxisMapNetRate, icpRateMap, icpRateLastMap, "icp调度率", "icp调度率趋势");
        JSONObject cityIndicatData = getIndicatData(xAxisMapNetRate, cityRateMap, cityRateLastMap, "本市率", "本市率趋势");
        JSONObject proIndicatData = getIndicatData(xAxisMapNetRate, proRateMap, proRateLastMap, "本省率", "本省率趋势");
        JSONObject sucIndicatData = getIndicatData(xAxisMapNetRate, sucRateMap, sucRateLastMap, "成功率", "成功率趋势");
        List<JSONObject> dataList = new ArrayList<>();
        dataList.add(netIndicatData);
        dataList.add(icpIndicatData);
        dataList.add(cityIndicatData);
        dataList.add(proIndicatData);
        dataList.add(sucIndicatData);
        return ResponseEntity.ok(dataList);
    }
    public JSONObject getIndicatData(Map<Date, String> xAxisMap,Map<String, Double> rateMap,Map<String, Double> rateLastMap,String chartName,String reportNetInName){
            List rateList = new ArrayList();
            List rateChangeList = new ArrayList();
            List<Date> removeList = new ArrayList<>();
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                String s = DateUtils.formatDataToString(entry.getKey(), DateUtils.yyyy_MM_dd_hh_mm_ss);
                Double rate = rateMap.getOrDefault(s, null);
                Double rateLast = rateLastMap.getOrDefault(s, null);
                if(rate==null && rateLast==null){
                    removeList.add(entry.getKey());
                }else{
                    if(rate ==null){
                        rate = 0D;
                    }
                    if(rateLast == null){
                        rateLast = 0D;
                    }
                    rateList.add(rate);
                    rateChangeList.add(buildDoubleRatio(rateLast, rate));
                }

            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
            //图表种类名称
            Map<String, String> legend = Maps.newLinkedHashMap();
            //图表种类数据
            Map<String, List> dataMap = Maps.newLinkedHashMap();
            legend.put(ReportUtils.LINE + "rate", chartName);
            legend.put(ReportUtils.LINE + "rateChange", "对比变化率");
            dataMap.put(ReportUtils.LINE + "rate", rateList);
            dataMap.put(ReportUtils.LINE + "rateChange", rateChangeList);
            JSONObject finalReport = new JSONObject(new LinkedHashMap());
            finalReport.put(REPORT_NAME, reportNetInName);
            finalReport.put(XAXIS, xAxisMap);
            List<String> xAxis = new ArrayList<>(xAxisMap.values());
            JSONObject finalResult = ReportUtils.buildReport(reportNetInName, legend, xAxis, dataMap);
            return finalResult;
    }
    public ResponseEntity radar(CdnBusinessAssessQueryVo queryVo) {
        Map<String, Object> map = new HashMap<>();
        CdnBusinessAssessRespVo cdn = mapper.rader(queryVo,queryVo.getQueryTable());
        map.put("network_in",cdn.getLocalNetRate());
        map.put("icp",cdn.getIcpRate());
        map.put("city_in",cdn.getLocalCityRate());
        map.put("province_in",cdn.getLocalProRate());
        map.put("nerghborhood",cdn.getNeighborhoodRate());
        map.put("suc_rate",cdn.getSucRate());
        return ResponseEntity.ok(map);
    }

    public ResponseEntity scoreCityTrend(CdnBusinessAssessQueryVo queryVo) {
        List<Map<String,Double>>  list = mapper.scoreCityTrend(queryVo,queryVo.getQueryTable());
        List<String> xAxisList = new ArrayList<>();
        List<Double> totalList = new ArrayList<>();
        list.forEach(data -> {
            xAxisList.add(String.valueOf(data.get("city")));
            totalList.add(data.get("score"));
        });
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "得分");
        // 报表名称
        String reportName = "各地市得分对比";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxisList, dataMap);
        return ResponseEntity.ok(finalResult);
    }

    public List<String> getCsving(CdnBusinessAssessQueryVo queryVo) {
        List<String> str = new ArrayList<>();
        queryVo.setLimit(10000L);
        List<CdnBusinessAssessRespVo> data = getData(queryVo);
        StringBuilder csvLine = new StringBuilder();
        for (CdnBusinessAssessRespVo datum : data) {
            csvLine.append(datum.getParseTime()).append(",")
            .append(datum.getBusiness()).append(",")
                    .append(datum.getAssessScore()).append(",")
                    .append(datum.getAssessScoreRateOfChange()).append(",")
                    .append(datum.getParseTotal()).append(",")
                    .append(datum.getParseTotalRateOfChange()).append(",")
            .append(datum.getSucRate()).append(",")
                    .append(datum.getSucRateOfChange()).append(",")
                    .append(datum.getIcpRate()).append(",")
                    .append(datum.getIcpRateOfChange()).append(",")
            .append(datum.getLocalNetRate()).append(",")
                    .append(datum.getLocalNetRateOfChange()).append(",")
                    .append(datum.getLocalProRate()).append(",")
                    .append(datum.getLocalProRateOfChange()).append(",")
                    .append(datum.getNeighborhoodRate()).append(",")
                    .append(datum.getNeighborhoodRateRateOfChange()).append(",").append("\n");
            str.add(csvLine.toString());
        }
        return str;
    }
}
