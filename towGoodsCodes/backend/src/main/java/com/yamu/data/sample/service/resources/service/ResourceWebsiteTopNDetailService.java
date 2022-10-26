package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;

import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.entity.ConstantEntity;
import com.yamu.data.sample.service.common.util.BusinessUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteNetOutDetailDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteTopNDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteNetOutDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteTopNDownloadNoResourceBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceWebsiteUserSourceVO;
import com.yamu.data.sample.service.resources.mapper.ResourceWebsiteTopNDetailMapper;
import com.yamu.data.sample.service.resources.mapper.ResourceWebsiteTopNNetOutDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author lishuntao
 * Date 2020-07-1
 */
@Service
@Slf4j
public class ResourceWebsiteTopNDetailService {

    @Autowired
    private ResourceWebsiteTopNNetOutDetailMapper websiteTopNNetOutDetailMapper;

    @Autowired
    private ResourceWebsiteTopNDetailMapper websiteTopNDetailMapper;

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    private final int DEFAULT_ISP_NUMBER = 20;

//    private final String DEFAULT_INTERVAL_TYPE = "10min";
//
//    private final String DEFAULT_QUERY_TYPE = "1min";

    private static String queryTable = "rpt_resource_website_topn_isp_distribution_";

    /**
     * 日期格式化.
     */
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JSONObject findwebsiteTopNRankTrend(ResourceWebsiteTopNDetail websiteTopN) throws YamuException{
        ResourceWebsiteTopNDetail resourceWebsiteTopN = checkWithinProvinceDomainRankParam(websiteTopN);
        List<String> websiteNameList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> parseTotalList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> parseLastTotalList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(websiteTopN.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            Map<String, String> easierTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(websiteTopN.getStartTime(), websiteTopN.getEndTime());
            List<ResourceWebsiteTopNDetail> resultList = websiteTopNDetailMapper.findNowTrendListByParamAll(resourceWebsiteTopN);
            resultList.stream().forEach(resultListDemo -> {
                if (ObjectUtil.isEmpty(resultListDemo.getWebsiteAppName())) {
                    websiteNameList.add("未知");
                }else {
                    websiteNameList.add(resultListDemo.getWebsiteAppName());
                }
            });
            Map<String, BigInteger> nowDataMap = resultList.stream()
                    .collect(Collectors.toMap(
                            ResourceWebsiteTopNDetail::getWebsiteAppName,
                            ResourceWebsiteTopNDetail::getParseTotalCnt,
                            BigInteger::add));
            if (CollUtil.isNotEmpty(websiteNameList)) {
                resourceWebsiteTopN.setLastStartTime(easierTimeMap.get(ReportUtils.EASIER_START));
                resourceWebsiteTopN.setLastEndTime(easierTimeMap.get(ReportUtils.EASIER_END));
                List<ResourceWebsiteTopNDetail> lastResultList = websiteTopNDetailMapper.findLastTrendListByParamAll(resourceWebsiteTopN, websiteNameList);
                Map<String, BigInteger> easierDataMap = lastResultList.stream()
                        .collect(Collectors.toMap(
                                ResourceWebsiteTopNDetail::getWebsiteAppName,
                                ResourceWebsiteTopNDetail::getParseTotalCnt));
                //是否补点
                for (String website : websiteNameList) {
                    parseTotalList.add(ObjectUtil.isEmpty(nowDataMap.get(website)) ? BigInteger.ZERO : nowDataMap.get(website));
                    parseLastTotalList.add(ObjectUtil.isEmpty(easierDataMap.get(website)) ? BigInteger.ZERO : easierDataMap.get(website));
                }
            }
        }else{
            List<ResourceWebsiteTopNDetail> resultList = websiteTopNDetailMapper.findNowTrendListByParam(resourceWebsiteTopN);
            String startTimeStr = resourceWebsiteTopN.getStartTime();
            String endTimeStr = resourceWebsiteTopN.getEndTime();
            //计算当前时间段和环比时间段
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
            //计算时间间隔
            Duration between = Duration.between(endTime, startTime);
            // 环比开始时间(向前推一个时间段)
            LocalDateTime earlierStartTime = startTime.plus(between);
            String earlierStartTimeStr = earlierStartTime.format(FORMATTER);
            // 计算环比时间
            resourceWebsiteTopN.setLastEndTime(startTimeStr);
            resourceWebsiteTopN.setLastStartTime(earlierStartTimeStr);

            resultList.stream().forEach(resultListDemo -> {
                if (ObjectUtil.isEmpty(resultListDemo.getWebsiteAppName())) {
                    websiteNameList.add("未知");
                }else {
                    websiteNameList.add(resultListDemo.getWebsiteAppName());
                }
                parseTotalList.add(resultListDemo.getParseTotalCnt());
            });
            if (CollUtil.isNotEmpty(websiteNameList)) {
                List<ResourceWebsiteTopNDetail> lastResultList = websiteTopNDetailMapper.findLastTrendListByParam(resourceWebsiteTopN,websiteNameList);

                int index = 0;
                for (ResourceWebsiteTopNDetail result:resultList){
                    LocalDateTime localDateTime = LocalDateTime.parse(result.getParseTimeLast(), FORMATTER);
                    LocalDateTime dateTime = localDateTime.plus(between);
                    String format = dateTime.format(FORMATTER);
                    String concat = result.getWebsiteAppName().concat(format);
                    for (ResourceWebsiteTopNDetail lastResult:lastResultList){
                        String lastConcat = lastResult.getWebsiteAppName().concat(lastResult.getParseTimeLast());
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
        }
        //组装结果
        String reportName = "Top20网站排名趋势";
        if (ObjectUtil.isNotEmpty(websiteTopN.getProvince())){
            reportName = websiteTopN.getProvince()+"Top20网站排名趋势";
        }
        JSONObject finalResult = buildRankReportWithParam(reportName, websiteNameList, parseTotalList, parseLastTotalList);
        return finalResult;
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

    private ResourceWebsiteTopNDetail checkWithinProvinceDomainRankParam(ResourceWebsiteTopNDetail provinceDomain) throws YamuException{
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
        String websiteAppName = provinceDomain.getWebsiteAppName();
        provinceDomain.setWebsiteAppName(ReportUtils.escapeChar(websiteAppName));
        return provinceDomain;
    }

    private boolean checkTimeParam(ResourceWebsiteTopNDetail queryParam) {
        boolean flag = false;
        if (StrUtil.isEmpty(queryParam.getStartTime()) || queryParam.getStartTime().equals("")) {
            flag = true;
        }
        return flag;
    }

    /**
     * 明细表
     * @param websiteTopNDetail
     * @return
     * @throws YamuException
     */
    public PageResult findTrendList(ResourceWebsiteTopNDetail websiteTopNDetail) throws YamuException{
        checkFindTrendListParam(websiteTopNDetail);
        Long total = Long.valueOf("0");
        List<ResourceWebsiteTopNDetail> dataList=org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(websiteTopNDetail.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = websiteTopNDetailMapper.countFindTrendListParamAll(websiteTopNDetail);
            dataList= websiteTopNDetailMapper.findTrendListByParamAll(websiteTopNDetail);
            dataList.stream().forEach(websiteTopN ->{
                websiteTopN.buildRate();
                websiteTopN.buildIcpRate();
                if (ObjectUtil.isEmpty(websiteTopN.getWebsiteAppName())){
                    websiteTopN.setWebsiteAppName("未知");
                }
                if (ObjectUtil.isEmpty(websiteTopN.getWebsiteType())) {
                    websiteTopN.setWebsiteType("未知");
                }
                websiteTopN.setTimeRange(websiteTopNDetail.getStartTime() + "~" + websiteTopNDetail.getEndTime());
            });
        } else {
            total = websiteTopNDetailMapper.countFindTrendListParam(websiteTopNDetail);
            dataList= websiteTopNDetailMapper.findTrendListByParam(websiteTopNDetail);
            dataList.stream().forEach(websiteTopN ->{
                websiteTopN.buildRate();
                websiteTopN.buildIcpRate();
                if (ObjectUtil.isEmpty(websiteTopN.getWebsiteAppName())){
                    websiteTopN.setWebsiteAppName("未知");
                }
                if (ObjectUtil.isEmpty(websiteTopN.getWebsiteType())) {
                    websiteTopN.setWebsiteType("未知");
                }
                websiteTopN.setTimeRange(DateUtils.formatDataToString(websiteTopN.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindTrendListParam(ResourceWebsiteTopNDetail popularCompanyTrend) throws YamuException{
        popularCompanyTrend.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        String websiteAppName = popularCompanyTrend.getWebsiteAppName();
        popularCompanyTrend.setWebsiteAppName(ReportUtils.escapeChar(websiteAppName));
        ValidationResult validationResult = ValidationUtils.validateEntity(popularCompanyTrend);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }


    public JSONObject findResourceReport(ResourceWebsiteTopNDetail websiteTopNDetail) throws YamuException, ParseException {
        checkFindTrendListParam(websiteTopNDetail);
        checkParam(websiteTopNDetail);
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendReportGroupByIspByParam(websiteTopNDetail);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (ResourceWebsiteTopNDetail companyTrend : dataList) {
            resultDataMap.put(companyTrend.getAnswerFirstIsp(), companyTrend.getARecordParseTotalCnt());
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


    private BigInteger resourceReportCountIspParseCntAndOther(List<ResourceWebsiteTopNDetail> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (ResourceWebsiteTopNDetail companyTrend : dataList) {
            if (index >= DEFAULT_ISP_NUMBER) {
                if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                    resultDataMap.put("其他", companyTrend.getARecordParseTotalCnt());
                } else {
                    resultDataMap.put("其他", resultDataMap.get("其他").add(companyTrend.getARecordParseTotalCnt()));
                }
            } else {
                if (companyTrend.getAnswerFirstIsp().equals("其他")) {
                    otherParseCnt = otherParseCnt.add(companyTrend.getARecordParseTotalCnt());
                    continue;
                }
                resultDataMap.put(companyTrend.getAnswerFirstIsp(), companyTrend.getARecordParseTotalCnt());
                index++;
            }
        }
        return otherParseCnt;
    }


    public JSONObject findRateReport(ResourceWebsiteTopNDetail websiteTopNDetail,boolean isTopN) throws YamuException, ParseException {
        checkFindTrendListParam(websiteTopNDetail);
        checkParam(websiteTopNDetail);
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendReportGroupByParseByParam(websiteTopNDetail,isTopN);
        dataList.stream().forEach(ResourceWebsiteTopNDetail::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(websiteTopNDetail.getStartTime(), websiteTopNDetail.getEndTime(), websiteTopNDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (ResourceWebsiteTopNDetail companyTrend : dataList) {
                netInRateResult.add(companyTrend.getNetInRate());
                parseInRateResult.add(companyTrend.getParseInRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, ResourceWebsiteTopNDetail> collect = dataList.stream().collect(Collectors.toMap(ResourceWebsiteTopNDetail::getParseTime, ResourceWebsiteTopNDetail -> ResourceWebsiteTopNDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                ResourceWebsiteTopNDetail companyDetail = collect.get(xKey);
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
        if (isTopN){
            reportName = "TopN网站本网率、本省率趋势";
        }
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findParseReport(ResourceWebsiteTopNDetail websiteTopNDetail) throws YamuException, ParseException {
        checkFindTrendListParam(websiteTopNDetail);
        checkParam(websiteTopNDetail);
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendReportGroupByParseByParam(websiteTopNDetail,false);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(websiteTopNDetail.getStartTime(), websiteTopNDetail.getEndTime(), websiteTopNDetail.getQueryType());

        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (ResourceWebsiteTopNDetail companyTrend : dataList) {
                CDNParseResult.add(companyTrend.getCdnParseTotalCnt());
                IDCParseResult.add(companyTrend.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, ResourceWebsiteTopNDetail> collect = dataList.stream().collect(Collectors.toMap(ResourceWebsiteTopNDetail::getParseTime, ResourceWebsiteTopNDetail -> ResourceWebsiteTopNDetail));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                ResourceWebsiteTopNDetail companyDetail = collect.get(xKey);
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


    public List<ResourceWebsiteTopNDetail> downloadByParam(ResourceWebsiteTopNDetail websiteTopNDetail) throws Exception{
        checkDownloadByParamMethodParam(websiteTopNDetail);
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendListByParam(websiteTopNDetail);
        dataList.stream().forEach(websiteTopN ->{
            websiteTopN.buildRate();
            if (ObjectUtil.isEmpty(websiteTopN.getWebsiteAppName())){
                websiteTopN.setWebsiteAppName("未知");
            }
            if (ObjectUtil.isEmpty(websiteTopN.getWebsiteType())) {
                websiteTopN.setWebsiteType("未知");
            }
        });
        return dataList;
    }

    private void checkDownloadByParamMethodParam(ResourceWebsiteTopNDetail websiteTopNDetail) throws UnsupportedEncodingException,YamuException {
        if (StrUtil.isEmpty(websiteTopNDetail.getStartTime()) || StrUtil.isEmpty(websiteTopNDetail.getEndTime())) {
            websiteTopNDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        ValidationResult validationResult = ValidationUtils.validateEntity(websiteTopNDetail);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkDownloadByParamMethodParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
        websiteTopNDetail.setLimit(10000L);
        websiteTopNDetail.setOffset(0L);
    }

    private void checkDownloadParam(ResourceWebsiteTopNDetail websiteTopNDetail, ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail) throws YamuException {
        if (StrUtil.isEmpty(websiteTopNDetail.getStartTime()) || StrUtil.isEmpty(websiteTopNDetail.getEndTime())) {
            websiteTopNDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        if (StrUtil.isEmpty(websiteTopNNetOutDetail.getStartTime()) || StrUtil.isEmpty(websiteTopNNetOutDetail.getEndTime())) {
            websiteTopNNetOutDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        ValidationResult websiteTopNDetailResult = ValidationUtils.validateEntity(websiteTopNDetail);
        if(websiteTopNDetailResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkDownloadParam method. param check error: " + websiteTopNDetailResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(websiteTopNDetailResult.getErrorMsg().values().stream().findFirst().get());
        }

        ValidationResult websiteTopNNetOutDetailResult = ValidationUtils.validateEntity(websiteTopNDetail);
        if(websiteTopNNetOutDetailResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkDownloadByParamMethodParam method. param check error: " + websiteTopNNetOutDetailResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(websiteTopNNetOutDetailResult.getErrorMsg().values().stream().findFirst().get());
        }

        websiteTopNNetOutDetail.setLimit(10000L);
        websiteTopNNetOutDetail.setOffset(0L);

        websiteTopNDetail.setLimit(10000L);
        websiteTopNDetail.setOffset(0L);
    }

    public JSONObject findNetInParseReport(ResourceWebsiteTopNDetail websiteTopNDetail) {
        websiteTopNDetail.formatParseTime(websiteTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.queryNetInParseGroupByDomainType(websiteTopNDetail);
        List<Date> parseDataList = dataList.stream().map(ResourceWebsiteTopNDetail::getParseTime).collect(Collectors.toList());
        List<String> xAxis = dataList.stream().map(ResourceWebsiteTopNDetail::getWebsiteAppName).collect(Collectors.toList());
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(websiteTopNDetail.getStartTime(), websiteTopNDetail.getEndTime(), websiteTopNDetail.getQueryType());
        List<Date> removeDateList = xAxisMap.keySet().stream().filter(date -> parseDataList.contains(date)).collect(Collectors.toList());
        removeDateList.stream().forEach(data -> xAxisMap.remove(data));
        List parseTotalResult = com.google.common.collect.Lists.newArrayList();
        List netInRateResult = com.google.common.collect.Lists.newArrayList();
        for (ResourceWebsiteTopNDetail resourceWebsiteTopNDetail : dataList) {
            parseTotalResult.add(resourceWebsiteTopNDetail.getParseTotalCnt());
            netInRateResult.add(ReportUtils.buildRatioBase(resourceWebsiteTopNDetail.getNetInParseTotalCnt(), resourceWebsiteTopNDetail.getARecordParseTotalCnt()));
        }
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put("netInRate", netInRateResult);
        dataMap.put("parseTotal", parseTotalResult);
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put("parseTotal", "解析次数");
        legend.put("netInRate", "本网率");
        // 报表名称
        String reportName = "热点网站/应用-TopN";
        Map<String, Map<String, Object>> paramMap = Maps.newHashMap();
        ReportUtils.putToReportMap(paramMap, "parseTotal", "type", "bar", "yAxisIndex", 0);
        ReportUtils.putToReportMap(paramMap, "netInRate", "type", "line", "yAxisIndex", 1);
        JSONObject finalResult = ReportUtils.buidReportWithParam(reportName, legend, xAxis, dataMap, paramMap);
        return finalResult;
    }

    public JSONObject findUserSource(ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO){
        List<ResourceWebsiteUserSource> dataList = websiteTopNDetailMapper.findUserSource(resourceWebsiteUserSourceVO);
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

    public JSONObject findCdnReportAll(ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        String queryType = resourceWebsiteTopNCdnBusinessDetail.getQueryType();
        resourceWebsiteTopNCdnBusinessDetail.setQueryTable(getTableName(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(getTableNameA(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setTimeRange(resourceWebsiteTopNCdnBusinessDetail.getStartTime() );

        List<ResourceWebsiteCdnReport> dataList = websiteTopNDetailMapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);

        Map<String, ResourceWebsiteCdnReport> xAxisMap = new LinkedHashMap<>();

        //只允许展示15条cdn厂商，剩下的按照其他计算
//        System.out.println(dataList.size()+"数据大小");
        if (dataList.size()>15){
            for (int i = 0; i < 15; i++) {
                dataList.get(i).setCdnResponseProportion((double)Math.round(dataList.get(i).getCdnResponseProportion()*10000)/10000);
                xAxisMap.put(dataList.get(i).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(i).getCdnBusiness(),
                                dataList.get(i).getCdnResponseCnt(),
                                dataList.get(i).getCdnResponseProportion()));
            }
            int tmp = 15 ;
            while (xAxisMap.size()<15){
                //自增一个即可
                dataList.get(tmp++).setCdnResponseProportion((double)Math.round(dataList.get(tmp).getCdnResponseProportion()*10000)/10000);
                xAxisMap.put(dataList.get(tmp).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(tmp).getCdnBusiness(),
                                dataList.get(tmp).getCdnResponseCnt(),
                                dataList.get(tmp).getCdnResponseProportion()));
//                System.out.println(tmp);
            }
            BigInteger tmpCdnResponseCnt = new BigInteger("0") ;
            BigInteger tmpCdnResponseProportion =new BigInteger("0") ;
            for (int i = tmp; i <dataList.size(); i++) {
                tmpCdnResponseCnt=tmpCdnResponseCnt.add(dataList.get(i).getCdnResponseCnt());
            }
            tmpCdnResponseProportion=dataList.get(0).getCdnParseTotalCnt();

            BigDecimal bigDecimal = new BigDecimal(tmpCdnResponseProportion+".0000");
            BigDecimal bigDecimal2 = new BigDecimal(tmpCdnResponseCnt+"00.0000");
            BigInteger[] bigIntegers = tmpCdnResponseCnt.divideAndRemainder(tmpCdnResponseProportion);
            Double divide ;

            divide = Double.parseDouble(bigDecimal2.divide(bigDecimal,4).setScale(4).toString());
            xAxisMap.put("其他",new ResourceWebsiteCdnReport("其他",tmpCdnResponseCnt,divide));

        }
        else {
            for (ResourceWebsiteCdnReport resourceWebsiteCdnReport : dataList) {
                resourceWebsiteCdnReport.setCdnResponseProportion(resourceWebsiteCdnReport.getCdnResponseProportion());
                xAxisMap.put(resourceWebsiteCdnReport.getCdnBusiness(),
                        new ResourceWebsiteCdnReport(resourceWebsiteCdnReport.getCdnBusiness(),
                                resourceWebsiteCdnReport.getCdnResponseCnt(),
                                resourceWebsiteCdnReport.getCdnResponseProportion()));
            }
        }

        //总数结果集,成功结果集
        List totalResult = Lists.newArrayList();
        List growthResult = Lists.newArrayList();
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

    public JSONObject findCdnReport(ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        String queryType = resourceWebsiteTopNCdnBusinessDetail.getQueryType();
        resourceWebsiteTopNCdnBusinessDetail.setQueryTable(getTableName(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(getTableNameA(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setTimeRange(resourceWebsiteTopNCdnBusinessDetail.getStartTime() );

        List<ResourceWebsiteCdnReport> dataList = websiteTopNDetailMapper.findCdnReport2(resourceWebsiteTopNCdnBusinessDetail);
        for (ResourceWebsiteCdnReport resourceWebsiteCdnReport : dataList) {
            resourceWebsiteCdnReport.setCdnResponseProportion(
                    ReportUtils.buildRatioBase(resourceWebsiteCdnReport.getCdnResponseCnt(), resourceWebsiteCdnReport.getCdnParseTotalCnt()));
        }

        Map<String, ResourceWebsiteCdnReport> xAxisMap = new LinkedHashMap<>();

        //只允许展示15条cdn厂商，剩下的按照其他计算
//        System.out.println(dataList.size()+"数据大小");
        if (dataList.size()>15){
            for (int i = 0; i < 15; i++) {
                dataList.get(i).setCdnResponseProportion((double)Math.round(dataList.get(i).getCdnResponseProportion()*10000)/10000);
                xAxisMap.put(dataList.get(i).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(i).getCdnBusiness(),
                                dataList.get(i).getCdnResponseCnt(),
                                dataList.get(i).getCdnResponseProportion()));
            }
            int tmp = 15 ;
            while (xAxisMap.size()<15){
                //自增一个即可
                dataList.get(tmp++).setCdnResponseProportion((double)Math.round(dataList.get(tmp).getCdnResponseProportion()*10000)/10000);
                xAxisMap.put(dataList.get(tmp).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(tmp).getCdnBusiness(),
                                dataList.get(tmp).getCdnResponseCnt(),
                                dataList.get(tmp).getCdnResponseProportion()));
//                System.out.println(tmp);
            }
            BigInteger tmpCdnResponseCnt = new BigInteger("0") ;
            BigInteger tmpCdncdnParseTotalCnt ; // =new BigInteger("0") ;
            for (int i = tmp; i <dataList.size(); i++) {
                tmpCdnResponseCnt=tmpCdnResponseCnt.add(dataList.get(i).getCdnResponseCnt());
            }
            tmpCdncdnParseTotalCnt=dataList.get(0).getCdnParseTotalCnt();

            BigDecimal bigDecimal = new BigDecimal(tmpCdncdnParseTotalCnt+".0000");
            BigDecimal bigDecimal2 = new BigDecimal(tmpCdnResponseCnt+".0000");
            Double divide ;

            divide = Double.parseDouble(bigDecimal2.divide(bigDecimal,4).setScale(4).toString());
            xAxisMap.put("其他",new ResourceWebsiteCdnReport("其他",tmpCdnResponseCnt,divide));

        }
        else {
            for (ResourceWebsiteCdnReport resourceWebsiteCdnReport : dataList) {
                resourceWebsiteCdnReport.setCdnResponseProportion(resourceWebsiteCdnReport.getCdnResponseProportion());
                xAxisMap.put(resourceWebsiteCdnReport.getCdnBusiness(),
                        new ResourceWebsiteCdnReport(resourceWebsiteCdnReport.getCdnBusiness(),
                                resourceWebsiteCdnReport.getCdnResponseCnt(),
                                resourceWebsiteCdnReport.getCdnResponseProportion()));
            }
        }

        //总数结果集,成功结果集
        List totalResult = Lists.newArrayList();
        List growthResult = Lists.newArrayList();
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

    public PageResult findCdnDetail(ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){

        String queryType = resourceWebsiteTopNCdnBusinessDetail.getQueryType();
        resourceWebsiteTopNCdnBusinessDetail.setQueryTable(getTableName(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(getTableNameA(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setTimeRange(resourceWebsiteTopNCdnBusinessDetail.getStartTime() );

        List<ResourceWebsiteCdnReport> dataList = websiteTopNDetailMapper.findCdnReport2(resourceWebsiteTopNCdnBusinessDetail);
        List<ResourceWebsiteCdnReportDetail> resourceWebsiteCdnReportDetails = new ArrayList<>();
        for (ResourceWebsiteCdnReport resourceWebsiteCdnReport : dataList) {
//            System.out.println("这是测试数据"+resourceWebsiteCdnReport.getResponseProportion());
            resourceWebsiteCdnReportDetails.add(toChange(resourceWebsiteCdnReport));
        }
//        resourceWebsiteTopNCdnBusinessDetail.setQueryType(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
////        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getQueryType());
////        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getQueryTable());
//        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
//        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getAQueryTable());
        Long total = websiteTopNDetailMapper.countCdnReportlList(resourceWebsiteTopNCdnBusinessDetail);

        PageResult pageResult = new PageResult(total, resourceWebsiteCdnReportDetails);

        return pageResult;
    }

    public List<ResourceDistributionProvinceData> getResourceDistributionProvinceList(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        List<ResourceDistributionProvinceData> list = websiteTopNDetailMapper.resourceDistributionProvince(resourceDistributionProvinceVO,tableName);
        List<ResourceDistributionProvinceDetail> detailList = websiteTopNDetailMapper.resourceDistributionProvinceDetail(resourceDistributionProvinceVO,tableName);
        for (ResourceDistributionProvinceData resourceDistributionProvinceData : list) {
            List<ResourceDistributionProvinceDetail> dataList = new ArrayList<>();
            for (ResourceDistributionProvinceDetail resourceDistributionProvinceDetail : detailList) {
                if(resourceDistributionProvinceData.getAnswerFirstProvince().equals(resourceDistributionProvinceDetail.getAnswerFirstProvince())){
                    dataList.add(resourceDistributionProvinceDetail);
                }
            }
            resourceDistributionProvinceData.setData(dataList);
        }
        return list;
    }

    public ResourceIpDetail findIpDetail(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        ResourceIpDetail resourceIpDetail = new ResourceIpDetail();
        Long total = websiteTopNDetailMapper.getIpDetailTotal(resourceDistributionProvinceVO,tableName);
        List<ResourceIpDetailData> list = websiteTopNDetailMapper.getIpDetailList(resourceDistributionProvinceVO,tableName);
        resourceIpDetail.setTotal(total);
        resourceIpDetail.setData(list);
        return resourceIpDetail;
    }


    public void download(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail,ResourceWebsiteTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail, HttpServletResponse response) throws IOException, YamuException {
        ResourceWebsiteTopNDetail websiteTopNDetail = BeanUtil.copyProperties(websiteTopNNetOutDetail, ResourceWebsiteTopNDetail.class, "queryTable");
        checkDownloadParam(websiteTopNDetail, websiteTopNNetOutDetail);
        // 网站表格导出
        List<WebsiteTopNDownloadBO> topNDownloadBOList = Lists.newArrayList();
        List<WebsiteNetOutDownloadBO> topNNetOutBOList = Lists.newArrayList();
        List<WebsiteNetOutDetailDownloadBO> netOutDetailBOList = Lists.newArrayList();
        List<ResourceWebsiteCdnReportDetail> resourceWebsiteCdnReportDetails = new ArrayList<>();

        resourceWebsiteTopNCdnBusinessDetail.setTimeRange(resourceWebsiteTopNCdnBusinessDetail.getStartTime() );
        resourceWebsiteTopNCdnBusinessDetail.setLimit(10000l);

        List<ResourceWebsiteCdnReport> dataList = websiteTopNDetailMapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        for (ResourceWebsiteCdnReport resourceWebsiteCdnReport : dataList) {
            resourceWebsiteCdnReportDetails.add(toChange(resourceWebsiteCdnReport));
        }

        if(ObjectUtil.equals(websiteTopNDetail.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            List<ResourceWebsiteTopNDetail> websiteTopNDataList = websiteTopNDetailMapper.findTrendListByParamAll(websiteTopNDetail);
            websiteTopNDataList.stream().forEach(topNDetail -> {
                topNDetail.setTimeRange(websiteTopNDetail.getStartTime() + "~" + websiteTopNDetail.getEndTime());
                WebsiteTopNDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteTopNDownloadBO.class);
                topNDownloadBO.buildRate();
                topNDownloadBO.buildIcpRate();
                topNDownloadBOList.add(topNDownloadBO);
            });
        }else{
            List<ResourceWebsiteTopNDetail> websiteTopNDataList = websiteTopNDetailMapper.findTrendListByParam(websiteTopNDetail);
            websiteTopNDataList.stream().forEach(topNDetail -> {
                topNDetail.setTimeRange(DateUtils.formatDataToString(topNDetail.getParseTime(), DateUtils.DEFAULT_FMT));
                WebsiteTopNDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteTopNDownloadBO.class);
                topNDownloadBO.buildRate();
                topNDownloadBO.buildIcpRate();
                topNDownloadBOList.add(topNDownloadBO);
            });
        }
        websiteTopNNetOutDetail.setAQueryTable(websiteTopNNetOutDetail.getQueryType());
        // 查找出网率数据
        List<ResourceWebsiteTopNNetOutDetail> netOutDataList = websiteTopNNetOutDetailMapper.findDomainNetOutExcelList(websiteTopNNetOutDetail,websiteTopNDetail);
        netOutDataList.stream().forEach(topNDetail -> {
            WebsiteNetOutDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteNetOutDownloadBO.class);
            topNNetOutBOList.add(topNDownloadBO);
        });

        // 查找出网率详细数据
        List<ResourceWebsiteTopNNetOutDetail> netOutDetailDataList = websiteTopNNetOutDetailMapper.findDomainNetOutDetailExcelList(websiteTopNNetOutDetail,websiteTopNDetail);
        netOutDetailDataList.stream().forEach(topNDetail -> {
            WebsiteNetOutDetailDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteNetOutDetailDownloadBO.class);
            netOutDetailBOList.add(topNDownloadBO);
        });

        ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO = BeanUtil.copyProperties(websiteTopNNetOutDetail, ResourceWebsiteUserSourceVO.class, "queryTable");
        List<ResourceWebsiteUserSource> userSourceList = websiteTopNDetailMapper.findUserSourceExcel(resourceWebsiteUserSourceVO,websiteTopNDetail);

        ResourceDistributionProvinceVO resourceDistributionProvinceVO = BeanUtil.copyProperties(websiteTopNNetOutDetail, ResourceDistributionProvinceVO.class);
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        List<ResourceIpDetailData> resourceIpDetailList = websiteTopNDetailMapper.getIpDetailExcelList(resourceDistributionProvinceVO,tableName,websiteTopNDetail);

        String timeInterval = websiteTopNNetOutDetail.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + websiteTopNNetOutDetail.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "TopN网站解析明细报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.setHeaderAlias(getWebsiteTopNHeaderAlias());
        writer.renameSheet("TopN网站解析明细报表");
        writer.setOnlyAlias(true);
        writer.write(topNDownloadBOList, true);

        writer.setHeaderAlias(getWebsiteNetOutHeaderAlias());
        writer.setSheet("TopN网站出网域名数据报表");
        writer.write(topNNetOutBOList, true);

        writer.setHeaderAlias(getNetOutDetailHeaderAlias());
        writer.setSheet("TopN网站出网域名明细报表");
        writer.write(netOutDetailBOList, true);

        writer.setHeaderAlias(getUserSourceHeaderAlias());
        writer.setSheet("TopN网站来源用户分布");
        writer.write(userSourceList, true);

        writer.setHeaderAlias(getIpDetailHeaderAlias());
        writer.setSheet("TopN网站资源分布省份");
        writer.write(resourceIpDetailList, true);

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

    public ResourceWebsiteCdnReportDetail toChange(ResourceWebsiteCdnReport resourceWebsiteCdnReport){
        ResourceWebsiteCdnReportDetail resourceWebsiteCdnReportDetail = new ResourceWebsiteCdnReportDetail();

        resourceWebsiteCdnReportDetail.setCdnBusiness(resourceWebsiteCdnReport.getCdnBusiness());
        resourceWebsiteCdnReportDetail.setCdnResponseCnt(resourceWebsiteCdnReport.getCdnResponseCnt());
        resourceWebsiteCdnReportDetail.setResponseProportion(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(resourceWebsiteCdnReport.getCdnResponseCnt(), resourceWebsiteCdnReport.getResponseCnt()),2));
        resourceWebsiteCdnReportDetail.setCdnResponseProportion(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(resourceWebsiteCdnReport.getCdnResponseCnt(), resourceWebsiteCdnReport.getCdnParseTotalCnt()),2));
        resourceWebsiteCdnReportDetail.setSuccessCnt(resourceWebsiteCdnReport.getSuccessCnt());
        resourceWebsiteCdnReportDetail.setSuccessRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(resourceWebsiteCdnReport.getSuccessCnt(), resourceWebsiteCdnReport.getCdnResponseCnt()),2));
        resourceWebsiteCdnReportDetail.setNetInCnt(resourceWebsiteCdnReport.getNetInCnt());
        resourceWebsiteCdnReportDetail.setNetInRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(resourceWebsiteCdnReport.getNetInCnt(), resourceWebsiteCdnReport.getCdnResponseCnt()),2));
        resourceWebsiteCdnReportDetail.setWithinCnt(resourceWebsiteCdnReport.getWithinCnt());
        resourceWebsiteCdnReportDetail.setWithinRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(resourceWebsiteCdnReport.getWithinCnt(), resourceWebsiteCdnReport.getCdnResponseCnt()),2));
        return resourceWebsiteCdnReportDetail ;
    }

    public void downloadNoResource(ResourceWebsiteTopNNetOutDetail websiteTopNNetOutDetail, HttpServletResponse response) throws IOException, YamuException {
        ResourceWebsiteTopNDetail websiteTopNDetail = BeanUtil.copyProperties(websiteTopNNetOutDetail, ResourceWebsiteTopNDetail.class, "queryTable");
        checkDownloadParam(websiteTopNDetail, websiteTopNNetOutDetail);
        // 网站表格导出
        List<WebsiteTopNDownloadNoResourceBO> topNDownloadBOList = Lists.newArrayList();
        List<WebsiteNetOutDownloadBO> topNNetOutBOList = Lists.newArrayList();
        List<WebsiteNetOutDetailDownloadBO> netOutDetailBOList = Lists.newArrayList();
        if(ObjectUtil.equals(websiteTopNDetail.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            List<ResourceWebsiteTopNDetail> websiteTopNDataList = websiteTopNDetailMapper.findTrendListByParamAll(websiteTopNDetail);
            websiteTopNDataList.stream().forEach(topNDetail -> {
                topNDetail.setTimeRange(websiteTopNDetail.getStartTime() + "~" + websiteTopNDetail.getEndTime());
                WebsiteTopNDownloadNoResourceBO websiteTopNDownloadNoResourceBO = BeanUtil.copyProperties(topNDetail, WebsiteTopNDownloadNoResourceBO.class);
                websiteTopNDownloadNoResourceBO.buildRate();
                topNDownloadBOList.add(websiteTopNDownloadNoResourceBO);
            });
        }else{
            List<ResourceWebsiteTopNDetail> websiteTopNDataList = websiteTopNDetailMapper.findTrendListByParam(websiteTopNDetail);
            websiteTopNDataList.stream().forEach(topNDetail -> {
                topNDetail.setTimeRange(DateUtils.formatDataToString(topNDetail.getParseTime(), DateUtils.DEFAULT_FMT));
                WebsiteTopNDownloadNoResourceBO websiteTopNDownloadNoResourceBO = BeanUtil.copyProperties(topNDetail, WebsiteTopNDownloadNoResourceBO.class);
                websiteTopNDownloadNoResourceBO.buildRate();
                topNDownloadBOList.add(websiteTopNDownloadNoResourceBO);
            });
        }
        // 查找出网率数据
        websiteTopNNetOutDetail.setAQueryTable(websiteTopNNetOutDetail.getQueryType());
        List<ResourceWebsiteTopNNetOutDetail> netOutDataList = websiteTopNNetOutDetailMapper.findDomainNetOutExcelListNoResource(websiteTopNNetOutDetail,websiteTopNDetail);
        netOutDataList.stream().forEach(topNDetail -> {
            WebsiteNetOutDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteNetOutDownloadBO.class);
            topNNetOutBOList.add(topNDownloadBO);
        });

        // 查找出网率详细数据
        List<ResourceWebsiteTopNNetOutDetail> netOutDetailDataList = websiteTopNNetOutDetailMapper.findDomainNetOutDetailExcelListNoResource(websiteTopNNetOutDetail,websiteTopNDetail);
        netOutDetailDataList.stream().forEach(topNDetail -> {
            WebsiteNetOutDetailDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteNetOutDetailDownloadBO.class);
            netOutDetailBOList.add(topNDownloadBO);
        });

        ResourceWebsiteUserSourceVO resourceWebsiteUserSourceVO = BeanUtil.copyProperties(websiteTopNNetOutDetail, ResourceWebsiteUserSourceVO.class, "queryTable");
        List<ResourceWebsiteUserSource> userSourceList = websiteTopNDetailMapper.findUserSourceExcelNoResource(resourceWebsiteUserSourceVO);
        String fileName = "网内省内网站无资源报表" + StrUtil.DASHED + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.merge(8, "导出时间段   开始时间:"+websiteTopNNetOutDetail.getStartTime().substring(0,websiteTopNNetOutDetail.getStartTime().length()-3)
                +",结束时间:"+websiteTopNNetOutDetail.getEndTime().substring(0,websiteTopNNetOutDetail.getEndTime().length()-3));
        writer.setHeaderAlias(getWebsiteTopNHeaderAliasNoResource());
        writer.renameSheet("网内省内网站无资源报表");
        writer.write(topNDownloadBOList, true);

        writer.setHeaderAlias(getWebsiteNetOutHeaderAlias());
        writer.setSheet("网内省内网站无资源出网域名数据报表");
        writer.merge(8, "导出时间段   开始时间:"+websiteTopNNetOutDetail.getStartTime().substring(0,websiteTopNNetOutDetail.getStartTime().length()-3)
                +",结束时间:"+websiteTopNNetOutDetail.getEndTime().substring(0,websiteTopNNetOutDetail.getEndTime().length()-3));
        writer.write(topNNetOutBOList, true);

        writer.setHeaderAlias(getNetOutDetailHeaderAlias());
        writer.setSheet("网内省内网站无资源出网域名明细报表");
        writer.merge(8, "导出时间段   开始时间:"+websiteTopNNetOutDetail.getStartTime().substring(0,websiteTopNNetOutDetail.getStartTime().length()-3)
                +",结束时间:"+websiteTopNNetOutDetail.getEndTime().substring(0,websiteTopNNetOutDetail.getEndTime().length()-3));
        writer.write(netOutDetailBOList, true);

        writer.setHeaderAlias(getUserSourceHeaderAlias());
        writer.setSheet("网内省内网站无资源来源用户分布");
        writer.merge(8, "导出时间段   开始时间:"+websiteTopNNetOutDetail.getStartTime().substring(0,websiteTopNNetOutDetail.getStartTime().length()-3)
                +",结束时间:"+websiteTopNNetOutDetail.getEndTime().substring(0,websiteTopNNetOutDetail.getEndTime().length()-3));
        writer.write(userSourceList, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private Map<String, String> getWebsiteTopNHeaderAliasNoResource() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("timeRange", "时间");
        aliasMapResult.put("websiteAppName", "网站名称");
        aliasMapResult.put("websiteType", "分类");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        aliasMapResult.put("netOutRate", "出网率");
        aliasMapResult.put("withoutParseTotalCnt", "外省次数");
        aliasMapResult.put("parseOutRate", "出省率");
        return aliasMapResult;
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
        aliasMapResult.put("websiteAppName", "网站名称");
        aliasMapResult.put("companyShortName", "公司");
        aliasMapResult.put("websiteType", "分类");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netInParseTotalCnt", "网内次数(IPv4)");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("icpRate", "ICP调度准确率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        aliasMapResult.put("netOutRate", "出网率");
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
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("answerFirstProvince", "省份");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("answerFirstIsp", "运营商");
        return aliasMapResult;
    }

    private Map<String, String> getUserSourceHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("websiteAppName", "网站名称");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        return aliasMapResult;
    }

    private Map<String, String> getIpDetailHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("answerFirstIp", "服务ip");
        aliasMapResult.put("parseTotalCnt", "请求次数");
        aliasMapResult.put("province", "省份");
        aliasMapResult.put("city", "城市");
        aliasMapResult.put("answerFirstIsp", "运营商");
        return aliasMapResult;
    }

    private void checkParam(ResourceWebsiteTopNDetail websiteTopNDetail) throws ParseException {
        // 按照时间粒度查询时，会传QueryTime参数，按照时间段查询时，不会传，不需要降维
        if (StrUtil.isNotEmpty(websiteTopNDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(websiteTopNDetail.getQueryType(), websiteTopNDetail.getQueryTime());
            websiteTopNDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            websiteTopNDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            websiteTopNDetail.setQueryType(ReportUtils.queryTypeDowngrade(websiteTopNDetail.getQueryType()));
        } else {
            websiteTopNDetail.formatParseTime(websiteTopNDetail.getQueryType(), "1d");
        }
    }

    private void setUnknownLast(List<ResourceWebsiteTopNDetail> dataList){
        for (ResourceWebsiteTopNDetail resourceWebsiteTopNDetail : dataList) {
            if("未知".equals(resourceWebsiteTopNDetail.getAnswerFirstIsp())){
                ResourceWebsiteTopNDetail data = resourceWebsiteTopNDetail;
                dataList.remove(resourceWebsiteTopNDetail);
                dataList.add(data);
                break;
            }
        }
    }

    private String getTableName(String queryType){
        return "rpt_resource_website_cdn_business_" + queryType;
    }
    private String getTableNameA(String queryType){
        return "rpt_resource_website_topn_detail_" + queryType;
    }
}
