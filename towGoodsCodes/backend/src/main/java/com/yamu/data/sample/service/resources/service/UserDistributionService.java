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
import com.yamu.data.sample.service.resources.entity.bo.UserDistributionBO;
import com.yamu.data.sample.service.resources.entity.enumerate.StatisticsWayEnum;
import com.yamu.data.sample.service.resources.entity.po.UserDistribution;
import com.yamu.data.sample.service.resources.entity.vo.UserDistributionMapDataVO;
import com.yamu.data.sample.service.resources.entity.vo.UserDistributionRateVO;
import com.yamu.data.sample.service.resources.mapper.UserDistributionMapper;
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
public class UserDistributionService {

    @Autowired
    private UserDistributionMapper userDistributionMapper;

    /**
     * 静态省节点信息，如果切换为双数据库，则可以替换
     */
    private static List<String> provinceNode = Arrays.asList("新疆维吾尔自治区", "青海省", "湖北省", "香港特别行政区", "山西省", "云南省", "河北省", "广西壮族自治区",
            "海南省", "上海市", "辽宁省", "澳门特别行政", "福建省", "陕西省", "四川省", "贵州省", "广东省", "北京市", "江苏省", "黑龙江省", "天津市", "重庆市", "山东省",
            "内蒙古自治区", "宁夏回族自治区", "浙江省", "台湾省", "西藏自治区", "吉林省", "安徽省", "江西省", "甘肃省", "河南省", "湖南省");

    private static final String ORDER_BY_PARSE_TIME = "parse_time";

    private static final DecimalFormat decimalFormatForTwo = new DecimalFormat("0.00");

    private final static String PROVINCE_CODE_END_STRING = "0000";

    private final static int DEFAULT_TOP_FOR_TREND_REPORT = 10;

    private final static String DEFAULT_CITY = "辖区";

    /**
     * 查找省市地图
     * @param userDistributionBO
     * @return
     */
    public List findProvinceMapDataList(UserDistributionBO userDistributionBO) {
        //根据时间参数进行查询，然后进行汇总，返回前端
        //1.查询，用map进行统计，然后返回
        userDistributionBO = checkFindProvinceMapDataListMethodParam(userDistributionBO);
        List resultList = Lists.newArrayList();
        List<UserDistributionMapDataVO> distributionMapDataVOList = Lists.newArrayList();
        if(isSearchProvinceInfo(userDistributionBO.getProvince())) {
            // 如果是省市为单位进行统计，则根据province 进行汇总，如果以区为单位，则不需要汇总
            List<UserDistribution> distributionList = userDistributionMapper.findInfoGroupByProvince(userDistributionBO);
            distributionMapDataVOList = statisticsProvinceDomainParseCount(distributionList, true);
        } else {
            List<UserDistribution> distributionList = userDistributionMapper.findInfoGroupByCity(userDistributionBO);
            distributionMapDataVOList = statisticsDistributionDomainParseCount(distributionList);
        }
        resultList = convertMapDataToJsonResult(distributionMapDataVOList);
        return resultList;
    }

    private UserDistributionBO checkFindProvinceMapDataListMethodParam(UserDistributionBO userDistributionBO) {
        // 运营商ispCode 0 默认为全国
        if("0".equals(userDistributionBO.getIspCode())) {
            userDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getRankNumber()) && userDistributionBO.getRankNumber().equals(0)) {
            userDistributionBO.setRankNumber(null);
        }
        return userDistributionBO;
    }

    private List convertMapDataToJsonResult(List<UserDistributionMapDataVO> distributionMapDataVOList) {
        List resultList = Lists.newArrayList();
        for(int index = 0; index < distributionMapDataVOList.size(); index++) {
            UserDistributionMapDataVO mapDataVO = distributionMapDataVOList.get(index);
            long rankNumber = index + 1;
            JSONObject dataResult = new JSONObject(new LinkedHashMap());
            dataResult.put("name", mapDataVO.getDistribution());
            dataResult.put("district", mapDataVO.getDistribution());
            dataResult.put("value", Arrays.asList(mapDataVO.getParseTotalCnt(), rankNumber));
            resultList.add(dataResult);
        }
        return resultList;
    }

    private List<UserDistribution> sortUserDistributionList(List<UserDistribution> distributionList){
        if(CollUtil.isNotEmpty(distributionList)) {
            //进行排序
            distributionList.stream().sorted(new Comparator<UserDistribution>() {
                @Override
                public int compare(UserDistribution o1, UserDistribution o2) {
                    return o1.getParseTotalCnt().compareTo(o2.getParseTotalCnt());
                }
            });
        }
        return distributionList;
    }

    private List<UserDistributionMapDataVO> sortUserDistributionMapDataList(List<UserDistributionMapDataVO> distributionMapList){
        if(CollUtil.isNotEmpty(distributionMapList)) {
            //进行排序
            distributionMapList.stream().sorted(new Comparator<UserDistributionMapDataVO>() {
                @Override
                public int compare(UserDistributionMapDataVO o1, UserDistributionMapDataVO o2) {
                    return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
                }
            });
        }
        return distributionMapList;
    }

    /**
     * 是否搜索province信息，如果province信息不为空，则搜索的是省份下面地市信息
     * @param province
     * @return
     */
    private boolean isSearchProvinceInfo(String province) {
        if(ObjectUtil.isEmpty(province)) {
            return true;
        }
        return false;
    }

    /**
     * 统计省份地区解析数
     * @param distributionList
     * @return
     */
    private List<UserDistributionMapDataVO> statisticsDistributionDomainParseCount(List<UserDistribution> distributionList) {
        List<UserDistributionMapDataVO> mapDataVOList = Lists.newArrayList();
        Map<String, BigInteger> areaMap = Maps.newHashMap();
        distributionList.stream().forEach(userDistribution -> {
            BigInteger parseTotal = userDistribution.getParseTotalCnt();
            if(DEFAULT_CITY.equals(userDistribution.getCity())) {
                if(areaMap.containsKey(userDistribution.getDistrict())) {
                    parseTotal = parseTotal.add(areaMap.get(userDistribution.getDistrict()));
                }
                areaMap.put(userDistribution.getDistrict(), parseTotal);
            } else {
                if(areaMap.containsKey(userDistribution.getCity())) {
                    parseTotal = parseTotal.add(areaMap.get(userDistribution.getCity()));
                }
                areaMap.put(userDistribution.getCity(), parseTotal);
            }
        });

        // 区域数据暂时没法补点
        areaMap.entrySet().forEach(areaMapEntry -> {
            if(StrUtil.isNotEmpty(areaMapEntry.getKey())){
                UserDistributionMapDataVO userDistributionMapDataVO = new UserDistributionMapDataVO();
                userDistributionMapDataVO.setParseTotalCnt(areaMapEntry.getValue());
                userDistributionMapDataVO.setDistribution(areaMapEntry.getKey());
                mapDataVOList.add(userDistributionMapDataVO);
            }
        });

        //进行排序
        List<UserDistributionMapDataVO> sortList = mapDataVOList.stream().sorted(new Comparator<UserDistributionMapDataVO>() {
            @Override
            public int compare(UserDistributionMapDataVO o1, UserDistributionMapDataVO o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());
        return sortList;
    }

    /**
     * 统计省份地区域名解析数
     * @param distributionList
     * @return
     */
    private List<UserDistributionMapDataVO> statisticsProvinceDomainParseCount(List<UserDistribution> distributionList, boolean isAddZero) {
        List<UserDistributionMapDataVO> mapDataVOList = Lists.newArrayList();
        Map<String, BigInteger> provinceMap = Maps.newHashMap();
        distributionList.stream().forEach(userDistribution -> {
            if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                BigInteger parseTotal = userDistribution.getParseTotalCnt();
                String province = userDistribution.getProvince();
                if(provinceMap.containsKey(province)) {
                    parseTotal = parseTotal.add(provinceMap.get(province));
                }
                provinceMap.put(province, parseTotal);
            }
        });

        provinceMap.entrySet().forEach(provinceMapEntry -> {
            if(StrUtil.isNotEmpty(provinceMapEntry.getKey())) {
                UserDistributionMapDataVO userDistributionMapDataVO = new UserDistributionMapDataVO();
                userDistributionMapDataVO.setParseTotalCnt(provinceMapEntry.getValue());
                userDistributionMapDataVO.setDistribution(provinceMapEntry.getKey());
                mapDataVOList.add(userDistributionMapDataVO);
            }
        });

        if(isAddZero) { // 地图不需要补零，其余进行补零操作
            List<String> existProvince = Lists.newArrayList();
            existProvince = provinceMap.keySet().stream().collect(Collectors.toList());
            for (String province : provinceNode) {
                if(!existProvince.contains(provinceNode)) {
                    UserDistributionMapDataVO userDistributionMapDataVO = new UserDistributionMapDataVO();
                    userDistributionMapDataVO.setParseTotalCnt(BigInteger.ZERO);
                    userDistributionMapDataVO.setDistribution(province);
                    mapDataVOList.add(userDistributionMapDataVO);
                }
            }
        }

        //进行排序
        List<UserDistributionMapDataVO> sortList = mapDataVOList.stream().sorted(new Comparator<UserDistributionMapDataVO>() {
            @Override
            public int compare(UserDistributionMapDataVO o1, UserDistributionMapDataVO o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());
        return sortList;
    }

    public JSONObject findProvinceRank(UserDistributionBO userDistributionBO) {
        //根据时间参数进行查询，然后进行汇总，返回前端
        //1.查询，用map进行统计，然后返回
        userDistributionBO = checkFindProvinceRankMethodParam(userDistributionBO);
        List<String> distributionList = Lists.newArrayList();
        List<BigInteger> parseTotalList = Lists.newArrayList();
        List<UserDistributionMapDataVO> distributionMapDataVOList = Lists.newArrayList();
        if(isSearchProvinceInfo(userDistributionBO.getProvince())) {
            List<UserDistribution> userDistributionList = userDistributionMapper.findInfoGroupByProvince(userDistributionBO);
            distributionMapDataVOList = statisticsProvinceDomainParseCount(userDistributionList, false);
        } else {
            List<UserDistribution> userDistributionList = userDistributionMapper.findInfoGroupByCity(userDistributionBO);
            distributionMapDataVOList = statisticsDistributionDomainParseCount(userDistributionList);
        }
        distributionMapDataVOList.stream().forEach(distributionMapDataVO -> {
            distributionList.add(distributionMapDataVO.getDistribution());
            parseTotalList.add(distributionMapDataVO.getParseTotalCnt());
        });
        JSONObject finalResult = ReportUtils.buildRankReportWithParam("用户分布排名", distributionList, parseTotalList, 7);
        return finalResult;
    }

    private UserDistributionBO checkFindProvinceRankMethodParam(UserDistributionBO userDistributionBO) {
        if("0".equals(userDistributionBO.getIspCode())) {
            userDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getRankNumber()) && userDistributionBO.getRankNumber().equals(0)) {
            userDistributionBO.setRankNumber(null);
        }
        return userDistributionBO;
    }

    public JSONObject findProvinceRate(UserDistributionBO userDistributionBO) {
        //根据时间参数进行查询，然后进行汇总，返回前端
        userDistributionBO = checkFindProvinceRateMethodParam(userDistributionBO);
        List<UserDistributionRateVO> rateVOList = Lists.newArrayList();
        List<UserDistributionRateVO> finalDataResult = Lists.newArrayList();
        List<UserDistributionMapDataVO> distributionMapDataVOList = Lists.newArrayList();
        String reportName = "";
        if(isSearchProvinceInfo(userDistributionBO.getProvince())) {
            //1.查询，用map进行统计，然后返回
            List<UserDistribution> userDistributionList = userDistributionMapper.findInfoGroupByProvince(userDistributionBO);
            distributionMapDataVOList = statisticsProvinceDomainParseCount(userDistributionList, false);
            reportName = "各省用户占比";
        } else {
            List<UserDistribution> userDistributionList = userDistributionMapper.findInfoGroupByCity(userDistributionBO);
            distributionMapDataVOList = statisticsDistributionDomainParseCount(userDistributionList);
            reportName = "各地市用户占比";
        }

        for (UserDistributionMapDataVO distributionMapDataVO : distributionMapDataVOList) {
            UserDistributionRateVO rateVO = new UserDistributionRateVO();
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

    private void statisticsUserDistributionRateListForOtherOrAll(List<UserDistributionRateVO> rateVOList, List<UserDistributionRateVO> finalDataResult) {
        if(rateVOList.size() > 0) {
            BigInteger totalParseCnt = BigInteger.ZERO;
            for(int i = 0; i < rateVOList.size(); i++) {
                if(i >= DEFAULT_TOP_FOR_TREND_REPORT) {
                    totalParseCnt = totalParseCnt.add(rateVOList.get(i).getValue());
                } else {
                    finalDataResult.add(rateVOList.get(i));
                }
            }
            finalDataResult.add(new UserDistributionRateVO("其他", totalParseCnt));
        } else {
            //针对没有数据处理
            finalDataResult.add(new UserDistributionRateVO("全部", BigInteger.ZERO));
        }
    }

    private List<UserDistributionRateVO> sortUserDistributionRateVOByDesc(List<UserDistributionRateVO> rateVOList) {
         rateVOList.stream().sorted(new Comparator<UserDistributionRateVO>() {
            @Override
            public int compare(UserDistributionRateVO o1, UserDistributionRateVO o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return rateVOList;
    }

    private UserDistributionBO checkFindProvinceRateMethodParam(UserDistributionBO userDistributionBO) {
        if("0".equals(userDistributionBO.getIspCode())) {
            userDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getRankNumber()) && userDistributionBO.getRankNumber().equals(0)) {
            userDistributionBO.setRankNumber(null);
        }
        return userDistributionBO;
    }

    public JSONObject findTrendReport(UserDistributionBO userDistributionBO) {
        List<String> provinceList = findTrendDistributeByNum(userDistributionBO, DEFAULT_TOP_FOR_TREND_REPORT);
        userDistributionBO = checkFindTrendReportMethodParam(userDistributionBO);
        calculateTotalDataNumAndResetSearchTime(userDistributionBO);
        Set<Date> aliveDateList = Sets.newTreeSet();
        JSONObject dataIndexJson = new JSONObject(new LinkedHashMap());
        JSONObject finalResult = new JSONObject(new LinkedHashMap());
        Map<String, List<UserDistribution>> provinceMap = Maps.newLinkedHashMap();
        Map<String, List<UserDistribution>> provinceResultMap = Maps.newLinkedHashMap();
        Map<String, BigInteger> provinceParseCntMap = Maps.newHashMap();
        Map<Date, String> xAxisMap = Maps.newHashMap();
        String reportName = "";
        if(isSearchProvinceInfo(userDistributionBO.getProvince())) {
            List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndProvince(userDistributionBO);
            // 生成X轴坐标时间
            xAxisMap = ReportUtils.buildXaxisMap(userDistributionBO.getStartTime(), userDistributionBO.getEndTime(), userDistributionBO.getQueryType());
            collectProvinceReportData(userDistributionList, xAxisMap, aliveDateList, dataIndexJson, provinceMap, userDistributionBO, provinceList);
            reportName = "各省活跃用户解析量趋势分析";
        } else{
            List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndCity(userDistributionBO);
            //userDistributionBO.setOrderBy(ORDER_BY_PARSE_TIME);
            // 生成X轴坐标时间
            xAxisMap = ReportUtils.buildXaxisMap(userDistributionBO.getStartTime(), userDistributionBO.getEndTime(), userDistributionBO.getQueryType());
            collectDistributionReportData(userDistributionList, xAxisMap, aliveDateList, dataIndexJson, provinceMap, provinceResultMap, provinceParseCntMap, userDistributionBO);
            reportName = "各地市活跃用户解析量趋势分析";
        }
        xAxisMap = removeXAxisNullPointData(xAxisMap, aliveDateList);
        // 进行分页，如何分页？
        //List xAxisMapVal = xAxisMap.values().stream().collect(Collectors.toList());
        finalResult.put("data", dataIndexJson);
        finalResult.put("xAxis", xAxisMap.values());
        finalResult.put("name", reportName);
        return finalResult;
    }

    private void collectDistributionReportData(List<UserDistribution> userDistributionList, Map<Date, String> xAxisMap, Set<Date> aliveDateList, JSONObject dataIndexJson, Map<String, List<UserDistribution>> provinceMap, Map<String, List<UserDistribution>> provinceResultMap, Map<String, BigInteger> provinceParseCntMap, UserDistributionBO userDistributionBO) {
        List<String> provinceRankList = new LinkedList<>();
        //以城市为单位进行汇总
        collectUserDistributionGroupByDistribution(userDistributionList, provinceMap);
        // 对总体的访问量进行排名
        for (Map.Entry<String, List<UserDistribution>> provinceResultEntity : provinceMap.entrySet()) {
            List<UserDistribution> dataList = provinceResultEntity.getValue();
            BigInteger totalParseCnt = BigInteger.ZERO;
            for (UserDistribution userDistribution : dataList) {
                totalParseCnt = userDistribution.getParseTotalCnt().add(totalParseCnt);
            }
            provinceParseCntMap.put(provinceResultEntity.getKey(), totalParseCnt);
        }
        //针对访问量进行排序处理
        provinceParseCntMap.entrySet().stream().sorted(Map.Entry.<String, BigInteger>comparingByValue().reversed()).
                forEachOrdered(object -> provinceRankList.add(object.getKey()));

        // 对排名进行topN或者topOther进行统计
        collectDistributionRankByTop10AndOther(provinceRankList, provinceResultMap, provinceMap);

        provinceResultMap.values().stream().forEach(provinceList ->{
            if (provinceList.size()!=0){
                provinceList.stream().forEach(item->aliveDateList.add(item.getParseTime()));
            }
        });
        collectDistributionListByParseTime(provinceResultMap, xAxisMap, aliveDateList, dataIndexJson, userDistributionBO);

    }

    private void collectDistributionListByParseTime(Map<String, List<UserDistribution>> provinceResultMap, Map<Date, String> xAxisMap, Set<Date> aliveDateList, JSONObject dataIndexJson, UserDistributionBO userDistributionBO) {
        int index = 0;
        for (Map.Entry<String, List<UserDistribution>> entrySet : provinceResultMap.entrySet()) {
            String distributionName = entrySet.getKey();
            // 现在获取到是以市为单位的数据
            List<UserDistribution> dataList = entrySet.getValue();
            Map<Date, BigInteger> parseDataMap = Maps.newTreeMap();
            // 对省市为单位的数据，再根据解析时间进行封装
            dataList.stream().forEach(userDistribution -> {
//                // 根据时间进行汇总
//                long parseTotalCnt = 0;
//                if(parseDataMap.containsKey(userDistribution.getParseTime())) {
//                    parseTotalCnt = parseTotalCnt + userDistribution.getParseTotalCnt();
//                } else {
//                    parseTotalCnt = userDistribution.getParseTotalCnt();
//                }
                if(ObjectUtil.isEmpty(parseDataMap.get(userDistribution.getParseTime()))) {
                    parseDataMap.put(userDistribution.getParseTime(), userDistribution.getParseTotalCnt());
                } else {
                    BigInteger parseTotal = parseDataMap.get(userDistribution.getParseTime());
                    parseDataMap.put(userDistribution.getParseTime(), userDistribution.getParseTotalCnt().add(parseTotal));
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
            //totalResult = PageUtils.subListByPage(totalResult, userDistributionBO.getOffset(), userDistributionBO.getLimit());
            JSONObject jsonData = new JSONObject(new LinkedHashMap());
            JSONObject resultData = new JSONObject(new LinkedHashMap());
            jsonData.put("data", totalResult);
            jsonData.put("name", distributionName);
            resultData.put("result", jsonData);
            dataIndexJson.put("data" + (++index), resultData);
        }
    }

    private void collectDistributionRankByTop10AndOther(List<String> provinceRankList, Map<String, List<UserDistribution>> provinceResultMap, Map<String, List<UserDistribution>> provinceMap) {
        //针对总访问量排序后的省市进行top10 以及other处理
        if(provinceRankList.size() < DEFAULT_TOP_FOR_TREND_REPORT) {
            // 进行补点
            for(String provinceName : provinceRankList) {
                //根据有序加入到结果集中
                provinceResultMap.put(provinceName, provinceMap.get(provinceName));
            }
        } else {
            // 如果top超过11个，则进行统计
            int pointNum = 0;
            List<UserDistribution> otherDistribution = Lists.newArrayList();
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
     * 以城市为单位进行汇总
     * @param userDistributionList
     * @param provinceMap
     */
    private void collectUserDistributionGroupByDistribution(List<UserDistribution> userDistributionList, Map<String, List<UserDistribution>> provinceMap) {
        userDistributionList.stream().forEach(userDistribution -> {
            if(StrUtil.isNotEmpty(userDistribution.getCity())) {
//                if(DEFAULT_CITY.equals(userDistribution.getCity())) {
//                    if(provinceMap.containsKey(userDistribution.getDistrict())) {
//                        provinceMap.get(userDistribution.getDistrict()).add(userDistribution);
//                    }else {
//                        List<UserDistribution> dataList = Lists.newArrayList();
//                        dataList.add(userDistribution);
//                        provinceMap.put(userDistribution.getDistrict(), dataList);
//                    }
//                } else {
                if(provinceMap.containsKey(userDistribution.getCity())) {
                    provinceMap.get(userDistribution.getCity()).add(userDistribution);
                } else {
                    List<UserDistribution> dataList = Lists.newArrayList();
                    dataList.add(userDistribution);
                    provinceMap.put(userDistribution.getCity(), dataList);
                }
//                }
            }
        });
    }

    private void collectProvinceReportData(List<UserDistribution> userDistributionList, Map<Date, String> xAxisMap,
                                           Set<Date> aliveDateList, JSONObject dataIndexJson, Map<String, List<UserDistribution>> provinceMap,
                                           UserDistributionBO userDistributionBO, List<String> provinceList) {
        // 全国节点或者运营商节点进行汇总
        //1. 先以省市为单位，进行汇总，2.在每个省市基础上，按照parseTime进行汇总
        List<String> provinceRankList = new LinkedList<>();
        // 以省市为单位进行汇总
        collectUserDistributionGroupByProvince(userDistributionList, provinceMap, provinceList);

        provinceMap.values().stream().forEach(distributeList ->{
            if (distributeList.size() != 0){
                distributeList.stream().forEach(item->aliveDateList.add(item.getParseTime()));
            }
        });
        // 再以时间为单位进行汇总
        collectProvinceListByParseTime(provinceMap, dataIndexJson, xAxisMap, aliveDateList, userDistributionBO);
    }

    private void collectProvinceListByParseTime(Map<String, List<UserDistribution>> provinceResultMap, JSONObject dataIndexJson, Map<Date, String> xAxisMap, Set<Date> aliveDateList, UserDistributionBO userDistributionBO) {
        int index = 0;
        //provinceResultMap<"省份"，<省份对应的data>>
        for (Map.Entry<String, List<UserDistribution>> entrySet : provinceResultMap.entrySet()) {
            String provinceName = entrySet.getKey();
            // 现在获取到是以省市为单位的数据
            List<UserDistribution> dataList = entrySet.getValue();
            Map<Date, BigInteger> parseDataMap = Maps.newTreeMap();
            // 对省市为单位的数据，再根据解析时间进行封装
            dataList.stream().forEach(userDistribution -> {
                if(ObjectUtil.isEmpty(parseDataMap.get(userDistribution.getParseTime()))) {
                    parseDataMap.put(userDistribution.getParseTime(), userDistribution.getParseTotalCnt());
                } else {
                    BigInteger parseTotal = parseDataMap.get(userDistribution.getParseTime());
                    parseDataMap.put(userDistribution.getParseTime(), userDistribution.getParseTotalCnt().add(parseTotal));
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
            // 针对数据节点进行分页
            //totalResult = PageUtils.subListByPage(totalResult, userDistributionBO.getOffset(), userDistributionBO.getLimit());
            JSONObject jsonData = new JSONObject(new LinkedHashMap());
            JSONObject resultData = new JSONObject(new LinkedHashMap());
            jsonData.put("data", totalResult);
            jsonData.put("name", provinceName);
            resultData.put("result", jsonData);
            dataIndexJson.put("data" + (++index), resultData);
        }
    }

    private void collectProvinceRankByTop10AndOther(List<String> provinceRankList, Map<String, List<UserDistribution>> provinceResultMap, Map<String, List<UserDistribution>> provinceMap) {
        if(provinceRankList.size() <= DEFAULT_TOP_FOR_TREND_REPORT) {
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
            List<UserDistribution> otherDistribution = Lists.newArrayList();
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
     * @param userDistributionList
     * @param provinceMap
     */
    private void collectUserDistributionGroupByProvince(List<UserDistribution> userDistributionList, Map<String, List<UserDistribution>> provinceMap, List<String> provinceList) {
        List<UserDistribution> otherData = Lists.newArrayList();
        userDistributionList.stream().forEach(userDistribution -> {
            if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                if(provinceList.contains(userDistribution.getProvince())) {
                    // 如果包含，按照省份进行汇总
                    if(provinceMap.containsKey(userDistribution.getProvince())) {
                        provinceMap.get(userDistribution.getProvince()).add(userDistribution);
                    } else {
                        List<UserDistribution> dataList = Lists.newArrayList();
                        dataList.add(userDistribution);
                        provinceMap.put(userDistribution.getProvince(), dataList);
                    }
                } else {
                    otherData.add(userDistribution);
                }
            }
        });
        if(CollUtil.isNotEmpty(otherData)) {
            provinceMap.put("其他", otherData);
        }
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

    private UserDistributionBO checkFindTrendReportMethodParam(UserDistributionBO userDistributionBO) {
        if(ObjectUtil.isEmpty(userDistributionBO.getStartTime()) || ObjectUtil.isEmpty(userDistributionBO.getEndTime())){
            userDistributionBO.setStartTime(DateUtils.getLaterTimeByMin(new Date(), 10, DateUtils.DEFAULT_FMT));
            userDistributionBO.setEndTime(DateUtils.formatDataToString(new Date(), DateUtils.DEFAULT_FMT));
        }
        if(ObjectUtil.isEmpty(userDistributionBO.getQueryType())) {
            userDistributionBO.setQueryType("1min");
        }
        if("0".equals(userDistributionBO.getIspCode())) {
            userDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getRankNumber()) && userDistributionBO.getRankNumber().equals(0)) {
            userDistributionBO.setRankNumber(null);
        }
        return userDistributionBO;
    }

    private UserDistributionBO checkFindTrendDetailMethodParam(UserDistributionBO userDistributionBO) {
        if(ObjectUtil.isEmpty(userDistributionBO.getStartTime()) || ObjectUtil.isEmpty(userDistributionBO.getEndTime())){
            userDistributionBO.setStartTime(DateUtils.getLaterTimeByMin(new Date(), 10, DateUtils.DEFAULT_FMT));
            userDistributionBO.setEndTime(DateUtils.formatDataToString(new Date(), DateUtils.DEFAULT_FMT));
        }
        if(ObjectUtil.isEmpty(userDistributionBO.getQueryType())) {
            userDistributionBO.setQueryType("1min");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getIspCode()) && userDistributionBO.getIspCode().equals("0")) {
            userDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getRankNumber()) && userDistributionBO.getRankNumber().equals(0)) {
            userDistributionBO.setRankNumber(null);
        }
        return userDistributionBO;
    }

    public List<String> findTrendDistributeByNum(UserDistributionBO userDistributionBO, int distributeNum) {
        //1.根据条件查询对应数据 2.以时间为节点进行统计《计算总解析数，》 以各省市为节点进行统计，计算占比
        List<String> provinceList = Lists.newArrayList();
        userDistributionBO = checkFindTrendDetailMethodParam(userDistributionBO);
        List<UserDistribution> userDistributionData = userDistributionMapper.findInfoGroupByProvince(userDistributionBO);
        Map<String, UserDistribution> resultMap = Maps.newHashMap();
        userDistributionData.stream().forEach(userDistribution -> {
            if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                UserDistribution distribution = null;
                if(resultMap.containsKey(userDistribution.getProvince())) {
                    distribution = resultMap.get(userDistribution.getProvince());
                    BigInteger parseTotalCnt = distribution.getParseTotalCnt();
                    distribution.setParseTotalCnt(parseTotalCnt.add(userDistribution.getParseTotalCnt()));
                } else {
                    distribution = new UserDistribution();
                    distribution.setProvince(userDistribution.getProvince());
                    distribution.setParseTotalCnt(userDistribution.getParseTotalCnt());
                }
                resultMap.put(userDistribution.getProvince(), distribution);
            }
        });

        List<UserDistribution> userDistributionList = Lists.newArrayList();
        if(CollUtil.isNotEmpty(resultMap)) {
            userDistributionList = new ArrayList<>(resultMap.values());
        }

        userDistributionList = userDistributionList.stream().sorted(new Comparator<UserDistribution>() {
            @Override
            public int compare(UserDistribution o1, UserDistribution o2) {
                return o2.getParseTotalCnt().compareTo(o1.getParseTotalCnt());
            }
        }).collect(Collectors.toList());

        userDistributionList.stream().forEach(userDistribution -> {
            provinceList.add(userDistribution.getProvince());
        });
        return provinceList.subList(0, distributeNum > provinceList.size() ? provinceList.size() : distributeNum);
    }

    public void downloadDetail(UserDistributionBO userDistributionBO, HttpServletResponse response) throws Exception {
        //1.根据条件查询对应数据 2.以时间为节点进行统计《计算总解析数，》 以各省市为节点进行统计，计算占比
        checkDownloadDetailMethodParam(userDistributionBO);
        userDistributionBO.setLimit(10000L);
        Map<Date, List<UserDistribution>> parseDataMap = Maps.newTreeMap();
        List<UserDistributionBO> dataList = Lists.newArrayList();

        if(isSearchProvinceInfo(userDistributionBO.getProvince())) {
            if(ObjectUtil.equals(userDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceLimitNumAll(userDistributionBO);
                String time=userDistributionBO.getStartTime() + "~" + userDistributionBO.getEndTime();
                BigInteger parseTotalCnt = BigInteger.ZERO;
                for (UserDistribution userDistribution : userDistributionList) {
                    if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                        parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                    }
                }
                for (UserDistribution userDistribution : userDistributionList) {
                    if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                        UserDistributionBO distributionBO = new UserDistributionBO();
                        distributionBO.setTimeRange(time);
                        distributionBO.setProvince(userDistribution.getProvince());
                        distributionBO.setParseTotalCnt(userDistribution.getParseTotalCnt());
                        distributionBO.setRate(ReportUtils.buildRatioBase(userDistribution.getParseTotalCnt(), parseTotalCnt));
                        dataList.add(distributionBO);
                    }
                }
            }else{
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceLimitNum(userDistributionBO);
                // 按照时间进行汇总
                userDistributionList.stream().forEach(userDistribution -> {
                    if (parseDataMap.containsKey(userDistribution.getParseTime())) {
                        parseDataMap.get(userDistribution.getParseTime()).add(userDistribution);
                    } else {
                        List<UserDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(userDistribution);
                        parseDataMap.put(userDistribution.getParseTime(), distributionTimeList);
                    }
                });
                for (Map.Entry<Date, List<UserDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    List<UserDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    for (UserDistribution userDistribution : valueData) {
                        if (StrUtil.isNotEmpty(userDistribution.getProvince())) {
                            parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                        }
                    }
                    for (UserDistribution userDistribution : valueData) {
                        if (StrUtil.isNotEmpty(userDistribution.getProvince())) {
                            UserDistributionBO distributionBO = new UserDistributionBO();
                            distributionBO.setTimeRange(DateUtils.formatDataToString(userDistribution.getParseTime(), DateUtils.DEFAULT_FMT));
                            distributionBO.setProvince(userDistribution.getProvince());
                            distributionBO.setParseTotalCnt(userDistribution.getParseTotalCnt());
                            distributionBO.setRate(ReportUtils.buildRatioBase(userDistribution.getParseTotalCnt(), parseTotalCnt));
                            dataList.add(distributionBO);
                        }
                    }
                }
            }
        } else {
            if(ObjectUtil.equals(userDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndCityLimitNumAll(userDistributionBO);
                String time=userDistributionBO.getStartTime() + "~" + userDistributionBO.getEndTime();
                BigInteger parseTotalCnt = BigInteger.ZERO;
                for (UserDistribution userDistribution : userDistributionList) {
                    if(StrUtil.isNotEmpty(userDistribution.getCity())) {
                        parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                    }
                }
                for (UserDistribution userDistribution : userDistributionList) {
                    if(StrUtil.isNotEmpty(userDistribution.getCity())) {
                        UserDistributionBO distributionBO = new UserDistributionBO();
                        distributionBO.setTimeRange(time);
                        distributionBO.setProvince(userDistribution.getCity());
                        distributionBO.setParseTotalCnt(userDistribution.getParseTotalCnt());
                        distributionBO.setRate(ReportUtils.buildRatioBase(userDistribution.getParseTotalCnt(), parseTotalCnt));
                        dataList.add(distributionBO);
                    }
                }
            }else{
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndCityLimitNum(userDistributionBO);
                // 按照时间进行汇总
                userDistributionList.stream().forEach(userDistribution -> {
                    if(parseDataMap.containsKey(userDistribution.getParseTime())) {
                        parseDataMap.get(userDistribution.getParseTime()).add(userDistribution);
                    } else {
                        List<UserDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(userDistribution);
                        parseDataMap.put(userDistribution.getParseTime(), distributionTimeList);
                    }
                });
                for (Map.Entry<Date, List<UserDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    List<UserDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    for (UserDistribution userDistribution : valueData) {
                        if (StrUtil.isNotEmpty(userDistribution.getCity())) {
                            parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                        }
                    }
                    for (UserDistribution userDistribution : valueData) {
                        if (StrUtil.isNotEmpty(userDistribution.getCity())) {
                            UserDistributionBO distributionBO = new UserDistributionBO();
                            distributionBO.setTimeRange(DateUtils.formatDataToString(userDistribution.getParseTime(), DateUtils.DEFAULT_FMT));
                            distributionBO.setProvince(userDistribution.getCity());
                            distributionBO.setParseTotalCnt(userDistribution.getParseTotalCnt());
                            distributionBO.setRate(ReportUtils.buildRatioBase(userDistribution.getParseTotalCnt(), parseTotalCnt));
                            dataList.add(distributionBO);
                        }
                    }
                }
            }
        }
        List<String> csvLines = dataList.stream().map(UserDistributionBO::getCsvLineSting).collect(Collectors.toList());
        CsvUtils.exportCsv(UserDistributionBO.CSV_NAME, UserDistributionBO.CSV_HEAD, csvLines, response);
    }

    private void checkDownloadDetailMethodParam(UserDistributionBO userDistributionBO) {
        if(ObjectUtil.isEmpty(userDistributionBO.getStartTime()) || ObjectUtil.isEmpty(userDistributionBO.getEndTime())){
            userDistributionBO.setStartTime(DateUtils.getLaterTimeByMin(new Date(), 10, DateUtils.DEFAULT_FMT));
            userDistributionBO.setEndTime(DateUtils.formatDataToString(new Date(), DateUtils.DEFAULT_FMT));
        }
        if(ObjectUtil.isEmpty(userDistributionBO.getQueryType())) {
            userDistributionBO.setQueryType("1min");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getIspCode()) && userDistributionBO.getIspCode().equals("0")) {
            userDistributionBO.setIspCode("");
        }
        if(ObjectUtil.isNotEmpty(userDistributionBO.getRankNumber()) && userDistributionBO.getRankNumber().equals(0)) {
            userDistributionBO.setRankNumber(null);
        }
    }

    public Map<String, Object> findTrendDetail(UserDistributionBO userDistributionBO) {
        Map<String, Object> finalResult = Maps.newHashMap();
        //1.根据条件查询对应数据 2.以时间为节点进行统计《计算总解析数，》 以各省市为节点进行统计，计算占比
        userDistributionBO = checkFindTrendDetailMethodParam(userDistributionBO);
        long total = calculateTotalDataNumAndResetSearchTime(userDistributionBO);
        Map<Date, List<UserDistribution>> parseDataMap = Maps.newTreeMap();
        List dataList = Lists.newArrayList();
        if(isSearchProvinceInfo(userDistributionBO.getProvince())) {
            if(ObjectUtil.equals(userDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                total= Long.valueOf("1");
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndProvinceAll(userDistributionBO);
                if(userDistributionList!=null&&userDistributionList.size()>0){
                    String time=userDistributionBO.getStartTime() + "~" + userDistributionBO.getEndTime();
                    BigInteger parseTotalCnt = BigInteger.ZERO;
                    for (UserDistribution userDistribution : userDistributionList) {
                        if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                            parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                        }
                    }
                    JSONObject dataObject = new JSONObject(new LinkedHashMap());
                    dataObject.put("时间", time);
                    dataObject.put("解析次数", parseTotalCnt);
                    for (UserDistribution userDistribution : userDistributionList) {
                        if(StrUtil.isNotEmpty(userDistribution.getProvince())) {
                            double parserRate = ReportUtils.buildRatioBase(userDistribution.getParseTotalCnt(), parseTotalCnt);
                            dataObject.put(userDistribution.getProvince(), userDistribution.getParseTotalCnt() + StrUtil.LF + StrUtils.convertDoubleToPercent(parserRate, 2));
                        }
                    }
                    dataList.add(dataObject);
                }
            }else{
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndProvince(userDistributionBO);
                // 按照时间进行汇总
                userDistributionList.stream().forEach(userDistribution -> {
                    if(parseDataMap.containsKey(userDistribution.getParseTime())) {
                        parseDataMap.get(userDistribution.getParseTime()).add(userDistribution);
                    } else {
                        List<UserDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(userDistribution);
                        parseDataMap.put(userDistribution.getParseTime(), distributionTimeList);
                    }
                });
                // 按照省市进行区分
                for (Map.Entry<Date, List<UserDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    Date parseDate = parseDataEntrySet.getKey();
                    List<UserDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    Map<String, BigInteger> pointMap = Maps.newHashMap(); // 这一分钟里，按照省市进行统计
                    for(UserDistribution userDistribution : valueData) {
                        String province = userDistribution.getProvince();
                        if(StrUtil.isNotEmpty(province)) {
                            parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                            if(pointMap.containsKey(province)) {
                                pointMap.put(province, pointMap.get(province).add(userDistribution.getParseTotalCnt()));
                            } else {
                                pointMap.put(province, userDistribution.getParseTotalCnt());
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
                    JSONObject dataObject = new JSONObject();
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
            if(ObjectUtil.equals(userDistributionBO.getStatisticsWay(), StatisticsWayEnum.ALL.getType())) {
                total= Long.valueOf("1");
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndCityAll(userDistributionBO);
                if(userDistributionList!=null&&userDistributionList.size()>0){
                    String time=userDistributionBO.getStartTime() + "~" + userDistributionBO.getEndTime();
                    BigInteger parseTotalCnt = BigInteger.ZERO;
                    for (UserDistribution userDistribution : userDistributionList) {
                        if(StrUtil.isNotEmpty(userDistribution.getCity())) {
                            parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                        }
                    }
                    JSONObject dataObject = new JSONObject(new LinkedHashMap());
                    dataObject.put("时间", time);
                    dataObject.put("解析次数", parseTotalCnt);
                    for (UserDistribution userDistribution : userDistributionList) {
                        if(StrUtil.isNotEmpty(userDistribution.getCity())) {
                            double parserRate = ReportUtils.buildRatioBase(userDistribution.getParseTotalCnt(), parseTotalCnt);
                            dataObject.put(userDistribution.getCity(), userDistribution.getParseTotalCnt() + StrUtil.LF + StrUtils.convertDoubleToPercent(parserRate, 2));
                        }
                    }
                    dataList.add(dataObject);
                }
            }else{
                List<UserDistribution> userDistributionList = userDistributionMapper.findBySelectiveGroupByParseTimeAndCity(userDistributionBO);
                // 按照时间进行汇总
                userDistributionList.stream().forEach(userDistribution -> {
                    if(parseDataMap.containsKey(userDistribution.getParseTime())) {
                        parseDataMap.get(userDistribution.getParseTime()).add(userDistribution);
                    } else {
                        List<UserDistribution> distributionTimeList = Lists.newArrayList();
                        distributionTimeList.add(userDistribution);
                        parseDataMap.put(userDistribution.getParseTime(), distributionTimeList);
                    }
                });
                // 按照区域进行汇总
                for (Map.Entry<Date, List<UserDistribution>> parseDataEntrySet : parseDataMap.entrySet()) {
                    Date parseDate = parseDataEntrySet.getKey();
                    List<UserDistribution> valueData = parseDataEntrySet.getValue();
                    BigInteger parseTotalCnt = BigInteger.ZERO; // 这一分钟的总体parseTotalCnt
                    Map<String, BigInteger> pointMap = Maps.newHashMap(); // 这一分钟里，按照省市进行统计
                    for(UserDistribution userDistribution : valueData) {
                        String city = userDistribution.getCity();
                        if(StrUtil.isNotEmpty(city)) {
                            parseTotalCnt = parseTotalCnt.add(userDistribution.getParseTotalCnt());
                            if(pointMap.containsKey(city)) {
                                pointMap.put(city, pointMap.get(city).add(userDistribution.getParseTotalCnt()));
                            } else {
                                pointMap.put(city, userDistribution.getParseTotalCnt());
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

    private long calculateTotalDataNumAndResetSearchTime(UserDistributionBO userDistributionBO) {
        Long total = userDistributionMapper.countPageByParseTime(userDistributionBO);
        /*
        List<String> timeData = userDistributionMapper.pageByParseTime(userDistributionBO);
        if(CollUtil.isNotEmpty(timeData)) {
            userDistributionBO.setStartTime(timeData.get(0));
            userDistributionBO.setEndTime(timeData.get(timeData.size() - 1));
        }
        */
        return total;
    }
}
