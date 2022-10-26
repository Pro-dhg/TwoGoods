package com.yamu.data.sample.service.resources.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yamu.data.sample.service.common.util.CsvUtils;
import com.yamu.data.sample.service.common.util.DateUtils;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.common.util.StrUtils;
import com.yamu.data.sample.service.resources.entity.bo.AnswerDistributionBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.AnswerDistribution;
import com.yamu.data.sample.service.resources.entity.vo.AnswerDistributionMapDataVO;
import com.yamu.data.sample.service.resources.entity.vo.AnswerDistributionRateVO;
import com.yamu.data.sample.service.resources.mapper.AnswerDistributionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author getiejun
 * Date 2020-07-1
 */
@Service
@Slf4j
public class AnswerDistributionService extends BaseService{

    @Autowired
    private AnswerDistributionMapper answerDistributionMapper;

    /**
     * 静态省节点信息，如果切换为双数据库，则可以替换
     */
    private static List<String> provinceNode = Arrays.asList("新疆维吾尔自治区", "青海省", "湖北省", "香港特别行政区", "山西省", "云南省", "河北省", "广西壮族自治区",
            "海南省", "上海市", "辽宁省", "澳门特别行政区", "福建省", "陕西省", "四川省", "贵州省", "广东省", "北京市", "江苏省", "黑龙江省", "天津市", "重庆市", "山东省",
            "内蒙古自治区", "宁夏回族自治区", "浙江省", "台湾省", "西藏自治区", "吉林省", "安徽省", "江西省", "甘肃省", "河南省", "湖南省");

    private static final String ORDER_BY_PARSE_TIME = "parse_time";

    private static final DecimalFormat decimalFormatForTwo = new DecimalFormat("0.00");

    private final static String PROVINCE_CODE_END_STRING = "0000";

    private final static int DEFAULT_TOP_FOR_TREND_REPORT = 10;

    private final static String DEFAULT_CITY = "辖区";

    /**
     * 查找省市地图
     * @param answerDistributionBO
     * @return
     */
    public List findProvinceMapDataList(AnswerDistributionBO answerDistributionBO) {
        //根据时间参数进行查询，然后进行汇总，返回前端
        //1.查询，用map进行统计，然后返回
        answerDistributionBO = checkFindProvinceMapDataListMethodParam(answerDistributionBO);
        List resultList = Lists.newArrayList();
        List<AnswerDistributionMapDataVO> distributionMapDataVOList = Lists.newArrayList();
        if(isSearchProvinceInfo(answerDistributionBO.getProvince())) {
            // 如果是省市为单位进行统计，则根据province 进行汇总，如果以区为单位，则不需要汇总
            List<AnswerDistribution> distributionList = answerDistributionMapper.findInfoGroupByProvince(answerDistributionBO);
            distributionMapDataVOList = statisticsProvinceDomainParseCount(distributionList, true);
        } else {
            answerDistributionBO.setProvince(super.getWholeProvinceName(answerDistributionBO.getProvince()));
            List<AnswerDistribution> distributionList = answerDistributionMapper.findInfoGroupByCity(answerDistributionBO);
            distributionMapDataVOList = statisticsDistributionDomainParseCount(distributionList);
        }
        resultList = convertMapDataToJsonResult(distributionMapDataVOList);
        return resultList;
    }

    private AnswerDistributionBO checkFindProvinceMapDataListMethodParam(AnswerDistributionBO answerDistributionBO) {
        // 运营商ispCode 0 默认为全国
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getIspCode()) && answerDistributionBO.getIspCode().equals("0")) {
            answerDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getRankNumber()) && answerDistributionBO.getRankNumber().equals(0)) {
            answerDistributionBO.setRankNumber(null);
        }
        return answerDistributionBO;
    }

    private List convertMapDataToJsonResult(List<AnswerDistributionMapDataVO> distributionMapDataVOList) {
        List resultList = Lists.newArrayList();
        for(int index = 0; index < distributionMapDataVOList.size(); index++) {
            AnswerDistributionMapDataVO mapDataVO = distributionMapDataVOList.get(index);
            long rankNumber = index + 1;
            JSONObject dataResult = new JSONObject(new LinkedHashMap());
            String shortDistrict = super.provinceAbbrMap.get(mapDataVO.getDistribution());
            dataResult.put("name", StrUtil.isNotEmpty(shortDistrict) ? shortDistrict : mapDataVO.getDistribution());
            dataResult.put("district", StrUtil.isNotEmpty(shortDistrict) ? shortDistrict : mapDataVO.getDistribution());
            dataResult.put("value", Arrays.asList(mapDataVO.getParseTotalCnt(), rankNumber));
            resultList.add(dataResult);
        }
        return resultList;
    }

    /**
     * 统计省份地区解析数
     * @param distributionList
     * @return
     */
    private List<AnswerDistributionMapDataVO> statisticsDistributionDomainParseCount(List<AnswerDistribution> distributionList) {
        List<AnswerDistributionMapDataVO> mapDataVOList = Lists.newArrayList();
        Map<String, BigInteger> areaMap = Maps.newHashMap();
        distributionList.stream().forEach(answerDistribution -> {
            BigInteger parseTotal = answerDistribution.getParseTotalCnt();
            if(areaMap.containsKey(answerDistribution.getCity())) {
                parseTotal = parseTotal.add(areaMap.get(answerDistribution.getCity()));
            }
            areaMap.put(answerDistribution.getCity(), parseTotal);
        });

        // 区域数据暂时没法补点
        areaMap.entrySet().forEach(areaMapEntry -> {
            if(StrUtil.isNotEmpty(areaMapEntry.getKey())){
                AnswerDistributionMapDataVO answerDistributionMapDataVO = new AnswerDistributionMapDataVO();
                answerDistributionMapDataVO.setParseTotalCnt(areaMapEntry.getValue());
                answerDistributionMapDataVO.setDistribution(areaMapEntry.getKey());
                mapDataVOList.add(answerDistributionMapDataVO);
            }
        });

        //进行排序
        List<AnswerDistributionMapDataVO> sortList = mapDataVOList.stream().sorted(new Comparator<AnswerDistributionMapDataVO>() {
            @Override
            public int compare(AnswerDistributionMapDataVO o1, AnswerDistributionMapDataVO o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());
        return sortList;
    }

    private List<AnswerDistribution> sortAnswerDistributionList(List<AnswerDistribution> distributionList){
        if(CollUtil.isNotEmpty(distributionList)) {
            //进行排序
            distributionList.stream().sorted(new Comparator<AnswerDistribution>() {
                @Override
                public int compare(AnswerDistribution o1, AnswerDistribution o2) {
                    return o1.getParseTotalCnt().compareTo(o2.getParseTotalCnt());
                }
            });
        }
        return distributionList;
    }

    private List<AnswerDistributionMapDataVO> sortAnswerDistributionMapDataList(List<AnswerDistributionMapDataVO> distributionMapList){
        if(CollUtil.isNotEmpty(distributionMapList)) {
            //进行排序
            distributionMapList.stream().sorted(new Comparator<AnswerDistributionMapDataVO>() {
                @Override
                public int compare(AnswerDistributionMapDataVO o1, AnswerDistributionMapDataVO o2) {
                    return o1.getParseTotalCnt().compareTo(o2.getParseTotalCnt());
                }
            });
        }
        return distributionMapList;
    }

    private boolean isSearchProvinceInfo(String province) {
        if(ObjectUtil.isEmpty(province)) {
            return true;
        }
        return false;
    }

    /**
     * 统计省份地区域名解析数
     * @param distributionList
     * @return
     */
    private List<AnswerDistributionMapDataVO> statisticsProvinceDomainParseCount(List<AnswerDistribution> distributionList, Boolean isAddZero) {
        List<AnswerDistributionMapDataVO> mapDataVOList = Lists.newArrayList();
        Map<String, BigInteger> provinceMap = Maps.newHashMap();
        distributionList.stream().forEach(answerDistribution -> {
            if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                BigInteger parseTotal = answerDistribution.getParseTotalCnt(); // 北京市&1_440404
                String province = answerDistribution.getProvince();
                if(provinceMap.containsKey(province)) {
                    parseTotal = parseTotal.add(provinceMap.get(province));
                }
                provinceMap.put(province, parseTotal);
            }
        });

        provinceMap.entrySet().forEach(provinceMapEntry -> {
            if(StrUtil.isNotEmpty(provinceMapEntry.getKey())) {
                String province = provinceMapEntry.getKey();
                AnswerDistributionMapDataVO answerDistributionMapDataVO = new AnswerDistributionMapDataVO();
                answerDistributionMapDataVO.setParseTotalCnt(provinceMapEntry.getValue());
                answerDistributionMapDataVO.setDistribution(province);
                mapDataVOList.add(answerDistributionMapDataVO);
            }
        });

        if(isAddZero) { // 地图不需要补零，其余进行补零操作
            List<String> existProvince = Lists.newArrayList();
            provinceMap.keySet().forEach(province -> {
                existProvince.add(province);
            });
            provinceNode.stream().forEach(provinceNode -> {
                if(!existProvince.contains(provinceNode)) {
                    AnswerDistributionMapDataVO answerDistributionMapDataVO = new AnswerDistributionMapDataVO();
                    answerDistributionMapDataVO.setParseTotalCnt(BigInteger.ZERO);
                    answerDistributionMapDataVO.setDistribution(provinceNode);
                    mapDataVOList.add(answerDistributionMapDataVO);
                }
            });
        }

        //进行排序
        List<AnswerDistributionMapDataVO> sortList = mapDataVOList.stream().sorted(new Comparator<AnswerDistributionMapDataVO>() {
            @Override
            public int compare(AnswerDistributionMapDataVO o1, AnswerDistributionMapDataVO o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());
        return sortList;
    }

    public JSONObject findProvinceRank(AnswerDistributionBO answerDistributionBO) {
        //根据时间参数进行查询，然后进行汇总，返回前端
        //1.查询，用map进行统计，然后返回
        answerDistributionBO = checkFindProvinceRankMethodParam(answerDistributionBO);
        List<String> distributionList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<AnswerDistributionMapDataVO> distributionMapDataVOList = Lists.newArrayList();
        if(isSearchProvinceInfo(answerDistributionBO.getProvince())) {
            List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findInfoGroupByProvince(answerDistributionBO);
            distributionMapDataVOList = statisticsProvinceDomainParseCount(answerDistributionList, false);
        } else {
            answerDistributionBO.setProvince(super.getWholeProvinceName(answerDistributionBO.getProvince()));
            List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findInfoGroupByCity(answerDistributionBO);
            distributionMapDataVOList = statisticsDistributionDomainParseCount(answerDistributionList);
        }
        distributionMapDataVOList.stream().forEach(distributionMapDataVO -> {
            distributionList.add(distributionMapDataVO.getDistribution());
            parseTotalList.add(distributionMapDataVO.getParseTotalCnt());
        });
        JSONObject finalResult = ReportUtils.buildRankReportWithParam("资源分布排名", distributionList, parseTotalList, 7);
        return finalResult;
    }

    private AnswerDistributionBO checkFindProvinceRankMethodParam(AnswerDistributionBO answerDistributionBO) {
        if("0".equals(answerDistributionBO.getIspCode())) {
            answerDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getRankNumber()) && answerDistributionBO.getRankNumber().equals(0)) {
            answerDistributionBO.setRankNumber(null);
        }
        return answerDistributionBO;
    }

    public JSONObject findProvinceRate(AnswerDistributionBO answerDistributionBO) {
        //根据时间参数进行查询，然后进行汇总，返回前端
        answerDistributionBO = checkFindProvinceRateMethodParam(answerDistributionBO);
        List<AnswerDistributionRateVO> rateVOList = Lists.newArrayList();
        List<AnswerDistributionRateVO> finalDataResult = Lists.newArrayList();
        List<AnswerDistributionMapDataVO> distributionMapDataVOList = Lists.newArrayList();
        String reportName = "";
        if(isSearchProvinceInfo(answerDistributionBO.getProvince())) {
            List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findInfoGroupByProvince(answerDistributionBO);
            distributionMapDataVOList = statisticsProvinceDomainParseCount(answerDistributionList, false);
            reportName = "各省资源占比";
        } else {
            answerDistributionBO.setProvince(super.getWholeProvinceName(answerDistributionBO.getProvince()));
            List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findInfoGroupByCity(answerDistributionBO);
            distributionMapDataVOList = statisticsDistributionDomainParseCount(answerDistributionList);
            reportName = "各地市资源占比";
        }
        for (AnswerDistributionMapDataVO distributionMapDataVO : distributionMapDataVOList) {
            AnswerDistributionRateVO rateVO = new AnswerDistributionRateVO();
            rateVO.setValue(distributionMapDataVO.getParseTotalCnt());
            rateVO.setName(distributionMapDataVO.getDistribution());
            rateVOList.add(rateVO);
        }
        //对数据进行降序排序
        rateVOList = sortUserDistributionRateVOByDesc(rateVOList);
        //对数据进行统计
        statisticsUserDistributionRateListForOtherOrAll(rateVOList, finalDataResult);

        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        finalResult.put("name", reportName);
        finalResult.put("data", finalDataResult);
        return finalResult;
    }

    private void statisticsUserDistributionRateListForOtherOrAll(List<AnswerDistributionRateVO> rateVOList, List<AnswerDistributionRateVO> finalDataResult) {
        if(rateVOList.size() > 0) {
            BigInteger totalParseCnt = BigInteger.ZERO;
            for(int i = 0; i < rateVOList.size(); i++) {
                if(i >= DEFAULT_TOP_FOR_TREND_REPORT) {
                    totalParseCnt = totalParseCnt.add(rateVOList.get(i).getValue());
                } else {
                    finalDataResult.add(rateVOList.get(i));
                }
            }
            finalDataResult.add(new AnswerDistributionRateVO("其他", totalParseCnt));
        } else {
            //针对没有数据处理
            finalDataResult.add(new AnswerDistributionRateVO("全部", BigInteger.ZERO));
        }
    }

    private List<AnswerDistributionRateVO> sortUserDistributionRateVOByDesc(List<AnswerDistributionRateVO> rateVOList) {
        rateVOList.stream().sorted(new Comparator<AnswerDistributionRateVO>() {
            @Override
            public int compare(AnswerDistributionRateVO o1, AnswerDistributionRateVO o2) {
                return o2.getValue().compareTo(o2.getValue());
            }
        });
        return rateVOList;
    }

    private AnswerDistributionBO checkFindProvinceRateMethodParam(AnswerDistributionBO answerDistributionBO) {
        if("0".equals(answerDistributionBO.getIspCode())) {
            answerDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getRankNumber()) && answerDistributionBO.getRankNumber().equals(0)) {
            answerDistributionBO.setRankNumber(null);
        }
        return answerDistributionBO;
    }

    public JSONObject findTrendReport(AnswerDistributionBO answerDistributionBO) {
        List<String> provinceList = findTrendProvinceByNum(answerDistributionBO, DEFAULT_TOP_FOR_TREND_REPORT);
        answerDistributionBO = checkFindTrendReportMethodParam(answerDistributionBO);
        calculateTotalDataNumAndResetSearchTime(answerDistributionBO);
        Set<Date> aliveDateList = Sets.newTreeSet();
        JSONObject dataIndexJson = new JSONObject(new LinkedHashMap());
        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        Map<String, List<AnswerDistribution>> provinceMap = Maps.newLinkedHashMap();
        Map<String, List<AnswerDistribution>> provinceResultMap = Maps.newLinkedHashMap();
        Map<String, BigInteger> provinceParseCntMap = Maps.newHashMap();
        Map<Date, String> xAxisMap = Maps.newHashMap();
        String reportName = "";
        if(isSearchProvinceInfo(answerDistributionBO.getProvince())) {
            List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndProvince(answerDistributionBO);
            xAxisMap = ReportUtils.buildXaxisMap(answerDistributionBO.getStartTime(), answerDistributionBO.getEndTime(), answerDistributionBO.getQueryType());
            collectProvinceReportData(answerDistributionList, xAxisMap, aliveDateList, dataIndexJson, provinceMap, answerDistributionBO, provinceList);
            reportName = "各省活跃资源解析量趋势分析";
        } else{
            if(answerDistributionBO.getProvinceFlag()==null ){
                answerDistributionBO.setProvince(super.getWholeProvinceName(answerDistributionBO.getProvince()));
            }
            List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndCity(answerDistributionBO);
            xAxisMap = ReportUtils.buildXaxisMap(answerDistributionBO.getStartTime(), answerDistributionBO.getEndTime(), answerDistributionBO.getQueryType());
            collectDistributionReportData(answerDistributionList, xAxisMap, aliveDateList, dataIndexJson, provinceMap, provinceResultMap, provinceParseCntMap, answerDistributionBO);
            reportName = "各地市活跃资源解析量趋势分析";
        }
        xAxisMap = removeXAxisNullPointData(xAxisMap, aliveDateList);
        finalResult.put("data", dataIndexJson);
        finalResult.put("xAxis", xAxisMap.values());
        finalResult.put("name", reportName);
        return finalResult;
    }

    private Long calculateTotalDataNumAndResetSearchTime(AnswerDistributionBO answerDistributionBO) {
        Long total = answerDistributionMapper.countPageByParseTime(answerDistributionBO);
        /*
        List<String> timeData = answerDistributionMapper.pageByParseTime(answerDistributionBO);
        if(CollUtil.isNotEmpty(timeData)) {
            answerDistributionBO.setStartTime(timeData.get(0));
            answerDistributionBO.setEndTime(timeData.get(timeData.size() - 1));
        }
         */
        return total;
    }

    private Map<Date, String> removeXAxisNullPointData(Map<Date, String> xAxisMap, Set<Date> aliveDateList) {
        // 获取需要移除的x轴点
        List<Date> removeDateList = xAxisMap.keySet().stream().filter(date -> {
            if(aliveDateList.contains(date)){
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        // 移除X轴的点
        for (Date removeDate : removeDateList) {
            xAxisMap.remove(removeDate);
        }
        return xAxisMap;
    }

    private void collectDistributionReportData(List<AnswerDistribution> answerDistributionList, Map<Date, String> xAxisMap, Set<Date> aliveDateList, JSONObject dataIndexJson, Map<String, List<AnswerDistribution>> provinceMap, Map<String, List<AnswerDistribution>> provinceResultMap, Map<String, BigInteger> provinceParseCntMap, AnswerDistributionBO answerDistributionBO) {
        List<String> provinceRankList = new LinkedList<>();
        collectAnswerDistributionGroupByDistribution(answerDistributionList, provinceMap);
        // 对总体的访问量进行排名
        for (Map.Entry<String, List<AnswerDistribution>> provinceResultEntity : provinceMap.entrySet()) {
            List<AnswerDistribution> dataList = provinceResultEntity.getValue();
            BigInteger totalParseCnt = BigInteger.ZERO;
            for (AnswerDistribution answerDistribution : dataList) {
                totalParseCnt = answerDistribution.getParseTotalCnt().add(totalParseCnt);
            }
            provinceParseCntMap.put(provinceResultEntity.getKey(), totalParseCnt);
        }
        //针对访问量进行排序处理
        provinceParseCntMap.entrySet().stream().sorted(Map.Entry.<String, BigInteger>comparingByValue().reversed()).
                forEachOrdered(object -> provinceRankList.add(object.getKey()));

        //针对总访问量排序后的省市进行top10 以及other处理
        collectDistributionRankByTop10AndOther(provinceRankList, provinceResultMap, provinceMap);

        provinceResultMap.values().stream().forEach(provinceList ->{
            if (provinceList.size()!=0){
                provinceList.stream().forEach(item->aliveDateList.add(item.getParseTime()));
            }
        });
        collectDistributionListByParseTime(provinceResultMap, xAxisMap, aliveDateList, dataIndexJson, answerDistributionBO);
    }

    private void collectDistributionListByParseTime(Map<String, List<AnswerDistribution>> provinceResultMap, Map<Date, String> xAxisMap, Set<Date> aliveDateList, JSONObject dataIndexJson, AnswerDistributionBO answerDistributionBO) {
        int index = 0;
        for (Map.Entry<String, List<AnswerDistribution>> entrySet : provinceResultMap.entrySet()) {
            String distributionName = entrySet.getKey();
            // 现在获取到是以市为单位的数据
            List<AnswerDistribution> dataList = entrySet.getValue();
            Map<Date, BigInteger> parseDataMap = Maps.newTreeMap();
            // 对省市为单位的数据，再根据解析时间进行封装
            dataList.stream().forEach(answerDistribution -> {
                // 根据时间进行汇总
//                long parseTotalCnt = 0;
//                if(parseDataMap.containsKey(answerDistribution.getParseTime())) {
//                    parseTotalCnt = parseTotalCnt + answerDistribution.getParseTotalCnt();
//                } else {
//                    parseTotalCnt = answerDistribution.getParseTotalCnt();
//                }
                if(ObjectUtil.isEmpty(parseDataMap.get(answerDistribution.getParseTime()))) {
                    parseDataMap.put(answerDistribution.getParseTime(), answerDistribution.getParseTotalCnt());
                } else {
                    BigInteger parseTotal = parseDataMap.get(answerDistribution.getParseTime());
                    parseDataMap.put(answerDistribution.getParseTime(), answerDistribution.getParseTotalCnt().add(parseTotal));
                }
            });
            List<BigInteger> parseData = parseDataMap.values().stream().collect(Collectors.toList());
            List totalResult = Lists.newArrayList();
            //判断需不需要补点
            if(xAxisMap.size() == parseDataMap.size()) {
                totalResult = parseData;
            } else {
                for (Date aliveDate : aliveDateList) {
                    BigInteger parseDataCnt = BigInteger.ZERO; //进行补点
                    if(parseDataMap.containsKey(aliveDate)) {
                        parseDataCnt = parseDataMap.get(aliveDate);
                    }
                    totalResult.add(parseDataCnt);
                }
            }
            //totalResult = PageUtils.subListByPage(totalResult, answerDistributionBO.getOffset(), answerDistributionBO.getLimit());
            JSONObject jsonData = new JSONObject(new LinkedHashMap());
            JSONObject resultData = new JSONObject(new LinkedHashMap());
            jsonData.put("data", totalResult);
            jsonData.put("name", distributionName);
            resultData.put("result", jsonData);
            dataIndexJson.put("data" + (++index), resultData);
        }
    }

    private void collectDistributionRankByTop10AndOther(List<String> provinceRankList, Map<String, List<AnswerDistribution>> provinceResultMap, Map<String, List<AnswerDistribution>> provinceMap) {
        if(provinceRankList.size() < DEFAULT_TOP_FOR_TREND_REPORT) {
            // 进行补点
            int pointNum = DEFAULT_TOP_FOR_TREND_REPORT - provinceRankList.size();
            for(String provinceName : provinceRankList) {
                //根据有序加入到结果集中
                provinceResultMap.put(provinceName, provinceMap.get(provinceName));
            }
        } else {
            // 如果top超过11个，则进行统计
            int pointNum = 0;
            List<AnswerDistribution> otherDistribution = Lists.newArrayList();
            for(String provinceName : provinceRankList) {
                if(pointNum < DEFAULT_TOP_FOR_TREND_REPORT) {
                    provinceResultMap.put(provinceName, provinceMap.get(provinceName));
                } else {
                    //其他进行汇总
                    otherDistribution.addAll(provinceMap.get(provinceName));
                }
                pointNum++;

            }
            provinceResultMap.put("其他", otherDistribution);
        }
    }

    private void collectAnswerDistributionGroupByDistribution(List<AnswerDistribution> answerDistributionList, Map<String, List<AnswerDistribution>> provinceMap) {
        answerDistributionList.stream().forEach(answerDistribution -> {
            if(StrUtil.isNotEmpty(answerDistribution.getCity())) {
                if(DEFAULT_CITY.equals(answerDistribution.getCity())) {
                    if(provinceMap.containsKey(answerDistribution.getDistrict())) {
                        provinceMap.get(answerDistribution.getDistrict()).add(answerDistribution);
                    }else {
                        List<AnswerDistribution> dataList = Lists.newArrayList();
                        dataList.add(answerDistribution);
                        provinceMap.put(answerDistribution.getDistrict(), dataList);
                    }
                } else {
                    if(provinceMap.containsKey(answerDistribution.getCity())) {
                        provinceMap.get(answerDistribution.getCity()).add(answerDistribution);
                    } else {
                        List<AnswerDistribution> dataList = Lists.newArrayList();
                        dataList.add(answerDistribution);
                        provinceMap.put(answerDistribution.getCity(), dataList);
                    }
                }
            }
        });
    }

    private void collectProvinceReportData(List<AnswerDistribution> answerDistributionList, Map<Date, String> xAxisMap, Set<Date> aliveDateList,
                                           JSONObject dataIndexJson, Map<String, List<AnswerDistribution>> provinceMap,
                                           AnswerDistributionBO answerDistributionBO, List<String> provinceList) {
        // 全国节点或者运营商节点进行汇总
        //1. 先以省市为单位，进行汇总，2.在每个省市基础上，按照parseTime进行汇总
        collectAnswerDistributionGroupByProvince(answerDistributionList, provinceMap, provinceList);
        provinceMap.values().stream().forEach(distributeList ->{
            if (distributeList.size()!=0){
                distributeList.stream().forEach(item->aliveDateList.add(item.getParseTime()));
            }
        });
        collectProvinceListByParseTime(provinceMap, dataIndexJson, xAxisMap, aliveDateList, answerDistributionBO);
    }

    private void collectProvinceListByParseTime(Map<String, List<AnswerDistribution>> provinceResultMap, JSONObject dataIndexJson, Map<Date, String> xAxisMap, Set<Date> aliveDateList, AnswerDistributionBO answerDistributionBO) {
        int index = 0;
        for (Map.Entry<String, List<AnswerDistribution>> entrySet : provinceResultMap.entrySet()) {
            String provinceName = entrySet.getKey();
            // 现在获取到是以省市为单位的数据
            List<AnswerDistribution> dataList = entrySet.getValue();
            Map<Date, BigInteger> parseDataMap = Maps.newTreeMap();
            // 对省市为单位的数据，再根据解析时间进行封装
            dataList.stream().forEach(answerDistribution -> {
                if(ObjectUtil.isEmpty(parseDataMap.get(answerDistribution.getParseTime()))) {
                    parseDataMap.put(answerDistribution.getParseTime(), answerDistribution.getParseTotalCnt());
                } else {
                    BigInteger parseTotal = parseDataMap.get(answerDistribution.getParseTime());
                    parseDataMap.put(answerDistribution.getParseTime(), answerDistribution.getParseTotalCnt().add(parseTotal));
                }
            });
            // 是否需要补点
            List<BigInteger> parseData = parseDataMap.values().stream().collect(Collectors.toList());
            List totalResult = Lists.newArrayList();
            if(xAxisMap.size() == parseDataMap.size()) {
                //不需要补点，遍历集合，获取数据，封装数据
                totalResult = parseData;
            } else {
                for (Date aliveDate : aliveDateList) {
                    BigInteger parseDataCnt = BigInteger.ZERO; //进行补点
                    if(parseDataMap.containsKey(aliveDate)) {
                        parseDataCnt = parseDataMap.get(aliveDate);
                    }
                    totalResult.add(parseDataCnt);
                }
            }
            //totalResult = PageUtils.subListByPage(totalResult, answerDistributionBO.getOffset(), answerDistributionBO.getLimit());
            JSONObject jsonData = new JSONObject(new LinkedHashMap());
            JSONObject resultData = new JSONObject(new LinkedHashMap());
            jsonData.put("data", totalResult);
            jsonData.put("name", provinceName);
            resultData.put("result", jsonData);
            dataIndexJson.put("data" + (++index), resultData);
        }
    }

    private void collectProvinceRankByTop10AndOther(List<String> provinceRankList, Map<String, List<AnswerDistribution>> provinceResultMap, Map<String, List<AnswerDistribution>> provinceMap) {
        //针对总访问量排序后的省市进行top10 以及other处理
        if(provinceRankList.size() < DEFAULT_TOP_FOR_TREND_REPORT) {
            // 进行补点
//                int pointNum = DEFAULT_TOP_FOR_TREND_REPORT - provinceRankList.size();
            for(String provinceName : provinceRankList) {
                //根据有序加入到结果集中
                provinceResultMap.put(provinceName, provinceMap.get(provinceName));
            }

//                for(int i = 0; pointNum > 0 && i < provinceNode.size(); i++) {
//                    if(pointNum == 1) {
//                        provinceResultMap.put("其他", Lists.newArrayList());
//                        pointNum--;
//                    } else {
//                        if(!provinceResultMap.containsKey(provinceNode.get(i))) {
//                            provinceResultMap.put(provinceNode.get(i), Lists.newArrayList());
//                            pointNum--;
//                        }
//                    }
//                }
        } else {
            // 如果top超过11个，则进行统计
            int pointNum = 0;
            List<AnswerDistribution> otherDistribution = Lists.newArrayList();
            for(String provinceName : provinceRankList) {
                if(pointNum < DEFAULT_TOP_FOR_TREND_REPORT) {
                    provinceResultMap.put(provinceName, provinceMap.get(provinceName));
                } else {
                    //其他进行汇总
                    otherDistribution.addAll(provinceMap.get(provinceName));
                }
                pointNum++;

            }
            provinceResultMap.put("其他", otherDistribution);
        }
    }

    /**
     * 以省市为单位进行汇总
     * @param answerDistributionList
     * @param provinceMap
     */
    private void collectAnswerDistributionGroupByProvince(List<AnswerDistribution> answerDistributionList, Map<String,
            List<AnswerDistribution>> provinceMap, List<String> provinceList) {
        List<AnswerDistribution> otherData = Lists.newArrayList();
        answerDistributionList.stream().forEach(answerDistribution -> {
            if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                if(provinceList.contains(answerDistribution.getProvince())) {
                    // 如果包含，按照省份进行汇总
                    if(provinceMap.containsKey(answerDistribution.getProvince())) {
                        provinceMap.get(answerDistribution.getProvince()).add(answerDistribution);
                    } else {
                        List<AnswerDistribution> dataList = Lists.newArrayList();
                        dataList.add(answerDistribution);
                        provinceMap.put(answerDistribution.getProvince(), dataList);
                    }
                } else {
                    otherData.add(answerDistribution);
                }
            }
        });
        if(CollUtil.isNotEmpty(otherData)) {
            provinceMap.put("其他", otherData);
        }
    }

    private AnswerDistributionBO checkFindTrendReportMethodParam(AnswerDistributionBO answerDistributionBO) {
        if(ObjectUtil.isEmpty(answerDistributionBO.getStartTime()) || ObjectUtil.isEmpty(answerDistributionBO.getEndTime())){
            answerDistributionBO.setStartTime(DateUtils.getLaterTimeByMin(new Date(), 10, DateUtils.DEFAULT_FMT));
            answerDistributionBO.setEndTime(DateUtils.formatDataToString(new Date(), DateUtils.DEFAULT_FMT));
        }
        if(ObjectUtil.isEmpty(answerDistributionBO.getQueryType())) {
            answerDistributionBO.setQueryType("1min");
        }
        if("0".equals(answerDistributionBO.getIspCode())) {
            answerDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getRankNumber()) && answerDistributionBO.getRankNumber().equals(0)) {
            answerDistributionBO.setRankNumber(null);
        }
        return answerDistributionBO;
    }

    private AnswerDistributionBO checkFindTrendDetailMethodParam(AnswerDistributionBO answerDistributionBO) {
        if(ObjectUtil.isEmpty(answerDistributionBO.getStartTime()) || ObjectUtil.isEmpty(answerDistributionBO.getEndTime())){
            answerDistributionBO.setStartTime(DateUtils.getLaterTimeByMin(new Date(), 10, DateUtils.DEFAULT_FMT));
            answerDistributionBO.setEndTime(DateUtils.formatDataToString(new Date(), DateUtils.DEFAULT_FMT));
        }
        if(ObjectUtil.isEmpty(answerDistributionBO.getQueryType())) {
            answerDistributionBO.setQueryType("1min");
        }
        if("0".equals(answerDistributionBO.getIspCode())) {
            answerDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getRankNumber()) && answerDistributionBO.getRankNumber().equals(0)) {
            answerDistributionBO.setRankNumber(null);
        }
        return answerDistributionBO;
    }

    public Map<String, Object> findTrendDetail(AnswerDistributionBO answerDistributionBO) {
        Map<String, Object> finalResult = Maps.newHashMap();
        //1.根据条件查询对应数据 2.以时间为节点进行统计《计算总解析数，》 以各省市为节点进行统计，计算占比
        answerDistributionBO = checkFindTrendDetailMethodParam(answerDistributionBO);
        Long total = calculateTotalDataNumAndResetSearchTime(answerDistributionBO);

        Map<Date, List<AnswerDistribution>> parseDataMap = Maps.newTreeMap();
        List dataList = Lists.newArrayList();

        if(isSearchProvinceInfo(answerDistributionBO.getProvince())) {
            if(ObjectUtil.equals(answerDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                total= Long.valueOf("1");
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceAll(answerDistributionBO);
                if(answerDistributionList!=null&&answerDistributionList.size()>0){
                    String time=answerDistributionBO.getStartTime() + "~" + answerDistributionBO.getEndTime();
                    BigInteger parseTotalCnt = BigInteger.ZERO;
                    for (AnswerDistribution answerDistribution : answerDistributionList) {
                        if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                            parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                        }
                    }
                    JSONObject dataObject = new JSONObject(new LinkedHashMap());
                    dataObject.put("时间", time);
                    dataObject.put("解析次数", parseTotalCnt);
                    for (AnswerDistribution answerDistribution : answerDistributionList) {
                        if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                            double parserRate = ReportUtils.buildRatioBase(answerDistribution.getParseTotalCnt(), parseTotalCnt);
                            dataObject.put(answerDistribution.getProvince(), answerDistribution.getParseTotalCnt() + StrUtil.LF + StrUtils.convertDoubleToPercent(parserRate, 2));
                        }
                    }
                    dataList.add(dataObject);
                }
            }else{
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndProvince(answerDistributionBO);
                // 按照时间进行汇总
                answerDistributionList.stream().forEach(answerDistribution -> {
                    if(parseDataMap.containsKey(answerDistribution.getParseTime())) {
                        parseDataMap.get(answerDistribution.getParseTime()).add(answerDistribution);
                    } else {
                        List<AnswerDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(answerDistribution);
                        parseDataMap.put(answerDistribution.getParseTime(), distributionTimeList);
                    }
                });
                // 按照省市进行区分
                for (Map.Entry<Date, List<AnswerDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    Date parseDate = parseDataEntrySet.getKey();
                    List<AnswerDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    Map<String, BigInteger> pointMap = Maps.newHashMap(); // 这一分钟里，按照省市进行统计
                    for(AnswerDistribution answerDistribution : valueData) {
                        String province = answerDistribution.getProvince();
                        if(StrUtil.isNotEmpty(province)) {
                            parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                            if(pointMap.containsKey(province)) {
                                pointMap.put(province, pointMap.get(province).add(answerDistribution.getParseTotalCnt()));
                            } else {
                                pointMap.put(province, answerDistribution.getParseTotalCnt());
                            }
                        }
                    }
                    // 是否需要对pointMap补点，不存在补0
//                provinceNode.stream().forEach(provinceNode -> {
//                    if(!pointMap.containsKey(provinceNode)) {
//                        pointMap.put(provinceNode, 0L);
//                    }
//                });
                    //组装数据
                    JSONObject dataObject = new JSONObject(new LinkedHashMap());
                    dataObject.put("时间", parseDate);
                    dataObject.put("解析次数", parseTotalCnt);
                    BigInteger provinceParseTotalCnt = parseTotalCnt;
                    pointMap.entrySet().forEach(pointMapEntrySet -> {
                        double parserRate = ReportUtils.buildRatioBase(pointMapEntrySet.getValue(), provinceParseTotalCnt);
                        dataObject.put(pointMapEntrySet.getKey(), pointMapEntrySet.getValue() + StrUtil.LF + StrUtils.convertDoubleToPercent(parserRate, 2));
                    });
                    dataList.add(dataObject);
                }
            }
        } else {
            if(answerDistributionBO.getProvinceFlag()==null ){
                answerDistributionBO.setProvince(super.getWholeProvinceName(answerDistributionBO.getProvince()));
            }
            if(ObjectUtil.equals(answerDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                total= Long.valueOf("1");
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndCityAll(answerDistributionBO);
                if(answerDistributionList!=null&&answerDistributionList.size()>0){
                    String time=answerDistributionBO.getStartTime() + "~" + answerDistributionBO.getEndTime();
                    BigInteger parseTotalCnt = BigInteger.ZERO;
                    for (AnswerDistribution answerDistribution : answerDistributionList) {
                        if(StrUtil.isNotEmpty(answerDistribution.getCity())) {
                            parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                        }
                    }
                    JSONObject dataObject = new JSONObject(new LinkedHashMap());
                    dataObject.put("时间", time);
                    dataObject.put("解析次数", parseTotalCnt);
                    for (AnswerDistribution answerDistribution : answerDistributionList) {
                        if(StrUtil.isNotEmpty(answerDistribution.getCity())) {
                            double parserRate = ReportUtils.buildRatioBase(answerDistribution.getParseTotalCnt(), parseTotalCnt);
                            dataObject.put(answerDistribution.getCity(), answerDistribution.getParseTotalCnt() + StrUtil.LF + StrUtils.convertDoubleToPercent(parserRate, 2));
                        }
                    }
                    dataList.add(dataObject);
                }
            }else{
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndCity(answerDistributionBO);
                // 按照时间进行汇总
                answerDistributionList.stream().forEach(answerDistribution -> {
                    if(parseDataMap.containsKey(answerDistribution.getParseTime())) {
                        parseDataMap.get(answerDistribution.getParseTime()).add(answerDistribution);
                    } else {
                        List<AnswerDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(answerDistribution);
                        parseDataMap.put(answerDistribution.getParseTime(), distributionTimeList);
                    }
                });
                // 按照区域进行汇总
                for (Map.Entry<Date, List<AnswerDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    Date parseDate = parseDataEntrySet.getKey();
                    List<AnswerDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    Map<String, BigInteger> pointMap = Maps.newHashMap(); // 这一分钟里，按照省市进行统计
                    for(AnswerDistribution answerDistribution : valueData) {
                        String city = answerDistribution.getCity();
                        if(StrUtil.isNotEmpty(city)) {
                            parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                            if(pointMap.containsKey(city)) {
                                pointMap.put(city, pointMap.get(city).add(answerDistribution.getParseTotalCnt()));
                            } else {
                                pointMap.put(city, answerDistribution.getParseTotalCnt());
                            }
                        }
                    }
                    //组装数据
                    JSONObject dataObject = new JSONObject(new LinkedHashMap());
                    dataObject.put("时间", parseDate);
                    dataObject.put("解析次数", parseTotalCnt);
                    BigInteger provinceParseTotalCnt = parseTotalCnt;
                    pointMap.entrySet().forEach(pointMapEntrySet -> {
                        double parserRate = ReportUtils.buildRatioBase(pointMapEntrySet.getValue(), provinceParseTotalCnt);
                        dataObject.put(pointMapEntrySet.getKey(), pointMapEntrySet.getValue() + StrUtil.LF + StrUtils.convertDoubleToPercent(parserRate, 2));
                    });
                    dataList.add(dataObject);
                }
            }
        }
        finalResult.put("total", total);
        finalResult.put("data", dataList);
        return finalResult;
    }

    public void downloadDetail(AnswerDistributionBO answerDistributionBO, HttpServletResponse response) throws Exception {

        //1.根据条件查询对应数据 2.以时间为节点进行统计《计算总解析数，》 以各省市为节点进行统计，计算占比
        checkDownloadDetailMethodParam(answerDistributionBO);
        answerDistributionBO.setLimit(10000L);
        Map<Date, List<AnswerDistribution>> parseDataMap = Maps.newTreeMap();
        List<AnswerDistributionBO> dataList = Lists.newArrayList();

        if(isSearchProvinceInfo(answerDistributionBO.getProvince())) {
            if(ObjectUtil.equals(answerDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceLimitNumAll(answerDistributionBO);
                String time=answerDistributionBO.getStartTime() + "~" + answerDistributionBO.getEndTime();
                BigInteger parseTotalCnt = BigInteger.ZERO;
                for (AnswerDistribution answerDistribution : answerDistributionList) {
                    if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                        parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                    }
                }
                for (AnswerDistribution answerDistribution : answerDistributionList) {
                    if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                        AnswerDistributionBO distributionBO = new AnswerDistributionBO();
                        distributionBO.setTimeRange(time);
                        distributionBO.setProvince(answerDistribution.getProvince());
                        distributionBO.setParseTotalCnt(answerDistribution.getParseTotalCnt());
                        distributionBO.setRate(ReportUtils.buildRatioBase(answerDistribution.getParseTotalCnt(), parseTotalCnt));
                        dataList.add(distributionBO);
                    }
                }
            }else{
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceLimitNum(answerDistributionBO);
                // 按照时间进行汇总
                answerDistributionList.stream().forEach(answerDistribution -> {
                    if (parseDataMap.containsKey(answerDistribution.getParseTime())) {
                        parseDataMap.get(answerDistribution.getParseTime()).add(answerDistribution);
                    } else {
                        List<AnswerDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(answerDistribution);
                        parseDataMap.put(answerDistribution.getParseTime(), distributionTimeList);
                    }
                });
                for (Map.Entry<Date, List<AnswerDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    List<AnswerDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    for (AnswerDistribution answerDistribution : valueData) {
                        if (StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                            parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                        }
                    }
                    for (AnswerDistribution answerDistribution : valueData) {
                        if (StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                            AnswerDistributionBO distributionBO = new AnswerDistributionBO();
                            distributionBO.setTimeRange(DateUtils.formatDataToString(answerDistribution.getParseTime(), DateUtils.DEFAULT_FMT));
                            distributionBO.setProvince(answerDistribution.getProvince());
                            distributionBO.setParseTotalCnt(answerDistribution.getParseTotalCnt());
                            distributionBO.setRate(ReportUtils.buildRatioBase(answerDistribution.getParseTotalCnt(), parseTotalCnt));
                            dataList.add(distributionBO);
                        }
                    }
                }
            }
        } else {
            if(answerDistributionBO.getProvinceFlag()==null ){
                answerDistributionBO.setProvince(super.getWholeProvinceName(answerDistributionBO.getProvince()));
            }
            if(ObjectUtil.equals(answerDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndCityLimitNumAll(answerDistributionBO);
                String time=answerDistributionBO.getStartTime() + "~" + answerDistributionBO.getEndTime();
                BigInteger parseTotalCnt = BigInteger.ZERO;
                for (AnswerDistribution answerDistribution : answerDistributionList) {
                    if(StrUtil.isNotEmpty(answerDistribution.getCity())) {
                        parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                    }
                }
                for (AnswerDistribution answerDistribution : answerDistributionList) {
                    if(StrUtil.isNotEmpty(answerDistribution.getCity())) {
                        AnswerDistributionBO distributionBO = new AnswerDistributionBO();
                        distributionBO.setTimeRange(time);
                        distributionBO.setProvince(answerDistribution.getCity());
                        distributionBO.setParseTotalCnt(answerDistribution.getParseTotalCnt());
                        distributionBO.setRate(ReportUtils.buildRatioBase(answerDistribution.getParseTotalCnt(), parseTotalCnt));
                        dataList.add(distributionBO);
                    }
                }
            }else{
                List<AnswerDistribution> answerDistributionList = answerDistributionMapper.findBySelectiveGroupByParseTimeAndCityLimitNum(answerDistributionBO);
                // 按照时间进行汇总
                answerDistributionList.stream().forEach(answerDistribution -> {
                    if(parseDataMap.containsKey(answerDistribution.getParseTime())) {
                        parseDataMap.get(answerDistribution.getParseTime()).add(answerDistribution);
                    } else {
                        List<AnswerDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(answerDistribution);
                        parseDataMap.put(answerDistribution.getParseTime(), distributionTimeList);
                    }
                });
                for (Map.Entry<Date, List<AnswerDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    List<AnswerDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    for (AnswerDistribution answerDistribution : valueData) {
                        if (StrUtil.isNotEmpty(answerDistribution.getCity())) {
                            parseTotalCnt = parseTotalCnt.add(answerDistribution.getParseTotalCnt());
                        }
                    }
                    for (AnswerDistribution answerDistribution : valueData) {
                        if (StrUtil.isNotEmpty(answerDistribution.getCity())) {
                            AnswerDistributionBO distributionBO = new AnswerDistributionBO();
                            distributionBO.setTimeRange(DateUtils.formatDataToString(answerDistribution.getParseTime(), DateUtils.DEFAULT_FMT));
                            distributionBO.setProvince(answerDistribution.getCity());
                            distributionBO.setParseTotalCnt(answerDistribution.getParseTotalCnt());
                            distributionBO.setRate(ReportUtils.buildRatioBase(answerDistribution.getParseTotalCnt(), parseTotalCnt));
                            dataList.add(distributionBO);
                        }
                    }
                }
            }
        }
        List<String> csvLines = dataList.stream().map(AnswerDistributionBO::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(AnswerDistributionBO.CSV_NAME, AnswerDistributionBO.CSV_HEAD, csvLines, response);
    }

    private void checkDownloadDetailMethodParam(AnswerDistributionBO answerDistributionBO) {
        if(ObjectUtil.isEmpty(answerDistributionBO.getStartTime()) || ObjectUtil.isEmpty(answerDistributionBO.getEndTime())){
            answerDistributionBO.setStartTime(DateUtils.getLaterTimeByMin(new Date(), 10, DateUtils.DEFAULT_FMT));
            answerDistributionBO.setEndTime(DateUtils.formatDataToString(new Date(), DateUtils.DEFAULT_FMT));
        }
        if(ObjectUtil.isEmpty(answerDistributionBO.getQueryType())) {
            answerDistributionBO.setQueryType("1min");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getIspCode()) && answerDistributionBO.getIspCode().equals("0")) {
            answerDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(answerDistributionBO.getRankNumber()) && answerDistributionBO.getRankNumber().equals(0)) {
            answerDistributionBO.setRankNumber(null);
        }
    }

    public List<String> findTrendProvinceByNum(AnswerDistributionBO answerDistributionBO, int distributeNum) {
        //1.根据条件查询对应数据 2.以时间为节点进行统计《计算总解析数，》 以各省市为节点进行统计，计算占比
        List<String> distributeList = Lists.newArrayList();
        answerDistributionBO = checkFindTrendDetailMethodParam(answerDistributionBO);
        List<AnswerDistribution> answerDistributionData = answerDistributionMapper.findInfoGroupByProvince(answerDistributionBO);
        Map<String, AnswerDistribution> resultMap = Maps.newHashMap();
        answerDistributionData.stream().forEach(answerDistribution -> {
            if(StrUtil.isNotEmpty(answerDistribution.getProvince())) {
                AnswerDistribution distribution = null;
                if(resultMap.containsKey(answerDistribution.getProvince())) {
                    distribution = resultMap.get(answerDistribution.getProvince());
                    BigInteger parseTotalCnt = distribution.getParseTotalCnt();
                    distribution.setParseTotalCnt(parseTotalCnt.add(answerDistribution.getParseTotalCnt()));
                } else {
                    distribution = new AnswerDistribution();
                    distribution.setProvince(answerDistribution.getProvince());
                    distribution.setParseTotalCnt(answerDistribution.getParseTotalCnt());
                }
                resultMap.put(answerDistribution.getProvince(), distribution);
            }
        });

        List<AnswerDistribution> answerDistributionList = Lists.newArrayList();
        if(CollUtil.isNotEmpty(resultMap)) {
            answerDistributionList = new ArrayList<>(resultMap.values());
        }

        answerDistributionList = answerDistributionList.stream().sorted(new Comparator<AnswerDistribution>() {
            @Override
            public int compare(AnswerDistribution o1, AnswerDistribution o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());

        answerDistributionList.stream().forEach(answerDistribution -> {
            distributeList.add(answerDistribution.getProvince());
        });
        return distributeList.subList(0, distributeNum > distributeList.size() ? distributeList.size() : distributeNum);
    }

    public JSONObject findProvinceResourceMap(AnswerDistributionBO answerDistributionBO) {
        answerDistributionBO = checkFindProvinceMapDataListMethodParam(answerDistributionBO);
        List<AnswerDistribution> distributionList = answerDistributionMapper.findGroupByProvinceAndIsp(answerDistributionBO);
        List mapDataList = statisticsProvinceData(distributionList);
        List ispCntDataList = statisticsProvinceIspCntData(distributionList);
        List ispDataList = statisticsProvinceIspData(distributionList);
        JSONObject finalResult = new JSONObject();
        finalResult.put("data", mapDataList);
        finalResult.put("ispData", ispCntDataList);
        finalResult.put("lineData", ispDataList);
        return finalResult;
    }

    private List statisticsProvinceData(List<AnswerDistribution> distributionList) {
        List mapDataList = statisticsProvinceDomainParseCount(distributionList, true);
        return convertMapDataToJsonResult(mapDataList);
    }

    private List statisticsProvinceIspData(List<AnswerDistribution> distributionList) {
        List mapDataVOList = Lists.newArrayList();
        Map<String, List<AnswerDistribution>> ispMap = Maps.newHashMap();
        distributionList.stream().forEach(answerDistribution -> {
            String isp = answerDistribution.getIsp();
            if(ispMap.containsKey(isp)) {
                ispMap.get(isp).add(answerDistribution);
            } else {
                List<AnswerDistribution> answerDistributionList = Lists.newArrayList();
                answerDistributionList.add(answerDistribution);
                ispMap.put(isp, answerDistributionList);
            }
        });

        ispMap.entrySet().forEach(provinceMapEntry -> {
            JSONObject jsonObject = new JSONObject();
            List ispDataList = Lists.newArrayList();
            provinceMapEntry.getValue().stream().forEach(answerDistribution -> {
                JSONObject ispData = new JSONObject();
                String shortDistrict = super.provinceAbbrMap.get(answerDistribution.getProvince());
                ispData.put("name", StrUtil.isNotEmpty(shortDistrict) ? shortDistrict : answerDistribution.getProvince());
                ispData.put("value", answerDistribution.getParseTotalCnt());
                ispDataList.add(ispData);
            });
            jsonObject.put(provinceMapEntry.getKey(), ispDataList);
            mapDataVOList.add(jsonObject);
        });
        return mapDataVOList;
    }

    private List statisticsProvinceIspCntData(List<AnswerDistribution> distributionList) {
        List mapDataVOList = Lists.newArrayList();
        Map<String, BigInteger> ispMap = Maps.newHashMap();
        distributionList.stream().forEach(answerDistribution -> {
            BigInteger parseTotal = answerDistribution.getParseTotalCnt(); // 北京市&1_440404
            String isp = answerDistribution.getIsp();
            if(ispMap.containsKey(isp)) {
                parseTotal = parseTotal.add(ispMap.get(isp));
            }
            ispMap.put(isp, parseTotal);
        });

        ispMap.entrySet().forEach(provinceMapEntry -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(provinceMapEntry.getKey(), provinceMapEntry.getValue());
            mapDataVOList.add(jsonObject);
        });
        return mapDataVOList;
    }
}
