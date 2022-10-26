package com.yamu.data.sample.service.resources.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.ResourceDomainTopn;
import com.yamu.data.sample.service.resources.service.ResourceDomainTopnService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 解析量趋势分析  rpt_basic_parse_trend_1min
 *
 * @Author Lishuntao
 * @Date 2021/3/20
 */
@RestController
@RequestMapping("/service/resource/domainTopN")
public class ResourceDomainTopnController {
    @Autowired
    ResourceDomainTopnService trendService;
    static String ORDER_BY = "parse_time asc";


    @GetMapping("report/v1")
    public ResponseEntity report(ResourceDomainTopn queryParam) throws Exception {

        queryParam.setOrderBy(ORDER_BY);
        List<ResourceDomainTopn> dataList = trendService.findAllGroupByIsp(queryParam);
        // x轴的坐标使用运营商名称来生成
        List<String> xaxisList = Lists.newArrayList();
        List<BigInteger> totalList = Lists.newArrayList();
        dataList.stream().forEach(item -> {
            xaxisList.add(item.getAnswerFirstIsp());
            totalList.add(item.getARecordParseTotalCnt());
        });

        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "解析次数");
        // 报表名称
        String reportName = "热点域名TopN资源分布";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xaxisList, dataMap);
        return ResponseEntity.ok(finalResult);
    }


    @GetMapping("reportRate/v1")
    public ResponseEntity reportRate(ResourceDomainTopn queryParam) {

        queryParam.setOrderBy(ORDER_BY);
        List<ResourceDomainTopn> dataList = trendService.findAllGroupByParseTime(queryParam);
        dataList.stream().forEach(ResourceDomainTopn::buildRate);
        // 生成X轴坐标map
        String qtype = queryParam.getQueryType();
        String startTime = queryParam.getStartTime();
        String endTime = queryParam.getEndTime();
        Map<Date, String> xaxisMap = ReportUtils.buildXaxisMap(startTime, endTime, qtype);

        //总数结果集,成功结果集
        List netInRateResult = Lists.newArrayList();
        List netOutRateResult = Lists.newArrayList();
        List parseInRateResult = Lists.newArrayList();
        List parseOutRateResult = Lists.newArrayList();
        List successRateResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xaxisMap.size() == dataList.size()) {
            for (ResourceDomainTopn trend : dataList) {
                // 计算成功率,失败率
                netInRateResult.add(trend.getNetInRate());
                netOutRateResult.add(trend.getNetOutRate());
                parseInRateResult.add(trend.getParseInRate());
                parseOutRateResult.add(trend.getParseOutRate());
                successRateResult.add(trend.getSuccessRate());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            for (Map.Entry<Date, String> entry : xaxisMap.entrySet()) {
                Date xkey = entry.getKey();
                Map<Date, ResourceDomainTopn> preaseTime = dataList.stream().collect(Collectors.toMap(ResourceDomainTopn::getParseTime, ResourceDomainTopn -> ResourceDomainTopn));
                ResourceDomainTopn trend = preaseTime.get(xkey);
                if (trend == null) {
                    // list_todo 不补点demo
//                    netInRateResult.add(0D);
//                    netOutRateResult.add(0D);
//                    parseInRateResult.add(0D);
//                    parseOutRateResult.add(0D);
//                    successRateResult.add(0D);
                    removeList.add(xkey);
                } else {
                    netInRateResult.add(trend.getNetInRate());
                    netOutRateResult.add(trend.getNetOutRate());
                    parseInRateResult.add(trend.getParseInRate());
                    parseOutRateResult.add(trend.getParseOutRate());
                    successRateResult.add(trend.getSuccessRate());
                }
            }
            removeList.stream().forEach(item -> xaxisMap.remove(item));
        }
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "netInRate", netInRateResult);
        dataMap.put(ReportUtils.LINE + "netOutRate", netOutRateResult);
        dataMap.put(ReportUtils.LINE + "parseInRate", parseInRateResult);
        dataMap.put(ReportUtils.LINE + "parseOutRate", parseOutRateResult);
        dataMap.put(ReportUtils.LINE + "successRate", successRateResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "successRate", "成功率");
        legend.put(ReportUtils.LINE + "netInRate", "本网率");
        legend.put(ReportUtils.LINE + "netOutRate", "出网率");
        legend.put(ReportUtils.LINE + "parseInRate", "本省率");
        legend.put(ReportUtils.LINE + "parseOutRate", "出省率");

        // 报表名称
        String reportName = "TopN域名趋势分析";
        List<String> xAxis = new ArrayList<>(xaxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        System.out.println(finalResult);
        return ResponseEntity.ok(finalResult);
    }


    @GetMapping("findRate/v1")
    public ResponseEntity findToRate(ResourceDomainTopn queryParam) {
        queryParam.setOrderBy(ORDER_BY);
        PageResult dataList = trendService.findAllGroupByParseTimeByPage(queryParam);
        return ResponseEntity.ok(dataList);
    }
    @GetMapping("download/v1")
    public void downloadTotal(ResourceDomainTopn queryParam, HttpServletResponse response) throws Exception {
        queryParam.setOrderBy(ORDER_BY);
        PageResult dataList = trendService.findAllGroupByParseTimeByPage(queryParam);
        List<ResourceDomainTopn> page =dataList.getData();
        List<String> collect = page.stream().map(ResourceDomainTopn::getCsvLineForTotal).collect(Collectors.toList());
        String csvHead ="时间,解析次数,IPv4解析次数,解析成功次数,成功率,网内次数(IPv4),本网率,出网次数(IPv4),出网率,本省次数,本省率,外省次数,出省率,CDN次数,IDC次数,CACHE次数\n";
        CsvUtils.exportCsv(ResourceDomainTopn.CSV_NAME,queryParam.getStartTime(), queryParam.getEndTime(),csvHead, collect, response);
    }
}
