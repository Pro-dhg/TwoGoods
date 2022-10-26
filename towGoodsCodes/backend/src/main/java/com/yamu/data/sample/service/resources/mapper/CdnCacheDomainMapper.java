package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheCompanyDetail;
import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheCompanyReport;
import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheDomainDetail;
import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheDomainReport;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheCompany;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheDomain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CdnCacheDomainMapper {
    List<ResourceCdnCacheDomain> cdnCacheAnalysis(@Param("queryParam") ResourceCdnCacheDomain parmer,@Param("queryList") List<String> queryList);
    List<ResourceCdnCacheDomain> cdnCacheAnalysisEvery(@Param("queryParam") ResourceCdnCacheDomain parmer);
    //List<ResourceCdnCacheDomain> cdnAnalysis(@Param("queryParam") ResourceCdnCacheDomain parmer,@Param("topDomainName") List<String> topDomainName);
    //List<ResourceCdnCacheDomain> cacheAnalysis(@Param("queryParam") ResourceCdnCacheDomain parmer,@Param("topDomainName") List<String> topDomainName);

    /**
     * 获取topn域名
     * @param queryParam
     * @return
     */
    List<String> getTopN(@Param("queryParam") ResourceCdnCacheDomain queryParam);

    /**
     * 获取分页域名
     * @param queryParam
     * @return
     */
    //List<String> getLimitDomain(@Param("queryParam") ResourceCdnCacheDomain queryParam);

    Long getCount(@Param("queryParam") ResourceCdnCacheDomain parmer, @Param("topDomainName") List<String> topDomainName);

    Long findDetailCount(@Param("queryParam") ResourceCdnCacheDomain parmer,@Param("topDomainName") List<String> topDomainName);

    List<ResourceCdnCacheDomain> findDetailData(@Param("queryParam") ResourceCdnCacheDomain parmer,@Param("topDomainName") List<String> topDomainName);

    Long findTimeSizeCount(@Param("queryParam") ResourceCdnCacheDomain parmer);

    List<ResourceCdnCacheDomain> findTimeSizeData(@Param("queryParam") ResourceCdnCacheDomain parmer);


    List<ResourceCdnCacheDomain> findRateReport(@Param("queryParam") ResourceCdnCacheDomain parmer);


    List<ResourceCdnCacheDomain> findParseReport(@Param("queryParam") ResourceCdnCacheDomain parmer);

    List<ResourceCdnCacheDomain> downloadTime(@Param("queryParam") ResourceCdnCacheDomain parmer);

    List<ResourceCdnCacheDomain> downloadAllTime(@Param("queryParam") ResourceCdnCacheDomain parmer, @Param("topDomainName") List<String> topDomainName);

    //List<ResourceCdnCacheDomain> cdnTimeAnalysis(@Param("queryParam") ResourceCdnCacheDomain parmer);

    //List<ResourceCdnCacheDomain> cacheTimeAnalysis(@Param("queryParam") ResourceCdnCacheDomain parmer);

    List<ResourceCdnCacheDomainReport> findtopNServiceReport(@Param("queryParam") ResourceCdnCacheDomain parmer);

    List<ResourceCdnCacheDomainDetail> topNServiceDetail(@Param("queryParam") ResourceCdnCacheDomain parmer);

    Long countTopNServiceDetail(@Param("queryParam") ResourceCdnCacheDomain parmer);
}
