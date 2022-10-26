package com.yamu.data.sample.service.resources.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.ResourceDistributionProvinceData;
import com.yamu.data.sample.service.resources.entity.po.ResourceIpDetail;
import com.yamu.data.sample.service.resources.entity.po.ResourcePartnerDomainDetail;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificDomainUserSourceVO;
import com.yamu.data.sample.service.resources.service.ResourcePartnerDomainDetailService;
import com.yamu.data.sample.common.result.PageResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author Lishuntao
 * @Date 2021/3/20
 */
@RestController
@RequestMapping("/service/resource/resourcePartnerDomainDetail")
public class ResourcePartnerDomainDetailController {
    @Autowired
    ResourcePartnerDomainDetailService trendService;
    static String ORDER_BY = "parse_time asc";

    @GetMapping("report/v1")
    public ResponseEntity report(ResourcePartnerDomainDetail queryParam) throws Exception {
        // report不需要做任何修改只需要传输的时候把域名传过来即可, 后续的将该部分的sql改为域名相等
        List<ResourcePartnerDomainDetail> dataList = trendService.findAllGroupByIsp(queryParam);
        // x轴的坐标使用运营商名称来生成
        List<String> xaxisList = Lists.newArrayList();
        List<BigInteger> totalList = Lists.newArrayList();
        dataList.stream().forEach(item -> {
            xaxisList.add(item.getIsp());
            totalList.add(item.getARecordParseTotalCnt());
        });
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "解析次数");
        // 报表名称
        String reportName = "特定域名资源分布";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xaxisList, dataMap);
        System.out.println(finalResult);
        return ResponseEntity.ok(finalResult);
    }

    public String buildIntervalType(String queryType) {
        String intervalType = "10min";
        if (queryType.equals("10min")){
            intervalType="1h";
        }
        else if (queryType.equals("1h")){
            intervalType="1d";
        }
        else if (queryType.equals("1d")){
            intervalType="1w";
        }
        else if (queryType.equals("1w")){
            intervalType="1m";
        }
        else if (queryType.equals("1m")){
            intervalType="1y";
        }else if (queryType.equals("1y")){
            intervalType="Ny";
        }
        return intervalType;
    }

    @GetMapping("reportRate/v1")
    public ResponseEntity reportRate(ResourcePartnerDomainDetail queryParam) throws ParseException {
        queryParam.setOrderBy(ORDER_BY);
        queryParam.formatParseTime();      // 格式化查询参数的开始时间与结束时间
        // 设置开始时间与结束时间
//        queryParam.getQueryType();
//        String intervalType=buildIntervalType(queryParam.getQueryType());
//        if (!intervalType.equals("Ny")){
//            Map<String, String> stringStringMap = ReportUtils.buildTimeParamToMOM(intervalType);
//            String nowStart = stringStringMap.get(ReportUtils.NOW_START);
//            String nowEnd = stringStringMap.get(ReportUtils.NOW_END);
//            queryParam.setStartTime(nowStart);
//            queryParam.setEndTime(nowEnd);
//        }else{
//            Date date= new Date();
//            date  =ReportUtils.getFirstOfYear(date);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(date);
//            calendar.add(Calendar.YEAR,-5);
//            Date endYear = calendar.getTime();
//            SimpleDateFormat fmt = new SimpleDateFormat(ReportUtils.DEFAULT_FMT);
//            queryParam.setStartTime(fmt.format(date));
//            queryParam.setEndTime(fmt.format(endYear));
//        }
        List<ResourcePartnerDomainDetail> dataList = trendService.findAllGroupByParseTime(queryParam);
        //dataList= ReportUtils.gather(dataList, ResourcePartnerDomainDetail.filters, ResourcePartnerDomainDetail.counters);
        dataList.stream().forEach(ResourcePartnerDomainDetail::buildRate);
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
            for (ResourcePartnerDomainDetail trend : dataList) {
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
                Map<Date, ResourcePartnerDomainDetail> preaseTime = dataList.stream().collect(Collectors.toMap(ResourcePartnerDomainDetail::getParseTime, ResourcePartnerDomainDetail -> ResourcePartnerDomainDetail));

                ResourcePartnerDomainDetail trend = preaseTime.get(xkey);
                if (trend == null) {
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
        Map<String, String> legend = Maps.newHashMap();
        legend.put(ReportUtils.LINE + "netInRate", "本网率");
        legend.put(ReportUtils.LINE + "netOutRate", "出网率");
        legend.put(ReportUtils.LINE + "parseInRate", "本省率");
        legend.put(ReportUtils.LINE + "parseOutRate", "出省率");
        legend.put(ReportUtils.LINE + "successRate", "成功率");
        // 报表名称
        String reportName = "特定域名趋势分析";
        List<String> xAxis = new ArrayList<>(xaxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        System.out.println(finalResult);
        return ResponseEntity.ok(finalResult);
    }


    @GetMapping("findRate/v1")
    public ResponseEntity findToRate(ResourcePartnerDomainDetail queryParam) throws Exception {
        PageResult result = trendService.findAllGroupByParseTimeAndDomainName(queryParam);
        return ResponseEntity.ok(result);
    }


    @GetMapping("download/v1")
    public void downloadTotal(ResourcePartnerDomainDetail queryParam, HttpServletResponse response) throws Exception {
        queryParam.setOffset(0L);
        queryParam.setLimit(10000L);
        trendService.download(queryParam,response);
    }

    @GetMapping("findUserSource/v1")
    public ResponseEntity findUserSource(ResourceSpecificDomainUserSourceVO resourceSpecificDomainUserSourceVO){
        JSONObject finalResult = trendService.findUserSource(resourceSpecificDomainUserSourceVO);
        return ResponseEntity.ok(finalResult);
    }

    @GetMapping("resourceDistributionProvince/v1")
    public ResponseEntity resourceDistributionProvince(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        List<ResourceDistributionProvinceData> list = trendService.getResourceDistributionProvinceList(resourceDistributionProvinceVO);
        return ResponseEntity.ok(list);
    }

    @GetMapping("findIpDetail/v1")
    public ResponseEntity findIpDetail(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        ResourceIpDetail resourceIpDetail = trendService.findIpDetail(resourceDistributionProvinceVO);
        return ResponseEntity.ok(resourceIpDetail);
    }
}
