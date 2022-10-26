package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.PartnerDomainTopNWebsite;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: ZhangYanping
 * @Date: 2021/7/5 23:47
 * @Desc: 特定域名TopN网站分析
 */
@Repository
public interface PartnerDomainTopNWebsiteMapper {

    Long countDetailReportByPage(@Param("queryParam") PartnerDomainTopNWebsite param);

    List<PartnerDomainTopNWebsite> findDetailReportByPage(@Param("queryParam") PartnerDomainTopNWebsite param);

    // Top20：排名趋势
    List<PartnerDomainTopNWebsite> findTop20ReportGroupByParseTimeByParam(@Param("queryParam") PartnerDomainTopNWebsite param);

    // TopN网站
    List<PartnerDomainTopNWebsite> findTopNReportGroupByParseTimeByParam(@Param("queryParam") PartnerDomainTopNWebsite param);

    // 某网站：资源分布
    List<PartnerDomainTopNWebsite> findTrendReportGroupByIspByParam(@Param("queryParam") PartnerDomainTopNWebsite param);

    // 网站：本省 本网率、CDN IDC
    List<PartnerDomainTopNWebsite> findTrendReportGroupByParseTimeByParam(@Param("queryParam") PartnerDomainTopNWebsite param);
}
