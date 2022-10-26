package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.entity.ConstantEntity;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.entity.bo.*;
import com.yamu.data.sample.service.resources.entity.po.ResourceWebsiteUserSource;
import com.yamu.data.sample.service.resources.entity.po.VisualizationHomeData;
import com.yamu.data.sample.service.resources.entity.vo.AnswerDistributionRateVO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeDataVO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeTopNTrendVO;
import com.yamu.data.sample.service.resources.entity.vo.VisualizationHomeTopTenParse;
import com.yamu.data.sample.service.resources.mapper.VisualizationHomeMapper;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.*;

/**
 * @Author yuyuan.Dong
 * @Date 2022/1/12
 * @DESC
 */
@Service
public class ResourceVisualizationHomeDownloadService {

    @Autowired
    private VisualizationHomeMapper mapper;

    @Autowired
    private ResourceVisualizationHomeService visualizationHomeService;

    public void downloadExcel(VisualizationHomeDataVO visualizationHomeDataVO, HttpServletResponse response) throws IOException {
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO, ConstantEntity.INTERVAL_10MIN);
        String timeInterval = visualizationHomeDataVO.getStartTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "")
                + StrUtil.UNDERLINE + visualizationHomeDataVO.getEndTime().split(StrUtil.SPACE)[0].replace(StrUtil.DASHED, "");
        String fileName = null;
        if ("固网".equals(visualizationHomeDataVO.getUserType())){
            fileName = "可视化首页(固网)" + StrUtil.DASHED + timeInterval + ".xls";
        }else if ("手机".equals(visualizationHomeDataVO.getUserType())){
            fileName = "可视化首页(手机)" + StrUtil.DASHED + timeInterval + ".xls";
        }else {
            fileName = "可视化首页" + StrUtil.DASHED + timeInterval + ".xls";
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();
        //关键指标业务统计
        extractedKeyBusiness(visualizationHomeDataVO, writer);
        //解析趋势
        extractedPopularDomainData(visualizationHomeDataVO, writer);
        //地图图
        extractedMapData(visualizationHomeDataVO, writer);
        //用户分布
        extractedUserData(visualizationHomeDataVO, writer);
        //top10应用
        extractedTop10Website(visualizationHomeDataVO, writer);
        //top10分类
        extractedTop10Type(visualizationHomeDataVO, writer);
        //top10域名
        extractedTop10Domain(visualizationHomeDataVO, writer);
        //运营商占比
        extractedIspProportion(visualizationHomeDataVO, writer);
        //TopN应用比重图
        extractedWebsiteProportion(visualizationHomeDataVO, writer);
        //TopN分类比重图
        extractedTypeProportion(visualizationHomeDataVO, writer);
        //TopN域名比重图
        extractedDomainProportion(visualizationHomeDataVO, writer);
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    public void extractedKeyBusiness(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer){
        List<VisualizationHomeKeyBusinessBO> dataList = visualizationHomeService.keyBusiness(visualizationHomeDataVO);
        if (dataList != null && dataList.size() > 0) {
            List<KeyBusinessDownloadBO> list = new ArrayList<>();
            dataList.stream().forEach(data -> {
                KeyBusinessDownloadBO websiteExcelBO = BeanUtil.copyProperties(data, KeyBusinessDownloadBO.class);
                if ("TopN域名解析量".equals(websiteExcelBO.getName()) && ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())){
                    websiteExcelBO.setName("Top"+visualizationHomeDataVO.getRankNumber()+"域名解析量");
                }
                if ("解析量".equals(websiteExcelBO.getName()) || "本网率".equals(websiteExcelBO.getName()) || "本省率".equals(websiteExcelBO.getName()) || "缓存成功率".equals(websiteExcelBO.getName()) || "递归成功率".equals(websiteExcelBO.getName())){
                    websiteExcelBO.setMomRate(checkKeyBusinessPram(data.getMomRate()));
                    websiteExcelBO.setYoyRate(checkKeyBusinessPram(data.getYoyRate()));
                    websiteExcelBO.setPopRate("/");
                }else {
                    websiteExcelBO.setMomRate("/");
                    websiteExcelBO.setYoyRate(checkKeyBusinessPram(data.getYoyRate()));
                    websiteExcelBO.setPopRate(checkKeyBusinessPram(data.getPopRate()));
                }
                list.add(websiteExcelBO);
            });
            Map aliasMapResult = Maps.newLinkedHashMap();
            aliasMapResult.put("name", "项目");
            aliasMapResult.put("yoyRate", "环比");
            aliasMapResult.put("momRate", "同比");
            aliasMapResult.put("popRate", "占比");
            writer.setHeaderAlias(aliasMapResult);
            writer.renameSheet("关键业务指标");
            writer.write(list, true);
        }
    }

    public String checkKeyBusinessPram(Double data){
        if (data != null){
            return StrUtils.convertDoubleToPercent(data,0);
        }else {
            return StrUtils.convertDoubleToPercent(0.0,0);
        }
    }

    public String checkPram(Double data){
        if (data != null){
            return StrUtils.convertDoubleToPercent(data,2);
        }else {
            return StrUtils.convertDoubleToPercent(0.0,2);
        }
    }

    public void extractedUserData(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        List<ResourceWebsiteUserSource> list = visualizationHomeService.findUserSource(visualizationHomeDataVO);
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("answerFirstCity", "城市名称");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        writer.setHeaderAlias(aliasMapResult);
        writer.setSheet("用户分布");
        writer.write(list, true);
    }


    public void extractedPopularDomainData(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        VisualizationHomeTopnBO allList = visualizationHomeService.topNTrend(visualizationHomeDataVO);
        List<VisualizationHomeTopNTrendVO> list = allList.getTopNTrendList();
        List<PopularDomainNameBO> domainNameBOList = new ArrayList<>();
        list.stream().forEach(data ->{
            domainNameBOList.add(parseData(data.getParseTotalCnt(),"解析次数",data.getParseTime()));
            domainNameBOList.add(parseData(data.getParseSuccessCnt(),"成功次数",data.getParseTime()));
            domainNameBOList.add(parseData(data.getParseFailCnt(),"失败次数",data.getParseTime()));
            domainNameBOList.add(parseData(data.getARecordParseTotalCnt(),"IPv4解析次数",data.getParseTime()));
            domainNameBOList.add(parseData(data.getAaaaRecordParseTotalCnt(),"IPv6解析次数",data.getParseTime()));
            domainNameBOList.add(parseData(data.getNetInParseTotalCnt(),"本网解析次数",data.getParseTime()));
            domainNameBOList.add(parseData(data.getNetOutParseTotalCnt(),"网外解析次数",data.getParseTime()));
        });
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("parseTime", "时间");
        aliasMapResult.put("name", "项目");
        aliasMapResult.put("parseTotalCnt", "解析次数");
        writer.setHeaderAlias(aliasMapResult);
        writer.setSheet("热点域名解析分析");
        writer.write(domainNameBOList, true);
    }

    public PopularDomainNameBO parseData(BigInteger data, String name, Date parseTime) {
        PopularDomainNameBO popularDomainNameBO = new PopularDomainNameBO();
        popularDomainNameBO.setParseTime(parseTime);
        popularDomainNameBO.setName(name);
        popularDomainNameBO.setParseTotalCnt(data);
        return popularDomainNameBO;
    }

    public void extractedMapData(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        List<VisualizationHomeMapBO> list = visualizationHomeService.getMap(visualizationHomeDataVO);
            Map aliasMapResult = Maps.newLinkedHashMap();
            aliasMapResult.put("distribution", "省份名称");
            aliasMapResult.put("parseTotalCnt", "解析次数");
            aliasMapResult.put("rankNumber", "排名");
            writer.setHeaderAlias(aliasMapResult);
            writer.setSheet("资源分布中国地图");
            writer.write(list, true);
    }

    private void extractedDomainProportion(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        VisualizationHomeData visualizationHomeDataTop = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeDataAll = new VisualizationHomeData();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
            visualizationHomeDataTop = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType(),"true","false");
        }else {
            visualizationHomeDataTop = mapper.topNDomainNameProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNDomainNameProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType(),"true","false");
        }
        TopNProportion topNProportion = new TopNProportion();
        TopNProportion otherTopNProportion = new TopNProportion();
        TopNProportion allProportion = new TopNProportion();
        List<TopNProportion> list = new ArrayList<>();
        VisualizationHomeData visualizationHomeData = new VisualizationHomeData();
        topNProportion.setName("Top10域名");
        topNProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataTop.getParseTotalCnt(),visualizationHomeDataAll.getParseTotalCnt())));
        list.add(topNProportion);
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber()) && visualizationHomeDataVO.getRankNumber()!=10L) {
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
                visualizationHomeData = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","false");
            }else {
                visualizationHomeData = mapper.topNDomainNameProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","false");
            }
            otherTopNProportion.setName("其他TopN");
            otherTopNProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeData.getParseTotalCnt(),visualizationHomeDataAll.getParseTotalCnt())));
            list.add(otherTopNProportion);
        }

        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber()) && visualizationHomeDataVO.getRankNumber()!=10L) {
            allProportion.setName("其他域名");
            allProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeData.getParseTotalCnt()).subtract(visualizationHomeDataTop.getParseTotalCnt()),visualizationHomeDataAll.getParseTotalCnt())));
        }else {
            allProportion.setName("其他域名");
            allProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeDataTop.getParseTotalCnt()),visualizationHomeDataAll.getParseTotalCnt())));
        }
        list.add(allProportion);
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("name", "项目");
        aliasMapResult.put("proportion", "占比");
        writer.setHeaderAlias(aliasMapResult);
        writer.setSheet("TopN域名比重图");
        writer.write(list, true);
    }

    private void extractedWebsiteProportion(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        VisualizationHomeData visualizationHomeDataTop = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeDataAll = new VisualizationHomeData();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            visualizationHomeDataTop = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType(),"true","false");
        }else {
            visualizationHomeDataTop = mapper.topNWebsiteProportion(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNWebsiteProportion(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType(),"true","false");
        }
        TopNProportion topNProportion = new TopNProportion();
        TopNProportion otherTopNProportion = new TopNProportion();
        TopNProportion allProportion = new TopNProportion();
        List<TopNProportion> list = new ArrayList<>();
        VisualizationHomeData visualizationHomeData = new VisualizationHomeData();
        topNProportion.setName("Top10应用");
        topNProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataTop.getParseTotalCnt(),visualizationHomeDataAll.getParseTotalCnt())));
        list.add(topNProportion);
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber()) && visualizationHomeDataVO.getRankNumber()!=10L) {
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
                visualizationHomeData = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","false");
            }else {
                visualizationHomeData = mapper.topNWebsiteProportion(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType(),"false","false");
            }
            otherTopNProportion.setName("其他TopN");
            otherTopNProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeData.getParseTotalCnt(),visualizationHomeDataAll.getParseTotalCnt())));
            list.add(otherTopNProportion);
        }

        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber()) && visualizationHomeDataVO.getRankNumber()!=10L) {
            allProportion.setName("其他应用");
            allProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeData.getParseTotalCnt()).subtract(visualizationHomeDataTop.getParseTotalCnt()),visualizationHomeDataAll.getParseTotalCnt())));
        }else {
            allProportion.setName("其他应用");
            allProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeDataTop.getParseTotalCnt()),visualizationHomeDataAll.getParseTotalCnt())));
        }
        list.add(allProportion);
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("name", "项目");
        aliasMapResult.put("proportion", "占比");
        writer.setHeaderAlias(aliasMapResult);
        writer.setSheet("TopN应用比重图");
        writer.write(list, true);
    }

    private void extractedTypeProportion(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        VisualizationHomeData visualizationHomeDataTop = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeDataAll = new VisualizationHomeData();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            visualizationHomeDataTop = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO,"rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO,"rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType(),"true","false");
        }else {
            visualizationHomeDataTop = mapper.topNTypeProportion(visualizationHomeDataVO,"rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNTypeProportion(visualizationHomeDataVO,"rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType(),"true","false");
        }
        TopNProportion topNProportion = new TopNProportion();
        TopNProportion otherTopNProportion = new TopNProportion();
        TopNProportion allProportion = new TopNProportion();
        List<TopNProportion> list = new ArrayList<>();
        VisualizationHomeData visualizationHomeData = new VisualizationHomeData();
        topNProportion.setName("Top10分类");
        topNProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataTop.getParseTotalCnt(),visualizationHomeDataAll.getParseTotalCnt())));
        list.add(topNProportion);
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber()) && visualizationHomeDataVO.getRankNumber()!=10L) {
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
                visualizationHomeData = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO, "rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType(), "false", "false");
            }else {
                visualizationHomeData = mapper.topNTypeProportion(visualizationHomeDataVO, "rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType(), "false", "false");
            }
            otherTopNProportion.setName("其他TopN");
            otherTopNProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeData.getParseTotalCnt(),visualizationHomeDataAll.getParseTotalCnt())));
            list.add(otherTopNProportion);
        }
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber()) && visualizationHomeDataVO.getRankNumber()!=10L) {
            allProportion.setName("其他分类");
            allProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeData.getParseTotalCnt()).subtract(visualizationHomeDataTop.getParseTotalCnt()),visualizationHomeDataAll.getParseTotalCnt())));
        }else {
            allProportion.setName("其他分类");
            allProportion.setProportion(checkPram(ReportUtils.buildRatioBase(visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeDataTop.getParseTotalCnt()),visualizationHomeDataAll.getParseTotalCnt())));
        }
        list.add(allProportion);
        Map aliasMapResult = Maps.newLinkedHashMap();
        aliasMapResult.put("name", "项目");
        aliasMapResult.put("proportion", "占比");
        writer.setHeaderAlias(aliasMapResult);
        writer.setSheet("TopN分类比重图");
        writer.write(list, true);
    }

    private AnswerDistributionRateVO extracted(String name, BigInteger value) {
        AnswerDistributionRateVO rateTop = new AnswerDistributionRateVO();
        rateTop.setName(name);
        rateTop.setValue(value);
        return rateTop;
    }

    private void extractedIspProportion(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        List<VisualizationHomeTopTenParse> dataList = Lists.newArrayList();
        VisualizationHomeTopTenParse ispAll = new VisualizationHomeTopTenParse();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            dataList = mapper.answerFirstIspIsTodayProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType());
            ispAll = mapper.answerFirstIspIsTodayAll(visualizationHomeDataVO, "rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType());
        }else {
            dataList = mapper.answerFirstIspProportion(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType());
            ispAll = mapper.answerFirstIspAll(visualizationHomeDataVO, "rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType());
        }
        if (dataList != null && dataList.size() > 0) {
            List<IspProportionBO> list = new ArrayList<>();
            VisualizationHomeTopTenParse finalIspAll = ispAll;
            dataList.stream().forEach(data -> {
                IspProportionBO websiteExcelBO = BeanUtil.copyProperties(data, IspProportionBO.class);
                websiteExcelBO.setProportion(checkPram(ReportUtils.buildRatioBase(data.getParseTotalCnt(), finalIspAll.getParseTotalCnt())));
                list.add(websiteExcelBO);
            });
            Map aliasMapResult = Maps.newLinkedHashMap();
            aliasMapResult.put("answerFirstIsp", "运营商");
            aliasMapResult.put("parseTotalCnt", "解析次数");
            aliasMapResult.put("proportion", "占比");
            writer.setHeaderAlias(aliasMapResult);
            writer.setSheet("运营商资源分布图");
            writer.write(list, true);
        }
    }

    private void extractedTop10Domain(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        List<VisualizationHomeTopTenParse> dataList = Lists.newArrayList();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            dataList = mapper.top10DomainNameIsTodayParseCnt(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType());
        }else {
            dataList = mapper.top10DomainNameParseCnt(visualizationHomeDataVO,"rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType());
        }
        if (dataList != null && dataList.size() > 0) {
            List<TopNDomainExcelBO> list = new ArrayList<>();
            dataList.stream().forEach(data -> {
                TopNDomainExcelBO websiteExcelBO = BeanUtil.copyProperties(data, TopNDomainExcelBO.class);
                list.add(websiteExcelBO);
            });
            Map aliasMapResult = Maps.newLinkedHashMap();
            aliasMapResult.put("domainName", "域名");
            aliasMapResult.put("parseTotalCnt", "解析次数");
            aliasMapResult.put("rankNumber", "排行");
            writer.setHeaderAlias(aliasMapResult);
            writer.setSheet("Top10域名分布图");
            writer.write(list, true);
        }
    }



    private void extractedTop10Type(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        List<VisualizationHomeTopTenParse> dataList = Lists.newArrayList();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            dataList = mapper.top10TypeIsTodayParseCnt(visualizationHomeDataVO,"rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType());
        }else {
            dataList = mapper.top10TypeParseCnt(visualizationHomeDataVO,"rpt_resource_popular_domain_topn_type_" + visualizationHomeDataVO.getQueryType());
        }
        if (dataList != null && dataList.size() > 0) {
            List<TopNTypeExcelBO> list = new ArrayList<>();
            dataList.stream().forEach(data -> {
                TopNTypeExcelBO websiteExcelBO = BeanUtil.copyProperties(data, TopNTypeExcelBO.class);
                list.add(websiteExcelBO);
            });
            Map aliasMapResult = Maps.newLinkedHashMap();
            aliasMapResult.put("domainType", "分类名称");
            aliasMapResult.put("parseTotalCnt", "解析次数");
            aliasMapResult.put("rankNumber", "排行");
            writer.setHeaderAlias(aliasMapResult);
            writer.setSheet("Top10分类分布图");
            writer.write(list, true);
        }
    }

    private void extractedTop10Website(VisualizationHomeDataVO visualizationHomeDataVO, ExcelWriter writer) {
        List<VisualizationHomeTopTenParse> dataList = Lists.newArrayList();
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            dataList = mapper.top10WebsiteIsTodayParseCnt(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType());
        }else {
            dataList = mapper.top10WebsiteParseCnt(visualizationHomeDataVO,"rpt_resource_website_topn_detail_" + visualizationHomeDataVO.getQueryType());
        }
        if (dataList != null && dataList.size() > 0) {
            List<TopNWebsiteExcelBO> list = new ArrayList<>();
            dataList.stream().forEach(data -> {
                TopNWebsiteExcelBO websiteExcelBO = BeanUtil.copyProperties(data, TopNWebsiteExcelBO.class);
                list.add(websiteExcelBO);
            });
            Map aliasMapResult = Maps.newLinkedHashMap();
            aliasMapResult.put("websiteAppName", "应用名称");
            aliasMapResult.put("parseTotalCnt", "解析次数");
            aliasMapResult.put("rankNumber", "排行");
            writer.setHeaderAlias(aliasMapResult);
            writer.setSheet("Top10应用分布图");
            writer.write(list, true);
        }
    }

    public VisualizationHomeDataVO formatParseTime(VisualizationHomeDataVO visualizationHomeDataVO, String intervalType) {
        if (StringUtils.isEmpty(visualizationHomeDataVO.getEndTime()) || StringUtils.isEmpty(visualizationHomeDataVO.getStartTime())) {
            Map<String, String> timeParamMap = ReportUtils.buildTimeParam(intervalType);
            visualizationHomeDataVO.setStartTime(timeParamMap.get(ReportUtils.NOW_START));
            visualizationHomeDataVO.setEndTime(timeParamMap.get(ReportUtils.NOW_END));
        }
        return visualizationHomeDataVO;
    }
}
