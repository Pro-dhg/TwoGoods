package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainFlow;
import com.yamu.data.sample.service.resources.mapper.PopularDomainFlowMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author getiejun
 * @date 2021/8/11
 */
@Service
public class PopularDomainFlowService {

    @Autowired
    private PopularDomainFlowMapper popularDomainFlowMapper;

    private final String DOMAIN_FLOW_DEFAULT_ORDER_BY = "net_out_parse_flow_total desc";

    public PageResult findTableDetail(PopularDomainFlow popularDomainFlow) {
        checkFindTableDetailMethodParam(popularDomainFlow);
        popularDomainFlow.formatParseTime(popularDomainFlow.getQueryType(), "1d");
        Long total = popularDomainFlowMapper.countQueryGroupByDomainName(popularDomainFlow);
        List<PopularDomainFlow> dataList = popularDomainFlowMapper.queryGroupByDomainName(popularDomainFlow);
        formatTimeRange(dataList, popularDomainFlow.getStartTime(), popularDomainFlow.getEndTime());
        dataList.stream().forEach(PopularDomainFlow::buildRate);
        dataList.stream().forEach(PopularDomainFlow::buildConvertFlow);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkFindTableDetailMethodParam(PopularDomainFlow popularDomainFlow) {
        popularDomainFlow.formatParseTime(popularDomainFlow.getQueryType(), "1d");
        if(StrUtil.isEmpty(popularDomainFlow.getOrderBy())) {
            popularDomainFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
    }

    public JSONObject findFlowReport(PopularDomainFlow popularDomainFlow) {
        checkFindReportMethodParam(popularDomainFlow);
        List<PopularDomainFlow> dataList = popularDomainFlowMapper.queryGroupByParseTime(popularDomainFlow);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularDomainFlow.getStartTime(), popularDomainFlow.getEndTime(), popularDomainFlow.getQueryType());

        //总数结果集,成功结果集
        List parseFlowTotalResult = Lists.newArrayList();
        List netOutParseFlowTotalResult = Lists.newArrayList();
        List netInParseFlowTotalResult = Lists.newArrayList();
        if (xAxisMap.size() == dataList.size()) {
            for (PopularDomainFlow domainFlow : dataList) {
                parseFlowTotalResult.add(domainFlow.getParseFlowTotal());
                netOutParseFlowTotalResult.add(domainFlow.getNetOutParseFlowTotal());
                netInParseFlowTotalResult.add(domainFlow.getNetInParseFlowTotal());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularDomainFlow> collect = dataList.stream().collect(Collectors.toMap(PopularDomainFlow::getParseTime, PopularDomainFlow -> PopularDomainFlow));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularDomainFlow domainFlow = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainFlow)) {
                    parseFlowTotalResult.add(domainFlow.getParseFlowTotal());
                    netOutParseFlowTotalResult.add(domainFlow.getNetOutParseFlowTotal());
                    netInParseFlowTotalResult.add(domainFlow.getNetInParseFlowTotal());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "parseFlowTotalResult", parseFlowTotalResult);
        dataMap.put(ReportUtils.LINE + "netOutParseFlowTotalResult", netOutParseFlowTotalResult);
        dataMap.put(ReportUtils.LINE + "netInParseFlowTotalResult", netInParseFlowTotalResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "parseFlowTotalResult", "总流量");
        legend.put(ReportUtils.LINE + "netOutParseFlowTotalResult", "出网流量");
        legend.put(ReportUtils.LINE + "netInParseFlowTotalResult", "网内流量");

        // 报表名称
        String reportName = "热点域名流量趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    private void checkFindReportMethodParam(PopularDomainFlow popularDomainFlow) {
        popularDomainFlow.formatParseTime(popularDomainFlow.getQueryType(), "1d");
        if(StrUtil.isEmpty(popularDomainFlow.getOrderBy())) {
            popularDomainFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
    }

    public JSONObject findNetInReport(PopularDomainFlow popularDomainFlow) {
        checkFindReportMethodParam(popularDomainFlow);
        List<PopularDomainFlow> dataList = popularDomainFlowMapper.queryGroupByParseTime(popularDomainFlow);
        dataList.stream().forEach(PopularDomainFlow::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularDomainFlow.getStartTime(), popularDomainFlow.getEndTime(), popularDomainFlow.getQueryType());
        //总数结果集,成功结果集
        List successRateResult = Lists.newArrayList();
        List netInRateResult = Lists.newArrayList();
        List netOutRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        List parseOutRateResult = Lists.newArrayList();
        if (xAxisMap.size() == dataList.size()) {
            for (PopularDomainFlow domainFlow : dataList) {
                successRateResult.add(domainFlow.getSuccessRate());
                netInRateResult.add(domainFlow.getNetInRate());
                netOutRateResult.add(domainFlow.getNetOutRate());
                parseInRateResult.add(domainFlow.getParseInRate());
                parseOutRateResult.add(domainFlow.getParseOutRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularDomainFlow> collect = dataList.stream().collect(Collectors.toMap(PopularDomainFlow::getParseTime, PopularDomainFlow -> PopularDomainFlow));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularDomainFlow domainFlow = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainFlow)) {
                    successRateResult.add(domainFlow.getSuccessRate());
                    netInRateResult.add(domainFlow.getNetInRate());
                    netOutRateResult.add(domainFlow.getNetOutRate());
                    parseInRateResult.add(domainFlow.getParseInRate());
                    parseOutRateResult.add(domainFlow.getParseOutRate());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "successRateResult", successRateResult);
        dataMap.put(ReportUtils.LINE + "netInRateResult", netInRateResult);
        dataMap.put(ReportUtils.LINE + "netOutRateResult", netOutRateResult);
        dataMap.put(ReportUtils.LINE + "parseInRateResult", parseInRateResult);
        dataMap.put(ReportUtils.LINE + "parseOutRateResult", parseOutRateResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "successRateResult", "成功率");
        legend.put(ReportUtils.LINE + "netInRateResult", "本网率");
        legend.put(ReportUtils.LINE + "netOutRateResult", "出网率");
        legend.put(ReportUtils.LINE + "parseInRateResult", "本省率");
        legend.put(ReportUtils.LINE + "parseOutRateResult", "出省率");

        // 报表名称
        String reportName = "热点域名趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    public List<PopularDomainFlow> downloadByParam(PopularDomainFlow popularDomainFlow) {
        checkDownloadByParamMethodParam(popularDomainFlow);
        List<PopularDomainFlow> dataList = popularDomainFlowMapper.queryGroupByDomainName(popularDomainFlow);
        formatTimeRange(dataList, popularDomainFlow.getStartTime(), popularDomainFlow.getEndTime());
        return dataList;
    }

    private void checkDownloadByParamMethodParam(PopularDomainFlow popularDomainFlow) {
        popularDomainFlow.formatParseTime(popularDomainFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(popularDomainFlow.getOrderBy())) {
            popularDomainFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
        popularDomainFlow.setLimit(10000L);
        popularDomainFlow.setOffset(0L);
    }

    private void formatTimeRange (List<PopularDomainFlow> domainFlows, String startTime, String endTime) {
        domainFlows.stream().forEach(domainFlow -> {
            try {
                domainFlow.setTimeRange(DateUtils.formatDate(startTime, DateUtils.DEFAULT_DAY_FMT)
                        + "~" +
                        DateUtils.formatDate(endTime, DateUtils.DEFAULT_DAY_FMT));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> findTopDomain(PopularDomainFlow popularDomainFlow) {
        if(ObjectUtil.isEmpty(popularDomainFlow.getRankNumber())) {
            popularDomainFlow.setRankNumber(10L);
        }
        return popularDomainFlowMapper.findTopDomain(popularDomainFlow);
    }
}
