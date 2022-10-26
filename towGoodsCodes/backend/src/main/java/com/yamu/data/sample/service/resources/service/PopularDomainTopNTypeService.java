package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.entity.ConstantEntity;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.CombinedXAxisBO;
import com.yamu.data.sample.service.resources.entity.bo.NetOutTopNTypeDetailDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.NetOutTopNTypeDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteTopNTypeDetailListDownloadBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceTopNTypeUserSourceVO;
import com.yamu.data.sample.service.resources.mapper.PopularDomainTopNTypeMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/23
 * @DESC
 */

@Service
@Slf4j
public class PopularDomainTopNTypeService {
    @Autowired
    private PopularDomainTopNTypeMapper popularDomainTopNTypeMapper;


    public JSONObject findRankNumber(PopularDomainTopNType domainNameWebsiteDetail) {
        //1.check参数2.查询参数，统计3.处理报表
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        Map<String, String> easierTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime());
        List<String> domainTypeList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> nowParseTotalList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> easierParseTotalList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<PopularDomainTopNType> easierDataList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<PopularDomainTopNType> nowDataList = org.apache.commons.compress.utils.Lists.newArrayList();
        // 处理报表，查询数据
        if(ObjectUtil.equals(domainNameWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            nowDataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            domainTypeList = nowDataList.stream().map(PopularDomainTopNType :: getDomainType).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(domainTypeList)) {
                domainNameWebsiteDetail.setStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                domainNameWebsiteDetail.setEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                easierDataList = popularDomainTopNTypeMapper.queryLastTimeDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail, domainTypeList);
                Map<String, BigInteger> nowDataMap = nowDataList.stream().collect(Collectors.toMap(PopularDomainTopNType::getDomainType, PopularDomainTopNType::getParseTotalCnt));
                Map<String, BigInteger> easierDataMap = easierDataList.stream().collect(Collectors.toMap(PopularDomainTopNType::getDomainType, PopularDomainTopNType::getParseTotalCnt));
                //是否补点
                for (String domainType : domainTypeList) {
                    nowParseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(domainType)) ? BigInteger.ZERO : nowDataMap.get(domainType));
                    easierParseTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(domainType)) ? BigInteger.ZERO : easierDataMap.get(domainType));
                }
            }
        } else {
            nowDataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            domainTypeList = nowDataList.stream().map(PopularDomainTopNType :: getDomainType).collect(Collectors.toList());
            if(CollUtil.isNotEmpty(domainTypeList)) {
                List<String> websiteTypes = domainTypeList.stream().distinct().collect(Collectors.toList());
                domainNameWebsiteDetail.setStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                domainNameWebsiteDetail.setEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                easierDataList = popularDomainTopNTypeMapper.queryLastTimeDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail, websiteTypes);
                Map<CombinedXAxisBO, BigInteger> nowDataMap = nowDataList.stream().collect(Collectors.toMap(domainNameWebsite -> new CombinedXAxisBO(buildYOYTimePointParamByStartTimeAndEndTime(domainNameWebsite.getParseTime(), domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime()), domainNameWebsite.getDomainType()), PopularDomainTopNType::getParseTotalCnt));
                Map<CombinedXAxisBO, BigInteger> easierDataMap = easierDataList.stream().collect(Collectors.toMap(domainNameWebsite -> new CombinedXAxisBO(domainNameWebsite.getParseTime(), domainNameWebsite.getDomainType()), PopularDomainTopNType::getParseTotalCnt));
                for (PopularDomainTopNType websiteDetail : nowDataList) {
                    CombinedXAxisBO combinedXAxisBO = new CombinedXAxisBO(buildYOYTimePointParamByStartTimeAndEndTime(websiteDetail.getParseTime(), domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime()), websiteDetail.getDomainType());
                    nowParseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(combinedXAxisBO)) ? BigInteger.ZERO : nowDataMap.get(combinedXAxisBO));
                    easierParseTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(combinedXAxisBO)) ? BigInteger.ZERO : easierDataMap.get(combinedXAxisBO));
                }
            }
        }
        JSONObject finalResult = buildRankReportWithParam("TopN分类排名", domainTypeList, nowParseTotalList, easierParseTotalList);
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
     * 排名分布 (y轴数必须和系数长度对应)
     */
    public static JSONObject buildRankReportWithParam(String reportName, List<String> websiteName, List<BigInteger> parseCnt, List<BigInteger> parseLastCnt) {
        List<String> legend = org.apache.commons.compress.utils.Lists.newArrayList();
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        JSONObject textData = new JSONObject(new LinkedHashMap());
        JSONObject easierParseCnt = new JSONObject(new LinkedHashMap());
        JSONObject nowParseCnt = new JSONObject(new LinkedHashMap());
        JSONObject easierResult = new JSONObject(new LinkedHashMap());
        JSONObject nowResult = new JSONObject(new LinkedHashMap());
        nowResult.put("name","当前时间段");
        nowResult.put("data",parseCnt);
        nowResult.put("type","bar");
        easierResult.put("name","上个时间段");
        easierResult.put("data",parseLastCnt);
        easierResult.put("type","bar");
        nowParseCnt.put("result",nowResult);
        easierParseCnt.put("result",easierResult);
        textData.put("bar_now",nowParseCnt);
        textData.put("bar_easier",easierParseCnt);
        legend.add("当前时间段");
        legend.add("上个时间段");
        finalReport.put("legend",legend);
        finalReport.put("xAxis", websiteName);
        finalReport.put("name", reportName);
        finalReport.put("data", textData);
        return finalReport;
    }

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";


    public PageResult findTableDetail(PopularDomainTopNType domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        Long total = 0L;
        List<PopularDomainTopNType> dataList = Lists.newArrayList();
        if(ObjectUtil.equals(domainNameWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            total = popularDomainTopNTypeMapper.countQueryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            total = popularDomainTopNTypeMapper.countQueryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.stream().forEach(PopularDomainTopNType::buildRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    /**
     * TopN本网本省率趋势.
     *
     * @param domainWebsiteDetail
     * @return
     */
    public JSONObject findRateReport(PopularDomainTopNType domainWebsiteDetail, boolean isTopn) throws ParseException {
        checkFindRateReportParam(domainWebsiteDetail);
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        List<PopularDomainTopNType> dataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeByParam(domainWebsiteDetail);
        dataList.stream().forEach(PopularDomainTopNType::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainWebsiteDetail.getStartTime(), domainWebsiteDetail.getEndTime(), domainWebsiteDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = org.apache.commons.compress.utils.Lists.newArrayList();
        List parseInRateResult = org.apache.commons.compress.utils.Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularDomainTopNType domainNameWebsiteDetail1 : dataList) {
                netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                parseInRateResult.add(domainNameWebsiteDetail1.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = org.apache.commons.compress.utils.Lists.newArrayList();
            Map<Date, PopularDomainTopNType> collect = dataList.stream().collect(Collectors.toMap(PopularDomainTopNType::getParseTime, PopularDomainTopNType -> PopularDomainTopNType));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularDomainTopNType domainNameWebsiteDetail1 = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainNameWebsiteDetail1)) {
                    netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                    parseInRateResult.add(domainNameWebsiteDetail1.getWithinRate());
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
        if (isTopn) {
            reportName = "TopN分类本网率、本省率趋势";
        } else {
            reportName = "本网率、本省率趋势";
        }
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    private void checkFindRateReportParam(PopularDomainTopNType domainTopNType) throws ParseException {
        if (StrUtil.isNotEmpty(domainTopNType.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainTopNType.getQueryType(), domainTopNType.getQueryTime());
            domainTopNType.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainTopNType.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainTopNType.setQueryType(ReportUtils.queryTypeDowngrade(domainTopNType.getQueryType()));
        } else {
            domainTopNType.formatParseTime(domainTopNType.getQueryType(), "1d");
        }
    }

    public JSONObject findResourceReport(PopularDomainTopNType domainWebsiteDetail) throws ParseException {
//        checkFindResourceReportParam(domainWebsiteDetail);
        domainWebsiteDetail.formatParseTime(domainWebsiteDetail.getQueryType(), "1d");
        List<PopularDomainTopNType> dataList = Lists.newArrayList();
        if(ObjectUtil.equals(domainWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            dataList = popularDomainTopNTypeMapper.findTrendReportGroupByIspByParam(domainWebsiteDetail);
        } else {
            dataList = popularDomainTopNTypeMapper.findTrendReportGroupByIspByParamEvery(domainWebsiteDetail);
        }
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (PopularDomainTopNType detail : dataList) {
            resultDataMap.put(detail.getAnswerFirstIsp(), detail.getARecordParseTotalCnt());
        }
        List<String> xAxisList = resultDataMap.keySet().stream().collect(Collectors.toList());
        List<BigInteger> totalList = resultDataMap.values().stream().collect(Collectors.toList());
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

    private void checkFindResourceReportParam(PopularDomainTopNType domainTopNType) throws ParseException {
        if(StrUtil.isNotEmpty(domainTopNType.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainTopNType.getQueryType(), domainTopNType.getQueryTime());
            domainTopNType.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainTopNType.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainTopNType.setQueryType(ReportUtils.queryTypeDowngrade(domainTopNType.getQueryType()));
        } else {
            domainTopNType.formatParseTime(domainTopNType.getQueryType(), "1d");
        }
    }

    public JSONObject findParseReport(PopularDomainTopNType domainNameWebsiteDetail) throws ParseException {
        checkFindParseReport(domainNameWebsiteDetail);
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        List<PopularDomainTopNType> dataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeByParam(domainNameWebsiteDetail);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), domainNameWebsiteDetail.getQueryType());
        //总数结果集,成功结果集
        List CDNParseResult = org.apache.commons.compress.utils.Lists.newArrayList();
        List IDCParseResult = org.apache.commons.compress.utils.Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularDomainTopNType domainWebsiteDetail : dataList) {
                CDNParseResult.add(domainWebsiteDetail.getCdnParseTotalCnt());
                IDCParseResult.add(domainWebsiteDetail.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = org.apache.commons.compress.utils.Lists.newArrayList();
            Map<Date, PopularDomainTopNType> collect = dataList.stream().collect(Collectors.toMap(PopularDomainTopNType::getParseTime, PopularDomainTopNType -> PopularDomainTopNType));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularDomainTopNType domainWebsiteDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainWebsiteDetail)) {
                    CDNParseResult.add(domainWebsiteDetail.getCdnParseTotalCnt());
                    IDCParseResult.add(domainWebsiteDetail.getIdcParseTotalCnt());
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

    private void checkFindParseReport(PopularDomainTopNType domainTopNType) throws ParseException {
        if(StrUtil.isNotEmpty(domainTopNType.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainTopNType.getQueryType(), domainTopNType.getQueryTime());
            domainTopNType.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainTopNType.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainTopNType.setQueryType(ReportUtils.queryTypeDowngrade(domainTopNType.getQueryType()));
        }
    }

    public List<PopularDomainTopNType> downloadByParam(PopularDomainTopNType domainWebsiteDetail) {
        checkDownloadByParamMethodParam(domainWebsiteDetail);
        List<PopularDomainTopNType> dataList = Lists.newArrayList();
        if(ObjectUtil.equals(domainWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            dataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeByParam(domainWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainWebsiteDetail.getStartTime() + "~" + domainWebsiteDetail.getEndTime());
            });
        } else {
            dataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainWebsiteDetail);
            dataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(PopularDomainTopNType domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        domainNameWebsiteDetail.setLimit(10000L);
        domainNameWebsiteDetail.setOffset(0L);
    }

    public JSONObject findNetInParseReport(PopularDomainTopNType domainNameWebsiteDetail) {
        checkFindNetInParseReportMethodParam(domainNameWebsiteDetail);
        List<PopularDomainTopNType> dataList = popularDomainTopNTypeMapper.queryNetInParseGroupByDomainType(domainNameWebsiteDetail);
        List<Date> parseDataList = dataList.stream().map(PopularDomainTopNType::getParseTime).collect(Collectors.toList());
        List<String> xAxis = dataList.stream().map(PopularDomainTopNType::getDomainType).collect(Collectors.toList());
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), domainNameWebsiteDetail.getQueryType());
        List<Date> removeDateList = xAxisMap.keySet().stream().filter(date -> !parseDataList.contains(date)).collect(Collectors.toList());
        removeDateList.stream().forEach(data -> xAxisMap.remove(data));
        List parseTotalResult = com.google.common.collect.Lists.newArrayList();
        List netInRateResult = com.google.common.collect.Lists.newArrayList();
        for (PopularDomainTopNType domainTopNType : dataList) {
            parseTotalResult.add(domainTopNType.getParseTotalCnt());
            netInRateResult.add(ReportUtils.buildRatioBase(domainTopNType.getNetInParseTotalCnt(), domainTopNType.getARecordParseTotalCnt()));
        }
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put("netInRate", netInRateResult);
        dataMap.put("parseTotal", parseTotalResult);
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put("parseTotal", "解析次数");
        legend.put("netInRate", "本网率");
        // 报表名称
        String reportName = "热点分类-TopN";
        Map<String, Map<String, Object>> paramMap = Maps.newHashMap();
        ReportUtils.putToReportMap(paramMap, "parseTotal", "type", "bar", "yAxisIndex", 0);
        ReportUtils.putToReportMap(paramMap, "netInRate", "type", "line", "yAxisIndex", 1);
        JSONObject finalResult = ReportUtils.buidReportWithParam(reportName, legend, xAxis, dataMap, paramMap);
        return finalResult;
    }

    private void checkFindNetInParseReportMethodParam(PopularDomainTopNType domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        if(ObjectUtil.isEmpty(domainNameWebsiteDetail.getRankNumber())) {
            domainNameWebsiteDetail.setRankNumber(10L);
        }
    }

    public JSONObject findOperator(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) {
        domainNameWebsiteTopNTypeDetail.formatParseTime(domainNameWebsiteTopNTypeDetail.getQueryType(), "1d");
        List<String> dataList = popularDomainTopNTypeMapper.queryDataGroupByOperatorByParam(domainNameWebsiteTopNTypeDetail);
        JSONObject finalResult = new JSONObject();
        finalResult.put("data",dataList);
        return finalResult;
    }

    public PageResult findOutDomainTable(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) {
        domainNameWebsiteTopNTypeDetail.formatParseTime(domainNameWebsiteTopNTypeDetail.getQueryType(), "1d");
        Long total = popularDomainTopNTypeMapper.countQueryDataGroupByWebsiteTypeDetailByParam(domainNameWebsiteTopNTypeDetail);
        List<PopularDomainTopNTypeDetail> dataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeDetailByParam(domainNameWebsiteTopNTypeDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public PageResult findOutDomainTableDetail(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) {
        domainNameWebsiteTopNTypeDetail.formatParseTime(domainNameWebsiteTopNTypeDetail.getQueryType(), "1d");
        Long total = popularDomainTopNTypeMapper.countQueryDataGroupByOutDomainTableDetailByParam(domainNameWebsiteTopNTypeDetail);
        List<PopularDomainTopNTypeDetail> dataList = popularDomainTopNTypeMapper.queryDataGroupByOutDomainTableDetailByParam(domainNameWebsiteTopNTypeDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public JSONObject findCdnReport(PopularDomainTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        List<PopularDomainCdnReport> dataList = popularDomainTopNTypeMapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        resourceWebsiteTopNCdnBusinessDetail.setQueryType(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));

        DecimalFormat format = new DecimalFormat("#.00");

        Map<String, ResourceWebsiteCdnReport> xAxisMap = new LinkedHashMap<>();

        //只允许展示15条cdn厂商，剩下的按照其他计算
        if (dataList.size()>15){
            for (int i = 0; i < 15; i++) {
                dataList.get(i).setCdnResponseProportion((double)Math.round(dataList.get(i).getCdnResponseProportion()/100*100)/100);
                xAxisMap.put(dataList.get(i).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(i).getCdnBusiness(),
                                dataList.get(i).getCdnResponseCnt(),
                                dataList.get(i).getCdnResponseProportion()));
            }
            BigInteger tmpCdnResponseCnt = new BigInteger("0") ;
            BigInteger tmpCdnResponseProportion =new BigInteger("0") ;
            for (int i = 15; i <dataList.size(); i++) {
                tmpCdnResponseCnt=tmpCdnResponseCnt.add(dataList.get(i).getCdnResponseCnt());
//                tmpCdnResponseProportion=tmpCdnResponseProportion.add(dataList.get(i).getCdnParseTotalCnt());
            }
//            System.out.println(tmpCdnResponseCnt+"cdn解析次数 "+dataList.get(0).getCdnParseTotalCnt()+"cdn总的解析次数");
            xAxisMap.put("其他",new ResourceWebsiteCdnReport("其他",tmpCdnResponseCnt,(double)Math.round(Double.parseDouble(tmpCdnResponseCnt.divide(dataList.get(0).getCdnParseTotalCnt()).toString())*100)/100 ));
        }
        else {
            for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
                resourceWebsiteCdnReport.setCdnResponseProportion((double)Math.round(resourceWebsiteCdnReport.getCdnResponseProportion()/100*100)/100);
                xAxisMap.put(resourceWebsiteCdnReport.getCdnBusiness(),
                        new ResourceWebsiteCdnReport(resourceWebsiteCdnReport.getCdnBusiness(),
                                resourceWebsiteCdnReport.getCdnResponseCnt(),
                                resourceWebsiteCdnReport.getCdnResponseProportion()));
            }
        }

        //总数结果集,成功结果集
        List totalResult = com.google.common.collect.Lists.newArrayList();
        List growthResult = com.google.common.collect.Lists.newArrayList();
//        if (xAxisMap.size() == dataList.size()) {
        for (Map.Entry<String, ResourceWebsiteCdnReport> stringResourceWebsiteCdnReportEntry : xAxisMap.entrySet()) {
            totalResult.add(stringResourceWebsiteCdnReportEntry.getValue().getCdnResponseCnt());
            growthResult.add(stringResourceWebsiteCdnReportEntry.getValue().getCdnResponseProportion());
        }

        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put("percent", growthResult);
        dataMap.put("total", totalResult);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put("total", "应答次数");
        legend.put("percent", "CDN应答占比");
        // 报表名称
        String reportName = "CDN厂商";
        List<String> xAxis = new ArrayList<>(xAxisMap.keySet());

        Map<String, Map<String, Object>> paramMap = Maps.newHashMap();
        ReportUtils.putToReportMap(paramMap, "total", "type", "bar", "yAxisIndex", 0);
        ReportUtils.putToReportMap(paramMap, "percent", "type", "line", "yAxisIndex", 1);

        JSONObject finalResult = ReportUtils.buidReportWithParam(reportName, legend, xAxis, dataMap, paramMap);
        return finalResult;
    }

    public PageResult findCdnDetail(PopularDomainTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){

        List<PopularDomainCdnReport> dataList = popularDomainTopNTypeMapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        List<PopularDomainCdnReportDetail> resourceWebsiteCdnReportDetails = new ArrayList<>();
        for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
            resourceWebsiteCdnReportDetails.add(toChange(resourceWebsiteCdnReport));
        }
        resourceWebsiteTopNCdnBusinessDetail.setQueryType(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
//        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getQueryType());
//        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getQueryTable());
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
//        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getAQueryTable());
        Long total = popularDomainTopNTypeMapper.countCdnReportlList(resourceWebsiteTopNCdnBusinessDetail);

        PageResult pageResult = new PageResult(total, resourceWebsiteCdnReportDetails);

        return pageResult;
    }


    public void download(PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail,PopularDomainTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail, HttpServletResponse response) throws IOException, YamuException {
        PopularDomainTopNType domainNameWebsiteDetail = BeanUtil.copyProperties(domainNameWebsiteTopNTypeDetail, PopularDomainTopNType.class, "queryTable");
        checkDownloadParam(domainNameWebsiteDetail, domainNameWebsiteTopNTypeDetail);
        // 网站表格导出
        List<WebsiteTopNTypeDetailListDownloadBO> topNDownloadBOList = Lists.newArrayList();
        List<NetOutTopNTypeDownloadBO> topNNetOutBOList = Lists.newArrayList();
        List<NetOutTopNTypeDetailDownloadBO> netOutDetailBOList = Lists.newArrayList();
        List<PopularDomainTopNType> websiteTopNDataList = Lists.newArrayList();
        List<PopularDomainCdnReportDetail> resourceWebsiteCdnReportDetails = new ArrayList<>();

        List<PopularDomainCdnReport> dataList = popularDomainTopNTypeMapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
            resourceWebsiteCdnReportDetails.add(toChange(resourceWebsiteCdnReport));
        }

        if(ObjectUtil.equals(domainNameWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            websiteTopNDataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            websiteTopNDataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            websiteTopNDataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            websiteTopNDataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        websiteTopNDataList.stream().forEach(PopularDomainTopNType::buildRate);
        websiteTopNDataList.stream().forEach(topNDetail -> {
            WebsiteTopNTypeDetailListDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteTopNTypeDetailListDownloadBO.class);
            topNDownloadBO.buildRate();
            topNDownloadBOList.add(topNDownloadBO);
        });
        if(CollUtil.isNotEmpty(websiteTopNDataList)) {
            // 查找出网率数据
            List<PopularDomainTopNTypeDetail> netOutDataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeDetailByParamExcel(domainNameWebsiteTopNTypeDetail);
            netOutDataList.stream().forEach(topNDetail -> {
                NetOutTopNTypeDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, NetOutTopNTypeDownloadBO.class);
                topNNetOutBOList.add(topNDownloadBO);
            });
            if(CollUtil.isNotEmpty(netOutDataList)) {
                // 查找出网率详细数据
                List<PopularDomainTopNTypeDetail> netOutDetailDataList = popularDomainTopNTypeMapper.queryDataGroupByOutDomainTableDetailByParamExcel(domainNameWebsiteTopNTypeDetail);
                netOutDetailDataList.stream().forEach(topNDetail -> {
                    NetOutTopNTypeDetailDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, NetOutTopNTypeDetailDownloadBO.class);
                    netOutDetailBOList.add(topNDownloadBO);
                });
            }
        }

        ResourceTopNTypeUserSourceVO resourceTopNTypeUserSourceVO = BeanUtil.copyProperties(domainNameWebsiteTopNTypeDetail, ResourceTopNTypeUserSourceVO.class, "queryTable");
        List<ResourceWebsiteUserSource> userSourceList = popularDomainTopNTypeMapper.findUserSource(resourceTopNTypeUserSourceVO);

        String timeInterval = domainNameWebsiteTopNTypeDetail.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + domainNameWebsiteTopNTypeDetail.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "TopN分类解析明细报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.setHeaderAlias(getWebsiteTopNHeaderAlias());
        writer.renameSheet("TopN分类解析明细报表");
        writer.setOnlyAlias(true);
        writer.write(topNDownloadBOList, true);

        writer.setHeaderAlias(getWebsiteNetOutHeaderAlias());
        writer.setSheet("TopN分类出网域名数据报表");
        writer.write(topNNetOutBOList, true);

        writer.setHeaderAlias(getNetOutDetailHeaderAlias());
        writer.setSheet("TopN分类出网域名明细报表");
        writer.write(netOutDetailBOList, true);

        writer.setHeaderAlias(getUserSourceHeaderAlias());
        writer.setSheet("TopN网站来源用户分布");
        writer.write(userSourceList, true);

        writer.setHeaderAlias(getCdnReportDetailHeaderAlias());
        writer.setOnlyAlias(true);
        writer.setSheet("CDN厂商");
        writer.write(resourceWebsiteCdnReportDetails, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    public PopularDomainCdnReportDetail toChange(PopularDomainCdnReport resourceWebsiteCdnReport){
        PopularDomainCdnReportDetail resourceWebsiteCdnReportDetail = new PopularDomainCdnReportDetail();

        resourceWebsiteCdnReportDetail.setCdnBusiness(resourceWebsiteCdnReport.getCdnBusiness());
        resourceWebsiteCdnReportDetail.setCdnResponseCnt(resourceWebsiteCdnReport.getCdnResponseCnt());
        resourceWebsiteCdnReportDetail.setResponseProportion(resourceWebsiteCdnReport.getResponseProportion().toString()+"%");
        resourceWebsiteCdnReportDetail.setCdnResponseProportion(resourceWebsiteCdnReport.getCdnResponseProportion().toString()+"%");
        resourceWebsiteCdnReportDetail.setCdnResponseProportion(resourceWebsiteCdnReport.getCdnResponseProportion().toString()+"%");
        resourceWebsiteCdnReportDetail.setSuccessCnt(resourceWebsiteCdnReport.getSuccessCnt());
        resourceWebsiteCdnReportDetail.setSuccessRate(resourceWebsiteCdnReport.getSuccessRate().toString()+"%");
        resourceWebsiteCdnReportDetail.setNetInCnt(resourceWebsiteCdnReport.getNetInCnt());
        resourceWebsiteCdnReportDetail.setNetInRate(resourceWebsiteCdnReport.getNetInRate().toString()+"%");
        resourceWebsiteCdnReportDetail.setWithinCnt(resourceWebsiteCdnReport.getWithinCnt());
        resourceWebsiteCdnReportDetail.setWithinRate(resourceWebsiteCdnReport.getWithinRate().toString()+"%");
        return resourceWebsiteCdnReportDetail ;
    }

    public JSONObject findUserSource(ResourceTopNTypeUserSourceVO resourceTopNTypeUserSourceVO){
        List<ResourceWebsiteUserSource> dataList = popularDomainTopNTypeMapper.findUserSource(resourceTopNTypeUserSourceVO);
        for (ResourceWebsiteUserSource resourceWebsiteUserSource : dataList) {
            if("未知".equals(resourceWebsiteUserSource.getAnswerFirstCity())){
                ResourceWebsiteUserSource data = resourceWebsiteUserSource;
                dataList.remove(resourceWebsiteUserSource);
                dataList.add(data);
                break;
            }
        }
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (ResourceWebsiteUserSource resourceWebsiteUserSource : dataList) {
            resultDataMap.put(resourceWebsiteUserSource.getAnswerFirstCity(), resourceWebsiteUserSource.getParseTotalCnt());
        }
        List<String> xAxisList = new ArrayList<>(resultDataMap.keySet());
        List<BigInteger> totalList = new ArrayList<>(resultDataMap.values());
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "用户数");
        // 报表名称
        String reportName = "来源用户分布";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxisList, dataMap);
        return finalResult;
    }

    private void checkDownloadParam(PopularDomainTopNType domainNameWebsiteDetail, PopularDomainTopNTypeDetail domainNameWebsiteTopNTypeDetail) throws YamuException {
        if (StrUtil.isEmpty(domainNameWebsiteDetail.getStartTime()) || StrUtil.isEmpty(domainNameWebsiteDetail.getEndTime())) {
            domainNameWebsiteDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        if (StrUtil.isEmpty(domainNameWebsiteTopNTypeDetail.getStartTime()) || StrUtil.isEmpty(domainNameWebsiteTopNTypeDetail.getEndTime())) {
            domainNameWebsiteTopNTypeDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        ValidationResult websiteTopNDetailResult = ValidationUtils.validateEntity(domainNameWebsiteDetail);
        if(websiteTopNDetailResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkDownloadParam method. param check error: " + websiteTopNDetailResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(websiteTopNDetailResult.getErrorMsg().values().stream().findFirst().get());
        }

        ValidationResult websiteTopNNetOutDetailResult = ValidationUtils.validateEntity(domainNameWebsiteDetail);
        if(websiteTopNNetOutDetailResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkDownloadByParamMethodParam method. param check error: " + websiteTopNNetOutDetailResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(websiteTopNNetOutDetailResult.getErrorMsg().values().stream().findFirst().get());
        }

        domainNameWebsiteTopNTypeDetail.setLimit(10000L);
        domainNameWebsiteTopNTypeDetail.setOffset(0L);

        domainNameWebsiteDetail.setLimit(10000L);
        domainNameWebsiteDetail.setOffset(0L);
    }

    private Map<String, String> getCdnReportDetailHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("cdnBusiness", "CDN厂商");
        aliasMapResult.put("cdnResponseCnt", "CDN应答次数");
        aliasMapResult.put("responseProportion", "应答占比");
        aliasMapResult.put("cdnResponseProportion", "CDN应答占比");
        aliasMapResult.put("successCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netInCnt", "本网次数");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("withinCnt", "本省次数");
        aliasMapResult.put("withinRate", "本省率");
        return aliasMapResult;
    }

    private Map<String, String> getWebsiteTopNHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("timeRange", "时间");
        aliasMapResult.put("domainType", "域名类型");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("icpRate", "ICP调度准确率");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        aliasMapResult.put("netOutRate", "出网率");
        aliasMapResult.put("netInParseTotalCnt", "网内次数(IPv4)");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("withinParseTotalCnt", "本省次数");
        aliasMapResult.put("withinRate", "本省率");
        aliasMapResult.put("withoutParseTotalCnt", "外省次数");
        aliasMapResult.put("withoutRate", "出省率");
        aliasMapResult.put("cdnParseTotalCnt", "CDN次数");
        aliasMapResult.put("idcParseTotalCnt", "IDC次数");
        return aliasMapResult;
    }

    private Map<String, String> getWebsiteNetOutHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("answerFirstIsp", "出网运营商");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数");
        return aliasMapResult;
    }

    private Map<String, String> getNetOutDetailHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("answerFirstIp", "服务ip");
        aliasMapResult.put("parseTotalCnt", "请求次数");
        aliasMapResult.put("answerFirstProvince", "省份");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("answerFirstIsp", "运营商");
        return aliasMapResult;
    }

    private Map<String, String> getUserSourceHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("websiteAppName", "域名类型");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        return aliasMapResult;
    }

    private void setUnknownLast(List<PopularDomainTopNType> dataList){
        for (PopularDomainTopNType popularDomainTopNType : dataList) {
            if("未知".equals(popularDomainTopNType.getAnswerFirstIsp())){
                PopularDomainTopNType data = popularDomainTopNType;
                dataList.remove(popularDomainTopNType);
                dataList.add(data);
                break;
            }
        }
    }

}
