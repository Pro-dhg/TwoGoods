package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.CombinedXAxisBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteDetail;
import com.yamu.data.sample.service.resources.mapper.SpecificDomainWebsiteMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author wanghe
 * @Date 2021/7/22
 * @DESC 特定域名Topn网站分析
 */
@Service
public class SpecificDomainWebsiteService {
    private static final int DEFAULT_ISP_NUMBER = 20;
    @Autowired
    private SpecificDomainWebsiteMapper specificDomainWebsiteMapper;

    /**
     * TopN网站排名趋势.
     *
     * @param domainWebsiteDetail
     * @return
     */
    public JSONObject findRankNumber(SpecificDomainWebsiteDetail domainWebsiteDetail) {
        //1.check参数2.查询参数，统计3.处理报表
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        Map<String, String> easierTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainWebsiteDetail.getStartTime(), domainWebsiteDetail.getEndTime());
        List<String> websiteList = Lists.newArrayList();
        List<BigInteger> nowParseTotalList = Lists.newArrayList();
        List<BigInteger> easierParseTotalList = Lists.newArrayList();
        List<SpecificDomainWebsiteDetail> easierDataList = Lists.newArrayList();
        List<SpecificDomainWebsiteDetail> nowDataList = Lists.newArrayList();
        // 处理报表，查询数据
        if (StatisticsWayEnum.ALL.getType().equals(domainWebsiteDetail.getStatisticsWay())) {
            // 查询当前明细表分页数据
            nowDataList = specificDomainWebsiteMapper.queryDataGroupByWebsiteByParam(domainWebsiteDetail);
            // 网站名相同,分类不同的数据按照网站合并:
            Map<String, BigInteger> nowDataMap = nowDataList.stream()
                    .collect(Collectors.toMap(
                            SpecificDomainWebsiteDetail::getWebsiteAppName,
                            SpecificDomainWebsiteDetail::getParseTotalCnt,
                            BigInteger::add));
            websiteList = nowDataList.stream().map(SpecificDomainWebsiteDetail::getWebsiteAppName).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(websiteList)) {
                domainWebsiteDetail.setStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                domainWebsiteDetail.setEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                // 仅按照网站名称集合查询
                easierDataList = specificDomainWebsiteMapper.queryRankNumberGroupByWebsite(domainWebsiteDetail, websiteList);

                Map<String, BigInteger> easierDataMap = easierDataList.stream()
                        .collect(Collectors.toMap(
                                SpecificDomainWebsiteDetail::getWebsiteAppName,
                                SpecificDomainWebsiteDetail::getParseTotalCnt));
                //是否补点
                for (String website : websiteList) {
                    nowParseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(website)) ? BigInteger.ZERO : nowDataMap.get(website));
                    easierParseTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(website)) ? BigInteger.ZERO : easierDataMap.get(website));
                }
            }
        } else {
            // 查询当前明细表分页数据
            nowDataList = specificDomainWebsiteMapper.queryDataGroupByParseTimeAndWebsiteByParam(domainWebsiteDetail);
            // 按照时间和网站名进行分组
            Map<CombinedXAxisBO, List<SpecificDomainWebsiteDetail>> collect = nowDataList.stream().collect(Collectors.groupingBy(
                    websiteDetail -> new CombinedXAxisBO(
                            buildYOYTimePointParamByStartTimeAndEndTime(
                                    websiteDetail.getParseTime(),
                                    domainWebsiteDetail.getStartTime(),
                                    domainWebsiteDetail.getEndTime()),
                            websiteDetail.getWebsiteAppName())));
            LinkedHashMap<CombinedXAxisBO, BigInteger> nowDataMap = Maps.newLinkedHashMap();
            // 组装nowDataMap
            collect.forEach((key, value) -> {
                BigInteger reduce = value.stream().map(SpecificDomainWebsiteDetail::getParseTotalCnt).reduce(BigInteger.ZERO, BigInteger::add);
                nowDataMap.put(key, reduce);
            });

            websiteList = nowDataList.stream().map(SpecificDomainWebsiteDetail::getWebsiteAppName).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(websiteList)) {
                List<String> websiteTypes = websiteList.stream().distinct().collect(Collectors.toList());
                domainWebsiteDetail.setStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                domainWebsiteDetail.setEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                easierDataList = specificDomainWebsiteMapper.queryRankNumberGroupByWebsiteByTime(domainWebsiteDetail, websiteTypes);
                Map<CombinedXAxisBO, BigInteger> easierDataMap = easierDataList.stream().collect(Collectors.toMap(domainWebsite -> new CombinedXAxisBO(domainWebsite.getParseTime(), domainWebsite.getWebsiteAppName()), SpecificDomainWebsiteDetail::getParseTotalCnt));
                for (SpecificDomainWebsiteDetail websiteDetail : nowDataList) {
                    CombinedXAxisBO combinedXAxisBO = new CombinedXAxisBO(buildYOYTimePointParamByStartTimeAndEndTime(websiteDetail.getParseTime(), domainWebsiteDetail.getStartTime(), domainWebsiteDetail.getEndTime()), websiteDetail.getWebsiteAppName());
                    nowParseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(combinedXAxisBO)) ? BigInteger.ZERO : nowDataMap.get(combinedXAxisBO));
                    easierParseTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(combinedXAxisBO)) ? BigInteger.ZERO : easierDataMap.get(combinedXAxisBO));
                }
            }
        }
        JSONObject finalResult = buildRankReportWithParam("Top20网站排名", websiteList, nowParseTotalList, easierParseTotalList);
        return finalResult;
    }

    private Date buildYOYTimePointParamByStartTimeAndEndTime(Date parseTime, String startTime, String endTime) {
        //计算当前时间段和环比时间段
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DateUtils.DEFAULT_FMT);
        LocalDateTime startLocalTime = LocalDateTime.parse(startTime, FORMATTER);
        LocalDateTime endLocalTime = LocalDateTime.parse(endTime, FORMATTER);
        LocalDateTime parseLocalTime = LocalDateTime.parse(DateUtils.formatDataToString(parseTime, DateUtils.DEFAULT_FMT), FORMATTER);
        //计算时间间隔
        Duration between = Duration.between(startLocalTime, endLocalTime);
        // 向前推一个时间段
        LocalDateTime earlierParseLocalTime = parseLocalTime.minus(between);
        ZonedDateTime zdt = earlierParseLocalTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 排名分布 (y轴数必须和系数长度对应).
     *
     * @param websiteName
     * @return
     */
    public static JSONObject buildRankReportWithParam(String reportName, List<String> websiteName, List<BigInteger> parseCnt, List<BigInteger> parseLastCnt) {
        List<String> legend = org.apache.commons.compress.utils.Lists.newArrayList();
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        JSONObject textData = new JSONObject(new LinkedHashMap());
        JSONObject easierParseCnt = new JSONObject(new LinkedHashMap());
        JSONObject nowParseCnt = new JSONObject(new LinkedHashMap());
        JSONObject easierResult = new JSONObject(new LinkedHashMap());
        JSONObject nowResult = new JSONObject(new LinkedHashMap());
        nowResult.put("name", "当前时间段");
        nowResult.put("data", parseCnt);
        nowResult.put("type", "bar");
        easierResult.put("name", "上个时间段");
        easierResult.put("data", parseLastCnt);
        easierResult.put("type", "bar");
        nowParseCnt.put("result", nowResult);
        easierParseCnt.put("result", easierResult);
        textData.put("bar_now", nowParseCnt);
        textData.put("bar_easier", easierParseCnt);
        legend.add("当前时间段");
        legend.add("上个时间段");
        finalReport.put("legend", legend);
        finalReport.put("xAxis", websiteName);
        finalReport.put("name", reportName);
        finalReport.put("data", textData);
        return finalReport;
    }

    public PageResult findTableDetail(SpecificDomainWebsiteDetail domainWebsiteDetail) {
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        Long total = 0L;
        List<SpecificDomainWebsiteDetail> dataList = Lists.newArrayList();
        if (StatisticsWayEnum.ALL.getType().equals(domainWebsiteDetail.getStatisticsWay())) {
            total = specificDomainWebsiteMapper.countQueryDataGroupByWebsiteByParam(domainWebsiteDetail);
            dataList = specificDomainWebsiteMapper.queryDataGroupByWebsiteByParam(domainWebsiteDetail);
            dataList.stream().forEach(websiteDetail -> {
                websiteDetail.setTimeRange(domainWebsiteDetail.getStartTime() + "~" + domainWebsiteDetail.getEndTime());
            });
        } else {
            total = specificDomainWebsiteMapper.countQueryDataGroupByParseTimeAndWebsiteByParam(domainWebsiteDetail);
            dataList = specificDomainWebsiteMapper.queryDataGroupByParseTimeAndWebsiteByParam(domainWebsiteDetail);
            dataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.forEach(SpecificDomainWebsiteDetail::buildRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    /**
     * TopN本网本省率趋势.
     *
     * @param domainWebsiteDetail
     * @return
     */
    public JSONObject findRateReport(SpecificDomainWebsiteDetail domainWebsiteDetail, boolean isTopN) throws ParseException {
        checkFindRateReportParam(domainWebsiteDetail);
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        List<SpecificDomainWebsiteDetail> dataList = specificDomainWebsiteMapper.queryDataGroupByParseTimeByParam(domainWebsiteDetail,isTopN);
        dataList.stream().forEach(SpecificDomainWebsiteDetail::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainWebsiteDetail.getStartTime(), domainWebsiteDetail.getEndTime(), domainWebsiteDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (SpecificDomainWebsiteDetail domainNameWebsiteDetail1 : dataList) {
                netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                parseInRateResult.add(domainNameWebsiteDetail1.getParseInRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, SpecificDomainWebsiteDetail> collect = dataList.stream().collect(Collectors.toMap(SpecificDomainWebsiteDetail::getParseTime, SpecificDomainWebsiteDetail -> SpecificDomainWebsiteDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                SpecificDomainWebsiteDetail domainNameWebsiteDetail1 = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainNameWebsiteDetail1)) {
                    netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                    parseInRateResult.add(domainNameWebsiteDetail1.getParseInRate());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.forEach(xAxisMap::remove);
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
        String reportName = "";
        if (isTopN) {
            reportName = "TopN网站本网率、本省率趋势";
        } else {
            reportName = "本网率、本省率趋势";
        }
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    private void checkFindRateReportParam(SpecificDomainWebsiteDetail domainWebsiteDetail) throws ParseException {
        if (StrUtil.isNotEmpty(domainWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainWebsiteDetail.getQueryType(), domainWebsiteDetail.getQueryTime());
            domainWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainWebsiteDetail.getQueryType()));
        } else {
            domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        }
    }

    public JSONObject findResourceReport(SpecificDomainWebsiteDetail domainWebsiteDetail) throws ParseException {
        checkFindResourceReportParam(domainWebsiteDetail);
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        List<SpecificDomainWebsiteDetail> dataList = specificDomainWebsiteMapper.findTrendReportGroupByIspByParam(domainWebsiteDetail);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (SpecificDomainWebsiteDetail param : dataList) {
            resultDataMap.put(param.getIsp(), param.getARecordParseTotalCnt());
        }
        //BigInteger otherParseCnt = BigInteger.ZERO;

       //otherParseCnt = resourceReportCountIspParseCntAndOther(dataList, resultDataMap, otherParseCnt);

        //resourceReportAddOtherData(resultDataMap, otherParseCnt);
        List<String> xAxisList = new ArrayList<>(resultDataMap.keySet());
        List<BigInteger> totalList = new ArrayList<>(resultDataMap.values());
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "解析次数");
        // 报表名称
        String reportName = "资源分布";
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

    private BigInteger resourceReportCountIspParseCntAndOther(List<SpecificDomainWebsiteDetail> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (SpecificDomainWebsiteDetail param : dataList) {
            if (index >= DEFAULT_ISP_NUMBER) {
                if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                    resultDataMap.put("其他", param.getARecordParseTotalCnt());
                } else {
                    resultDataMap.put("其他", resultDataMap.get("其他").add(param.getARecordParseTotalCnt()));
                }
            } else {
                if (param.getIsp().equals("其他")) {
                    otherParseCnt = otherParseCnt.add(param.getARecordParseTotalCnt());
                    continue;
                }
                resultDataMap.put(param.getIsp(), param.getARecordParseTotalCnt());
                index++;
            }
        }
        return otherParseCnt;
    }

    private void checkFindResourceReportParam(SpecificDomainWebsiteDetail domainWebsiteDetail) throws ParseException {
        if (StrUtil.isNotEmpty(domainWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainWebsiteDetail.getQueryType(), domainWebsiteDetail.getQueryTime());
            domainWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainWebsiteDetail.getQueryType()));
        } else {
            domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        }
    }

    public JSONObject findParseReport(SpecificDomainWebsiteDetail domainWebsiteDetail) throws ParseException {
        checkFindParseReport(domainWebsiteDetail);
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        List<SpecificDomainWebsiteDetail> dataList = specificDomainWebsiteMapper.queryDataGroupByParseTimeByParam(domainWebsiteDetail,false);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainWebsiteDetail.getStartTime(), domainWebsiteDetail.getEndTime(), domainWebsiteDetail.getQueryType());
        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (SpecificDomainWebsiteDetail websiteDetail : dataList) {
                CDNParseResult.add(websiteDetail.getCdnParseTotalCnt());
                IDCParseResult.add(websiteDetail.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, SpecificDomainWebsiteDetail> collect = dataList.stream().collect(Collectors.toMap(SpecificDomainWebsiteDetail::getParseTime, SpecificDomainWebsiteDetail -> SpecificDomainWebsiteDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                SpecificDomainWebsiteDetail websiteDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(websiteDetail)) {
                    CDNParseResult.add(websiteDetail.getCdnParseTotalCnt());
                    IDCParseResult.add(websiteDetail.getIdcParseTotalCnt());
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
        String reportName = "CDN、IDC调度趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    private void checkFindParseReport(SpecificDomainWebsiteDetail domainWebsiteDetail) throws ParseException {
        if (StrUtil.isNotEmpty(domainWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainWebsiteDetail.getQueryType(), domainWebsiteDetail.getQueryTime());
            domainWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainWebsiteDetail.getQueryType()));
        }
    }

    public List<SpecificDomainWebsiteDetail> downloadByParam(SpecificDomainWebsiteDetail domainWebsiteDetail) {
        checkDownloadByParamMethodParam(domainWebsiteDetail);
        List<SpecificDomainWebsiteDetail> dataList = Lists.newArrayList();
        if (StatisticsWayEnum.ALL.getType().equals(domainWebsiteDetail.getStatisticsWay())) {
            dataList = specificDomainWebsiteMapper.queryDataGroupByWebsiteByParam(domainWebsiteDetail);
            dataList.stream().forEach(websiteDetail -> {
                websiteDetail.setTimeRange(domainWebsiteDetail.getStartTime() + "~" + domainWebsiteDetail.getEndTime());
            });
        } else {
            dataList = specificDomainWebsiteMapper.queryDataGroupByParseTimeAndWebsiteByParam(domainWebsiteDetail);
            dataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(SpecificDomainWebsiteDetail domainWebsiteDetail) {
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        domainWebsiteDetail.setLimit(10000L);
        domainWebsiteDetail.setOffset(0L);
    }

    private void setUnknownLast(List<SpecificDomainWebsiteDetail> dataList){
        for (SpecificDomainWebsiteDetail specificDomainWebsiteDetail : dataList) {
            if("未知".equals(specificDomainWebsiteDetail.getIsp())){
                SpecificDomainWebsiteDetail data = specificDomainWebsiteDetail;
                dataList.remove(specificDomainWebsiteDetail);
                dataList.add(data);
                break;
            }
        }
    }
}
