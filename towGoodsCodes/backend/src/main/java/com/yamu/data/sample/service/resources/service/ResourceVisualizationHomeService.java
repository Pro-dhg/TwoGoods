package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yamu.data.sample.service.common.entity.ConstantEntity;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.entity.bo.AnswerDistributionBO;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeKeyBusinessBO;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeMapBO;
import com.yamu.data.sample.service.resources.entity.bo.VisualizationHomeTopnBO;
import com.yamu.data.sample.service.resources.entity.po.*;
import com.yamu.data.sample.service.resources.entity.vo.*;
import com.yamu.data.sample.service.resources.mapper.AnswerDistributionMapper;
import com.yamu.data.sample.service.resources.mapper.VisualizationHomeMapper;
import com.yamu.data.sample.service.resources.thread.*;
import com.yamu.data.sample.service.resources.visualizationHomeMap.VisualizationHomeRamMap;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @Author dys
 * @Date 2022/1/6
 */
@Service
public class ResourceVisualizationHomeService extends BaseService{

    @Autowired
    private VisualizationHomeMapper mapper;

    @Autowired
    private AnswerDistributionMapper answerDistributionMapper;

    @Autowired
    private VisualizationHomeRamMap visualizationHomeRamMap;

    @Autowired
    @Qualifier("taskExecutor")
    private Executor taskExecutor;

    @Value("${config.execution_date_offset}")
    public Integer executionDateOffset;

    // y轴
    public static String YAXIS = "yAxis";
    // 系数
    public static String SERIES = "series";
    //报表title
    public static String TITLE = "title";

    private static List<String> provinceNode = Arrays.asList("新疆维吾尔自治区", "青海省", "湖北省", "香港特别行政区", "山西省", "云南省", "河北省", "广西壮族自治区",
            "海南省", "上海市", "辽宁省", "澳门特别行政区", "福建省", "陕西省", "四川省", "贵州省", "广东省", "北京市", "江苏省", "黑龙江省", "天津市", "重庆市", "山东省",
            "内蒙古自治区", "宁夏回族自治区", "浙江省", "台湾省", "西藏自治区", "吉林省", "安徽省", "江西省", "甘肃省", "河南省", "湖南省");

    private final static Map codeName = new HashMap(){{
        put("wlan_cnt","固网今日解析量");
        put("mobile_cnt","手机今日解析量");
        put("total_cnt","今日解析量");
    }};

    public List<VisualizationHomeKeyBusinessBO> keyBusiness(VisualizationHomeDataVO visualizationHomeDataVO) {
        List<VisualizationHomeKeyBusinessBO> list = new ArrayList<>();
        VisualizationHomeDataVO yoyVisualizationHomeDataVO = BeanUtil.copyProperties(visualizationHomeDataVO, VisualizationHomeDataVO.class);
        VisualizationHomeDataVO momVisualizationHomeDataVO = BeanUtil.copyProperties(visualizationHomeDataVO, VisualizationHomeDataVO.class);
        String queryTable = "";
        if(visualizationHomeDataVO.getUserType() != null && "手机".equals(visualizationHomeDataVO.getUserType())){
            queryTable = "rpt_resource_visualized_mobile_yoy_" + visualizationHomeDataVO.getQueryType();
        }else if(visualizationHomeDataVO.getUserType() != null && "固网".equals(visualizationHomeDataVO.getUserType())){
            queryTable = "rpt_resource_visualized_wlan_yoy_" + visualizationHomeDataVO.getQueryType();
        }else{
            queryTable = "rpt_resource_visualized_yoy_" + visualizationHomeDataVO.getQueryType();
        }
        String topNQueryTable = "rpt_resource_domain_topn_detail_" + visualizationHomeDataVO.getQueryType();
        String recursionQueryTable = "rpt_basic_recursion_parse_trend_" + visualizationHomeDataVO.getQueryType();
        Map<String, String> yoyTimeMap = ReportUtils.buildYOYTimeParamByStartTimeAndEndTime(visualizationHomeDataVO.getStartTime(), visualizationHomeDataVO.getEndTime());
        Map<String, String> momTimeMap = ReportUtils.buildMOMTimeParamByStartTimeAndEndTime(visualizationHomeDataVO.getStartTime(), visualizationHomeDataVO.getEndTime());
        Map<String,VisualizationHomeData> dataMap = new HashMap<>();
        CountDownLatch latch;
        if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
            if(visualizationHomeDataVO.getUserType() != null && !"".equals(visualizationHomeDataVO.getUserType())){
                latch = new CountDownLatch(5);
            }else{
                latch = new CountDownLatch(8);
            }
        }else{
            if(visualizationHomeDataVO.getUserType() != null && !"".equals(visualizationHomeDataVO.getUserType())){
                latch = new CountDownLatch(3);
            }else{
                latch = new CountDownLatch(6);
            }
        }
        NowDataThread recursionDataThread = new NowDataThread(visualizationHomeDataVO,latch,mapper,recursionQueryTable,"recursionData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread nowDataThread = new NowDataThread(visualizationHomeDataVO,latch,mapper,queryTable,"nowData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread mobileNowDataThread = new NowDataThread(visualizationHomeDataVO,latch,mapper,queryTable,"mobileData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread wlanNowDataThread = new NowDataThread(visualizationHomeDataVO,latch,mapper,queryTable,"wlanData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread topNDataThread = new NowDataThread(visualizationHomeDataVO,latch,mapper,topNQueryTable,"topNData",visualizationHomeDataVO.getIsToday(),false,"");
        if(visualizationHomeDataVO.getUserType() != null && "手机".equals(visualizationHomeDataVO.getUserType())){
            taskExecutor.execute(mobileNowDataThread);
        }else if(visualizationHomeDataVO.getUserType() != null && "固网".equals(visualizationHomeDataVO.getUserType())){
            taskExecutor.execute(wlanNowDataThread);
        }else{
            taskExecutor.execute(recursionDataThread);
            taskExecutor.execute(nowDataThread);
        }
        if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
            taskExecutor.execute(topNDataThread);
        }
        //环比
        yoyVisualizationHomeDataVO.setStartTime(yoyTimeMap.get(ReportUtils.EASIER_START));
        yoyVisualizationHomeDataVO.setEndTime(yoyTimeMap.get(ReportUtils.EASIER_END));
        NowDataThread yoyMobileNowDataThread = new NowDataThread(yoyVisualizationHomeDataVO,latch,mapper,queryTable,"mobileData",visualizationHomeDataVO.getIsToday(),true,visualizationHomeDataVO.getEndTime());
        NowDataThread yoyWlanNowDataThread = new NowDataThread(yoyVisualizationHomeDataVO,latch,mapper,queryTable,"wlanData",visualizationHomeDataVO.getIsToday(),true,visualizationHomeDataVO.getEndTime());
        NowDataThread yoyRecursionDataThread = new NowDataThread(yoyVisualizationHomeDataVO,latch,mapper,recursionQueryTable,"recursionData",visualizationHomeDataVO.getIsToday(),true,visualizationHomeDataVO.getEndTime());
        NowDataThread yoyDataThread = new NowDataThread(yoyVisualizationHomeDataVO, latch,mapper,queryTable,"yoyData",visualizationHomeDataVO.getIsToday(),true,visualizationHomeDataVO.getEndTime());
        NowDataThread yoyTopNDataThread = new NowDataThread(yoyVisualizationHomeDataVO,latch,mapper,topNQueryTable,"topNData",visualizationHomeDataVO.getIsToday(),true,visualizationHomeDataVO.getEndTime());
        if(visualizationHomeDataVO.getUserType() != null && "手机".equals(visualizationHomeDataVO.getUserType())){
            taskExecutor.execute(yoyMobileNowDataThread);
        }else if(visualizationHomeDataVO.getUserType() != null && "固网".equals(visualizationHomeDataVO.getUserType())){
            taskExecutor.execute(yoyWlanNowDataThread);
        }else{
            taskExecutor.execute(yoyRecursionDataThread);
            taskExecutor.execute(yoyDataThread);
        }
        if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
            taskExecutor.execute(yoyTopNDataThread);
        }
        //同比
        momVisualizationHomeDataVO.setStartTime(momTimeMap.get(ReportUtils.EASIER_START));
        momVisualizationHomeDataVO.setEndTime(momTimeMap.get(ReportUtils.EASIER_END));
        NowDataThread momMobileNowDataThread = new NowDataThread(momVisualizationHomeDataVO,latch,mapper,queryTable,"mobileData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread momWlanNowDataThread = new NowDataThread(momVisualizationHomeDataVO,latch,mapper,queryTable,"wlanData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread momRecursionDataThread = new NowDataThread(momVisualizationHomeDataVO,latch,mapper,recursionQueryTable,"recursionData",visualizationHomeDataVO.getIsToday(),false,"");
        NowDataThread momDataThread = new NowDataThread(momVisualizationHomeDataVO, latch,mapper,queryTable,"momData",visualizationHomeDataVO.getIsToday(),false,"");
        if(visualizationHomeDataVO.getUserType() != null && "手机".equals(visualizationHomeDataVO.getUserType())){
            taskExecutor.execute(momMobileNowDataThread);
        }else if(visualizationHomeDataVO.getUserType() != null && "固网".equals(visualizationHomeDataVO.getUserType())){
            taskExecutor.execute(momWlanNowDataThread);
        }else{
            taskExecutor.execute(momRecursionDataThread);
            taskExecutor.execute(momDataThread);
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(visualizationHomeDataVO.getUserType() != null && "手机".equals(visualizationHomeDataVO.getUserType())){
            dataMap.put("nowData",mobileNowDataThread.getData());
            dataMap.put("yoyData",yoyMobileNowDataThread.getData());
            dataMap.put("momData",momMobileNowDataThread.getData());
        }else if(visualizationHomeDataVO.getUserType() != null && "固网".equals(visualizationHomeDataVO.getUserType())){
            dataMap.put("nowData",wlanNowDataThread.getData());
            dataMap.put("yoyData",yoyWlanNowDataThread.getData());
            dataMap.put("momData",momWlanNowDataThread.getData());
        }else{
            dataMap.put("nowData",nowDataThread.getData());
            dataMap.put("yoyData",yoyDataThread.getData());
            dataMap.put("momData",momDataThread.getData());
            dataMap.put("recursionData",recursionDataThread.getData());
            dataMap.put("yoyRecursionData",yoyRecursionDataThread.getData());
            dataMap.put("momRecursionData",momRecursionDataThread.getData());
        }
        if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
            dataMap.put("topNData",topNDataThread.getData());
            dataMap.put("yoyTopNData",yoyTopNDataThread.getData());
        }
        list = getKeyBusinessResult(dataMap,visualizationHomeDataVO.getRankNumber(),visualizationHomeDataVO.getUserType());
        if(!visualizationHomeDataVO.getIsToday()){
            if(list.get(0).getData().compareTo(BigInteger.ZERO) == 0){
                list = getKeyBusinessLastTime(visualizationHomeDataVO,queryTable);
            }else{
                Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
                map.remove("keyBusinessDataList");
            }
        }
        Map<String, List> cacheMap = visualizationHomeRamMap.getAllRegionMap();
        if(visualizationHomeDataVO.getRankNumber() != null && !"".equals(visualizationHomeDataVO.getRankNumber())){
            cacheMap.put("runkNumber",new ArrayList(Collections.singleton(visualizationHomeDataVO.getRankNumber())));
        }else{
            cacheMap.remove("runkNumber");
        }
        if(visualizationHomeDataVO.getUserType() != null && !"".equals(visualizationHomeDataVO.getUserType())){
            cacheMap.put("userType",new ArrayList(Collections.singleton(visualizationHomeDataVO.getUserType())));
        }else{
            cacheMap.remove("userType");
        }
        return list;
    }

    private List<VisualizationHomeKeyBusinessBO> getKeyBusinessLastTime(VisualizationHomeDataVO visualizationHomeDataVO,String queryTable){
        List<VisualizationHomeKeyBusinessBO> dataList = new ArrayList<>();
        Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
        dataList = map.get("keyBusinessDataList");
        if(dataList == null){
            VisualizationHomeDataVO maxTimeData = mapper.getMaxTime(queryTable+"_real");
            if(maxTimeData == null){
                dataList = new ArrayList<>();
                visualizationHomeRamMap.initRegion("keyBusinessDataList",dataList);
                return dataList;
            }
            String startTime = getStartTime(visualizationHomeDataVO.getStartTime(),visualizationHomeDataVO.getEndTime(),maxTimeData.getEndTime());
            visualizationHomeDataVO.setStartTime(startTime);
            visualizationHomeDataVO.setEndTime(maxTimeData.getEndTime());
            dataList = keyBusiness(visualizationHomeDataVO);
            visualizationHomeRamMap.initRegion("keyBusinessDataList",dataList);
        }
        return dataList;
    }


    private List<VisualizationHomeKeyBusinessBO> getKeyBusinessResult(Map<String,VisualizationHomeData> dataMap,Long rankNumber,String userType){
        List<VisualizationHomeKeyBusinessBO> list = new ArrayList<>();
        dataMap.get("nowData").buildRate();
        dataMap.get("yoyData").buildRate();
        dataMap.get("momData").buildRate();
        if(userType == null || "".equals(userType)){
            dataMap.get("recursionData").buildRecursionSuccessRate();
            dataMap.get("yoyRecursionData").buildRecursionSuccessRate();
            dataMap.get("momRecursionData").buildRecursionSuccessRate();
        }
        VisualizationHomeKeyBusinessBO visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
        visualizationHomeKeyBusinessBO.setName("解析量");
        visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getParseTotalCnt());
        visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
        visualizationHomeKeyBusinessBO.setMomRate(ReportUtils.buildRatioGT(dataMap.get("momData").getParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
        list.add(visualizationHomeKeyBusinessBO);
        if(rankNumber != null && !"".equals(rankNumber)){
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("Top"+rankNumber+"域名解析量");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("topNData").getParseTotalCnt());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyTopNData").getParseTotalCnt(), dataMap.get("topNData").getParseTotalCnt()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("topNData").getParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
        }else{
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("TopN域名解析量");
            visualizationHomeKeyBusinessBO.setData(BigInteger.ZERO);
            visualizationHomeKeyBusinessBO.setYoyRate(Double.valueOf("0"));
            visualizationHomeKeyBusinessBO.setPopRate(Double.valueOf("0"));
        }
        list.add(visualizationHomeKeyBusinessBO);
        visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
        visualizationHomeKeyBusinessBO.setName("IPv4解析次数");
        visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getARecordParseTotalCnt());
        visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getARecordParseTotalCnt(), dataMap.get("nowData").getARecordParseTotalCnt()));
        visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getARecordParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
        list.add(visualizationHomeKeyBusinessBO);
        visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
        visualizationHomeKeyBusinessBO.setName("IPv6解析次数");
        visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getAaaaRecordParseTotalCnt());
        visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getAaaaRecordParseTotalCnt(), dataMap.get("nowData").getAaaaRecordParseTotalCnt()));
        visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getAaaaRecordParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
        list.add(visualizationHomeKeyBusinessBO);
        visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
        visualizationHomeKeyBusinessBO.setName("缓存成功率");
        visualizationHomeKeyBusinessBO.setRate(dataMap.get("nowData").getCacheSuccessRate());
        visualizationHomeKeyBusinessBO.setYoyRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("yoyData").getCacheSuccessRate()),
                BigDecimal.valueOf(dataMap.get("nowData").getCacheSuccessRate())));
        visualizationHomeKeyBusinessBO.setMomRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("momData").getCacheSuccessRate()),
                BigDecimal.valueOf(dataMap.get("nowData").getCacheSuccessRate())));
        list.add(visualizationHomeKeyBusinessBO);
        if(userType != null && "手机".equals(userType)){
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("5G");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getParseTotalCnt5g());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getParseTotalCnt5g(), dataMap.get("nowData").getParseTotalCnt5g()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getParseTotalCnt5g(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("4G/5G");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getParseTotalCnt45g());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getParseTotalCnt45g(), dataMap.get("nowData").getParseTotalCnt45g()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getParseTotalCnt45g(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("2/3/4G");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getParseTotalCnt234g());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getParseTotalCnt234g(), dataMap.get("nowData").getParseTotalCnt234g()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getParseTotalCnt234g(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
        }else if(userType != null && "固网".equals(userType)){
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("家客");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getJiakeParseTotalCnt());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getJiakeParseTotalCnt(), dataMap.get("nowData").getJiakeParseTotalCnt()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getJiakeParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("集客");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getJikeParseTotalCnt());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getJikeParseTotalCnt(), dataMap.get("nowData").getJikeParseTotalCnt()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getJikeParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("WLAN");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getWlanParseTotalCnt());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getWlanParseTotalCnt(), dataMap.get("nowData").getWlanParseTotalCnt()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getWlanParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
        }else{
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("递归成功率");
            visualizationHomeKeyBusinessBO.setRate(dataMap.get("recursionData").getRecursionSuccessRate());
            visualizationHomeKeyBusinessBO.setYoyRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("yoyRecursionData").getRecursionSuccessRate()),
                    BigDecimal.valueOf(dataMap.get("recursionData").getRecursionSuccessRate())));
            visualizationHomeKeyBusinessBO.setMomRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("momRecursionData").getRecursionSuccessRate()),
                    BigDecimal.valueOf(dataMap.get("recursionData").getRecursionSuccessRate())));
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("固网解析量");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getWlanParseTotalCnt());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getWlanParseTotalCnt(), dataMap.get("nowData").getWlanParseTotalCnt()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getWlanParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("手机解析量");
            visualizationHomeKeyBusinessBO.setData(dataMap.get("nowData").getMobileParseTotalCnt());
            visualizationHomeKeyBusinessBO.setYoyRate(ReportUtils.buildRatioGT(dataMap.get("yoyData").getMobileParseTotalCnt(), dataMap.get("nowData").getMobileParseTotalCnt()));
            visualizationHomeKeyBusinessBO.setPopRate(ReportUtils.buildRatioBase(dataMap.get("nowData").getMobileParseTotalCnt(), dataMap.get("nowData").getParseTotalCnt()));
            list.add(visualizationHomeKeyBusinessBO);
        }
        visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
        visualizationHomeKeyBusinessBO.setName("本网率");
        visualizationHomeKeyBusinessBO.setRate(dataMap.get("nowData").getNetInRate());
        visualizationHomeKeyBusinessBO.setYoyRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("yoyData").getNetInRate()),
                BigDecimal.valueOf(dataMap.get("nowData").getNetInRate())));
        visualizationHomeKeyBusinessBO.setMomRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("momData").getNetInRate()),
                BigDecimal.valueOf(dataMap.get("nowData").getNetInRate())));
        list.add(visualizationHomeKeyBusinessBO);
        visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
        visualizationHomeKeyBusinessBO.setName("本省率");
        visualizationHomeKeyBusinessBO.setRate(dataMap.get("nowData").getParseInRate());
        visualizationHomeKeyBusinessBO.setYoyRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("yoyData").getParseInRate()),
                BigDecimal.valueOf(dataMap.get("nowData").getParseInRate())));
        visualizationHomeKeyBusinessBO.setMomRate(buildRatioGT(BigDecimal.valueOf(dataMap.get("momData").getParseInRate()),
                BigDecimal.valueOf(dataMap.get("nowData").getParseInRate())));
        list.add(visualizationHomeKeyBusinessBO);
        return list;
    }

    public List<VisualizationHomeMapBO> getMap(VisualizationHomeDataVO visualizationHomeDataVO) {
        AnswerDistributionBO answerDistributionBO = new AnswerDistributionBO();
        List<AnswerDistribution> distributionList = new ArrayList<>();
        if(visualizationHomeDataVO.getIsToday()){
            answerDistributionBO.setStartTime(visualizationHomeDataVO.getStartTime());
            answerDistributionBO.setEndTime(visualizationHomeDataVO.getEndTime());
            answerDistributionBO.setUserType(visualizationHomeDataVO.getUserType());
            distributionList = answerDistributionMapper.findGroupByProvinceToday(answerDistributionBO);
        }else{
            answerDistributionBO.setStartTime(visualizationHomeDataVO.getStartTime());
            answerDistributionBO.setEndTime(visualizationHomeDataVO.getEndTime());
            answerDistributionBO.setQueryType(visualizationHomeDataVO.getQueryType());
            answerDistributionBO.setUserType(visualizationHomeDataVO.getUserType());
            distributionList = answerDistributionMapper.findGroupByProvince(answerDistributionBO);
            if(distributionList == null || distributionList.size() == 0){
                distributionList = getDistributionListLastTime(visualizationHomeDataVO);
            }else{
                Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
                map.remove("mapDataList");
            }
        }
        List<VisualizationHomeMapBO> mapDataList = statisticsProvinceDomainParseCount(distributionList, true);
        return mapDataList;
    }

    private List<AnswerDistribution> getDistributionListLastTime(VisualizationHomeDataVO visualizationHomeDataVO){
        List<AnswerDistribution> distributionList = new ArrayList<>();
        Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
        distributionList = map.get("mapDataList");
        if(distributionList == null){
            VisualizationHomeDataVO maxTimeData = mapper.getMaxTime("rpt_resource_answer_distribution_"+visualizationHomeDataVO.getQueryType()+"_real");
            if(maxTimeData == null){
                distributionList = new ArrayList<>();
                visualizationHomeRamMap.initRegion("mapDataList",distributionList);
                return distributionList;
            }
            String startTime = getStartTime(visualizationHomeDataVO.getStartTime(),visualizationHomeDataVO.getEndTime(),maxTimeData.getEndTime());
            visualizationHomeDataVO.setStartTime(startTime);
            visualizationHomeDataVO.setEndTime(maxTimeData.getEndTime());
            AnswerDistributionBO answerDistributionBO = new AnswerDistributionBO();
            answerDistributionBO.setStartTime(visualizationHomeDataVO.getStartTime());
            answerDistributionBO.setEndTime(visualizationHomeDataVO.getEndTime());
            answerDistributionBO.setQueryType(visualizationHomeDataVO.getQueryType());
            answerDistributionBO.setUserType(visualizationHomeDataVO.getUserType());
            distributionList = answerDistributionMapper.findGroupByProvince(answerDistributionBO);
            visualizationHomeRamMap.initRegion("mapDataList",distributionList);
        }
        return distributionList;
    }

    public VisualizationHomeTopnBO topNTrend(VisualizationHomeDataVO visualizationHomeDataVO){
        if(visualizationHomeDataVO.getIsToday()){
            visualizationHomeDataVO.setQueryType("1h");
        }
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        String queryTable = "";
        if(visualizationHomeDataVO.getUserType() != null && "手机".equals(visualizationHomeDataVO.getUserType())){
            queryTable ="rpt_resource_visualized_mobile_yoy_" + visualizationHomeDataVO.getQueryType();
        }else if(visualizationHomeDataVO.getUserType() != null && "固网".equals(visualizationHomeDataVO.getUserType())){
            queryTable ="rpt_resource_visualized_wlan_yoy_" + visualizationHomeDataVO.getQueryType();
        }else{
            queryTable ="rpt_resource_visualized_yoy_" + visualizationHomeDataVO.getQueryType();
        }
        List<VisualizationHomeTopNTrendVO> dataList = mapper.topNTrend(visualizationHomeDataVO,queryTable);
        if(dataList == null || dataList.size() == 0){
            VisualizationHomeDataVO topNTrendLastTimeVO = BeanUtil.copyProperties(visualizationHomeDataVO, VisualizationHomeDataVO.class);
            dataList = getTopNTrendLastTime(topNTrendLastTimeVO,queryTable);
        }else{
            Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
            map.remove("topNTrendDataList");
        }
        for (VisualizationHomeTopNTrendVO visualizationHomeTopNTrendVO : dataList) {
            visualizationHomeTopNTrendVO.setTime(interceptTimeStr(visualizationHomeTopNTrendVO.getParseTime(),visualizationHomeDataVO.getQueryType()));
        }
        List<ResourceWebsiteUserSource> userList = findUserSource(visualizationHomeDataVO);
        VisualizationHomeTopnBO visualizationHomeTopnBO = new VisualizationHomeTopnBO();
        visualizationHomeTopnBO.setTopNTrendList(dataList);
        visualizationHomeTopnBO.setUserList(userList);
        return visualizationHomeTopnBO;
    }

    private List<VisualizationHomeTopNTrendVO> getTopNTrendLastTime(VisualizationHomeDataVO visualizationHomeDataVO,String queryTable){
        List<VisualizationHomeTopNTrendVO> dataList = new ArrayList<>();
        Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
        dataList = map.get("topNTrendDataList");
        if(dataList == null){
            VisualizationHomeDataVO maxTimeData = mapper.getMaxTime(queryTable+"_real");
            if(maxTimeData == null){
                dataList = new ArrayList<>();
                visualizationHomeRamMap.initRegion("topNTrendDataList",dataList);
                return dataList;
            }
            String startTime = getStartTime(visualizationHomeDataVO.getStartTime(),visualizationHomeDataVO.getEndTime(),maxTimeData.getEndTime());
            visualizationHomeDataVO.setStartTime(startTime);
            visualizationHomeDataVO.setEndTime(maxTimeData.getEndTime());
            dataList = mapper.topNTrend(visualizationHomeDataVO,queryTable);
            visualizationHomeRamMap.initRegion("topNTrendDataList",dataList);
        }
        return dataList;
    }

    public List<VisualizationHomeKeyBusinessBO> todayParseTotal(VisualizationHomeDataVO visualizationHomeDataVO) {
        List<VisualizationHomeKeyBusinessBO> list = new ArrayList<>();
        VisualizationHomeDataVO visualizationHomeDataVOToday = new VisualizationHomeDataVO();
        String startTime = DateUtils.formatDataToString(new Date(),DateUtils.DEFAULT_DAY_FMT) + " 00:00:00";
        String endTime = DateUtils.getLaterTimeByMin(new Date(),executionDateOffset,"yyyy-MM-dd HH:mm") + ":00";
        visualizationHomeDataVOToday.setStartTime(startTime);
        visualizationHomeDataVOToday.setEndTime(endTime);
        if(visualizationHomeDataVO.getUserType() != null){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            String parseTime = DateUtils.formatDataToString(calendar.getTime(), DateUtils.DEFAULT_DAY_FMT);
            ResourceVisualizedParseYes yesterdayData = mapper.yesterdayParseTotal(parseTime);
            VisualizationHomeKeyBusinessBO yesVisualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            String typeCode = "";
            if("手机".equals(visualizationHomeDataVO.getUserType())){
                typeCode = "mobile_cnt";
                yesVisualizationHomeKeyBusinessBO.setName("手机昨日解析量");
                if(yesterdayData == null){
                    yesVisualizationHomeKeyBusinessBO.setData(BigInteger.ZERO);
                }else{
                    yesVisualizationHomeKeyBusinessBO.setData(yesterdayData.getMobileCnt());
                }
                list.add(yesVisualizationHomeKeyBusinessBO);
                VisualizationHomeData visualizationHomeDataMobile = mapper.queryParseTotalCntMobileToday(visualizationHomeDataVOToday);
                VisualizationHomeKeyBusinessBO visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
                visualizationHomeKeyBusinessBO.setName("手机今日解析量");
                visualizationHomeKeyBusinessBO.setData(visualizationHomeDataMobile.getParseTotalCnt());
                list.add(visualizationHomeKeyBusinessBO);
            }else{
                typeCode = "wlan_cnt";
                yesVisualizationHomeKeyBusinessBO.setName("固网昨日解析量");
                if(yesterdayData == null){
                    yesVisualizationHomeKeyBusinessBO.setData(BigInteger.ZERO);
                }else{
                    yesVisualizationHomeKeyBusinessBO.setData(yesterdayData.getWlanCnt());
                }
                list.add(yesVisualizationHomeKeyBusinessBO);
                VisualizationHomeData visualizationHomeDataWlan = mapper.queryParseTotalCntWlanToday(visualizationHomeDataVOToday);
                VisualizationHomeKeyBusinessBO visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
                visualizationHomeKeyBusinessBO.setName("固网今日解析量");
                visualizationHomeKeyBusinessBO.setData(visualizationHomeDataWlan.getParseTotalCnt());
                list.add(visualizationHomeKeyBusinessBO);
            }
        }else{
            VisualizationHomeData visualizationHomeData = mapper.queryParseTotalCntToday(visualizationHomeDataVOToday);
            VisualizationHomeKeyBusinessBO visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("今日解析量");
            visualizationHomeKeyBusinessBO.setData(visualizationHomeData.getParseTotalCnt());
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("手机今日解析量");
            visualizationHomeKeyBusinessBO.setData(visualizationHomeData.getMobileParseTotalCnt());
            list.add(visualizationHomeKeyBusinessBO);
            visualizationHomeKeyBusinessBO = new VisualizationHomeKeyBusinessBO();
            visualizationHomeKeyBusinessBO.setName("固网今日解析量");
            visualizationHomeKeyBusinessBO.setData(visualizationHomeData.getWlanParseTotalCnt());
            list.add(visualizationHomeKeyBusinessBO);
        }
        return list;
    }

    public List<ResourceWebsiteUserSource> findUserSource(VisualizationHomeDataVO visualizationHomeDataVO){
        List<ResourceWebsiteUserSource> dataList = new ArrayList<>();
        if(visualizationHomeDataVO.getIsToday()){
            dataList = mapper.findUserSourceToday(visualizationHomeDataVO);
        }else{
            dataList = mapper.findUserSource(visualizationHomeDataVO,"rpt_resource_user_distribution_visualized_" + visualizationHomeDataVO.getQueryType());
            if(dataList == null || dataList.size() == 0){
                Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
                dataList = map.get("userSourceDataList");
                if(dataList == null){
                    VisualizationHomeDataVO maxTimeData = mapper.getMaxTime("rpt_resource_user_distribution_visualized_" + visualizationHomeDataVO.getQueryType()+"_real");
                    if(maxTimeData == null){
                        dataList = new ArrayList<>();
                        visualizationHomeRamMap.initRegion("userSourceDataList",dataList);
                        return dataList;
                    }
                    String startTime = getStartTime(visualizationHomeDataVO.getStartTime(),visualizationHomeDataVO.getEndTime(),maxTimeData.getEndTime());
                    visualizationHomeDataVO.setStartTime(startTime);
                    visualizationHomeDataVO.setEndTime(maxTimeData.getEndTime());
                    dataList = mapper.findUserSource(visualizationHomeDataVO,"rpt_resource_user_distribution_visualized_" + visualizationHomeDataVO.getQueryType());
                    visualizationHomeRamMap.initRegion("userSourceDataList",dataList);
                }
            }else{
                Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
                map.remove("userSourceDataList");
            }
        }
        for (ResourceWebsiteUserSource resourceWebsiteUserSource : dataList) {
            if("未知".equals(resourceWebsiteUserSource.getAnswerFirstCity())){
                ResourceWebsiteUserSource data = resourceWebsiteUserSource;
                dataList.remove(resourceWebsiteUserSource);
                dataList.add(data);
                break;
            }
        }
        return dataList;
    }

    public static Double buildRatioGT(BigDecimal beforeData, BigDecimal data) {
        if (beforeData.compareTo(BigDecimal.ZERO) == 0) {
            return 0D;
        }
        BigDecimal result = data.subtract(beforeData).divide(beforeData, 4, RoundingMode.HALF_UP);
        return result.doubleValue();
    }

    private List<VisualizationHomeMapBO> statisticsProvinceDomainParseCount(List<AnswerDistribution> distributionList, Boolean isAddZero) {
        //进行排序
        List<AnswerDistribution> sortList = distributionList.stream().sorted(new Comparator<AnswerDistribution>() {
            @Override
            public int compare(AnswerDistribution o1, AnswerDistribution o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());
        List<VisualizationHomeMapBO> resultList = new ArrayList<>();
        for(int index = 0; index < sortList.size(); index++) {
            AnswerDistribution answerDistribution = sortList.get(index);
            VisualizationHomeMapBO visualizationHomeMapBO = new VisualizationHomeMapBO();
            long rankNumber = index + 1;
            visualizationHomeMapBO.setRankNumber(rankNumber);
            visualizationHomeMapBO.setDistribution(answerDistribution.getProvince());
            visualizationHomeMapBO.setParseTotalCnt(answerDistribution.getParseTotalCnt());
            resultList.add(visualizationHomeMapBO);
        }
        return resultList;
    }

    public VisualizationHomeDataVO formatParseTime(VisualizationHomeDataVO visualizationHomeDataVO, String intervalType) {
        if (StringUtils.isEmpty(visualizationHomeDataVO.getEndTime()) || StringUtils.isEmpty(visualizationHomeDataVO.getStartTime())) {
            Map<String, String> timeParamMap = ReportUtils.buildTimeParam(intervalType);
            visualizationHomeDataVO.setStartTime(timeParamMap.get(ReportUtils.NOW_START));
            visualizationHomeDataVO.setEndTime(timeParamMap.get(ReportUtils.NOW_END));
        }
        return visualizationHomeDataVO;
    }

    public JSONObject top10WebsiteParseCnt(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<String> top10DataNameList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<VisualizationHomeTopTenParse> topTenParses = Lists.newArrayList();
        String queryTable = "rpt_resource_website_topn_detail_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            topTenParses = mapper.top10WebsiteIsTodayParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }else {
            topTenParses = mapper.top10WebsiteParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }
        if (topTenParses == null || topTenParses.size() == 0){
            topTenParses = getTop10ListLastTime(visualizationHomeDataVO,queryTable,"top10WebsiteDataList");
        }else{
            Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
            map.remove("top10WebsiteDataList");
        }
        topTenParses.stream().forEach(topDataVO -> {
            top10DataNameList.add(topDataVO.getWebsiteAppName());
            parseTotalList.add(topDataVO.getParseTotalCnt());
        });
//        if (top10DataNameList.size() == 0){
//            Collections.addAll(top10DataNameList, "抖音","快手","微信","QQ","百度","今日头条","西瓜视频","爱奇艺","淘宝","天猫");
//            Collections.addAll(parseTotalList,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO);
//        }
        JSONObject finalResult = buildRankReportWithParam("Top10应用分布图", top10DataNameList, parseTotalList, 10);
        return finalResult;
    }

    private List<VisualizationHomeTopTenParse> getTop10ListLastTime(VisualizationHomeDataVO visualizationHomeDataVO,String queryTable,String mapKey){
        List<VisualizationHomeTopTenParse> distributionList = new ArrayList<>();
        Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
        distributionList = map.get(mapKey);
        if(distributionList == null){
            VisualizationHomeDataVO maxTimeData = mapper.getMaxTime(queryTable + visualizationHomeDataVO.getQueryType()+"_real");
            if(maxTimeData == null){
                distributionList = new ArrayList<>();
                visualizationHomeRamMap.initRegion(mapKey,distributionList);
                return distributionList;
            }
            String startTime = getStartTime(visualizationHomeDataVO.getStartTime(),visualizationHomeDataVO.getEndTime(),maxTimeData.getEndTime());
            visualizationHomeDataVO.setStartTime(startTime);
            visualizationHomeDataVO.setEndTime(maxTimeData.getEndTime());
            if ("top10WebsiteDataList".equals(mapKey)){
                distributionList = mapper.top10WebsiteParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
                visualizationHomeRamMap.initRegion(mapKey,distributionList);
            }else if ("top10TypeDataList".equals(mapKey)){
                distributionList = mapper.top10TypeParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
                visualizationHomeRamMap.initRegion(mapKey,distributionList);
            }else if ("top10DomainDataList".equals(mapKey)){
                distributionList = mapper.top10DomainNameParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
                visualizationHomeRamMap.initRegion(mapKey,distributionList);
            }else if ("answerFirstIspProportion".equals(mapKey)){
                distributionList = mapper.answerFirstIspProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
                visualizationHomeRamMap.initRegion(mapKey,distributionList);
            }
        }
        return distributionList;
    }

    public JSONObject top10TypeParseCnt(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<String> top10DataNameList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<VisualizationHomeTopTenParse> topTenParses = Lists.newArrayList();
        String queryTable = "rpt_resource_popular_domain_topn_type_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            topTenParses = mapper.top10TypeIsTodayParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }else {
            topTenParses = mapper.top10TypeParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }
        if (topTenParses == null || topTenParses.size() == 0){
            topTenParses = getTop10ListLastTime(visualizationHomeDataVO,queryTable,"top10TypeDataList");
        }else{
            Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
            map.remove("top10TypeDataList");
        }
        topTenParses.stream().forEach(topDataVO -> {
            top10DataNameList.add(topDataVO.getDomainType());
            parseTotalList.add(topDataVO.getParseTotalCnt());
        });
//        if (top10DataNameList.size() == 0){
//            Collections.addAll(top10DataNameList, "视频","云平台","综合其他","科技数码","电商购物","社交网站","交通旅游","游戏网站","新闻传媒","教育培训");
//            Collections.addAll(parseTotalList,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO);
//        }
        JSONObject finalResult = buildRankReportWithParam("Top10分类分布图", top10DataNameList, parseTotalList, 10);
        return finalResult;
    }

    public JSONObject top10DomainNameParseCnt(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<String> top10DataNameList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<VisualizationHomeTopTenParse> topTenParses = Lists.newArrayList();
        String queryTable = "rpt_resource_domain_topn_detail_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            topTenParses = mapper.top10DomainNameIsTodayParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }else {
            topTenParses = mapper.top10DomainNameParseCnt(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }
        if (topTenParses == null || topTenParses.size() == 0){
            topTenParses = getTop10ListLastTime(visualizationHomeDataVO,queryTable,"top10DomainDataList");
        }else{
            Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
            map.remove("top10DomainDataList");
        }
        topTenParses.stream().forEach(topDataVO -> {
            top10DataNameList.add(topDataVO.getDomainName());
            parseTotalList.add(topDataVO.getParseTotalCnt());
        });
//        if (top10DataNameList.size() == 0){
//            Collections.addAll(top10DataNameList, "www.taobao.com","www.jd.com","query.hicloud.com","httpdns.alicdn.com","aps.amap.com","softpr.hl.chinamobile.com","v11.kwaicdn.com","www.sina.com.cn","dns.weixin.qq.com","cgicol.amap.com");
//            Collections.addAll(parseTotalList,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO,BigInteger.ZERO);
//        }
        JSONObject finalResult = buildRankReportWithParam("Top10域名分布图", top10DataNameList, parseTotalList, 10);
        return finalResult;
    }

    public static JSONObject buildRankReportWithParam(String reportName, List<String> yAxis, List<BigInteger> series, int pageSize) {
        JSONObject pageReport = new JSONObject(new LinkedHashMap());
        List<JSONObject> dataList = com.google.common.collect.Lists.newArrayList();
        for (int index = 0; index < series.size(); ) {
            if (index % pageSize == 0) {
                JSONObject yAxisData = new JSONObject(new LinkedHashMap());
                JSONObject textData = new JSONObject(new LinkedHashMap());
                JSONObject seriesData = new JSONObject(new LinkedHashMap());
                yAxisData.put("data", yAxis.subList(index, index + pageSize > yAxis.size() ? yAxis.size() : index + pageSize));
                textData.put("text", reportName);
                seriesData.put("data", series.subList(index, index + pageSize > series.size() ? series.size() : index + pageSize));
                pageReport.put(YAXIS, yAxisData);
                pageReport.put(TITLE, textData);
                pageReport.put(SERIES, seriesData);
                dataList.add(pageReport);
            }
            index = index + pageSize;
        }
        return pageReport;
    }


    public JSONObject answerFirstIspProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<AnswerDistributionRateVO> rateVOList = Lists.newArrayList();
        List<VisualizationHomeTopTenParse> ispParseCntList = Lists.newArrayList();
        String queryTable = "rpt_resource_domain_topn_detail_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            ispParseCntList = mapper.answerFirstIspIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }else {
            ispParseCntList = mapper.answerFirstIspProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType());
        }
        if (ispParseCntList == null || ispParseCntList.size() == 0){
            ispParseCntList = getTop10ListLastTime(visualizationHomeDataVO,queryTable,"answerFirstIspProportion");
        }else{
            Map<String, List> map = visualizationHomeRamMap.getAllRegionMap();
            map.remove("answerFirstIspProportion");
        }
        ispParseCntList.stream().forEach(topDataVO -> {
            AnswerDistributionRateVO rateVO = new AnswerDistributionRateVO();
            rateVO.setName(topDataVO.getAnswerFirstIsp());
            rateVO.setValue(topDataVO.getParseTotalCnt());
            rateVOList.add(rateVO);
        });
        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        finalResult.put("name","资源分布占比");
        finalResult.put("data", rateVOList);
        return finalResult;
    }


    /**
     * top网站占比
     * @param visualizationHomeDataVO
     * @return
     */
    public JSONObject topNWebsiteProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<AnswerDistributionRateVO> rateVOList = Lists.newArrayList();
        VisualizationHomeData visualizationHomeDataTop = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeDataAll = new VisualizationHomeData();
        String queryTable = "rpt_resource_website_topn_detail_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            visualizationHomeDataTop = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
        }else {
            visualizationHomeDataTop = mapper.topNWebsiteProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNWebsiteProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
        }
        if (visualizationHomeDataTop == null){
            getTop10ProportionLastTime(visualizationHomeDataVO, queryTable);
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
                visualizationHomeDataTop = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
                visualizationHomeDataAll = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
            }else {
                visualizationHomeDataTop = mapper.topNWebsiteProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
                visualizationHomeDataAll = mapper.topNWebsiteProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
            }
        }
        rateVOList.add(extracted("Top10应用",visualizationHomeDataTop.getParseTotalCnt()));
        VisualizationHomeData visualizationHomeData = new VisualizationHomeData();
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())&&visualizationHomeDataVO.getRankNumber()!=10L) {
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
                visualizationHomeData = mapper.topNWebsiteIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","false");
            }else {
                visualizationHomeData = mapper.topNWebsiteProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","false");
            }
            rateVOList.add(extracted("其他TopN",visualizationHomeData.getParseTotalCnt()));
        }
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())&&visualizationHomeDataVO.getRankNumber()!=10L) {
            rateVOList.add(extracted("其他应用",visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeData.getParseTotalCnt()).subtract(visualizationHomeDataTop.getParseTotalCnt())));
        }else {
            rateVOList.add(extracted("其他应用",visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeDataTop.getParseTotalCnt())));
        }
        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        finalResult.put("name","Top应用比重图");
        finalResult.put("data", rateVOList);
        return finalResult;
    }


    private VisualizationHomeDataVO getTop10ProportionLastTime(VisualizationHomeDataVO visualizationHomeDataVO,String queryTable){
        if(visualizationHomeDataVO == null){
            VisualizationHomeDataVO maxTimeData = mapper.getMaxTime(queryTable + visualizationHomeDataVO.getQueryType()+"_real");
            String startTime = getStartTime(visualizationHomeDataVO.getStartTime(),visualizationHomeDataVO.getEndTime(),maxTimeData.getEndTime());
            visualizationHomeDataVO.setStartTime(startTime);
            visualizationHomeDataVO.setEndTime(maxTimeData.getEndTime());
        }
        return visualizationHomeDataVO;
    }


    /**
     * top分类占比
     * @param visualizationHomeDataVO
     * @return
     */
    public JSONObject topNTypeProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<AnswerDistributionRateVO> rateVOList = Lists.newArrayList();
        VisualizationHomeData visualizationHomeDataTop = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeData = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeDataAll = new VisualizationHomeData();
        String queryTable = "rpt_resource_popular_domain_topn_type_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
            visualizationHomeDataTop = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
        }else {
            visualizationHomeDataTop = mapper.topNTypeProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNTypeProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
        }
        if (visualizationHomeDataTop == null){
            getTop10ProportionLastTime(visualizationHomeDataVO, queryTable);
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()){
                visualizationHomeDataTop = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
                visualizationHomeDataAll = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
            }else {
                visualizationHomeDataTop = mapper.topNTypeProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
                visualizationHomeDataAll = mapper.topNTypeProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
            }
        }
        rateVOList.add(extracted("Top10分类",visualizationHomeDataTop.getParseTotalCnt()));
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())&&visualizationHomeDataVO.getRankNumber()!=10L) {
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
                visualizationHomeData = mapper.topNTypeIsTodayProportion(visualizationHomeDataVO, queryTable + visualizationHomeDataVO.getQueryType(), "false", "false");
            }else {
                visualizationHomeData = mapper.topNTypeProportion(visualizationHomeDataVO, queryTable + visualizationHomeDataVO.getQueryType(), "false", "false");
            }
            rateVOList.add(extracted("其他TopN",visualizationHomeData.getParseTotalCnt()));
        }
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())&&visualizationHomeDataVO.getRankNumber()!=10L) {
            rateVOList.add(extracted("其他分类",visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeData.getParseTotalCnt()).subtract(visualizationHomeDataTop.getParseTotalCnt())));
        }else {
            rateVOList.add(extracted("其他分类",visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeDataTop.getParseTotalCnt())));
        }
        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        finalResult.put("name","Top分类比重图");
        finalResult.put("data", rateVOList);
        return finalResult;
    }

    /**
     * top域名占比
     * @param visualizationHomeDataVO
     * @return
     */
    public JSONObject topNDomainNameProportion(VisualizationHomeDataVO visualizationHomeDataVO){
        visualizationHomeDataVO = formatParseTime(visualizationHomeDataVO,ConstantEntity.INTERVAL_10MIN);
        List<AnswerDistributionRateVO> rateVOList = Lists.newArrayList();
        VisualizationHomeData visualizationHomeDataTop = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeData = new VisualizationHomeData();
        VisualizationHomeData visualizationHomeDataAll = new VisualizationHomeData();
        String queryTable = "rpt_resource_domain_topn_detail_";
        if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
            visualizationHomeDataTop = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
        }else {
            visualizationHomeDataTop = mapper.topNDomainNameProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
            visualizationHomeDataAll = mapper.topNDomainNameProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
        }
        if (visualizationHomeDataTop == null){
            getTop10ProportionLastTime(visualizationHomeDataVO, queryTable);
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
                visualizationHomeDataTop = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
                visualizationHomeDataAll = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
            }else {
                visualizationHomeDataTop = mapper.topNDomainNameProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","true");
                visualizationHomeDataAll = mapper.topNDomainNameProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"true","false");
            }
        }
        rateVOList.add(extracted("Top10域名",visualizationHomeDataTop.getParseTotalCnt()));
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())&&visualizationHomeDataVO.getRankNumber()!=10L) {
            if (ObjectUtil.isNotNull(visualizationHomeDataVO.getIsToday()) && visualizationHomeDataVO.getIsToday()) {
                visualizationHomeData = mapper.topNDomainNameIsTodayProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","false");
            }else {
                visualizationHomeData = mapper.topNDomainNameProportion(visualizationHomeDataVO,queryTable + visualizationHomeDataVO.getQueryType(),"false","false");
            }
            rateVOList.add(extracted("其他TopN",visualizationHomeData.getParseTotalCnt()));
        }
        if (ObjectUtil.isNotEmpty(visualizationHomeDataVO.getRankNumber())&&visualizationHomeDataVO.getRankNumber()!=10L) {
            rateVOList.add(extracted("其他域名",visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeData.getParseTotalCnt()).subtract(visualizationHomeDataTop.getParseTotalCnt())));
        }else {
            rateVOList.add(extracted("其他域名",visualizationHomeDataAll.getParseTotalCnt().subtract(visualizationHomeDataTop.getParseTotalCnt())));
        }
        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        finalResult.put("name","Top域名比重图");
        finalResult.put("data", rateVOList);
        return finalResult;
    }

    private AnswerDistributionRateVO extracted(String name, BigInteger value) {
        AnswerDistributionRateVO rateTop = new AnswerDistributionRateVO();
        rateTop.setName(name);
        rateTop.setValue(value);
        return rateTop;
    }

    private String interceptTimeStr(Date parseTime,String queryType){
        String parseTimeStr = "";
        if("1min".equals(queryType) || "10min".equals(queryType)){
            parseTimeStr = DateUtils.formatDataToString(parseTime, DateUtils.DEFAULT_HOUR_FMT);
        }else if("1h".equals(queryType)){
            parseTimeStr = DateUtils.formatDataToString(parseTime, DateUtils.DEFAULT_HOUR_FMT);
        }else{
            parseTimeStr = DateUtils.formatDataToString(parseTime, "MM-dd");
        }
        return parseTimeStr;
    }

    //根据页面开始/结束时间计算数据库最后时段的开始时间
    private String getStartTime(String startTime,String endTime,String maxTime){
        LocalDateTime startLocalTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endLocalTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //计算时间间隔
        Duration between = Duration.between(startLocalTime, endLocalTime);
        // 环比开始时间(向前推一个时间段)
        LocalDateTime earlierEndLocalTime = LocalDateTime.parse(maxTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime earlierStartLocalTime = earlierEndLocalTime.minus(between);
        Map<String, String> result = Maps.newHashMap();
        return earlierStartLocalTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
