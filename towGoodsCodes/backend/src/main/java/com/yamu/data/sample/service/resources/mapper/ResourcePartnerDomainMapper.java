package com.yamu.data.sample.service.resources.mapper;


import com.yamu.data.sample.service.resources.entity.po.ResourcePartnerDomain;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author Lishuntao
 * @Date 2021/1/21
 */
@Repository
public interface ResourcePartnerDomainMapper {
    @Select("<script>"
            + "select * from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
            + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
            + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
            + "<if test = 'queryParam.rankNumber != null and queryParam.rankNumber != \"\" '>and rank_number &lt;= #{queryParam.rankNumber}</if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + "</script>")
    List<ResourcePartnerDomain> findAll(@Param("queryParam") ResourcePartnerDomain queryParam);



    @Select("<script>"
            + "select parse_time, SUM(parse_total_cnt) as parse_total_cnt, SUM(parse_success_cnt) as parse_success_cnt, "
            + "SUM(net_in_parse_total_cnt) as net_in_parse_total_cnt, SUM(net_out_parse_total_cnt) as net_out_parse_total_cnt, SUM(without_parse_total_cnt) as without_parse_total_cnt, "
            + "SUM(within_parse_total_cnt) as within_parse_total_cnt, SUM(cdn_parse_total_cnt) as cdn_parse_total_cnt, SUM(cache_parse_total_cnt) as cache_parse_total_cnt, "
            + "SUM(idc_parse_total_cnt) as idc_parse_total_cnt, sum(a_record_parse_total_cnt) as a_record_parse_total_cnt  "
            + "from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey}</if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
            + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
            + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>" //&lt;= #{queryParam.rankNumber}
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + "<if test = 'queryParam.rankNumber != null and queryParam.domainTypeKey == null'>and domain_type_key global in ("
            + "select domain_type_key from ${queryParam.queryTable} where 1=1"
                + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
                + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
                + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
                + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
                + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
                + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
                + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
                + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
                + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
                + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
                + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
                + " group by domain_type_key"
                + " order by SUM(parse_total_cnt) desc"
                + " limit #{queryParam.rankNumber}"
            + ") </if>"
            + "group by (parse_time)"
            + " order by parse_time"
            + "</script>")
    List<ResourcePartnerDomain> findAllGroupByParseTime(@Param("queryParam") ResourcePartnerDomain queryParam);
    // list_todo 暂定只统计总数信息
    @Select("<script>"
            + "select  SUM(a_record_parse_total_cnt) as a_record_parse_total_cnt,"
            + "answer_first_isp as isp  "
            + "from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey}</if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
            + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
            + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + "group by answer_first_isp order by a_record_parse_total_cnt desc"
            + "<if test = 'queryParam.rankNumber != null'>limit #{queryParam.rankNumber}</if>"
            + "</script>")
    List<ResourcePartnerDomain> findAllGroupByIsp(@Param("queryParam") ResourcePartnerDomain queryParam);
    @Select("<script>"
            +"select count(*) from "
            + "(select parse_time "
            + "from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey}</if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
            + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
            + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + "<if test = 'queryParam.rankNumber != null and queryParam.domainTypeKey == null'>and domain_type_key global in ("
                + "select domain_type_key from ${queryParam.queryTable} where 1=1"
                + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
                + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
                + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
                + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
                + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
                + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
                + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
                + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
                + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
                + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
                + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
                + " group by domain_type_key"
                + " order by SUM(parse_total_cnt) desc"
                + " limit #{queryParam.rankNumber}"
                + ") </if>"
            + "group by (parse_time) )"
            + "</script>")
    Long countAllGroupByParseTime(@Param("queryParam") ResourcePartnerDomain queryParam);

    @Select("<script>"
            + "select parse_time, SUM(parse_total_cnt) as parse_total_cnt, SUM(parse_success_cnt) as parse_success_cnt, "
            + "SUM(net_in_parse_total_cnt) as net_in_parse_total_cnt, SUM(net_out_parse_total_cnt) as net_out_parse_total_cnt, SUM(without_parse_total_cnt) as without_parse_total_cnt, "
            + "SUM(within_parse_total_cnt) as within_parse_total_cnt, SUM(cdn_parse_total_cnt) as cdn_parse_total_cnt, SUM(cache_parse_total_cnt) as cache_parse_total_cnt, "
            + "SUM(idc_parse_total_cnt) as idc_parse_total_cnt , sum(a_record_parse_total_cnt) as a_record_parse_total_cnt  "
            + "from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey}</if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
            + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
            + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>" //&lt;= #{queryParam.rankNumber}
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + "<if test = 'queryParam.rankNumber != null and queryParam.domainTypeKey == null'>and domain_type_key global in ("
            + "select domain_type_key from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\" '>and isp_code = #{queryParam.ispCode}</if>"
            + "<if test = 'queryParam.qtype != null and queryParam.qtype != \"\"'>and qtype = #{queryParam.qtype}</if>"
            + "<if test = 'queryParam.otherQtype'>and qtype not in('1','2','5','28')</if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\" '>and province  = #{queryParam.province}</if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\" '>and city  = #{queryParam.city}</if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\" '>and district  = #{queryParam.district}</if>"
            + "<if test = 'queryParam.userType != null and queryParam.userType != \"\" '>and user_type  = #{queryParam.userType}</if>"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt;= #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + " group by domain_type_key"
            + " order by SUM(parse_total_cnt) desc"
            + " limit #{queryParam.rankNumber}"
            + ") </if>"
            + " group by (parse_time)"
            + " order by parse_time"
            + " limit #{queryParam.offset} , #{queryParam.limit} "
            + "</script>")
    List<ResourcePartnerDomain> findGroupByParseTime(@Param("queryParam") ResourcePartnerDomain queryParam);
}
