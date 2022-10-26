package com.yamu.data.sample.service.resources.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.ResourceDomainTopnDetailExcelBO;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDomainDetailUserSourceVO;
import com.yamu.data.sample.service.resources.service.ResourceDomainTopnDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.ParseException;
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
@RequestMapping("/service/resource/resourceDomainTopnDetail")
@Api(value = "域名TopN明细分析", tags = "域名TopN明细分析API")
public class ResourceDomainTopnDetailController {
    @Autowired
    ResourceDomainTopnDetailService trendService;
    static String ORDER_BY = "parse_time asc";

    /**
     * 域名资源分布.
     *
     * @param queryParam
     * @return
     * @throws Exception
     */
    @GetMapping("report/v1")
    public ResponseEntity report(ResourceDomainTopnDetail queryParam) throws YamuException, ParseException {
        queryParam.setOrderBy(ORDER_BY);
        return ResponseEntity.ok(trendService.findGroupByIsp(queryParam));
    }

    public String buildIntervalType(String queryType) {
        String intervalType = "10min";
        if (queryType.equals("10min")) {
            intervalType = "1h";
        } else if (queryType.equals("1h")) {
            intervalType = "1d";
        } else if (queryType.equals("1d")) {
            intervalType = "1w";
        } else if (queryType.equals("1w")) {
            intervalType = "1m";
        } else if (queryType.equals("1m")) {
            intervalType = "1y";
        } else if (queryType.equals("1y")) {
            intervalType = "Ny";
        }
        return intervalType;
    }

    /**
     * 域名趋势分析.
     *
     * @param queryParam
     * @return
     */
    @GetMapping("reportRate/v1")
    public ResponseEntity reportRate(ResourceDomainTopnDetail queryParam) throws ParseException {
        queryParam.setOrderBy(ORDER_BY);
        // 设置开始时间与结束时间
//        queryParam.getQueryType();
//        Integer ny = 1;
//        String intervalType = buildIntervalType(queryParam.getQueryType());
//        if (!intervalType.equals("Ny")) {
//            Map<String, String> stringStringMap = ReportUtils.buildTimeParamToMOM(intervalType);
//            String nowStart = stringStringMap.get(ReportUtils.NOW_START);
//            String nowEnd = stringStringMap.get(ReportUtils.NOW_END);
//            queryParam.setStartTime(nowStart);
//            queryParam.setEndTime(nowEnd);
//        } else {
//            Date date = new Date();
//            date = ReportUtils.getFirstOfYear(date);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.YEAR, -5);
//            Date endYear = calendar.getTime();
//            SimpleDateFormat fmt = new SimpleDateFormat(ReportUtils.DEFAULT_FMT);
//            queryParam.setStartTime(fmt.format(date));
//            queryParam.setEndTime(fmt.format(endYear));
//        }
        // 判断和处理时间粒度降维
        trendService.checkFindRateReportParam(queryParam);
        List<ResourceDomainTopnDetail> dataList;

        // 当不存在查询时间时,说明不需要"降维"处理
        if (null != queryParam.getIsTopN() && queryParam.getIsTopN()) {
            dataList = trendService.findAllGroupByTopnParseTime(queryParam);
        } else if (null == queryParam.getDomainName() || queryParam.getDomainName().isEmpty()) {
            // 若域名为空,则直接返回空List
            dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        } else {
            dataList = trendService.findAllGroupByDoaminParseTime(queryParam);
        }
        dataList.stream().forEach(ResourceDomainTopnDetail::buildRate);
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
            for (ResourceDomainTopnDetail trend : dataList) {
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
                Map<Date, ResourceDomainTopnDetail> preaseTime = dataList.stream().collect(Collectors.toMap(ResourceDomainTopnDetail::getParseTime, ResourceDomainTopnDetail -> ResourceDomainTopnDetail));
                ResourceDomainTopnDetail trend = preaseTime.get(xkey);
                if (trend == null) {
                    removeList.add(xkey);
//                    netInRateResult.add(0D);
//                    netOutRateResult.add(0D);
//                    parseInRateResult.add(0D);
//                    parseOutRateResult.add(0D);
//                    successRateResult.add(0D);
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
        Map<String, String> legend = Maps.newHashMap();
        legend.put(ReportUtils.LINE + "netInRate", "本网率");
        legend.put(ReportUtils.LINE + "netOutRate", "出网率");
        legend.put(ReportUtils.LINE + "parseInRate", "本省率");
        legend.put(ReportUtils.LINE + "parseOutRate", "出省率");
        legend.put(ReportUtils.LINE + "successRate", "成功率");
        // 报表名称
        String reportName = "域名趋势分析";  //getReportName(queryParam.getQueryType());
        List<String> xAxis = new ArrayList<>(xaxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        System.out.println(finalResult);
        return ResponseEntity.ok(finalResult);
    }

    public static String getReportName(String type) {
        String baseReportName = "域名趋势分析";
        switch (type) {
            case "1min":
                return baseReportName + ":最近10分钟数据";
            case "10min":
                return baseReportName + ":最近1小时数据";
            case "1h":
                return baseReportName + ":最近1天数据";
            case "1w":
                return baseReportName + ":最近1月数据";
            case "1q":
                return baseReportName + ":最近1年数据";
            case "1y":
                return baseReportName + ":最近5年数据";
            default:
                return baseReportName + ":最近10分钟数据";
        }
    }


    /**
     * 明细表.
     *
     * @param queryParam
     * @return
     * @throws Exception
     */
    @GetMapping("findRate/v1")
    public ResponseEntity findRate(ResourceDomainTopnDetail queryParam) throws Exception {
        queryParam.setOrderBy(ORDER_BY);
        PageResult result = trendService.findRate(queryParam);
        return ResponseEntity.ok(result);
    }

//    @GetMapping("findDetails/v1")
//    public ResponseEntity findDetails(ResourceDomainTopnDetail queryParam) throws Exception {
//        queryParam.setOrderBy(ORDER_BY);
//        queryParam.formatParseTime();
//        PageResult result = trendService.findDetailsByPage(queryParam);
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("findCdnReportAll/v1")
    @ApiOperation("cdn厂商汇总")
    public ResponseEntity findCdnReportAll(PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail){
        JSONObject finalResult = trendService.findCdnReportAll(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnReport/v1")
    @ApiOperation("cdn厂商")
    public ResponseEntity findCdnReport(PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail){
        JSONObject finalResult = trendService.findCdnReport(domainNameWebsiteDetail);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("findCdnDetail/v1")
    @ApiOperation("cdn厂商明细")
    public ResponseEntity findCdnDetail(PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail){
        PageResult resourceWebsiteDetail = trendService.findCdnDetail(domainNameWebsiteDetail);
        return ResponseEntity.ok(resourceWebsiteDetail);
    }


    /**
     * 下载明细表.
     *
     * @param queryParam
     * @param response
     * @throws Exception
     */
    /*
    @GetMapping("download/v1")
    public void downloadNetOut(PopularDomainTopNCdnBusinessDetail domainNameWebsiteDetail, HttpServletResponse response) throws Exception {
        trendService.download(domainNameWebsiteDetail, response);
    }
    */


    @GetMapping("download/v1")
    public void downloadTotal(ResourceDomainTopnDetail queryParam, HttpServletResponse response) throws Exception {
        queryParam.setOrderBy(ORDER_BY);
        queryParam.setLimit(10000L);
        queryParam.setOffset(0L);
        trendService.download(queryParam,response);
   }


    /**
     * 导出网内省内无资源明细表.
     *
     * @param queryParam
     * @param response
     * @throws Exception
     */
    @GetMapping("downloadNoResource/v1")
    public void downloadNoResource(ResourceDomainTopnDetail queryParam, HttpServletResponse response) throws Exception {
        queryParam.setOrderBy(ORDER_BY);
        queryParam.setLimit(10000L);
        queryParam.setOffset(0L);
        List<ResourceDomainTopnDetailExcelBO> list = new ArrayList<>();
        List<ResourceDomainTopnDetail> dataList = trendService.findRate(queryParam).getData();
        for (ResourceDomainTopnDetail resourceDomainTopnDetail : dataList) {
            resourceDomainTopnDetail.buildRateNoResource();
            ResourceDomainTopnDetailExcelBO resourceDomainTopnDetailExcelBO = BeanUtil.copyProperties(resourceDomainTopnDetail, ResourceDomainTopnDetailExcelBO.class);
            list.add(resourceDomainTopnDetailExcelBO);
        }
        dataList.stream().forEach(ResourceDomainTopnDetail::buildRate);
        String fileName = "网内省内域名无资源报表" + StrUtil.DASHED + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();
        Map userVisualizationKeyBusinessResult = Maps.newLinkedHashMap();
        userVisualizationKeyBusinessResult.put("timeRange", "时间");
        userVisualizationKeyBusinessResult.put("domainName", "域名");
        userVisualizationKeyBusinessResult.put("parseTotalCnt", "解析次数");
        userVisualizationKeyBusinessResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        userVisualizationKeyBusinessResult.put("parseSuccessCnt", "解析成功次数");
        userVisualizationKeyBusinessResult.put("successRateStr", "成功率");
        userVisualizationKeyBusinessResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        userVisualizationKeyBusinessResult.put("netOutRateStr", "出网率");
        userVisualizationKeyBusinessResult.put("withOutParseTotalCnt", "外省次数");
        userVisualizationKeyBusinessResult.put("parseOutRateStr", "出省率");
        writer.merge(8, "导出时间段   开始时间:"+queryParam.getStartTime().substring(0,queryParam.getStartTime().length()-3)
                +",结束时间:"+queryParam.getEndTime().substring(0,queryParam.getEndTime().length()-3));
        writer.setHeaderAlias(userVisualizationKeyBusinessResult);
        writer.renameSheet("网内省内域名无资源报表");
        writer.write(list, true);
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }


    /**
     * 服务节点ip解析量.
     *
     * @param queryParam
     * @return
     */
    @GetMapping("findDetails/v1")
    public ResponseEntity nodeServerDetail(ResourceDomainTopnDetail queryParam) throws YamuException, ParseException {
        PageResult pageResult = trendService.nodeServerDetail(queryParam);
        return ResponseEntity.ok(pageResult);
    }

    @GetMapping("findUserSource/v1")
    public ResponseEntity findUserSource(ResourceDomainDetailUserSourceVO resourceDomainDetailUserSourceVO){
        JSONObject finalResult = trendService.findUserSource(resourceDomainDetailUserSourceVO);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("resourceDistributionProvince/v1")
    public List<ResourceDistributionProvinceData> resourceDistributionProvince(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        List<ResourceDistributionProvinceData> list = trendService.getResourceDistributionProvinceList(resourceDistributionProvinceVO);
        return list;
    }

}
