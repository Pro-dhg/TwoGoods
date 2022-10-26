package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.resources.common.utils.pdf.PDFGenerator;
import com.yamu.data.sample.service.resources.entity.bo.*;
import com.yamu.data.sample.service.resources.entity.enumerate.ReportPdfTypeEnum;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.mapper.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ResourceDownloadReportService {

    private final String DEFAULT_INTERVAL_TYPE = "1d";

    private final String DEFAULT_QUERY_TYPE = "1h";

    static String ORDER_BY = "parse_time asc";

    @Autowired
    private ResourceWebsiteTopNDetailMapper websiteTopNDetailMapper;

    @Autowired
    ResourceDomainTopnMapper mapper;

    @Autowired
    ResourceDomainTopnDetailMapper resourceDomainTopnDetailMapper;

    @Autowired
    private PopularDomainTopNTypeMapper popularDomainTopNTypeMapper;

    @Autowired
    private PopularCompanyTrendMapper popularCompanyTrendMapper;

    @Autowired
    private CdnCacheDomainMapper cdnCacheDomainMapper;

    @Autowired
    private ResourceCdnCacheCompanyMapper  cdnCacheCompanyMapper;

    @Autowired
    private AnswerDistributionMapper answerDistributionMapper;

    public void download(ResourceWebsiteReport resourceWebsiteReport, HttpServletResponse response)throws Exception{
        List<ResourceReportPdf> list=new ArrayList<>();
        if(resourceWebsiteReport.getDomainNameTrendChart() != null){//域名解析量趋势图
            DefaultCategoryDataset domainNameTrendChartResult = getDomainNameTrendChart(resourceWebsiteReport);
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("域名解析量趋势图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.LINECHART);
            resourceReportPdf.setData(domainNameTrendChartResult);
            list.add(resourceReportPdf);
        }
        if(resourceWebsiteReport.getWebsiteTopNTrendChart() != null){//topN网站本网率趋势图
            DefaultCategoryDataset websiteTopNTrendChartResult = getWebsiteTopNTrendChart(resourceWebsiteReport);
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("topN网站本网率趋势图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.LINECHART);
            resourceReportPdf.setData(websiteTopNTrendChartResult);
            list.add(resourceReportPdf);
        }
        if(resourceWebsiteReport.getDomainNameTopNTrendChart() != null){//topN域名本网率趋势图
            DefaultCategoryDataset domainNameTopNTrendChartResult = getDomainNameTopNTrendChart(resourceWebsiteReport);
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("topN域名本网率趋势图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.LINECHART);
            resourceReportPdf.setData(domainNameTopNTrendChartResult);
            list.add(resourceReportPdf);
        }
        if(resourceWebsiteReport.getWebsiteTopNTypeTrendChart() != null){//topN分类本网率趋势图
            DefaultCategoryDataset websiteTopNTypeTrendChartResult = getWebsiteTopNTypeTrendChart(resourceWebsiteReport);
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("topN分类本网率趋势图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.LINECHART);
            resourceReportPdf.setData(websiteTopNTypeTrendChartResult);
            list.add(resourceReportPdf);
        }
        if(resourceWebsiteReport.getAnswerDistributionTrendChart() != null){//各省资源分布
            DefaultPieDataset answerDistributionTrendChartResult = getAnswerDistributionTrendChart(resourceWebsiteReport);
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("各省资源分布饼图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.PIECHART);
            resourceReportPdf.setData(answerDistributionTrendChartResult);
            list.add(resourceReportPdf);
        }
        if(resourceWebsiteReport.getOperatorTrendChart() != null){//域名各运营商
            DefaultPieDataset operatorTrendChartResult = getOperatorTrendChart(resourceWebsiteReport);
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("域名各运营商饼图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.PIECHART);
            resourceReportPdf.setData(operatorTrendChartResult);
            list.add(resourceReportPdf);
        }
        if(resourceWebsiteReport.getWebsite() != null){//网站解析次数
            List<ResourceReportPdf> resourceReportPdfList = getWebsite(resourceWebsiteReport);
            list.addAll(resourceReportPdfList);
        }
        if(resourceWebsiteReport.getWebsiteType() != null){//分类解析次数
            List<ResourceReportPdf> resourceReportPdfList = getWebsiteType(resourceWebsiteReport);
            list.addAll(resourceReportPdfList);
        }
        if(resourceWebsiteReport.getWebsiteDomainName() != null){//域名解析次数
            List<ResourceReportPdf> resourceReportPdfList = getWebsiteDomainName(resourceWebsiteReport);
            list.addAll(resourceReportPdfList);
        }
        if(resourceWebsiteReport.getCompany() != null){//公司解析次数
            List<ResourceReportPdf> resourceReportPdfList = getCompany(resourceWebsiteReport);
            list.addAll(resourceReportPdfList);
        }
        if(resourceWebsiteReport.getCdnDomainName() != null){//cdn域名解析次数
            List<ResourceReportPdf> resourceReportPdfList = getCdnDomainName(resourceWebsiteReport);
            list.addAll(resourceReportPdfList);
        }
        if(resourceWebsiteReport.getCdnManufacturer() != null){//cdn厂商解析次数
            List<ResourceReportPdf> resourceReportPdfList = getCdnManufacturer(resourceWebsiteReport);
            list.addAll(resourceReportPdfList);
        }
        String timeInterval = resourceWebsiteReport.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + resourceWebsiteReport.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "资源分析pdf" + StrUtil.DASHED + timeInterval + ".pdf";
        response.setContentType("application/pdf;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        PDFGenerator.generatePdf(list,outputStream);
    }

    public void downloadExcel(ResourceWebsiteReport resourceWebsiteReport, HttpServletResponse response) throws IOException {
        String timeInterval = resourceWebsiteReport.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + resourceWebsiteReport.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = "资源分析报告" + StrUtil.DASHED + timeInterval + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();
        int sheetFlag = 0;
        if(resourceWebsiteReport.getDomainNameTrendChart() != null){//域名解析量趋势图
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            ResourceDomainTopnDetail resourceDomainTopnDetail = new ResourceDomainTopnDetail();
            resourceDomainTopnDetail.setStartTime(resourceWebsiteReport.getStartTime());
            resourceDomainTopnDetail.setEndTime(resourceWebsiteReport.getEndTime());
            resourceDomainTopnDetail.setUserType(resourceWebsiteReport.getUserType());
            resourceDomainTopnDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
            resourceDomainTopnDetail.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
            resourceDomainTopnDetail.setQueryType(resourceWebsiteReport.getQueryType());
            resourceDomainTopnDetail.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
            List<ResourceDomainTopnDetail> dataList = resourceDomainTopnDetailMapper.queryParseTotalByParam(resourceDomainTopnDetail);
            if (dataList != null && dataList.size()>0) {
                List<DomainNameTrendChartExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    DomainNameTrendChartExcelBO domainNameTrendChartExcelBO = BeanUtil.copyProperties(data, DomainNameTrendChartExcelBO.class);
                    domainNameTrendChartExcelBO.setTimeRange(DateUtils.formatDataToString(data.getParseTime(), DateUtils.DEFAULT_FMT));
                    list.add(domainNameTrendChartExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("timeRange", "时间");
                aliasMapResult.put("parseTotalCnt", "解析量");
                aliasMapResult.put("netInParseTotalCnt", "本网次数");
                aliasMapResult.put("netOutParseTotalCnt", "出网次数");
                aliasMapResult.put("withInParseTotalCnt", "本省次数");
                aliasMapResult.put("withOutParseTotalCnt", "出省次数");
                writer.setHeaderAlias(aliasMapResult);
                writer.renameSheet("TopN域名解析量");
                writer.write(list, true);
                sheetFlag = 1;
            }
        }
        if(resourceWebsiteReport.getWebsite() != null){//网站解析次数
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            ResourceWebsiteTopNDetail resourceWebsiteTopNDetail = new ResourceWebsiteTopNDetail();
            resourceWebsiteTopNDetail.setStartTime(resourceWebsiteReport.getStartTime());
            resourceWebsiteTopNDetail.setEndTime(resourceWebsiteReport.getEndTime());
            resourceWebsiteTopNDetail.setUserType(resourceWebsiteReport.getUserType());
            resourceWebsiteTopNDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
            resourceWebsiteTopNDetail.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
            resourceWebsiteTopNDetail.setQueryType(resourceWebsiteReport.getQueryType());
            resourceWebsiteTopNDetail.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
            List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendListByParamAll(resourceWebsiteTopNDetail);
            if (dataList != null && dataList.size()>0) {
                List<WebsiteExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    WebsiteExcelBO websiteExcelBO = BeanUtil.copyProperties(data, WebsiteExcelBO.class);
                    list.add(websiteExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("websiteAppName", "网站名称");
                aliasMapResult.put("parseTotalCnt", "解析次数");
                writer.setHeaderAlias(aliasMapResult);
                if(sheetFlag == 0){
                    writer.renameSheet("网站名称解析量");
                    sheetFlag = 1;
                }else{
                    writer.setSheet("网站名称解析量");
                }
                writer.write(list, true);
            }
        }
        if(resourceWebsiteReport.getWebsiteType() != null){//分类解析次数
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            PopularDomainTopNType popularDomainTopNType = new PopularDomainTopNType();
            popularDomainTopNType.setStartTime(resourceWebsiteReport.getStartTime());
            popularDomainTopNType.setEndTime(resourceWebsiteReport.getEndTime());
            popularDomainTopNType.setUserType(resourceWebsiteReport.getUserType());
            popularDomainTopNType.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
            popularDomainTopNType.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
            popularDomainTopNType.setQueryType(resourceWebsiteReport.getQueryType());
            popularDomainTopNType.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
            List<PopularDomainTopNType> dataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeByParam(popularDomainTopNType);
            if (dataList != null && dataList.size()>0) {
                List<WebsiteTypeExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    WebsiteTypeExcelBO websiteTypeExcelBO = BeanUtil.copyProperties(data, WebsiteTypeExcelBO.class);
                    list.add(websiteTypeExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("domainType", "网站分类");
                aliasMapResult.put("parseTotalCnt", "解析次数");
                writer.setHeaderAlias(aliasMapResult);
                if(sheetFlag == 0){
                    writer.renameSheet("网站分类解析量");
                    sheetFlag = 1;
                }else{
                    writer.setSheet("网站分类解析量");
                }
                writer.write(list, true);
            }
        }
        if(resourceWebsiteReport.getWebsiteDomainName() != null){//域名解析次数
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            ResourceDomainTopnDetail resourceDomainTopnDetail = new ResourceDomainTopnDetail();
            resourceDomainTopnDetail.setStartTime(resourceWebsiteReport.getStartTime());
            resourceDomainTopnDetail.setEndTime(resourceWebsiteReport.getEndTime());
            resourceDomainTopnDetail.setUserType(resourceWebsiteReport.getUserType());
            resourceDomainTopnDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
            resourceDomainTopnDetail.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
            resourceDomainTopnDetail.setQueryType(resourceWebsiteReport.getQueryType());
            resourceDomainTopnDetail.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
            List<ResourceDomainTopnDetail> dataList = resourceDomainTopnDetailMapper.findAllGroupByDomain(resourceDomainTopnDetail);
            if (dataList != null && dataList.size()>0) {
                List<WebsiteDomainNameExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    WebsiteDomainNameExcelBO websiteDomainNameExcelBO = BeanUtil.copyProperties(data, WebsiteDomainNameExcelBO.class);
                    list.add(websiteDomainNameExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("domainName", "域名");
                aliasMapResult.put("parseTotalCnt", "解析次数");
                writer.setHeaderAlias(aliasMapResult);
                if(sheetFlag == 0){
                    writer.renameSheet("域名解析量");
                    sheetFlag = 1;
                }else{
                    writer.setSheet("域名解析量");
                }
                writer.write(list, true);
            }
        }
        if(resourceWebsiteReport.getCompany() != null){//公司解析次数
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            PopularCompanyTrend popularCompanyTrend = new PopularCompanyTrend();
            popularCompanyTrend.setStartTime(resourceWebsiteReport.getStartTime());
            popularCompanyTrend.setEndTime(resourceWebsiteReport.getEndTime());
            popularCompanyTrend.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
            popularCompanyTrend.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
            popularCompanyTrend.setQueryType(resourceWebsiteReport.getQueryType());
            popularCompanyTrend.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
            List<PopularCompanyTrend> dataList = popularCompanyTrendMapper.findTrendListByParamAll(popularCompanyTrend);
            if (dataList != null && dataList.size()>0) {
                List<CompanyExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    CompanyExcelBO companyExcelBO = BeanUtil.copyProperties(data, CompanyExcelBO.class);
                    list.add(companyExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("companyShortName", "公司名");
                aliasMapResult.put("parseTotalCnt", "解析次数");
                writer.setHeaderAlias(aliasMapResult);
                if(sheetFlag == 0){
                    writer.renameSheet("公司解析量");
                    sheetFlag = 1;
                }else{
                    writer.setSheet("公司解析量");
                }
                writer.write(list, true);
            }
        }
        if(resourceWebsiteReport.getCdnDomainName() != null){//cdn域名解析次数
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            ResourceCdnCacheDomain resourceCdnCacheDomain = new ResourceCdnCacheDomain();
            resourceCdnCacheDomain.setStartTime(resourceWebsiteReport.getStartTime());
            resourceCdnCacheDomain.setEndTime(resourceWebsiteReport.getEndTime());
            resourceCdnCacheDomain.setUserType(resourceWebsiteReport.getUserType());
            resourceCdnCacheDomain.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
            resourceCdnCacheDomain.setQueryType(resourceWebsiteReport.getQueryType());
            resourceCdnCacheDomain.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
            List<String> topDomainName = new ArrayList<>();
            Long count = cdnCacheDomainMapper.findDetailCount(resourceCdnCacheDomain, topDomainName);
            List<ResourceCdnCacheDomain> dataList = cdnCacheDomainMapper.findDetailData(resourceCdnCacheDomain, topDomainName);
            if (dataList != null && dataList.size()>0) {
                List<WebsiteDomainNameExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    WebsiteDomainNameExcelBO websiteDomainNameExcelBO = BeanUtil.copyProperties(data, WebsiteDomainNameExcelBO.class);
                    list.add(websiteDomainNameExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("domainName", "cdn域名");
                aliasMapResult.put("parseTotalCnt", "解析次数");
                writer.setHeaderAlias(aliasMapResult);
                if(sheetFlag == 0){
                    writer.renameSheet("cdn域名解析量");
                    sheetFlag = 1;
                }else{
                    writer.setSheet("cdn域名解析量");
                }
                writer.write(list, true);
            }
        }
        if(resourceWebsiteReport.getCdnManufacturer() != null){//cdn厂商解析次数
            resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
            ResourceCdnCacheCompany resourceCdnCacheCompany = new ResourceCdnCacheCompany();
            resourceCdnCacheCompany.setStartTime(resourceWebsiteReport.getStartTime());
            resourceCdnCacheCompany.setEndTime(resourceWebsiteReport.getEndTime());
            resourceCdnCacheCompany.setUserType(resourceWebsiteReport.getUserType());
            resourceCdnCacheCompany.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
            resourceCdnCacheCompany.setQueryType(resourceWebsiteReport.getQueryType());
            resourceCdnCacheCompany.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
            resourceCdnCacheCompany.formatParseTime();
            List<ResourceCdnCacheCompany> dataList = cdnCacheCompanyMapper.findTableDataByParamAll(resourceCdnCacheCompany);
            if (dataList != null && dataList.size()>0) {
                List<CdnManufacturerExcelBO> list = new ArrayList<>();
                dataList.stream().forEach(data -> {
                    CdnManufacturerExcelBO cdnManufacturerExcelBO = BeanUtil.copyProperties(data, CdnManufacturerExcelBO.class);
                    list.add(cdnManufacturerExcelBO);
                });
                Map aliasMapResult = Maps.newLinkedHashMap();
                aliasMapResult.put("business", "cdn厂商");
                aliasMapResult.put("parseTotalCnt", "解析次数");
                writer.setHeaderAlias(aliasMapResult);
                if(sheetFlag == 0){
                    writer.renameSheet("cdn厂商解析量");
                    sheetFlag = 1;
                }else{
                    writer.setSheet("cdn厂商解析量");
                }
                writer.write(list, true);
            }
        }
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    public DefaultCategoryDataset getWebsiteTopNTrendChart(ResourceWebsiteReport resourceWebsiteReport){
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceWebsiteTopNDetail websiteTopNDetail = new ResourceWebsiteTopNDetail();
        websiteTopNDetail.setStartTime(resourceWebsiteReport.getStartTime());
        websiteTopNDetail.setEndTime(resourceWebsiteReport.getEndTime());
        websiteTopNDetail.setUserType(resourceWebsiteReport.getUserType());
        websiteTopNDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        websiteTopNDetail.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
        websiteTopNDetail.setQueryType(resourceWebsiteReport.getQueryType());
        websiteTopNDetail.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendReportGroupByParseByParam(websiteTopNDetail,false);
        dataList.stream().forEach(ResourceWebsiteTopNDetail::buildRate);
        if (dataList == null) {
            return defaultCategoryDataset;
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        for (ResourceWebsiteTopNDetail data : dataList) {
            String timeStr = format.format(data.getParseTime());
            defaultCategoryDataset.setValue(data.getNetInRate(), "本网率" , timeStr);
        }
        return defaultCategoryDataset;
    }

    public DefaultCategoryDataset getDomainNameTopNTrendChart(ResourceWebsiteReport resourceWebsiteReport){
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceDomainTopnDetail resourceDomainTopnDetail = new ResourceDomainTopnDetail();
        resourceDomainTopnDetail.setStartTime(resourceWebsiteReport.getStartTime());
        resourceDomainTopnDetail.setEndTime(resourceWebsiteReport.getEndTime());
        resourceDomainTopnDetail.setUserType(resourceWebsiteReport.getUserType());
        resourceDomainTopnDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        resourceDomainTopnDetail.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
        resourceDomainTopnDetail.setQueryType(resourceWebsiteReport.getQueryType());
        resourceDomainTopnDetail.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
        List<ResourceDomainTopnDetail> dataList = resourceDomainTopnDetailMapper.findAllGroupByTopnParseTime(resourceDomainTopnDetail);
        dataList.stream().forEach(ResourceDomainTopnDetail::buildRate);
        if (dataList == null) {
            return defaultCategoryDataset;
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        for (ResourceDomainTopnDetail data : dataList) {
            String timeStr = format.format(data.getParseTime());
            defaultCategoryDataset.setValue(data.getNetInRate(), "本网率" , timeStr);
        }
        return defaultCategoryDataset;
    }

    public DefaultCategoryDataset getWebsiteTopNTypeTrendChart(ResourceWebsiteReport resourceWebsiteReport){
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        PopularDomainTopNType popularDomainTopNType = new PopularDomainTopNType();
        popularDomainTopNType.setStartTime(resourceWebsiteReport.getStartTime());
        popularDomainTopNType.setEndTime(resourceWebsiteReport.getEndTime());
        popularDomainTopNType.setUserType(resourceWebsiteReport.getUserType());
        popularDomainTopNType.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        popularDomainTopNType.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
        popularDomainTopNType.setQueryType(resourceWebsiteReport.getQueryType());
        popularDomainTopNType.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
        List<PopularDomainTopNType> dataList = popularDomainTopNTypeMapper.queryDataGroupByParseTimeByParam(popularDomainTopNType);
        dataList.stream().forEach(PopularDomainTopNType::buildRate);
        if (dataList == null) {
            return defaultCategoryDataset;
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        for (PopularDomainTopNType data : dataList) {
            String timeStr = format.format(data.getParseTime());
            defaultCategoryDataset.setValue(data.getNetInRate(), "本网率" , timeStr);
        }
        return defaultCategoryDataset;
    }

    public List<ResourceReportPdf> getWebsite(ResourceWebsiteReport resourceWebsiteReport){
        List<ResourceReportPdf> resourceReportPdfList=new ArrayList<>();
        List<DefaultCategoryDataset> defaultCategoryDatasetList = new ArrayList<>();
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceWebsiteTopNDetail resourceWebsiteTopNDetail = new ResourceWebsiteTopNDetail();
        resourceWebsiteTopNDetail.setStartTime(resourceWebsiteReport.getStartTime());
        resourceWebsiteTopNDetail.setEndTime(resourceWebsiteReport.getEndTime());
        resourceWebsiteTopNDetail.setUserType(resourceWebsiteReport.getUserType());
        resourceWebsiteTopNDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        resourceWebsiteTopNDetail.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
        resourceWebsiteTopNDetail.setQueryType(resourceWebsiteReport.getQueryType());
        resourceWebsiteTopNDetail.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
        List<ResourceWebsiteTopNDetail> dataList = websiteTopNDetailMapper.findTrendListByParamAll(resourceWebsiteTopNDetail);
        dataList.stream().forEach(websiteTopN ->{
            if (ObjectUtil.isEmpty(websiteTopN.getWebsiteAppName())){
                websiteTopN.setWebsiteAppName("未知");
            }
            if (ObjectUtil.isEmpty(websiteTopN.getWebsiteType())) {
                websiteTopN.setWebsiteType("未知");
            }
        });
        if (dataList == null || dataList.size() == 0) {
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("网站解析次数柱状图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
            resourceReportPdf.setData(new DefaultCategoryDataset());
            resourceReportPdfList.add(resourceReportPdf);
            return resourceReportPdfList;
        }
        int listNum=1;
        for(int i = 0 ;i < dataList.size();i++){
            defaultCategoryDataset.setValue(dataList.get(i).getParseTotalCnt(), "解析次数" , dataList.get(i).getWebsiteAppName());
            if(listNum == 30){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
                defaultCategoryDataset = new DefaultCategoryDataset();
                listNum=0;
            }else if(i == dataList.size()-1){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
            }
            listNum++;
        }
        for(int i = 0;i < defaultCategoryDatasetList.size();i++){
            if(i == 0){
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setTitleName("网站解析次数柱状图");
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }else{
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }
        }
        return resourceReportPdfList;
    }

    public List<ResourceReportPdf> getWebsiteType(ResourceWebsiteReport resourceWebsiteReport){
        List<ResourceReportPdf> resourceReportPdfList=new ArrayList<>();
        List<DefaultCategoryDataset> defaultCategoryDatasetList = new ArrayList<>();
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        PopularDomainTopNType popularDomainTopNType = new PopularDomainTopNType();
        popularDomainTopNType.setStartTime(resourceWebsiteReport.getStartTime());
        popularDomainTopNType.setEndTime(resourceWebsiteReport.getEndTime());
        popularDomainTopNType.setUserType(resourceWebsiteReport.getUserType());
        popularDomainTopNType.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        popularDomainTopNType.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
        popularDomainTopNType.setQueryType(resourceWebsiteReport.getQueryType());
        popularDomainTopNType.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
        List<PopularDomainTopNType> dataList = popularDomainTopNTypeMapper.queryDataGroupByWebsiteTypeByParam(popularDomainTopNType);
        if (dataList == null || dataList.size() == 0) {
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("分类解析次数柱状图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
            resourceReportPdf.setData(new DefaultCategoryDataset());
            resourceReportPdfList.add(resourceReportPdf);
            return resourceReportPdfList;
        }
        int listNum=1;
        for(int i = 0 ;i < dataList.size();i++){
            defaultCategoryDataset.setValue(dataList.get(i).getParseTotalCnt(), "解析次数" , dataList.get(i).getDomainType());
            if(listNum == 25){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
                defaultCategoryDataset = new DefaultCategoryDataset();
                listNum=0;
            }else if(i == dataList.size()-1){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
            }
            listNum++;
        }
        for(int i = 0;i < defaultCategoryDatasetList.size();i++){
            if(i == 0){
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setTitleName("分类解析次数柱状图");
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }else{
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }
        }
        return resourceReportPdfList;
    }

    public List<ResourceReportPdf> getWebsiteDomainName(ResourceWebsiteReport resourceWebsiteReport){
        List<ResourceReportPdf> resourceReportPdfList=new ArrayList<>();
        List<DefaultCategoryDataset> defaultCategoryDatasetList = new ArrayList<>();
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceDomainTopnDetail resourceDomainTopnDetail = new ResourceDomainTopnDetail();
        resourceDomainTopnDetail.setStartTime(resourceWebsiteReport.getStartTime());
        resourceDomainTopnDetail.setEndTime(resourceWebsiteReport.getEndTime());
        resourceDomainTopnDetail.setUserType(resourceWebsiteReport.getUserType());
        resourceDomainTopnDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        resourceDomainTopnDetail.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
        resourceDomainTopnDetail.setQueryType(resourceWebsiteReport.getQueryType());
        resourceDomainTopnDetail.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
        List<ResourceDomainTopnDetail> dataList = resourceDomainTopnDetailMapper.findAllGroupByDomain(resourceDomainTopnDetail);
        if (dataList == null || dataList.size() == 0) {
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("域名解析次数柱状图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
            resourceReportPdf.setData(new DefaultCategoryDataset());
            resourceReportPdfList.add(resourceReportPdf);
            return resourceReportPdfList;
        }
        int listNum=1;
        for(int i = 0 ;i < dataList.size();i++){
            defaultCategoryDataset.setValue(dataList.get(i).getParseTotalCnt(), "解析次数" , dataList.get(i).getDomainName());
            if(listNum == 25){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
                defaultCategoryDataset = new DefaultCategoryDataset();
                listNum=0;
            }else if(i == dataList.size()-1){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
            }
            listNum++;
        }
        for(int i = 0;i < defaultCategoryDatasetList.size();i++){
            if(i == 0){
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setTitleName("域名解析次数柱状图");
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }else{
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }
        }
        return resourceReportPdfList;
    }

    public List<ResourceReportPdf> getCompany(ResourceWebsiteReport resourceWebsiteReport){
        List<ResourceReportPdf> resourceReportPdfList=new ArrayList<>();
        List<DefaultCategoryDataset> defaultCategoryDatasetList = new ArrayList<>();
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        PopularCompanyTrend popularCompanyTrend = new PopularCompanyTrend();
        popularCompanyTrend.setStartTime(resourceWebsiteReport.getStartTime());
        popularCompanyTrend.setEndTime(resourceWebsiteReport.getEndTime());
        popularCompanyTrend.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        popularCompanyTrend.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
        popularCompanyTrend.setQueryType(resourceWebsiteReport.getQueryType());
        popularCompanyTrend.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
        List<PopularCompanyTrend> dataList = popularCompanyTrendMapper.findTrendListByParamAll(popularCompanyTrend);
        if (dataList == null || dataList.size() == 0) {
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("公司解析次数柱状图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
            resourceReportPdf.setData(new DefaultCategoryDataset());
            resourceReportPdfList.add(resourceReportPdf);
            return resourceReportPdfList;
        }
        int listNum=1;
        for(int i = 0 ;i < dataList.size();i++){
            defaultCategoryDataset.setValue(dataList.get(i).getParseTotalCnt(), "解析次数" , dataList.get(i).getCompanyShortName());
            if(listNum == 25){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
                defaultCategoryDataset = new DefaultCategoryDataset();
                listNum=0;
            }else if(i == dataList.size()-1){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
            }
            listNum++;
        }
        for(int i = 0;i < defaultCategoryDatasetList.size();i++){
            if(i == 0){
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setTitleName("公司解析次数柱状图");
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }else{
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }
        }
        return resourceReportPdfList;
    }

    public List<ResourceReportPdf> getCdnDomainName(ResourceWebsiteReport resourceWebsiteReport){
        List<ResourceReportPdf> resourceReportPdfList=new ArrayList<>();
        List<DefaultCategoryDataset> defaultCategoryDatasetList = new ArrayList<>();
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceCdnCacheDomain resourceCdnCacheDomain = new ResourceCdnCacheDomain();
        resourceCdnCacheDomain.setStartTime(resourceWebsiteReport.getStartTime());
        resourceCdnCacheDomain.setEndTime(resourceWebsiteReport.getEndTime());
        resourceCdnCacheDomain.setUserType(resourceWebsiteReport.getUserType());
        resourceCdnCacheDomain.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
        resourceCdnCacheDomain.setQueryType(resourceWebsiteReport.getQueryType());
        resourceCdnCacheDomain.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
        List<String> topDomainName = new ArrayList<>();
        Long count = cdnCacheDomainMapper.findDetailCount(resourceCdnCacheDomain, topDomainName);
        List<ResourceCdnCacheDomain> dataList = cdnCacheDomainMapper.findDetailData(resourceCdnCacheDomain, topDomainName);
        if (dataList == null || dataList.size() == 0) {
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("cdn域名解析次数柱状图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
            resourceReportPdf.setData(new DefaultCategoryDataset());
            resourceReportPdfList.add(resourceReportPdf);
            return resourceReportPdfList;
        }
        int listNum=1;
        for(int i = 0 ;i < dataList.size();i++){
            defaultCategoryDataset.setValue(dataList.get(i).getParseTotalCnt(), "解析次数" , dataList.get(i).getDomainName());
            if(listNum == 25){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
                defaultCategoryDataset = new DefaultCategoryDataset();
                listNum=0;
            }else if(i == dataList.size()-1){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
            }
            listNum++;
        }
        for(int i = 0;i < defaultCategoryDatasetList.size();i++){
            if(i == 0){
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setTitleName("cdn域名解析次数柱状图");
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }else{
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }
        }
        return resourceReportPdfList;
    }

    public List<ResourceReportPdf> getCdnManufacturer(ResourceWebsiteReport resourceWebsiteReport){
        List<ResourceReportPdf> resourceReportPdfList=new ArrayList<>();
        List<DefaultCategoryDataset> defaultCategoryDatasetList = new ArrayList<>();
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceCdnCacheCompany resourceCdnCacheCompany = new ResourceCdnCacheCompany();
        resourceCdnCacheCompany.setStartTime(resourceWebsiteReport.getStartTime());
        resourceCdnCacheCompany.setEndTime(resourceWebsiteReport.getEndTime());
        resourceCdnCacheCompany.setUserType(resourceWebsiteReport.getUserType());
        resourceCdnCacheCompany.setRankNumber(resourceWebsiteReport.getWebsiteRankNumber());
        resourceCdnCacheCompany.setQueryType(resourceWebsiteReport.getQueryType());
        resourceCdnCacheCompany.setLimit(resourceWebsiteReport.getWebsiteRankNumber());
        resourceCdnCacheCompany.formatParseTime();
        List<ResourceCdnCacheCompany> dataList = cdnCacheCompanyMapper.findTableDataByParamAll(resourceCdnCacheCompany);
        if (dataList == null || dataList.size() == 0) {
            ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
            resourceReportPdf.setTitleName("cdn厂商解析次数柱状图");
            resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
            resourceReportPdf.setData(new DefaultCategoryDataset());
            resourceReportPdfList.add(resourceReportPdf);
            return resourceReportPdfList;
        }
        int listNum=1;
        for(int i = 0 ;i < dataList.size();i++){
            defaultCategoryDataset.setValue(dataList.get(i).getParseTotalCnt(), "解析次数" , dataList.get(i).getBusiness());
            if(listNum == 25){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
                defaultCategoryDataset = new DefaultCategoryDataset();
                listNum=0;
            }else if(i == dataList.size()-1){
                defaultCategoryDatasetList.add(defaultCategoryDataset);
            }
            listNum++;
        }
        for(int i = 0;i < defaultCategoryDatasetList.size();i++){
            if(i == 0){
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setTitleName("cdn厂商解析次数柱状图");
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }else{
                ResourceReportPdf resourceReportPdf = new ResourceReportPdf();
                resourceReportPdf.setReportPdfType(ReportPdfTypeEnum.HISTOGRAM);
                resourceReportPdf.setData(defaultCategoryDatasetList.get(i));
                resourceReportPdfList.add(resourceReportPdf);
            }
        }
        return resourceReportPdfList;
    }

    public DefaultCategoryDataset getDomainNameTrendChart(ResourceWebsiteReport resourceWebsiteReport){
        DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        ResourceDomainTopnDetail resourceDomainTopnDetail = new ResourceDomainTopnDetail();
        resourceDomainTopnDetail.setStartTime(resourceWebsiteReport.getStartTime());
        resourceDomainTopnDetail.setEndTime(resourceWebsiteReport.getEndTime());
        resourceDomainTopnDetail.setUserType(resourceWebsiteReport.getUserType());
        resourceDomainTopnDetail.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        resourceDomainTopnDetail.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
        resourceDomainTopnDetail.setQueryType(resourceWebsiteReport.getQueryType());
        resourceDomainTopnDetail.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
        List<ResourceDomainTopnDetail> dataList = resourceDomainTopnDetailMapper.queryParseTotalByParam(resourceDomainTopnDetail);
        if (dataList == null) {
            return defaultCategoryDataset;
        }
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        for (ResourceDomainTopnDetail data : dataList) {
            String timeStr = format.format(data.getParseTime());
            defaultCategoryDataset.setValue(data.getParseTotalCnt() , "解析量" , timeStr);
            defaultCategoryDataset.setValue(data.getNetInParseTotalCnt() , "本网次数" , timeStr);
            defaultCategoryDataset.setValue(data.getNetOutParseTotalCnt() , "出网次数" , timeStr);
            defaultCategoryDataset.setValue(data.getWithInParseTotalCnt() , "本省次数" , timeStr);
            defaultCategoryDataset.setValue(data.getWithOutParseTotalCnt() , "出省次数" , timeStr);
        }
        return defaultCategoryDataset;
    }

    public DefaultPieDataset getAnswerDistributionTrendChart(ResourceWebsiteReport resourceWebsiteReport){
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        AnswerDistributionBO answerDistributionBO = new AnswerDistributionBO();
        answerDistributionBO.setStartTime(resourceWebsiteReport.getStartTime());
        answerDistributionBO.setEndTime(resourceWebsiteReport.getEndTime());
        answerDistributionBO.setUserType(resourceWebsiteReport.getUserType());
        answerDistributionBO.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        answerDistributionBO.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
        answerDistributionBO.setQueryType(resourceWebsiteReport.getQueryType());
        answerDistributionBO.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
        List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceAllPdf(answerDistributionBO);
        DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        if( answerDistributionList != null && answerDistributionList.size() > 0){
            if(answerDistributionList.size()>10){
                for(int i = 0; i < 10;i++){
                    defaultPieDataset.setValue(answerDistributionList.get(i).getProvince() + "(" + answerDistributionList.get(i).getParseTotalCnt() + ")"
                            ,answerDistributionList.get(i).getParseTotalCnt());
                }
                BigInteger otherNum=BigInteger.ZERO;
                for(int i = 10; i < answerDistributionList.size();i++){
                    otherNum = otherNum.add(answerDistributionList.get(i).getParseTotalCnt());
                }
                defaultPieDataset.setValue("其他(" + otherNum + ")",otherNum);
            }else{
                for (AnswerDistribution data : answerDistributionList) {
                    defaultPieDataset.setValue(data.getProvince() + "(" + data.getParseTotalCnt() + ")",data.getParseTotalCnt());
                }
            }
        }
        return defaultPieDataset;
    }

    public DefaultPieDataset getOperatorTrendChart(ResourceWebsiteReport resourceWebsiteReport){
        resourceWebsiteReport.formatParseTime(resourceWebsiteReport.getQueryType(), DEFAULT_INTERVAL_TYPE);
        AnswerDistributionBO answerDistributionBO = new AnswerDistributionBO();
        answerDistributionBO.setStartTime(resourceWebsiteReport.getStartTime());
        answerDistributionBO.setEndTime(resourceWebsiteReport.getEndTime());
        answerDistributionBO.setUserType(resourceWebsiteReport.getUserType());
        answerDistributionBO.setAnswerFirstIsp(resourceWebsiteReport.getAnswerFirstIsp());
        answerDistributionBO.setRankNumber(resourceWebsiteReport.getTrendChartRankNumber());
        answerDistributionBO.setQueryType(resourceWebsiteReport.getQueryType());
        answerDistributionBO.setLimit(resourceWebsiteReport.getTrendChartRankNumber());
        List<AnswerDistribution> list = answerDistributionMapper.findGroupByIsp(answerDistributionBO);
        DefaultPieDataset defaultPieDataset = new DefaultPieDataset();
        if( list != null && list.size() > 0){
            for (AnswerDistribution data : list) {
                defaultPieDataset.setValue(data.getIsp() + "(" + data.getParseTotalCnt() + ")",data.getParseTotalCnt());
            }
        }
        return defaultPieDataset;
    }
}