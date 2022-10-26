package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.util.BusinessUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.vo.*;
import com.yamu.data.sample.service.resources.mapper.CdnServiceQualityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author dys
 * @Date 2022/07/26
 */
@Service
public class CdnServiceQualityService {

    @Autowired
    CdnServiceQualityMapper cdnServiceQualityMapper;

    public CdnServiceQualitySumData sumData(CdnServiceQualityParam cdnServiceQualityParam){
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        CdnServiceQualityData data = cdnServiceQualityMapper.getSumData(cdnServiceQualityParam,tableName);
        CdnServiceQualitySumData cdnServiceQualitySumData = BeanUtil.copyProperties(data, CdnServiceQualitySumData.class);
        cdnServiceQualitySumData.setNetInRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()),2));
        cdnServiceQualitySumData.setWithinRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(data.getWithinParseTotalCnt(), data.getParseTotalCnt()),2));
        cdnServiceQualitySumData.setUserCityInNetInRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(data.getCityAndNetInParseTotalCnt(), data.getParseTotalCnt()),2));
        cdnServiceQualitySumData.setUserWithinNetInRate(BusinessUtils.convertDoubleToPercent(
                ReportUtils.buildRatioBase(data.getWithAndNetInParseTotalCnt(), data.getParseTotalCnt()),2));
        return cdnServiceQualitySumData;
    }

    public List<CdnServiceQualityMapData> map(CdnServiceQualityParam cdnServiceQualityParam){
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        List<CdnServiceQualityMapData> list = new ArrayList<>();
        List<CdnServiceQualityData> dataList = new ArrayList<>();
        if(cdnServiceQualityParam.getAnswerFirstProvince() != null && !"".equals(cdnServiceQualityParam.getAnswerFirstProvince())){
            dataList = cdnServiceQualityMapper.getMapProvince(cdnServiceQualityParam,tableName);
        }else{
            dataList = cdnServiceQualityMapper.getMap(cdnServiceQualityParam,tableName);
        }
        for(CdnServiceQualityData data : dataList){
            CdnServiceQualityMapData cdnServiceQualityMapData = new CdnServiceQualityMapData();
            cdnServiceQualityMapData.setName(data.getName());
            cdnServiceQualityMapData.setParseTotalCnt(data.getParseTotalCnt());
            cdnServiceQualityMapData.setNodeCnt(data.getNodeCnt());
            cdnServiceQualityMapData.setSuccessRate(ReportUtils.buildRatioBase(data.getParseSuccessCnt(), data.getParseTotalCnt()));
            cdnServiceQualityMapData.setNetInRate(ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()));
            list.add(cdnServiceQualityMapData);
        }
        for(int i = 0 ;i < list.size();i++){
            long rankNumber = i + 1;
            list.get(i).setRankNumber(rankNumber);
        }
        return list;
    }

    public CdnServiceQualityCdnCover cdnCover(CdnServiceQualityParam cdnServiceQualityParam){
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        CdnServiceQualityCdnCoverList cdnCoverCnt = new CdnServiceQualityCdnCoverList();
        List<CdnServiceQualityCdnCoverList> list = new ArrayList<>();
        if(cdnServiceQualityParam.getAnswerFirstProvince() != null && !"".equals(cdnServiceQualityParam.getAnswerFirstProvince())){
            cdnCoverCnt = cdnServiceQualityMapper.getCdnCoverCntProvince(cdnServiceQualityParam,tableName);
            list = cdnServiceQualityMapper.getCdnServiceQualityCdnCoverListProvince(cdnServiceQualityParam,tableName);
        }else{
            cdnCoverCnt = cdnServiceQualityMapper.getCdnCoverCnt(cdnServiceQualityParam,tableName);
            list = cdnServiceQualityMapper.getCdnServiceQualityCdnCoverList(cdnServiceQualityParam,tableName);
        }
        CdnServiceQualityCdnCover data = new CdnServiceQualityCdnCover();
        data.setCdnCoverCnt(cdnCoverCnt.getParseTotalCnt());
        data.setData(list);
        return data;
    }

    public ResponseResultsTrend responseResultsTrend(CdnServiceQualityParam cdnServiceQualityParam){
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        ResponseResultsTrend data = cdnServiceQualityMapper.getResponseResultsTrend(cdnServiceQualityParam,tableName);
        return data;
    }

    public List<RateTrend> rateTrend(CdnServiceQualityParam cdnServiceQualityParam) throws ParseException {
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        List<CdnServiceQualityData> dataList = cdnServiceQualityMapper.getRateTrend(cdnServiceQualityParam,tableName);
        List<RateTrend> list = new ArrayList<>();
        for (CdnServiceQualityData data : dataList) {
            RateTrend rateTrend = new RateTrend();
            rateTrend.setParseTime(BusinessUtils.formatTime(data.getParseTime(),cdnServiceQualityParam.getQueryType()));
            rateTrend.setNetInRate(BusinessUtils.convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getNetInParseTotalCnt(), data.getParseTotalCnt()),2));
            rateTrend.setWithInRate(BusinessUtils.convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getWithinParseTotalCnt(), data.getParseTotalCnt()),2));
            rateTrend.setCityInRate(BusinessUtils.convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getCityInParseTotalCnt(), data.getParseTotalCnt()),2));
            list.add(rateTrend);
        }
        return list;
    }

    public List<ResourceCdnServerDispatchTrend> dispatchTrend(CdnServiceQualityParam cdnServiceQualityParam) throws ParseException {
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        List<ResourceCdnServerDispatchTrend> list = cdnServiceQualityMapper.getDispatchTrend(cdnServiceQualityParam,tableName);
        for (ResourceCdnServerDispatchTrend data : list) {
            data.setParseTime(BusinessUtils.formatTime(data.getParseTime(),cdnServiceQualityParam.getQueryType()));
        }
        return list;
    }

    public List<String> topNCdn(CdnServiceQualityParam cdnServiceQualityParam){
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        if(cdnServiceQualityParam.getRankNumber() == null ){
            cdnServiceQualityParam.setRankNumber(20L);
        }else if(cdnServiceQualityParam.getRankNumber() != null && cdnServiceQualityParam.getRankNumber() > 20){
            cdnServiceQualityParam.setRankNumber(20L);
        }
        List<String> list = cdnServiceQualityMapper.getTopNCdn(cdnServiceQualityParam,tableName);
        return list;
    }

    public void download(CdnServiceQualityParam cdnServiceQualityParam, HttpServletResponse response) throws IOException, ParseException{
        checkFindTrendListParam(cdnServiceQualityParam);
        String tableName = getTableName(cdnServiceQualityParam.getQueryType());
        String fileName = "CDN厂商服务质量报表" + StrUtil.UNDERLINE + DateUtils.formatDataToString(new Date(),"yyyyMMddHHmm") + ".xls";
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.merge(8, "导出时间段   开始时间:"+cdnServiceQualityParam.getStartTime().substring(0,cdnServiceQualityParam.getStartTime().length()-3)
                +",结束时间:"+cdnServiceQualityParam.getEndTime().substring(0,cdnServiceQualityParam.getEndTime().length()-3));
        writer.setHeaderAlias(getListHeaderAlias(cdnServiceQualityParam.getProvince()));
        writer.renameSheet("CDN厂商服务质量报表");
        writer.setOnlyAlias(true);
        if(cdnServiceQualityParam.getProvince().equals("黑龙江省")){
            List<CdnServiceQualityDownLoadHlj> hljList = getHljData(cdnServiceQualityParam,tableName);
            writer.write(hljList, true);
        }
        ServletOutputStream out=response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    private Map<String, String> getListHeaderAlias(String province) {
        Map aliasMapResult = Maps.newLinkedHashMap();
        if(province.equals("黑龙江省")){
            aliasMapResult.put("parseTime", "时间");
            aliasMapResult.put("business", "厂商名称");
            aliasMapResult.put("parseTotalCnt", "解析次数");
            aliasMapResult.put("rate", "占比");
            aliasMapResult.put("netInParseTotalCnt", "网内次数");
            aliasMapResult.put("netInCityCnt", "覆盖网内城市数量");
            aliasMapResult.put("netOutCityCnt", "覆盖网外城市数量");
            aliasMapResult.put("withinCityCnt", "覆盖本省城市数量");
            aliasMapResult.put("withOutCityCnt", "覆盖外省城市数量");
            aliasMapResult.put("hebData", "哈尔滨");
            aliasMapResult.put("qqheData", "齐齐哈尔");
            aliasMapResult.put("jxData", "鸡西");
            aliasMapResult.put("hgData", "鹤岗");
            aliasMapResult.put("sysData", "双鸭山");
            aliasMapResult.put("dqData", "大庆");
            aliasMapResult.put("ycData", "伊春");
            aliasMapResult.put("jmsData", "佳木斯");
            aliasMapResult.put("qthData", "七台河");
            aliasMapResult.put("mdjData", "牡丹江");
            aliasMapResult.put("hhData", "黑河");
            aliasMapResult.put("shData", "绥化");
            aliasMapResult.put("dxalData", "大兴安岭");
            aliasMapResult.put("jlData", "吉林");
            aliasMapResult.put("lnData", "辽宁");
        }
        return aliasMapResult;
    }

    private String getTableName(String queryType){
        return "rpt_resource_cdn_business_service_quality_" + queryType;
    }

    private void checkFindTrendListParam(CdnServiceQualityParam cdnServiceQualityParam){
        String businessLike = cdnServiceQualityParam.getBusinessLike();
        cdnServiceQualityParam.setBusinessLike(ReportUtils.escapeChar(businessLike));
    }

    private List<CdnServiceQualityDownLoadHlj> getHljData(CdnServiceQualityParam cdnServiceQualityParam,String tableName){
        List<String> nearProvinceName = new ArrayList<>();
        nearProvinceName.add("吉林省");
        nearProvinceName.add("辽宁省");
        List<CdnServiceQualityDownLoadData> list = cdnServiceQualityMapper.getDownloadData(cdnServiceQualityParam,tableName);
        List<CdnServiceQualityDownLoadData> cityList = cdnServiceQualityMapper.getDownloadDataCity(cdnServiceQualityParam,tableName);
        List<CdnServiceQualityDownLoadData> nearProvinceList = cdnServiceQualityMapper.getDownloadDataNearProvince(cdnServiceQualityParam,tableName,nearProvinceName);
        List<CdnServiceQualityDownLoadHlj> result = new ArrayList<>();
        for(CdnServiceQualityDownLoadData data : list){
            CdnServiceQualityDownLoadHlj hljData = new CdnServiceQualityDownLoadHlj();
            hljData.setParseTime(cdnServiceQualityParam.getStartTime() + "~" + cdnServiceQualityParam.getEndTime());
            hljData.setBusiness(data.getBusiness());
            hljData.setParseTotalCnt(data.getParseTotalCnt());
            hljData.setRate(BusinessUtils.convertDoubleToPercent(
                    ReportUtils.buildRatioBase(data.getParseTotalCnt(), data.getSumCnt()),2));
            hljData.setNetInParseTotalCnt(data.getNetInParseTotalCnt());
            hljData.setNetInCityCnt(data.getNetInCityCnt());
            hljData.setNetOutCityCnt(data.getNetOutCityCnt());
            hljData.setWithinCityCnt(data.getWithinCityCnt());
            hljData.setWithOutCityCnt(data.getWithOutCityCnt());
            for(CdnServiceQualityDownLoadData cityData : cityList){
                if(data.getBusiness().equals(cityData.getBusiness())){
                    if(cityData.getUserCity().equals("哈尔滨市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setHebData(cityStr);
                    }else if(cityData.getUserCity().equals("齐齐哈尔市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setQqheData(cityStr);
                    }else if(cityData.getUserCity().equals("鸡西市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setJxData(cityStr);
                    }else if(cityData.getUserCity().equals("鹤岗市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setHgData(cityStr);
                    }else if(cityData.getUserCity().equals("双鸭山市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setSysData(cityStr);
                    }else if(cityData.getUserCity().equals("大庆市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setDqData(cityStr);
                    }else if(cityData.getUserCity().equals("伊春市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setYcData(cityStr);
                    }else if(cityData.getUserCity().equals("佳木斯市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setJmsData(cityStr);
                    }else if(cityData.getUserCity().equals("七台河市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setQthData(cityStr);
                    }else if(cityData.getUserCity().equals("牡丹江市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setMdjData(cityStr);
                    }else if(cityData.getUserCity().equals("黑河市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setHhData(cityStr);
                    }else if(cityData.getUserCity().equals("绥化市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setShData(cityStr);
                    }else if(cityData.getUserCity().equals("大兴安岭市")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(cityData.getAnswerFirstCnt(), cityData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + cityData.getUserCnt() + ",本地解析量:" + cityData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setDxalData(cityStr);
                    }
                }
            }
            for(CdnServiceQualityDownLoadData nearProvinceData : nearProvinceList){
                if(data.getBusiness().equals(nearProvinceData.getBusiness())){
                    if(nearProvinceData.getUserProvince().equals("吉林省")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(nearProvinceData.getAnswerFirstCnt(), nearProvinceData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + nearProvinceData.getUserCnt() + ",本地解析量:" + nearProvinceData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setJlData(cityStr);
                    }else if(nearProvinceData.getUserProvince().equals("辽宁省")){
                        String rate = BusinessUtils.convertDoubleToPercent(
                                ReportUtils.buildRatioBase(nearProvinceData.getAnswerFirstCnt(), nearProvinceData.getUserCnt()),2);
                        String cityStr = "用户解析量:" + nearProvinceData.getUserCnt() + ",本地解析量:" + nearProvinceData.getAnswerFirstCnt() + ",占比:" + rate;
                        hljData.setLnData(cityStr);
                    }
                }
            }
            result.add(hljData);
        }
        return result;
    }
}
