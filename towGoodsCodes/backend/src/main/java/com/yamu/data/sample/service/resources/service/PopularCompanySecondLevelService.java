package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.SecondDomainTableListBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.PopularCompanySecondLevel;
import com.yamu.data.sample.service.resources.entity.vo.SecondDomainServerVO;
import com.yamu.data.sample.service.resources.entity.vo.SecondDomainTableListVO;
import com.yamu.data.sample.service.resources.mapper.PopularCompanySecondLevelMapper;
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
 * @Date 2022/3/16
 * @DESC
 */
@Service
@Slf4j
public class PopularCompanySecondLevelService {

    @Autowired
    private PopularCompanySecondLevelMapper secondLevelDomainTopNMapper;

    private static final int DEFAULT_ISP_NUMBER = 20;

    public SecondDomainTableListBO findTableDetail(PopularCompanySecondLevel companySecondLevel) {
        companySecondLevel.formatParseTime(companySecondLevel.getQueryType(), "1d");
        Long total = 0L;
        List<SecondDomainTableListVO> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(companySecondLevel.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = secondLevelDomainTopNMapper.countQueryDataGroupByTimeParam(companySecondLevel);
            dataList = secondLevelDomainTopNMapper.queryDataGroupByTimeParam(companySecondLevel);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(companySecondLevel.getStartTime() + "~" + companySecondLevel.getEndTime());
            });
        } else {
            total = secondLevelDomainTopNMapper.countQueryDataGroupByQueryTimeParam(companySecondLevel);
            dataList = secondLevelDomainTopNMapper.queryDataGroupByQueryTimeParam(companySecondLevel);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        SecondDomainTableListBO secondDomainTableListBO = new SecondDomainTableListBO();
        secondDomainTableListBO.setTotal(total);
        secondDomainTableListBO.setData(dataList);
        return secondDomainTableListBO;
    }

    //子表
    public PageResult findSecondTableDetail(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        companySecondLevel.formatParseTime(companySecondLevel.getQueryType(), "1d");
        Long total = 0L;
        List<PopularCompanySecondLevel> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(companySecondLevel.getStatisticsWay(),StatisticsWayEnum.ALL.getType())) {
            total = secondLevelDomainTopNMapper.countQuerySecondDataGroupByTimeParam(companySecondLevel);
            dataList = secondLevelDomainTopNMapper.querySecondDataGroupByTimeParam(companySecondLevel);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(companySecondLevel.getStartTime() + "~" + companySecondLevel.getEndTime());
            });
        } else {
            checkFindQueryTimeParam(companySecondLevel);
            total = secondLevelDomainTopNMapper.countQuerySecondDataGroupByQueryTimeParam(companySecondLevel);
            dataList = secondLevelDomainTopNMapper.querySecondDataGroupByQueryTimeParam(companySecondLevel);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.stream().forEach(PopularCompanySecondLevel::buildRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindQueryTimeParam(PopularCompanySecondLevel companySecondLevel){
        if (StrUtil.isNotEmpty(companySecondLevel.getQueryTime())) {
            companySecondLevel.setStartTime(companySecondLevel.getQueryTime());
            companySecondLevel.setEndTime(companySecondLevel.getQueryTime());
        }
    }


    /**
     * 本网本省率
     * @param companySecondLevel
     * @return
     * @throws Exception
     */
    public JSONObject findRateReport(PopularCompanySecondLevel companySecondLevel) throws Exception {
        checkFindRateReportParam(companySecondLevel);
        List<PopularCompanySecondLevel> dataList = secondLevelDomainTopNMapper.queryLastTimeDataGroupByWebsiteByParam(companySecondLevel);
        dataList.stream().forEach(PopularCompanySecondLevel::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(companySecondLevel.getStartTime(), companySecondLevel.getEndTime(), companySecondLevel.getQueryType());

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularCompanySecondLevel companySecondLevel1 : dataList) {
                netInRateResult.add(companySecondLevel1.getNetInRate());
                parseInRateResult.add(companySecondLevel1.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularCompanySecondLevel> collect = dataList.stream().
                    collect(Collectors.toMap(PopularCompanySecondLevel::getParseTime, PopularCompanySecondLevel -> PopularCompanySecondLevel));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularCompanySecondLevel companySecondLevel1 = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companySecondLevel1)) {
                    netInRateResult.add(companySecondLevel1.getNetInRate());
                    parseInRateResult.add(companySecondLevel1.getWithinRate());
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

    private void checkFindRateReportParam(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        if (StrUtil.isNotEmpty(companySecondLevel.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(companySecondLevel.getQueryType(), companySecondLevel.getQueryTime());
            companySecondLevel.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            companySecondLevel.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            companySecondLevel.setQueryType(ReportUtils.queryTypeDowngrade(companySecondLevel.getQueryType()));
        } else {
            companySecondLevel.formatParseTime(companySecondLevel.getQueryType(), "1d");
        }
    }

    /**
     * 资源分布
     * @param secondLevelDomainTopN
     * @return
     * @throws ParseException
     */
    public JSONObject findResourceReport(PopularCompanySecondLevel secondLevelDomainTopN) throws ParseException {
        secondLevelDomainTopN.formatParseTime(secondLevelDomainTopN.getQueryType(), "1d");
        if (ObjectUtil.equals(secondLevelDomainTopN.getStatisticsWay(),"every")) {
            checkFindQueryTimeParam(secondLevelDomainTopN);
        }
        List<PopularCompanySecondLevel> dataList = secondLevelDomainTopNMapper.findTrendReportGroupByIspByParam(secondLevelDomainTopN);
        setUnknownLast(dataList);
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (PopularCompanySecondLevel param : dataList) {
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

    private BigInteger resourceReportCountIspParseCntAndOther(List<PopularCompanySecondLevel> dataList, Map<String, BigInteger> resultDataMap, BigInteger otherParseCnt) {
        int index = 0;
        for (PopularCompanySecondLevel param : dataList) {
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

    private void checkFindResourceReportParam(PopularCompanySecondLevel websiteSecondLevelDomain) throws ParseException {
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
     * @param companySecondLevel
     * @return
     * @throws ParseException
     */
    public JSONObject findParseReport(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        checkFindParseReport(companySecondLevel);
        companySecondLevel.formatParseTime(companySecondLevel.getQueryType(), "1d");
        List<PopularCompanySecondLevel> dataList = secondLevelDomainTopNMapper.queryLastTimeDataGroupByWebsiteByParam(companySecondLevel);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(companySecondLevel.getStartTime(), companySecondLevel.getEndTime(), companySecondLevel.getQueryType());
        //总数结果集,成功结果集
        List CDNParseResult = Lists.newArrayList();
        List IDCParseResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (PopularCompanySecondLevel domainWebsiteDetail : dataList) {
                CDNParseResult.add(domainWebsiteDetail.getCdnParseTotalCnt());
                IDCParseResult.add(domainWebsiteDetail.getIdcParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularCompanySecondLevel> collect = dataList.stream().collect(Collectors.toMap(PopularCompanySecondLevel::getParseTime, PopularCompanySecondLevel -> PopularCompanySecondLevel));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularCompanySecondLevel domainWebsiteDetail = collect.get(xKey);
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

    private void checkFindParseReport(PopularCompanySecondLevel companySecondLevel) throws ParseException {
        if (StrUtil.isNotEmpty(companySecondLevel.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(companySecondLevel.getQueryType(), companySecondLevel.getQueryTime());
            companySecondLevel.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            companySecondLevel.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            companySecondLevel.setQueryType(ReportUtils.queryTypeDowngrade(companySecondLevel.getQueryType()));
        }
    }

    public List<PopularCompanySecondLevel> downloadByParam(PopularCompanySecondLevel companySecondLevel) {
        checkDownloadByParamMethodParam(companySecondLevel);
        List<PopularCompanySecondLevel> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(companySecondLevel.getStatisticsWay(),"all")) {
            dataList = secondLevelDomainTopNMapper.queryDataByTimeParamToDownload(companySecondLevel);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(companySecondLevel.getStartTime() + "~" + companySecondLevel.getEndTime());
            });
        } else {
            dataList = secondLevelDomainTopNMapper.queryDataByQueryTimeParamToDownload(companySecondLevel);
            dataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        return dataList;
    }

    private void checkDownloadByParamMethodParam(PopularCompanySecondLevel companySecondLevel) {
        companySecondLevel.formatParseTime(companySecondLevel.getQueryType(), "1d");
        companySecondLevel.setLimit(10000L);
        companySecondLevel.setOffset(0L);
    }

    private void setUnknownLast(List<PopularCompanySecondLevel> dataList){
        for (PopularCompanySecondLevel websiteSecondLevelDomainTopN : dataList) {
            if("未知".equals(websiteSecondLevelDomainTopN.getIsp())){
                PopularCompanySecondLevel data = websiteSecondLevelDomainTopN;
                dataList.remove(websiteSecondLevelDomainTopN);
                dataList.add(data);
                break;
            }
        }
    }


    /**
     * 服务IP数据.
     *
     * @param queryParam
     * @return
     */
    public PageResult nodeServerDetail(PopularCompanySecondLevel queryParam) throws ParseException {
        Long total = 0L;
        List<SecondDomainServerVO> resourceDomainTopnDetails = Lists.newArrayList();
        if (ObjectUtil.equals(queryParam.getStatisticsWay(),"every")) {
            checkFindQueryTimeParam(queryParam);
        }
        // 若域名为空,则返回空数据
        if (null == queryParam.getSecondLevelDomain() || queryParam.getSecondLevelDomain().isEmpty()) {
            return new PageResult(total, resourceDomainTopnDetails);
        }
//        checkFindRateReportParam(queryParam);
        queryParam.formatParseTime(queryParam.getQueryType(), "1d");
        total = secondLevelDomainTopNMapper.nodeServerDetailCount(queryParam);
        resourceDomainTopnDetails = secondLevelDomainTopNMapper.nodeServerDetail(queryParam);
        if (ObjectUtil.isNull(total)){
            total = 0L;
        }
        PageResult pageResult = new PageResult(total, resourceDomainTopnDetails);
        return pageResult;
    }
}
