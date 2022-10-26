package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.FocusCompanyTrend;
import com.yamu.data.sample.service.resources.mapper.FocusCompanyTrendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author zhangyanping
 * Date 2020-07-1
 */
@Service
public class FocusCompanyTrendService {

    @Autowired
    private FocusCompanyTrendMapper focusCompanyTrendMapper;

    /**
     * 默认开始时间与结束时间为最近1天, 时间粒度为1d
     */
    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    private final int DEFAULT_ISP_NUMBER = 20;

    /**
     * 重点公司访问趋势分析: 访问重点公司趋势明细
     * @param focusCompanyTrend
     * @return
     */
    public PageResult findTrendDetailByPage (FocusCompanyTrend focusCompanyTrend){
        checkFindTrendListParam(focusCompanyTrend);
        Long total = Long.valueOf("0");
        List<FocusCompanyTrend> data = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(focusCompanyTrend.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = focusCompanyTrendMapper.countTrendDetailByPageAll(focusCompanyTrend);
            data = focusCompanyTrendMapper.findTrendDetailByPageAll(focusCompanyTrend);
            data.stream().forEach(focusCompanyTrendParam -> {
                focusCompanyTrendParam.setTimeRange(focusCompanyTrend.getStartTime() + "~" + focusCompanyTrend.getEndTime());
            });
        }else{
            total = focusCompanyTrendMapper.countTrendDetailByPage(focusCompanyTrend);
            data = focusCompanyTrendMapper.findTrendDetailByPage(focusCompanyTrend);
            data.stream().forEach(focusCompanyTrendParam -> {
                focusCompanyTrendParam.setTimeRange(DateUtils.formatDataToString(focusCompanyTrendParam.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        data.stream().forEach(FocusCompanyTrend::buildRate);
        PageResult result = new PageResult(total, data);
        return result;
    }
    private void checkFindTrendListParam(FocusCompanyTrend focusCompanyTrend) {
        focusCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
    }

    /**
     * 重点公司访问趋势分析: 重点公司资源分布
     * @param focusCompanyTrend
     * @return
     */
    public JSONObject findResourceReport(FocusCompanyTrend focusCompanyTrend, boolean isCount) throws ParseException {
        checkFindTrendListParam(focusCompanyTrend);
        checkParam(focusCompanyTrend);
        List<FocusCompanyTrend> dataList = focusCompanyTrendMapper.findTrendReportGroupByIspByParam(focusCompanyTrend);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (FocusCompanyTrend companyTrend : dataList) {
            resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
        }
        /*
        BigInteger otherParseCnt = BigInteger.ZERO;
        if(isCount) {
            otherParseCnt = resourceReportCountIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);
        } else {
            otherParseCnt = resourceReportAllIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);
        }
        resourceReportAddOtherData(resultDataMap, otherParseCnt);
        */
        List<String> xAxisList = new ArrayList<>(resultDataMap.keySet());
        List<BigInteger> totalList = new ArrayList<>(resultDataMap.values());
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "解析次数");
        // 报表名称
        String reportName = "重点公司资源分布";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxisList, dataMap);
        return finalResult;
    }

    private void resourceReportAddOtherData(Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        if (!otherParseCnt.equals(BigInteger.ZERO)) {
            if(ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                resultDataMap.put("其他", otherParseCnt);
            } else {
                resultDataMap.put("其他", resultDataMap.get("其他").add(otherParseCnt));
            }
        }
    }

    private BigInteger resourceReportAllIspParseCntAndOther(List<FocusCompanyTrend> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        for (FocusCompanyTrend companyTrend : dataList) {
            if(companyTrend.getIsp().equals("其他")) {
                otherParseCnt = otherParseCnt.add(companyTrend.getARecordParseTotalCnt());
                continue;
            }
            resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
        }
        return otherParseCnt;
    }

    private BigInteger resourceReportCountIspParseCntAndOther(List<FocusCompanyTrend> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (FocusCompanyTrend companyTrend : dataList) {
            if(index >= DEFAULT_ISP_NUMBER) {
                if(ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                    resultDataMap.put("其他", companyTrend.getARecordParseTotalCnt());
                } else {
                    resultDataMap.put("其他", resultDataMap.get("其他").add(companyTrend.getARecordParseTotalCnt()));
                }
            } else {
                if(companyTrend.getIsp().equals("其他")) {
                    otherParseCnt = otherParseCnt.add(companyTrend.getARecordParseTotalCnt());
                    continue;
                }
                resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
                index++;
            }
        }
        return otherParseCnt;
    }


    public JSONObject findAccessReport(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        checkFindTrendListParam(focusCompanyTrend);
        checkParam(focusCompanyTrend);
        List<FocusCompanyTrend> dataList = focusCompanyTrendMapper.findTrendReportGroupByParseTimeByParam(focusCompanyTrend);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(focusCompanyTrend.getStartTime(), focusCompanyTrend.getEndTime(), focusCompanyTrend.getQueryType());

        //总数结果集,成功结果集
        List parseTotalResult = Lists.newArrayList();
        List netInParseTotalCnt = Lists.newArrayList();
        List netOutParseTotalCnt = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (FocusCompanyTrend companyTrend : dataList) {
                parseTotalResult.add(companyTrend.getParseTotalCnt());
                netInParseTotalCnt.add(companyTrend.getNetInParseTotalCnt());
                netOutParseTotalCnt.add(companyTrend.getNetOutParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, FocusCompanyTrend> collect = dataList.stream().collect(Collectors.toMap(FocusCompanyTrend::getParseTime, FocusCompanyTrend -> FocusCompanyTrend));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                FocusCompanyTrend companyDetail = collect.get(xKey);
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
        String reportName = "重点公司访问趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findRateReport(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        checkFindTrendListParam(focusCompanyTrend);
        checkParam(focusCompanyTrend);
        List<FocusCompanyTrend> dataList = focusCompanyTrendMapper.findTrendReportGroupByParseTimeByParam(focusCompanyTrend);
        dataList.stream().forEach(FocusCompanyTrend::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(focusCompanyTrend.getStartTime(), focusCompanyTrend.getEndTime(), focusCompanyTrend.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (FocusCompanyTrend companyTrend : dataList) {
                netInRateResult.add(companyTrend.getNetInRate());
                parseInRateResult.add(companyTrend.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, FocusCompanyTrend> collect = dataList.stream().collect(Collectors.toMap(FocusCompanyTrend::getParseTime, FocusCompanyTrend -> FocusCompanyTrend));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                FocusCompanyTrend companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    netInRateResult.add(companyDetail.getNetInRate());
                    parseInRateResult.add(companyDetail.getWithinRate());
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
        String reportName = "重点公司本网率、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findParseReport(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        checkFindTrendListParam(focusCompanyTrend);
        checkParam(focusCompanyTrend);
        List<FocusCompanyTrend> dataList = focusCompanyTrendMapper.findTrendReportGroupByParseTimeByParam(focusCompanyTrend);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(focusCompanyTrend.getStartTime(), focusCompanyTrend.getEndTime(), focusCompanyTrend.getQueryType());

        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (FocusCompanyTrend companyTrend : dataList) {
                CDNParseResult.add(companyTrend.getCdnParseTotalCnt());
                IDCParseResult.add(companyTrend.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, FocusCompanyTrend> collect = dataList.stream().collect(Collectors.toMap(FocusCompanyTrend::getParseTime, FocusCompanyTrend -> FocusCompanyTrend));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                FocusCompanyTrend companyDetail = collect.get(xKey);
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
        String reportName = "重点公司CDN、IDC调度趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public List<FocusCompanyTrend> downloadByParam(FocusCompanyTrend focusCompanyTrend) {
        checkDownloadByParamMethodParam(focusCompanyTrend);
        List<FocusCompanyTrend> dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(focusCompanyTrend.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            dataList = focusCompanyTrendMapper.findTrendDetailByPageAll(focusCompanyTrend);
            dataList.stream().forEach(focusCompanyTrendParam -> {
                focusCompanyTrendParam.setTimeRange(focusCompanyTrend.getStartTime() + "~" + focusCompanyTrend.getEndTime());
            });
        }else{
            dataList = focusCompanyTrendMapper.findTrendDetailByPage(focusCompanyTrend);
            dataList.stream().forEach(focusCompanyTrendParam -> {
                focusCompanyTrendParam.setTimeRange(DateUtils.formatDataToString(focusCompanyTrendParam.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(FocusCompanyTrend focusCompanyTrend){
        if (StrUtil.isEmpty(focusCompanyTrend.getStartTime()) || StrUtil.isEmpty(focusCompanyTrend.getEndTime())) {
            focusCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        focusCompanyTrend.setLimit(10000L);
        focusCompanyTrend.setOffset(0L);
    }

    private void checkParam(FocusCompanyTrend focusCompanyTrend) throws ParseException {
        // 按照时间粒度查询时，会传QueryTime参数，按照时间段查询时，不会传，不需要降维
        if (StrUtil.isNotEmpty(focusCompanyTrend.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(focusCompanyTrend.getQueryType(), focusCompanyTrend.getQueryTime());
            focusCompanyTrend.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            focusCompanyTrend.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            focusCompanyTrend.setQueryType(ReportUtils.queryTypeDowngrade(focusCompanyTrend.getQueryType()));
        } else {
            focusCompanyTrend.formatParseTime(focusCompanyTrend.getQueryType(), "1d");
        }
    }

    private void setUnknownLast(List<FocusCompanyTrend> dataList){
        for (FocusCompanyTrend focusCompanyTrend : dataList) {
            if("未知".equals(focusCompanyTrend.getIsp())){
                FocusCompanyTrend data = focusCompanyTrend;
                dataList.remove(focusCompanyTrend);
                dataList.add(data);
                break;
            }
        }
    }

}
