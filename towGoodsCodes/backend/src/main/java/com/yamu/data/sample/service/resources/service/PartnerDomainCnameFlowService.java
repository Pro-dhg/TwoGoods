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
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainCnameFlow;
import com.yamu.data.sample.service.resources.mapper.PartnerDomainCnameFlowMapper;
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
 * @Date 2021/8/27
 * @DESC
 */
@Service
@Slf4j
public class PartnerDomainCnameFlowService {
    private static final String DOMAIN_FLOW_DEFAULT_ORDER_BY = "net_out_parse_flow_total desc";

    /**
     * 导出Excel sheet页名称.
     */
    private static final String[] SHEET_NAME = {"域名流量统计", "子域名流量统计"};
    /**
     * 域名sheet页表头数据
     */
    private static final String[] DOMAIN_COLUMS = {"时间", "域名", "别名", "下发状态", "出网流量(G)", "出网次数", "解析次数", "成功次数"};

    /**
     * 域名sheet页表头数据
     */
    private static final String[] CDOMAIN_COLUMS = {"时间", "域名", "别名", "下发状态", "出网流量(G)", "出网流量占比", "出网次数", "解析次数", "成功次数", "成功率", "总流量(G)", "出网率", "网内流量(G)", "本网次数", "本网率", "本省次数", "本省率", "外省次数", "出省率", "CDN次数", "IDC次数"};


    @Autowired
    private PartnerDomainCnameFlowMapper partnerDomainCnameFlowMapper;

    public PageResult findTableDetail(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        checkFindTableDetailMethodParam(partnerDomainCnameFlow);
        partnerDomainCnameFlow.formatParseTime(partnerDomainCnameFlow.getQueryType(), "1d");
        Long total = partnerDomainCnameFlowMapper.countQueryGroupByDomainName(partnerDomainCnameFlow);
        List<PartnerDomainCnameFlow> dataList = partnerDomainCnameFlowMapper.queryGroupByDomainName(partnerDomainCnameFlow);
        dataList.forEach(pop -> {
            pop.setNetOutParseFlowTotal(FlowUtils.flowConvert(pop.getNetOutParseFlowTotal(), FlowUtils.B, FlowUtils.GB, 2));
        });
        formatTimeRange(dataList, partnerDomainCnameFlow.getStartTime(), partnerDomainCnameFlow.getEndTime());
        return PageResult.buildPageResult(total, dataList);
    }

    private void formatTimeRange(List<PartnerDomainCnameFlow> domainList, String startTime, String endTime) {
        domainList.stream().forEach(domainFlow -> {
            try {
                domainFlow.setTimeRange(DateUtils.formatDate(startTime, DateUtils.DEFAULT_DAY_FMT)
                        + "~" +
                        DateUtils.formatDate(endTime, DateUtils.DEFAULT_DAY_FMT));
            } catch (ParseException e) {
                log.error(e.toString());
            }
        });
    }

    /**
     * 出网流量趋势.
     *
     * @param partnerDomainCnameFlow
     * @return
     */
    public JSONObject findFlowReport(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        checkFindReportMethodParam(partnerDomainCnameFlow);
        List<PartnerDomainCnameFlow> dataList = partnerDomainCnameFlowMapper.queryGroupByParseTime(partnerDomainCnameFlow);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(partnerDomainCnameFlow.getStartTime(), partnerDomainCnameFlow.getEndTime(), partnerDomainCnameFlow.getQueryType());
        //总数结果集,成功结果集
        List netOutParseFlowTotalResult = Lists.newArrayList();
        if (xAxisMap.size() == dataList.size()) {
            for (PartnerDomainCnameFlow domainFlow : dataList) {
                netOutParseFlowTotalResult.add(domainFlow.getNetOutParseFlowTotal());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PartnerDomainCnameFlow> collect = dataList.stream()
                    .collect(Collectors.toMap(PartnerDomainCnameFlow::getParseTime, PartnerDomainCnameFlow -> PartnerDomainCnameFlow));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PartnerDomainCnameFlow domainFlow = collect.get(xKey);
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

    private void checkFindReportMethodParam(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        partnerDomainCnameFlow.formatParseTime(partnerDomainCnameFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(partnerDomainCnameFlow.getOrderBy())) {
            partnerDomainCnameFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
    }

    private void checkFindTableDetailMethodParam(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        partnerDomainCnameFlow.formatParseTime(partnerDomainCnameFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(partnerDomainCnameFlow.getOrderBy())) {
            partnerDomainCnameFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
    }

    public JSONObject findNetInReport(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        checkFindReportMethodParam(partnerDomainCnameFlow);
        List<PartnerDomainCnameFlow> dataList = partnerDomainCnameFlowMapper.queryGroupByParseTime(partnerDomainCnameFlow);
        dataList.forEach(PartnerDomainCnameFlow::buildRate);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(partnerDomainCnameFlow.getStartTime(), partnerDomainCnameFlow.getEndTime(), partnerDomainCnameFlow.getQueryType());
        //总数结果集,成功结果集
        List successRateResult = Lists.newArrayList();
        List netInRateResult = Lists.newArrayList();
        List netOutRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        List parseOutRateResult = Lists.newArrayList();
        if (xAxisMap.size() == dataList.size()) {
            for (PartnerDomainCnameFlow domainFlow : dataList) {
                successRateResult.add(domainFlow.getSuccessRate());
                netInRateResult.add(domainFlow.getNetInRate());
                netOutRateResult.add(domainFlow.getNetOutRate());
                parseInRateResult.add(domainFlow.getParseInRate());
                parseOutRateResult.add(domainFlow.getParseOutRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, PartnerDomainCnameFlow> collect = dataList.stream().collect(Collectors.toMap(PartnerDomainCnameFlow::getParseTime, PartnerDomainCnameFlow -> PartnerDomainCnameFlow));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                PartnerDomainCnameFlow domainFlow = collect.get(xKey);
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
        String reportName = "域名CNAME本网率趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }

    /**
     * 导出
     *
     * @param partnerDomainCnameFlow
     * @param response
     */
    public void download(PartnerDomainCnameFlow partnerDomainCnameFlow, HttpServletResponse response) throws IOException {
        checkDownloadByParamMethodParam(partnerDomainCnameFlow);
        List<PartnerDomainCnameFlow> dataList = partnerDomainCnameFlowMapper.queryGroupByDomainName(partnerDomainCnameFlow);
        dataList.forEach(pop -> {
            pop.setNetOutParseFlowTotal(FlowUtils.flowConvert(pop.getNetOutParseFlowTotal(), FlowUtils.B, FlowUtils.GB, 2));
        });
        formatTimeRange(dataList, partnerDomainCnameFlow.getStartTime(), partnerDomainCnameFlow.getEndTime());

        /**
         * 1万条域名(最多)
         */
        List<String> domainNameList = dataList.stream().map(PartnerDomainCnameFlow::getDomainName).collect(Collectors.toList());


        String timeInterval = partnerDomainCnameFlow.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + partnerDomainCnameFlow.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
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
                partnerDomainCnameFlow.setLimit(null);
                partnerDomainCnameFlow.setOffset(null);
                List<PartnerDomainCnameFlow> cnameDataList = Lists.newArrayList();
                for (int i = 0; i < domainNameList.size(); i += 1000) {
                    String domainNames = "";
                    if (i + 1000 > domainNameList.size()) {
                        domainNames = String.join(";", domainNameList.subList(i, domainNameList.size()));
                    } else {
                        domainNames = String.join(";", domainNameList.subList(i, i + 1000));
                    }
                    cnameDataList = partnerDomainCnameFlowMapper.queryCnameFlowByDomainName(partnerDomainCnameFlow, domainNames);
                }
                formatTimeRange(cnameDataList, partnerDomainCnameFlow.getStartTime(), partnerDomainCnameFlow.getEndTime());
                cnameDataList.forEach(cnameFlow -> {
                    cnameFlow.buildRate();
                    cnameFlow.buildNetOutRate();
                    cnameFlow.buildConvertFlow();
                });

                sheetDateList = cnameDataList.stream().map(pop -> {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("时间", pop.getTimeRange());
                    jsonObject.put("域名", pop.getDomainName());
                    jsonObject.put("别名", pop.getCname());
                    jsonObject.put("下发状态", pop.getDistributeState());
                    jsonObject.put("出网流量(G)", pop.getNetOutParseFlowTotal());
                    jsonObject.put("出网流量占比", pop.getNetOutFlowRate());
                    jsonObject.put("出网次数", pop.getNetOutParseTotalCnt());
                    jsonObject.put("解析次数", pop.getParseTotalCnt());
                    jsonObject.put("成功次数", pop.getParseSuccessCnt());
                    jsonObject.put("成功率", pop.getSuccessRate());
                    jsonObject.put("总流量(G)", pop.getParseFlowTotal());
                    jsonObject.put("出网率", pop.getNetOutRate());
                    jsonObject.put("网内流量(G)", pop.getNetInParseFlowTotal());
                    jsonObject.put("本网次数", pop.getNetInParseTotalCnt());
                    jsonObject.put("本网率", pop.getNetInRate());
                    jsonObject.put("本省次数", pop.getWithinParseTotalCnt());
                    jsonObject.put("本省率", pop.getParseInRate());
                    jsonObject.put("外省次数", pop.getWithoutParseTotalCnt());
                    jsonObject.put("出省率", pop.getParseOutRate());
                    jsonObject.put("CDN次数", pop.getCdnParseTotalCnt());
                    jsonObject.put("IDC次数", pop.getIdcParseTotalCnt());
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

    private void checkDownloadByParamMethodParam(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        partnerDomainCnameFlow.formatParseTime(partnerDomainCnameFlow.getQueryType(), "1d");
        if (StrUtil.isEmpty(partnerDomainCnameFlow.getOrderBy())) {
            partnerDomainCnameFlow.setOrderBy(DOMAIN_FLOW_DEFAULT_ORDER_BY);
        }
        partnerDomainCnameFlow.setLimit(10000L);
        partnerDomainCnameFlow.setOffset(0L);
    }

    public PageResult findCnameTableDetail(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        checkFindTableDetailMethodParam(partnerDomainCnameFlow);
        Long count = partnerDomainCnameFlowMapper.queryCountGroupByCName(partnerDomainCnameFlow);
        List<PartnerDomainCnameFlow> dataList = partnerDomainCnameFlowMapper.queryGroupByCName(partnerDomainCnameFlow);
        formatTimeRange(dataList, partnerDomainCnameFlow.getStartTime(), partnerDomainCnameFlow.getEndTime());
        dataList.forEach(cnameFlow -> {
            cnameFlow.buildRate();
            cnameFlow.buildNetOutRate();
            cnameFlow.buildConvertFlow();
        });
        return PageResult.buildPageResult(count, dataList);
    }

    public List<String> topDomain(PartnerDomainCnameFlow partnerDomainCnameFlow) {
        if (ObjectUtil.isEmpty(partnerDomainCnameFlow.getRankNumber())) {
            log.info("ranknumber is null set it 10");
            partnerDomainCnameFlow.setRankNumber(10L);
        }
        return partnerDomainCnameFlowMapper.topDomain(partnerDomainCnameFlow);
    }
}
