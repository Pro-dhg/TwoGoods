package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.yamu.data.sample.common.exception.YamuException;
import com.yamu.data.sample.common.utils.ValidationResult;
import com.yamu.data.sample.common.utils.ValidationUtils;
import com.yamu.data.sample.service.resources.entity.po.WithinProvinceDomain;
import com.yamu.data.sample.service.resources.mapper.WithinProvinceDomainMapper;
import com.yamu.data.sample.service.common.util.ReportUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
public class WithinProvinceDomainService {
    @Autowired
    private WithinProvinceDomainMapper domainMapper;

    public JSONObject findWithinProvinceDomainRank(WithinProvinceDomain provinceDomain) throws YamuException {
        WithinProvinceDomain withinProvinceDomain = checkWithinProvinceDomainRankParam(provinceDomain);
        List<String> domainList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<BigInteger> parseLastTotalList = Lists.newArrayList();
        if (checkTimeParam(withinProvinceDomain)) {
            Date endDate = new Date();
            Date startDate = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.set(Calendar.SECOND, 0);
            endDate = calendar.getTime();
            calendar.add(Calendar.DATE, -1);
            startDate = calendar.getTime();
            SimpleDateFormat fmt = new SimpleDateFormat(ReportUtils.DEFAULT_FMT);
            withinProvinceDomain.setStartTime(fmt.format(startDate));
            withinProvinceDomain.setEndTime(fmt.format(endDate));
        }
        List<WithinProvinceDomain> resultList = domainMapper.findWithinProvinceDomainTopNByParam(withinProvinceDomain);
        resultList.stream().forEach(resultListDemo -> {
            if (ObjectUtil.isEmpty(resultListDemo.getDomainName())){
                domainList.add("未知");
            }else {
                domainList.add(resultListDemo.getDomainName());
            }
            parseTotalList.add(resultListDemo.getParseTotalCnt());
            parseLastTotalList.add(resultListDemo.getLastParseTotalCnt());
        });

        //组装结果
        String reportName = "Top20本省域名排名";
        if (ObjectUtil.isNotEmpty(provinceDomain.getDistrictsCode())){
            reportName = provinceDomain.getProvince()+"-Top20本省域名排名";
        }
        JSONObject finalResult = buildRankReportWithParam(reportName, domainList, parseTotalList, parseLastTotalList);
        return finalResult;
    }


    /**
     * 排名分布 (y轴数必须和系数长度对应)
     */
    public static JSONObject buildRankReportWithParam(String reportName, List<String> domain, List<BigInteger> parseCnt, List<BigInteger> parseLastCnt) {
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
        finalReport.put("xAxis", domain);
        finalReport.put("name", reportName);
        finalReport.put("data", textData);
        return finalReport;
    }

    private WithinProvinceDomain checkWithinProvinceDomainRankParam(WithinProvinceDomain provinceDomain) throws YamuException{
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
        String domainName = provinceDomain.getDomainName();
        provinceDomain.setDomainName(ReportUtils.escapeChar(domainName));
        return provinceDomain;
    }

    private boolean checkTimeParam(WithinProvinceDomain queryParam) {
        boolean flag = false;
        if (StrUtil.isEmpty(queryParam.getStartTime()) || queryParam.getStartTime().equals("")) {
            flag = true;
        }
        return flag;
    }
}
