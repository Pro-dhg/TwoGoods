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
import com.yamu.data.sample.service.common.entity.ConstantEntity;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.NetOutTopNTypeDetailDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.NetOutTopNTypeDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteTopNTypeDetailListDownloadBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDomainDetailUserSourceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceTopNTypeUserSourceVO;
import com.yamu.data.sample.service.resources.mapper.ResourceDomainTopnDetailMapper;
import com.yamu.data.sample.service.resources.thread.ResourceHomeKeyBusinessThread;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @Author Lishuntao
 * @Date 2021/1/21
 */
@Service
public class ResourceDomainTopnDetailService {

    private static final int DEFAULT_ISP_NUMBER = 20;
    @Autowired
    ResourceDomainTopnDetailMapper mapper;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    //
    public JSONObject findGroupByIsp(ResourceDomainTopnDetail queryParam) throws ParseException {
        checkFindRateReportParam(queryParam);
        List<ResourceDomainTopnDetail> dataList;
        if (null != queryParam.getIsTopN() && queryParam.getIsTopN()) {
            // 根据topn条件查询
            dataList = mapper.findTopnGroupByIsp(queryParam);
        } else if (null == queryParam.getDomainName() || queryParam.getDomainName().isEmpty()) {
            // 若域名为空,则直接返回空List
            dataList = Lists.newArrayList();
        } else {
            // 根据具体域名查询
            dataList = mapper.findDomainGroupByIsp(queryParam);
        }
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (ResourceDomainTopnDetail param : dataList) {
            resultDataMap.put(param.getIsp(), param.getARecordParseTotalCnt());
        }
        //BigInteger otherParseCnt = BigInteger.ZERO;
        // 按照20条以上归于"其他"处理
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
        String reportName = "";
        if((queryParam.getNetInZero() != null && !"".equals(queryParam.getNetInZero())) ||
                (queryParam.getWithinZero() != null && !"".equals(queryParam.getWithinZero()))){
            reportName = "资源分布";
        }else{
            reportName = "热点域名资源分布";
        }
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

    // isCount=false: 统计运营商(isp='其他')和数据map
    private BigInteger resourceReportAllIspParseCntAndOther(List<ResourceDomainTopnDetail> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        for (ResourceDomainTopnDetail param : dataList) {
            if (param.getIsp().equals("其他")) {
                otherParseCnt = otherParseCnt.add(param.getParseTotalCnt());
                continue;
            }
            resultDataMap.put(param.getIsp(), param.getParseTotalCnt());
        }
        return otherParseCnt;
    }

    /**
     * 按照20条数据进行整个.超过20归于"其他"
     *
     * @param dataList
     * @param resultDataMap
     * @param otherParseCnt
     * @return
     */
    private BigInteger resourceReportCountIspParseCntAndOther(List<ResourceDomainTopnDetail> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (ResourceDomainTopnDetail param : dataList) {
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

    /**
     * 查询TopN趋势分析
     *
     * @param queryParam
     * @return
     */
    public List<ResourceDomainTopnDetail> findAllGroupByTopnParseTime(ResourceDomainTopnDetail queryParam) {
        List<ResourceDomainTopnDetail> dataList = mapper.findAllGroupByTopnParseTime(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }


    /**
     * 根据domain精确查询趋势分析.
     *
     * @param queryParam
     * @return
     */
    public List<ResourceDomainTopnDetail> findAllGroupByDoaminParseTime(ResourceDomainTopnDetail queryParam) {
        List<ResourceDomainTopnDetail> dataList = mapper.findAllGroupByDoaminParseTime(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    /**
     * 查询明细表.
     *
     * @param queryParam
     * @return
     */
    public PageResult findRate(ResourceDomainTopnDetail queryParam) {
        PageResult pageResult = new PageResult();
//        String domainName = queryParam.getDomainName();
//        if (StringUtils.isNotEmpty(domainName)) {
//            List<String> collect = Arrays.stream(domainName.split(";")).collect(Collectors.toList());
//            queryParam.setDomainParamList(collect);
//        }
        Long total = 0L;
        // 判断按照时间粒度查询还是按照时间段查询
        if (StatisticsWayEnum.ALL.getType().equals(queryParam.getStatisticsWay())) {
            total = mapper.countAllGroupByDomain(queryParam);
        } else {
            total = mapper.countAllGroupByParseTimeAndDomain(queryParam);
        }

        if (total == null || total == 0) {
            pageResult.setTotal(0L);
            pageResult.setData(Lists.newArrayList());
            return pageResult;
        }
        List<ResourceDomainTopnDetail> dataList;
        if (StatisticsWayEnum.ALL.getType().equals(queryParam.getStatisticsWay())) {
            dataList = mapper.findAllGroupByDomain(queryParam);
            dataList.forEach(resourceDomainTopnDetail -> resourceDomainTopnDetail.setTimeRange(queryParam.getStartTime() + "~" + queryParam.getEndTime()));
        } else {
            dataList = mapper.findAllGroupByParseTimeAndDomain(queryParam);
            dataList.forEach(resourceDomainTopnDetail -> resourceDomainTopnDetail.setTimeRange(DateUtils.formatDataToString(resourceDomainTopnDetail.getParseTime(), DateUtils.DEFAULT_FMT)));
        }

        dataList.stream().forEach(ResourceDomainTopnDetail::buildRate);
        pageResult.setTotal(total);
        pageResult.setData(dataList);
        return pageResult;
    }

    public PageResult findDetailsByPage(ResourceDomainTopnDetail queryParam) {
        Long total = mapper.findByCount(queryParam);
        List<ResourceDomainTopnDetail> result = mapper.findByPage(queryParam);
        return PageResult.buildPageResult(total, result);
    }

    /**
     * 查询时间做降维处理.
     *
     * @param resourceDomainTopnDetail
     * @throws ParseException
     */
    public void checkFindRateReportParam(ResourceDomainTopnDetail resourceDomainTopnDetail) throws ParseException {
        if (StrUtil.isNotEmpty(resourceDomainTopnDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(resourceDomainTopnDetail.getQueryType(), resourceDomainTopnDetail.getQueryTime());
            resourceDomainTopnDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            resourceDomainTopnDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            resourceDomainTopnDetail.setQueryType(ReportUtils.queryTypeDowngrade(resourceDomainTopnDetail.getQueryType()));
        } else {
            resourceDomainTopnDetail.formatParseTime(resourceDomainTopnDetail.getQueryType(), "1d");
        }
    }

    /**
     * 服务IP数据.
     *
     * @param queryParam
     * @return
     */
    public PageResult nodeServerDetail(ResourceDomainTopnDetail queryParam) throws ParseException {
        Long total = 0L;
        List<ResourceDomainTopnDetail> resourceDomainTopnDetails = Lists.newArrayList();
        // 若域名为空,则返回空数据
        if (null == queryParam.getDomainName() || queryParam.getDomainName().isEmpty()) {
            return new PageResult(total, resourceDomainTopnDetails);
        }
        checkFindRateReportParam(queryParam);
        total = mapper.nodeServerDetailCount(queryParam);
        resourceDomainTopnDetails = mapper.nodeServerDetail(queryParam);
        return new PageResult(total, resourceDomainTopnDetails);
    }

    public JSONObject findNetInParseReport(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        List<ResourceDomainTopnDetail> dataList = mapper.queryNetInParseGroupByDomainType(domainTopNDetail);
        List<Date> parseDataList = dataList.stream().map(ResourceDomainTopnDetail::getParseTime).collect(Collectors.toList());
        List<String> xAxis = dataList.stream().map(ResourceDomainTopnDetail::getDomainName).collect(Collectors.toList());
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime(), domainTopNDetail.getQueryType());
        List<Date> removeDateList = xAxisMap.keySet().stream().filter(date -> !parseDataList.contains(date)).collect(Collectors.toList());
        removeDateList.stream().forEach(data -> xAxisMap.remove(data));
        List parseTotalResult = com.google.common.collect.Lists.newArrayList();
        List netInRateResult = com.google.common.collect.Lists.newArrayList();
        for (ResourceDomainTopnDetail resourceDomainTopnDetail : dataList) {
            parseTotalResult.add(resourceDomainTopnDetail.getParseTotalCnt());
            netInRateResult.add(ReportUtils.buildRatioBase(resourceDomainTopnDetail.getNetInParseTotalCnt(), resourceDomainTopnDetail.getARecordParseTotalCnt()));
        }
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put("netInRate", netInRateResult);
        dataMap.put("parseTotal", parseTotalResult);
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put("parseTotal", "解析次数");
        legend.put("netInRate", "本网率");
        // 报表名称
        String reportName = "热点域名-TopN";
        Map<String, Map<String, Object>> paramMap = Maps.newHashMap();
        ReportUtils.putToReportMap(paramMap, "parseTotal", "type", "bar", "yAxisIndex", 0);
        ReportUtils.putToReportMap(paramMap, "netInRate", "type", "line", "yAxisIndex", 1);
        JSONObject finalResult = ReportUtils.buidReportWithParam(reportName, legend, xAxis, dataMap, paramMap);
        return finalResult;
    }

    public JSONObject findNetRate(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        List<ResourceDomainTopnDetail> dataList = mapper.queryParseTotalByParam(domainTopNDetail);
        List<Date> parseDataList = dataList.stream().map(ResourceDomainTopnDetail::getParseTime).collect(Collectors.toList());
        dataList.stream().forEach(ResourceDomainTopnDetail::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime(), domainTopNDetail.getQueryType());
        List<Date> removeDateList = xAxisMap.keySet().stream().filter(date -> !parseDataList.contains(date)).collect(Collectors.toList());
        removeDateList.stream().forEach(data -> xAxisMap.remove(data));
        List withInRateResult = com.google.common.collect.Lists.newArrayList();
        List netInRateResult = com.google.common.collect.Lists.newArrayList();
        for (ResourceDomainTopnDetail resourceDomainTopnDetail : dataList) {
            withInRateResult.add(resourceDomainTopnDetail.getParseInRate());
            netInRateResult.add(resourceDomainTopnDetail.getNetInRate());
        }
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put("netInRate", netInRateResult);
        dataMap.put("withInRate", withInRateResult);
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put("netInRate", "本网率");
        legend.put("withInRate", "本省率");
        // 报表名称
        String reportName = "热点域名本网率、本省率";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findDomainParse(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        List<ResourceDomainTopnDetail> dataList = mapper.queryParseTotalByParam(domainTopNDetail);
        List<Date> parseDataList = dataList.stream().map(ResourceDomainTopnDetail::getParseTime).collect(Collectors.toList());
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime(), domainTopNDetail.getQueryType());
        List<Date> removeDateList = xAxisMap.keySet().stream().filter(date -> !parseDataList.contains(date)).collect(Collectors.toList());
        removeDateList.stream().forEach(data -> xAxisMap.remove(data));
        List parseTotalResult = com.google.common.collect.Lists.newArrayList();
        List parseSuccessResult = com.google.common.collect.Lists.newArrayList();
        List parseFailResult = com.google.common.collect.Lists.newArrayList();
        List netInParseResult = com.google.common.collect.Lists.newArrayList();
//        List netOutParseResult = com.google.common.collect.Lists.newArrayList();
//        List withOutParseResult = com.google.common.collect.Lists.newArrayList();
        List withInParseResult = com.google.common.collect.Lists.newArrayList();
        List aRecordParseCnt = com.google.common.collect.Lists.newArrayList();
        for (ResourceDomainTopnDetail resourceDomainTopnDetail : dataList) {
            parseTotalResult.add(resourceDomainTopnDetail.getParseTotalCnt());
            parseSuccessResult.add(resourceDomainTopnDetail.getParseSuccessCnt());
            parseFailResult.add(resourceDomainTopnDetail.getParseFailCnt());
            netInParseResult.add(resourceDomainTopnDetail.getNetInParseTotalCnt());
//            netOutParseResult.add(resourceDomainTopnDetail.getNetOutParseTotalCnt());
//            withOutParseResult.add(resourceDomainTopnDetail.getWithOutParseTotalCnt());
            withInParseResult.add(resourceDomainTopnDetail.getWithInParseTotalCnt());
            aRecordParseCnt.add(resourceDomainTopnDetail.getARecordParseTotalCnt());
        }
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put("parseTotalResult", parseTotalResult);
        dataMap.put("parseSuccessResult", parseSuccessResult);
        dataMap.put("parseFailResult", parseFailResult);
        dataMap.put("netInParseResult", netInParseResult);
//        dataMap.put("netOutParseResult", parseTotalResult);
//        dataMap.put("withOutParseResult", parseSuccessResult);
        dataMap.put("withInParseResult", withInParseResult);
        dataMap.put("aRecordParseCnt", aRecordParseCnt);
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put("parseTotalResult", "解析次数");
        legend.put("parseSuccessResult", "成功次数");
        legend.put("parseFailResult", "失败次数");
        legend.put("netInParseResult", "网内次数");
//        legend.put("netOutParseResult", "网外次数");
        legend.put("withInParseResult", "省内次数");
//        legend.put("withOutParseResult", "省外次数");
        legend.put("aRecordParseCnt", "IPv4解析次数");
        // 报表名称
        String reportName = "热点域名解析统计";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public JSONObject findDomainParseRate(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        Map<String, String> yoyTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        Map<String, String> momTimeMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        ResourceDomainTopnDetail data = mapper.queryParseTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(yoyTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(yoyTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail yoyData = mapper.queryParseTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(momTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(momTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail momData = mapper.queryParseTotalCntByParam(domainTopNDetail);
        JSONObject finalResult = new JSONObject();
        finalResult.put("parseTotal", data.getParseTotalCnt());
        finalResult.put("yoyRate", ReportUtils.buildRatioGT(yoyData.getParseTotalCnt(), data.getParseTotalCnt()));
        finalResult.put("momRate", ReportUtils.buildRatioGT(momData.getParseTotalCnt(), data.getParseTotalCnt()));
        return finalResult;
    }

    public JSONObject findDomainSuccessRate(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        Map<String, String> yoyTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        Map<String, String> momTimeMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        ResourceDomainTopnDetail data = mapper.queryParseSuccessTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(yoyTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(yoyTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail yoyData = mapper.queryParseSuccessTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(momTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(momTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail momData = mapper.queryParseSuccessTotalCntByParam(domainTopNDetail);
        JSONObject finalResult = new JSONObject();
        finalResult.put("successRate", ReportUtils.buildRatioBase(data.getParseSuccessCnt(), data.getParseTotalCnt()));
        finalResult.put("yoyRate", ReportUtils.buildRatioGT(yoyData.getParseSuccessCnt(), data.getParseSuccessCnt()));
        finalResult.put("momRate", ReportUtils.buildRatioGT(momData.getParseSuccessCnt(), data.getParseSuccessCnt()));
        return finalResult;
    }

    public JSONObject findDomainNetInRate(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        Map<String, String> yoyTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        Map<String, String> momTimeMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        ResourceDomainTopnDetail data = mapper.queryParseNetTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(yoyTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(yoyTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail yoyData = mapper.queryParseNetTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(momTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(momTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail momData = mapper.queryParseNetTotalCntByParam(domainTopNDetail);
        JSONObject finalResult = new JSONObject();
        finalResult.put("netInRate", ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getARecordParseTotalCnt()));
        finalResult.put("yoyRate", ReportUtils.buildRatioGT(yoyData.getNetInParseTotalCnt(), data.getNetInParseTotalCnt()));
        finalResult.put("momRate", ReportUtils.buildRatioGT(momData.getNetInParseTotalCnt(), data.getNetInParseTotalCnt()));
        return finalResult;
    }

    public JSONObject findDomainWithInRate(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        Map<String, String> yoyTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        Map<String, String> momTimeMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        ResourceDomainTopnDetail data = mapper.queryParseWithTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(yoyTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(yoyTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail yoyData = mapper.queryParseWithTotalCntByParam(domainTopNDetail);
        domainTopNDetail.setStartTime(momTimeMap.get(ReportUtils.EASIER_START));
        domainTopNDetail.setEndTime(momTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail momData = mapper.queryParseWithTotalCntByParam(domainTopNDetail);
        JSONObject finalResult = new JSONObject();
        finalResult.put("withInRate", ReportUtils.buildRatioBase(data.getWithInParseTotalCnt(), data.getParseTotalCnt()));
        finalResult.put("yoyRate", ReportUtils.buildRatioGT(yoyData.getWithInParseTotalCnt(), data.getWithInParseTotalCnt()));
        finalResult.put("momRate", ReportUtils.buildRatioGT(momData.getWithInParseTotalCnt(), data.getWithInParseTotalCnt()));
        return finalResult;
    }

    public JSONObject domainDetailRate(ResourceDomainTopnDetail domainTopNDetail) {
        domainTopNDetail.formatParseTime(domainTopNDetail.getQueryType(), ConstantEntity.INTERVAL_10MIN);
        Map<String, String> yoyTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        Map<String, String> momTimeMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(domainTopNDetail.getStartTime(), domainTopNDetail.getEndTime());
        ResourceDomainTopnDetail yoyDomainTopNDetail = BeanUtil.copyProperties(domainTopNDetail, ResourceDomainTopnDetail.class);
        yoyDomainTopNDetail.setStartTime(yoyTimeMap.get(ReportUtils.EASIER_START));
        yoyDomainTopNDetail.setEndTime(yoyTimeMap.get(ReportUtils.EASIER_END));
        ResourceDomainTopnDetail momDomainTopNDetail = BeanUtil.copyProperties(domainTopNDetail, ResourceDomainTopnDetail.class);
        momDomainTopNDetail.setStartTime(momTimeMap.get(ReportUtils.EASIER_START));
        momDomainTopNDetail.setEndTime(momTimeMap.get(ReportUtils.EASIER_END));
        CountDownLatch latch = new CountDownLatch(3);
        ResourceHomeKeyBusinessThread resourceHomeKeyBusinessThread = new ResourceHomeKeyBusinessThread(domainTopNDetail,latch,mapper);
        ResourceHomeKeyBusinessThread yoyResourceHomeKeyBusinessThread = new ResourceHomeKeyBusinessThread(yoyDomainTopNDetail,latch,mapper);
        ResourceHomeKeyBusinessThread momResourceHomeKeyBusinessThread = new ResourceHomeKeyBusinessThread(momDomainTopNDetail,latch,mapper);
        taskExecutor.execute(resourceHomeKeyBusinessThread);
        taskExecutor.execute(yoyResourceHomeKeyBusinessThread);
        taskExecutor.execute(momResourceHomeKeyBusinessThread);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResourceDomainTopnDetail data = resourceHomeKeyBusinessThread.getData();
        ResourceDomainTopnDetail yoyData = yoyResourceHomeKeyBusinessThread.getData();
        ResourceDomainTopnDetail momData = momResourceHomeKeyBusinessThread.getData();
        JSONObject jsonResult = new JSONObject();
        JSONObject parseTotalResult = new JSONObject();
        parseTotalResult.put("parseTotal", data.getParseTotalCnt());
        parseTotalResult.put("yoyRate", ReportUtils.buildRatioGT(yoyData.getParseTotalCnt(), data.getParseTotalCnt()));
        parseTotalResult.put("momRate", ReportUtils.buildRatioGT(momData.getParseTotalCnt(), data.getParseTotalCnt()));
        JSONObject successRateResult = new JSONObject();
        Double successRate = ReportUtils.buildRatioBase(data.getParseSuccessCnt(), data.getParseTotalCnt());
        successRateResult.put("successRate", successRate);
        successRateResult.put("yoyRate", buildRatioGT(BigDecimal.valueOf(ReportUtils.buildRatioBase(yoyData.getParseSuccessCnt(),yoyData.getParseTotalCnt())),
                BigDecimal.valueOf(successRate)));
        successRateResult.put("momRate", buildRatioGT(BigDecimal.valueOf(ReportUtils.buildRatioBase(momData.getParseSuccessCnt(),momData.getParseTotalCnt())),
                BigDecimal.valueOf(successRate)));
        JSONObject netInResult = new JSONObject();
        Double netInRate = ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getARecordParseTotalCnt());
        netInResult.put("netInRate", netInRate);
        netInResult.put("yoyRate", buildRatioGT(BigDecimal.valueOf(ReportUtils.buildRatioBase(yoyData.getNetInParseTotalCnt(),yoyData.getARecordParseTotalCnt())),
                BigDecimal.valueOf(netInRate)));
        netInResult.put("momRate", buildRatioGT(BigDecimal.valueOf(ReportUtils.buildRatioBase(momData.getNetInParseTotalCnt(),momData.getARecordParseTotalCnt())),
                BigDecimal.valueOf(netInRate)));
        JSONObject withInResult = new JSONObject();
        Double withInRate = ReportUtils.buildRatioBase(data.getWithInParseTotalCnt(), data.getParseTotalCnt());
        withInResult.put("withInRate", withInRate);
        withInResult.put("yoyRate", buildRatioGT(BigDecimal.valueOf(ReportUtils.buildRatioBase(yoyData.getWithInParseTotalCnt(),yoyData.getParseTotalCnt())),
                BigDecimal.valueOf(withInRate)));
        withInResult.put("momRate", buildRatioGT(BigDecimal.valueOf(ReportUtils.buildRatioBase(momData.getWithInParseTotalCnt(),momData.getParseTotalCnt())),
                BigDecimal.valueOf(withInRate)));
        jsonResult.put("parseTotal", parseTotalResult);
        jsonResult.put("successRate", successRateResult);
        jsonResult.put("netInRate", netInResult);
        jsonResult.put("withInRate", withInResult);
        return jsonResult;
    }

    public JSONObject findUserSource(ResourceDomainDetailUserSourceVO resourceDomainDetailUserSourceVO){
        List<ResourceWebsiteUserSource> dataList = mapper.findUserSource(resourceDomainDetailUserSourceVO);
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

    private void setUnknownLast(List<ResourceDomainTopnDetail> dataList){
        for (ResourceDomainTopnDetail resourceDomainTopnDetail : dataList) {
            if("未知".equals(resourceDomainTopnDetail.getIsp())){
                ResourceDomainTopnDetail data = resourceDomainTopnDetail;
                dataList.remove(resourceDomainTopnDetail);
                dataList.add(data);
                break;
            }
        }
    }

    public static Double buildRatioGT(BigDecimal beforeData, BigDecimal data) {
        if (beforeData.compareTo(BigDecimal.ZERO) == 0) {
            return 0D;
        }
        BigDecimal result = data.subtract(beforeData).divide(beforeData, 4, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    public List<ResourceDistributionProvinceData> getResourceDistributionProvinceList(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        String queryTable = "rpt_resource_domain_topn_detail_" + resourceDistributionProvinceVO.getQueryType();
        List<ResourceDistributionProvinceData> list = mapper.resourceDistributionProvince(resourceDistributionProvinceVO,queryTable);
        List<ResourceDistributionProvinceDetail> detailList = mapper.resourceDistributionProvinceDetail(resourceDistributionProvinceVO,queryTable);
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

    public JSONObject findCdnReportAll(PopularDomainTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        String queryType = resourceWebsiteTopNCdnBusinessDetail.getQueryType();
        resourceWebsiteTopNCdnBusinessDetail.setQueryTable(getTableName(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(getTableNameA(queryType));
        List<PopularDomainCdnReport> dataList = mapper.findCdnReport2(resourceWebsiteTopNCdnBusinessDetail);
        DecimalFormat format = new DecimalFormat("#.00");

        Map<String, ResourceWebsiteCdnReport> xAxisMap = new LinkedHashMap<>();

        //只允许展示15条cdn厂商，剩下的按照其他计算
        if (dataList.size()>15){
            for (int i = 0; i < 15; i++) {
                dataList.get(i).setCdnResponseProportion((double)Math.round(dataList.get(i).getCdnResponseProportion()/100*10000)/10000);
                xAxisMap.put(dataList.get(i).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(i).getCdnBusiness(),
                                dataList.get(i).getCdnResponseCnt(),
                                dataList.get(i).getCdnResponseProportion()));
            }

            int tmp = 15 ;
            while (xAxisMap.size()<15){
                //自增一个即可
                dataList.get(tmp++).setCdnResponseProportion((double)Math.round(dataList.get(tmp).getCdnResponseProportion()*100)/10000);
                xAxisMap.put(dataList.get(tmp).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(tmp).getCdnBusiness(),
                                dataList.get(tmp).getCdnResponseCnt(),
                                dataList.get(tmp).getCdnResponseProportion()));
            }

            BigInteger tmpCdnResponseCnt = new BigInteger("0") ;
            BigInteger tmpCdnResponseProportion =new BigInteger("0") ;
            for (int i = tmp; i <dataList.size(); i++) {
                tmpCdnResponseCnt=tmpCdnResponseCnt.add(dataList.get(i).getCdnResponseCnt());
            }
            tmpCdnResponseProportion=dataList.get(0).getCdnParseTotalCnt();

            BigDecimal bigDecimal = new BigDecimal(tmpCdnResponseProportion+".0000");
            BigDecimal bigDecimal2 = new BigDecimal(tmpCdnResponseCnt+".0000");
            Double divide = Double.parseDouble(bigDecimal2.divide(bigDecimal,4).setScale(4).toString());
            xAxisMap.put("其他",new ResourceWebsiteCdnReport("其他",tmpCdnResponseCnt,divide));
        }
        else {
            for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
//                resourceWebsiteCdnReport.setCdnResponseProportion((double)Math.round(resourceWebsiteCdnReport.getCdnResponseProportion()/100*100)/100);
                resourceWebsiteCdnReport.setCdnResponseProportion(resourceWebsiteCdnReport.getCdnResponseProportion()/100);
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

    public JSONObject findCdnReport(PopularDomainTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail){
        String queryType = resourceWebsiteTopNCdnBusinessDetail.getQueryType();
        resourceWebsiteTopNCdnBusinessDetail.setQueryTable(getTableName(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(getTableNameA(queryType));
        List<PopularDomainCdnReport> dataList = mapper.findCdnReport2(resourceWebsiteTopNCdnBusinessDetail);
        DecimalFormat format = new DecimalFormat("#.00");

        Map<String, ResourceWebsiteCdnReport> xAxisMap = new LinkedHashMap<>();

        //只允许展示15条cdn厂商，剩下的按照其他计算
        if (dataList.size()>15){
            for (int i = 0; i < 15; i++) {
                dataList.get(i).setCdnResponseProportion((double)Math.round(dataList.get(i).getCdnResponseProportion()/100*10000)/10000);
                xAxisMap.put(dataList.get(i).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(i).getCdnBusiness(),
                                dataList.get(i).getCdnResponseCnt(),
                                dataList.get(i).getCdnResponseProportion()));
            }

            int tmp = 15 ;
            while (xAxisMap.size()<15){
                //自增一个即可
                dataList.get(tmp++).setCdnResponseProportion((double)Math.round(dataList.get(tmp).getCdnResponseProportion()*100)/10000);
                xAxisMap.put(dataList.get(tmp).getCdnBusiness(),
                        new ResourceWebsiteCdnReport(dataList.get(tmp).getCdnBusiness(),
                                dataList.get(tmp).getCdnResponseCnt(),
                                dataList.get(tmp).getCdnResponseProportion()));
            }

            BigInteger tmpCdnResponseCnt = new BigInteger("0") ;
            BigInteger tmpCdnResponseProportion =new BigInteger("0") ;
            for (int i = tmp; i <dataList.size(); i++) {
                tmpCdnResponseCnt=tmpCdnResponseCnt.add(dataList.get(i).getCdnResponseCnt());
            }
            tmpCdnResponseProportion=dataList.get(0).getCdnParseTotalCnt();

            BigDecimal bigDecimal = new BigDecimal(tmpCdnResponseProportion+".0000");
            BigDecimal bigDecimal2 = new BigDecimal(tmpCdnResponseCnt+".0000");
            Double divide = Double.parseDouble(bigDecimal2.divide(bigDecimal,4).setScale(4).toString());
            xAxisMap.put("其他",new ResourceWebsiteCdnReport("其他",tmpCdnResponseCnt,divide));
        }
        else {
            for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
//                resourceWebsiteCdnReport.setCdnResponseProportion((double)Math.round(resourceWebsiteCdnReport.getCdnResponseProportion()/100*100)/100);
                resourceWebsiteCdnReport.setCdnResponseProportion(resourceWebsiteCdnReport.getCdnResponseProportion()/100);
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
        String queryType = resourceWebsiteTopNCdnBusinessDetail.getQueryType();
        resourceWebsiteTopNCdnBusinessDetail.setQueryTable(getTableName(queryType));
        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(getTableNameA(queryType));
//        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getAQueryTable()+"上上上");

        List<PopularDomainCdnReport> dataList = mapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        List<PopularDomainCdnReportDetail> resourceWebsiteCdnReportDetails = new ArrayList<>();
        for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
            resourceWebsiteCdnReport.setCdnResponseProportion((double)Math.round(resourceWebsiteCdnReport.getCdnResponseProportion()/100*10000)/100);
            if (resourceWebsiteCdnReport.getCdnResponseProportion()<=0){
                resourceWebsiteCdnReport.setCdnResponseProportion(0d);
            }
            resourceWebsiteCdnReportDetails.add(toChange(resourceWebsiteCdnReport));
        }
//        resourceWebsiteTopNCdnBusinessDetail.setQueryType(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
////        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getQueryType());
////        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getQueryTable());
//        resourceWebsiteTopNCdnBusinessDetail.setAQueryTable(ReportUtils.queryTypeDowngrade(resourceWebsiteTopNCdnBusinessDetail.getQueryType()));
//        System.out.println(resourceWebsiteTopNCdnBusinessDetail.getAQueryTable()+"下下下");
        Long total = mapper.countCdnReportlList(resourceWebsiteTopNCdnBusinessDetail);

        PageResult pageResult = new PageResult(total, resourceWebsiteCdnReportDetails);

        return pageResult;
    }

    /*
    public void download(PopularDomainTopNCdnBusinessDetail resourceWebsiteTopNCdnBusinessDetail, HttpServletResponse response) throws IOException, YamuException {
        PopularDomainTopNType domainNameWebsiteDetail = BeanUtil.copyProperties(resourceWebsiteTopNCdnBusinessDetail, PopularDomainTopNType.class, "queryTable");
        // 网站表格导出
        List<PopularDomainCdnReportDetail> resourceWebsiteCdnReportDetails = new ArrayList<>();

        resourceWebsiteTopNCdnBusinessDetail.setLimit(10000L);
        List<PopularDomainCdnReport> dataList = mapper.findCdnReport(resourceWebsiteTopNCdnBusinessDetail);
        for (PopularDomainCdnReport resourceWebsiteCdnReport : dataList) {
            resourceWebsiteCdnReportDetails.add(toChange(resourceWebsiteCdnReport));
        }

        String timeInterval = resourceWebsiteTopNCdnBusinessDetail.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
              + StrUtil.UNDERLINE + resourceWebsiteTopNCdnBusinessDetail.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "域名TopN明细分析报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();


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
    */

    public void download(ResourceDomainTopnDetail queryParam, HttpServletResponse response) throws IOException {
        List<ResourceDomainTopnDetail> dataList = findRate(queryParam).getData();
        ResourceDistributionProvinceVO resourceDistributionProvinceVO = BeanUtil.copyProperties(queryParam, ResourceDistributionProvinceVO.class);
        if(queryParam.isOtherQtype()){
            resourceDistributionProvinceVO.setQtype("other");
        }
        List<ResourceIpDetailData> resourceIpDetailList = mapper.getIpDetailExcelList(resourceDistributionProvinceVO,queryParam.getQueryTable());
        String fileName = "热点域名TopN明细分析" + StrUtil.DASHED + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.setHeaderAlias(getWebsiteTopNHeaderAlias());
        writer.renameSheet("热点域名TopN明细");
        writer.setOnlyAlias(true);
        writer.write(dataList, true);

        writer.setHeaderAlias(getIpDetailHeaderAlias());
        writer.setSheet("热点域名TopN资源分布省份");
        writer.setOnlyAlias(true);
        writer.write(resourceIpDetailList, true);

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

    private Map<String, String> getWebsiteTopNHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("timeRange", "时间");
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("websiteAppName", "网站");
        aliasMapResult.put("companyShortName", "公司");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("icpRate", "ICP调度准确率");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netInParseTotalCnt", "网内次数(IPv4)");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        aliasMapResult.put("netOutRate", "出网率");
        aliasMapResult.put("withInParseTotalCnt", "本省次数");
        aliasMapResult.put("parseInRate", "本省率");
        aliasMapResult.put("withOutParseTotalCnt", "外省次数");
        aliasMapResult.put("parseOutRate", "出省率");
        aliasMapResult.put("cdnParseTotalCnt", "CDN次数");
        aliasMapResult.put("idcParseTotalCnt", "IDC次数");
        aliasMapResult.put("cacheParseTotalCnt", "CACHE次数");
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

    private String getTableName(String queryType){
        return "rpt_resource_domain_topn_cdn_business_" + queryType;
    }
    private String getTableNameA(String queryType){
        return "rpt_resource_domain_topn_detail_" + queryType;
    }
}
