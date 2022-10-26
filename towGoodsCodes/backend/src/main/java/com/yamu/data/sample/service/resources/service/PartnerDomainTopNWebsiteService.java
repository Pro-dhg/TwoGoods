package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNWebsite;
import com.yamu.data.sample.service.resources.mapper.PartnerDomainTopNWebsiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: ZhangYanping
 * @Date: 2021/7/5 23:46
 * @Desc: 特定域名TopN网站分析
 */

@Service
public class PartnerDomainTopNWebsiteService {

    @Autowired
    private PartnerDomainTopNWebsiteMapper mapper;


    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    private final int DEFAULT_ISP_NUMBER = 20;

    public PageResult findDetailReportByPage(PartnerDomainTopNWebsite param) {
        checkQueryTime(param);
        Long total = mapper.countDetailReportByPage(param);
        List<PartnerDomainTopNWebsite> data = mapper.findDetailReportByPage(param);
        data.stream().forEach(PartnerDomainTopNWebsite::buildRate);
        PageResult result = new PageResult(total, data);
        return result;
    }

    private void checkQueryTime(PartnerDomainTopNWebsite param) {
        param.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
    }

    public JSONObject findTop20ReportGroupByParseTimeByParam(PartnerDomainTopNWebsite param) {
        checkQueryTime(param);
        List<PartnerDomainTopNWebsite> dataList = mapper.findTop20ReportGroupByParseTimeByParam(param);
        // 设置结果集
        for (PartnerDomainTopNWebsite topNWebsite : dataList) {
                if (ObjectUtil.isNotEmpty(topNWebsite.getWebsiteName())) {
//                    List<Long> xAxisList = new ArrayList<Long>(topNWebsite.getParseTotalCnt());
//                    List<String> websiteList = new ArrayList<>(resultDataMap.values());
//                    param.getParseTotalCnt();
//                    continue;
                }
        }

        Map<String, List> dataMap = new LinkedHashMap<>();
        dataMap.put(ReportUtils.BAR + "total", dataList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "本省域名解析次数");
        // 报表名称
        String reportName = "Top20网站排名分析趋势";
//        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxisList, dataMap);
//        return finalResult;
        return null;
    }

    // 资源
    public JSONObject findResourceReport(PartnerDomainTopNWebsite param, boolean isCount) {
        checkQueryTime(param);
        List<PartnerDomainTopNWebsite> dataList = mapper.findTrendReportGroupByIspByParam(param);

        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        BigInteger otherParseCnt = BigInteger.ZERO;
        if (isCount) {
            otherParseCnt = resourceReportCountIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);
        } else {
            otherParseCnt = resourceReportAllIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);
        }
        resourceReportAddOtherData(resultDataMap, otherParseCnt);
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

    // 统计完isp='其他'之后，添加到集合
    private void resourceReportAddOtherData(Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        if (!otherParseCnt.equals(BigInteger.ZERO)) {
            if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                resultDataMap.put("其他", otherParseCnt);
            } else {
                resultDataMap.put("其他", resultDataMap.get("其他").add(otherParseCnt));
            }
        }
    }

    // isCount=false: 统计运营商(isp='其他')和数据map
    private BigInteger resourceReportAllIspParseCntAndOther(List<PartnerDomainTopNWebsite> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        for (PartnerDomainTopNWebsite param : dataList) {
            if (param.getIsp().equals("其他")) {
                otherParseCnt = otherParseCnt.add(param.getParseTotalCnt());
                continue;
            }
            resultDataMap.put(param.getIsp(), param.getParseTotalCnt());
        }
        return otherParseCnt;
    }

    // isCount=true: 统计运营商和数据map，运营商数大于20为其他(包括isp='其他')
    private BigInteger resourceReportCountIspParseCntAndOther(List<PartnerDomainTopNWebsite> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (PartnerDomainTopNWebsite param : dataList) {
            if (index >= DEFAULT_ISP_NUMBER) {
                if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                    resultDataMap.put("其他", param.getParseTotalCnt());
                } else {
                    resultDataMap.put("其他", resultDataMap.get("其他").add(param.getParseTotalCnt()));
                }
            } else {
                if (param.getIsp().equals("其他")) {
                    otherParseCnt = otherParseCnt.add(param.getParseTotalCnt());
                    continue;
                }
                resultDataMap.put(param.getIsp(), param.getParseTotalCnt());
                index++;
            }
        }
        return otherParseCnt;
    }

    // TopN网站：本省、本网趋势
    public JSONObject findTopNRateReport(PartnerDomainTopNWebsite param) {
        checkQueryTime(param);
        List<PartnerDomainTopNWebsite> dataList = mapper.findTopNReportGroupByParseTimeByParam(param);
        dataList.stream().forEach(PartnerDomainTopNWebsite::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(param.getStartTime(), param.getEndTime(), param.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PartnerDomainTopNWebsite topNWebsite : dataList) {
                netInRateResult.add(topNWebsite.getNetInRate());
                parseInRateResult.add(topNWebsite.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PartnerDomainTopNWebsite> collect = dataList.stream().collect(Collectors.toMap(PartnerDomainTopNWebsite::getParseTime, PartnerDomainTopNWebsite -> PartnerDomainTopNWebsite));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PartnerDomainTopNWebsite topNWebsite = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(topNWebsite)) {
                    netInRateResult.add(topNWebsite.getNetInRate());
                    parseInRateResult.add(topNWebsite.getWithinRate());
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
        String reportName = "Top" + param.getRankNumber() + "网站本网、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    // 某网站：本省、本网趋势
    public JSONObject findRateReport(PartnerDomainTopNWebsite param) {
        checkQueryTime(param);
        List<PartnerDomainTopNWebsite> dataList = mapper.findTrendReportGroupByParseTimeByParam(param);
        dataList.stream().forEach(PartnerDomainTopNWebsite::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(param.getStartTime(), param.getEndTime(), param.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PartnerDomainTopNWebsite topNWebsite : dataList) {
                netInRateResult.add(topNWebsite.getNetInRate());
                parseInRateResult.add(topNWebsite.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PartnerDomainTopNWebsite> collect = dataList.stream().collect(Collectors.toMap(PartnerDomainTopNWebsite::getParseTime, PartnerDomainTopNWebsite -> PartnerDomainTopNWebsite));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PartnerDomainTopNWebsite topNWebsite = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(topNWebsite)) {
                    netInRateResult.add(topNWebsite.getNetInRate());
                    parseInRateResult.add(topNWebsite.getWithinRate());
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
        String reportName = param.getWebsiteName() + "网站本网、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    // 某网站：CDN、IDC
    public JSONObject findParseReport(PartnerDomainTopNWebsite param) {
        checkQueryTime(param);
        List<PartnerDomainTopNWebsite> dataList = mapper.findTrendReportGroupByParseTimeByParam(param);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(param.getStartTime(), param.getEndTime(), param.getQueryType());

        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PartnerDomainTopNWebsite topNWebsite : dataList) {
                CDNParseResult.add(topNWebsite.getCdnParseTotalCnt());
                IDCParseResult.add(topNWebsite.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PartnerDomainTopNWebsite> collect = dataList.stream().collect(Collectors.toMap(PartnerDomainTopNWebsite::getParseTime, PartnerDomainTopNWebsite -> PartnerDomainTopNWebsite));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PartnerDomainTopNWebsite topNWebsite = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(topNWebsite)) {
                    CDNParseResult.add(topNWebsite.getCdnParseTotalCnt());
                    IDCParseResult.add(topNWebsite.getIdcParseTotalCnt());
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
        String reportName = param.getWebsiteName() + "CDN、IDC解析趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public List<PartnerDomainTopNWebsite> downloadByParam(PartnerDomainTopNWebsite param) {
        checkDownloadByParamMethodParam(param);
        List<PartnerDomainTopNWebsite> dataList = mapper.findDetailReportByPage(param);
        return dataList;
    }

    private void checkDownloadByParamMethodParam(PartnerDomainTopNWebsite param) {
        if (StrUtil.isEmpty(param.getStartTime()) || StrUtil.isEmpty(param.getEndTime())) {
            param.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        param.setLimit(10000L);
        param.setOffset(0L);
    }

}
