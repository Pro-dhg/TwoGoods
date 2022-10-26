package com.yamu.data.sample.service.resources.service;

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
import com.yamu.data.sample.service.resources.entity.bo.WebsiteNetOutDetailDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteNetOutDownloadBO;
import com.yamu.data.sample.service.resources.entity.bo.WebsiteTopNDownloadBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.ResourceDistributionProvinceVO;
import com.yamu.data.sample.service.resources.entity.vo.ResourceSpecificWebsiteUserSourceVO;
import com.yamu.data.sample.service.resources.mapper.SpecificDomainWebsiteMapper;
import com.yamu.data.sample.service.resources.mapper.SpecificDomainWebsiteNetOutDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author getiejun
 * Date 2020-10-28
 */
@Service
@Slf4j
public class SpecificDomainWebsiteNetOutDetailService {

    @Autowired
    private SpecificDomainWebsiteNetOutDetailMapper websiteNetOutDetailMapper;

    @Autowired
    private SpecificDomainWebsiteMapper specificDomainWebsiteMapper;

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    private static String queryTable = "rpt_resource_specific_domain_website_isp_distribution_";

    public PageResult findDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        checkQueryTimeParam(websiteNetOutDetail);
        Long total = websiteNetOutDetailMapper.countDomainNetOutList(websiteNetOutDetail);
        List<SpecificDomainWebsiteNetOutDetail> dataList= websiteNetOutDetailMapper.findDomainNetOutList(websiteNetOutDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    private void checkQueryTimeParam(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        if (StrUtil.isNotEmpty(websiteNetOutDetail.getQueryTime())) {
            Map<String, String> queryTimeMap = ReportUtils.buildLaterTimeParamByQueryTimeToYOY(websiteNetOutDetail.getQueryType(), websiteNetOutDetail.getQueryTime());
            websiteNetOutDetail.setStartTime(queryTimeMap.get(ReportUtils.NOW_START));
            websiteNetOutDetail.setEndTime(queryTimeMap.get(ReportUtils.NOW_END));
            websiteNetOutDetail.setQueryType(ReportUtils.queryTypeDowngrade(websiteNetOutDetail.getQueryType()));
        } else {
            websiteNetOutDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
    }

    public JSONObject findIspOfDomainNetOut(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        JSONObject jsonObject = new JSONObject();
        checkQueryTimeParam(websiteNetOutDetail);
        List<String> ispData = websiteNetOutDetailMapper.findIspOfDomainNetOut(websiteNetOutDetail);
        jsonObject.put("data", ispData);
        return jsonObject;
    }

    public PageResult findDomainNetOutDetail(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws ParseException {
        checkQueryTimeParam(websiteNetOutDetail);
        Long total = websiteNetOutDetailMapper.countDomainNetOutDetailList(websiteNetOutDetail);
        List<SpecificDomainWebsiteNetOutDetail> dataList= websiteNetOutDetailMapper.findDomainNetOutDetailList(websiteNetOutDetail);
        PageResult pageResult = new PageResult(total, dataList);
        return pageResult;
    }

    public void download(SpecificDomainWebsiteNetOutDetail websiteNetOutDetail, HttpServletResponse response) throws IOException, YamuException, ParseException {
        SpecificDomainWebsiteDetail domainWebsiteDetail = BeanUtil.copyProperties(websiteNetOutDetail, SpecificDomainWebsiteDetail.class, "queryTable");
        checkDownloadParam(domainWebsiteDetail, websiteNetOutDetail);
        // 网站表格导出
        List<WebsiteTopNDownloadBO> topNDownloadBOList = Lists.newArrayList();
        List<WebsiteNetOutDownloadBO> topNNetOutBOList = Lists.newArrayList();
        List<WebsiteNetOutDetailDownloadBO> netOutDetailBOList = Lists.newArrayList();
        List<SpecificDomainWebsiteDetail> websiteTopNDataList = Lists.newArrayList();
        if (StatisticsWayEnum.ALL.getType().equals(domainWebsiteDetail.getStatisticsWay())) {
            websiteTopNDataList = specificDomainWebsiteMapper.queryDataGroupByWebsiteByParam(domainWebsiteDetail);
            websiteTopNDataList.stream().forEach(websiteDetail -> {
                websiteDetail.setTimeRange(domainWebsiteDetail.getStartTime() + "~" + domainWebsiteDetail.getEndTime());
            });
        } else {
            websiteTopNDataList = specificDomainWebsiteMapper.queryDataGroupByParseTimeAndWebsiteByParam(domainWebsiteDetail);
            websiteTopNDataList.stream().forEach(domainWebsite -> {
                domainWebsite.setTimeRange(DateUtils.formatDataToString(domainWebsite.getParseTime(), DateUtils.DEFAULT_FMT));
            });
        }
        websiteTopNDataList.stream().forEach(topNDetail -> {
            WebsiteTopNDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteTopNDownloadBO.class);
            topNDownloadBO.buildRate();
            topNDownloadBOList.add(topNDownloadBO);
        });

        // 查找出网率数据
        List<SpecificDomainWebsiteNetOutDetail> netOutDataList = websiteNetOutDetailMapper.findDomainNetOutListExcel(websiteNetOutDetail,domainWebsiteDetail);
        netOutDataList.stream().forEach(topNDetail -> {
            WebsiteNetOutDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteNetOutDownloadBO.class);
            topNNetOutBOList.add(topNDownloadBO);
        });

        // 查找出网率详细数据
        List<SpecificDomainWebsiteNetOutDetail> netOutDetailDataList = websiteNetOutDetailMapper.findDomainNetOutDetailListExcel(websiteNetOutDetail,domainWebsiteDetail);
        netOutDetailDataList.stream().forEach(topNDetail -> {
            WebsiteNetOutDetailDownloadBO topNDownloadBO = BeanUtil.copyProperties(topNDetail, WebsiteNetOutDetailDownloadBO.class);
            netOutDetailBOList.add(topNDownloadBO);
        });

        ResourceSpecificWebsiteUserSourceVO resourceSpecificWebsiteUserSourceVO = BeanUtil.copyProperties(websiteNetOutDetail, ResourceSpecificWebsiteUserSourceVO.class, "queryTable");
        List<ResourceWebsiteUserSource> userSourceList = specificDomainWebsiteMapper.findUserSourceExcel(resourceSpecificWebsiteUserSourceVO,domainWebsiteDetail);

        ResourceDistributionProvinceVO resourceDistributionProvinceVO = BeanUtil.copyProperties(websiteNetOutDetail, ResourceDistributionProvinceVO.class);
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        List<ResourceIpDetailData> resourceIpDetailList = specificDomainWebsiteMapper.getIpDetailExcelList(resourceDistributionProvinceVO,tableName,domainWebsiteDetail);

        String timeInterval = websiteNetOutDetail.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + websiteNetOutDetail.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "特定域名TopN网站分析报表" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();

        writer.setHeaderAlias(getWebsiteTopNHeaderAlias());
        writer.renameSheet("特定域名TopN网站解析明细报表");
        writer.write(topNDownloadBOList, true);

        writer.setHeaderAlias(getWebsiteNetOutHeaderAlias());
        writer.setSheet("特定域名TopN网站出网域名数据报表");
        writer.write(topNNetOutBOList, true);

        writer.setHeaderAlias(getNetOutDetailHeaderAlias());
        writer.setSheet("特定域名TopN网站出网域名明细报表");
        writer.write(netOutDetailBOList, true);

        writer.setHeaderAlias(getUserSourceHeaderAlias());
        writer.setSheet("特定域名TopN网站来源用户分布");
        writer.write(userSourceList, true);

        writer.setHeaderAlias(getIpDetailHeaderAlias());
        writer.setSheet("特定域名TopN网站资源分布省份");
        writer.write(resourceIpDetailList, true);

        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private void checkDownloadParam(SpecificDomainWebsiteDetail domainWebsiteDetail, SpecificDomainWebsiteNetOutDetail websiteNetOutDetail) throws YamuException {
        if (StrUtil.isEmpty(domainWebsiteDetail.getStartTime()) || StrUtil.isEmpty(domainWebsiteDetail.getEndTime())) {
            domainWebsiteDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }
        if (StrUtil.isEmpty(websiteNetOutDetail.getStartTime()) || StrUtil.isEmpty(websiteNetOutDetail.getEndTime())) {
            websiteNetOutDetail.formatParseTime(DEFAULT_QUERY_TYPE, DEFAULT_INTERVAL_TYPE);
        }

        websiteNetOutDetail.setLimit(10000L);
        websiteNetOutDetail.setOffset(0L);

        domainWebsiteDetail.setLimit(10000L);
        domainWebsiteDetail.setOffset(0L);
    }

    public JSONObject findUserSource(ResourceSpecificWebsiteUserSourceVO resourceSpecificWebsiteUserSourceVO){
        List<ResourceWebsiteUserSource> dataList = specificDomainWebsiteMapper.findUserSource(resourceSpecificWebsiteUserSourceVO);
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

    private Map<String, String> getWebsiteTopNHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("timeRange", "时间");
        aliasMapResult.put("websiteAppName", "网站名称");
        aliasMapResult.put("websiteType", "分类");
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
        return aliasMapResult;
    }

    private Map<String, String> getWebsiteNetOutHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("domainName", "域名");
        aliasMapResult.put("answerFirstIsp", "出网运营商");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("aRecordParseTotalCnt", "IPv4解析次数");
        aliasMapResult.put("netOutParseTotalCnt", "出网次数");
        return aliasMapResult;
    }

    private Map<String, String> getNetOutDetailHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("answerFirstIp", "服务ip");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        aliasMapResult.put("answerFirstProvince", "省份");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("answerFirstIsp", "运营商");
        return aliasMapResult;
    }

    private Map<String, String> getUserSourceHeaderAlias() {
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("websiteAppName", "网站名称");
        aliasMapResult.put("answerFirstCity", "城市");
        aliasMapResult.put("parseTotalCnt", "解析次数");
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

    public List<ResourceDistributionProvinceData> getResourceDistributionProvinceList(ResourceDistributionProvinceVO resourceDistributionProvinceVO){
        String tableName = queryTable + resourceDistributionProvinceVO.getQueryType();
        List<ResourceDistributionProvinceData> list = specificDomainWebsiteMapper.resourceDistributionProvince(resourceDistributionProvinceVO,tableName);
        List<ResourceDistributionProvinceDetail> detailList = specificDomainWebsiteMapper.resourceDistributionProvinceDetail(resourceDistributionProvinceVO,tableName);
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
        Long total = specificDomainWebsiteMapper.getIpDetailTotal(resourceDistributionProvinceVO,tableName);
        List<ResourceIpDetailData> list = specificDomainWebsiteMapper.getIpDetailList(resourceDistributionProvinceVO,tableName);
        resourceIpDetail.setTotal(total);
        resourceIpDetail.setData(list);
        return resourceIpDetail;
    }
}
