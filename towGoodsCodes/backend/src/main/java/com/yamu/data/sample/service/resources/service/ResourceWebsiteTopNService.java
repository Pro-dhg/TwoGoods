package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteTopN;
import com.yamu.data.sample.service.resources.mapper.ResourceWebsiteTopNMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * @author dongyuyuan
 * Date 2020-07-1
 */
@Service
@Slf4j
public class ResourceWebsiteTopNService {

    @Autowired
    private ResourceWebsiteTopNMapper websiteTopNMapper;

    /**
     * 日期格式化.
     */
    private static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public JSONObject findwebsiteTopNRankTrend(ResourceWebsiteTopN websiteTopN) throws YamuException{
        ResourceWebsiteTopN resourceWebsiteTopN = checkWithinProvinceDomainRankParam(websiteTopN);
        List<String> websiteNameList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<BigInteger> parseLastTotalList = Lists.newArrayList();
        List<ResourceWebsiteTopN> resultList = websiteTopNMapper.findWebsiteTopNByParam(resourceWebsiteTopN);


        String startTimeStr = resourceWebsiteTopN.getStartTime();
        String endTimeStr = resourceWebsiteTopN.getEndTime();
        //计算当前时间段和环比时间段
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, FORMATTER);
        //计算时间间隔
        Duration between = Duration.between(endTime, startTime);
        // 环比开始时间(向前推一个时间段)
        LocalDateTime earlierStartTime = startTime.plus(between);
        String earlierStartTimeStr = earlierStartTime.format(FORMATTER);
        // 计算环比时间
        resourceWebsiteTopN.setEndTime(startTimeStr);
        resourceWebsiteTopN.setStartTime(earlierStartTimeStr);

        resultList.stream().forEach(resultListDemo -> {
            if (ObjectUtil.isEmpty(resultListDemo.getWebsiteAppName())) {
                websiteNameList.add("未知");
            }else {
                websiteNameList.add(resultListDemo.getWebsiteAppName());
            }
//            websiteNameList.add(resultListDemo.getWebsiteAppName());
            parseTotalList.add(resultListDemo.getParseTotalCnt());
        });

        List<ResourceWebsiteTopN> lastResultList = websiteTopNMapper.findWebsiteTopNByParam(resourceWebsiteTopN);

        lastResultList.stream().forEach(resultListDemo -> {
            parseLastTotalList.add(resultListDemo.getParseTotalCnt());
        });

        //组装结果
        String reportName = "Top20网站排名趋势";
        if (ObjectUtil.isNotEmpty(websiteTopN.getProvince())){
            reportName = websiteTopN.getProvince()+"Top20网站排名趋势";
        }
        JSONObject finalResult = buildRankReportWithParam(reportName, websiteNameList, parseTotalList, parseLastTotalList);
        return finalResult;
    }


    /**
     * 排名分布 (y轴数必须和系数长度对应)
     */
    public static JSONObject buildRankReportWithParam(String reportName, List<String> websiteName, List<BigInteger> parseCnt, List<BigInteger> parseLastCnt) {
        List<String> legend = Lists.newArrayList();
        JSONObject finalReport = new JSONObject(new LinkedHashMap());
        JSONObject textData = new JSONObject(new LinkedHashMap());
        JSONObject easierParseCnt = new JSONObject(new LinkedHashMap());
        JSONObject nowParseCnt = new JSONObject(new LinkedHashMap());
        JSONObject easierResult = new JSONObject(new LinkedHashMap());
        JSONObject nowResult = new JSONObject(new LinkedHashMap());
        nowResult.put("name","当前时间段");
        nowResult.put("data",parseCnt);
        nowResult.put("type","bar");
        easierResult.put("name","上个时间段");
        easierResult.put("data",parseLastCnt);
        easierResult.put("type","bar");
        nowParseCnt.put("result",nowResult);
        easierParseCnt.put("result",easierResult);
        textData.put("bar_now",nowParseCnt);
        textData.put("bar_easier",easierParseCnt);
        legend.add("当前时间段");
        legend.add("上个时间段");
        finalReport.put("legend",legend);
        finalReport.put("xAxis", websiteName);
        finalReport.put("name", reportName);
        finalReport.put("data", textData);
        return finalReport;
    }

    private ResourceWebsiteTopN checkWithinProvinceDomainRankParam(ResourceWebsiteTopN provinceDomain) throws YamuException{
        if (checkTimeParam(provinceDomain)) {
            Date endDate = new Date();
            Date startDate = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.set(Calendar.SECOND, 0);
            endDate = calendar.getTime();
            calendar.add(Calendar.DATE, -1);
            startDate = calendar.getTime();
            SimpleDateFormat fmt = new SimpleDateFormat(ReportUtils.DEFAULT_FMT);
            provinceDomain.setStartTime(fmt.format(startDate));
            provinceDomain.setEndTime(fmt.format(endDate));
        }
        if(ObjectUtil.isEmpty(provinceDomain.getIspCode()) || provinceDomain.getIspCode().equals("0")) {
            provinceDomain.setIspCode(null);
        }
        if(ObjectUtil.isEmpty(provinceDomain.getProvince())) {
            provinceDomain.setProvince(null);
        }
        if(ObjectUtil.isEmpty(provinceDomain.getCity())) {
            provinceDomain.setCity(null);
        }
        if(ObjectUtil.isEmpty(provinceDomain.getRankNumber()) || provinceDomain.getRankNumber().equals(0L)) {
            provinceDomain.setRankNumber(null);
        }
        ValidationResult validationResult = ValidationUtils.validateEntity(provinceDomain);
        if(validationResult.isHasErrors()) {
            log.error(">>PopularCompanyTrendService checkFindTrendListParam method. param check error: " + validationResult.getErrorMsg().values().stream().findFirst().get());
            throw new YamuException(validationResult.getErrorMsg().values().stream().findFirst().get());
        }
        String websiteAppName = provinceDomain.getWebsiteAppName();
        provinceDomain.setWebsiteAppName(ReportUtils.escapeChar(websiteAppName));
        return provinceDomain;
    }

    private boolean checkTimeParam(ResourceWebsiteTopN queryParam) {
        boolean flag = false;
        if (StrUtil.isEmpty(queryParam.getStartTime()) || queryParam.getStartTime().equals("")) {
            flag = true;
        }
        return flag;
    }
}
