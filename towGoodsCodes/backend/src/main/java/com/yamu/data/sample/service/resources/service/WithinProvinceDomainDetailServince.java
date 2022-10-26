package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.collection.CollUtil;
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
import com.yamu.data.sample.service.resources.entity.po.WithinProvinceDomainDetail;
import com.yamu.data.sample.service.resources.mapper.WithinProvinceDomainDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author dongyuyuan
 * Date 2020-07-1
 */
@Service
@Slf4j
public class WithinProvinceDomainDetailServince {

    @Autowired
    private WithinProvinceDomainDetailMapper provinceDomainDetailMapper;

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    private final int DEFAULT_ISP_NUMBER = 20;

//    private final String DEFAULT_INTERVAL_TYPE = "10min";
//
//    private final String DEFAULT_QUERY_TYPE = "1min";

    /**
     * 日期格式化.
     */
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JSONObject findWithinProvinceDomainRank(WithinProvinceDomainDetail provinceDomain) throws YamuException {
        WithinProvinceDomainDetail withinProvinceDomain = checkWithinProvinceDomainRankParam(provinceDomain);
        List<String> domainList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> parseTotalList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> parseLastTotalList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(provinceDomain.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            Map<String, String> easierTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(provinceDomain.getStartTime(), provinceDomain.getEndTime());
            List<WithinProvinceDomainDetail> resultList = provinceDomainDetailMapper.findNowTrendListByParamAll(withinProvinceDomain);
            resultList.stream().forEach(resultListDemo -> {
                domainList.add(resultListDemo.getDomainName());
            });
            Map<String, BigInteger> nowDataMap = resultList.stream()
                    .collect(Collectors.toMap(
                            WithinProvinceDomainDetail::getDomainName,
                            WithinProvinceDomainDetail::getParseTotalCnt));
            if (CollUtil.isNotEmpty(resultList)) {
                withinProvinceDomain.setLastStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                withinProvinceDomain.setLastEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                List<WithinProvinceDomainDetail> lastResultList = provinceDomainDetailMapper.findLastTrendListByParamAll(withinProvinceDomain, domainList);
                Map<String, BigInteger> easierDataMap = lastResultList.stream()
                        .collect(Collectors.toMap(
                                WithinProvinceDomainDetail::getDomainName,
                                WithinProvinceDomainDetail::getParseTotalCnt));
                //是否补点
                for (String website : domainList) {
                    parseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(website)) ? BigInteger.ZERO : nowDataMap.get(website));
                    parseLastTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(website)) ? BigInteger.ZERO : easierDataMap.get(website));
                }
            }
        }else{
            List<WithinProvinceDomainDetail> resultList = provinceDomainDetailMapper.findNowTrendListByParam(withinProvinceDomain);
            //上一个时间段
            String startTimeStr = withinProvinceDomain.getStartTime();
            String endTimeStr = withinProvinceDomain.getEndTime();
            //计算当前时间段和环比时间段
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
            //计算时间间隔
            Duration between = Duration.between(endTime, startTime);
            // 环比开始时间(向前推一个时间段)
            LocalDateTime earlierStartTime = startTime.plus(between);
            String earlierStartTimeStr = earlierStartTime.format(FORMATTER);
            // 计算环比时间
            withinProvinceDomain.setLastEndTime(startTimeStr);
            withinProvinceDomain.setLastStartTime(earlierStartTimeStr);
            resultList.stream().forEach(resultListDemo -> {
//            if (ObjectUtil.isEmpty(resultListDemo.getDomainName())){
//                domainList.add("未知");
//            }else {
//                domainList.add(resultListDemo.getDomainName());
//            }
                domainList.add(resultListDemo.getDomainName());
                parseTotalList.add(resultListDemo.getParseTotalCnt());
            });
            List<WithinProvinceDomainDetail> lastResultList = provinceDomainDetailMapper.findLastTrendListByParam(withinProvinceDomain);
            int index = 0;
            for (WithinProvinceDomainDetail result:resultList){
                LocalDateTime localDateTime = LocalDateTime.parse(result.getParseTimeLast(), FORMATTER);
                LocalDateTime dateTime = localDateTime.plus(between);
                String format = dateTime.format(FORMATTER);
                String concat = result.getDomainName().concat(format);
                for (WithinProvinceDomainDetail lastResult:lastResultList){
                    String lastConcat = lastResult.getDomainName().concat(lastResult.getParseTimeLast());
                    if (concat.equals(lastConcat)) {
                        parseLastTotalList.add(lastResult.getParseTotalCnt());
                        index = 0;
                        break;
                    }
                    index++;
                }
                if (index==lastResultList.size()){
                    parseLastTotalList.add(BigInteger.ZERO);
                    index = 0;
                }
            }
        }
        //组装结果
        String reportName = "Top20本省域名排名";
        if (ObjectUtil.isNotEmpty(provinceDomain.getDistrictsCode())){
            reportName = provinceDomain.getProvince()+"-Top20本省域名排名";
        }
        JSONObject finalResult = buildRankReportWithParam(reportName, domainList, parseTotalList, parseLastTotalList);
        return finalResult;
    }


    /**
     * 排名分布 (y轴数必须和系数长度对应)
     */
    public static JSONObject buildRankReportWithParam(String reportName, List<String> domain, List<BigInteger> parseCnt, List<BigInteger> parseLastCnt) {
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
        finalReport.put("xAxis", domain);
        finalReport.put("name", reportName);
        finalReport.put("data", textData);
        return finalReport;
    }

    private WithinProvinceDomainDetail checkWithinProvinceDomainRankParam(WithinProvinceDomainDetail provinceDomain) throws YamuException{
        if (checkTimeParam(provinceDomain)) {
            Date endDate = new Date();
            Date startDate = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.set(Calendar.SECOND, 0);
            endDate = calendar.getTime();
            calendar.add(Calendar.DATE, -1);
            startDate = calendar.getTime();
            SimpleDateFormat fmt = new SimpleDateFormat(ReportUtils.DEFAULT_FMT);
            provinceDomain.setStartTime(fmt.format(startDate));
            provinceDomain.setEndTime(fmt.format(endDate));
        }
        if(ObjectUtil.isEmpty(provinceDomain.getIspCode()) || provinceDomain.getIspCode().equals("0")) {
            provinceDomain.setIspCode(null);
        }
        if(ObjectUtil.isEmpty(provinceDomain.getProvince())) {
            provinceDomain.setProvince(null);
        }
        if(ObjectUtil.isEmpty(provinceDomain.getCity())) {
            provinceDomain.setCity(null);
        }
        if(ObjectUtil.isEmpty(provinceDomain.getRankNumber()) || provinceDomain.getRankNumber().equals(0L)) {
            provinceDomain.setRankNumber(null);
        }
        ValidationResult validationResult = ValidationUtils.validateEntity(provinceDomain);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
        String domainName = provinceDomain.getDomainName();
        provinceDomain.setDomainName(ReportUtils.escapeChar(domainName));
        return provinceDomain;
    }

    private boolean checkTimeParam(WithinProvinceDomainDetail queryParam) {
        boolean flag = false;
        if (StrUtil.isEmpty(queryParam.getStartTime()) || queryParam.getStartTime().equals("")) {
            flag = true;
        }
        return flag;
    }

    public PageResult findTrendList(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException{
        checkFindTrendListParam(provinceDomainDetail);
        Long total = Long.valueOf("0");
        List<WithinProvinceDomainDetail> dataList=org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(provinceDomainDetail.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = provinceDomainDetailMapper.countFindTrendListParamAll(provinceDomainDetail);
            dataList= provinceDomainDetailMapper.findTrendListByParamAll(provinceDomainDetail);
            dataList.stream().forEach(domainDetail ->{
                domainDetail.buildRate();
                domainDetail.setTimeRange(provinceDomainDetail.getStartTime() + "~" + provinceDomainDetail.getEndTime());
            });
        }else{
            total = provinceDomainDetailMapper.countFindTrendListParam(provinceDomainDetail);
            dataList= provinceDomainDetailMapper.findTrendListByParam(provinceDomainDetail);
            dataList.stream().forEach(domainDetail ->{
                domainDetail.buildRate();
//            if (ObjectUtil.isEmpty(domainDetail.getDomainName())){
//                domainDetail.setDomainName("未知");
//            }
                domainDetail.setTimeRange(DateUtils.formatDataToString(domainDetail.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }

        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindTrendListParam(WithinProvinceDomainDetail popularCompanyTrend) throws YamuException{
        popularCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        String domainName = popularCompanyTrend.getDomainName();
        popularCompanyTrend.setDomainName(ReportUtils.escapeChar(domainName));
        ValidationResult validationResult = ValidationUtils.validateEntity(popularCompanyTrend);
        if(validationResult.isHasErrors()) {
            log.error(">>WithinProvinceDomainDetail checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }



    public JSONObject findResourceReport(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException, ParseException {
        checkFindTrendListParam(provinceDomainDetail);
        checkParam(provinceDomainDetail);
        List<WithinProvinceDomainDetail> dataList = provinceDomainDetailMapper.findTrendReportGroupByIspByParam(provinceDomainDetail);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (WithinProvinceDomainDetail companyTrend : dataList) {
            resultDataMap.put(companyTrend.getIsp(), companyTrend.getARecordParseTotalCnt());
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

    private BigInteger resourceReportCountIspParseCntAndOther(List<WithinProvinceDomainDetail> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (WithinProvinceDomainDetail companyTrend : dataList) {
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

    public JSONObject findRateTopNReport(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException, ParseException {
        checkFindTrendListParam(provinceDomainDetail);
        checkParam(provinceDomainDetail);
        List<WithinProvinceDomainDetail> dataList = provinceDomainDetailMapper.findTrendReportGroupByParseByParam(provinceDomainDetail);
        dataList.stream().forEach(WithinProvinceDomainDetail::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(provinceDomainDetail.getStartTime(), provinceDomainDetail.getEndTime(), provinceDomainDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (WithinProvinceDomainDetail companyTrend : dataList) {
                netInRateResult.add(companyTrend.getNetInRate());
                parseInRateResult.add(companyTrend.getParseInRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, WithinProvinceDomainDetail> collect = dataList.stream().collect(Collectors.toMap(WithinProvinceDomainDetail::getParseTime, WithinProvinceDomainDetail -> WithinProvinceDomainDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                WithinProvinceDomainDetail companyDetail = collect.get(xKey);
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
        String reportName = "TopN本省域名本网率、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }


    public JSONObject findRateReport(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException, ParseException {
        checkFindTrendListParam(provinceDomainDetail);
        checkParam(provinceDomainDetail);
        List<WithinProvinceDomainDetail> dataList = provinceDomainDetailMapper.findTrendReportGroupByParseByParam(provinceDomainDetail);
        dataList.stream().forEach(WithinProvinceDomainDetail::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(provinceDomainDetail.getStartTime(), provinceDomainDetail.getEndTime(), provinceDomainDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (WithinProvinceDomainDetail companyTrend : dataList) {
                netInRateResult.add(companyTrend.getNetInRate());
                parseInRateResult.add(companyTrend.getParseInRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, WithinProvinceDomainDetail> collect = dataList.stream().collect(Collectors.toMap(WithinProvinceDomainDetail::getParseTime, WithinProvinceDomainDetail -> WithinProvinceDomainDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                WithinProvinceDomainDetail companyDetail = collect.get(xKey);
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
        String reportName = "本网率、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findParseReport(WithinProvinceDomainDetail provinceDomainDetail) throws YamuException, ParseException {
        checkFindTrendListParam(provinceDomainDetail);
        checkParam(provinceDomainDetail);
        List<WithinProvinceDomainDetail> dataList = provinceDomainDetailMapper.findTrendReportGroupByParseByParam(provinceDomainDetail);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(provinceDomainDetail.getStartTime(), provinceDomainDetail.getEndTime(), provinceDomainDetail.getQueryType());

        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (WithinProvinceDomainDetail companyTrend : dataList) {
                CDNParseResult.add(companyTrend.getCdnParseTotalCnt());
                IDCParseResult.add(companyTrend.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, WithinProvinceDomainDetail> collect = dataList.stream().collect(Collectors.toMap(WithinProvinceDomainDetail::getParseTime, WithinProvinceDomainDetail -> WithinProvinceDomainDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                WithinProvinceDomainDetail companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    CDNParseResult.add(companyDetail.getCdnParseTotalCnt());
                    IDCParseResult.add(companyDetail.getIdcParseTotalCnt());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(xAxisMap::remove);
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
        String reportName = "CDN、IDC解析趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public List<WithinProvinceDomainDetail> downloadByParam(WithinProvinceDomainDetail provinceDomainDetail) throws Exception{
        checkDownloadByParamMethodParam(provinceDomainDetail);
        List<WithinProvinceDomainDetail> dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(provinceDomainDetail.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            dataList= provinceDomainDetailMapper.findTrendListByParamAll(provinceDomainDetail);
            dataList.stream().forEach(domainDetail ->{
                domainDetail.buildRate();
                if (ObjectUtil.isEmpty(domainDetail.getDomainName())){
                    domainDetail.setDomainName("未知");
                }
                domainDetail.setTimeRange(provinceDomainDetail.getStartTime() + "~" + provinceDomainDetail.getEndTime());
            });
        }else{
            dataList = provinceDomainDetailMapper.findTrendListByParam(provinceDomainDetail);
            dataList.stream().forEach(domainDetail ->{
                domainDetail.buildRate();
                if (ObjectUtil.isEmpty(domainDetail.getDomainName())){
                    domainDetail.setDomainName("未知");
                }
                domainDetail.setTimeRange(DateUtils.formatDataToString(domainDetail.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(WithinProvinceDomainDetail provinceDomainDetail) throws UnsupportedEncodingException,YamuException {
        if (StrUtil.isEmpty(provinceDomainDetail.getStartTime()) || StrUtil.isEmpty(provinceDomainDetail.getEndTime())) {
            provinceDomainDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
//        if(ObjectUtil.isNotEmpty(provinceDomainDetail.getDomainName())) {
//            provinceDomainDetail.setDomainName(URLDecoder.decode(provinceDomainDetail.getDomainName(), "utf-8"));
//        }
        ValidationResult validationResult = ValidationUtils.validateEntity(provinceDomainDetail);
        if(validationResult.isHasErrors()) {
            log.error(">>WithinProvinceDomainDetail checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
//        String domainName = provinceDomainDetail.getDomainName();
//        provinceDomainDetail.setDomainName(ReportUtils.escapeChar(domainName));
        provinceDomainDetail.setLimit(10000L);
        provinceDomainDetail.setOffset(0L);
    }

    private void checkParam(WithinProvinceDomainDetail provinceDomainDetail) throws ParseException {
        // 按照时间粒度查询时，会传QueryTime参数，按照时间段查询时，不会传，不需要降维
        if (StrUtil.isNotEmpty(provinceDomainDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(provinceDomainDetail.getQueryType(), provinceDomainDetail.getQueryTime());
            provinceDomainDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            provinceDomainDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            provinceDomainDetail.setQueryType(ReportUtils.queryTypeDowngrade(provinceDomainDetail.getQueryType()));
        } else {
            provinceDomainDetail.formatParseTime(provinceDomainDetail.getQueryType(), "1d");
        }
    }

    private void setUnknownLast(List<WithinProvinceDomainDetail> dataList){
        for (WithinProvinceDomainDetail withinProvinceDomainDetail : dataList) {
            if("未知".equals(withinProvinceDomainDetail.getIsp())){
                WithinProvinceDomainDetail data = withinProvinceDomainDetail;
                dataList.remove(withinProvinceDomainDetail);
                dataList.add(data);
                break;
            }
        }
    }
}
