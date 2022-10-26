package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.UserDistributionBO;
import com.yamu.data.sample.service.resources.entity.po.UserDistribution;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDistributionMapper {

    @Select("<script>"
            + "select * from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "</script>")
    List<UserDistribution> findBySelective(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select province, SUM(parse_total_cnt) as parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by province"
            + "</script>")
    List<UserDistribution> findInfoGroupByProvince(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select case when city='辖区' then district else city end as city, SUM(parse_total_cnt) as parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by case when city='辖区' then district else city end as city"
            + "</script>")
    List<UserDistribution> findInfoGroupByCity(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select parse_time, province, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by(parse_time, province)"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndProvince(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select parse_time, case when city='辖区' then district else city end as city, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by(parse_time, case when city='辖区' then district else city end as city)"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndCity(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select parse_time from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by parse_time order by parse_time limit #{queryParam.offset},#{queryParam.limit} "
            + "</script>")
    List<String> pageByParseTime(@Param("queryParam") UserDistributionBO userDistributionBO);

    @Select("<script>"
            + "select parse_time, province, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by(parse_time, province) order by parse_time desc limit #{queryParam.offset} , #{queryParam.limit}"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndProvinceLimitNum(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select parse_time, case when city='辖区' then district else city end as city, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by(parse_time, case when city='辖区' then district else city end as city) order by parse_time desc limit #{queryParam.offset} , #{queryParam.limit}"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndCityLimitNum(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select count(1) from ("
            + "select parse_time from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by parse_time"
            + ")"
            + "</script>")
    Long countPageByParseTime(@Param("queryParam") UserDistributionBO userDistributionBO);

    @Select("<script>"
            + "select province, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by province "
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndProvinceAll(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select case when city='辖区' then district else city end as city, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by(case when city='辖区' then district else city end as city)"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndCityAll(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select province, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by province  limit #{queryParam.offset} , #{queryParam.limit}"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndProvinceLimitNumAll(@Param("queryParam") UserDistributionBO queryParam);

    @Select("<script>"
            + "select case when city='辖区' then district else city end as city, SUM(parse_total_cnt) AS parse_total_cnt from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.rankNumber != null'>and rank_number &lt;= #{queryParam.rankNumber} </if>"
            + "<if test = 'queryParam.parseTotalCnt != null'>and parse_total_cnt = #{queryParam.parseTotalCnt} </if>"
            + "<if test = 'queryParam.successCnt != null'>and success_cnt = #{queryParam.successCnt} </if>"
            + "<if test = 'queryParam.failCnt != null'>and fail_cnt = #{queryParam.failCnt} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\"'>and districts_code like concat(#{queryParam.districtsCode},'%') </if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\"'>and user_type = #{queryParam.userType} </if>"
            + "<if test = 'queryParam.answerFirstIsp != null and queryParam.answerFirstIsp != \"\"'>and isp = #{queryParam.answerFirstIsp} </if>"
            + "<if test = 'queryParam.answerFirstIpDistrictsName != null and queryParam.answerFirstIpDistrictsName != \"\"'>and answer_first_ip_districts_name = #{queryParam.answerFirstIpDistrictsName} </if>"
            + "group by(case when city='辖区' then district else city end as city)  limit #{queryParam.offset} , #{queryParam.limit}"
            + "</script>")
    List<UserDistribution> findBySelectiveGroupByParseTimeAndCityLimitNumAll(@Param("queryParam") UserDistributionBO queryParam);
}