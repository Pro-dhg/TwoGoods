package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.po.WebsiteSecondLevelDomainTopN;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2021/8/18
 * @DESC
 */
@Repository
public interface WebsiteSecondLevelDomainTopNMapper {
    //导出，时间段
    List<WebsiteSecondLevelDomainTopN> queryDataByTimeParamToDownload(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //导出
    List<WebsiteSecondLevelDomainTopN> queryDataByQueryTimeParamToDownload(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);

    List<WebsiteSecondLevelDomainTopN> findTrendReportGroupByIspByParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);

    List<WebsiteSecondLevelDomainTopN> queryLastTimeDataGroupByWebsiteByParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);

    //按时间段查总表
    List<WebsiteSecondLevelDomainTopN> queryDataGroupByTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间段查子表
    List<WebsiteSecondLevelDomainTopN> querySecondDataGroupByTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间粒度查总表
    List<WebsiteSecondLevelDomainTopN> queryDataGroupByQueryTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间粒度查子表
    List<WebsiteSecondLevelDomainTopN> querySecondDataGroupByQueryTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间段汇聚total
    Long countQueryDataGroupByTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间段汇聚子表total
    Long countQuerySecondDataGroupByTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间粒度汇聚总表total
    Long countQueryDataGroupByQueryTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
    //按时间粒度汇聚子表total
    Long countQuerySecondDataGroupByQueryTimeParam(@Param("queryParam") WebsiteSecondLevelDomainTopN domainNameWebsiteDetail);
}