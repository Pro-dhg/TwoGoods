package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.WebsiteSecondLevelDomainTopN;
import com.yamu.data.sample.service.resources.mapper.WebsiteSecondLevelDomainTopNMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yuyuan.Dong
 * @Date 2021/8/18
 * @DESC
 */
@Service
@Slf4j
public class WebsiteSecondLevelDomainTopNService {


    @Autowired
    private WebsiteSecondLevelDomainTopNMapper secondLevelDomainTopNMapper;

    private static final int DEFAULT_ISP_NUMBER = 20;

    public PageResult findTableDetail(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        Long total = 0L;
        List<WebsiteSecondLevelDomainTopN> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(domainNameWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            total = secondLevelDomainTopNMapper.countQueryDataGroupByTimeParam(domainNameWebsiteDetail);
            dataList = secondLevelDomainTopNMapper.queryDataGroupByTimeParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            total = secondLevelDomainTopNMapper.countQueryDataGroupByQueryTimeParam(domainNameWebsiteDetail);
            dataList = secondLevelDomainTopNMapper.queryDataGroupByQueryTimeParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
//        dataList.stream().forEach(WebsiteSecondLevelDomainTopN::buildRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    //子表
    public PageResult findSecondTableDetail(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException{
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        Long total = 0L;
        List<WebsiteSecondLevelDomainTopN> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(domainNameWebsiteDetail.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            total = secondLevelDomainTopNMapper.countQuerySecondDataGroupByTimeParam(domainNameWebsiteDetail);
            dataList = secondLevelDomainTopNMapper.querySecondDataGroupByTimeParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            checkFindQueryTimeParam(domainNameWebsiteDetail);
            total = secondLevelDomainTopNMapper.countQuerySecondDataGroupByQueryTimeParam(domainNameWebsiteDetail);
            dataList = secondLevelDomainTopNMapper.querySecondDataGroupByQueryTimeParam(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.stream().forEach(WebsiteSecondLevelDomainTopN::buildRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindQueryTimeParam(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail){
        if (StrUtil.isNotEmpty(domainNameWebsiteDetail.getQueryTime())) {
            domainNameWebsiteDetail.setStartTime(domainNameWebsiteDetail.getQueryTime());
            domainNameWebsiteDetail.setEndTime(domainNameWebsiteDetail.getQueryTime());
        }
    }


    /**
     * 本网本省率
     * @param domainNameWebsiteDetail
     * @return
     * @throws Exception
     */
    public JSONObject findRateReport(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws Exception {
        checkFindRateReportParam(domainNameWebsiteDetail);
        List<WebsiteSecondLevelDomainTopN> dataList = secondLevelDomainTopNMapper.queryLastTimeDataGroupByWebsiteByParam(domainNameWebsiteDetail);
        dataList.stream().forEach(WebsiteSecondLevelDomainTopN::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), domainNameWebsiteDetail.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (WebsiteSecondLevelDomainTopN domainNameWebsiteDetail1 : dataList) {
                netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                parseInRateResult.add(domainNameWebsiteDetail1.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, WebsiteSecondLevelDomainTopN> collect = dataList.stream().
                    collect(Collectors.toMap(WebsiteSecondLevelDomainTopN::getParseTime, WebsiteSecondLevelDomainTopN -> WebsiteSecondLevelDomainTopN));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                WebsiteSecondLevelDomainTopN domainNameWebsiteDetail1 = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainNameWebsiteDetail1)) {
                    netInRateResult.add(domainNameWebsiteDetail1.getNetInRate());
                    parseInRateResult.add(domainNameWebsiteDetail1.getWithinRate());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }

        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "netInRateResult", netInRateResult);
        dataMap.put(ReportUtils.LINE + "WithinRateResult", parseInRateResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "netInRateResult", "本网率");
        legend.put(ReportUtils.LINE + "WithinRateResult", "本省率");

        // 报表名称
        String reportName = "本网率、本省率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    private void checkFindRateReportParam(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException {
        if (StrUtil.isNotEmpty(domainNameWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainNameWebsiteDetail.getQueryType(), domainNameWebsiteDetail.getQueryTime());
            domainNameWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainNameWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainNameWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainNameWebsiteDetail.getQueryType()));
        } else {
            domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        }
    }

    /**
     * 资源分布
     * @param secondLevelDomainTopN
     * @return
     * @throws ParseException
     */
    public JSONObject findResourceReport(WebsiteSecondLevelDomainTopN secondLevelDomainTopN) throws ParseException {
        secondLevelDomainTopN.formatParseTime(secondLevelDomainTopN.getQueryType(), "1d");
        if (ObjectUtil.equals(secondLevelDomainTopN.getStatisticsWay(),"every")) {
            checkFindQueryTimeParam(secondLevelDomainTopN);
        }
        List<WebsiteSecondLevelDomainTopN> dataList = secondLevelDomainTopNMapper.findTrendReportGroupByIspByParam(secondLevelDomainTopN);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (WebsiteSecondLevelDomainTopN param : dataList) {
            resultDataMap.put(param.getIsp(), param.getSecondLevelDomainParseTotalCnt());
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

    private BigInteger resourceReportCountIspParseCntAndOther(List<WebsiteSecondLevelDomainTopN> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (WebsiteSecondLevelDomainTopN param : dataList) {
            if (index >= DEFAULT_ISP_NUMBER) {
                if (ObjectUtil.isEmpty(resultDataMap.get("其他"))) {
                    resultDataMap.put("其他", param.getSecondLevelDomainParseTotalCnt());
                } else {
                    resultDataMap.put("其他", resultDataMap.get("其他").add(param.getSecondLevelDomainParseTotalCnt()));
                }
            } else {
                if (param.getIsp().equals("其他")) {
                    otherParseCnt = otherParseCnt.add(param.getSecondLevelDomainParseTotalCnt());
                    continue;
                }
                resultDataMap.put(param.getIsp(), param.getSecondLevelDomainParseTotalCnt());
                index++;
            }
        }
        return otherParseCnt;
    }

    private void checkFindResourceReportParam(WebsiteSecondLevelDomainTopN websiteSecondLevelDomain) throws ParseException {
        if (StrUtil.isNotEmpty(websiteSecondLevelDomain.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(websiteSecondLevelDomain.getQueryType(), websiteSecondLevelDomain.getQueryTime());
            websiteSecondLevelDomain.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            websiteSecondLevelDomain.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            websiteSecondLevelDomain.setQueryType(ReportUtils.queryTypeDowngrade(websiteSecondLevelDomain.getQueryType()));
        } else {
            websiteSecondLevelDomain.formatParseTime(websiteSecondLevelDomain.getQueryType(), "1d");
        }
    }


    /**
     * IDC\CDN次数
     * @param domainNameWebsiteDetail
     * @return
     * @throws ParseException
     */
    public JSONObject findParseReport(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException {
        checkFindParseReport(domainNameWebsiteDetail);
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        List<WebsiteSecondLevelDomainTopN> dataList = secondLevelDomainTopNMapper.queryLastTimeDataGroupByWebsiteByParam(domainNameWebsiteDetail);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(domainNameWebsiteDetail.getStartTime(), domainNameWebsiteDetail.getEndTime(), domainNameWebsiteDetail.getQueryType());
        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (WebsiteSecondLevelDomainTopN domainWebsiteDetail : dataList) {
                CDNParseResult.add(domainWebsiteDetail.getCdnParseTotalCnt());
                IDCParseResult.add(domainWebsiteDetail.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, WebsiteSecondLevelDomainTopN> collect = dataList.stream().collect(Collectors.toMap(WebsiteSecondLevelDomainTopN::getParseTime, WebsiteSecondLevelDomainTopN -> WebsiteSecondLevelDomainTopN));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                WebsiteSecondLevelDomainTopN domainWebsiteDetail = collect.get(xKey);
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

    private void checkFindParseReport(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) throws ParseException {
        if (StrUtil.isNotEmpty(domainNameWebsiteDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(domainNameWebsiteDetail.getQueryType(), domainNameWebsiteDetail.getQueryTime());
            domainNameWebsiteDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            domainNameWebsiteDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            domainNameWebsiteDetail.setQueryType(ReportUtils.queryTypeDowngrade(domainNameWebsiteDetail.getQueryType()));
        }
    }

    public List<WebsiteSecondLevelDomainTopN> downloadByParam(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) {
        checkDownloadByParamMethodParam(domainNameWebsiteDetail);
        List<WebsiteSecondLevelDomainTopN> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(domainNameWebsiteDetail.getStatisticsWay(),"all")) {
            dataList = secondLevelDomainTopNMapper.queryDataByTimeParamToDownload(domainNameWebsiteDetail);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(domainNameWebsiteDetail.getStartTime() + "~" + domainNameWebsiteDetail.getEndTime());
            });
        } else {
            dataList = secondLevelDomainTopNMapper.queryDataByQueryTimeParamToDownload(domainNameWebsiteDetail);
            dataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(WebsiteSecondLevelDomainTopN domainNameWebsiteDetail) {
        domainNameWebsiteDetail.formatParseTime(domainNameWebsiteDetail.getQueryType(), "1d");
        domainNameWebsiteDetail.setLimit(10000L);
        domainNameWebsiteDetail.setOffset(0L);
    }

    private void setUnknownLast(List<WebsiteSecondLevelDomainTopN> dataList){
        for (WebsiteSecondLevelDomainTopN websiteSecondLevelDomainTopN : dataList) {
            if("未知".equals(websiteSecondLevelDomainTopN.getIsp())){
                WebsiteSecondLevelDomainTopN data = websiteSecondLevelDomainTopN;
                dataList.remove(websiteSecondLevelDomainTopN);
                dataList.add(data);
                break;
            }
        }
    }
}
