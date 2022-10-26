package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.FlowUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.PopularDomainCnameFlow;
import com.yamu.data.sample.service.resources.mapper.PopularDomainCnameFlowMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author wanghe
 * @Date 2021/8/24
 * @DESC
 */
@Service
@Slf4j
public class PopularDomainCnameFlowService {

    private static final String DOMAIN_FLOW_DEFAULT_ORDER_BY = "net_out_parse_flow_total desc";
    /**
     * 导出Excel sheet页名称.
     */
    private static final String[] SHEET_NAME = {"域名流量统计", "子域名流量统计"};
    /**
     * 域名sheet页表头数据
     */
    private static final String[] DOMAIN_COLUMS = {"时间", "域名","网站","公司", "别名", "下发状态", "出网流量(G)", "出网次数", "解析次数", "成功次数"};

    /**
     * 域名sheet页表头数据
     */
    private static final String[] CDOMAIN_COLUMS = {"时间", "域名", "别名", "下发状态", "出网流量(G)", "出网流量占比", "出网次数", "解析次数", "成功次数", "成功率", "总流量(G)", "出网率", "网内流量(G)", "本网次数", "本网率", "本省次数", "本省率", "外省次数", "出省率", "CDN次数", "IDC次数"};


    @Autowired
    private PopularDomainCnameFlowMapper popularDomainCnameFlowMapper;

    public PageResult findTableDetail(PopularDomainCnameFlow popularDomainCnameFlow) {
        checkFindTableDetailMethodParam(popularDomainCnameFlow);
        Long count = popularDomainCnameFlowMapper.queryCountGroupByDomainName(popularDomainCnameFlow);
        List<PopularDomainCnameFlow> dataList = popularDomainCnameFlowMapper.queryGroupByDomainName(popularDomainCnameFlow);
        dataList.forEach(pop -> {
            pop.setNetOutParseFlowTotal(FlowUtils.flowConvert(pop.getNetOutParseFlowTotal(), FlowUtils.B, FlowUtils.GB, 2));
        });
        formatTimeRange(dataList, popularDomainCnameFlow.getStartTime(), popularDomainCnameFlow.getEndTime());
        return PageResult.buildPageResult(count, dataList);
    }

    private void formatTimeRange(List<PopularDomainCnameFlow> dataList, String startTime, String endTime) {
        dataList.forEach(domainFlow -> {
            try {
                domainFlow.setTimeRange(DateUtils.formatDate(startTime, DateUtils.DEFAULT_DAY_FMT)
                        + "~" +
                        DateUtils.formatDate(endTime, DateUtils.DEFAULT_DAY_FMT));
            } catch (ParseException e) {
                log.error(e.toString());
            }
        });
    }

    private void checkFindTableDetailMethodParam(PopularDomainCnameFlow popularDomainCnameFlow) {
        popularDomainCnameFlow.formatParseTime(popularDomainCnameFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(popularDomainCnameFlow.getOrderBy())) {
            popularDomainCnameFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
    }

    /**
     * 出网流量.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    public JSONObject findFlowReport(PopularDomainCnameFlow popularDomainCnameFlow) {
        checkFindReportMethodParam(popularDomainCnameFlow);
        List<PopularDomainCnameFlow> dataList = popularDomainCnameFlowMapper.queryGroupByParseTime(popularDomainCnameFlow);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularDomainCnameFlow.getStartTime(), popularDomainCnameFlow.getEndTime(), popularDomainCnameFlow.getQueryType());
        //总数结果集,成功结果集
        List netOutParseFlowTotalResult = Lists.newArrayList();
        if (xAxisMap.size() == dataList.size()) {
            for (PopularDomainCnameFlow domainFlow : dataList) {
                netOutParseFlowTotalResult.add(domainFlow.getNetOutParseFlowTotal());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularDomainCnameFlow> collect = dataList.stream().collect(Collectors.toMap(PopularDomainCnameFlow::getParseTime, PopularDomainCnameFlow -> PopularDomainCnameFlow));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularDomainCnameFlow domainFlow = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(domainFlow)) {
                    netOutParseFlowTotalResult.add(domainFlow.getNetOutParseFlowTotal());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(item -> xAxisMap.remove(item));
        }
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "netOutParseFlowTotalResult", netOutParseFlowTotalResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "netOutParseFlowTotalResult", "出网流量");

        // 报表名称
        String reportName = "域名CNAME流量趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    private void checkFindReportMethodParam(PopularDomainCnameFlow popularDomainCnameFlow) {
        popularDomainCnameFlow.formatParseTime(popularDomainCnameFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(popularDomainCnameFlow.getOrderBy())) {
            popularDomainCnameFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
    }

    /**
     * 本网率趋势.
     *
     * @param popularDomainCnameFlow
     * @return
     */
    public JSONObject findNetInReport(PopularDomainCnameFlow popularDomainCnameFlow) {
        checkFindReportMethodParam(popularDomainCnameFlow);

        List<PopularDomainCnameFlow> dataList = popularDomainCnameFlowMapper.queryGroupByParseTime(popularDomainCnameFlow);
        dataList.forEach(PopularDomainCnameFlow::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(popularDomainCnameFlow.getStartTime(), popularDomainCnameFlow.getEndTime(), popularDomainCnameFlow.getQueryType());
        //总数结果集,成功结果集
        List successRateResult = Lists.newArrayList();
        List netInRateResult = Lists.newArrayList();
        List netOutRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        List parseOutRateResult = Lists.newArrayList();
        if (xAxisMap.size() == dataList.size()) {
            for (PopularDomainCnameFlow domainFlow : dataList) {
                successRateResult.add(domainFlow.getSuccessRate());
                netInRateResult.add(domainFlow.getNetInRate());
                netOutRateResult.add(domainFlow.getNetOutRate());
                parseInRateResult.add(domainFlow.getParseInRate());
                parseOutRateResult.add(domainFlow.getParseOutRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PopularDomainCnameFlow> collect = dataList.stream().collect(Collectors.toMap(PopularDomainCnameFlow::getParseTime, PopularDomainCnameFlow -> PopularDomainCnameFlow));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PopularDomainCnameFlow domainFlow = collect.get(xKey);
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
        String reportName = "域名CNAME域名趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    /**
     * 导出
     *
     * @param popularDomainCnameFlow
     * @param response
     */
    public void download(PopularDomainCnameFlow popularDomainCnameFlow, HttpServletResponse response) throws IOException {
        checkDownloadByParamMethodParam(popularDomainCnameFlow);
        List<PopularDomainCnameFlow> dataList = popularDomainCnameFlowMapper.queryGroupByDomainName(popularDomainCnameFlow);
        dataList.forEach(pop -> {
            pop.setNetOutParseFlowTotal(FlowUtils.flowConvert(pop.getNetOutParseFlowTotal(), FlowUtils.B, FlowUtils.GB, 2));
        });
        formatTimeRange(dataList, popularDomainCnameFlow.getStartTime(), popularDomainCnameFlow.getEndTime());

        /**
         * 1万条域名(最多)
         */
        List<String> domainNameList = dataList.stream().map(PopularDomainCnameFlow::getDomainName).collect(Collectors.toList());


        String timeInterval = popularDomainCnameFlow.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + popularDomainCnameFlow.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "域名CNAME流量报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();


        for (int index = 0; index < SHEET_NAME.length; index++) {
            writer.clearHeaderAlias();
            renameOrSetSheetName(index, SHEET_NAME[index], writer);
            List<JSONObject> sheetDateList = Lists.newArrayList();
            if (index == 0) {
                sheetDateList = dataList.stream().map(pop -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("时间", pop.getTimeRange());
                    jsonObject.put("域名", pop.getDomainName());
                    jsonObject.put("网站", pop.getWebsiteAppName());
                    jsonObject.put("公司", pop.getCompanyShortName());
                    jsonObject.put("别名", pop.getCname());
                    jsonObject.put("下发状态", pop.getDistributeState());
                    jsonObject.put("出网流量(G)", pop.getNetOutParseFlowTotal());
                    jsonObject.put("出网次数", pop.getNetOutParseTotalCnt());
                    jsonObject.put("解析次数", pop.getParseTotalCnt());
                    jsonObject.put("成功次数", pop.getParseSuccessCnt());
                    return jsonObject;
                }).collect(Collectors.toList());

                Arrays.stream(DOMAIN_COLUMS).forEach(
                        colum -> {
                            writer.addHeaderAlias(colum, colum);
                        }
                );
                // 设置数据
                writer.write(sheetDateList, true);
            }
            if (index == 1) {
                popularDomainCnameFlow.setLimit(null);
                popularDomainCnameFlow.setOffset(null);
                List<PopularDomainCnameFlow> cnameDataList = Lists.newArrayList();
                for (int i = 0; i < domainNameList.size(); i += 1000) {
                    String domainNames = "";
                    if (i + 1000 > domainNameList.size()) {
                        domainNames = String.join(";", domainNameList.subList(i, domainNameList.size()));
                    } else {
                        domainNames = String.join(";", domainNameList.subList(i, i + 1000));
                    }
                    cnameDataList = popularDomainCnameFlowMapper.queryCnameFlowByDomainName(popularDomainCnameFlow, domainNames);
                }

                formatTimeRange(cnameDataList, popularDomainCnameFlow.getStartTime(), popularDomainCnameFlow.getEndTime());
                cnameDataList.forEach(cnameFlow -> {
                    cnameFlow.buildRate();
                    cnameFlow.buildNetOutRate();
                    cnameFlow.buildConvertFlow();
                });

                sheetDateList = cnameDataList.stream().map(partner -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("时间", partner.getTimeRange());
                    jsonObject.put("域名", partner.getDomainName());
                    jsonObject.put("别名", partner.getCname());
                    jsonObject.put("下发状态", partner.getDistributeState());
                    jsonObject.put("出网流量(G)", partner.getNetOutParseFlowTotal());
                    jsonObject.put("出网流量占比", partner.getNetOutFlowRate());
                    jsonObject.put("出网次数", partner.getNetOutParseTotalCnt());
                    jsonObject.put("解析次数", partner.getParseTotalCnt());
                    jsonObject.put("成功次数", partner.getParseSuccessCnt());
                    jsonObject.put("成功率", partner.getSuccessRate());
                    jsonObject.put("总流量(G)", partner.getParseFlowTotal());
                    jsonObject.put("出网率", partner.getNetOutRate());
                    jsonObject.put("网内流量(G)", partner.getNetInParseFlowTotal());
                    jsonObject.put("本网次数", partner.getNetInParseTotalCnt());
                    jsonObject.put("本网率", partner.getNetInRate());
                    jsonObject.put("本省次数", partner.getWithinParseTotalCnt());
                    jsonObject.put("本省率", partner.getParseInRate());
                    jsonObject.put("外省次数", partner.getWithoutParseTotalCnt());
                    jsonObject.put("出省率", partner.getParseOutRate());
                    jsonObject.put("CDN次数", partner.getCdnParseTotalCnt());
                    jsonObject.put("IDC次数", partner.getIdcParseTotalCnt());
                    return jsonObject;
                }).collect(Collectors.toList());
                Arrays.stream(CDOMAIN_COLUMS).forEach(
                        colum -> {
                            writer.addHeaderAlias(colum, colum);
                        }
                );
                // 设置数据
                writer.write(sheetDateList, true);
            }
        }


        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private void renameOrSetSheetName(int index, String sheetName, ExcelWriter writer) {
        if (index == 0) {
            writer.renameSheet(sheetName);
        } else {
            writer.setSheet(sheetName);
        }
    }

    private void checkDownloadByParamMethodParam(PopularDomainCnameFlow popularDomainCnameFlow) {
        popularDomainCnameFlow.formatParseTime(popularDomainCnameFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(popularDomainCnameFlow.getOrderBy())) {
            popularDomainCnameFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
        popularDomainCnameFlow.setLimit(10000L);
        popularDomainCnameFlow.setOffset(0L);
    }

    public PageResult findCnameTableDetail(PopularDomainCnameFlow popularDomainCnameFlow) {
        checkFindTableDetailMethodParam(popularDomainCnameFlow);
        Long count = popularDomainCnameFlowMapper.queryCountGroupByCName(popularDomainCnameFlow);
        List<PopularDomainCnameFlow> dataList = popularDomainCnameFlowMapper.queryGroupByCName(popularDomainCnameFlow);
        formatTimeRange(dataList, popularDomainCnameFlow.getStartTime(), popularDomainCnameFlow.getEndTime());
        dataList.forEach(cnameFlow -> {
            cnameFlow.buildRate();
            cnameFlow.buildNetOutRate();
            cnameFlow.buildConvertFlow();
        });
        return PageResult.buildPageResult(count, dataList);
    }

    public List<String> topDomain(PopularDomainCnameFlow popularDomainCnameFlow) {

        if (ObjectUtil.isEmpty(popularDomainCnameFlow.getRankNumber())) {
            log.info("ranknumber is null, set it 10");
            popularDomainCnameFlow.setRankNumber(10L);
        }
        return popularDomainCnameFlowMapper.topDomain(popularDomainCnameFlow);
    }
}
