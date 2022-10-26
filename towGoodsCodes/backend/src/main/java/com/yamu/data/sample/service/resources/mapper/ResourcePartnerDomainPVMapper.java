package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.PartnerDomainPVBO;
import com.yamu.data.sample.service.resources.entity.po.PartnerDomainPV;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourcePartnerDomainPVMapper {
    @Select("<script>"
            + "<if test = 'queryParam.rankNumber != null and queryParam.rankNumber != 0'>" +
            " select site_name, business_name, domain_name, domain_type, pv, rank_number from( </if>"
            + "select domain_name, site_name, business_name, domain_type, SUM(pv) as pv, row_number() OVER(ORDER BY pv DESC) rank_number from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt; #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.pv != null'>and pv = #{queryParam.pv} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.domainName != null and queryParam.domainName != \"\"'>and domain_name = #{queryParam.domainName} </if>"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.isp != null and queryParam.isp != \"\"'>and isp = #{queryParam.isp} </if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\"'>and isp_code = #{queryParam.ispCode} </if>"
            + "<if test = 'queryParam.businessName != null and queryParam.businessName != \"\"'>and business_name = #{queryParam.businessName} </if>"
            + "<if test = 'queryParam.siteName != null and queryParam.siteName != \"\"'>and site_name = #{queryParam.siteName} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + " group by(domain_name, site_name, business_name, domain_type)"
            + "<if test = 'queryParam.rankNumber != null and queryParam.rankNumber != 0'>" +
            " ) where rank_number &lt;= #{queryParam.rankNumber} </if>"
            + " limit #{queryParam.offset}, #{queryParam.limit}"
            + " settings allow_experimental_window_functions = 1"
            + "</script>")
    List<PartnerDomainPVBO> findBySelectiveGroupByNetNameAndSiteNameAndTopNAndOrderByPVAndPage(@Param("queryParam") PartnerDomainPV queryParam);

    @Select("<script>"
            + " select count(1) from ( "
            + "select domain_name, site_name, business_name, domain_type, SUM(pv) as pv, row_number() OVER(ORDER BY pv DESC) rank_number from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt; #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.pv != null'>and pv = #{queryParam.pv} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.domainName != null and queryParam.domainName != \"\"'>and domain_name = #{queryParam.domainName} </if>"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.isp != null and queryParam.isp != \"\"'>and isp = #{queryParam.isp} </if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\"'>and isp_code = #{queryParam.ispCode} </if>"
            + "<if test = 'queryParam.businessName != null and queryParam.businessName != \"\"'>and business_name = #{queryParam.businessName} </if>"
            + "<if test = 'queryParam.siteName != null and queryParam.siteName != \"\"'>and site_name = #{queryParam.siteName} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + " group by(domain_name, site_name, business_name, domain_type))"
            + "<if test = 'queryParam.rankNumber != null and queryParam.rankNumber != 0'>" +
            " where rank_number &lt;= #{queryParam.rankNumber} </if>"
            + " settings allow_experimental_window_functions = 1"
            + "</script>")
    Long countGroupByNetNameAndSiteNameAndTopNAndOrderByPV(@Param("queryParam") PartnerDomainPV queryParam);

    @Select("<script>"
            + "<if test = 'queryParam.rankNumber != null and queryParam.rankNumber != 0'>" +
            " select site_name, business_name, domain_name, domain_type, pv, rank_number from( </if>"
            + "select domain_name, site_name, business_name, domain_type, SUM(pv) as pv, row_number() OVER(ORDER BY pv DESC) rank_number from ${queryParam.queryTable} where 1=1"
            + "<if test = 'queryParam.startTime != null'>and parse_time &gt;= #{queryParam.startTime} </if>"
            + "<if test = 'queryParam.endTime != null'>and parse_time &lt; #{queryParam.endTime} </if>"
            + "<if test = 'queryParam.pv != null'>and pv = #{queryParam.pv} </if>"
            + "<if test = 'queryParam.businessIp != null and queryParam.businessIp != \"\"'>and business_ip = #{queryParam.businessIp} </if>"
            + "<if test = 'queryParam.domainName != null and queryParam.domainName != \"\"'>and domain_name = #{queryParam.domainName} </if>"
            + "<if test = 'queryParam.domainTypeKey != null'>and domain_type_key = #{queryParam.domainTypeKey} </if>"
            + "<if test = 'queryParam.districtsCode != null and queryParam.districtsCode != \"\" '>and districts_code = #{queryParam.districtsCode}</if>"
            + "<if test = 'queryParam.country != null and queryParam.country != \"\"'>and country = #{queryParam.country} </if>"
            + "<if test = 'queryParam.province != null and queryParam.province != \"\"'>and province = #{queryParam.province} </if>"
            + "<if test = 'queryParam.city != null and queryParam.city != \"\"'>and city = #{queryParam.city} </if>"
            + "<if test = 'queryParam.district != null and queryParam.district != \"\"'>and district = #{queryParam.district} </if>"
            + "<if test = 'queryParam.isp != null and queryParam.isp != \"\"'>and isp = #{queryParam.isp} </if>"
            + "<if test = 'queryParam.ispCode != null and queryParam.ispCode != \"\"'>and isp_code = #{queryParam.ispCode} </if>"
            + "<if test = 'queryParam.businessName != null and queryParam.businessName != \"\"'>and business_name = #{queryParam.businessName} </if>"
            + "<if test = 'queryParam.siteName != null and queryParam.siteName != \"\"'>and site_name = #{queryParam.siteName} </if>"
            + "<if test = 'queryParam.serverNodeName != null and queryParam.serverNodeName != \"\"'>and server_node_name = #{queryParam.serverNodeName} </if>"
            + " group by(domain_name, site_name, business_name, domain_type)"
            + "<if test = 'queryParam.rankNumber != null and queryParam.rankNumber != 0'>" +
            " ) where rank_number &lt;= #{queryParam.rankNumber} </if>"
            + " limit #{queryParam.offset}, #{queryParam.limit}"
            + " settings allow_experimental_window_functions = 1"
            + "</script>")
    List<PartnerDomainPVBO> downloadBySelective(@Param("queryParam") PartnerDomainPV queryParam);

}
