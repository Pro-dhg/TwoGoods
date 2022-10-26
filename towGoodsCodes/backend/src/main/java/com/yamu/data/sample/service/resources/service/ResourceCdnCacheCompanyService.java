package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.CdnTopNServiceCompanyParam;
import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheCompanyDetail;
import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheCompanyReport;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessTopNServiceCompanyList;
import com.yamu.data.sample.service.resources.entity.vo.CdnBusinessTopNServiceDomainList;
import com.yamu.data.sample.service.resources.mapper.CdnTopNServiceMapper;
import com.yamu.data.sample.service.resources.mapper.ResourceCdnCacheCompanyMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.yamu.data.sample.service.common.util.ReportUtils.REPORT_NAME;
import static com.yamu.data.sample.service.common.util.ReportUtils.XAXIS;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/20
 * @DESC
 */
@Service
@Slf4j
public class ResourceCdnCacheCompanyService {

    @Autowired
    private ResourceCdnCacheCompanyMapper  cdnCacheCompanyMapper;

    private final static String DEFAULT_INTERVAL_TYPE = "1d";

    private final static String DEFAULT_QUERY_TYPE = "1h";

    @Autowired
    private CdnTopNServiceMapper cdnTopNServiceMapper;


    public ResponseEntity cdnAnalysis(ResourceCdnCacheCompany cdnCacheCompany) throws Exception {
        checkFindTrendListParam(cdnCacheCompany);
        List<ResourceCdnCacheCompany> list = Lists.newArrayList();
        if (ObjectUtil.equals(cdnCacheCompany.getStatisticsWay(),"all")) {
            List<ResourceCdnCacheCompany> tableList = cdnCacheCompanyMapper.findTableDataByParamAll(cdnCacheCompany);
            if (tableList.size() > 0) {
                List<String> queryList = tableList.stream().map(item -> item.getBusiness()).collect(Collectors.toList());
                list = cdnCacheCompanyMapper.cdnAnalysisByParamAll(cdnCacheCompany, queryList);
            }
        } else{
            list = cdnCacheCompanyMapper.cdnAnalysisByParamEvery(cdnCacheCompany);
        }
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(cdnCacheCompany.getStartTime(), cdnCacheCompany.getEndTime(), cdnCacheCompany.getQueryType());

        //展示种类
        Map<String, String> legend = Maps.newLinkedHashMap();
        Map<String, List> dataMap = Maps.newLinkedHashMap();
        for (ResourceCdnCacheCompany res : list) {
            legend.put(ReportUtils.LINE + res.getBusiness(), res.getBusiness());
            dataMap.put(ReportUtils.LINE + res.getBusiness(), null);
        }
        for (Map.Entry<String, String> entry : legend.entrySet()) {
            List<BigInteger> listAll = new ArrayList();
            for (Map.Entry<Date, String> datelist : xAxisMap.entrySet()) {
                Map<String, BigInteger> collect = list.stream().filter(e -> {
                    Date key = datelist.getKey();
                    return ObjectUtil.equals(e.getParseTime(),key);
                }).collect(Collectors.toMap(ResourceCdnCacheCompany::getBusiness, ResourceCdnCacheCompany::getCdnParseTotalCnt));
                BigInteger aLong = collect.get(entry.getValue());
                if (aLong == null) {
                    aLong = BigInteger.valueOf(0);
                }
                listAll.add(aLong);
            }
            dataMap.put(ReportUtils.LINE + entry.getValue(), listAll);
        }




        // 报表名称

        String reportName = "CDN调度趋势";
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        finalReport.put(REPORT_NAME, reportName);
        finalReport.put(XAXIS, xAxisMap);
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return ResponseEntity.ok(finalResult);

    }

    public ResponseEntity cacheAnalysis(ResourceCdnCacheCompany parmer) throws Exception {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheCompany> list = new ArrayList<>();
        if (ObjectUtil.equals(parmer.getStatisticsWay(),"all")) {
            List<ResourceCdnCacheCompany> tableList = cdnCacheCompanyMapper.findTableDataByParamAll(parmer);
            if (tableList.size() > 0) {
                List<String> queryList = tableList.stream().map(item -> item.getBusiness()).collect(Collectors.toList());
                list = cdnCacheCompanyMapper.cdnAnalysisByParamAll(parmer, queryList);
            }
        } else{
            list = cdnCacheCompanyMapper.cdnAnalysisByParamEvery(parmer);
        }

        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());

        //展示种类
        Map<String, String> legend = Maps.newLinkedHashMap();
        Map<String, List> dataMap = Maps.newLinkedHashMap();
        for (ResourceCdnCacheCompany res : list) {
            legend.put(ReportUtils.LINE + res.getBusiness(), res.getBusiness());
        }
        for (Map.Entry<String, String> entry : legend.entrySet()) {
            List<BigInteger> listAll = new ArrayList();
            for (Map.Entry<Date, String> data : xAxisMap.entrySet()) {
                Map<String, BigInteger> collect = list.stream().filter(e -> {
                    Date key = data.getKey();
                    return ObjectUtil.equals(e.getParseTime(),key);
                }).collect(Collectors.toMap(ResourceCdnCacheCompany::getBusiness, ResourceCdnCacheCompany::getCacheParseTotalCnt));
                //当前域名所在的时间 解析量
                BigInteger aLong = collect.get(entry.getValue());
                if (aLong == null) {
                    aLong = BigInteger.valueOf(0);
                }
                listAll.add(aLong);
            }
            dataMap.put(ReportUtils.LINE + entry.getValue(), listAll);
        }

        // 报表名称

        String reportName = "CACHE调度趋势";
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        finalReport.put(REPORT_NAME, reportName);
        finalReport.put(XAXIS, xAxisMap);
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return ResponseEntity.ok(finalResult);

    }

    public JSONObject findRateReport(ResourceCdnCacheCompany parmer) throws YamuException, ParseException {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheCompany> list = new ArrayList<>();
        list = cdnCacheCompanyMapper.findRateReport(parmer);
        list.stream().forEach(ResourceCdnCacheCompany::buildRate);
        List netInRateList = new ArrayList<>();
        List parseInRateList = new ArrayList<>();
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());
//        for (Map.Entry<Date, String> date : xAxisMap.entrySet()) {
//            List<ResourceCdnCacheCompany> rate = list.stream().filter(e -> e.getParseTime().equals(date.getKey())).collect(Collectors.toList());
//            if (rate == null || rate.size() == 0) {
//                netInRateList.add(0);
//                parseInRateList.add(0);
//            } else {
//                netInRateList.add(rate.get(0).getNetInRate());
//                parseInRateList.add(rate.get(0).getWithinRate());
//            }
//        }
//        list.stream().forEach(item -> xAxisMap.remove(item));
        if (xAxisMap.size() == list.size()) {
            for (ResourceCdnCacheCompany companyTrend : list) {
                netInRateList.add(companyTrend.getNetInRate());
                parseInRateList.add(companyTrend.getWithinRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = com.google.common.collect.Lists.newArrayList();
            Map<Date, ResourceCdnCacheCompany> collect = list.stream().collect(Collectors.toMap(ResourceCdnCacheCompany::getParseTime, ResourceCdnCacheCompany -> ResourceCdnCacheCompany));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                ResourceCdnCacheCompany companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    netInRateList.add(companyDetail.getNetInRate());
                    parseInRateList.add(companyDetail.getWithinRate());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }

        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "netInRateResult", netInRateList);
        dataMap.put(ReportUtils.LINE + "parseInRateResult", parseInRateList);

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


    public JSONObject findParseReport(ResourceCdnCacheCompany parmer) throws YamuException, ParseException {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheCompany> list = cdnCacheCompanyMapper.findParseReport(parmer);
        List<BigInteger> netInParseList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> netOutParseList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> cdnParseList = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> cacheParseList = org.apache.commons.compress.utils.Lists.newArrayList();
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());
        if (xAxisMap.size() == list.size()) {
            for (ResourceCdnCacheCompany companyTrend : list) {
                netInParseList.add(companyTrend.getNetInParseTotalCnt());
                netOutParseList.add(companyTrend.getNetOutParseTotalCnt());
                cdnParseList.add(companyTrend.getCdnParseTotalCnt());
                cacheParseList.add(companyTrend.getCacheParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = com.google.common.collect.Lists.newArrayList();
            Map<Date, ResourceCdnCacheCompany> collect = list.stream().collect(Collectors.toMap(ResourceCdnCacheCompany::getParseTime, ResourceCdnCacheCompany -> ResourceCdnCacheCompany));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                ResourceCdnCacheCompany companyDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(companyDetail)) {
                    netInParseList.add(companyDetail.getNetInParseTotalCnt());
                    netOutParseList.add(companyDetail.getNetOutParseTotalCnt());
                    cdnParseList.add(companyDetail.getCdnParseTotalCnt());
                    cacheParseList.add(companyDetail.getCacheParseTotalCnt());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }



        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "netInParseResult", netInParseList);
        dataMap.put(ReportUtils.LINE + "netOutParseResult", netOutParseList);
        dataMap.put(ReportUtils.LINE + "cdnParseResult", cdnParseList);
        dataMap.put(ReportUtils.LINE + "cacheParseResult", cacheParseList);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "netInParseResult", "网内次数");
        legend.put(ReportUtils.LINE + "netOutParseResult", "出网次数");
        legend.put(ReportUtils.LINE + "cdnParseResult", "CDN次数");
        legend.put(ReportUtils.LINE + "cacheParseResult", "CACHE次数");


        // 报表名称
        String reportName = "解析次数趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }


    public PageResult cdnCacheDetail(ResourceCdnCacheCompany cdnCacheCompany) throws YamuException,ParseException {
        checkFindTrendListParam(cdnCacheCompany);
        Long total = 0L;
        List<ResourceCdnCacheCompany> dataList = Lists.newArrayList();
        if (ObjectUtil.equals(cdnCacheCompany.getStatisticsWay(),"all")) {
            total = cdnCacheCompanyMapper.countFindTableDataByParamAll(cdnCacheCompany);
            dataList = cdnCacheCompanyMapper.findTableDataByParamAll(cdnCacheCompany);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(cdnCacheCompany.getStartTime() + "~" + cdnCacheCompany.getEndTime());
            });
        } else if (ObjectUtil.equals(cdnCacheCompany.getStatisticsWay(),"every")) {
            total = cdnCacheCompanyMapper.countFindTableDataByParamEvery(cdnCacheCompany);
            dataList = cdnCacheCompanyMapper.findTableDataByParamEvery(cdnCacheCompany);
            dataList.stream().forEach(domainNameWebsite -> {
                domainNameWebsite.setTimeRange(DateUtils.formatDataToString(domainNameWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.stream().forEach(ResourceCdnCacheCompany::buildRate);
        dataList.stream().forEach(ResourceCdnCacheCompany::buildIcpRate);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public JSONObject findtopNServiceReport(ResourceCdnCacheCompany parmer) throws YamuException, ParseException {

        parmer.setQueryCdnTable(getCdnTable(parmer));

        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheCompanyReport> list = cdnCacheCompanyMapper.findtopNServiceReport(parmer);
        List<String> xAxis = new ArrayList<>();
        List<String> websiteAppName = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> cdnParseTotalCnt = org.apache.commons.compress.utils.Lists.newArrayList();

        for (ResourceCdnCacheCompanyReport companyTrend : list) {
            websiteAppName.add(companyTrend.getWebsiteAppName());
            cdnParseTotalCnt.add(companyTrend.getCdnParseTotalCnt());
            xAxis.add(companyTrend.getWebsiteAppName());
        }

        Map<String, List> dataMap = Maps.newHashMap();
//        dataMap.put(ReportUtils.LINE + "websiteAppName", websiteAppName);
        dataMap.put(ReportUtils.BAR + "total", cdnParseTotalCnt);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
//        legend.put(ReportUtils.LINE + "websiteAppName", "网站名");
        legend.put(ReportUtils.BAR + "total", "CDN域名解析次数");

        // 报表名称
        String reportName = "TopN服务网站";

        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public PageResult topNServiceDetail(ResourceCdnCacheCompany cdnCacheCompany) throws YamuException,ParseException {
        cdnCacheCompany.setQueryCdnTable(getCdnTable(cdnCacheCompany));
        checkFindTrendListParam(cdnCacheCompany);
        Long total = cdnCacheCompanyMapper.countTopNServiceDetail(cdnCacheCompany);
        List<ResourceCdnCacheCompanyDetail> dataList = cdnCacheCompanyMapper.topNServiceDetail(cdnCacheCompany);
        for(ResourceCdnCacheCompanyDetail data : dataList){
            data.setParseTime(cdnCacheCompany.getStartTime() + "~" + cdnCacheCompany.getEndTime());
        }
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }


    public void download(ResourceCdnCacheCompany cdnCacheCompany, HttpServletResponse response) throws Exception {
        checkFindTrendListParam(cdnCacheCompany);
        cdnCacheCompany.setLimit(20000L);
        List<ResourceCdnCacheCompany> dataList = new ArrayList<>();
        if(ObjectUtil.equals(cdnCacheCompany.getStatisticsWay(),"all")) {
            dataList = cdnCacheCompanyMapper.findTableDataByParamAll(cdnCacheCompany);
        } else {
            dataList = cdnCacheCompanyMapper.findTableDataByParamEvery(cdnCacheCompany);
        }
        dataList.stream().forEach(e -> {
            e.buildRate();
            e.buildIcpRate();
            if (ObjectUtil.isEmpty(e.getBusiness())) {
                e.setBusiness("未知");
            }
        });

        //获取当前本省运营商
        String nowIsp = cdnCacheCompany.getBusinessIsp();
        //统计方式
        String statisticsMethod = cdnCacheCompany.getStatisticsWay();
        //统计开始时间
        String startTime = cdnCacheCompany.getStartTime();
        //统计结束时间
        String endTime = cdnCacheCompany.getEndTime();
        List<String> csvLines = dataList.stream().map(e -> e.getCsvLineSting(statisticsMethod, nowIsp, startTime, endTime)).collect(Collectors.toList());
        CsvUtils.exportCsv(ResourceCdnCacheCompany.CSV_NAME, cdnCacheCompany.getStartTime(), cdnCacheCompany.getEndTime(),
                ResourceCdnCacheCompany.getCsvHead(nowIsp), csvLines, response);
    }


    private void checkFindTrendListParam(ResourceCdnCacheCompany cdnCacheCompany) throws YamuException,ParseException {
        cdnCacheCompany.formatParseTime();
        ValidationResult validationResult = ValidationUtils.validateEntity(cdnCacheCompany);
        if (validationResult.isHasErrors()) {
            log.error(">>ResourceCdnCacheCompany checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
        if(StrUtil.isNotEmpty(cdnCacheCompany.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(cdnCacheCompany.getQueryType(), cdnCacheCompany.getQueryTime());
            cdnCacheCompany.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            cdnCacheCompany.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            cdnCacheCompany.setQueryType(ReportUtils.queryTypeDowngrade(cdnCacheCompany.getQueryType()));
        } else {
            cdnCacheCompany.formatParseTime(cdnCacheCompany.getQueryType(), "1d");
        }
//        String domainName = cdnCacheCompany.getBusiness();
//        cdnCacheCompany.setBusiness(ReportUtils.escapeChar(domainName));
    }

    private String getCdnTable(ResourceCdnCacheCompany parmer) {
        String tableName ;
//        //选择了topn 就从带topn后缀的那张表取数据
//        if (parmer.getRankNumber()!=null ){
//            tableName=getTopNTableName(parmer.getIntervalType());
//        } else {
            tableName=getCdnTableName(parmer.getQueryType());
//        }
        return tableName;
    }

    private String getCdnTableName(String queryType){
//        return "rpt_resource_business_cdn_cache_for_website_" + queryType;
        return "rpt_resource_business_cdn_cache_for_website_" + queryType;
    }

    private String getTopNTableName(String intervalType){
//        return "rpt_resource_business_cdn_cache_for_website_topn_" + intervalType;
        return "rpt_resource_domain_cdn_cache_for_website_topn_" + intervalType;
    }

    public com.yamu.data.sample.service.common.entity.PageResult<CdnBusinessTopNServiceCompanyList> topNServiceCompany(CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam){
        String tableName = getCdnTableName(cdnTopNServiceCompanyParam.getQueryType());
        Long total = cdnTopNServiceMapper.cdnBusinessTopNServiceCompanyTotal(cdnTopNServiceCompanyParam,tableName);
        List<TopNServiceData> dataList = cdnTopNServiceMapper.cdnBusinessTopNServiceCompanyList(cdnTopNServiceCompanyParam,tableName);
        TopNServiceData sumData = cdnTopNServiceMapper.cdnBusinessTopNServiceCompanySumData(cdnTopNServiceCompanyParam,tableName);
        List<CdnBusinessTopNServiceCompanyList> list = new ArrayList<>();
        for(TopNServiceData data : dataList){
            CdnBusinessTopNServiceCompanyList cdnBusinessTopNServiceCompanyList = BeanUtil.copyProperties(data,CdnBusinessTopNServiceCompanyList.class);
            cdnBusinessTopNServiceCompanyList.setParseTime(cdnTopNServiceCompanyParam.getStartTime() + "~" + cdnTopNServiceCompanyParam.getEndTime());
            cdnBusinessTopNServiceCompanyList.setRate(ReportUtils.buildRatioBase(data.getParseTotalCnt(), sumData.getParseTotalCnt()));
            cdnBusinessTopNServiceCompanyList.setNetInRate(ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()));
            list.add(cdnBusinessTopNServiceCompanyList);
        }
        return new com.yamu.data.sample.service.common.entity.PageResult<>(total, list);
    }

    public com.yamu.data.sample.service.common.entity.PageResult<CdnBusinessTopNServiceDomainList> topNServiceDomain(CdnTopNServiceCompanyParam cdnTopNServiceCompanyParam){
        String tableName = getCdnTableName(cdnTopNServiceCompanyParam.getQueryType());
        Long total = cdnTopNServiceMapper.cdnusinessTopNServiceDomainTotal(cdnTopNServiceCompanyParam,tableName);
        List<TopNServiceData> dataList = cdnTopNServiceMapper.cdnusinessTopNServiceDomainList(cdnTopNServiceCompanyParam,tableName);
        TopNServiceData sumData = cdnTopNServiceMapper.cdnusinessTopNServiceDomainSumData(cdnTopNServiceCompanyParam,tableName);
        List<CdnBusinessTopNServiceDomainList> list = new ArrayList<>();
        for(TopNServiceData data : dataList){
            CdnBusinessTopNServiceDomainList cdnBusinessTopNServiceDomainList = BeanUtil.copyProperties(data,CdnBusinessTopNServiceDomainList.class);
            cdnBusinessTopNServiceDomainList.setParseTime(cdnTopNServiceCompanyParam.getStartTime() + "~" + cdnTopNServiceCompanyParam.getEndTime());
            cdnBusinessTopNServiceDomainList.setRate(ReportUtils.buildRatioBase(data.getParseTotalCnt(), sumData.getParseTotalCnt()));
            cdnBusinessTopNServiceDomainList.setNetInRate(ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()));
            list.add(cdnBusinessTopNServiceDomainList);
        }
        return new com.yamu.data.sample.service.common.entity.PageResult<>(total, list);
    }
}
