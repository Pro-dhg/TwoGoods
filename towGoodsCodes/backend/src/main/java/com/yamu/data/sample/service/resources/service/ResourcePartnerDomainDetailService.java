package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.common.result.PageResult;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificDomainUserSourceVO;
import com.yamu.data.sample.service.resources.mapper.ResourcePartnerDomainDetailMapper;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * @Author Lishuntao
 * @Date 2021/1/21
 */
@Service
public class ResourcePartnerDomainDetailService {
    @Autowired
    ResourcePartnerDomainDetailMapper mapper ;

    private static String queryTable = "rpt_resource_partner_domain_name_detail_";

    public List<ResourcePartnerDomainDetail> findAll(ResourcePartnerDomainDetail queryParam) {
        List<ResourcePartnerDomainDetail> dataList = mapper.findAll(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public List<ResourcePartnerDomainDetail> findAllGroupByParseTime(ResourcePartnerDomainDetail queryParam) throws ParseException {
        checkParam(queryParam);
        List<ResourcePartnerDomainDetail> dataList = mapper.findAllGroupByParseTime(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }
    public List<ResourcePartnerDomainDetail> findAllGroupByIsp(ResourcePartnerDomainDetail queryParam) throws ParseException {
        checkParam(queryParam);
        List<ResourcePartnerDomainDetail> dataList = mapper.findAllGroupByIsp(queryParam);
        setUnknownLast(dataList);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    public PageResult findAllGroupByParseTimeAndDomainName(ResourcePartnerDomainDetail queryParam) {
        PageResult pageResult = new PageResult();
        Long total = Long.valueOf("0");
        List<ResourcePartnerDomainDetail> dataList = org.apache.commons.compress.utils.Lists.newArrayList();
        if(ObjectUtil.equals(queryParam.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
            total = mapper.countAllGroupByParseTimeAndDomainAll(queryParam);
            if (total == null || total == 0) {
                pageResult.setTotal(0);
                pageResult.setData(Lists.newArrayList());
                return pageResult;
            }
            dataList = mapper.findAllGroupByParseTimeAndDomainNameAll(queryParam);
            dataList.stream().forEach(popularCompanyTrendParam -> {
                popularCompanyTrendParam.setTimeRange(queryParam.getStartTime() + "~" + queryParam.getEndTime());
            });
        }else{
            total = mapper.countAllGroupByParseTimeAndDomain(queryParam);
            if (total == null || total == 0) {
                pageResult.setTotal(0);
                pageResult.setData(Lists.newArrayList());
                return pageResult;
            }
            dataList = mapper.findAllGroupByParseTimeAndDomainName(queryParam);
            dataList.stream().forEach(popularCompanyTrendParam -> {
                popularCompanyTrendParam.setTimeRange(DateUtils.formatDataToString(popularCompanyTrendParam.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        dataList.stream().forEach(ResourcePartnerDomainDetail::buildRate);
        pageResult.setTotal(total);
        pageResult.setData(dataList);
        return pageResult;
    }

    public List<ResourcePartnerDomainDetail> findGroupByParseTimeAndDomainName(ResourcePartnerDomainDetail queryParam) {
        List<ResourcePartnerDomainDetail> dataList = mapper.findAllGroupByParseTimeAndDomainName(queryParam);
        if (dataList == null) {
            return Lists.newArrayList();
        } else {
            return dataList;
        }
    }

    private void checkParam(ResourcePartnerDomainDetail queryParam) throws ParseException {
        // 按照时间粒度查询时，会传QueryTime参数，按照时间段查询时，不会传，不需要降维
        if (StrUtil.isNotEmpty(queryParam.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(queryParam.getQueryType(), queryParam.getQueryTime());
            queryParam.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            queryParam.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            queryParam.setQueryType(ReportUtils.queryTypeDowngrade(queryParam.getQueryType()));
        } else {
            queryParam.formatParseTime(queryParam.getQueryType(), "1d");
        }
    }

    public JSONObject findUserSource(ResourceSpecificDomainUserSourceVO resourceSpecificDomainUserSourceVO){
        List<ResourceWebsiteUserSource> dataList = mapper.findUserSource(resourceSpecificDomainUserSourceVO);
        for (ResourceWebsiteUserSource resourceWebsiteUserSource : dataList) {
            if("未知".equals(resourceWebsiteUserSource.getAnswerFirstCity())){
                ResourceWebsiteUserSource data = resourceWebsiteUserSource;
                dataList.remove(resourceWebsiteUserSource);
                dataList.add(data);
                break;
            }
        }
        Map<String, BigInteger> resultDataMap = new LinkedHashMap<>();
        for (ResourceWebsiteUserSource resourceWebsiteUserSource : dataList) {
            resultDataMap.put(resourceWebsiteUserSource.getAnswerFirstCity(), resourceWebsiteUserSource.getParseTotalCnt());
        }
        List<String> xAxisList = new ArrayList<>(resultDataMap.keySet());
        List<BigInteger> totalList = new ArrayList<>(resultDataMap.values());
        // 设置结果集
        Map<String, List> dataMap = Maps.newHashMap();
        dataMap.put(ReportUtils.BAR + "total", totalList);
        //构建报表内容, key为结果map的key, value为含义内容
        Map<String, String> legend = Maps.newLinkedHashMap();
        legend.put(ReportUtils.BAR + "total", "用户数");
        // 报表名称
        String reportName = "来源用户分布";
        JSONObject finalResult = ReportUtils.buildReport(reportName, legend, xAxisList, dataMap);
        return finalResult;
    }

    private void setUnknownLast(List<ResourcePartnerDomainDetail> dataList){
        for (ResourcePartnerDomainDetail resourcePartnerDomainDetail : dataList) {
            if("未知".equals(resourcePartnerDomainDetail.getIsp())){
                ResourcePartnerDomainDetail data = resourcePartnerDomainDetail;
                dataList.remove(resourcePartnerDomainDetail);
                dataList.add(data);
                break;
            }
        }
    }

    public List<ResourceDistributionProvinceData> getResourceDistributionProvinceList(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        List<ResourceDistributionProvinceData> list = mapper.resourceDistributionProvince(resourceDistributionProvinceVO,tableName);
        List<ResourceDistributionProvinceDetail> detailList = mapper.resourceDistributionProvinceDetail(resourceDistributionProvinceVO,tableName);
        for (ResourceDistributionProvinceData resourceDistributionProvinceData : list) {
            List<ResourceDistributionProvinceDetail> dataList = new ArrayList<>();
            for (ResourceDistributionProvinceDetail resourceDistributionProvinceDetail : detailList) {
                if(resourceDistributionProvinceData.getAnswerFirstProvince().equals(resourceDistributionProvinceDetail.getAnswerFirstProvince())){
                    dataList.add(resourceDistributionProvinceDetail);
                }
            }
            resourceDistributionProvinceData.setData(dataList);
        }
        return list;
    }

    public ResourceIpDetail findIpDetail(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        ResourceIpDetail resourceIpDetail = new ResourceIpDetail();
        Long total = mapper.getIpDetailTotal(resourceDistributionProvinceVO,tableName);
        List<ResourceIpDetailData> list = mapper.getIpDetailList(resourceDistributionProvinceVO,tableName);
        resourceIpDetail.setTotal(total);
        resourceIpDetail.setData(list);
        return resourceIpDetail;
    }

    public void download(ResourcePartnerDomainDetail queryParam, HttpServletResponse response) throws IOException {
        List<ResourceDomainTopnDetail> dataList = findAllGroupByParseTimeAndDomainName(queryParam).getData();
        ResourceDistributionProvinceVO resourceDistributionProvinceVO = BeanUtil.copyProperties(queryParam, ResourceDistributionProvinceVO.class);
        if(queryParam.isOtherQtype()){
            resourceDistributionProvinceVO.setQtype("other");
        }
        List<ResourceIpDetailData> resourceIpDetailList = mapper.getIpDetailExcelList(resourceDistributionProvinceVO,queryParam.getQueryTable());
        String fileName = "特定域名明细分析表" + StrUtil.DASHED + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.setHeaderAlias(getWebsiteTopNHeaderAlias());
        writer.renameSheet("特定域名明细分析表");
        writer.setOnlyAlias(true);
        writer.write(dataList, true);

        writer.setHeaderAlias(getIpDetailHeaderAlias());
        writer.setSheet("特定域名资源分布省份");
        writer.setOnlyAlias(true);
        writer.write(resourceIpDetailList, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private Map<String, String> getWebsiteTopNHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("timeRange", "时间");
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("websiteAppName", "网站");
        aliasMapResult.put("companyShortName", "公司");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("parseSuccessCnt", "成功次数");
        aliasMapResult.put("successRate", "成功率");
        aliasMapResult.put("netInParseTotalCnt", "网内次数(IPv4)");
        aliasMapResult.put("netInRate", "本网率");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数(IPv4)");
        aliasMapResult.put("netOutRate", "出网率");
        aliasMapResult.put("withinParseTotalCnt", "本省次数");
        aliasMapResult.put("parseInRate", "本省率");
        aliasMapResult.put("withoutParseTotalCnt", "外省次数");
        aliasMapResult.put("parseOutRate", "出省率");
        aliasMapResult.put("cdnParseTotalCnt", "CDN次数");
        aliasMapResult.put("idcParseTotalCnt", "IDC次数");
        aliasMapResult.put("cacheParseTotalCnt", "CACHE次数");
        return aliasMapResult;
    }

    private Map<String, String> getIpDetailHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("answerFirstIp", "服务ip");
        aliasMapResult.put("parseTotalCnt", "请求次数");
        aliasMapResult.put("province", "省份");
        aliasMapResult.put("city", "城市");
        aliasMapResult.put("answerFirstIsp", "运营商");
        return aliasMapResult;
    }
}
