package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.BusinessUtils;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.*;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheCompany;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheDomain;
import com.yamu.data.sample.service.resources.entity.po.TopNServiceData;
import com.yamu.data.sample.service.resources.entity.vo.CdnTopNServiceCompanyList;
import com.yamu.data.sample.service.resources.entity.vo.CdnTopNServiceDomainList;
import com.yamu.data.sample.service.resources.mapper.CdnCacheDomainMapper;
import com.yamu.data.sample.service.resources.mapper.CdnTopNServiceMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yamu.data.sample.service.common.util.ReportUtils.*;

@Service
@Slf4j
public class CdnCacheDomainService {
    @Autowired
    private CdnCacheDomainMapper cdnCacheDomainMapper;

    private final static String DEFAULT_INTERVAL_TYPE = "1d";

    private final static String DEFAULT_QUERY_TYPE = "1h";

    private static final String RESOURCE_DOMAIN_CDN_CACHE_TABLE_PREFIX = "rpt_resource_domain_cdn_cache";

    @Autowired
    CdnTopNServiceMapper cdnTopNServiceMapper;

    public ResponseEntity cdnAnalysis(ResourceCdnCacheDomain parmer) throws Exception {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheDomain> list = new ArrayList<>();
        if (parmer.getStatisticsWay().equals("all")) {
            //获取分页域名
            List<ResourceCdnCacheDomain> tableList = cdnCacheDomainMapper.findDetailData(parmer, new ArrayList<>());
            if (tableList.size() > 0) {
                List<String> topDomainName = tableList.stream().map(item -> item.getDomainName()).collect(Collectors.toList());
                list = cdnCacheDomainMapper.cdnCacheAnalysis(parmer, topDomainName);
            }
        } else if (parmer.getStatisticsWay().equals("every")) {
            list = cdnCacheDomainMapper.cdnCacheAnalysisEvery(parmer);
        }

        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());

        //展示种类
        Map<String, String> legend = Maps.newLinkedHashMap();
        Map<String, List> dataMap = Maps.newLinkedHashMap();
        //去掉没有数据的时间点
        List<Date> removeList = new ArrayList<>();
        for (ResourceCdnCacheDomain res : list) {
            legend.put(ReportUtils.LINE + res.getDomainName(), res.getDomainName());
            dataMap.put(ReportUtils.LINE + res.getDomainName(), new ArrayList());
        }
        for (Map.Entry<String, String> entry : legend.entrySet()) {
            List<BigInteger> listAll = new ArrayList();
            for (Map.Entry<Date, String> datelist : xAxisMap.entrySet()) {
                Map<String, BigInteger> collect = list.stream().filter(e -> {
                    Date key = datelist.getKey();
                    return ObjectUtil.equals(e.getParseTime(),key);
                }).collect(Collectors.toMap(ResourceCdnCacheDomain::getDomainName, ResourceCdnCacheDomain::getCdnParseTotalCnt));
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

    public ResponseEntity cacheAnalysis(ResourceCdnCacheDomain parmer) throws Exception {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheDomain> list = new ArrayList<>();
        if (parmer.getStatisticsWay().equals("all")) {
            //获取分页域名
            List<ResourceCdnCacheDomain> tableList = cdnCacheDomainMapper.findDetailData(parmer, new ArrayList<>());
            if (tableList.size() > 0) {
                List<String> topDomainName = tableList.stream().map(item -> item.getDomainName()).collect(Collectors.toList());
                list = cdnCacheDomainMapper.cdnCacheAnalysis(parmer, topDomainName);
            }
        } else if (parmer.getStatisticsWay().equals("every")) {
            list = cdnCacheDomainMapper.cdnCacheAnalysisEvery(parmer);
        }


        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());

        //展示种类
        Map<String, String> legend = Maps.newLinkedHashMap();
        Map<String, List> dataMap = Maps.newLinkedHashMap();
        //去掉没有数据的时间点
        List<Date> removeList = new ArrayList<>();

        for (ResourceCdnCacheDomain res : list) {
            legend.put(ReportUtils.LINE + res.getDomainName(), res.getDomainName());
            dataMap.put(ReportUtils.LINE + res.getDomainName(), new ArrayList());
        }
        for (Map.Entry<String, String> entry : legend.entrySet()) {
            List<BigInteger> listAll = new ArrayList();
            for (Map.Entry<Date, String> data : xAxisMap.entrySet()) {
                Map<String, BigInteger> collect = list.stream().filter(e -> {
                    Date key = data.getKey();
                    return ObjectUtil.equals(e.getParseTime(),key);
                }).collect(Collectors.toMap(ResourceCdnCacheDomain::getDomainName, ResourceCdnCacheDomain::getCacheParseTotalCnt));
                //当前域名所在的时间 解析量
                BigInteger aLong = collect.get(entry.getValue());
                if (aLong == null) {
                    aLong = BigInteger.valueOf(0);
                }
                listAll.add(aLong);
            }
            dataMap.put(ReportUtils.LINE + entry.getValue(), listAll);
        }

        removeList.stream().forEach(item -> xAxisMap.remove(item));

        // 报表名称

        String reportName = "CACHE调度趋势";
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        finalReport.put(REPORT_NAME, reportName);
        finalReport.put(XAXIS, xAxisMap);
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return ResponseEntity.ok(finalResult);

    }

    public JSONObject findRateReport(ResourceCdnCacheDomain parmer) throws YamuException, ParseException {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheDomain> list = new ArrayList<>();
        if (parmer.getStatisticsWay().equals("all")) {
//            getQueryType(parmer);
            list = cdnCacheDomainMapper.findRateReport(parmer);
        } else if (parmer.getStatisticsWay().equals("every")) {
            if (parmer.getQueryTime() != null && parmer.getQueryTime() != "") {
                //开始时间参数需大于 parse_time
                getTimeQueryType(parmer);
                list = cdnCacheDomainMapper.findRateReport(parmer);
            }
        }

        list.stream().forEach(ResourceCdnCacheDomain::buildRate);
        List netInRateList = new ArrayList<>();
        List parseInRateList = new ArrayList<>();
        List<Date> removeList = new ArrayList<>();
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());
        for (Map.Entry<Date, String> date : xAxisMap.entrySet()) {
            List<ResourceCdnCacheDomain> rate = list.stream().filter(e -> e.getParseTime().equals(date.getKey())).collect(Collectors.toList());
            if (rate == null || rate.size() == 0) {
                removeList.add(date.getKey());
            } else {
                netInRateList.add(rate.get(0).getNetInRate());
                parseInRateList.add(rate.get(0).getWithinRate());
            }
        }
        removeList.stream().forEach(item -> xAxisMap.remove(item));
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


    public JSONObject findParseReport(ResourceCdnCacheDomain parmer) throws YamuException, ParseException {
        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheDomain> list = new ArrayList<>();
        if (parmer.getStatisticsWay().equals("all")) {
//            getQueryType(parmer);
            list = cdnCacheDomainMapper.findParseReport(parmer);
        } else if (parmer.getStatisticsWay().equals("every")) {
            if (parmer.getQueryTime() != null && parmer.getQueryTime() != "") {
                //开始时间参数需大于 parse_time
                getTimeQueryType(parmer);
                list = cdnCacheDomainMapper.findParseReport(parmer);
            }
        }
        List netInParseList = new ArrayList<>();
        List netOutParseList = new ArrayList<>();
        List cdnParseList = new ArrayList<>();
        List cacheParseList = new ArrayList<>();
        List<Date> removeList = new ArrayList<>();
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(parmer.getStartTime(), parmer.getEndTime(), parmer.getQueryType());
        for (Map.Entry<Date, String> date : xAxisMap.entrySet()) {
            List<ResourceCdnCacheDomain> rate = list.stream().filter(e -> e.getParseTime().equals(date.getKey())).collect(Collectors.toList());
            if (rate == null || rate.size() == 0) {
                removeList.add(date.getKey());
            } else {
                netInParseList.add(rate.get(0).getNetInParseTotalCnt());
                netOutParseList.add(rate.get(0).getNetOutParseTotalCnt());
                cdnParseList.add(rate.get(0).getCdnParseTotalCnt());
                cacheParseList.add(rate.get(0).getCacheParseTotalCnt());
            }
        }
        removeList.stream().forEach(item -> xAxisMap.remove(item));
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


    public PageResult cdnCacheDetail(ResourceCdnCacheDomain parmer) throws YamuException {
        checkFindTrendListParam(parmer);
        PageResult pageResult = new PageResult();
        //topN 域名
        List<String> topDomainName = new ArrayList<>();
        if (parmer.getStatisticsWay().equals("all")) {
//            if (parmer.getRankNumber() != null && parmer.getRankNumber() != 0) {
//                topDomainName = cdnCacheDomainMapper.getTopN(parmer);
//            }
            Long count = cdnCacheDomainMapper.findDetailCount(parmer, topDomainName);
            List<ResourceCdnCacheDomain> list = cdnCacheDomainMapper.findDetailData(parmer, topDomainName);
            list.stream().forEach(ResourceCdnCacheDomain::buildRate);
            list.stream().forEach(ResourceCdnCacheDomain::buildIcpRate);
            pageResult.setTotal(count);
            pageResult.setData(list);
        } else if (parmer.getStatisticsWay().equals("every")) {
            Long count = cdnCacheDomainMapper.findTimeSizeCount(parmer);
            List<ResourceCdnCacheDomain> list = cdnCacheDomainMapper.findTimeSizeData(parmer);
            list.stream().forEach(ResourceCdnCacheDomain::buildRate);
            list.stream().forEach(ResourceCdnCacheDomain::buildIcpRate);
            list.stream().forEach(item -> {item.setTimeRange(DateUtils.formatDataToString(item.getParseTime(),DateUtils.DEFAULT_FMT));});
            pageResult.setTotal(count);
            pageResult.setData(list);
        }
        return pageResult;
    }

    public JSONObject findtopNServiceReport(ResourceCdnCacheDomain parmer) throws YamuException, ParseException {

        parmer.setQueryCdnTable(getCdnTable(parmer));

        checkFindTrendListParam(parmer);
        List<ResourceCdnCacheDomainReport> list = cdnCacheDomainMapper.findtopNServiceReport(parmer);
        List<String> xAxis = new ArrayList<>();
        List<String> websiteAppName = org.apache.commons.compress.utils.Lists.newArrayList();
        List<BigInteger> cdnParseTotalCnt = org.apache.commons.compress.utils.Lists.newArrayList();

        for (ResourceCdnCacheDomainReport companyTrend : list) {
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

    public PageResult topNServiceDetail(ResourceCdnCacheDomain cdnCacheCompany) throws YamuException{
        cdnCacheCompany.setQueryCdnTable(getCdnTable(cdnCacheCompany));
        checkFindTrendListParam(cdnCacheCompany);
        Long total = cdnCacheDomainMapper.countTopNServiceDetail(cdnCacheCompany);
        List<ResourceCdnCacheDomainDetail> dataList = cdnCacheDomainMapper.topNServiceDetail(cdnCacheCompany);
        for(ResourceCdnCacheDomainDetail data : dataList){
            data.setParseTime(cdnCacheCompany.getStartTime() + "~" + cdnCacheCompany.getEndTime());
        }
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public void download(ResourceCdnCacheDomain parmer, HttpServletResponse response) throws Exception {
        checkFindTrendListParam(parmer);
        parmer.setLimit(20000L);
        //topN 域名
        List<String> topDomainName = new ArrayList<>();
//        if (parmer.getRankNumber() != null && parmer.getRankNumber() != 0) {
//            topDomainName = cdnCacheDomainMapper.getTopN(parmer);
//        }
        List<ResourceCdnCacheDomain> dataList = new ArrayList<>();
        if (parmer.getStatisticsWay().equals("all")) {
            dataList = cdnCacheDomainMapper.findDetailData(parmer, topDomainName);
        } else if (parmer.getStatisticsWay().equals("every")) {
            dataList = cdnCacheDomainMapper.findTimeSizeData(parmer);
        }
        dataList.stream().forEach(e -> {
            e.buildRate();
            e.buildIcpRate();
            if (ObjectUtil.isEmpty(e.getDomainName())) {
                e.setDomainName("未知");
            }
        });

        //获取当前本省运营商
        String business = parmer.getBusinessIsp();
        //统计方式
        String statisticsMethod = parmer.getStatisticsWay();
        //统计开始时间
        String startTime = parmer.getStartTime();
        //统计结束时间
        String endTime = parmer.getEndTime();

        List<String> csvLines = dataList.stream().map(e -> e.getCsvLineSting(statisticsMethod, business, startTime, endTime)).collect(Collectors.toList());
        CsvUtils.exportCsv(ResourceCdnCacheDomain.CSV_NAME, ResourceCdnCacheDomain.getCsvHead(business), csvLines, response);
    }

    private void checkFindTrendListParam(ResourceCdnCacheDomain parm) throws YamuException {
        parm.formatParseTime();
        ValidationResult validationResult = ValidationUtils.validateEntity(parm);
        if (validationResult.isHasErrors()) {
            log.error(">>CdnCacheDomainService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
        String domainName = parm.getDomainName();
        parm.setDomainName(ReportUtils.escapeChar(domainName));
        parm.formatFieldToOther();
    }

    public void getTimeQueryType(ResourceCdnCacheDomain parmer) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date endDate = new Date();
        String endTime = "";
        Date date = format.parse(parmer.getQueryTime());
        String startTime = parmer.getQueryTime();
        calendar.setTime(date);
        switch (parmer.getQueryType()) {
            case ("1min"):
                parmer.setStartTime(startTime);
                parmer.setEndTime(startTime);
                parmer.setQueryType("1min");
                break;
            case ("10min"):
                calendar.add(Calendar.MINUTE, 9);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("1min");
                break;
            case ("1h"):
                calendar.add(Calendar.MINUTE, 59);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("10min");
                break;
            case ("1d"):
                calendar.add(Calendar.HOUR, 23);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("1h");
                break;
            case ("1w"):
                calendar.add(Calendar.DATE, 6);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("1d");
                break;
            case ("1m"):
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DATE, -1);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("1d");
                break;
            case ("1q"):
                calendar.add(Calendar.MONTH, 3);
                calendar.add(Calendar.DATE, -1);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("1w");
                break;
            case ("1y"):
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.DATE, -1);
                endDate = calendar.getTime();
                endTime = format.format(endDate);
                parmer.setStartTime(startTime);
                parmer.setEndTime(endTime);
                parmer.setQueryType("1m");
                break;
        }

    }

    private void getQueryType(ResourceCdnCacheDomain parmer) {
        switch (parmer.getQueryType()) {
            case ("10min"):
                parmer.setQueryType("1min");
                break;
            case ("1h"):
                parmer.setQueryType("10min");
                break;
            case ("1d"):
                parmer.setQueryType("1h");
                break;
            case ("1w"):
                parmer.setQueryType("1d");
                break;
            case ("1m"):
                parmer.setQueryType("1d");
                break;
            case ("1q"):
                parmer.setQueryType("1w");
                break;
            case ("1y"):
                parmer.setQueryType("1m");
                break;
        }
    }

    private String getCdnTable(ResourceCdnCacheDomain parmer) {
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
        return "rpt_resource_domain_cdn_cache_for_website_" + queryType;
    }

    private String getTopNTableName(String intervalType){
//        return "rpt_resource_business_cdn_cache_for_website_topn_" + intervalType;
        return "rpt_resource_domain_cdn_cache_for_website_topn_" + intervalType;
    }

    public com.yamu.data.sample.service.common.entity.PageResult<CdnTopNServiceCompanyList> topNServiceCompany(CdnTopNServiceParam cdnTopNServiceParam){
        String tableName = getCdnTableName(cdnTopNServiceParam.getQueryType());
        Long total = cdnTopNServiceMapper.cdnTopNServiceCompanyTotal(cdnTopNServiceParam,tableName);
        List<TopNServiceData> dataList = cdnTopNServiceMapper.cdnTopNServiceCompanyList(cdnTopNServiceParam,tableName);
        TopNServiceData sumData = cdnTopNServiceMapper.cdnTopNServiceCompanySumData(cdnTopNServiceParam,tableName);
        List<CdnTopNServiceCompanyList> list = new ArrayList<>();
        for(TopNServiceData data : dataList){
            CdnTopNServiceCompanyList cdnTopNServiceCompanyList = BeanUtil.copyProperties(data,CdnTopNServiceCompanyList.class);
            cdnTopNServiceCompanyList.setParseTime(cdnTopNServiceParam.getStartTime() + "~" + cdnTopNServiceParam.getEndTime());
            cdnTopNServiceCompanyList.setRate(ReportUtils.buildRatioBase(data.getParseTotalCnt(), sumData.getParseTotalCnt()));
            cdnTopNServiceCompanyList.setNetInRate(ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()));
            list.add(cdnTopNServiceCompanyList);
        }
        return new com.yamu.data.sample.service.common.entity.PageResult<>(total, list);
    }

    public com.yamu.data.sample.service.common.entity.PageResult<CdnTopNServiceDomainList> topNServiceDomain(CdnTopNServiceParam cdnTopNServiceParam){
        String tableName = getCdnTableName(cdnTopNServiceParam.getQueryType());
        Long total = cdnTopNServiceMapper.cdnTopNServiceDomainTotal(cdnTopNServiceParam,tableName);
        List<TopNServiceData> dataList = cdnTopNServiceMapper.cdnTopNServiceDomainList(cdnTopNServiceParam,tableName);
        TopNServiceData sumData = cdnTopNServiceMapper.cdnTopNServiceDomainSumData(cdnTopNServiceParam,tableName);
        List<CdnTopNServiceDomainList> list = new ArrayList<>();
        for(TopNServiceData data : dataList){
            CdnTopNServiceDomainList cdnTopNServiceDomainList = BeanUtil.copyProperties(data,CdnTopNServiceDomainList.class);
            cdnTopNServiceDomainList.setParseTime(cdnTopNServiceParam.getStartTime() + "~" + cdnTopNServiceParam.getEndTime());
            cdnTopNServiceDomainList.setRate(ReportUtils.buildRatioBase(data.getParseTotalCnt(), sumData.getParseTotalCnt()));
            cdnTopNServiceDomainList.setNetInRate(ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()));
            list.add(cdnTopNServiceDomainList);
        }
        return new com.yamu.data.sample.service.common.entity.PageResult<>(total, list);
    }

}
