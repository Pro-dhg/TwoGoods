package com.yamu.data.sample.service.resources.mapper;

import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheCompanyDetail;
import com.yamu.data.sample.service.resources.entity.bo.ResourceCdnCacheCompanyReport;
import com.yamu.data.sample.service.resources.entity.po.ResourceCdnCacheCompany;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author yuyuan.Dong
 * @Date 2021/7/20
 * @DESC
 */
@Repository
public interface ResourceCdnCacheCompanyMapper {

    List<ResourceCdnCacheCompany> findTableDataByParamEvery(@Param("queryParam") ResourceCdnCacheCompany domainNameWebsiteDetail);

    Long countFindTableDataByParamEvery(@Param("queryParam") ResourceCdnCacheCompany domainNameWebsiteDetail);

    List<ResourceCdnCacheCompany> findTableDataByParamAll(@Param("queryParam") ResourceCdnCacheCompany domainNameWebsiteDetail);

    Long countFindTableDataByParamAll(@Param("queryParam") ResourceCdnCacheCompany domainNameWebsiteDetail);

    List<ResourceCdnCacheCompany> findParseReport(@Param("queryParam") ResourceCdnCacheCompany cdnCacheCompany);

    List<ResourceCdnCacheCompany> findRateReport(@Param("queryParam") ResourceCdnCacheCompany cdnCacheCompany);

    List<ResourceCdnCacheCompany> cdnAnalysisByParamAll(@Param("queryParam") ResourceCdnCacheCompany parmer,@Param("queryList") List<String> queryList);

    List<ResourceCdnCacheCompany> cdnAnalysisByParamEvery(@Param("queryParam") ResourceCdnCacheCompany parmer);

    List<ResourceCdnCacheCompanyReport> findtopNServiceReport(@Param("queryParam") ResourceCdnCacheCompany parmer);

    List<ResourceCdnCacheCompanyDetail> topNServiceDetail(@Param("queryParam") ResourceCdnCacheCompany parmer);

    Long countTopNServiceDetail(@Param("queryParam") ResourceCdnCacheCompany parmer);


}
