package com.yamu.data.sample.service.resources.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author Lishuntao
 * @Date 2021/5/25
 */
@Component
@Data
public class BaseService {

    // 省份  code - 省名  map
    public Map<String, String> provinceDistMap = Maps.newLinkedHashMap();
    // 唯一键值对map, 然后进行反转, 也就是 dist-code map
    public Map<String, String> distProvinceMap = HashBiMap.create();
    // 复杂省名 -- 省名 map
    public HashBiMap<String, String> provinceAbbrMap = HashBiMap.create();
    // 直辖市列表
    public List<String> municipalityCityList = Lists.newArrayList();

    // 判断是否为直辖市
    protected boolean isMunicipalityCity(String province) {
        return municipalityCityList.contains(province);
    }

    //获取省的简称
    public String getAbbrProvinceName(String provinceName) {
        return provinceAbbrMap.get(provinceName);
    }

    //获取省的全称
    public String getWholeProvinceName(String provinceName) {
        return provinceAbbrMap.inverse().get(provinceName);
    }

    @PostConstruct
    public void postConstruct() {
        provinceDistMap.put("110000", "北京市");
        provinceDistMap.put("120000", "天津市");
        provinceDistMap.put("130000", "河北省");
        provinceDistMap.put("140000", "山西省");
        provinceDistMap.put("150000", "内蒙古自治区");
        provinceDistMap.put("210000", "辽宁省");
        provinceDistMap.put("220000", "吉林省");
        provinceDistMap.put("230000", "黑龙江省");
        provinceDistMap.put("310000", "上海市");
        provinceDistMap.put("320000", "江苏省");
        provinceDistMap.put("330000", "浙江省");
        provinceDistMap.put("340000", "安徽省");
        provinceDistMap.put("350000", "福建省");
        provinceDistMap.put("360000", "江西省");
        provinceDistMap.put("370000", "山东省");
        provinceDistMap.put("410000", "河南省");
        provinceDistMap.put("420000", "湖北省");
        provinceDistMap.put("430000", "湖南省");
        provinceDistMap.put("440000", "广东省");
        provinceDistMap.put("450000", "广西壮族自治区");
        provinceDistMap.put("460000", "海南省");
        provinceDistMap.put("500000", "重庆市");
        provinceDistMap.put("510000", "四川省");
        provinceDistMap.put("520000", "贵州省");
        provinceDistMap.put("530000", "云南省");
        provinceDistMap.put("540000", "西藏自治区");
        provinceDistMap.put("610000", "陕西省");
        provinceDistMap.put("620000", "甘肃省");
        provinceDistMap.put("630000", "青海省");
        provinceDistMap.put("640000", "宁夏回族自治区");
        provinceDistMap.put("650000", "新疆维吾尔自治区");
        provinceDistMap.put("710000", "台湾省");
        provinceDistMap.put("810000", "香港特别行政区");
        provinceDistMap.put("820000", "澳门特别行政区");
        // 创建反转map
        distProvinceMap = HashBiMap.create(provinceDistMap).inverse();
        // 生成一个带有简写的
        provinceDistMap.values().stream().forEach(item -> {
            if (item.equals("内蒙古自治区")) {
                provinceAbbrMap.put(item, "内蒙古");
            } else if (item.equals("黑龙江省")) {
                provinceAbbrMap.put(item, "黑龙江");
            } else {
                provinceAbbrMap.put(item, item.substring(0, 2));
            }
        });
        municipalityCityList = Lists.newArrayList("北京", "上海", "重庆", "天津");
    }

    /**
     * 获取城市结尾统称
     * @return
     */
    public List<String> getCollectiveNameOfCity() {
        List<String> cityCollList = Lists.newArrayList();
        cityCollList.add("蒙古自治州");
        cityCollList.add("苗族自治州");
        cityCollList.add("回族自治州");
        cityCollList.add("藏族自治州");
        cityCollList.add("黎族自治县");
        cityCollList.add("土家族自治县");
        cityCollList.add("傣族自治州");
        cityCollList.add("彝族自治州");
        cityCollList.add("僳族自治州");
        cityCollList.add("侗族自治州");
        cityCollList.add("羌族自治州");
        cityCollList.add("黎族自治县");
        cityCollList.add("黎族自治县");
        cityCollList.add("朝鲜族自治州");
        cityCollList.add("白族自治州");
        cityCollList.add("地区");
        cityCollList.add("林区");
        cityCollList.add("自治州");
        cityCollList.add("自治县");
        cityCollList.add("市");
        cityCollList.add("区");
        cityCollList.add("县");
        return cityCollList;
    }


    /**
     * 重新设置ispCode属性
     *
     * @param ispCode
     * @return
     */
    public String reSetIspCode(String ispCode) {
        if (Objects.equals("0", ispCode)) {
            return null;
        } else {
            return ispCode;
        }
    }

    /**
     * 重新设置ranknumber属性
     *
     * @param rankNumber
     * @return
     */
    public Long reSetRankNumber(Long rankNumber) {
        if (rankNumber != null && rankNumber.equals(0L)) {
            return null;
        } else {
            return rankNumber;
        }
    }


    protected JSONObject getResultJson() {
        return new JSONObject(new LinkedHashMap<>());
    }
}
