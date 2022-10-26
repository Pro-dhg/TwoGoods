package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanyTrend;
import com.yamu.data.sample.service.resources.mapper.PopularCompanyTrendMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author getiejun
 * Date 2020-07-1
 */
@Service
@Slf4j
public class PopularCompanyTrendService {

    @Autowired
    private PopularCompanyTrendMapper popularCompanyTrendMapper;

    private final String DEFAULT_INTERVAL_TYPE_ID = "1d";

    private final String DEFAULT_QUERY_TYPE_1H = "1h";

    private final int DEFAULT_ISP_NUMBER = 20;

    public PageResult findTrendList(PopularCompanyTrend popularCompanyTrend) throws YamuException {
        checkFindTrendListParam(popularCompanyTrend);
        Long total = Long.valueOf("0");
        List<PopularCompanyTrend> dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(popularCompanyTrend.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = popularCompanyTrendMapper.countFindTrendListParamAll(popularCompanyTrend);
            dataList = popularCompanyTrendMapper.findTrendListByParamAll(popularCompanyTrend);
            dataList.stream().forEach(popularCompanyTrendParam -> {
                popularCompanyTrendParam.setTimeRange(popularCompanyTrend.getStartTime() + "~" + popularCompanyTrend.getEndTime());
            });
        }else{
            total = popularCompanyTrendMapper.countFindTrendListParam(popularCompanyTrend);
            dataList = popularCompanyTrendMapper.findTrendListByParam(popularCompanyTrend);
            dataList.stream().forEach(popularCompanyTrendParam -> {
                popularCompanyTrendParam.setTimeRange(DateUtils.formatDataToString(popularCompanyTrendParam.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.stream().forEach(PopularCompanyTrend::buildRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindTrendListParam(PopularCompanyTrend popularCompanyTrend) throws YamuException {
        popularCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE_1H, DEFAULT_INTERVAL_TYPE_ID);
        ValidationResult validationResult = ValidationUtils.validateEntity(popularCompanyTrend);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }

    private void checkDefaultParseTimeParam(PopularCompanyTrend popularCompanyTrend) {
        popularCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE_1H, DEFAULT_INTERVAL_TYPE_ID);
    }

    public JSONObject findResourceReport(PopularCompanyTrend popularCompanyTrend, boolean isCount) throws ParseException {
        checkDefaultParseTimeParam(popularCompanyTrend);
        checkParam(popularCompanyTrend);
        List<PopularCompanyTrend> dataList = popularCompanyTrendMapper.findTrendReportGroupByIspByParam(popularCompanyTrend);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (PopularCompanyTrend companyTrend : dataList) {
            resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
        }
        /*
        BigInteger otherParseCnt = BigInteger.ZERO;
        if (isCount) {
            otherParseCnt = resourceReportCountIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);
        } else {
            otherParseCnt = resourceReportAllIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);
        }
        resourceReportAddOtherData(resultDataMap, otherParseCnt);
        */
        List<String> xAxisList = resultDataMap.keySet().stream().collect(Collectors.toList());
        List<BigInteger> totalList = resultDataMap.values().stream().collect(Collectors.toList());
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "解析次数");
        // 报表名称
        String reportName = "热点公司资源分布";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxisList, dataMap);
        return finalResult;
    }

    private void resourceReportAddOtherData(Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        if (!otherParseCnt.equals(BigInteger.ZERO)) {
            if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                resultDataMap.put("其他", otherParseCnt);
            } else {
                resultDataMap.put("其他", resultDataMap.get("其他").add(otherParseCnt));
            }
        }
    }

    private BigInteger resourceReportAllIspParseCntAndOther(List<PopularCompanyTrend> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        for (PopularCompanyTrend companyTrend : dataList) {
            if (companyTrend.getIsp().equals("其他")) {
                otherParseCnt = otherParseCnt.add(companyTrend.getARecordParseTotalCnt());
                continue;
            }
            resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
        }
        return otherParseCnt;
    }

    private BigInteger resourceReportCountIspParseCntAndOther(List<PopularCompanyTrend> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (PopularCompanyTrend companyTrend : dataList) {
            if (index >= DEFAULT_ISP_NUMBER) {
                if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                    resultDataMap.put("其他", companyTrend.getARecordParseTotalCnt());
                } else {
                    resultDataMap.put("其他", resultDataMap.get("其他").add(companyTrend.getARecordParseTotalCnt()));
                }
            } else {
                if (companyTrend.getIsp().equals("其他")) {
                    otherParseCnt = otherParseCnt.add(companyTrend.getARecordParseTotalCnt());
                    continue;
                }
                resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
                index++;
            }
        }
        return otherParseCnt;
    }

    public JSONObject findAccessReport(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        checkDefaultParseTimeParam(popularCompanyTrend);
        checkParam(popularCompanyTrend);
        List<PopularCompanyTrend> dataList = popularCompanyTrendMapper.findTrendReportGroupByParseByParam(popularCompanyTrend);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularCompanyTrend.getStartTime(), popularCompanyTrend.getEndTime(), popularCompanyTrend.getQueryType());

        //总数结果集,成功结果集
        List parseTotalResult = Lists.newArrayList();
        List netInParseTotalCnt = Lists.newArrayList();
        List netOutParseTotalCnt = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularCompanyTrend companyTrend : dataList) {
                parseTotalResult.add(companyTrend.getParseTotalCnt());
                netInParseTotalCnt.add(companyTrend.getNetInParseTotalCnt());
                netOutParseTotalCnt.add(companyTrend.getNetOutParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularCompanyTrend> collect = dataList.stream().collect(Collectors.toMap(PopularCompanyTrend::getParseTime, PopularCompanyTrend -> PopularCompanyTrend));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularCompanyTrend companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    parseTotalResult.add(companyDetail.getParseTotalCnt());
                    netInParseTotalCnt.add(companyDetail.getNetInParseTotalCnt());
                    netOutParseTotalCnt.add(companyDetail.getNetOutParseTotalCnt());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }

        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "parseTotalResult", parseTotalResult);
        dataMap.put(ReportUtils.LINE + "netInParseTotalCnt", netInParseTotalCnt);
        dataMap.put(ReportUtils.LINE + "netOutParseTotalCnt", netOutParseTotalCnt);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "parseTotalResult", "解析次数");
        legend.put(ReportUtils.LINE + "netInParseTotalCnt", "网内次数");
        legend.put(ReportUtils.LINE + "netOutParseTotalCnt", "出网次数");

        // 报表名称
        String reportName = "热点公司访问趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findRateReport(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        checkDefaultParseTimeParam(popularCompanyTrend);
        checkParam(popularCompanyTrend);
        List<PopularCompanyTrend> dataList = popularCompanyTrendMapper.findTrendReportGroupByParseByParam(popularCompanyTrend);
        dataList.stream().forEach(PopularCompanyTrend::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularCompanyTrend.getStartTime(), popularCompanyTrend.getEndTime(), popularCompanyTrend.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularCompanyTrend companyTrend : dataList) {
                netInRateResult.add(companyTrend.getNetInRate());
                parseInRateResult.add(companyTrend.getParseInRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularCompanyTrend> collect = dataList.stream().collect(Collectors.toMap(PopularCompanyTrend::getParseTime, PopularCompanyTrend -> PopularCompanyTrend));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularCompanyTrend companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    netInRateResult.add(companyDetail.getNetInRate());
                    parseInRateResult.add(companyDetail.getParseInRate());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }

        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "netInRateResult", netInRateResult);
        dataMap.put(ReportUtils.LINE + "parseInRateResult", parseInRateResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "netInRateResult", "本网率");
        legend.put(ReportUtils.LINE + "parseInRateResult", "本省率");

        // 报表名称
        String reportName = "热点公司本网率、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findParseReport(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        checkDefaultParseTimeParam(popularCompanyTrend);
        checkParam(popularCompanyTrend);
        List<PopularCompanyTrend> dataList = popularCompanyTrendMapper.findTrendReportGroupByParseByParam(popularCompanyTrend);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularCompanyTrend.getStartTime(), popularCompanyTrend.getEndTime(), popularCompanyTrend.getQueryType());

        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularCompanyTrend companyTrend : dataList) {
                CDNParseResult.add(companyTrend.getCdnParseTotalCnt());
                IDCParseResult.add(companyTrend.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularCompanyTrend> collect = dataList.stream().collect(Collectors.toMap(PopularCompanyTrend::getParseTime, PopularCompanyTrend -> PopularCompanyTrend));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularCompanyTrend companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    CDNParseResult.add(companyDetail.getCdnParseTotalCnt());
                    IDCParseResult.add(companyDetail.getIdcParseTotalCnt());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }

        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "CDNParseResult", CDNParseResult);
        dataMap.put(ReportUtils.LINE + "IDCParseResult", IDCParseResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "CDNParseResult", "CDN次数");
        legend.put(ReportUtils.LINE + "IDCParseResult", "IDC次数");

        // 报表名称
        String reportName = "热点公司CDN、IDC调度趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public List<PopularCompanyTrend> downloadByParam(PopularCompanyTrend popularCompanyTrend) throws YamuException {
        checkDownloadByParamMethodParam(popularCompanyTrend);
        List<PopularCompanyTrend> dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(popularCompanyTrend.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            dataList = popularCompanyTrendMapper.findTrendListByParamAll(popularCompanyTrend);
            dataList.stream().forEach(data -> {
                data.setTimeRange(popularCompanyTrend.getStartTime() + "~" + popularCompanyTrend.getEndTime());
            });
        }else{
            dataList = popularCompanyTrendMapper.findTrendListByParam(popularCompanyTrend);
            dataList.stream().forEach(data -> {
                data.setTimeRange(DateUtils.formatDataToString(data.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(PopularCompanyTrend popularCompanyTrend) throws YamuException {
        if (StrUtil.isEmpty(popularCompanyTrend.getStartTime()) || StrUtil.isEmpty(popularCompanyTrend.getEndTime())) {
            popularCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE_1H, DEFAULT_INTERVAL_TYPE_ID);
        }
        ValidationResult validationResult = ValidationUtils.validateEntity(popularCompanyTrend);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkDownloadByParamMethodParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
        popularCompanyTrend.setLimit(10000L);
        popularCompanyTrend.setOffset(0L);
    }

    private void checkParam(PopularCompanyTrend popularCompanyTrend) throws ParseException {
        // 按照时间粒度查询时，会传QueryTime参数，按照时间段查询时，不会传，不需要降维
        if (StrUtil.isNotEmpty(popularCompanyTrend.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(popularCompanyTrend.getQueryType(), popularCompanyTrend.getQueryTime());
            popularCompanyTrend.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            popularCompanyTrend.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            popularCompanyTrend.setQueryType(ReportUtils.queryTypeDowngrade(popularCompanyTrend.getQueryType()));
        } else {
            popularCompanyTrend.formatParseTime(popularCompanyTrend.getQueryType(), "1d");
        }
    }

    private void setUnknownLast(List<PopularCompanyTrend> dataList){
        for (PopularCompanyTrend popularCompanyTrend : dataList) {
            if("未知".equals(popularCompanyTrend.getIsp())){
                PopularCompanyTrend data = popularCompanyTrend;
                dataList.remove(popularCompanyTrend);
                dataList.add(data);
                break;
            }
        }
    }
}
