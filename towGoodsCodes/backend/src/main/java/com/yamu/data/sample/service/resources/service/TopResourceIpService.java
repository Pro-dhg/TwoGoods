package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.TopResourceIp;
import com.yamu.data.sample.service.resources.entity.po.TopResourceIpExcelData;
import com.yamu.data.sample.service.resources.mapper.TopResourceIpMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yuyuan.Dong
 * @Date 2022/2/16
 * @DESC
 */
@Repository
@Slf4j
public class TopResourceIpService {

    @Autowired
    private TopResourceIpMapper resourceIpMapper;

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    public JSONObject findTopIpTrend(TopResourceIp resourceIp) throws YamuException, ParseException {
        checkFindTrendListParam(resourceIp);
//        checkParam(topUserIp);
        if (ObjectUtil.isNotEmpty(resourceIp.getUserProvince()) && resourceIp.getUserProvince().contains("_其他")){
            String province = resourceIp.getUserProvince().split("_")[0];
            resourceIp.setIsOther("true");
            resourceIp.setUserProvince(province);
        }
        List<TopResourceIp> dataList = resourceIpMapper.findTopIpTrend(resourceIp);
        Map<Date, String> xAxisMap = ReportUtils.buildXaxisMap(resourceIp.getStartTime(), resourceIp.getEndTime(), resourceIp.getQueryType());

        //总数结果集,成功结果集
        List parseTotalCntResult = Lists.newArrayList();
        //封装, 根据parsedate,从data中获取参数封装到结果集中,若出现空数据则使用0进行补充
        if (xAxisMap.size() == dataList.size()) {
            for (TopResourceIp parseTrend : dataList) {
                parseTotalCntResult.add(parseTrend.getParseTotalCnt());
            }
        } else {
            // 填充占位数据
            List<Date> removeList = Lists.newArrayList();
            Map<Date, TopResourceIp> collect = dataList.stream().collect(Collectors.toMap(TopResourceIp::getParseTime, TopResourceIp -> TopResourceIp));
            for (Map.Entry<Date, String> entry : xAxisMap.entrySet()) {
                Date xKey = entry.getKey();
                TopResourceIp ipDetail = collect.get(xKey);
                if (ObjectUtil.isNotEmpty(ipDetail)) {
                    parseTotalCntResult.add(ipDetail.getParseTotalCnt());
                } else {
                    removeList.add(xKey);
                }
            }
            removeList.stream().forEach(xAxisMap::remove);
        }

        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.LINE + "parseTotalCnt", parseTotalCntResult);

        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.LINE + "parseTotalCnt", "解析次数");

        // 报表名称
        String reportName = "TopN资源IP解析趋势";
        List<String> xAxis = new ArrayList<>(xAxisMap.values());
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxis, dataMap);
        return finalResult;
    }


    public PageResult findTableDetail(TopResourceIp resourceIp) {
        resourceIp.formatParseTime(resourceIp.getQueryType(), "1d");
        Long total = 0L;
        List<TopResourceIp> dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        if (ObjectUtil.isNotEmpty(resourceIp.getUserProvince()) && resourceIp.getUserProvince().contains("_其他")){
            String province = resourceIp.getUserProvince().split("_")[0];
            resourceIp.setIsOther("true");
            resourceIp.setUserProvince(province);
        }
        total = resourceIpMapper.countTopIp(resourceIp);
        if (total == null){
            total = 0L;
        }
        dataList = resourceIpMapper.findTopIpTableList(resourceIp);
        for (int i = 0;i < dataList.size();i++) {
            Long number = resourceIp.getOffset()+i+1;
            dataList.get(i).setRankNumber(number);
        }
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }


    public List<TopResourceIpExcelData> downloadByParam(TopResourceIp resourceIp) {
        checkDownloadByParamMethodParam(resourceIp);
        if (ObjectUtil.isNotEmpty(resourceIp.getUserProvince()) && resourceIp.getUserProvince().contains("_其他")){
            String province = resourceIp.getUserProvince().split("_")[0];
            resourceIp.setIsOther("true");
            resourceIp.setUserProvince(province);
        }
        List<TopResourceIpExcelData> dataList = resourceIpMapper.findTopIpTableExcelList(resourceIp);
        for (int i = 0;i < dataList.size();i++) {
            Long number = resourceIp.getOffset()+i+1;
            dataList.get(i).setRankNumber(number);
        }
        return dataList;
    }

    private void checkFindTrendListParam(TopResourceIp resourceIp) throws YamuException {
        resourceIp.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        String srcIp = resourceIp.getAnswerFirst();
        resourceIp.setAnswerFirst(ReportUtils.escapeChar(srcIp));
        ValidationResult validationResult = ValidationUtils.validateEntity(resourceIp);
        if (validationResult.isHasErrors()) {
            log.error(">>TopUserIpService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
    }


    private void checkDownloadByParamMethodParam(TopResourceIp resourceIp) {
        resourceIp.formatParseTime(resourceIp.getQueryType(), "1d");
        resourceIp.setLimit(10000L);
        resourceIp.setOffset(0L);
    }
}
