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
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.CombinedXAxisBO;
import com.yamu.data.sample.service.resources.entity.bo.NetOutTopNTypeDetailDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.NetOutTopNTypeDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.SpecificDomainTopNTypeDetailListDownloadBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteUserSource;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainNameWebsiteDetail;
import com.yamu.data.sample.service.resources.entity.po.SpecificDomainWebsiteNetOutDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificTopNTypeUserSourceVO;
import com.yamu.data.sample.service.resources.mapper.SpecificDomainNameWebsiteMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author getiejun
 * @date 2021/7/21
 */
@Slf4j
@Service
public class SpecificDomainNameWebsiteService {

    @Autowired
    private SpecificDomainNameWebsiteMapper specificDomainNameWebsiteMapper;

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    public JSONObject findRankNumber(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) {
        //1.check参数2.查询参数，统计3.处理报表
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        Map<String, String> easierTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime());
        List<String> websiteTypeList = Lists.newArrayList();
        List<BigInteger> nowParseTotalList = Lists.newArrayList();
        List<BigInteger> easierParseTotalList = Lists.newArrayList();
        List<SpecificDomainNameWebsiteDetail> easierDataList = Lists.newArrayList();
        List<SpecificDomainNameWebsiteDetail> nowDataList = Lists.newArrayList();
        // 处理报表，查询数据
        if (domainNameWebsiteDetail.getStatisticsWay().equals(StatisticsWayEnum.ALL.getType())) {
            nowDataList = specificDomainNameWebsiteMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            // 获取当前时间段需要查询的网站分类,去重
            websiteTypeList = nowDataList.stream()
                    .map(SpecificDomainNameWebsiteDetail::getWebsiteType)
                    .collect(Collectors.toList());

            if (CollUtil.isNotEmpty(websiteTypeList)) {
                domainNameWebsiteDetail.setStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                domainNameWebsiteDetail.setEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                easierDataList = specificDomainNameWebsiteMapper.queryLastTimeDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail, websiteTypeList);
                Map<String, BigInteger> nowDataMap = nowDataList.stream().collect(Collectors.toMap(SpecificDomainNameWebsiteDetail::getWebsiteType, SpecificDomainNameWebsiteDetail::getParseTotalCnt));
                Map<String, BigInteger> easierDataMap = easierDataList.stream().collect(Collectors.toMap(SpecificDomainNameWebsiteDetail::getWebsiteType, SpecificDomainNameWebsiteDetail::getParseTotalCnt));
                //是否补点
                for (String websiteType : websiteTypeList) {
                    nowParseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(websiteType)) ? BigInteger.ZERO : nowDataMap.get(websiteType));
                    easierParseTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(websiteType)) ? BigInteger.ZERO : easierDataMap.get(websiteType));
                }
            }
        } else {
            nowDataList = specificDomainNameWebsiteMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            // 获取当前时间段需要查询的网站分类,去重
            websiteTypeList = nowDataList.stream()
                    .map(SpecificDomainNameWebsiteDetail::getWebsiteType)
                    .collect(Collectors.toList());

            if (CollUtil.isNotEmpty(websiteTypeList)) {
                domainNameWebsiteDetail.setStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                domainNameWebsiteDetail.setEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                easierDataList = specificDomainNameWebsiteMapper.queryLastTimeDataGroupByParseTimeAndWebsiteTypeByParam(
                        domainNameWebsiteDetail, websiteTypeList);
                Map<CombinedXAxisBO, BigInteger> nowDataMap = nowDataList.stream().collect(
                        Collectors.toMap(
                                domainNameWebsite -> new CombinedXAxisBO(buildYOYTimePointParamByStartTimeAndEndTime(
                                        domainNameWebsite.getParseTime(),
                                        domainNameWebsiteDetail.getStartTime(),
                                        domainNameWebsiteDetail.getEndTime()),
                                        domainNameWebsite.getWebsiteType()),
                                SpecificDomainNameWebsiteDetail::getParseTotalCnt));

                Map<CombinedXAxisBO, BigInteger> easierDataMap = easierDataList.stream().collect(Collectors.toMap(
                        domainNameWebsite -> new CombinedXAxisBO(
                                domainNameWebsite.getParseTime(),
                                domainNameWebsite.getWebsiteType()),
                        SpecificDomainNameWebsiteDetail::getParseTotalCnt));
                for (SpecificDomainNameWebsiteDetail websiteDetail : nowDataList) {
                    CombinedXAxisBO combinedXAxisBO = new CombinedXAxisBO(buildYOYTimePointParamByStartTimeAndEndTime(
                            websiteDetail.getParseTime(),
                            domainNameWebsiteDetail.getStartTime(),
                            domainNameWebsiteDetail.getEndTime()),
                            websiteDetail.getWebsiteType());

                    nowParseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(combinedXAxisBO)) ? BigInteger.ZERO
                            : nowDataMap.get(combinedXAxisBO));

                    easierParseTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(combinedXAxisBO)) ? BigInteger.ZERO
                            : easierDataMap.get(combinedXAxisBO));
                }
            }
        }
        JSONObject finalResult = buildRankReportWithParam("TopN分类排名", websiteTypeList, nowParseTotalList, easierParseTotalList);
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

    public PageResult findTableDetail(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        Long total = 0L;
        List<SpecificDomainNameWebsiteDetail> dataList = Lists.newArrayList();
        if (domainNameWebsiteDetail.getStatisticsWay().equals(StatisticsWayEnum.ALL.getType())) {
            total = specificDomainNameWebsiteMapper.countQueryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList.forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            total = specificDomainNameWebsiteMapper.countQueryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList.forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.forEach(SpecificDomainNameWebsiteDetail::buildRate);
        return PageResult.buildPageResult(total, dataList);
    }

    public JSONObject findRateReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail, boolean isTopN) throws Exception {
        checkFindRateReportParam(domainNameWebsiteDetail);
        List<SpecificDomainNameWebsiteDetail> dataList;
        if (StatisticsWayEnum.ALL.getType().equals(domainNameWebsiteDetail.getStatisticsWay())) {
            // 按照时间段计算TopN
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByAllParseTimeByParam(domainNameWebsiteDetail);
        } else {
            // 按照时间粒度计算TopN
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByEveryParseTimeByParam(domainNameWebsiteDetail);
        }

        dataList.stream().forEach(SpecificDomainNameWebsiteDetail::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), domainNameWebsiteDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (SpecificDomainNameWebsiteDetail domainNameWebsiteDetail1 : dataList) {
                netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                parseInRateResult.add(domainNameWebsiteDetail1.getParseInRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, SpecificDomainNameWebsiteDetail> collect = dataList.stream().collect(Collectors.toMap(SpecificDomainNameWebsiteDetail::getParseTime, SpecificDomainNameWebsiteDetail -> SpecificDomainNameWebsiteDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                SpecificDomainNameWebsiteDetail domainNameWebsiteDetail1 = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainNameWebsiteDetail1)) {
                    netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                    parseInRateResult.add(domainNameWebsiteDetail1.getParseInRate());
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
        String reportName = "";
        if (isTopN) {
            reportName = "TopN分类本网率、本省率趋势";
        } else {
            reportName = "本网率、本省率趋势";
        }
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    /**
     * 本省本网率参数检查.
     *
     * @param domainNameWebsiteDetail
     * @throws ParseException
     */
    private void checkFindRateReportParam(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        // 按照时间粒度查询时，会传QueryTime参数，按照时间段查询时，不会传，不需要降维
        if (StrUtil.isNotEmpty(domainNameWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainNameWebsiteDetail.getQueryType(), domainNameWebsiteDetail.getQueryTime());
            domainNameWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainNameWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainNameWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainNameWebsiteDetail.getQueryType()));
        } else {
            domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        }
        // 若查询TopN的数据,则将网站类型置空
        if (domainNameWebsiteDetail.getIsTopN()) {
            // 页面无网站分类查询框,当查询TopN时,不设置网站分类
            domainNameWebsiteDetail.setWebsiteType(null);
        }
    }

    public JSONObject findResourceReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        // 根据statisticsWay判断是否是按照时间粒度或时间段查询
        checkFindResourceReportParam(domainNameWebsiteDetail);
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        List<SpecificDomainNameWebsiteDetail> dataList = specificDomainNameWebsiteMapper.findTrendReportGroupByIspByParam(domainNameWebsiteDetail);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (SpecificDomainNameWebsiteDetail domainWebsiteDetail : dataList) {
            resultDataMap.put(domainWebsiteDetail.getIsp(), domainWebsiteDetail.getARecordParseTotalCnt());
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

    private void checkFindResourceReportParam(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        // 按照时间粒度查询时,需要重新设置开始结束时间为queryTime
        if (StrUtil.isNotEmpty(domainNameWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainNameWebsiteDetail.getQueryType(), domainNameWebsiteDetail.getQueryTime());
            domainNameWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainNameWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainNameWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainNameWebsiteDetail.getQueryType()));
        } else {
            domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        }
    }

    public JSONObject findParseReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        checkFindParseReport(domainNameWebsiteDetail);
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        List<SpecificDomainNameWebsiteDetail> dataList;
        if (StatisticsWayEnum.ALL.getType().equals(domainNameWebsiteDetail.getStatisticsWay())) {
            // 按照时间段计算TopN
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByAllParseTimeByParam(domainNameWebsiteDetail);
        } else {
            // 按照时间粒度计算TopN
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByEveryParseTimeByParam(domainNameWebsiteDetail);
        }
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), domainNameWebsiteDetail.getQueryType());
        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (SpecificDomainNameWebsiteDetail domainWebsiteDetail : dataList) {
                CDNParseResult.add(domainWebsiteDetail.getCdnParseTotalCnt());
                IDCParseResult.add(domainWebsiteDetail.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, SpecificDomainNameWebsiteDetail> collect = dataList.stream().collect(Collectors.toMap(SpecificDomainNameWebsiteDetail::getParseTime, SpecificDomainNameWebsiteDetail -> SpecificDomainNameWebsiteDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                SpecificDomainNameWebsiteDetail domainWebsiteDetail = collect.get(xKey);
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

    private void checkFindParseReport(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) throws ParseException {
        if (StrUtil.isNotEmpty(domainNameWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainNameWebsiteDetail.getQueryType(), domainNameWebsiteDetail.getQueryTime());
            domainNameWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainNameWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainNameWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainNameWebsiteDetail.getQueryType()));
        }
    }

    public List<SpecificDomainNameWebsiteDetail> downloadByParam(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) {
        checkDownloadByParamMethodParam(domainNameWebsiteDetail);
        List<SpecificDomainNameWebsiteDetail> dataList = Lists.newArrayList();
        if (domainNameWebsiteDetail.getStatisticsWay().equals(StatisticsWayEnum.ALL.getType())) {
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            dataList = specificDomainNameWebsiteMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        domainNameWebsiteDetail.setLimit(10000L);
        domainNameWebsiteDetail.setOffset(0L);
    }

    public JSONObject findIspOfDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) {
        JSONObject jsonObject = new JSONObject();
        List<String> ispData = specificDomainNameWebsiteMapper.findIspOfDomainNetOut(websiteNetOutDetail);
        jsonObject.put("data", ispData);
        return jsonObject;
    }

    public PageResult findDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) {
        Long total = specificDomainNameWebsiteMapper.countDomainNetOutList(websiteNetOutDetail);
        List<SpecificDomainWebsiteNetOutDetail> dataList= specificDomainNameWebsiteMapper.findDomainNetOutList(websiteNetOutDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public PageResult findDomainNetOutDetail(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) {
        Long total = specificDomainNameWebsiteMapper.countDomainNetOutDetailList(websiteNetOutDetail);
        List<SpecificDomainWebsiteNetOutDetail> dataList= specificDomainNameWebsiteMapper.findDomainNetOutDetailList(websiteNetOutDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public void download(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail, HttpServletResponse response) throws IOException, YamuException {
        SpecificDomainNameWebsiteDetail domainNameWebsiteDetail = BeanUtil.copyProperties(websiteNetOutDetail, SpecificDomainNameWebsiteDetail.class, "queryTable");
        checkDownloadParam(domainNameWebsiteDetail, websiteNetOutDetail);
        // 网站表格导出
        List<SpecificDomainTopNTypeDetailListDownloadBO> topNDownloadBOList = Lists.newArrayList();
        List<NetOutTopNTypeDownloadBO> topNNetOutBOList = Lists.newArrayList();
        List<NetOutTopNTypeDetailDownloadBO> netOutDetailBOList = Lists.newArrayList();
        List<SpecificDomainNameWebsiteDetail> websiteTopNDataList = Lists.newArrayList();
        if (domainNameWebsiteDetail.getStatisticsWay().equals(StatisticsWayEnum.ALL.getType())) {
            websiteTopNDataList = specificDomainNameWebsiteMapper.queryDataGroupByWebsiteTypeByParam(domainNameWebsiteDetail);
            websiteTopNDataList.forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            websiteTopNDataList = specificDomainNameWebsiteMapper.queryDataGroupByParseTimeAndWebsiteTypeByParam(domainNameWebsiteDetail);
            websiteTopNDataList.forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        websiteTopNDataList.forEach(SpecificDomainNameWebsiteDetail::buildRate);
        websiteTopNDataList.stream().forEach(topNDetail -> {
            SpecificDomainTopNTypeDetailListDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, SpecificDomainTopNTypeDetailListDownloadBO.class);
            topNDownloadBO.buildRate();
            topNDownloadBOList.add(topNDownloadBO);
        });
        if(CollUtil.isNotEmpty(websiteTopNDataList)) {
            // 查找出网率数据
            List<SpecificDomainWebsiteNetOutDetail> netOutDataList = specificDomainNameWebsiteMapper.findDomainNetOutList(websiteNetOutDetail);
            netOutDataList.stream().forEach(topNDetail -> {
                NetOutTopNTypeDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, NetOutTopNTypeDownloadBO.class);
                topNNetOutBOList.add(topNDownloadBO);
            });
            if(CollUtil.isNotEmpty(netOutDataList)) {
                // 查找出网率详细数据
                List<SpecificDomainWebsiteNetOutDetail> netOutDetailDataList = specificDomainNameWebsiteMapper.findDomainNetOutDetailList(websiteNetOutDetail);
                netOutDetailDataList.stream().forEach(topNDetail -> {
                    NetOutTopNTypeDetailDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, NetOutTopNTypeDetailDownloadBO.class);
                    netOutDetailBOList.add(topNDownloadBO);
                });
            }
        }

        ResourceSpecificTopNTypeUserSourceVO resourceSpecificTopNTypeUserSourceVO = BeanUtil.copyProperties(websiteNetOutDetail, ResourceSpecificTopNTypeUserSourceVO.class, "queryTable");
        List<ResourceWebsiteUserSource> userSourceList = specificDomainNameWebsiteMapper.findUserSource(resourceSpecificTopNTypeUserSourceVO);

        String timeInterval = websiteNetOutDetail.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + websiteNetOutDetail.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "TopN分类解析明细报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.setHeaderAlias(getWebsiteTopNHeaderAlias());
        writer.renameSheet("TopN分类解析明细报表");
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

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private void checkDownloadParam(SpecificDomainNameWebsiteDetail domainNameWebsiteDetail, SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws YamuException {
        if (StrUtil.isEmpty(domainNameWebsiteDetail.getStartTime()) || StrUtil.isEmpty(domainNameWebsiteDetail.getEndTime())) {
            domainNameWebsiteDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        if (StrUtil.isEmpty(websiteNetOutDetail.getStartTime()) || StrUtil.isEmpty(websiteNetOutDetail.getEndTime())) {
            websiteNetOutDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
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

        websiteNetOutDetail.setLimit(10000L);
        websiteNetOutDetail.setOffset(0L);

        domainNameWebsiteDetail.setLimit(10000L);
        domainNameWebsiteDetail.setOffset(0L);
    }

    private Map<String, String> getWebsiteTopNHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("timeRange", "时间");
        aliasMapResult.put("websiteType", "网站类型");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        aliasMapResult.put("netOutRate", "出网率");
        aliasMapResult.put("netInParseTotalCnt", "网内次数(IPv4)");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("withinParseTotalCnt", "本省次数");
        aliasMapResult.put("parseInRate", "本省率");
        aliasMapResult.put("withoutParseTotalCnt", "外省次数");
        aliasMapResult.put("parseOutRate", "出省率");
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
        aliasMapResult.put("websiteAppName", "网站类型");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        return aliasMapResult;
    }

    public JSONObject findUserSource(ResourceSpecificTopNTypeUserSourceVO resourceSpecificTopNTypeUserSourceVO){
        List<ResourceWebsiteUserSource> dataList = specificDomainNameWebsiteMapper.findUserSource(resourceSpecificTopNTypeUserSourceVO);
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

    private void setUnknownLast(List<SpecificDomainNameWebsiteDetail> dataList){
        for (SpecificDomainNameWebsiteDetail specificDomainNameWebsiteDetail : dataList) {
            if("未知".equals(specificDomainNameWebsiteDetail.getIsp())){
                SpecificDomainNameWebsiteDetail data = specificDomainNameWebsiteDetail;
                dataList.remove(specificDomainNameWebsiteDetail);
                dataList.add(data);
                break;
            }
        }
    }
}
